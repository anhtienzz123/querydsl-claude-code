package hatien.querydsl.core.database;

import java.sql.SQLException;
import java.util.List;

/**
 * Wrapper that adapts TransactionalQueryExecutor to the QueryExecutor interface.
 * This allows transactional executors to be used wherever QueryExecutor is expected.
 */
public class TransactionalQueryExecutorWrapper extends QueryExecutor {
    private final TransactionalQueryExecutor transactionalExecutor;
    
    /**
     * Creates a new wrapper for the specified transactional executor.
     *
     * @param transactionalExecutor the transactional executor to wrap
     */
    public TransactionalQueryExecutorWrapper(TransactionalQueryExecutor transactionalExecutor) {
        // Pass a dummy connection manager since we won't use the parent class methods
        super(new ConnectionManager(DatabaseConfig.h2InMemory("dummy")));
        this.transactionalExecutor = transactionalExecutor;
    }
    
    @Override
    public <T> List<T> executeQuery(String sql, List<Object> parameters, Class<T> resultType) throws SQLException {
        return transactionalExecutor.executeQuery(sql, parameters, resultType);
    }
    
    @Override
    public <T> T executeQueryFirst(String sql, List<Object> parameters, Class<T> resultType) throws SQLException {
        return transactionalExecutor.executeQueryFirst(sql, parameters, resultType);
    }
    
    @Override
    public <T> T executeQueryOne(String sql, List<Object> parameters, Class<T> resultType) throws SQLException {
        return transactionalExecutor.executeQueryOne(sql, parameters, resultType);
    }
    
    @Override
    public long executeCount(String sql, List<Object> parameters) throws SQLException {
        return transactionalExecutor.executeCount(sql, parameters);
    }
    
    @Override
    public long executeUpdate(String sql, List<Object> parameters) throws SQLException {
        return transactionalExecutor.executeUpdate(sql, parameters);
    }
    
    @Override
    public List<Long> executeInsertWithKeys(String sql, List<Object> parameters) throws SQLException {
        return transactionalExecutor.executeInsertWithKeys(sql, parameters);
    }
    
    @Override
    public int[] executeBatch(String sql, List<List<Object>> parameterSets) throws SQLException {
        return transactionalExecutor.executeBatch(sql, parameterSets);
    }
    
    @Override
    public ConnectionManager getConnectionManager() {
        return transactionalExecutor.getTransaction().getConnection() != null ? 
            super.getConnectionManager() : null;
    }
}