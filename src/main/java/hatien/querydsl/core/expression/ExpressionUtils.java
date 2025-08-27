package hatien.querydsl.core.expression;

/**
 * Utility class providing static factory methods for creating common SQL expressions
 * including aggregate functions and CASE WHEN constructs.
 * This class provides a convenient API for building complex expressions.
 */
public final class ExpressionUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ExpressionUtils() {
        throw new AssertionError("ExpressionUtils should not be instantiated");
    }

    // =============================================================================
    // AGGREGATE FUNCTIONS
    // =============================================================================

    /**
     * Creates a COUNT(*) expression that counts all rows.
     *
     * @return a CountExpression that counts all rows
     */
    public static CountExpression count() {
        return CountExpression.countAll();
    }

    /**
     * Creates a COUNT(expression) that counts non-null values of the given expression.
     *
     * @param expression the expression to count non-null values of
     * @return a CountExpression that counts the given expression
     */
    public static CountExpression count(Expression<?> expression) {
        return CountExpression.count(expression);
    }

    /**
     * Creates a SUM(expression) that calculates the sum of the given numeric expression.
     *
     * @param <T> the numeric type
     * @param expression the numeric expression to sum
     * @return a SumExpression for the given expression
     */
    public static <T extends Number> SumExpression<T> sum(Expression<T> expression) {
        return SumExpression.sum(expression);
    }

    /**
     * Creates an AVG(expression) that calculates the average of the given numeric expression.
     *
     * @param <T> the numeric type
     * @param expression the numeric expression to average
     * @return an AvgExpression for the given expression
     */
    public static <T extends Number> AvgExpression<T> avg(Expression<T> expression) {
        return AvgExpression.avg(expression);
    }

    /**
     * Creates a MIN(expression) that finds the minimum value of the given comparable expression.
     *
     * @param <T> the comparable type
     * @param expression the comparable expression to find the minimum of
     * @return a MinExpression for the given expression
     */
    public static <T extends Comparable<T>> MinExpression<T> min(Expression<T> expression) {
        return MinExpression.min(expression);
    }

    /**
     * Creates a MAX(expression) that finds the maximum value of the given comparable expression.
     *
     * @param <T> the comparable type
     * @param expression the comparable expression to find the maximum of
     * @return a MaxExpression for the given expression
     */
    public static <T extends Comparable<T>> MaxExpression<T> max(Expression<T> expression) {
        return MaxExpression.max(expression);
    }

    // =============================================================================
    // CASE WHEN CONSTRUCTS
    // =============================================================================

    /**
     * Creates a new CaseBuilder for building CASE expressions with the specified return type.
     *
     * <p>Example usage:
     * <pre>
     * CaseExpression&lt;String&gt; caseExpr = ExpressionUtils.caseWhen(String.class)
     *     .when(user.age.lt(18), "Minor")
     *     .when(user.age.between(18, 65), "Adult")
     *     .otherwise("Senior");
     * </pre>
     *
     * @param <T> the return type
     * @param type the class representing the return type
     * @return a new CaseBuilder instance
     */
    public static <T> CaseExpression.CaseBuilder<T> caseWhen(Class<T> type) {
        return CaseExpression.builder(type);
    }

    /**
     * Creates a simple CASE expression for String values.
     * This is a convenience method for the most common case.
     *
     * @return a new CaseBuilder for String values
     */
    public static CaseExpression.CaseBuilder<String> caseWhen() {
        return CaseExpression.builder(String.class);
    }

    /**
     * Creates a simple CASE expression for Integer values.
     *
     * @return a new CaseBuilder for Integer values
     */
    public static CaseExpression.CaseBuilder<Integer> caseWhenInt() {
        return CaseExpression.builder(Integer.class);
    }

    /**
     * Creates a simple CASE expression for Long values.
     *
     * @return a new CaseBuilder for Long values
     */
    public static CaseExpression.CaseBuilder<Long> caseWhenLong() {
        return CaseExpression.builder(Long.class);
    }

    /**
     * Creates a simple CASE expression for Boolean values.
     *
     * @return a new CaseBuilder for Boolean values
     */
    public static CaseExpression.CaseBuilder<Boolean> caseWhenBoolean() {
        return CaseExpression.builder(Boolean.class);
    }
}