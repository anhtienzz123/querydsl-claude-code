package hatien.querydsl.core.expression;

/**
 * Expression representing the MAX aggregate function in SQL.
 * MAX finds the largest value in a column.
 * Returns the same type as the input expression.
 *
 * @param <T> the comparable type
 */
public class MaxExpression<T extends Comparable<T>> extends AggregateExpression<T> {

    /**
     * Creates a MAX(expression) that finds the maximum value of the given comparable expression.
     *
     * @param <T> the comparable type
     * @param expression the comparable expression to find the maximum of
     * @return a MaxExpression for the given expression
     */
    public static <T extends Comparable<T>> MaxExpression<T> max(Expression<T> expression) {
        return new MaxExpression<>(expression);
    }

    /**
     * Constructs a new MaxExpression.
     *
     * @param expression the comparable expression to find the maximum of
     */
    @SuppressWarnings("unchecked")
    private MaxExpression(Expression<T> expression) {
        super((Class<? extends T>) expression.getType(), AggregateType.MAX, expression);
    }
}