package hatien.querydsl.examples;

import hatien.querydsl.core.query.QueryFactory;
import hatien.querydsl.core.query.Query;
import hatien.querydsl.core.query.InsertQuery;
import hatien.querydsl.core.query.UpdateQuery;
import hatien.querydsl.core.query.DeleteQuery;
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
        
        // Example 8: INSERT operations
        insertOperationsExample();
        
        // Example 9: UPDATE operations
        updateOperationsExample();
        
        // Example 10: DELETE operations
        deleteOperationsExample();
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
    
    private static void insertOperationsExample() {
        System.out.println("8. INSERT operations:");
        
        // Insert a new user with set() method
        InsertQuery<User> insert1 = queryFactory
                .insertInto(user.getEntityPath())
                .set(user.firstName, "Alice")
                .set(user.lastName, "Johnson")
                .set(user.age, 28)
                .set(user.email, "alice.johnson@example.com")
                .set(user.city, "San Francisco");
        
        System.out.println("   Insert user with set(): " + insert1.toSQL());
        
        // Insert with columns and values
        InsertQuery<User> insert2 = queryFactory
                .<User>insert()
                .into(user.getEntityPath())
                .columns(user.firstName, user.lastName, user.email)
                .values("Bob", "Smith", "bob.smith@example.com");
        
        System.out.println("   Insert with columns/values: " + insert2.toSQL());
        
        // Insert a new product
        InsertQuery<Product> insert3 = queryFactory
                .insertInto(product.getEntityPath())
                .set(product.name, "Gaming Laptop")
                .set(product.description, "High-performance gaming laptop")
                .set(product.price, new BigDecimal("1299.99"))
                .set(product.category, "Electronics")
                .set(product.stockQuantity, 5);
        
        System.out.println("   Insert product: " + insert3.toSQL());
        
        // Insert minimal data
        InsertQuery<Product> insert4 = queryFactory
                .insertInto(product.getEntityPath())
                .set(product.name, "Basic Mouse")
                .set(product.price, new BigDecimal("19.99"));
        
        System.out.println("   Insert minimal product: " + insert4.toSQL());
        System.out.println();
    }
    
    private static void updateOperationsExample() {
        System.out.println("9. UPDATE operations:");
        
        // Update user email by first name
        UpdateQuery<User> update1 = queryFactory
                .update(user.getEntityPath())
                .set(user.email, "john.doe.updated@example.com")
                .where(user.firstName.eq("John"));
        
        System.out.println("   Update user email: " + update1.toSQL());
        
        // Update multiple fields with multiple conditions
        UpdateQuery<User> update2 = queryFactory
                .update(user.getEntityPath())
                .set(user.city, "Boston")
                .set(user.age, 31)
                .where(user.firstName.eq("John"), user.lastName.eq("Doe"));
        
        System.out.println("   Update multiple fields: " + update2.toSQL());
        
        // Update product price for a category
        UpdateQuery<Product> update3 = queryFactory
                .update(product.getEntityPath())
                .set(product.price, new BigDecimal("79.99"))
                .where(product.category.eq("Electronics")
                       .and(product.name.contains("Mouse")));
        
        System.out.println("   Update product price: " + update3.toSQL());
        
        // Bulk update - reduce stock for expensive items
        UpdateQuery<Product> update4 = queryFactory
                .update(product.getEntityPath())
                .set(product.stockQuantity, 0)
                .where(product.price.gt(new BigDecimal("1000")));
        
        System.out.println("   Bulk stock update: " + update4.toSQL());
        
        // Update with expression (referencing current column value)
        UpdateQuery<Product> update5 = queryFactory
                .update(product.getEntityPath())
                .set(product.stockQuantity, product.stockQuantity)  // In practice, this would be arithmetic
                .where(product.category.eq("Electronics"));
        
        System.out.println("   Update with expression: " + update5.toSQL());
        System.out.println();
    }
    
    private static void deleteOperationsExample() {
        System.out.println("10. DELETE operations:");
        
        // Delete users by age
        DeleteQuery<User> delete1 = queryFactory
                .deleteFrom(user.getEntityPath())
                .where(user.age.lt(18));
        
        System.out.println("   Delete minors: " + delete1.toSQL());
        
        // Delete users from specific city
        DeleteQuery<User> delete2 = queryFactory
                .<User>delete()
                .from(user.getEntityPath())
                .where(user.city.eq("Unknown"));
        
        System.out.println("   Delete users from Unknown city: " + delete2.toSQL());
        
        // Delete products with complex conditions
        DeleteQuery<Product> delete3 = queryFactory
                .deleteFrom(product.getEntityPath())
                .where(product.stockQuantity.eq(0)
                       .and(product.price.lt(new BigDecimal("10"))));
        
        System.out.println("   Delete cheap out-of-stock products: " + delete3.toSQL());
        
        // Delete using OR condition
        DeleteQuery<User> delete4 = queryFactory
                .deleteFrom(user.getEntityPath())
                .where(user.age.lt(18).or(user.age.gt(65)));
        
        System.out.println("   Delete by age range (OR): " + delete4.toSQL());
        
        // Delete using IN condition
        DeleteQuery<User> delete5 = queryFactory
                .deleteFrom(user.getEntityPath())
                .where(user.city.in("TestCity1", "TestCity2", "InvalidCity"));
        
        System.out.println("   Delete users from test cities: " + delete5.toSQL());
        
        // Delete products by category
        DeleteQuery<Product> delete6 = queryFactory
                .deleteFrom(product.getEntityPath())
                .where(product.category.eq("Discontinued"));
        
        System.out.println("   Delete discontinued products: " + delete6.toSQL());
        
        // Conditional delete with string operations
        DeleteQuery<User> delete7 = queryFactory
                .deleteFrom(user.getEntityPath())
                .where(user.email.like("%@tempmail.%")
                       .or(user.firstName.isEmpty()));
        
        System.out.println("   Delete temp/empty users: " + delete7.toSQL());
        System.out.println();
    }
}