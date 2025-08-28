package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.path.Path;
import hatien.querydsl.core.predicate.Predicate;

/**
 * Represents an UPDATE query that can be built and executed. Provides a fluent
 * API for constructing SQL UPDATE statements.
 *
 * @param <T> the type of the entity being updated
 */
public interface UpdateQuery<T> {
	/**
	 * Specifies the table/entity to update.
	 *
	 * @param entity the target entity path
	 * @return this query for method chaining
	 */
	UpdateQuery<T> table(Expression<T> entity);

	/**
	 * Sets a specific column to a value.
	 *
	 * @param <V>    the type of the column value
	 * @param column the column path
	 * @param value  the value to set
	 * @return this query for method chaining
	 */
	<V> UpdateQuery<T> set(Path<V> column, V value);

	/**
	 * Sets a specific column to the result of an expression.
	 *
	 * @param <V>        the type of the column value
	 * @param column     the column path
	 * @param expression the expression to evaluate
	 * @return this query for method chaining
	 */
	<V> UpdateQuery<T> set(Path<V> column, Expression<V> expression);

	/**
	 * Adds WHERE clause predicates to this query.
	 *
	 * @param predicates the predicates to add as conditions
	 * @return this query for method chaining
	 */
	UpdateQuery<T> where(Predicate... predicates);

	/**
	 * Executes this update query.
	 *
	 * @return the number of affected rows
	 */
	long execute();

	/**
	 * Converts this query to its SQL string representation.
	 *
	 * @return the SQL string for this query
	 */
	String toSQL();
}