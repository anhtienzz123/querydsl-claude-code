package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;

/**
 * Factory class for creating Query instances with a fluent API.
 * Provides convenient methods to start building queries with various starting points.
 */
public class QueryFactory {
    
    /**
     * Creates a new QueryFactory instance.
     *
     * @return a new QueryFactory instance
     */
    public static QueryFactory create() {
        return new QueryFactory();
    }
    
    /**
     * Creates a new query starting with a SELECT clause for a single expression.
     *
     * @param <T> the type of the expression
     * @param expression the expression to select
     * @return a new Query instance with the SELECT clause configured
     */
    public <T> Query<T> select(Expression<T> expression) {
        return new QueryBuilder<T>().select(expression);
    }
    
    /**
     * Creates a new query starting with a SELECT clause for multiple expressions.
     * This method returns a Query<Object[]> since multiple columns are selected.
     *
     * @param expressions the expressions to select
     * @return a new Query instance with the SELECT clause configured for multiple columns
     */
    public Query<Object[]> select(Expression<?>... expressions) {
        return new QueryBuilder<Object[]>().select(expressions);
    }
    
    /**
     * Creates a new query starting with both SELECT and FROM clauses using the same expression.
     * This is a convenience method for queries that select from an entity path.
     *
     * @param <T> the type of the source expression
     * @param source the source expression to select from and use in FROM clause
     * @return a new Query instance with both SELECT and FROM clauses configured
     */
    public <T> Query<T> selectFrom(Expression<T> source) {
        return new QueryBuilder<T>().select(source).from(source);
    }
    
    /**
     * Creates a new query starting with a FROM clause.
     *
     * @param <T> the type of the source expression
     * @param source the source expression for the FROM clause
     * @return a new Query instance with the FROM clause configured
     */
    public <T> Query<T> from(Expression<T> source) {
        return new QueryBuilder<T>().from(source);
    }
}