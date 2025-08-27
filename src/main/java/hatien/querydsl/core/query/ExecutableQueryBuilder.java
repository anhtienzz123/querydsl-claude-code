package hatien.querydsl.core.query;

import hatien.querydsl.core.database.QueryExecutor;
import hatien.querydsl.core.predicate.Predicate;
import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.visitor.SQLVisitor;
import hatien.querydsl.core.visitor.ParameterizedSQLVisitor;
import hatien.querydsl.core.visitor.ParameterExtractor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An executable implementation of the Query interface that can execute SQL queries using JDBC.
 * Extends QueryBuilder functionality with actual database execution capabilities.
 *
 * @param <T> the type of results returned by this query
 */
public class ExecutableQueryBuilder<T> implements Query<T> {
    private final List<Expression<?>> selectList = new ArrayList<>();
    private final List<Expression<?>> fromList = new ArrayList<>();
    private final List<Predicate> whereList = new ArrayList<>();
    private final List<Expression<?>> orderByList = new ArrayList<>();
    private final List<Expression<?>> groupByList = new ArrayList<>();
    private final List<Predicate> havingList = new ArrayList<>();
    private Long limitValue;
    private Long offsetValue;
    
    private final SQLVisitor sqlVisitor = new SQLVisitor();
    private final ParameterizedSQLVisitor parameterizedSqlVisitor = new ParameterizedSQLVisitor();
    private final ParameterExtractor parameterExtractor = new ParameterExtractor();
    private final QueryExecutor queryExecutor;
    private final Class<T> resultType;
    
    /**
     * Creates a new ExecutableQueryBuilder with the specified executor and result type.
     *
     * @param queryExecutor the query executor to use for database operations
     * @param resultType the class representing the result type
     */
    public ExecutableQueryBuilder(QueryExecutor queryExecutor, Class<T> resultType) {
        this.queryExecutor = queryExecutor;
        this.resultType = resultType;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> select(Expression<?>... expressions) {
        selectList.addAll(Arrays.asList(expressions));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> from(Expression<?>... sources) {
        fromList.addAll(Arrays.asList(sources));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> where(Predicate... predicates) {
        whereList.addAll(Arrays.asList(predicates));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> orderBy(Expression<?>... expressions) {
        orderByList.addAll(Arrays.asList(expressions));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> groupBy(Expression<?>... expressions) {
        groupByList.addAll(Arrays.asList(expressions));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> having(Predicate... predicates) {
        havingList.addAll(Arrays.asList(predicates));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> limit(long limit) {
        this.limitValue = limit;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Query<T> offset(long offset) {
        this.offsetValue = offset;
        return this;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Executes the query and returns all matching results.
     */
    @Override
    public List<T> fetch() {
        try {
            String sql = toParameterizedSQL();
            List<Object> parameters = extractParameters();
            return queryExecutor.executeQuery(sql, parameters, resultType);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query", e);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * Executes the query and returns exactly one result.
     */
    @Override
    public T fetchOne() {
        try {
            String sql = toParameterizedSQL();
            List<Object> parameters = extractParameters();
            return queryExecutor.executeQueryOne(sql, parameters, resultType);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query", e);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * Executes the query and returns the first result, if any.
     */
    @Override
    public T fetchFirst() {
        try {
            String sql = toParameterizedSQL();
            List<Object> parameters = extractParameters();
            return queryExecutor.executeQueryFirst(sql, parameters, resultType);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query", e);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * Executes a count query to determine the number of matching results.
     */
    @Override
    public long fetchCount() {
        try {
            String countSql = toParameterizedCountSQL();
            List<Object> parameters = extractParameters();
            return queryExecutor.executeCount(countSql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute count query", e);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * Constructs a SQL string by combining all the query clauses in the proper order.
     */
    @Override
    public String toSQL() {
        StringBuilder sql = new StringBuilder();
        
        // SELECT clause
        if (!selectList.isEmpty()) {
            sql.append("SELECT ");
            sql.append(selectList.stream()
                    .map(expr -> expr.accept(sqlVisitor))
                    .collect(Collectors.joining(", ")));
        } else {
            sql.append("SELECT *");
        }
        
        // FROM clause
        if (!fromList.isEmpty()) {
            sql.append(" FROM ");
            sql.append(fromList.stream()
                    .map(expr -> expr.accept(sqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // WHERE clause
        if (!whereList.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereList.stream()
                    .map(predicate -> predicate.accept(sqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        // GROUP BY clause
        if (!groupByList.isEmpty()) {
            sql.append(" GROUP BY ");
            sql.append(groupByList.stream()
                    .map(expr -> expr.accept(sqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // HAVING clause
        if (!havingList.isEmpty()) {
            sql.append(" HAVING ");
            sql.append(havingList.stream()
                    .map(predicate -> predicate.accept(sqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        // ORDER BY clause
        if (!orderByList.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(orderByList.stream()
                    .map(expr -> expr.accept(sqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // LIMIT clause
        if (limitValue != null) {
            sql.append(" LIMIT ").append(limitValue);
        }
        
        // OFFSET clause
        if (offsetValue != null) {
            sql.append(" OFFSET ").append(offsetValue);
        }
        
        return sql.toString();
    }
    
    /**
     * Constructs a parameterized SQL string with ? placeholders for parameters.
     *
     * @return the parameterized SQL string
     */
    private String toParameterizedSQL() {
        StringBuilder sql = new StringBuilder();
        
        // SELECT clause
        if (!selectList.isEmpty()) {
            sql.append("SELECT ");
            sql.append(selectList.stream()
                    .map(expr -> expr.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(", ")));
        } else {
            sql.append("SELECT *");
        }
        
        // FROM clause
        if (!fromList.isEmpty()) {
            sql.append(" FROM ");
            sql.append(fromList.stream()
                    .map(expr -> expr.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // WHERE clause
        if (!whereList.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereList.stream()
                    .map(predicate -> predicate.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        // GROUP BY clause
        if (!groupByList.isEmpty()) {
            sql.append(" GROUP BY ");
            sql.append(groupByList.stream()
                    .map(expr -> expr.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // HAVING clause
        if (!havingList.isEmpty()) {
            sql.append(" HAVING ");
            sql.append(havingList.stream()
                    .map(predicate -> predicate.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        // ORDER BY clause
        if (!orderByList.isEmpty()) {
            sql.append(" ORDER BY ");
            sql.append(orderByList.stream()
                    .map(expr -> expr.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // LIMIT clause
        if (limitValue != null) {
            sql.append(" LIMIT ").append(limitValue);
        }
        
        // OFFSET clause
        if (offsetValue != null) {
            sql.append(" OFFSET ").append(offsetValue);
        }
        
        return sql.toString();
    }
    
    /**
     * Constructs a COUNT SQL query.
     *
     * @return the COUNT SQL string
     */
    private String toCountSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*)");
        
        // FROM clause
        if (!fromList.isEmpty()) {
            sql.append(" FROM ");
            sql.append(fromList.stream()
                    .map(expr -> expr.accept(sqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // WHERE clause
        if (!whereList.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereList.stream()
                    .map(predicate -> predicate.accept(sqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        // GROUP BY clause (affects count)
        if (!groupByList.isEmpty()) {
            sql.append(" GROUP BY ");
            sql.append(groupByList.stream()
                    .map(expr -> expr.accept(sqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // HAVING clause (affects count)
        if (!havingList.isEmpty()) {
            sql.append(" HAVING ");
            sql.append(havingList.stream()
                    .map(predicate -> predicate.accept(sqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }
    
    /**
     * Constructs a parameterized COUNT SQL query with ? placeholders.
     *
     * @return the parameterized COUNT SQL string
     */
    private String toParameterizedCountSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*)");
        
        // FROM clause
        if (!fromList.isEmpty()) {
            sql.append(" FROM ");
            sql.append(fromList.stream()
                    .map(expr -> expr.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // WHERE clause
        if (!whereList.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereList.stream()
                    .map(predicate -> predicate.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        // GROUP BY clause (affects count)
        if (!groupByList.isEmpty()) {
            sql.append(" GROUP BY ");
            sql.append(groupByList.stream()
                    .map(expr -> expr.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(", ")));
        }
        
        // HAVING clause (affects count)
        if (!havingList.isEmpty()) {
            sql.append(" HAVING ");
            sql.append(havingList.stream()
                    .map(predicate -> predicate.accept(parameterizedSqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }
    
    /**
     * Extracts parameters from the query for prepared statement execution.
     *
     * @return list of parameter values
     */
    private List<Object> extractParameters() {
        List<Object> parameters = new ArrayList<>();
        
        // Extract parameters from WHERE clauses
        for (Predicate predicate : whereList) {
            parameters.addAll(predicate.accept(parameterExtractor));
        }
        
        // Extract parameters from HAVING clauses
        for (Predicate predicate : havingList) {
            parameters.addAll(predicate.accept(parameterExtractor));
        }
        
        return parameters;
    }
}