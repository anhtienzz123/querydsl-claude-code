package hatien.querydsl.core.query;

import hatien.querydsl.core.database.QueryExecutor;
import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.path.Path;
import hatien.querydsl.core.predicate.Predicate;
import hatien.querydsl.core.visitor.SQLVisitor;
import hatien.querydsl.core.visitor.ParameterizedSQLVisitor;
import hatien.querydsl.core.visitor.ParameterExtractor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Executable implementation of the UpdateQuery interface that can execute SQL
 * UPDATE statements using JDBC. Extends UpdateQueryBuilder functionality with
 * actual database execution capabilities.
 *
 * @param <T> the type of the entity being updated
 */
public class ExecutableUpdateQueryBuilder<T> implements UpdateQuery<T> {
	private Expression<T> targetEntity;
	private final Map<Path<?>, Object> setValues = new LinkedHashMap<>();
	private final Map<Path<?>, Expression<?>> setExpressions = new LinkedHashMap<>();
	private final List<Predicate> whereList = new ArrayList<>();

	private final SQLVisitor sqlVisitor = new SQLVisitor();
	private final ParameterizedSQLVisitor parameterizedSqlVisitor = new ParameterizedSQLVisitor();
	private final ParameterExtractor parameterExtractor = new ParameterExtractor();
	private final QueryExecutor queryExecutor;

	/**
	 * Creates a new ExecutableUpdateQueryBuilder with the specified query executor.
	 *
	 * @param queryExecutor the query executor to use for database operations
	 */
	public ExecutableUpdateQueryBuilder(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UpdateQuery<T> table(Expression<T> entity) {
		this.targetEntity = entity;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <V> UpdateQuery<T> set(Path<V> column, V value) {
		setValues.put(column, value);
		setExpressions.remove(column); // Remove any conflicting expression
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <V> UpdateQuery<T> set(Path<V> column, Expression<V> expression) {
		setExpressions.put(column, expression);
		setValues.remove(column); // Remove any conflicting value
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UpdateQuery<T> where(Predicate... predicates) {
		whereList.addAll(Arrays.asList(predicates));
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Executes the UPDATE statement and returns the number of affected rows.
	 */
	@Override
	public long execute() {
		try {
			String sql = toParameterizedSQL();
			List<Object> parameters = extractParameters();
			return queryExecutor.executeUpdate(sql, parameters);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to execute UPDATE statement", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Constructs an UPDATE SQL string with literal values (for display purposes).
	 */
	@Override
	public String toSQL() {
		if (targetEntity == null) {
			throw new IllegalStateException("Target entity must be specified using table()");
		}

		if (setValues.isEmpty() && setExpressions.isEmpty()) {
			throw new IllegalStateException("At least one SET clause must be specified");
		}

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ");
		sql.append(targetEntity.accept(sqlVisitor));

		// SET clause
		sql.append(" SET ");
		List<String> setClauses = new ArrayList<>();

		// Add value-based set clauses
		for (Map.Entry<Path<?>, Object> entry : setValues.entrySet()) {
			String columnName = entry.getKey().accept(sqlVisitor);
			String value = formatValue(entry.getValue());
			setClauses.add(columnName + " = " + value);
		}

		// Add expression-based set clauses
		for (Map.Entry<Path<?>, Expression<?>> entry : setExpressions.entrySet()) {
			String columnName = entry.getKey().accept(sqlVisitor);
			String expression = entry.getValue().accept(sqlVisitor);
			setClauses.add(columnName + " = " + expression);
		}

		sql.append(String.join(", ", setClauses));

		// WHERE clause
		if (!whereList.isEmpty()) {
			sql.append(" WHERE ");
			sql.append(whereList.stream().map(predicate -> predicate.accept(sqlVisitor))
					.collect(Collectors.joining(" AND ")));
		}

		return sql.toString();
	}

	/**
	 * Constructs a parameterized UPDATE SQL string with ? placeholders.
	 *
	 * @return the parameterized UPDATE SQL string
	 */
	private String toParameterizedSQL() {
		if (targetEntity == null) {
			throw new IllegalStateException("Target entity must be specified using table()");
		}

		if (setValues.isEmpty() && setExpressions.isEmpty()) {
			throw new IllegalStateException("At least one SET clause must be specified");
		}

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ");
		sql.append(targetEntity.accept(parameterizedSqlVisitor));

		// SET clause
		sql.append(" SET ");
		List<String> setClauses = new ArrayList<>();

		// Add value-based set clauses with placeholders
		for (Map.Entry<Path<?>, Object> entry : setValues.entrySet()) {
			String columnName = entry.getKey().accept(parameterizedSqlVisitor);
			setClauses.add(columnName + " = ?");
		}

		// Add expression-based set clauses
		for (Map.Entry<Path<?>, Expression<?>> entry : setExpressions.entrySet()) {
			String columnName = entry.getKey().accept(parameterizedSqlVisitor);
			String expression = entry.getValue().accept(parameterizedSqlVisitor);
			setClauses.add(columnName + " = " + expression);
		}

		sql.append(String.join(", ", setClauses));

		// WHERE clause
		if (!whereList.isEmpty()) {
			sql.append(" WHERE ");
			sql.append(whereList.stream().map(predicate -> predicate.accept(parameterizedSqlVisitor))
					.collect(Collectors.joining(" AND ")));
		}

		return sql.toString();
	}

	/**
	 * Extracts parameter values for prepared statement execution.
	 *
	 * @return list of parameter values in the correct order
	 */
	private List<Object> extractParameters() {
		List<Object> parameters = new ArrayList<>();

		// Add SET clause parameters (values, not expressions)
		for (Object value : setValues.values()) {
			parameters.add(value);
		}

		// Add WHERE clause parameters
		for (Predicate predicate : whereList) {
			parameters.addAll(predicate.accept(parameterExtractor));
		}

		return parameters;
	}

	/**
	 * Formats a value for use in SQL, adding quotes around strings.
	 *
	 * @param value the value to format
	 * @return formatted SQL value (strings are quoted, others converted to string)
	 */
	private String formatValue(Object value) {
		if (value instanceof String) {
			return "'" + value + "'";
		}
		return String.valueOf(value);
	}
}