package hatien.querydsl.core.expression;

/**
 * Expression representing the SUM aggregate function in SQL. SUM calculates the
 * total of all numeric values in a column. Returns the same numeric type as the
 * input expression.
 *
 * @param <T> the numeric type being summed
 */
public class SumExpression<T extends Number> extends AggregateExpression<T> {

	/**
	 * Creates a SUM(expression) that calculates the sum of the given numeric
	 * expression.
	 *
	 * @param <T>        the numeric type
	 * @param expression the numeric expression to sum
	 * @return a SumExpression for the given expression
	 */
	public static <T extends Number> SumExpression<T> sum(Expression<T> expression) {
		return new SumExpression<>(expression);
	}

	/**
	 * Constructs a new SumExpression.
	 *
	 * @param expression the numeric expression to sum
	 */
	@SuppressWarnings("unchecked")
	private SumExpression(Expression<T> expression) {
		super((Class<? extends T>) expression.getType(), AggregateType.SUM, expression);
	}
}