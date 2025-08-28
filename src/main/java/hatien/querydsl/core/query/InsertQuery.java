package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.path.Path;

/**
 * Represents an INSERT query that can be built and executed. Provides a fluent
 * API for constructing SQL INSERT statements.
 *
 * @param <T> the type of the entity being inserted
 */
public interface InsertQuery<T> {
	/**
	 * Specifies the table/entity to insert into.
	 *
	 * @param entity the target entity path
	 * @return this query for method chaining
	 */
	InsertQuery<T> into(Expression<T> entity);

	/**
	 * Specifies the columns to insert values for.
	 *
	 * @param columns the column paths
	 * @return this query for method chaining
	 */
	InsertQuery<T> columns(Path<?>... columns);

	/**
	 * Specifies the values to insert, matching the order of columns.
	 *
	 * @param values the values to insert
	 * @return this query for method chaining
	 */
	InsertQuery<T> values(Object... values);

	/**
	 * Sets a specific column to a value.
	 *
	 * @param <V>    the type of the column value
	 * @param column the column path
	 * @param value  the value to set
	 * @return this query for method chaining
	 */
	<V> InsertQuery<T> set(Path<V> column, V value);

	/**
	 * Executes this insert query.
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