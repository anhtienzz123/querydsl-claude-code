package hatien.querydsl;

import hatien.querydsl.core.query.QueryFactory;
import hatien.querydsl.core.query.Query;
import hatien.querydsl.core.query.InsertQuery;
import hatien.querydsl.core.query.UpdateQuery;
import hatien.querydsl.core.query.DeleteQuery;
import hatien.querydsl.examples.QUser;
import hatien.querydsl.examples.QProduct;
import hatien.querydsl.core.expression.CountExpression;
import hatien.querydsl.core.expression.SumExpression;
import hatien.querydsl.core.expression.AvgExpression;
import hatien.querydsl.core.expression.MinExpression;
import hatien.querydsl.core.expression.MaxExpression;
import hatien.querydsl.core.expression.CaseExpression;
import hatien.querydsl.core.expression.ExpressionUtils;
import java.math.BigDecimal;

public class QueryDSLTest {

	private static final QUser user = QUser.user;
	private static final QProduct product = QProduct.product;
	private static final QueryFactory queryFactory = QueryFactory.create();

	public static void main(String[] args) {
		System.out.println("=== QueryDSL Test Suite ===\n");

		testBasicQueries();
		testComplexPredicates();
		testStringOperations();
		testNumericOperations();
		testBooleanLogic();
		testColumnSelection();
		testInsertQueries();
		testUpdateQueries();
		testDeleteQueries();
		testAggregateFunctions();
		testCaseWhenExpressions();
		testJoinQueries();

		System.out.println("\n=== All tests completed successfully! ===");
	}

	private static void testBasicQueries() {
		System.out.println("Testing basic queries...");

		// Test simple equality
		Query<hatien.querydsl.examples.User> q1 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.firstName.eq("John"));

		String sql1 = q1.toSQL();
		System.out.println("✓ Basic equality: " + sql1);
		assert sql1.contains("user.firstName = 'John'");

		// Test null check
		Query<hatien.querydsl.examples.User> q2 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.email.isNotNull());

		String sql2 = q2.toSQL();
		System.out.println("✓ Null check: " + sql2);
		assert sql2.contains("user.email IS NOT NULL");

		System.out.println();
	}

	private static void testComplexPredicates() {
		System.out.println("Testing complex predicates...");

		// Test multiple conditions
		Query<hatien.querydsl.examples.User> q1 = queryFactory.selectFrom(user.getEntityPath()).where(user.age.goe(18),
				user.city.ne("Unknown"));

		String sql1 = q1.toSQL();
		System.out.println("✓ Multiple conditions: " + sql1);
		assert sql1.contains("user.age >= 18");
		assert sql1.contains("user.city != 'Unknown'");

		System.out.println();
	}

	private static void testStringOperations() {
		System.out.println("Testing string operations...");

		// Test LIKE
		Query<hatien.querydsl.examples.User> q1 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.email.like("%@company.com"));

		String sql1 = q1.toSQL();
		System.out.println("✓ LIKE operation: " + sql1);
		assert sql1.contains("LIKE '%@company.com'");

		// Test CONTAINS (should translate to LIKE with wildcards)
		Query<hatien.querydsl.examples.User> q2 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.firstName.contains("oh"));

		String sql2 = q2.toSQL();
		System.out.println("✓ CONTAINS operation: " + sql2);
		assert sql2.contains("LIKE '%oh%'");

		// Test STARTS WITH
		Query<hatien.querydsl.examples.User> q3 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.lastName.startsWith("Sm"));

		String sql3 = q3.toSQL();
		System.out.println("✓ STARTS WITH operation: " + sql3);
		assert sql3.contains("LIKE 'Sm%'");

		System.out.println();
	}

	private static void testNumericOperations() {
		System.out.println("Testing numeric operations...");

		// Test BETWEEN
		Query<hatien.querydsl.examples.User> q1 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.age.between(25, 65));

		String sql1 = q1.toSQL();
		System.out.println("✓ BETWEEN operation: " + sql1);
		assert sql1.contains("BETWEEN 25 AND 65");

		// Test IN with numbers
		Query<hatien.querydsl.examples.User> q2 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.age.in(25, 30, 35));

		String sql2 = q2.toSQL();
		System.out.println("✓ IN operation: " + sql2);
		assert sql2.contains("IN (25, 30, 35)");

		// Test BigDecimal comparison
		Query<hatien.querydsl.examples.Product> q3 = queryFactory.selectFrom(product.getEntityPath())
				.where(product.price.gt(new BigDecimal("99.99")));

		String sql3 = q3.toSQL();
		System.out.println("✓ BigDecimal comparison: " + sql3);
		assert sql3.contains("product.price > 99.99");

		System.out.println();
	}

	private static void testBooleanLogic() {
		System.out.println("Testing boolean logic...");

		// Test AND
		Query<hatien.querydsl.examples.User> q1 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.firstName.eq("John").and(user.age.gt(25)));

		String sql1 = q1.toSQL();
		System.out.println("✓ AND operation: " + sql1);
		assert sql1.contains("AND");

		// Test OR
		Query<hatien.querydsl.examples.User> q2 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.city.eq("New York").or(user.city.eq("Boston")));

		String sql2 = q2.toSQL();
		System.out.println("✓ OR operation: " + sql2);
		assert sql2.contains("OR");

		// Test NOT
		Query<hatien.querydsl.examples.User> q3 = queryFactory.selectFrom(user.getEntityPath())
				.where(user.firstName.eq("John").not());

		String sql3 = q3.toSQL();
		System.out.println("✓ NOT operation: " + sql3);
		assert sql3.contains("NOT");

		System.out.println();
	}

	private static void testColumnSelection() {
		System.out.println("Testing column selection...");

		// Test single column selection
		Query<String> q1 = queryFactory.select(user.firstName).from(user.getEntityPath()).where(user.age.gt(18));

		String sql1 = q1.toSQL();
		System.out.println("✓ Single column: " + sql1);
		assert sql1.contains("SELECT user.firstName");
		assert sql1.contains("FROM user");
		assert !sql1.contains("SELECT *");

		// Test multiple columns
		Query<Object[]> q2 = queryFactory.select(user.firstName, user.lastName, user.age).from(user.getEntityPath())
				.where(user.city.eq("Test"));

		String sql2 = q2.toSQL();
		System.out.println("✓ Multiple columns: " + sql2);
		assert sql2.contains("SELECT user.firstName, user.lastName, user.age");
		assert sql2.contains("FROM user");

		// Test mixed data types
		Query<Object[]> q3 = queryFactory.select(product.name, product.price).from(product.getEntityPath())
				.where(product.stockQuantity.gt(0));

		String sql3 = q3.toSQL();
		System.out.println("✓ Mixed data types: " + sql3);
		assert sql3.contains("SELECT product.name, product.price");
		assert sql3.contains("product.stockQuantity > 0");

		// Test column selection with complex conditions
		Query<Object[]> q4 = queryFactory.select(user.firstName, user.email).from(user.getEntityPath())
				.where(user.age.between(25, 65), user.email.isNotNull());

		String sql4 = q4.toSQL();
		System.out.println("✓ Complex conditions with columns: " + sql4);
		assert sql4.contains("SELECT user.firstName, user.email");
		assert sql4.contains("BETWEEN 25 AND 65");
		assert sql4.contains("IS NOT NULL");

		System.out.println();
	}

	private static void testInsertQueries() {
		System.out.println("Testing INSERT queries...");

		// Test basic insert with set() method
		InsertQuery<hatien.querydsl.examples.User> q1 = queryFactory.insertInto(user.getEntityPath())
				.set(user.firstName, "John").set(user.lastName, "Doe").set(user.age, 30)
				.set(user.email, "john.doe@example.com");

		String sql1 = q1.toSQL();
		System.out.println("✓ Basic insert with set(): " + sql1);
		assert sql1.contains("INSERT INTO user");
		assert sql1.contains("user.firstName, user.lastName, user.age, user.email");
		assert sql1.contains("VALUES ('John', 'Doe', 30, 'john.doe@example.com')");

		// Test insert with columns and values
		InsertQuery<hatien.querydsl.examples.User> q2 = queryFactory.<hatien.querydsl.examples.User>insert()
				.into(user.getEntityPath()).columns(user.firstName, user.lastName, user.city)
				.values("Jane", "Smith", "Boston");

		String sql2 = q2.toSQL();
		System.out.println("✓ Insert with columns/values: " + sql2);
		assert sql2.contains("INSERT INTO user");
		assert sql2.contains("(user.firstName, user.lastName, user.city)");
		assert sql2.contains("VALUES ('Jane', 'Smith', 'Boston')");

		// Test insert product with BigDecimal
		InsertQuery<hatien.querydsl.examples.Product> q3 = queryFactory.insertInto(product.getEntityPath())
				.set(product.name, "Laptop").set(product.price, new BigDecimal("999.99"))
				.set(product.stockQuantity, 10);

		String sql3 = q3.toSQL();
		System.out.println("✓ Insert product with BigDecimal: " + sql3);
		assert sql3.contains("INSERT INTO product");
		assert sql3.contains("VALUES ('Laptop', 999.99, 10)");

		System.out.println();
	}

	private static void testUpdateQueries() {
		System.out.println("Testing UPDATE queries...");

		// Test basic update with where clause
		UpdateQuery<hatien.querydsl.examples.User> q1 = queryFactory.update(user.getEntityPath())
				.set(user.email, "newemail@example.com").set(user.city, "New York").where(user.firstName.eq("John"));

		String sql1 = q1.toSQL();
		System.out.println("✓ Basic update with where: " + sql1);
		assert sql1.contains("UPDATE user");
		assert sql1.contains("SET user.email = 'newemail@example.com', user.city = 'New York'");
		assert sql1.contains("WHERE (user.firstName = 'John')");

		// Test update without where clause
		UpdateQuery<hatien.querydsl.examples.Product> q2 = queryFactory.<hatien.querydsl.examples.Product>update()
				.table(product.getEntityPath()).set(product.stockQuantity, 0);

		String sql2 = q2.toSQL();
		System.out.println("✓ Update without where: " + sql2);
		assert sql2.contains("UPDATE product");
		assert sql2.contains("SET product.stockQuantity = 0");
		assert !sql2.contains("WHERE");

		// Test update with multiple conditions
		UpdateQuery<hatien.querydsl.examples.User> q3 = queryFactory.update(user.getEntityPath()).set(user.age, 31)
				.where(user.firstName.eq("John"), user.lastName.eq("Doe"));

		String sql3 = q3.toSQL();
		System.out.println("✓ Update with multiple conditions: " + sql3);
		assert sql3.contains("UPDATE user");
		assert sql3.contains("SET user.age = 31");
		assert sql3.contains("WHERE (user.firstName = 'John') AND (user.lastName = 'Doe')");

		// Test update with expression (column = column + value)
		UpdateQuery<hatien.querydsl.examples.Product> q4 = queryFactory.update(product.getEntityPath())
				.set(product.stockQuantity, product.stockQuantity) // In real implementation, this would be an
																	// arithmetic expression
				.where(product.price.gt(new BigDecimal("100")));

		String sql4 = q4.toSQL();
		System.out.println("✓ Update with expression: " + sql4);
		assert sql4.contains("UPDATE product");
		assert sql4.contains("SET product.stockQuantity = product.stockQuantity");
		assert sql4.contains("WHERE (product.price > 100)");

		System.out.println();
	}

	private static void testDeleteQueries() {
		System.out.println("Testing DELETE queries...");

		// Test basic delete with where clause
		DeleteQuery<hatien.querydsl.examples.User> q1 = queryFactory.deleteFrom(user.getEntityPath())
				.where(user.age.lt(18));

		String sql1 = q1.toSQL();
		System.out.println("✓ Basic delete with where: " + sql1);
		assert sql1.contains("DELETE FROM user");
		assert sql1.contains("WHERE (user.age < 18)");

		// Test delete with multiple conditions
		DeleteQuery<hatien.querydsl.examples.User> q2 = queryFactory.<hatien.querydsl.examples.User>delete()
				.from(user.getEntityPath()).where(user.city.eq("Unknown"), user.email.isNull());

		String sql2 = q2.toSQL();
		System.out.println("✓ Delete with multiple conditions: " + sql2);
		assert sql2.contains("DELETE FROM user");
		assert sql2.contains("WHERE (user.city = 'Unknown') AND (user.email IS NULL)");

		// Test delete with complex boolean logic
		DeleteQuery<hatien.querydsl.examples.User> q3 = queryFactory.deleteFrom(user.getEntityPath())
				.where(user.age.lt(18).or(user.age.gt(65)));

		String sql3 = q3.toSQL();
		System.out.println("✓ Delete with complex boolean logic: " + sql3);
		assert sql3.contains("DELETE FROM user");
		assert sql3.contains("WHERE ((user.age < 18) OR (user.age > 65))");

		// Test delete all (no where clause)
		DeleteQuery<hatien.querydsl.examples.Product> q4 = queryFactory.deleteFrom(product.getEntityPath());

		String sql4 = q4.toSQL();
		System.out.println("✓ Delete all (no where): " + sql4);
		assert sql4.contains("DELETE FROM product");
		assert !sql4.contains("WHERE");

		// Test delete with IN condition
		DeleteQuery<hatien.querydsl.examples.User> q5 = queryFactory.deleteFrom(user.getEntityPath())
				.where(user.city.in("Tokyo", "Seoul", "Beijing"));

		String sql5 = q5.toSQL();
		System.out.println("✓ Delete with IN condition: " + sql5);
		assert sql5.contains("DELETE FROM user");
		assert sql5.contains("WHERE (user.city IN ('Tokyo', 'Seoul', 'Beijing'))");

		System.out.println();
	}

	private static void testAggregateFunctions() {
		System.out.println("Testing aggregate functions...");

		// Test COUNT(*)
		Query<Long> q1 = queryFactory.select(queryFactory.count()).from(user.getEntityPath());

		String sql1 = q1.toSQL();
		System.out.println("✓ COUNT(*): " + sql1);
		assert sql1.contains("SELECT COUNT(*)");
		assert sql1.contains("FROM user");

		// Test COUNT(column)
		Query<Long> q2 = queryFactory.select(queryFactory.count(user.email)).from(user.getEntityPath())
				.where(user.age.goe(18));

		String sql2 = q2.toSQL();
		System.out.println("✓ COUNT(column) with WHERE: " + sql2);
		assert sql2.contains("SELECT COUNT(user.email)");
		assert sql2.contains("WHERE (user.age >= 18)");

		// Test SUM
		Query<Integer> q3 = queryFactory.select(queryFactory.sum(user.age)).from(user.getEntityPath())
				.where(user.city.eq("New York"));

		String sql3 = q3.toSQL();
		System.out.println("✓ SUM with WHERE: " + sql3);
		assert sql3.contains("SELECT SUM(user.age)");
		assert sql3.contains("WHERE (user.city = 'New York')");

		// Test AVG
		Query<BigDecimal> q4 = queryFactory.select(queryFactory.avg(product.price)).from(product.getEntityPath())
				.where(product.category.eq("Electronics"));

		String sql4 = q4.toSQL();
		System.out.println("✓ AVG with WHERE: " + sql4);
		assert sql4.contains("SELECT AVG(product.price)");
		assert sql4.contains("WHERE (product.category = 'Electronics')");

		// Test MIN
		Query<BigDecimal> q5 = queryFactory.select(queryFactory.min(product.price)).from(product.getEntityPath());

		String sql5 = q5.toSQL();
		System.out.println("✓ MIN: " + sql5);
		assert sql5.contains("SELECT MIN(product.price)");

		// Test MAX
		Query<Integer> q6 = queryFactory.select(queryFactory.max(user.age)).from(user.getEntityPath())
				.where(user.city.in("Tokyo", "Seoul"));

		String sql6 = q6.toSQL();
		System.out.println("✓ MAX with IN: " + sql6);
		assert sql6.contains("SELECT MAX(user.age)");
		assert sql6.contains("WHERE (user.city IN ('Tokyo', 'Seoul'))");

		// Test multiple aggregates in one query
		Query<Object[]> q7 = queryFactory.select(queryFactory.count(), queryFactory.avg(user.age),
				queryFactory.min(user.age), queryFactory.max(user.age)).from(user.getEntityPath())
				.where(user.email.isNotNull());

		String sql7 = q7.toSQL();
		System.out.println("✓ Multiple aggregates: " + sql7);
		assert sql7.contains("SELECT COUNT(*), AVG(user.age), MIN(user.age), MAX(user.age)");
		assert sql7.contains("WHERE (user.email IS NOT NULL)");

		System.out.println();
	}

	private static void testCaseWhenExpressions() {
		System.out.println("Testing CASE WHEN expressions...");

		// Test simple CASE WHEN with constants
		CaseExpression<String> case1 = queryFactory.caseWhen(String.class).when(user.age.lt(18), "Minor")
				.when(user.age.between(18, 65), "Adult").otherwise("Senior");

		Query<String> q1 = queryFactory.select(case1).from(user.getEntityPath());

		String sql1 = q1.toSQL();
		System.out.println("✓ Simple CASE WHEN: " + sql1);
		assert sql1.contains("SELECT CASE WHEN (user.age < 18) THEN Minor");
		assert sql1.contains("WHEN (user.age BETWEEN 18 AND 65) THEN Adult");
		assert sql1.contains("ELSE Senior END");

		// Test CASE WHEN with ExpressionUtils
		CaseExpression<String> case2 = ExpressionUtils.caseWhen(String.class).when(user.firstName.isNull(), "Unknown")
				.when(user.firstName.isEmpty(), "Empty").otherwise("Known");

		Query<String> q2 = queryFactory.select(case2).from(user.getEntityPath()).where(user.age.goe(18));

		String sql2 = q2.toSQL();
		System.out.println("✓ CASE WHEN with null/empty checks: " + sql2);
		assert sql2.contains("SELECT CASE WHEN (user.firstName IS NULL) THEN Unknown");
		assert sql2.contains("WHEN (user.firstName = '' OR user.firstName IS NULL) THEN Empty");
		assert sql2.contains("ELSE Known END");

		// Test CASE WHEN for numeric values
		CaseExpression<Integer> case3 = ExpressionUtils.caseWhenInt().when(product.stockQuantity.eq(0), -1)
				.when(product.stockQuantity.between(1, 10), 1).when(product.stockQuantity.gt(10), 2).end(); // No ELSE
																											// clause

		Query<Integer> q3 = queryFactory.select(case3).from(product.getEntityPath())
				.where(product.price.gt(new BigDecimal("0")));

		String sql3 = q3.toSQL();
		System.out.println("✓ CASE WHEN with numeric values (no ELSE): " + sql3);
		assert sql3.contains("SELECT CASE WHEN (product.stockQuantity = 0) THEN -1");
		assert sql3.contains("WHEN (product.stockQuantity BETWEEN 1 AND 10) THEN 1");
		assert sql3.contains("WHEN (product.stockQuantity > 10) THEN 2 END");
		assert !sql3.contains("ELSE");

		// Test CASE WHEN in SELECT with complex expression
		CaseExpression<String> case4 = ExpressionUtils.caseWhen().when(user.city.eq("New York"), "NY")
				.when(user.city.eq("Los Angeles"), "LA").otherwise("Other");

		Query<String> q4 = queryFactory.select(case4).from(user.getEntityPath()).where(user.age.goe(18));

		String sql4 = q4.toSQL();
		System.out.println("✓ CASE WHEN in complex query: " + sql4);
		assert sql4.contains("SELECT CASE WHEN (user.city = 'New York') THEN NY");
		assert sql4.contains("WHEN (user.city = 'Los Angeles') THEN LA");
		assert sql4.contains("ELSE Other END");
		assert sql4.contains("WHERE (user.age >= 18)");

		// Test CASE WHEN with boolean expressions
		CaseExpression<Boolean> case5 = ExpressionUtils.caseWhenBoolean()
				.when(user.age.lt(18).or(user.age.gt(65)), true).otherwise(false);

		Query<Boolean> q5 = queryFactory.select(case5).from(user.getEntityPath()).where(user.email.isNotNull());

		String sql5 = q5.toSQL();
		System.out.println("✓ CASE WHEN with boolean result: " + sql5);
		assert sql5.contains("SELECT CASE WHEN ((user.age < 18) OR (user.age > 65)) THEN true");
		assert sql5.contains("ELSE false END");

		// Test CASE WHEN with simple aggregate condition
		CaseExpression<String> case6 = ExpressionUtils.caseWhen().when(user.age.gt(65), "Senior")
				.when(user.age.between(18, 65), "Adult").otherwise("Minor");

		Query<String> q6 = queryFactory.select(case6).from(user.getEntityPath()).where(user.email.isNotNull());

		String sql6 = q6.toSQL();
		System.out.println("✓ CASE WHEN with age ranges: " + sql6);
		assert sql6.contains("SELECT CASE WHEN (user.age > 65) THEN Senior");
		assert sql6.contains("WHEN (user.age BETWEEN 18 AND 65) THEN Adult");
		assert sql6.contains("ELSE Minor END");
		assert sql6.contains("WHERE (user.email IS NOT NULL)");

		System.out.println();
	}

	private static void testJoinQueries() {
		System.out.println("Testing JOIN queries...");

		// Test INNER JOIN
		Query<hatien.querydsl.examples.User> q1 = queryFactory.selectFrom(user.getEntityPath())
				.innerJoin(product.getEntityPath(), user.id.eq(1L));

		String sql1 = q1.toSQL();
		System.out.println("✓ INNER JOIN: " + sql1);
		assert sql1.contains("SELECT * FROM user");
		assert sql1.contains("INNER JOIN product ON (user.id = 1)");

		// Test LEFT JOIN with alias
		Query<hatien.querydsl.examples.User> q2 = queryFactory.selectFrom(user.getEntityPath())
				.leftJoin(product.getEntityPath(), "p", product.stockQuantity.gt(0)).where(user.age.goe(18));

		String sql2 = q2.toSQL();
		System.out.println("✓ LEFT JOIN with alias: " + sql2);
		assert sql2.contains("SELECT * FROM user");
		assert sql2.contains("LEFT JOIN product AS p ON (product.stockQuantity > 0)");
		assert sql2.contains("WHERE (user.age >= 18)");

		// Test RIGHT JOIN
		Query<hatien.querydsl.examples.User> q3 = queryFactory.selectFrom(user.getEntityPath())
				.rightJoin(product.getEntityPath(), product.price.gt(new java.math.BigDecimal("100")))
				.where(user.firstName.eq("John"));

		String sql3 = q3.toSQL();
		System.out.println("✓ RIGHT JOIN with WHERE: " + sql3);
		assert sql3.contains("SELECT * FROM user");
		assert sql3.contains("RIGHT JOIN product ON (product.price > 100)");
		assert sql3.contains("WHERE (user.firstName = 'John')");

		// Test FULL OUTER JOIN
		Query<hatien.querydsl.examples.User> q4 = queryFactory.selectFrom(user.getEntityPath())
				.fullOuterJoin(product.getEntityPath(), product.name.isNotNull());

		String sql4 = q4.toSQL();
		System.out.println("✓ FULL OUTER JOIN: " + sql4);
		assert sql4.contains("SELECT * FROM user");
		assert sql4.contains("FULL OUTER JOIN product ON (product.name IS NOT NULL)");

		// Test CROSS JOIN
		Query<hatien.querydsl.examples.User> q5 = queryFactory.selectFrom(user.getEntityPath())
				.crossJoin(product.getEntityPath());

		String sql5 = q5.toSQL();
		System.out.println("✓ CROSS JOIN: " + sql5);
		assert sql5.contains("SELECT * FROM user");
		assert sql5.contains("CROSS JOIN product");
		assert !sql5.contains("ON"); // CROSS JOIN has no ON condition

		// Test multiple JOINs
		Query<hatien.querydsl.examples.User> q6 = queryFactory.selectFrom(user.getEntityPath())
				.innerJoin(product.getEntityPath(), user.age.gt(20))
				.leftJoin(product.getEntityPath(), "p2", product.stockQuantity.loe(100))
				.where(user.city.eq("New York"));

		String sql6 = q6.toSQL();
		System.out.println("✓ Multiple JOINs: " + sql6);
		assert sql6.contains("SELECT * FROM user");
		assert sql6.contains("INNER JOIN product ON (user.age > 20)");
		assert sql6.contains("LEFT JOIN product AS p2 ON (product.stockQuantity <= 100)");
		assert sql6.contains("WHERE (user.city = 'New York')");

		// Test JOIN with column selection
		Query<Object[]> q7 = queryFactory.select(user.firstName, user.lastName, product.name, product.price)
				.from(user.getEntityPath()).innerJoin(product.getEntityPath(), user.city.eq("Boston"))
				.where(product.price.between(new java.math.BigDecimal("10"), new java.math.BigDecimal("100")));

		String sql7 = q7.toSQL();
		System.out.println("✓ JOIN with column selection: " + sql7);
		assert sql7.contains("SELECT user.firstName, user.lastName, product.name, product.price");
		assert sql7.contains("FROM user");
		assert sql7.contains("INNER JOIN product ON (user.city = 'Boston')");
		assert sql7.contains("WHERE (product.price BETWEEN 10 AND 100)");

		// Test FROM with alias and JOIN
		Query<hatien.querydsl.examples.User> q8 = queryFactory.selectFrom(user.getEntityPath())
				.from(user.getEntityPath(), "u")
				.innerJoin(product.getEntityPath(), "p", product.category.eq("Electronics"));

		String sql8 = q8.toSQL();
		System.out.println("✓ FROM with alias and JOIN: " + sql8);
		assert sql8.contains("FROM user AS u");
		assert sql8.contains("INNER JOIN product AS p ON (product.category = 'Electronics')");

		// Test JOIN with complex conditions
		Query<hatien.querydsl.examples.User> q9 = queryFactory.selectFrom(user.getEntityPath())
				.innerJoin(product.getEntityPath(),
						user.age.goe(25).and(product.price.gt(new java.math.BigDecimal("50"))))
				.where(user.city.isNotNull());

		String sql9 = q9.toSQL();
		System.out.println("✓ JOIN with complex conditions: " + sql9);
		assert sql9.contains("INNER JOIN product ON ((user.age >= 25) AND (product.price > 50))");
		assert sql9.contains("WHERE (user.city IS NOT NULL)");

		// Test JOIN with aggregates
		Query<Object[]> q10 = queryFactory.select(user.firstName, queryFactory.count(), queryFactory.sum(product.price))
				.from(user.getEntityPath()).leftJoin(product.getEntityPath(), product.stockQuantity.gt(0))
				.where(user.email.isNotNull()).groupBy(user.firstName);

		String sql10 = q10.toSQL();
		System.out.println("✓ JOIN with aggregates and GROUP BY: " + sql10);
		assert sql10.contains("SELECT user.firstName, COUNT(*), SUM(product.price)");
		assert sql10.contains("LEFT JOIN product ON (product.stockQuantity > 0)");
		assert sql10.contains("WHERE (user.email IS NOT NULL)");
		assert sql10.contains("GROUP BY user.firstName");

		System.out.println();
	}
}