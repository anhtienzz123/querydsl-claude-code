package hatien.querydsl.core.expression;

import hatien.querydsl.core.visitor.ExpressionVisitor;

/**
 * Base class for all SQL aggregate function expressions (COUNT, SUM, AVG, MIN, MAX).
 * Aggregate expressions operate on groups of rows and return a single value.
 *
 * @param <T> the return type of the aggregate function
 */
public abstract class AggregateExpression<T> implements Expression<T> {
    protected final Class<? extends T> type;
    protected final AggregateType aggregateType;
    protected final Expression<?> expression;

    public enum AggregateType {
        COUNT, SUM, AVG, MIN, MAX
    }

    /**
     * Constructs a new AggregateExpression with the specified type, aggregate operation, and target expression.
     *
     * @param type the return type of this aggregate function
     * @param aggregateType the type of aggregate operation
     * @param expression the expression to aggregate over (can be null for COUNT(*))
     */
    public AggregateExpression(Class<? extends T> type, AggregateType aggregateType, Expression<?> expression) {
        this.type = type;
        this.aggregateType = aggregateType;
        this.expression = expression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends T> getType() {
        return type;
    }

    /**
     * Returns the type of aggregate operation this expression represents.
     *
     * @return the AggregateType of this expression
     */
    public AggregateType getAggregateType() {
        return aggregateType;
    }

    /**
     * Returns the expression being aggregated over.
     * May be null for operations like COUNT(*).
     *
     * @return the target expression or null
     */
    public Expression<?> getExpression() {
        return expression;
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
        if (expression == null) {
            return String.format("%s(*)", aggregateType);
        }
        return String.format("%s(%s)", aggregateType, expression);
    }
}