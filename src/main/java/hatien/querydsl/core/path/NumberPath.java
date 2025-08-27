package hatien.querydsl.core.path;

import hatien.querydsl.core.expression.ComparableExpression;
import hatien.querydsl.core.visitor.ExpressionVisitor;

public class NumberPath<T extends Number & Comparable<T>> extends ComparableExpression<T> implements Path<T> {
    private final String name;
    private final Path<?> parent;
    
    /**
     * Constructs a new NumberPath with the specified type, name, and parent path.
     *
     * @param type the Class object representing the number type of this path
     * @param name the name of this path segment
     * @param parent the parent path, or null if this is a root path
     */
    public NumberPath(Class<? extends T> type, String name, Path<?> parent) {
        super(type);
        this.name = name;
        this.parent = parent;
    }
    
    /**
     * Constructs a new NumberPath with the specified type and name, without a parent.
     *
     * @param type the Class object representing the number type of this path
     * @param name the name of this path segment
     */
    public NumberPath(Class<? extends T> type, String name) {
        this(type, name, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Path<?> getParent() {
        return parent;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getFullPath() {
        if (parent != null) {
            return parent.getFullPath() + "." + name;
        }
        return name;
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
        return getFullPath();
    }
}