package hatien.querydsl.core.path;

import hatien.querydsl.core.expression.StringExpression;
import hatien.querydsl.core.visitor.ExpressionVisitor;

public class StringPath extends StringExpression implements Path<String> {
    private final String name;
    private final Path<?> parent;
    
    /**
     * Constructs a new StringPath with the specified name and parent path.
     *
     * @param name the name of this path segment
     * @param parent the parent path, or null if this is a root path
     */
    public StringPath(String name, Path<?> parent) {
        this.name = name;
        this.parent = parent;
    }
    
    /**
     * Constructs a new StringPath with the specified name, without a parent.
     *
     * @param name the name of this path segment
     */
    public StringPath(String name) {
        this(name, null);
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