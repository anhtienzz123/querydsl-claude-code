package hatien.querydsl.core.expression;

import hatien.querydsl.core.visitor.ExpressionVisitor;

public interface Expression<T> {
    /**
     * Returns the type of this expression.
     *
     * @return the Class object representing the type of this expression
     */
    Class<? extends T> getType();
    
    /**
     * Accepts a visitor and delegates the visit call to the appropriate method.
     * This is part of the Visitor pattern implementation for expression processing.
     *
     * @param <R> the return type of the visitor
     * @param visitor the expression visitor to accept
     * @return the result of the visitor's visit operation
     */
    <R> R accept(ExpressionVisitor<R> visitor);
    
    /**
     * Returns a string representation of this expression.
     *
     * @return a string representation of this expression
     */
    String toString();
}