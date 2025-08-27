package hatien.querydsl.core.expression;

import hatien.querydsl.core.predicate.BooleanExpression;
import hatien.querydsl.core.predicate.Predicates;

public abstract class StringExpression extends ComparableExpression<String> {
    
    /**
     * Constructs a new StringExpression.
     */
    public StringExpression() {
        super(String.class);
    }
    
    /**
     * Creates a LIKE predicate for pattern matching against this string expression.
     *
     * @param pattern the pattern to match, can include wildcards like % and _
     * @return a BooleanExpression representing the LIKE condition
     */
    public BooleanExpression like(String pattern) {
        return Predicates.like(this, pattern);
    }
    
    /**
     * Creates a predicate that tests if this string expression contains the specified substring.
     *
     * @param substring the substring to search for
     * @return a BooleanExpression representing the contains condition
     */
    public BooleanExpression contains(String substring) {
        return Predicates.contains(this, substring);
    }
    
    /**
     * Creates a predicate that tests if this string expression starts with the specified prefix.
     *
     * @param prefix the prefix to test for
     * @return a BooleanExpression representing the starts-with condition
     */
    public BooleanExpression startsWith(String prefix) {
        return Predicates.startsWith(this, prefix);
    }
    
    /**
     * Creates a predicate that tests if this string expression ends with the specified suffix.
     *
     * @param suffix the suffix to test for
     * @return a BooleanExpression representing the ends-with condition
     */
    public BooleanExpression endsWith(String suffix) {
        return Predicates.endsWith(this, suffix);
    }
    
    /**
     * Creates a predicate that tests if this string expression is empty (has zero length).
     *
     * @return a BooleanExpression representing the empty string condition
     */
    public BooleanExpression isEmpty() {
        return Predicates.isEmpty(this);
    }
    
    /**
     * Creates a predicate that tests if this string expression is not empty (has non-zero length).
     *
     * @return a BooleanExpression representing the non-empty string condition
     */
    public BooleanExpression isNotEmpty() {
        return Predicates.isNotEmpty(this);
    }
}