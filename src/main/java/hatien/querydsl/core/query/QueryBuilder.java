package hatien.querydsl.core.query;

import hatien.querydsl.core.predicate.Predicate;
import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.visitor.SQLVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the Query interface that builds SQL queries using a fluent
 * API. This class maintains internal lists for each SQL clause and provides
 * methods to construct and execute queries.
 *
 * @param <T> the type of results returned by this query
 */
public class QueryBuilder<T> implements Query<T> {
	private final List<Expression<?>> selectList = new ArrayList<>();
	private final List<Expression<?>> fromList = new ArrayList<>();
	private final List<Predicate> whereList = new ArrayList<>();
	private final List<Expression<?>> orderByList = new ArrayList<>();
	private final List<Expression<?>> groupByList = new ArrayList<>();
	private final List<Predicate> havingList = new ArrayList<>();
	private Long limitValue;
	private Long offsetValue;

	private final SQLVisitor sqlVisitor = new SQLVisitor();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query<T> select(Expression<?>... expressions) {
		selectList.addAll(Arrays.asList(expressions));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query<T> from(Expression<?>... sources) {
		fromList.addAll(Arrays.asList(sources));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query<T> where(Predicate... predicates) {
		whereList.addAll(Arrays.asList(predicates));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query<T> orderBy(Expression<?>... expressions) {
		orderByList.addAll(Arrays.asList(expressions));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query<T> groupBy(Expression<?>... expressions) {
		groupByList.addAll(Arrays.asList(expressions));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query<T> having(Predicate... predicates) {
		havingList.addAll(Arrays.asList(predicates));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query<T> limit(long limit) {
		this.limitValue = limit;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query<T> offset(long offset) {
		this.offsetValue = offset;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> fetch() {
		throw new UnsupportedOperationException("Query execution not implemented in this simplified version");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T fetchOne() {
		throw new UnsupportedOperationException("Query execution not implemented in this simplified version");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T fetchFirst() {
		throw new UnsupportedOperationException("Query execution not implemented in this simplified version");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long fetchCount() {
		throw new UnsupportedOperationException("Query execution not implemented in this simplified version");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Constructs a SQL string by combining all the query clauses in the proper
	 * order: SELECT, FROM, WHERE, GROUP BY, HAVING, ORDER BY, LIMIT, OFFSET.
	 */
	@Override
	public String toSQL() {
		StringBuilder sql = new StringBuilder();

		// SELECT clause
		if (!selectList.isEmpty()) {
			sql.append("SELECT ");
			sql.append(selectList.stream().map(expr -> expr.accept(sqlVisitor)).collect(Collectors.joining(", ")));
		} else {
			sql.append("SELECT *");
		}

		// FROM clause
		if (!fromList.isEmpty()) {
			sql.append(" FROM ");
			sql.append(fromList.stream().map(expr -> expr.accept(sqlVisitor)).collect(Collectors.joining(", ")));
		}

		// WHERE clause
		if (!whereList.isEmpty()) {
			sql.append(" WHERE ");
			sql.append(whereList.stream().map(predicate -> predicate.accept(sqlVisitor))
					.collect(Collectors.joining(" AND ")));
		}

		// GROUP BY clause
		if (!groupByList.isEmpty()) {
			sql.append(" GROUP BY ");
			sql.append(groupByList.stream().map(expr -> expr.accept(sqlVisitor)).collect(Collectors.joining(", ")));
		}

		// HAVING clause
		if (!havingList.isEmpty()) {
			sql.append(" HAVING ");
			sql.append(havingList.stream().map(predicate -> predicate.accept(sqlVisitor))
					.collect(Collectors.joining(" AND ")));
		}

		// ORDER BY clause
		if (!orderByList.isEmpty()) {
			sql.append(" ORDER BY ");
			sql.append(orderByList.stream().map(expr -> expr.accept(sqlVisitor)).collect(Collectors.joining(", ")));
		}

		// LIMIT clause
		if (limitValue != null) {
			sql.append(" LIMIT ").append(limitValue);
		}

		// OFFSET clause
		if (offsetValue != null) {
			sql.append(" OFFSET ").append(offsetValue);
		}

		return sql.toString();
	}
}