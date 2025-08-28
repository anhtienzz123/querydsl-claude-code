package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.predicate.Predicate;
import hatien.querydsl.core.visitor.ExpressionVisitor;

/**
 * Expression representing a SQL JOIN operation between two table sources. This
 * class encapsulates the join type, target table, optional alias, and join
 * condition.
 *
 * @param <T> the type of the joined expression
 */
public class JoinExpression<T> implements Expression<T> {
	private final JoinType joinType;
	private final Expression<T> target;
	private final String alias;
	private final Predicate condition;

	/**
	 * Constructs a new JoinExpression with the specified join type, target, and
	 * condition.
	 *
	 * @param joinType  the type of join (INNER, LEFT, RIGHT, etc.)
	 * @param target    the target expression/table to join with
	 * @param condition the join condition (ON clause)
	 */
	public JoinExpression(JoinType joinType, Expression<T> target, Predicate condition) {
		this(joinType, target, null, condition);
	}

	/**
	 * Constructs a new JoinExpression with the specified join type, target, alias,
	 * and condition.
	 *
	 * @param joinType  the type of join (INNER, LEFT, RIGHT, etc.)
	 * @param target    the target expression/table to join with
	 * @param alias     optional alias for the joined table
	 * @param condition the join condition (ON clause)
	 */
	public JoinExpression(JoinType joinType, Expression<T> target, String alias, Predicate condition) {
		this.joinType = joinType;
		this.target = target;
		this.alias = alias;
		this.condition = condition;
	}

	/**
	 * Returns the type of join operation.
	 *
	 * @return the JoinType (INNER, LEFT, RIGHT, etc.)
	 */
	public JoinType getJoinType() {
		return joinType;
	}

	/**
	 * Returns the target expression being joined.
	 *
	 * @return the target expression/table
	 */
	public Expression<T> getTarget() {
		return target;
	}

	/**
	 * Returns the alias for the joined table, if any.
	 *
	 * @return the alias string, or null if no alias is specified
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Returns the join condition (ON clause).
	 *
	 * @return the join condition predicate
	 */
	public Predicate getCondition() {
		return condition;
	}

	/**
	 * Checks if this join has an alias.
	 *
	 * @return true if an alias is specified, false otherwise
	 */
	public boolean hasAlias() {
		return alias != null && !alias.trim().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends T> getType() {
		return target.getType();
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
		StringBuilder sb = new StringBuilder();
		sb.append(joinType.getSqlKeyword()).append(" ");
		sb.append(target);
		if (hasAlias()) {
			sb.append(" AS ").append(alias);
		}
		sb.append(" ON ").append(condition);
		return sb.toString();
	}
}