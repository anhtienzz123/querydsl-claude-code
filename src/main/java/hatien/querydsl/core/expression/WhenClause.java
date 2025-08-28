package hatien.querydsl.core.expression;

import hatien.querydsl.core.predicate.BooleanExpression;

/**
 * Represents a single WHEN condition THEN value clause in a CASE expression.
 * This is used as part of the CASE WHEN construct in SQL.
 *
 * @param <T> the type of the value returned by this WHEN clause
 */
public class WhenClause<T> {
	private final BooleanExpression condition;
	private final Expression<T> value;

	/**
	 * Constructs a new WhenClause with the specified condition and value.
	 *
	 * @param condition the boolean condition to evaluate
	 * @param value     the expression to return when the condition is true
	 */
	public WhenClause(BooleanExpression condition, Expression<T> value) {
		this.condition = condition;
		this.value = value;
	}

	/**
	 * Constructs a new WhenClause with the specified condition and constant value.
	 *
	 * @param condition the boolean condition to evaluate
	 * @param value     the constant value to return when the condition is true
	 * @param valueType the type of the constant value
	 */
	public WhenClause(BooleanExpression condition, T value, Class<T> valueType) {
		this.condition = condition;
		this.value = new ConstantExpression<>(value, valueType);
	}

	/**
	 * Returns the condition for this WHEN clause.
	 *
	 * @return the boolean condition
	 */
	public BooleanExpression getCondition() {
		return condition;
	}

	/**
	 * Returns the value expression for this WHEN clause.
	 *
	 * @return the value expression
	 */
	public Expression<T> getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("WHEN %s THEN %s", condition, value);
	}

	/**
	 * Simple expression implementation for constant values in CASE expressions.
	 */
	private static class ConstantExpression<T> implements Expression<T> {
		private final T value;
		private final Class<T> type;

		public ConstantExpression(T value, Class<T> type) {
			this.value = value;
			this.type = type;
		}

		@Override
		public Class<? extends T> getType() {
			return type;
		}

		@Override
		public <R> R accept(hatien.querydsl.core.visitor.ExpressionVisitor<R> visitor) {
			// For constant values, we don't need visitor pattern - just return the string
			// representation
			return (R) String.valueOf(value);
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}

		public T getValue() {
			return value;
		}
	}
}