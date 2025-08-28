package hatien.querydsl.core.expression;

import hatien.querydsl.core.predicate.BooleanExpression;
import hatien.querydsl.core.visitor.ExpressionVisitor;
import java.util.ArrayList;
import java.util.List;

/**
 * Expression representing a SQL CASE WHEN construct. Supports multiple WHEN
 * clauses and an optional ELSE clause.
 * 
 * <p>
 * Example usage:
 * 
 * <pre>
 * CaseExpression&lt;String&gt; caseExpr = CaseExpression.&lt;String&gt;builder(String.class).when(user.age.lt(18), "Minor")
 * 		.when(user.age.between(18, 65), "Adult").otherwise("Senior");
 * </pre>
 *
 * @param <T> the return type of the CASE expression
 */
public class CaseExpression<T> implements Expression<T> {
	private final Class<? extends T> type;
	private final List<WhenClause<T>> whenClauses;
	private final Expression<T> elseExpression;

	/**
	 * Constructs a new CaseExpression.
	 *
	 * @param type           the return type of this CASE expression
	 * @param whenClauses    the list of WHEN clauses
	 * @param elseExpression the ELSE expression (can be null)
	 */
	private CaseExpression(Class<? extends T> type, List<WhenClause<T>> whenClauses, Expression<T> elseExpression) {
		this.type = type;
		this.whenClauses = new ArrayList<>(whenClauses);
		this.elseExpression = elseExpression;
	}

	/**
	 * Creates a new CaseBuilder for building CASE expressions.
	 *
	 * @param <T>  the return type
	 * @param type the class representing the return type
	 * @return a new CaseBuilder instance
	 */
	public static <T> CaseBuilder<T> builder(Class<T> type) {
		return new CaseBuilder<>(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends T> getType() {
		return type;
	}

	/**
	 * Returns the list of WHEN clauses in this CASE expression.
	 *
	 * @return an unmodifiable list of WHEN clauses
	 */
	public List<WhenClause<T>> getWhenClauses() {
		return List.copyOf(whenClauses);
	}

	/**
	 * Returns the ELSE expression for this CASE statement.
	 *
	 * @return the ELSE expression or null if not specified
	 */
	public Expression<T> getElseExpression() {
		return elseExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> R accept(ExpressionVisitor<R> visitor) {
		return visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("CASE");
		for (WhenClause<T> whenClause : whenClauses) {
			sb.append(" ").append(whenClause);
		}
		if (elseExpression != null) {
			sb.append(" ELSE ").append(elseExpression);
		}
		sb.append(" END");
		return sb.toString();
	}

	/**
	 * Builder class for constructing CASE expressions with a fluent API.
	 *
	 * @param <T> the return type of the CASE expression
	 */
	public static class CaseBuilder<T> {
		private final Class<T> type;
		private final List<WhenClause<T>> whenClauses;

		/**
		 * Constructs a new CaseBuilder.
		 *
		 * @param type the return type of the CASE expression
		 */
		private CaseBuilder(Class<T> type) {
			this.type = type;
			this.whenClauses = new ArrayList<>();
		}

		/**
		 * Adds a WHEN clause with a condition and expression value.
		 *
		 * @param condition the boolean condition to evaluate
		 * @param value     the expression to return when the condition is true
		 * @return this builder for method chaining
		 */
		public CaseBuilder<T> when(BooleanExpression condition, Expression<T> value) {
			whenClauses.add(new WhenClause<>(condition, value));
			return this;
		}

		/**
		 * Adds a WHEN clause with a condition and constant value.
		 *
		 * @param condition the boolean condition to evaluate
		 * @param value     the constant value to return when the condition is true
		 * @return this builder for method chaining
		 */
		public CaseBuilder<T> when(BooleanExpression condition, T value) {
			whenClauses.add(new WhenClause<>(condition, value, type));
			return this;
		}

		/**
		 * Sets the ELSE clause with an expression value and builds the CASE expression.
		 *
		 * @param elseValue the expression to return when no WHEN conditions match
		 * @return the completed CaseExpression
		 */
		public CaseExpression<T> otherwise(Expression<T> elseValue) {
			return new CaseExpression<>(type, whenClauses, elseValue);
		}

		/**
		 * Sets the ELSE clause with a constant value and builds the CASE expression.
		 *
		 * @param elseValue the constant value to return when no WHEN conditions match
		 * @return the completed CaseExpression
		 */
		public CaseExpression<T> otherwise(T elseValue) {
			return new CaseExpression<>(type, whenClauses, new ConstantExpression<>(elseValue, type));
		}

		/**
		 * Builds the CASE expression without an ELSE clause. In SQL, this means NULL
		 * will be returned when no WHEN conditions match.
		 *
		 * @return the completed CaseExpression
		 */
		public CaseExpression<T> end() {
			return new CaseExpression<>(type, whenClauses, null);
		}
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
		public <R> R accept(ExpressionVisitor<R> visitor) {
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