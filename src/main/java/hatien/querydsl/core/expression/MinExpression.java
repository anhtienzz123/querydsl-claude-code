package hatien.querydsl.core.expression;

/**
 * Expression representing the MIN aggregate function in SQL. MIN finds the
 * smallest value in a column. Returns the same type as the input expression.
 *
 * @param <T> the comparable type
 */
public class MinExpression<T extends Comparable<T>> extends AggregateExpression<T> {

	/**
	 * Creates a MIN(expression) that finds the minimum value of the given
	 * comparable expression.
	 *
	 * @param <T>        the comparable type
	 * @param expression the comparable expression to find the minimum of
	 * @return a MinExpression for the given expression
	 */
	public static <T extends Comparable<T>> MinExpression<T> min(Expression<T> expression) {
		return new MinExpression<>(expression);
	}

	/**
	 * Constructs a new MinExpression.
	 *
	 * @param expression the comparable expression to find the minimum of
	 */
	@SuppressWarnings("unchecked")
	private MinExpression(Expression<T> expression) {
		super((Class<? extends T>) expression.getType(), AggregateType.MIN, expression);
	}
}