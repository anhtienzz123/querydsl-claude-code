package hatien.querydsl.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple connection pool manager for database connections.
 * Provides basic connection pooling functionality without external dependencies.
 */
public class ConnectionManager {
    private final DatabaseConfig config;
    private final ConcurrentLinkedQueue<Connection> availableConnections;
    private final AtomicInteger activeConnections;
    private volatile boolean initialized = false;
    
    /**
     * Creates a new ConnectionManager with the specified configuration.
     *
     * @param config the database configuration
     */
    public ConnectionManager(DatabaseConfig config) {
        this.config = config;
        this.availableConnections = new ConcurrentLinkedQueue<>();
        this.activeConnections = new AtomicInteger(0);
    }
    
    /**
     * Initializes the connection manager and loads the JDBC driver.
     *
     * @throws SQLException if initialization fails
     */
    public synchronized void initialize() throws SQLException {
        if (initialized) {
            return;
        }
        
        try {
            Class.forName(config.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found: " + config.getDriverClassName(), e);
        }
        
        // Pre-populate with initial connections
        for (int i = 0; i < Math.min(2, config.getMaxPoolSize()); i++) {
            Connection conn = createNewConnection();
            availableConnections.offer(conn);
        }
        
        initialized = true;
    }
    
    /**
     * Gets a connection from the pool or creates a new one if needed.
     *
     * @return a database connection
     * @throws SQLException if unable to obtain a connection
     */
    public Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        
        Connection connection = availableConnections.poll();
        
        if (connection == null || connection.isClosed()) {
            if (activeConnections.get() < config.getMaxPoolSize()) {
                connection = createNewConnection();
            } else {
                throw new SQLException("Connection pool exhausted. Maximum connections: " + config.getMaxPoolSize());
            }
        }
        
        activeConnections.incrementAndGet();
        return connection;
    }
    
    /**
     * Returns a connection to the pool.
     *
     * @param connection the connection to return
     */
    public void returnConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed() && connection.isValid(1)) {
                    availableConnections.offer(connection);
                }
            } catch (SQLException e) {
                // Connection is invalid, don't return to pool
            }
            activeConnections.decrementAndGet();
        }
    }
    
    /**
     * Closes a connection and removes it from the pool.
     *
     * @param connection the connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Ignore close errors
            }
            activeConnections.decrementAndGet();
        }
    }
    
    /**
     * Closes all connections and shuts down the connection manager.
     */
    public synchronized void shutdown() {
        Connection connection;
        while ((connection = availableConnections.poll()) != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Ignore close errors
            }
        }
        initialized = false;
    }
    
    /**
     * Creates a new database connection.
     *
     * @return a new database connection
     * @throws SQLException if connection creation fails
     */
    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(
            config.getUrl(),
            config.getUsername(),
            config.getPassword()
        );
    }
    
    /**
     * Gets the current number of active connections.
     *
     * @return the number of active connections
     */
    public int getActiveConnections() {
        return activeConnections.get();
    }
    
    /**
     * Gets the current number of available connections in the pool.
     *
     * @return the number of available connections
     */
    public int getAvailableConnections() {
        return availableConnections.size();
    }
}