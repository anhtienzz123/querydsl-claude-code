package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.predicate.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete source structure of a query, including the main table
 * and any JOIN operations. This class manages the FROM clause and all
 * associated JOINs.
 */
public class QuerySource {
	private TableSource<?> mainSource;
	private final List<JoinExpression<?>> joins = new ArrayList<>();

	/**
	 * Creates an empty QuerySource.
	 */
	public QuerySource() {
	}

	/**
	 * Creates a QuerySource with the specified main table source.
	 *
	 * @param mainSource the main table to select from
	 */
	public QuerySource(TableSource<?> mainSource) {
		this.mainSource = mainSource;
	}

	/**
	 * Sets the main table source for the FROM clause.
	 *
	 * @param source the source expression
	 * @return this QuerySource for method chaining
	 */
	public QuerySource from(Expression<?> source) {
		this.mainSource = new TableSource<>(source);
		return this;
	}

	/**
	 * Sets the main table source with an alias for the FROM clause.
	 *
	 * @param source the source expression
	 * @param alias  the alias to use for the main table
	 * @return this QuerySource for method chaining
	 */
	public QuerySource from(Expression<?> source, String alias) {
		this.mainSource = new TableSource<>(source, alias);
		return this;
	}

	/**
	 * Adds an INNER JOIN to the query.
	 *
	 * @param target    the target table to join
	 * @param condition the join condition (ON clause)
	 * @return this QuerySource for method chaining
	 */
	public QuerySource innerJoin(Expression<?> target, Predicate condition) {
		return join(JoinType.INNER, target, null, condition);
	}

	/**
	 * Adds an INNER JOIN with alias to the query.
	 *
	 * @param target    the target table to join
	 * @param alias     the alias for the joined table
	 * @param condition the join condition (ON clause)
	 * @return this QuerySource for method chaining
	 */
	public QuerySource innerJoin(Expression<?> target, String alias, Predicate condition) {
		return join(JoinType.INNER, target, alias, condition);
	}

	/**
	 * Adds a LEFT JOIN to the query.
	 *
	 * @param target    the target table to join
	 * @param condition the join condition (ON clause)
	 * @return this QuerySource for method chaining
	 */
	public QuerySource leftJoin(Expression<?> target, Predicate condition) {
		return join(JoinType.LEFT, target, null, condition);
	}

	/**
	 * Adds a LEFT JOIN with alias to the query.
	 *
	 * @param target    the target table to join
	 * @param alias     the alias for the joined table
	 * @param condition the join condition (ON clause)
	 * @return this QuerySource for method chaining
	 */
	public QuerySource leftJoin(Expression<?> target, String alias, Predicate condition) {
		return join(JoinType.LEFT, target, alias, condition);
	}

	/**
	 * Adds a RIGHT JOIN to the query.
	 *
	 * @param target    the target table to join
	 * @param condition the join condition (ON clause)
	 * @return this QuerySource for method chaining
	 */
	public QuerySource rightJoin(Expression<?> target, Predicate condition) {
		return join(JoinType.RIGHT, target, null, condition);
	}

	/**
	 * Adds a RIGHT JOIN with alias to the query.
	 *
	 * @param target    the target table to join
	 * @param alias     the alias for the joined table
	 * @param condition the join condition (ON clause)
	 * @return this QuerySource for method chaining
	 */
	public QuerySource rightJoin(Expression<?> target, String alias, Predicate condition) {
		return join(JoinType.RIGHT, target, alias, condition);
	}

	/**
	 * Adds a FULL OUTER JOIN to the query.
	 *
	 * @param target    the target table to join
	 * @param condition the join condition (ON clause)
	 * @return this QuerySource for method chaining
	 */
	public QuerySource fullOuterJoin(Expression<?> target, Predicate condition) {
		return join(JoinType.FULL_OUTER, target, null, condition);
	}

	/**
	 * Adds a FULL OUTER JOIN with alias to the query.
	 *
	 * @param target    the target table to join
	 * @param alias     the alias for the joined table
	 * @param condition the join condition (ON clause)
	 * @return this QuerySource for method chaining
	 */
	public QuerySource fullOuterJoin(Expression<?> target, String alias, Predicate condition) {
		return join(JoinType.FULL_OUTER, target, alias, condition);
	}

	/**
	 * Adds a CROSS JOIN to the query.
	 *
	 * @param target the target table to join
	 * @return this QuerySource for method chaining
	 */
	public QuerySource crossJoin(Expression<?> target) {
		return join(JoinType.CROSS, target, null, null);
	}

	/**
	 * Adds a CROSS JOIN with alias to the query.
	 *
	 * @param target the target table to join
	 * @param alias  the alias for the joined table
	 * @return this QuerySource for method chaining
	 */
	public QuerySource crossJoin(Expression<?> target, String alias) {
		return join(JoinType.CROSS, target, alias, null);
	}

	/**
	 * Generic method to add any type of join to the query.
	 *
	 * @param joinType  the type of join
	 * @param target    the target table to join
	 * @param alias     optional alias for the joined table
	 * @param condition the join condition (can be null for CROSS JOIN)
	 * @return this QuerySource for method chaining
	 */
	@SuppressWarnings("unchecked")
	private QuerySource join(JoinType joinType, Expression<?> target, String alias, Predicate condition) {
		JoinExpression<?> joinExpr = new JoinExpression<>(joinType, target, alias, condition);
		joins.add(joinExpr);
		return this;
	}

	/**
	 * Returns the main table source.
	 *
	 * @return the main table source, or null if not set
	 */
	public TableSource<?> getMainSource() {
		return mainSource;
	}

	/**
	 * Returns the list of JOIN expressions.
	 *
	 * @return an unmodifiable list of JOIN expressions
	 */
	public List<JoinExpression<?>> getJoins() {
		return List.copyOf(joins);
	}

	/**
	 * Checks if this query source has any JOINs.
	 *
	 * @return true if there are JOINs, false otherwise
	 */
	public boolean hasJoins() {
		return !joins.isEmpty();
	}

	/**
	 * Checks if this query source has been configured with a main source.
	 *
	 * @return true if a main source is set, false otherwise
	 */
	public boolean hasMainSource() {
		return mainSource != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (mainSource != null) {
			sb.append("FROM ").append(mainSource);
		}
		for (JoinExpression<?> join : joins) {
			sb.append(" ").append(join);
		}
		return sb.toString();
	}
}