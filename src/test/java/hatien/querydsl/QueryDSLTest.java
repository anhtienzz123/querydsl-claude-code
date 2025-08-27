package hatien.querydsl;

import hatien.querydsl.core.query.QueryFactory;
import hatien.querydsl.core.query.Query;
import hatien.querydsl.core.query.InsertQuery;
import hatien.querydsl.core.query.UpdateQuery;
import hatien.querydsl.core.query.DeleteQuery;
import hatien.querydsl.examples.QUser;
import hatien.querydsl.examples.QProduct;
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
        
        System.out.println("\n=== All tests completed successfully! ===");
    }
    
    private static void testBasicQueries() {
        System.out.println("Testing basic queries...");
        
        // Test simple equality
        Query<hatien.querydsl.examples.User> q1 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.firstName.eq("John"));
        
        String sql1 = q1.toSQL();
        System.out.println("✓ Basic equality: " + sql1);
        assert sql1.contains("user.firstName = 'John'");
        
        // Test null check
        Query<hatien.querydsl.examples.User> q2 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.email.isNotNull());
        
        String sql2 = q2.toSQL();
        System.out.println("✓ Null check: " + sql2);
        assert sql2.contains("user.email IS NOT NULL");
        
        System.out.println();
    }
    
    private static void testComplexPredicates() {
        System.out.println("Testing complex predicates...");
        
        // Test multiple conditions
        Query<hatien.querydsl.examples.User> q1 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.age.goe(18), user.city.ne("Unknown"));
        
        String sql1 = q1.toSQL();
        System.out.println("✓ Multiple conditions: " + sql1);
        assert sql1.contains("user.age >= 18");
        assert sql1.contains("user.city != 'Unknown'");
        
        System.out.println();
    }
    
    private static void testStringOperations() {
        System.out.println("Testing string operations...");
        
        // Test LIKE
        Query<hatien.querydsl.examples.User> q1 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.email.like("%@company.com"));
        
        String sql1 = q1.toSQL();
        System.out.println("✓ LIKE operation: " + sql1);
        assert sql1.contains("LIKE '%@company.com'");
        
        // Test CONTAINS (should translate to LIKE with wildcards)
        Query<hatien.querydsl.examples.User> q2 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.firstName.contains("oh"));
        
        String sql2 = q2.toSQL();
        System.out.println("✓ CONTAINS operation: " + sql2);
        assert sql2.contains("LIKE '%oh%'");
        
        // Test STARTS WITH
        Query<hatien.querydsl.examples.User> q3 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.lastName.startsWith("Sm"));
        
        String sql3 = q3.toSQL();
        System.out.println("✓ STARTS WITH operation: " + sql3);
        assert sql3.contains("LIKE 'Sm%'");
        
        System.out.println();
    }
    
    private static void testNumericOperations() {
        System.out.println("Testing numeric operations...");
        
        // Test BETWEEN
        Query<hatien.querydsl.examples.User> q1 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.age.between(25, 65));
        
        String sql1 = q1.toSQL();
        System.out.println("✓ BETWEEN operation: " + sql1);
        assert sql1.contains("BETWEEN 25 AND 65");
        
        // Test IN with numbers
        Query<hatien.querydsl.examples.User> q2 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.age.in(25, 30, 35));
        
        String sql2 = q2.toSQL();
        System.out.println("✓ IN operation: " + sql2);
        assert sql2.contains("IN (25, 30, 35)");
        
        // Test BigDecimal comparison
        Query<hatien.querydsl.examples.Product> q3 = queryFactory
                .selectFrom(product.getEntityPath())
                .where(product.price.gt(new BigDecimal("99.99")));
        
        String sql3 = q3.toSQL();
        System.out.println("✓ BigDecimal comparison: " + sql3);
        assert sql3.contains("product.price > 99.99");
        
        System.out.println();
    }
    
    private static void testBooleanLogic() {
        System.out.println("Testing boolean logic...");
        
        // Test AND
        Query<hatien.querydsl.examples.User> q1 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.firstName.eq("John").and(user.age.gt(25)));
        
        String sql1 = q1.toSQL();
        System.out.println("✓ AND operation: " + sql1);
        assert sql1.contains("AND");
        
        // Test OR
        Query<hatien.querydsl.examples.User> q2 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.city.eq("New York").or(user.city.eq("Boston")));
        
        String sql2 = q2.toSQL();
        System.out.println("✓ OR operation: " + sql2);
        assert sql2.contains("OR");
        
        // Test NOT
        Query<hatien.querydsl.examples.User> q3 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.firstName.eq("John").not());
        
        String sql3 = q3.toSQL();
        System.out.println("✓ NOT operation: " + sql3);
        assert sql3.contains("NOT");
        
        System.out.println();
    }
    
    private static void testColumnSelection() {
        System.out.println("Testing column selection...");
        
        // Test single column selection
        Query<String> q1 = queryFactory
                .select(user.firstName)
                .from(user.getEntityPath())
                .where(user.age.gt(18));
        
        String sql1 = q1.toSQL();
        System.out.println("✓ Single column: " + sql1);
        assert sql1.contains("SELECT user.firstName");
        assert sql1.contains("FROM user");
        assert !sql1.contains("SELECT *");
        
        // Test multiple columns
        Query<Object[]> q2 = queryFactory
                .select(user.firstName, user.lastName, user.age)
                .from(user.getEntityPath())
                .where(user.city.eq("Test"));
        
        String sql2 = q2.toSQL();
        System.out.println("✓ Multiple columns: " + sql2);
        assert sql2.contains("SELECT user.firstName, user.lastName, user.age");
        assert sql2.contains("FROM user");
        
        // Test mixed data types
        Query<Object[]> q3 = queryFactory
                .select(product.name, product.price)
                .from(product.getEntityPath())
                .where(product.stockQuantity.gt(0));
        
        String sql3 = q3.toSQL();
        System.out.println("✓ Mixed data types: " + sql3);
        assert sql3.contains("SELECT product.name, product.price");
        assert sql3.contains("product.stockQuantity > 0");
        
        // Test column selection with complex conditions
        Query<Object[]> q4 = queryFactory
                .select(user.firstName, user.email)
                .from(user.getEntityPath())
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
        InsertQuery<hatien.querydsl.examples.User> q1 = queryFactory
                .insertInto(user.getEntityPath())
                .set(user.firstName, "John")
                .set(user.lastName, "Doe")
                .set(user.age, 30)
                .set(user.email, "john.doe@example.com");
        
        String sql1 = q1.toSQL();
        System.out.println("✓ Basic insert with set(): " + sql1);
        assert sql1.contains("INSERT INTO user");
        assert sql1.contains("user.firstName, user.lastName, user.age, user.email");
        assert sql1.contains("VALUES ('John', 'Doe', 30, 'john.doe@example.com')");
        
        // Test insert with columns and values
        InsertQuery<hatien.querydsl.examples.User> q2 = queryFactory
                .<hatien.querydsl.examples.User>insert()
                .into(user.getEntityPath())
                .columns(user.firstName, user.lastName, user.city)
                .values("Jane", "Smith", "Boston");
        
        String sql2 = q2.toSQL();
        System.out.println("✓ Insert with columns/values: " + sql2);
        assert sql2.contains("INSERT INTO user");
        assert sql2.contains("(user.firstName, user.lastName, user.city)");
        assert sql2.contains("VALUES ('Jane', 'Smith', 'Boston')");
        
        // Test insert product with BigDecimal
        InsertQuery<hatien.querydsl.examples.Product> q3 = queryFactory
                .insertInto(product.getEntityPath())
                .set(product.name, "Laptop")
                .set(product.price, new BigDecimal("999.99"))
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
        UpdateQuery<hatien.querydsl.examples.User> q1 = queryFactory
                .update(user.getEntityPath())
                .set(user.email, "newemail@example.com")
                .set(user.city, "New York")
                .where(user.firstName.eq("John"));
        
        String sql1 = q1.toSQL();
        System.out.println("✓ Basic update with where: " + sql1);
        assert sql1.contains("UPDATE user");
        assert sql1.contains("SET user.email = 'newemail@example.com', user.city = 'New York'");
        assert sql1.contains("WHERE (user.firstName = 'John')");
        
        // Test update without where clause
        UpdateQuery<hatien.querydsl.examples.Product> q2 = queryFactory
                .<hatien.querydsl.examples.Product>update()
                .table(product.getEntityPath())
                .set(product.stockQuantity, 0);
        
        String sql2 = q2.toSQL();
        System.out.println("✓ Update without where: " + sql2);
        assert sql2.contains("UPDATE product");
        assert sql2.contains("SET product.stockQuantity = 0");
        assert !sql2.contains("WHERE");
        
        // Test update with multiple conditions
        UpdateQuery<hatien.querydsl.examples.User> q3 = queryFactory
                .update(user.getEntityPath())
                .set(user.age, 31)
                .where(user.firstName.eq("John"), user.lastName.eq("Doe"));
        
        String sql3 = q3.toSQL();
        System.out.println("✓ Update with multiple conditions: " + sql3);
        assert sql3.contains("UPDATE user");
        assert sql3.contains("SET user.age = 31");
        assert sql3.contains("WHERE (user.firstName = 'John') AND (user.lastName = 'Doe')");
        
        // Test update with expression (column = column + value)
        UpdateQuery<hatien.querydsl.examples.Product> q4 = queryFactory
                .update(product.getEntityPath())
                .set(product.stockQuantity, product.stockQuantity) // In real implementation, this would be an arithmetic expression
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
        DeleteQuery<hatien.querydsl.examples.User> q1 = queryFactory
                .deleteFrom(user.getEntityPath())
                .where(user.age.lt(18));
        
        String sql1 = q1.toSQL();
        System.out.println("✓ Basic delete with where: " + sql1);
        assert sql1.contains("DELETE FROM user");
        assert sql1.contains("WHERE (user.age < 18)");
        
        // Test delete with multiple conditions
        DeleteQuery<hatien.querydsl.examples.User> q2 = queryFactory
                .<hatien.querydsl.examples.User>delete()
                .from(user.getEntityPath())
                .where(user.city.eq("Unknown"), user.email.isNull());
        
        String sql2 = q2.toSQL();
        System.out.println("✓ Delete with multiple conditions: " + sql2);
        assert sql2.contains("DELETE FROM user");
        assert sql2.contains("WHERE (user.city = 'Unknown') AND (user.email IS NULL)");
        
        // Test delete with complex boolean logic
        DeleteQuery<hatien.querydsl.examples.User> q3 = queryFactory
                .deleteFrom(user.getEntityPath())
                .where(user.age.lt(18).or(user.age.gt(65)));
        
        String sql3 = q3.toSQL();
        System.out.println("✓ Delete with complex boolean logic: " + sql3);
        assert sql3.contains("DELETE FROM user");
        assert sql3.contains("WHERE ((user.age < 18) OR (user.age > 65))");
        
        // Test delete all (no where clause)
        DeleteQuery<hatien.querydsl.examples.Product> q4 = queryFactory
                .deleteFrom(product.getEntityPath());
        
        String sql4 = q4.toSQL();
        System.out.println("✓ Delete all (no where): " + sql4);
        assert sql4.contains("DELETE FROM product");
        assert !sql4.contains("WHERE");
        
        // Test delete with IN condition
        DeleteQuery<hatien.querydsl.examples.User> q5 = queryFactory
                .deleteFrom(user.getEntityPath())
                .where(user.city.in("Tokyo", "Seoul", "Beijing"));
        
        String sql5 = q5.toSQL();
        System.out.println("✓ Delete with IN condition: " + sql5);
        assert sql5.contains("DELETE FROM user");
        assert sql5.contains("WHERE (user.city IN ('Tokyo', 'Seoul', 'Beijing'))");
        
        System.out.println();
    }
}