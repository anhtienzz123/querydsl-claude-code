package hatien.querydsl.core.query;

import hatien.querydsl.core.database.QueryExecutor;
import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.predicate.Predicate;
import hatien.querydsl.core.visitor.SQLVisitor;
import hatien.querydsl.core.visitor.ParameterizedSQLVisitor;
import hatien.querydsl.core.visitor.ParameterExtractor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Executable implementation of the DeleteQuery interface that can execute SQL DELETE statements using JDBC.
 * Extends DeleteQueryBuilder functionality with actual database execution capabilities.
 *
 * @param <T> the type of the entity being deleted
 */
public class ExecutableDeleteQueryBuilder<T> implements DeleteQuery<T> {
    private Expression<T> targetEntity;
    private final List<Predicate> whereList = new ArrayList<>();
    
    private final SQLVisitor sqlVisitor = new SQLVisitor();
    private final ParameterizedSQLVisitor parameterizedSqlVisitor = new ParameterizedSQLVisitor();
    private final ParameterExtractor parameterExtractor = new ParameterExtractor();
    private final QueryExecutor queryExecutor;
    
    /**
     * Creates a new ExecutableDeleteQueryBuilder with the specified query executor.
     *
     * @param queryExecutor the query executor to use for database operations
     */
    public ExecutableDeleteQueryBuilder(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteQuery<T> from(Expression<T> entity) {
        this.targetEntity = entity;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteQuery<T> where(Predicate... predicates) {
        whereList.addAll(Arrays.asList(predicates));
        return this;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Executes the DELETE statement and returns the number of affected rows.
     */
    @Override
    public long execute() {
        try {
            String sql = toParameterizedSQL();
            List<Object> parameters = extractParameters();
            return queryExecutor.executeUpdate(sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute DELETE statement", e);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * Constructs a DELETE SQL string with literal values (for display purposes).
     */
    @Override
    public String toSQL() {
        if (targetEntity == null) {
            throw new IllegalStateException("Target entity must be specified using from()");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(targetEntity.accept(sqlVisitor));
        
        // WHERE clause
        if (!whereList.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereList.stream()
                    .map(predicate -> predicate.accept(sqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }
    
    /**
     * Constructs a parameterized DELETE SQL string with ? placeholders.
     *
     * @return the parameterized DELETE SQL string
     */
    private String toParameterizedSQL() {
        if (targetEntity == null) {
            throw new IllegalStateException("Target entity must be specified using from()");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(targetEntity.accept(parameterizedSqlVisitor));
        
        // WHERE clause
        if (!whereList.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereList.stream()
                    .map(predicate -> predicate.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }
    
    /**
     * Extracts parameter values for prepared statement execution.
     *
     * @return list of parameter values in the correct order
     */
    private List<Object> extractParameters() {
        List<Object> parameters = new ArrayList<>();
        
        // Add WHERE clause parameters
        for (Predicate predicate : whereList) {
            parameters.addAll(predicate.accept(parameterExtractor));
        }
        
        return parameters;
    }
}