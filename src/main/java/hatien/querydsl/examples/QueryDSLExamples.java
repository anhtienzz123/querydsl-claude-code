package hatien.querydsl.examples;

import hatien.querydsl.core.query.QueryFactory;
import hatien.querydsl.core.query.Query;
import hatien.querydsl.core.predicate.BooleanExpression;
import java.math.BigDecimal;

public class QueryDSLExamples {
    
    private static final QUser user = QUser.user;
    private static final QProduct product = QProduct.product;
    private static final QueryFactory queryFactory = QueryFactory.create();
    
    public static void main(String[] args) {
        System.out.println("=== QueryDSL Examples ===\n");
        
        // Example 1: Simple SELECT with WHERE
        simpleSelectExample();
        
        // Example 2: Complex WHERE conditions
        complexWhereExample();
        
        // Example 3: String operations
        stringOperationsExample();
        
        // Example 4: Numeric comparisons
        numericComparisonsExample();
        
        // Example 5: Compound conditions
        compoundConditionsExample();
        
        // Example 6: Product queries
        productQueriesExample();
        
        // Example 7: Column selection queries
        columnSelectionExample();
    }
    
    private static void simpleSelectExample() {
        System.out.println("1. Simple SELECT with WHERE:");
        
        Query<User> query1 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.firstName.eq("John"));
        
        System.out.println("   " + query1.toSQL());
        System.out.println();
    }
    
    private static void complexWhereExample() {
        System.out.println("2. Complex WHERE conditions:");
        
        Query<User> query2 = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.age.goe(18), 
                       user.city.eq("New York"));
        
        System.out.println("   " + query2.toSQL());
        System.out.println();
    }
    
    private static void stringOperationsExample() {
        System.out.println("3. String operations:");
        
        // LIKE operation
        Query<User> query3a = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.email.like("%@gmail.com"));
        
        System.out.println("   LIKE: " + query3a.toSQL());
        
        // CONTAINS operation
        Query<User> query3b = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.firstName.contains("oh"));
        
        System.out.println("   CONTAINS: " + query3b.toSQL());
        
        // STARTS WITH operation
        Query<User> query3c = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.lastName.startsWith("Sm"));
        
        System.out.println("   STARTS WITH: " + query3c.toSQL());
        System.out.println();
    }
    
    private static void numericComparisonsExample() {
        System.out.println("4. Numeric comparisons:");
        
        // BETWEEN operation
        Query<User> query4a = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.age.between(25, 35));
        
        System.out.println("   BETWEEN: " + query4a.toSQL());
        
        // IN operation
        Query<User> query4b = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.age.in(25, 30, 35, 40));
        
        System.out.println("   IN: " + query4b.toSQL());
        System.out.println();
    }
    
    private static void compoundConditionsExample() {
        System.out.println("5. Compound conditions (AND/OR):");
        
        BooleanExpression condition1 = user.firstName.eq("John");
        BooleanExpression condition2 = user.age.gt(25);
        BooleanExpression condition3 = user.city.eq("Boston");
        
        // AND condition
        Query<User> query5a = queryFactory
                .selectFrom(user.getEntityPath())
                .where(condition1.and(condition2));
        
        System.out.println("   AND: " + query5a.toSQL());
        
        // OR condition
        Query<User> query5b = queryFactory
                .selectFrom(user.getEntityPath())
                .where(condition1.or(condition3));
        
        System.out.println("   OR: " + query5b.toSQL());
        
        // Complex condition with NOT
        Query<User> query5c = queryFactory
                .selectFrom(user.getEntityPath())
                .where(condition1.and(condition2.not()));
        
        System.out.println("   AND with NOT: " + query5c.toSQL());
        System.out.println();
    }
    
    private static void productQueriesExample() {
        System.out.println("6. Product queries:");
        
        // Find expensive products
        Query<Product> query6a = queryFactory
                .selectFrom(product.getEntityPath())
                .where(product.price.gt(new BigDecimal("100.00")));
        
        System.out.println("   Expensive products: " + query6a.toSQL());
        
        // Find products by category with stock
        Query<Product> query6b = queryFactory
                .selectFrom(product.getEntityPath())
                .where(product.category.eq("Electronics")
                       .and(product.stockQuantity.gt(0)));
        
        System.out.println("   Electronics in stock: " + query6b.toSQL());
        
        // Find products with name containing search term
        Query<Product> query6c = queryFactory
                .selectFrom(product.getEntityPath())
                .where(product.name.contains("Laptop")
                       .or(product.description.contains("laptop")));
        
        System.out.println("   Laptop search: " + query6c.toSQL());
        System.out.println();
    }
    
    private static void columnSelectionExample() {
        System.out.println("7. Column selection queries:");
        
        // Select single column
        Query<String> query7a = queryFactory
                .select(user.firstName)
                .from(user.getEntityPath())
                .where(user.age.gt(18));
        
        System.out.println("   Single column: " + query7a.toSQL());
        
        // Select multiple columns
        Query<Object[]> query7b = queryFactory
                .select(user.firstName, user.lastName, user.age)
                .from(user.getEntityPath())
                .where(user.city.eq("New York"));
        
        System.out.println("   Multiple columns: " + query7b.toSQL());
        
        // Select specific columns with complex conditions
        Query<Object[]> query7c = queryFactory
                .select(user.firstName, user.email)
                .from(user.getEntityPath())
                .where(user.age.between(25, 65), user.email.isNotNull());
        
        System.out.println("   Complex selection: " + query7c.toSQL());
        
        // Product column selection
        Query<Object[]> query7d = queryFactory
                .select(product.name, product.price, product.category)
                .from(product.getEntityPath())
                .where(product.price.gt(new BigDecimal("50.00")));
        
        System.out.println("   Product columns: " + query7d.toSQL());
        
        // Select with mixed data types
        Query<Object[]> query7e = queryFactory
                .select(user.firstName, user.age, user.city)
                .from(user.getEntityPath())
                .where(user.firstName.startsWith("J"));
        
        System.out.println("   Mixed data types: " + query7e.toSQL());
        System.out.println();
    }
}