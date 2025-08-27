package hatien.querydsl.core.predicate;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.expression.StringExpression;

/**
 * Utility class providing static factory methods for creating predicate expressions.
 * This class cannot be instantiated.
 */
public final class Predicates {
    
    private Predicates() {}
    
    /**
     * Creates an equality predicate comparing an expression to a value.
     *
     * @param <T> the type of the expression and value
     * @param left the expression to compare
     * @param value the value to compare against
     * @return a BooleanExpression representing the equality condition
     */
    public static <T> BooleanExpression eq(Expression<T> left, T value) {
        return new BooleanExpression(BooleanExpression.PredicateType.EQ, left, value);
    }
    
    /**
     * Creates a not-equal predicate comparing an expression to a value.
     *
     * @param <T> the type of the expression and value
     * @param left the expression to compare
     * @param value the value to compare against
     * @return a BooleanExpression representing the not-equal condition
     */
    public static <T> BooleanExpression ne(Expression<T> left, T value) {
        return new BooleanExpression(BooleanExpression.PredicateType.NE, left, value);
    }
    
    /**
     * Creates a less-than predicate comparing an expression to a value.
     *
     * @param <T> the comparable type of the expression and value
     * @param left the expression to compare
     * @param value the value to compare against
     * @return a BooleanExpression representing the less-than condition
     */
    public static <T extends Comparable<T>> BooleanExpression lt(Expression<T> left, T value) {
        return new BooleanExpression(BooleanExpression.PredicateType.LT, left, value);
    }
    
    /**
     * Creates a less-than-or-equal predicate comparing an expression to a value.
     *
     * @param <T> the comparable type of the expression and value
     * @param left the expression to compare
     * @param value the value to compare against
     * @return a BooleanExpression representing the less-than-or-equal condition
     */
    public static <T extends Comparable<T>> BooleanExpression loe(Expression<T> left, T value) {
        return new BooleanExpression(BooleanExpression.PredicateType.LOE, left, value);
    }
    
    /**
     * Creates a greater-than predicate comparing an expression to a value.
     *
     * @param <T> the comparable type of the expression and value
     * @param left the expression to compare
     * @param value the value to compare against
     * @return a BooleanExpression representing the greater-than condition
     */
    public static <T extends Comparable<T>> BooleanExpression gt(Expression<T> left, T value) {
        return new BooleanExpression(BooleanExpression.PredicateType.GT, left, value);
    }
    
    /**
     * Creates a greater-than-or-equal predicate comparing an expression to a value.
     *
     * @param <T> the comparable type of the expression and value
     * @param left the expression to compare
     * @param value the value to compare against
     * @return a BooleanExpression representing the greater-than-or-equal condition
     */
    public static <T extends Comparable<T>> BooleanExpression goe(Expression<T> left, T value) {
        return new BooleanExpression(BooleanExpression.PredicateType.GOE, left, value);
    }
    
    /**
     * Creates a BETWEEN predicate testing if an expression's value falls within a range.
     *
     * @param <T> the comparable type of the expression and range values
     * @param left the expression to test
     * @param min the minimum value of the range (inclusive)
     * @param max the maximum value of the range (inclusive)
     * @return a BooleanExpression representing the BETWEEN condition
     */
    public static <T extends Comparable<T>> BooleanExpression between(Expression<T> left, T min, T max) {
        return new BooleanExpression(BooleanExpression.PredicateType.BETWEEN, left, min, max);
    }
    
    /**
     * Creates a predicate testing if an expression is null.
     *
     * @param <T> the type of the expression
     * @param expression the expression to test
     * @return a BooleanExpression representing the IS NULL condition
     */
    public static <T> BooleanExpression isNull(Expression<T> expression) {
        return new BooleanExpression(BooleanExpression.PredicateType.IS_NULL, expression);
    }
    
    /**
     * Creates a predicate testing if an expression is not null.
     *
     * @param <T> the type of the expression
     * @param expression the expression to test
     * @return a BooleanExpression representing the IS NOT NULL condition
     */
    public static <T> BooleanExpression isNotNull(Expression<T> expression) {
        return new BooleanExpression(BooleanExpression.PredicateType.IS_NOT_NULL, expression);
    }
    
    /**
     * Creates a predicate testing if an expression's value is in a set of values.
     *
     * @param <T> the type of the expression and values
     * @param expression the expression to test
     * @param values the values to test against
     * @return a BooleanExpression representing the IN condition
     */
    @SafeVarargs
    public static <T> BooleanExpression in(Expression<T> expression, T... values) {
        return new BooleanExpression(BooleanExpression.PredicateType.IN, expression, values);
    }
    
    /**
     * Creates a LIKE predicate for pattern matching against a string expression.
     *
     * @param expression the string expression to match
     * @param pattern the pattern to match, can include wildcards like % and _
     * @return a BooleanExpression representing the LIKE condition
     */
    public static BooleanExpression like(StringExpression expression, String pattern) {
        return new BooleanExpression(BooleanExpression.PredicateType.LIKE, expression, pattern);
    }
    
    /**
     * Creates a predicate testing if a string expression contains a substring.
     *
     * @param expression the string expression to test
     * @param substring the substring to search for
     * @return a BooleanExpression representing the contains condition
     */
    public static BooleanExpression contains(StringExpression expression, String substring) {
        return new BooleanExpression(BooleanExpression.PredicateType.CONTAINS, expression, substring);
    }
    
    /**
     * Creates a predicate testing if a string expression starts with a prefix.
     *
     * @param expression the string expression to test
     * @param prefix the prefix to test for
     * @return a BooleanExpression representing the starts-with condition
     */
    public static BooleanExpression startsWith(StringExpression expression, String prefix) {
        return new BooleanExpression(BooleanExpression.PredicateType.STARTS_WITH, expression, prefix);
    }
    
    /**
     * Creates a predicate testing if a string expression ends with a suffix.
     *
     * @param expression the string expression to test
     * @param suffix the suffix to test for
     * @return a BooleanExpression representing the ends-with condition
     */
    public static BooleanExpression endsWith(StringExpression expression, String suffix) {
        return new BooleanExpression(BooleanExpression.PredicateType.ENDS_WITH, expression, suffix);
    }
    
    /**
     * Creates a predicate testing if a string expression is empty.
     *
     * @param expression the string expression to test
     * @return a BooleanExpression representing the empty string condition
     */
    public static BooleanExpression isEmpty(StringExpression expression) {
        return new BooleanExpression(BooleanExpression.PredicateType.IS_EMPTY, expression);
    }
    
    /**
     * Creates a predicate testing if a string expression is not empty.
     *
     * @param expression the string expression to test
     * @return a BooleanExpression representing the non-empty string condition
     */
    public static BooleanExpression isNotEmpty(StringExpression expression) {
        return new BooleanExpression(BooleanExpression.PredicateType.IS_NOT_EMPTY, expression);
    }
}