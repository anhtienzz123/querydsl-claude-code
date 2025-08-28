package hatien.querydsl.core.query;

import hatien.querydsl.core.database.QueryExecutor;
import hatien.querydsl.core.database.ConnectionManager;
import hatien.querydsl.core.database.DatabaseConfig;
import hatien.querydsl.core.database.Transaction;
import hatien.querydsl.core.database.TransactionalQueryExecutorWrapper;
import hatien.querydsl.core.expression.Expression;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory class for creating executable Query instances with JDBC database
 * integration. Provides convenient methods to start building queries that can
 * execute against an actual database.
 */
public class ExecutableQueryFactory {
	private final QueryExecutor queryExecutor;
	private final ConnectionManager connectionManager;

	/**
	 * Creates a new ExecutableQueryFactory with the specified database
	 * configuration.
	 *
	 * @param config the database configuration
	 * @throws SQLException if database initialization fails
	 */
	public ExecutableQueryFactory(DatabaseConfig config) throws SQLException {
		this.connectionManager = new ConnectionManager(config);
		this.connectionManager.initialize();
		this.queryExecutor = new QueryExecutor(connectionManager);
	}

	/**
	 * Creates a new ExecutableQueryFactory with an existing QueryExecutor.
	 *
	 * @param queryExecutor the query executor to use
	 */
	public ExecutableQueryFactory(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
		this.connectionManager = queryExecutor.getConnectionManager();
	}

	/**
	 * Creates a new ExecutableQueryFactory with H2 in-memory database for testing.
	 *
	 * @param databaseName the name of the in-memory database
	 * @return a new ExecutableQueryFactory instance
	 * @throws SQLException if database initialization fails
	 */
	public static ExecutableQueryFactory createH2InMemory(String databaseName) throws SQLException {
		return new ExecutableQueryFactory(DatabaseConfig.h2InMemory(databaseName));
	}

	/**
	 * Creates a new ExecutableQueryFactory with H2 file-based database.
	 *
	 * @param filePath the path to the database file
	 * @return a new ExecutableQueryFactory instance
	 * @throws SQLException if database initialization fails
	 */
	public static ExecutableQueryFactory createH2File(String filePath) throws SQLException {
		return new ExecutableQueryFactory(DatabaseConfig.h2File(filePath));
	}

	/**
	 * Creates a new executable query starting with a SELECT clause for a single
	 * expression.
	 *
	 * @param <T>        the type of the expression
	 * @param expression the expression to select
	 * @return a new executable Query instance with the SELECT clause configured
	 */
	public <T> Query<T> select(Expression<T> expression) {
		@SuppressWarnings("unchecked")
		Class<T> resultType = (Class<T>) determineResultType(expression);
		return new ExecutableQueryBuilder<T>(queryExecutor, resultType).select(expression);
	}

	/**
	 * Creates a new executable query starting with a SELECT clause for multiple
	 * expressions. This method returns a Query<Object[]> since multiple columns are
	 * selected.
	 *
	 * @param expressions the expressions to select
	 * @return a new executable Query instance with the SELECT clause configured for
	 *         multiple columns
	 */
	public Query<Object[]> select(Expression<?>... expressions) {
		return new ExecutableQueryBuilder<Object[]>(queryExecutor, Object[].class).select(expressions);
	}

	/**
	 * Creates a new executable query starting with both SELECT and FROM clauses
	 * using the same expression. This is a convenience method for queries that
	 * select from an entity path.
	 *
	 * @param <T>    the type of the source expression
	 * @param source the source expression to select from and use in FROM clause
	 * @return a new executable Query instance with both SELECT and FROM clauses
	 *         configured
	 */
	public <T> Query<T> selectFrom(Expression<T> source) {
		@SuppressWarnings("unchecked")
		Class<T> resultType = (Class<T>) determineResultType(source);
		return new ExecutableQueryBuilder<T>(queryExecutor, resultType).select(source).from(source);
	}

	/**
	 * Creates a new executable query starting with a FROM clause.
	 *
	 * @param <T>    the type of the source expression
	 * @param source the source expression for the FROM clause
	 * @return a new executable Query instance with the FROM clause configured
	 */
	public <T> Query<T> from(Expression<T> source) {
		@SuppressWarnings("unchecked")
		Class<T> resultType = (Class<T>) determineResultType(source);
		return new ExecutableQueryBuilder<T>(queryExecutor, resultType).from(source);
	}

	/**
	 * Creates a new executable INSERT query.
	 *
	 * @param <T> the type of the entity to insert
	 * @return a new executable InsertQuery instance
	 */
	public <T> InsertQuery<T> insert() {
		return new ExecutableInsertQueryBuilder<T>(queryExecutor);
	}

	/**
	 * Creates a new executable INSERT query with the target entity specified.
	 *
	 * @param <T>    the type of the entity to insert
	 * @param entity the target entity path
	 * @return a new executable InsertQuery instance with the target entity
	 *         configured
	 */
	public <T> InsertQuery<T> insertInto(Expression<T> entity) {
		return new ExecutableInsertQueryBuilder<T>(queryExecutor).into(entity);
	}

	/**
	 * Creates a new executable UPDATE query.
	 *
	 * @param <T> the type of the entity to update
	 * @return a new executable UpdateQuery instance
	 */
	public <T> UpdateQuery<T> update() {
		return new ExecutableUpdateQueryBuilder<T>(queryExecutor);
	}

	/**
	 * Creates a new executable UPDATE query with the target entity specified.
	 *
	 * @param <T>    the type of the entity to update
	 * @param entity the target entity path
	 * @return a new executable UpdateQuery instance with the target entity
	 *         configured
	 */
	public <T> UpdateQuery<T> update(Expression<T> entity) {
		return new ExecutableUpdateQueryBuilder<T>(queryExecutor).table(entity);
	}

	/**
	 * Creates a new executable DELETE query.
	 *
	 * @param <T> the type of the entity to delete
	 * @return a new executable DeleteQuery instance
	 */
	public <T> DeleteQuery<T> delete() {
		return new ExecutableDeleteQueryBuilder<T>(queryExecutor);
	}

	/**
	 * Creates a new executable DELETE query with the target entity specified.
	 *
	 * @param <T>    the type of the entity to delete
	 * @param entity the target entity path
	 * @return a new executable DeleteQuery instance with the target entity
	 *         configured
	 */
	public <T> DeleteQuery<T> deleteFrom(Expression<T> entity) {
		return new ExecutableDeleteQueryBuilder<T>(queryExecutor).from(entity);
	}

	/**
	 * Gets the query executor used by this factory.
	 *
	 * @return the query executor
	 */
	public QueryExecutor getQueryExecutor() {
		return queryExecutor;
	}

	/**
	 * Gets the connection manager used by this factory.
	 *
	 * @return the connection manager
	 */
	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	/**
	 * Begins a new database transaction.
	 *
	 * @return a new Transaction that can be used with try-with-resources
	 * @throws SQLException if transaction creation fails
	 */
	public Transaction beginTransaction() throws SQLException {
		Connection connection = connectionManager.getConnection();
		return new Transaction(connection, connectionManager);
	}

	/**
	 * Executes a block of code within a transaction. The transaction is
	 * automatically committed if the block completes successfully, or rolled back
	 * if an exception occurs.
	 *
	 * @param block the code block to execute in the transaction
	 * @throws SQLException if transaction or execution fails
	 */
	public void inTransaction(TransactionBlock block) throws SQLException {
		try (Transaction transaction = beginTransaction()) {
			hatien.querydsl.core.database.TransactionalQueryExecutor txExecutor = new hatien.querydsl.core.database.TransactionalQueryExecutor(
					transaction);
			// Create a wrapper QueryExecutor that delegates to the transactional executor
			QueryExecutor wrappedExecutor = new TransactionalQueryExecutorWrapper(txExecutor);
			ExecutableQueryFactory txFactory = new ExecutableQueryFactory(wrappedExecutor);

			block.execute(txFactory);
			transaction.commit();
		}
	}

	/**
	 * Functional interface for transaction blocks.
	 */
	@FunctionalInterface
	public interface TransactionBlock {
		void execute(ExecutableQueryFactory queryFactory) throws SQLException;
	}

	/**
	 * Closes the query factory and releases all database connections.
	 */
	public void close() {
		connectionManager.shutdown();
	}

	/**
	 * Determines the result type for an expression. This is a simplified
	 * implementation that works for basic cases.
	 *
	 * @param expression the expression
	 * @return the result type class
	 */
	private Class<?> determineResultType(Expression<?> expression) {
		// For EntityPath expressions, try to extract the entity type
		if (expression instanceof hatien.querydsl.core.path.EntityPath) {
			return ((hatien.querydsl.core.path.EntityPath<?>) expression).getType();
		}
		// For other path types, return Object as a fallback
		return Object.class;
	}
}