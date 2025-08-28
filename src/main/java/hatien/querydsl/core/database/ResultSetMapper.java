package hatien.querydsl.core.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps ResultSet rows to Java objects using reflection. Supports primitive
 * types, common Java types, and entity classes.
 */
public class ResultSetMapper {

	/**
	 * Maps a ResultSet row to an object of the specified type.
	 *
	 * @param <T>        the type of object to create
	 * @param rs         the ResultSet positioned at the row to map
	 * @param targetType the class of the target type
	 * @return the mapped object
	 * @throws SQLException if ResultSet access fails
	 */
	@SuppressWarnings("unchecked")
	public <T> T mapRow(ResultSet rs, Class<T> targetType) throws SQLException {
		if (targetType == null) {
			throw new IllegalArgumentException("Target type cannot be null");
		}

		// Handle primitive and wrapper types directly
		if (isPrimitiveOrWrapper(targetType)) {
			return (T) mapPrimitive(rs, targetType, 1);
		}

		// Handle Object[] for multi-column selects
		if (targetType == Object[].class) {
			return (T) mapObjectArray(rs);
		}

		// Handle entity classes
		return mapEntity(rs, targetType);
	}

	/**
	 * Maps a ResultSet row to an array of objects.
	 *
	 * @param rs the ResultSet positioned at the row to map
	 * @return array of column values
	 * @throws SQLException if ResultSet access fails
	 */
	private Object[] mapObjectArray(ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		Object[] result = new Object[columnCount];

		for (int i = 1; i <= columnCount; i++) {
			result[i - 1] = rs.getObject(i);
		}

		return result;
	}

	/**
	 * Maps a ResultSet row to an entity object.
	 *
	 * @param <T>        the entity type
	 * @param rs         the ResultSet positioned at the row to map
	 * @param targetType the entity class
	 * @return the mapped entity
	 * @throws SQLException if ResultSet access fails
	 */
	private <T> T mapEntity(ResultSet rs, Class<T> targetType) throws SQLException {
		try {
			// Create instance using default constructor
			Constructor<T> constructor = targetType.getDeclaredConstructor();
			constructor.setAccessible(true);
			T entity = constructor.newInstance();

			// Get column information
			ResultSetMetaData metaData = rs.getMetaData();
			Map<String, Integer> columnMap = new HashMap<>();

			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String columnName = metaData.getColumnLabel(i);
				columnMap.put(columnName.toLowerCase(), i);
			}

			// Map fields
			Field[] fields = targetType.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String fieldName = field.getName().toLowerCase();

				// Try exact match first, then try with common naming conventions
				Integer columnIndex = columnMap.get(fieldName);
				if (columnIndex == null) {
					// Try with underscore conversion (e.g., firstName -> first_name)
					String underscoreName = camelToUnderscore(fieldName);
					columnIndex = columnMap.get(underscoreName);
				}

				if (columnIndex != null) {
					Object value = mapPrimitive(rs, field.getType(), columnIndex);
					field.set(entity, value);
				}
			}

			return entity;
		} catch (Exception e) {
			throw new SQLException("Failed to map ResultSet to " + targetType.getSimpleName(), e);
		}
	}

	/**
	 * Maps a primitive or wrapper type from a ResultSet column.
	 *
	 * @param rs          the ResultSet
	 * @param targetType  the target type
	 * @param columnIndex the column index (1-based)
	 * @return the mapped value
	 * @throws SQLException if ResultSet access fails
	 */
	private Object mapPrimitive(ResultSet rs, Class<?> targetType, int columnIndex) throws SQLException {
		if (rs.wasNull()) {
			return null;
		}

		if (targetType == String.class) {
			return rs.getString(columnIndex);
		} else if (targetType == Integer.class || targetType == int.class) {
			return rs.getInt(columnIndex);
		} else if (targetType == Long.class || targetType == long.class) {
			return rs.getLong(columnIndex);
		} else if (targetType == Double.class || targetType == double.class) {
			return rs.getDouble(columnIndex);
		} else if (targetType == Float.class || targetType == float.class) {
			return rs.getFloat(columnIndex);
		} else if (targetType == Boolean.class || targetType == boolean.class) {
			return rs.getBoolean(columnIndex);
		} else if (targetType == BigDecimal.class) {
			return rs.getBigDecimal(columnIndex);
		} else if (targetType == LocalDateTime.class) {
			Timestamp timestamp = rs.getTimestamp(columnIndex);
			return timestamp != null ? timestamp.toLocalDateTime() : null;
		} else if (targetType == java.util.Date.class) {
			return rs.getDate(columnIndex);
		} else if (targetType == java.sql.Date.class) {
			return rs.getDate(columnIndex);
		} else if (targetType == Timestamp.class) {
			return rs.getTimestamp(columnIndex);
		} else {
			return rs.getObject(columnIndex);
		}
	}

	/**
	 * Checks if a type is a primitive or wrapper type.
	 *
	 * @param type the type to check
	 * @return true if the type is primitive or wrapper
	 */
	private boolean isPrimitiveOrWrapper(Class<?> type) {
		return type.isPrimitive() || type == String.class || type == Integer.class || type == Long.class
				|| type == Double.class || type == Float.class || type == Boolean.class || type == BigDecimal.class
				|| type == LocalDateTime.class || type == java.util.Date.class || type == java.sql.Date.class
				|| type == Timestamp.class;
	}

	/**
	 * Converts camelCase to underscore_case.
	 *
	 * @param camelCase the camelCase string
	 * @return the underscore_case string
	 */
	private String camelToUnderscore(String camelCase) {
		return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
	}
}