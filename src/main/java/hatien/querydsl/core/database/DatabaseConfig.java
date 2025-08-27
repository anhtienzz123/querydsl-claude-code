package hatien.querydsl.core.database;

/**
 * Configuration class for database connection settings.
 * Holds JDBC connection parameters and database-specific settings.
 */
public class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;
    private final int maxPoolSize;
    private final int connectionTimeoutMs;
    
    /**
     * Creates a new DatabaseConfig with the specified connection parameters.
     *
     * @param url the JDBC URL for the database
     * @param username the database username
     * @param password the database password
     * @param driverClassName the JDBC driver class name
     */
    public DatabaseConfig(String url, String username, String password, String driverClassName) {
        this(url, username, password, driverClassName, 10, 30000);
    }
    
    /**
     * Creates a new DatabaseConfig with all connection parameters.
     *
     * @param url the JDBC URL for the database
     * @param username the database username
     * @param password the database password
     * @param driverClassName the JDBC driver class name
     * @param maxPoolSize maximum number of connections in pool
     * @param connectionTimeoutMs connection timeout in milliseconds
     */
    public DatabaseConfig(String url, String username, String password, String driverClassName, 
                         int maxPoolSize, int connectionTimeoutMs) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeoutMs = connectionTimeoutMs;
    }
    
    /**
     * Creates a configuration for H2 in-memory database.
     *
     * @param databaseName the name of the in-memory database
     * @return DatabaseConfig for H2 in-memory database
     */
    public static DatabaseConfig h2InMemory(String databaseName) {
        return new DatabaseConfig(
            "jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "sa",
            "",
            "org.h2.Driver"
        );
    }
    
    /**
     * Creates a configuration for H2 file-based database.
     *
     * @param filePath the path to the database file
     * @return DatabaseConfig for H2 file database
     */
    public static DatabaseConfig h2File(String filePath) {
        return new DatabaseConfig(
            "jdbc:h2:file:" + filePath,
            "sa",
            "",
            "org.h2.Driver"
        );
    }
    
    /**
     * Creates a configuration for MySQL database.
     *
     * @param host the database host
     * @param port the database port
     * @param database the database name
     * @param username the database username
     * @param password the database password
     * @return DatabaseConfig for MySQL database
     */
    public static DatabaseConfig mysql(String host, int port, String database, String username, String password) {
        return new DatabaseConfig(
            "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true",
            username,
            password,
            "com.mysql.cj.jdbc.Driver"
        );
    }
    
    /**
     * Creates a configuration for PostgreSQL database.
     *
     * @param host the database host
     * @param port the database port
     * @param database the database name
     * @param username the database username
     * @param password the database password
     * @return DatabaseConfig for PostgreSQL database
     */
    public static DatabaseConfig postgresql(String host, int port, String database, String username, String password) {
        return new DatabaseConfig(
            "jdbc:postgresql://" + host + ":" + port + "/" + database,
            username,
            password,
            "org.postgresql.Driver"
        );
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getDriverClassName() {
        return driverClassName;
    }
    
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
}