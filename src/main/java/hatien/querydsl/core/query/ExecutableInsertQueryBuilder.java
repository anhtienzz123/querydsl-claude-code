package hatien.querydsl.core.query;

import hatien.querydsl.core.database.QueryExecutor;
import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.path.Path;
import hatien.querydsl.core.visitor.SQLVisitor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Executable implementation of the InsertQuery interface that can execute SQL
 * INSERT statements using JDBC. Extends InsertQueryBuilder functionality with
 * actual database execution capabilities.
 *
 * @param <T> the type of the entity being inserted
 */
public class ExecutableInsertQueryBuilder<T> implements InsertQuery<T> {
	private Expression<T> targetEntity;
	private final List<Path<?>> columnList = new ArrayList<>();
	private final List<Object> valuesList = new ArrayList<>();
	private final Map<Path<?>, Object> setValues = new LinkedHashMap<>();

	private final SQLVisitor sqlVisitor = new SQLVisitor();
	private final QueryExecutor queryExecutor;

	/**
	 * Creates a new ExecutableInsertQueryBuilder with the specified query executor.
	 *
	 * @param queryExecutor the query executor to use for database operations
	 */
	public ExecutableInsertQueryBuilder(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InsertQuery<T> into(Expression<T> entity) {
		this.targetEntity = entity;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InsertQuery<T> columns(Path<?>... columns) {
		columnList.addAll(Arrays.asList(columns));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InsertQuery<T> values(Object... values) {
		valuesList.addAll(Arrays.asList(values));
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <V> InsertQuery<T> set(Path<V> column, V value) {
		setValues.put(column, value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Executes the INSERT statement and returns the number of affected rows.
	 */
	@Override
	public long execute() {
		try {
			String sql = toParameterizedSQL();
			List<Object> parameters = extractParameters();
			return queryExecutor.executeUpdate(sql, parameters);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to execute INSERT statement", e);
		}
	}

	/**
	 * Executes the INSERT statement and returns generated keys.
	 *
	 * @return list of generated keys
	 */
	public List<Long> executeAndReturnKeys() {
		try {
			String sql = toParameterizedSQL();
			List<Object> parameters = extractParameters();
			return queryExecutor.executeInsertWithKeys(sql, parameters);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to execute INSERT statement with key generation", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Constructs an INSERT SQL string with literal values (for display purposes).
	 */
	@Override
	public String toSQL() {
		if (targetEntity == null) {
			throw new IllegalStateException("Target entity must be specified using into()");
		}

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(targetEntity.accept(sqlVisitor));

		List<Path<?>> columnsToUse;
		List<Object> valuesToUse;

		if (!setValues.isEmpty()) {
			columnsToUse = new ArrayList<>(setValues.keySet());
			valuesToUse = new ArrayList<>(setValues.values());
		} else {
			columnsToUse = columnList;
			valuesToUse = valuesList;
		}

		if (!columnsToUse.isEmpty()) {
			sql.append(" (");
			sql.append(
					columnsToUse.stream().map(column -> column.accept(sqlVisitor)).collect(Collectors.joining(", ")));
			sql.append(")");
		}

		if (!valuesToUse.isEmpty()) {
			sql.append(" VALUES (");
			sql.append(valuesToUse.stream().map(this::formatValue).collect(Collectors.joining(", ")));
			sql.append(")");
		}

		return sql.toString();
	}

	/**
	 * Constructs a parameterized INSERT SQL string with ? placeholders.
	 *
	 * @return the parameterized INSERT SQL string
	 */
	private String toParameterizedSQL() {
		if (targetEntity == null) {
			throw new IllegalStateException("Target entity must be specified using into()");
		}

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(targetEntity.accept(sqlVisitor));

		List<Path<?>> columnsToUse;
		List<Object> valuesToUse;

		if (!setValues.isEmpty()) {
			columnsToUse = new ArrayList<>(setValues.keySet());
			valuesToUse = new ArrayList<>(setValues.values());
		} else {
			columnsToUse = columnList;
			valuesToUse = valuesList;
		}

		if (!columnsToUse.isEmpty()) {
			sql.append(" (");
			sql.append(
					columnsToUse.stream().map(column -> column.accept(sqlVisitor)).collect(Collectors.joining(", ")));
			sql.append(")");
		}

		if (!valuesToUse.isEmpty()) {
			sql.append(" VALUES (");
			sql.append(valuesToUse.stream().map(v -> "?").collect(Collectors.joining(", ")));
			sql.append(")");
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

		if (!setValues.isEmpty()) {
			parameters.addAll(setValues.values());
		} else {
			parameters.addAll(valuesList);
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