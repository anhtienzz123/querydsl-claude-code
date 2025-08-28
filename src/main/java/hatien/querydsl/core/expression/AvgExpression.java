package hatien.querydsl.core.expression;

import java.math.BigDecimal;

/**
 * Expression representing the AVG aggregate function in SQL. AVG calculates the
 * average of all numeric values in a column. Always returns BigDecimal for
 * precision in decimal calculations.
 *
 * @param <T> the numeric type being averaged
 */
public class AvgExpression<T extends Number> extends AggregateExpression<BigDecimal> {

	/**
	 * Creates an AVG(expression) that calculates the average of the given numeric
	 * expression.
	 *
	 * @param <T>        the numeric type
	 * @param expression the numeric expression to average
	 * @return an AvgExpression for the given expression
	 */
	public static <T extends Number> AvgExpression<T> avg(Expression<T> expression) {
		return new AvgExpression<>(expression);
	}

	/**
	 * Constructs a new AvgExpression.
	 *
	 * @param expression the numeric expression to average
	 */
	private AvgExpression(Expression<T> expression) {
		super(BigDecimal.class, AggregateType.AVG, expression);
	}
}