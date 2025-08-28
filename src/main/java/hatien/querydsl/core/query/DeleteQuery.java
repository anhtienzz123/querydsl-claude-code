package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.predicate.Predicate;

/**
 * Represents a DELETE query that can be built and executed. Provides a fluent
 * API for constructing SQL DELETE statements.
 *
 * @param <T> the type of the entity being deleted
 */
public interface DeleteQuery<T> {
	/**
	 * Specifies the table/entity to delete from.
	 *
	 * @param entity the target entity path
	 * @return this query for method chaining
	 */
	DeleteQuery<T> from(Expression<T> entity);

	/**
	 * Adds WHERE clause predicates to this query.
	 *
	 * @param predicates the predicates to add as conditions
	 * @return this query for method chaining
	 */
	DeleteQuery<T> where(Predicate... predicates);

	/**
	 * Executes this delete query.
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