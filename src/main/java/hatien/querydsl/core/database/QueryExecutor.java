package hatien.querydsl.core.database;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.path.EntityPath;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes SQL queries and manages database operations.
 * Handles prepared statements, result set processing, and resource management.
 */
public class QueryExecutor {
    private final ConnectionManager connectionManager;
    private final ResultSetMapper resultSetMapper;
    
    /**
     * Creates a new QueryExecutor with the specified connection manager.
     *
     * @param connectionManager the connection manager to use
     */
    public QueryExecutor(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.resultSetMapper = new ResultSetMapper();
    }
    
    /**
     * Executes a SELECT query and returns all matching results.
     *
     * @param <T> the type of results to return
     * @param sql the SQL query to execute
     * @param parameters the query parameters
     * @param resultType the class of the result type
     * @return a list of results
     * @throws SQLException if query execution fails
     */
    public <T> List<T> executeQuery(String sql, List<Object> parameters, Class<T> resultType) throws SQLException {
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, parameters);
            
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    T result = resultSetMapper.mapRow(rs, resultType);
                    results.add(result);
                }
                return results;
            }
        } finally {
            connectionManager.returnConnection(connection);
        }
    }
    
    /**
     * Executes a SELECT query and returns the first result.
     *
     * @param <T> the type of result to return
     * @param sql the SQL query to execute
     * @param parameters the query parameters
     * @param resultType the class of the result type
     * @return the first result, or null if no results found
     * @throws SQLException if query execution fails
     */
    public <T> T executeQueryFirst(String sql, List<Object> parameters, Class<T> resultType) throws SQLException {
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, parameters);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetMapper.mapRow(rs, resultType);
                }
                return null;
            }
        } finally {
            connectionManager.returnConnection(connection);
        }
    }
    
    /**
     * Executes a SELECT query and returns exactly one result.
     *
     * @param <T> the type of result to return
     * @param sql the SQL query to execute
     * @param parameters the query parameters
     * @param resultType the class of the result type
     * @return the single result
     * @throws SQLException if query execution fails
     * @throws IllegalStateException if no result or more than one result is found
     */
    public <T> T executeQueryOne(String sql, List<Object> parameters, Class<T> resultType) throws SQLException {
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, parameters);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalStateException("No result found for query: " + sql);
                }
                
                T result = resultSetMapper.mapRow(rs, resultType);
                
                if (rs.next()) {
                    throw new IllegalStateException("More than one result found for query: " + sql);
                }
                
                return result;
            }
        } finally {
            connectionManager.returnConnection(connection);
        }
    }
    
    /**
     * Executes a COUNT query and returns the count.
     *
     * @param sql the SQL query to execute
     * @param parameters the query parameters
     * @return the count result
     * @throws SQLException if query execution fails
     */
    public long executeCount(String sql, List<Object> parameters) throws SQLException {
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, parameters);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } finally {
            connectionManager.returnConnection(connection);
        }
    }
    
    /**
     * Executes an UPDATE, INSERT, or DELETE statement.
     *
     * @param sql the SQL statement to execute
     * @param parameters the statement parameters
     * @return the number of affected rows
     * @throws SQLException if statement execution fails
     */
    public long executeUpdate(String sql, List<Object> parameters) throws SQLException {
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, parameters);
            return stmt.executeUpdate();
        } finally {
            connectionManager.returnConnection(connection);
        }
    }
    
    /**
     * Executes an INSERT statement and returns generated keys.
     *
     * @param sql the SQL INSERT statement
     * @param parameters the statement parameters
     * @return the generated keys
     * @throws SQLException if statement execution fails
     */
    public List<Long> executeInsertWithKeys(String sql, List<Object> parameters) throws SQLException {
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt, parameters);
            stmt.executeUpdate();
            
            List<Long> keys = new ArrayList<>();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                while (rs.next()) {
                    keys.add(rs.getLong(1));
                }
            }
            return keys;
        } finally {
            connectionManager.returnConnection(connection);
        }
    }
    
    /**
     * Executes multiple statements in a single batch.
     *
     * @param sql the SQL statement to execute
     * @param parameterSets list of parameter sets for batch execution
     * @return array of update counts for each statement
     * @throws SQLException if batch execution fails
     */
    public int[] executeBatch(String sql, List<List<Object>> parameterSets) throws SQLException {
        Connection connection = connectionManager.getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (List<Object> parameters : parameterSets) {
                setParameters(stmt, parameters);
                stmt.addBatch();
            }
            return stmt.executeBatch();
        } finally {
            connectionManager.returnConnection(connection);
        }
    }
    
    /**
     * Sets parameters on a prepared statement.
     *
     * @param stmt the prepared statement
     * @param parameters the parameters to set
     * @throws SQLException if parameter setting fails
     */
    private void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param == null) {
                    stmt.setNull(i + 1, java.sql.Types.NULL);
                } else {
                    stmt.setObject(i + 1, param);
                }
            }
        }
    }
    
    /**
     * Gets the connection manager used by this executor.
     *
     * @return the connection manager
     */
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }
}