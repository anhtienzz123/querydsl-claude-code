package hatien.querydsl.core.expression;

import hatien.querydsl.core.predicate.BooleanExpression;
import hatien.querydsl.core.predicate.Predicates;

public abstract class SimpleExpression<T> implements Expression<T> {
	protected final Class<? extends T> type;

	/**
	 * Constructs a new SimpleExpression with the specified type.
	 *
	 * @param type the Class object representing the type of this expression
	 */
	public SimpleExpression(Class<? extends T> type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends T> getType() {
		return type;
	}

	/**
	 * Creates an equality predicate comparing this expression to the specified
	 * value.
	 *
	 * @param value the value to compare against
	 * @return a BooleanExpression representing the equality condition
	 */
	public BooleanExpression eq(T value) {
		return Predicates.eq(this, value);
	}

	/**
	 * Creates a not-equal predicate comparing this expression to the specified
	 * value.
	 *
	 * @param value the value to compare against
	 * @return a BooleanExpression representing the not-equal condition
	 */
	public BooleanExpression ne(T value) {
		return Predicates.ne(this, value);
	}

	/**
	 * Creates a predicate that tests if this expression is null.
	 *
	 * @return a BooleanExpression representing the null check condition
	 */
	public BooleanExpression isNull() {
		return Predicates.isNull(this);
	}

	/**
	 * Creates a predicate that tests if this expression is not null.
	 *
	 * @return a BooleanExpression representing the not-null check condition
	 */
	public BooleanExpression isNotNull() {
		return Predicates.isNotNull(this);
	}

	/**
	 * Creates a predicate that tests if this expression's value is in the specified
	 * set of values.
	 *
	 * @param values the values to test against
	 * @return a BooleanExpression representing the IN condition
	 */
	public BooleanExpression in(T... values) {
		return Predicates.in(this, values);
	}
}