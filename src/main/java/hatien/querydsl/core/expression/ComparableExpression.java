package hatien.querydsl.core.expression;

import hatien.querydsl.core.predicate.BooleanExpression;
import hatien.querydsl.core.predicate.Predicates;

public abstract class ComparableExpression<T extends Comparable<T>> extends SimpleExpression<T> {
    
    /**
     * Constructs a new ComparableExpression with the specified type.
     *
     * @param type the Class object representing the comparable type of this expression
     */
    public ComparableExpression(Class<? extends T> type) {
        super(type);
    }
    
    /**
     * Creates a less-than predicate comparing this expression to the specified value.
     *
     * @param value the value to compare against
     * @return a BooleanExpression representing the less-than condition
     */
    public BooleanExpression lt(T value) {
        return Predicates.lt(this, value);
    }
    
    /**
     * Creates a less-than-or-equal predicate comparing this expression to the specified value.
     *
     * @param value the value to compare against
     * @return a BooleanExpression representing the less-than-or-equal condition
     */
    public BooleanExpression loe(T value) {
        return Predicates.loe(this, value);
    }
    
    /**
     * Creates a greater-than predicate comparing this expression to the specified value.
     *
     * @param value the value to compare against
     * @return a BooleanExpression representing the greater-than condition
     */
    public BooleanExpression gt(T value) {
        return Predicates.gt(this, value);
    }
    
    /**
     * Creates a greater-than-or-equal predicate comparing this expression to the specified value.
     *
     * @param value the value to compare against
     * @return a BooleanExpression representing the greater-than-or-equal condition
     */
    public BooleanExpression goe(T value) {
        return Predicates.goe(this, value);
    }
    
    /**
     * Creates a BETWEEN predicate testing if this expression's value falls within the specified range.
     *
     * @param min the minimum value of the range (inclusive)
     * @param max the maximum value of the range (inclusive)
     * @return a BooleanExpression representing the BETWEEN condition
     */
    public BooleanExpression between(T min, T max) {
        return Predicates.between(this, min, max);
    }
}