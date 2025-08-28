package hatien.querydsl.core.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a database transaction with automatic resource management.
 * Provides transaction boundaries and rollback capabilities.
 */
public class Transaction implements AutoCloseable {
	private final Connection connection;
	private final ConnectionManager connectionManager;
	private boolean committed = false;
	private boolean rolledBack = false;

	/**
	 * Creates a new transaction with the specified connection.
	 *
	 * @param connection        the database connection
	 * @param connectionManager the connection manager for cleanup
	 * @throws SQLException if transaction initialization fails
	 */
	public Transaction(Connection connection, ConnectionManager connectionManager) throws SQLException {
		this.connection = connection;
		this.connectionManager = connectionManager;

		// Begin transaction
		connection.setAutoCommit(false);
	}

	/**
	 * Commits the transaction.
	 *
	 * @throws SQLException          if commit fails
	 * @throws IllegalStateException if transaction is already committed or rolled
	 *                               back
	 */
	public void commit() throws SQLException {
		if (committed) {
			throw new IllegalStateException("Transaction is already committed");
		}
		if (rolledBack) {
			throw new IllegalStateException("Transaction is already rolled back");
		}

		try {
			connection.commit();
			committed = true;
		} catch (SQLException e) {
			rollback();
			throw e;
		}
	}

	/**
	 * Rolls back the transaction.
	 *
	 * @throws SQLException if rollback fails
	 */
	public void rollback() throws SQLException {
		if (!committed && !rolledBack) {
			try {
				connection.rollback();
			} finally {
				rolledBack = true;
			}
		}
	}

	/**
	 * Gets the connection associated with this transaction.
	 *
	 * @return the database connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Checks if the transaction has been committed.
	 *
	 * @return true if committed, false otherwise
	 */
	public boolean isCommitted() {
		return committed;
	}

	/**
	 * Checks if the transaction has been rolled back.
	 *
	 * @return true if rolled back, false otherwise
	 */
	public boolean isRolledBack() {
		return rolledBack;
	}

	/**
	 * Closes the transaction, rolling back if not committed. This method is called
	 * automatically when used in try-with-resources.
	 */
	@Override
	public void close() {
		try {
			if (!committed && !rolledBack) {
				rollback();
			}
		} catch (SQLException e) {
			// Log the error but don't throw it in close()
			System.err.println("Error rolling back transaction: " + e.getMessage());
		} finally {
			try {
				connection.setAutoCommit(true);
				connectionManager.returnConnection(connection);
			} catch (SQLException e) {
				// Log the error but don't throw it in close()
				System.err.println("Error restoring auto-commit: " + e.getMessage());
				connectionManager.closeConnection(connection);
			}
		}
	}
}