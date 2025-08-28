package hatien.querydsl.core.predicate;

import hatien.querydsl.core.expression.Expression;

public interface Predicate extends Expression<Boolean> {
	/**
	 * Creates the logical negation of this predicate.
	 *
	 * @return a new Predicate representing the NOT operation
	 */
	Predicate not();

	/**
	 * Creates a logical AND combination of this predicate with another predicate.
	 *
	 * @param other the predicate to combine with this one using AND
	 * @return a BooleanExpression representing the AND operation
	 */
	BooleanExpression and(Predicate other);

	/**
	 * Creates a logical OR combination of this predicate with another predicate.
	 *
	 * @param other the predicate to combine with this one using OR
	 * @return a BooleanExpression representing the OR operation
	 */
	BooleanExpression or(Predicate other);
}