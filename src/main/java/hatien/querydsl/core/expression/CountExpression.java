package hatien.querydsl.core.expression;

/**
 * Expression representing the COUNT aggregate function in SQL.
 * COUNT returns the number of rows that match the query criteria.
 * Always returns a Long value.
 */
public class CountExpression extends AggregateExpression<Long> {

    /**
     * Creates a COUNT(*) expression that counts all rows.
     *
     * @return a CountExpression that counts all rows
     */
    public static CountExpression countAll() {
        return new CountExpression(null);
    }

    /**
     * Creates a COUNT(expression) that counts non-null values of the given expression.
     *
     * @param expression the expression to count non-null values of
     * @return a CountExpression that counts the given expression
     */
    public static CountExpression count(Expression<?> expression) {
        return new CountExpression(expression);
    }

    /**
     * Constructs a new CountExpression.
     *
     * @param expression the expression to count, or null for COUNT(*)
     */
    private CountExpression(Expression<?> expression) {
        super(Long.class, AggregateType.COUNT, expression);
    }

    /**
     * Returns true if this is a COUNT(*) expression.
     *
     * @return true if counting all rows, false if counting a specific expression
     */
    public boolean isCountAll() {
        return expression == null;
    }
}