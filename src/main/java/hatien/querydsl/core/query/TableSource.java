package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.visitor.ExpressionVisitor;

/**
 * Represents a table source in a query, which can be a simple table reference
 * or a table with an alias. This is used in FROM clauses and as the base for
 * JOIN operations.
 *
 * @param <T> the type of the table source expression
 */
public class TableSource<T> implements Expression<T> {
	private final Expression<T> source;
	private final String alias;

	/**
	 * Creates a TableSource without an alias.
	 *
	 * @param source the source expression (typically an EntityPath)
	 */
	public TableSource(Expression<T> source) {
		this(source, null);
	}

	/**
	 * Creates a TableSource with an alias.
	 *
	 * @param source the source expression (typically an EntityPath)
	 * @param alias  the alias to use for this table source
	 */
	public TableSource(Expression<T> source, String alias) {
		this.source = source;
		this.alias = alias;
	}

	/**
	 * Returns the underlying source expression.
	 *
	 * @return the source expression
	 */
	public Expression<T> getSource() {
		return source;
	}

	/**
	 * Returns the alias for this table source.
	 *
	 * @return the alias string, or null if no alias is specified
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Checks if this table source has an alias.
	 *
	 * @return true if an alias is specified, false otherwise
	 */
	public boolean hasAlias() {
		return alias != null && !alias.trim().isEmpty();
	}

	/**
	 * Returns the effective name to use for this table source. If an alias is
	 * present, returns the alias; otherwise returns the source name.
	 *
	 * @return the effective name for SQL generation
	 */
	public String getEffectiveName() {
		if (hasAlias()) {
			return alias;
		}
		// For EntityPath, we would typically extract the table name
		// For now, we'll use the string representation
		return source.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends T> getType() {
		return source.getType();
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
		if (hasAlias()) {
			return source + " AS " + alias;
		}
		return source.toString();
	}
}