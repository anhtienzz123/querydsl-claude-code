package hatien.querydsl.core.predicate;

import hatien.querydsl.core.expression.SimpleExpression;
import hatien.querydsl.core.visitor.ExpressionVisitor;

public class BooleanExpression extends SimpleExpression<Boolean> implements Predicate {
    private final PredicateType type;
    private final Object[] operands;
    
    public enum PredicateType {
        EQ, NE, LT, GT, LOE, GOE, BETWEEN, 
        IS_NULL, IS_NOT_NULL, IN, 
        LIKE, CONTAINS, STARTS_WITH, ENDS_WITH, IS_EMPTY, IS_NOT_EMPTY,
        AND, OR, NOT
    }
    
    /**
     * Constructs a new BooleanExpression with the specified predicate type and operands.
     *
     * @param type the type of predicate operation
     * @param operands the operands for this predicate operation
     */
    public BooleanExpression(PredicateType type, Object... operands) {
        super(Boolean.class);
        this.type = type;
        this.operands = operands;
    }
    
    /**
     * Returns the type of this predicate operation.
     *
     * @return the PredicateType of this boolean expression
     */
    public PredicateType getPredicateType() {
        return type;
    }
    
    /**
     * Returns the operands used in this predicate operation.
     *
     * @return an array of operands for this boolean expression
     */
    public Object[] getOperands() {
        return operands;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Predicate not() {
        return new BooleanExpression(PredicateType.NOT, this);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression and(Predicate other) {
        return new BooleanExpression(PredicateType.AND, this, other);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanExpression or(Predicate other) {
        return new BooleanExpression(PredicateType.OR, this, other);
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
        return String.format("%s(%s)", type, java.util.Arrays.toString(operands));
    }
}