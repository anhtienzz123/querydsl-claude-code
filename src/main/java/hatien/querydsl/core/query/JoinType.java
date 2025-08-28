package hatien.querydsl.core.query;

/**
 * Enumeration of SQL JOIN types supported by the QueryDSL implementation. Each
 * JOIN type defines how tables should be combined in a query.
 */
public enum JoinType {
	/**
	 * INNER JOIN - Returns only rows that have matching values in both tables. This
	 * is the most restrictive join type.
	 */
	INNER("INNER JOIN"),

	/**
	 * LEFT JOIN (LEFT OUTER JOIN) - Returns all rows from the left table, and
	 * matched rows from the right table. NULL values are returned for unmatched
	 * rows from the right table.
	 */
	LEFT("LEFT JOIN"),

	/**
	 * RIGHT JOIN (RIGHT OUTER JOIN) - Returns all rows from the right table, and
	 * matched rows from the left table. NULL values are returned for unmatched rows
	 * from the left table.
	 */
	RIGHT("RIGHT JOIN"),

	/**
	 * FULL OUTER JOIN - Returns all rows when there is a match in either left or
	 * right table. NULL values are returned for unmatched rows from both sides.
	 */
	FULL_OUTER("FULL OUTER JOIN"),

	/**
	 * CROSS JOIN - Returns the Cartesian product of both tables. Each row from the
	 * left table is combined with every row from the right table.
	 */
	CROSS("CROSS JOIN");

	private final String sqlKeyword;

	/**
	 * Constructor for JoinType enum.
	 *
	 * @param sqlKeyword the SQL keyword used for this join type
	 */
	JoinType(String sqlKeyword) {
		this.sqlKeyword = sqlKeyword;
	}

	/**
	 * Returns the SQL keyword for this join type.
	 *
	 * @return the SQL keyword (e.g., "INNER JOIN", "LEFT JOIN")
	 */
	public String getSqlKeyword() {
		return sqlKeyword;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return sqlKeyword;
	}
}