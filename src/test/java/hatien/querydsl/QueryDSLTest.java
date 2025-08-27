package hatien.querydsl;

import hatien.querydsl.core.query.QueryFactory;
import hatien.querydsl.core.query.Query;
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
}