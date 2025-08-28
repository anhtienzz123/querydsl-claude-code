package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.expression.*;
import static hatien.querydsl.core.expression.ExpressionUtils.*;

/**
 * Factory class for creating Query instances with a fluent API. Provides
 * convenient methods to start building queries with various starting points.
 * Supports SELECT, INSERT, UPDATE, and DELETE operations.
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
	 * @param <T>        the type of the expression
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
	 * @return a new Query instance with the SELECT clause configured for multiple
	 *         columns
	 */
	public Query<Object[]> select(Expression<?>... expressions) {
		return new QueryBuilder<Object[]>().select(expressions);
	}

	/**
	 * Creates a new query starting with both SELECT and FROM clauses using the same
	 * expression. This is a convenience method for queries that select from an
	 * entity path.
	 *
	 * @param <T>    the type of the source expression
	 * @param source the source expression to select from and use in FROM clause
	 * @return a new Query instance with both SELECT and FROM clauses configured
	 */
	public <T> Query<T> selectFrom(Expression<T> source) {
		return new QueryBuilder<T>().select(source).from(source);
	}

	/**
	 * Creates a new query starting with a FROM clause.
	 *
	 * @param <T>    the type of the source expression
	 * @param source the source expression for the FROM clause
	 * @return a new Query instance with the FROM clause configured
	 */
	public <T> Query<T> from(Expression<T> source) {
		return new QueryBuilder<T>().from(source);
	}

	/**
	 * Creates a new INSERT query.
	 *
	 * @param <T> the type of the entity to insert
	 * @return a new InsertQuery instance
	 */
	public <T> InsertQuery<T> insert() {
		return new InsertQueryBuilder<T>();
	}

	/**
	 * Creates a new INSERT query with the target entity specified.
	 *
	 * @param <T>    the type of the entity to insert
	 * @param entity the target entity path
	 * @return a new InsertQuery instance with the target entity configured
	 */
	public <T> InsertQuery<T> insertInto(Expression<T> entity) {
		return new InsertQueryBuilder<T>().into(entity);
	}

	/**
	 * Creates a new UPDATE query.
	 *
	 * @param <T> the type of the entity to update
	 * @return a new UpdateQuery instance
	 */
	public <T> UpdateQuery<T> update() {
		return new UpdateQueryBuilder<T>();
	}

	/**
	 * Creates a new UPDATE query with the target entity specified.
	 *
	 * @param <T>    the type of the entity to update
	 * @param entity the target entity path
	 * @return a new UpdateQuery instance with the target entity configured
	 */
	public <T> UpdateQuery<T> update(Expression<T> entity) {
		return new UpdateQueryBuilder<T>().table(entity);
	}

	/**
	 * Creates a new DELETE query.
	 *
	 * @param <T> the type of the entity to delete
	 * @return a new DeleteQuery instance
	 */
	public <T> DeleteQuery<T> delete() {
		return new DeleteQueryBuilder<T>();
	}

	/**
	 * Creates a new DELETE query with the target entity specified.
	 *
	 * @param <T>    the type of the entity to delete
	 * @param entity the target entity path
	 * @return a new DeleteQuery instance with the target entity configured
	 */
	public <T> DeleteQuery<T> deleteFrom(Expression<T> entity) {
		return new DeleteQueryBuilder<T>().from(entity);
	}

	// =============================================================================
	// AGGREGATE FUNCTION CONVENIENCE METHODS
	// =============================================================================

	/**
	 * Creates a COUNT(*) expression that counts all rows. This is a convenience
	 * method delegating to ExpressionUtils.count().
	 *
	 * @return a CountExpression that counts all rows
	 */
	public CountExpression count() {
		return ExpressionUtils.count();
	}

	/**
	 * Creates a COUNT(expression) that counts non-null values of the given
	 * expression. This is a convenience method delegating to
	 * ExpressionUtils.count(expression).
	 *
	 * @param expression the expression to count non-null values of
	 * @return a CountExpression that counts the given expression
	 */
	public CountExpression count(Expression<?> expression) {
		return ExpressionUtils.count(expression);
	}

	/**
	 * Creates a SUM(expression) that calculates the sum of the given numeric
	 * expression. This is a convenience method delegating to
	 * ExpressionUtils.sum(expression).
	 *
	 * @param <T>        the numeric type
	 * @param expression the numeric expression to sum
	 * @return a SumExpression for the given expression
	 */
	public <T extends Number> SumExpression<T> sum(Expression<T> expression) {
		return ExpressionUtils.sum(expression);
	}

	/**
	 * Creates an AVG(expression) that calculates the average of the given numeric
	 * expression. This is a convenience method delegating to
	 * ExpressionUtils.avg(expression).
	 *
	 * @param <T>        the numeric type
	 * @param expression the numeric expression to average
	 * @return an AvgExpression for the given expression
	 */
	public <T extends Number> AvgExpression<T> avg(Expression<T> expression) {
		return ExpressionUtils.avg(expression);
	}

	/**
	 * Creates a MIN(expression) that finds the minimum value of the given
	 * comparable expression. This is a convenience method delegating to
	 * ExpressionUtils.min(expression).
	 *
	 * @param <T>        the comparable type
	 * @param expression the comparable expression to find the minimum of
	 * @return a MinExpression for the given expression
	 */
	public <T extends Comparable<T>> MinExpression<T> min(Expression<T> expression) {
		return ExpressionUtils.min(expression);
	}

	/**
	 * Creates a MAX(expression) that finds the maximum value of the given
	 * comparable expression. This is a convenience method delegating to
	 * ExpressionUtils.max(expression).
	 *
	 * @param <T>        the comparable type
	 * @param expression the comparable expression to find the maximum of
	 * @return a MaxExpression for the given expression
	 */
	public <T extends Comparable<T>> MaxExpression<T> max(Expression<T> expression) {
		return ExpressionUtils.max(expression);
	}

	/**
	 * Creates a new CaseBuilder for building CASE expressions with the specified
	 * return type. This is a convenience method delegating to
	 * ExpressionUtils.caseWhen(type).
	 *
	 * @param <T>  the return type
	 * @param type the class representing the return type
	 * @return a new CaseBuilder instance
	 */
	public <T> CaseExpression.CaseBuilder<T> caseWhen(Class<T> type) {
		return ExpressionUtils.caseWhen(type);
	}
}