package hatien.querydsl.core.query;

import hatien.querydsl.core.predicate.Predicate;
import hatien.querydsl.core.expression.Expression;
import java.util.List;

/**
 * Represents a query that can be built and executed to retrieve results.
 * Provides a fluent API for constructing SQL-like queries.
 *
 * @param <T> the type of results returned by this query
 */
public interface Query<T> {
	/**
	 * Adds SELECT clause expressions to this query.
	 *
	 * @param expressions the expressions to select
	 * @return this query for method chaining
	 */
	Query<T> select(Expression<?>... expressions);

	/**
	 * Adds FROM clause sources to this query.
	 *
	 * @param sources the source expressions (typically entity paths)
	 * @return this query for method chaining
	 */
	Query<T> from(Expression<?>... sources);

	/**
	 * Adds WHERE clause predicates to this query.
	 *
	 * @param predicates the predicates to add as conditions
	 * @return this query for method chaining
	 */
	Query<T> where(Predicate... predicates);

	/**
	 * Adds ORDER BY clause expressions to this query.
	 *
	 * @param expressions the expressions to order by
	 * @return this query for method chaining
	 */
	Query<T> orderBy(Expression<?>... expressions);

	/**
	 * Adds GROUP BY clause expressions to this query.
	 *
	 * @param expressions the expressions to group by
	 * @return this query for method chaining
	 */
	Query<T> groupBy(Expression<?>... expressions);

	/**
	 * Adds HAVING clause predicates to this query.
	 *
	 * @param predicates the predicates for the having clause
	 * @return this query for method chaining
	 */
	Query<T> having(Predicate... predicates);

	/**
	 * Sets the maximum number of results to return.
	 *
	 * @param limit the maximum number of results
	 * @return this query for method chaining
	 */
	Query<T> limit(long limit);

	/**
	 * Sets the number of results to skip.
	 *
	 * @param offset the number of results to skip
	 * @return this query for method chaining
	 */
	Query<T> offset(long offset);

	/**
	 * Executes this query and returns all matching results.
	 *
	 * @return a list of all matching results
	 */
	List<T> fetch();

	/**
	 * Executes this query expecting exactly one result.
	 *
	 * @return the single result
	 * @throws RuntimeException if no result or more than one result is found
	 */
	T fetchOne();

	/**
	 * Executes this query and returns the first result, if any.
	 *
	 * @return the first result, or null if no results found
	 */
	T fetchFirst();

	/**
	 * Executes a count query to determine the number of matching results.
	 *
	 * @return the number of matching results
	 */
	long fetchCount();

	/**
	 * Converts this query to its SQL string representation.
	 *
	 * @return the SQL string for this query
	 */
	String toSQL();
}