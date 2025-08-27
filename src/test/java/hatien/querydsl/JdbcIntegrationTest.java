package hatien.querydsl;

import hatien.querydsl.core.database.DatabaseConfig;
import hatien.querydsl.core.database.Transaction;
import hatien.querydsl.core.query.ExecutableQueryFactory;
import hatien.querydsl.examples.QUser;
import hatien.querydsl.examples.QProduct;
import hatien.querydsl.examples.User;
import hatien.querydsl.examples.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class JdbcIntegrationTest {
    
    private static final QUser user = QUser.user;
    private static final QProduct product = QProduct.product;
    
    public static void main(String[] args) throws SQLException {
        System.out.println("=== QueryDSL JDBC Integration Test ===\n");
        
        // Create H2 in-memory database for testing
        ExecutableQueryFactory queryFactory = ExecutableQueryFactory.createH2InMemory("testdb");
        
        try {
            // Setup database schema
            setupDatabase(queryFactory);
            
            // Run tests
            testInsertOperations(queryFactory);
            testSelectOperations(queryFactory);
            testUpdateOperations(queryFactory);
            testDeleteOperations(queryFactory);
            testTransactions(queryFactory);
            
            System.out.println("\n=== All JDBC integration tests completed successfully! ===");
        } finally {
            queryFactory.close();
        }
    }
    
    private static void setupDatabase(ExecutableQueryFactory queryFactory) throws SQLException {
        System.out.println("Setting up database schema...");
        
        // Get a raw connection to create tables
        try (Connection conn = queryFactory.getConnectionManager().getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                // Create USER table
                stmt.executeUpdate("""
                    CREATE TABLE user (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        first_name VARCHAR(255),
                        last_name VARCHAR(255),
                        email VARCHAR(255),
                        age INTEGER,
                        city VARCHAR(255)
                    )
                """);
                
                // Create PRODUCT table
                stmt.executeUpdate("""
                    CREATE TABLE product (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        price DECIMAL(19,2),
                        category VARCHAR(255),
                        stock_quantity INTEGER DEFAULT 0
                    )
                """);
                
                System.out.println("✓ Database schema created");
            }
        }
        System.out.println();
    }
    
    private static void testInsertOperations(ExecutableQueryFactory queryFactory) {
        System.out.println("Testing INSERT operations...");
        
        // Insert users
        long insertCount1 = queryFactory
                .insertInto(user.getEntityPath())
                .set(user.firstName, "John")
                .set(user.lastName, "Doe")
                .set(user.email, "john.doe@example.com")
                .set(user.age, 30)
                .set(user.city, "New York")
                .execute();
        
        System.out.println("✓ Inserted " + insertCount1 + " user record");
        
        long insertCount2 = queryFactory
                .insertInto(user.getEntityPath())
                .set(user.firstName, "Jane")
                .set(user.lastName, "Smith")
                .set(user.email, "jane.smith@example.com")
                .set(user.age, 25)
                .set(user.city, "Boston")
                .execute();
        
        System.out.println("✓ Inserted " + insertCount2 + " user record");
        
        // Insert products
        long insertCount3 = queryFactory
                .insertInto(product.getEntityPath())
                .set(product.name, "Gaming Laptop")
                .set(product.description, "High-performance gaming laptop")
                .set(product.price, new BigDecimal("1299.99"))
                .set(product.category, "Electronics")
                .set(product.stockQuantity, 5)
                .execute();
        
        System.out.println("✓ Inserted " + insertCount3 + " product record");
        
        System.out.println();
    }
    
    private static void testSelectOperations(ExecutableQueryFactory queryFactory) {
        System.out.println("Testing SELECT operations...");
        
        // Select all users
        List<User> allUsers = queryFactory
                .selectFrom(user.getEntityPath())
                .fetch();
        
        System.out.println("✓ Found " + allUsers.size() + " users");
        for (User u : allUsers) {
            System.out.println("  - " + u.getFirstName() + " " + u.getLastName() + " (" + u.getAge() + ")");
        }
        
        // Select users with condition
        List<User> youngUsers = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.age.lt(30))
                .fetch();
        
        System.out.println("✓ Found " + youngUsers.size() + " users under 30");
        
        // Select single user
        User singleUser = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.firstName.eq("John"))
                .fetchFirst();
        
        System.out.println("✓ Single user lookup: " + 
                (singleUser != null ? singleUser.getFirstName() + " " + singleUser.getLastName() : "null"));
        
        // Count users
        long userCount = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.city.eq("New York"))
                .fetchCount();
        
        System.out.println("✓ Users in New York: " + userCount);
        
        // Select products
        List<Product> products = queryFactory
                .selectFrom(product.getEntityPath())
                .where(product.price.gt(new BigDecimal("100")))
                .fetch();
        
        System.out.println("✓ Found " + products.size() + " expensive products");
        
        System.out.println();
    }
    
    private static void testUpdateOperations(ExecutableQueryFactory queryFactory) {
        System.out.println("Testing UPDATE operations...");
        
        // Update user email
        long updateCount1 = queryFactory
                .update(user.getEntityPath())
                .set(user.email, "john.doe.updated@example.com")
                .where(user.firstName.eq("John"))
                .execute();
        
        System.out.println("✓ Updated " + updateCount1 + " user email");
        
        // Update multiple fields
        long updateCount2 = queryFactory
                .update(user.getEntityPath())
                .set(user.city, "Los Angeles")
                .set(user.age, 31)
                .where(user.firstName.eq("John"), user.lastName.eq("Doe"))
                .execute();
        
        System.out.println("✓ Updated " + updateCount2 + " user records");
        
        // Update product price
        long updateCount3 = queryFactory
                .update(product.getEntityPath())
                .set(product.price, new BigDecimal("999.99"))
                .where(product.name.contains("Laptop"))
                .execute();
        
        System.out.println("✓ Updated " + updateCount3 + " product prices");
        
        System.out.println();
    }
    
    private static void testDeleteOperations(ExecutableQueryFactory queryFactory) {
        System.out.println("Testing DELETE operations...");
        
        // Insert test data for deletion
        queryFactory
                .insertInto(user.getEntityPath())
                .set(user.firstName, "Test")
                .set(user.lastName, "User")
                .set(user.email, "test@example.com")
                .set(user.age, 99)
                .set(user.city, "TestCity")
                .execute();
        
        // Delete test user
        long deleteCount1 = queryFactory
                .deleteFrom(user.getEntityPath())
                .where(user.firstName.eq("Test"))
                .execute();
        
        System.out.println("✓ Deleted " + deleteCount1 + " test user");
        
        // Insert and delete products
        queryFactory
                .insertInto(product.getEntityPath())
                .set(product.name, "Test Product")
                .set(product.price, new BigDecimal("1.00"))
                .set(product.category, "Test")
                .set(product.stockQuantity, 0)
                .execute();
        
        long deleteCount2 = queryFactory
                .deleteFrom(product.getEntityPath())
                .where(product.category.eq("Test"))
                .execute();
        
        System.out.println("✓ Deleted " + deleteCount2 + " test products");
        
        System.out.println();
    }
    
    private static void testTransactions(ExecutableQueryFactory queryFactory) throws SQLException {
        System.out.println("Testing transactions...");
        
        // Test successful transaction
        queryFactory.inTransaction(txFactory -> {
            txFactory
                    .insertInto(user.getEntityPath())
                    .set(user.firstName, "Alice")
                    .set(user.lastName, "Johnson")
                    .set(user.email, "alice@example.com")
                    .set(user.age, 28)
                    .set(user.city, "Chicago")
                    .execute();
            
            txFactory
                    .insertInto(user.getEntityPath())
                    .set(user.firstName, "Bob")
                    .set(user.lastName, "Wilson")
                    .set(user.email, "bob@example.com")
                    .set(user.age, 35)
                    .set(user.city, "Chicago")
                    .execute();
        });
        
        long chicagoUsers = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.city.eq("Chicago"))
                .fetchCount();
        
        System.out.println("✓ Transaction committed - Users in Chicago: " + chicagoUsers);
        
        // Test rollback transaction
        try {
            queryFactory.inTransaction(txFactory -> {
                txFactory
                        .insertInto(user.getEntityPath())
                        .set(user.firstName, "Charlie")
                        .set(user.lastName, "Brown")
                        .set(user.email, "charlie@example.com")
                        .set(user.age, 40)
                        .set(user.city, "Denver")
                        .execute();
                
                // Force an error to test rollback
                throw new RuntimeException("Simulated error for rollback test");
            });
        } catch (Exception e) {
            // Expected exception for rollback test
        }
        
        long denverUsers = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.city.eq("Denver"))
                .fetchCount();
        
        System.out.println("✓ Transaction rolled back - Users in Denver: " + denverUsers);
        
        // Test manual transaction management
        try (Transaction transaction = queryFactory.beginTransaction()) {
            ExecutableQueryFactory txFactory = new ExecutableQueryFactory(
                new hatien.querydsl.core.database.TransactionalQueryExecutorWrapper(
                    new hatien.querydsl.core.database.TransactionalQueryExecutor(transaction)
                )
            );
            
            txFactory
                    .insertInto(user.getEntityPath())
                    .set(user.firstName, "David")
                    .set(user.lastName, "Miller")
                    .set(user.email, "david@example.com")
                    .set(user.age, 33)
                    .set(user.city, "Seattle")
                    .execute();
            
            transaction.commit();
        }
        
        long seattleUsers = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.city.eq("Seattle"))
                .fetchCount();
        
        System.out.println("✓ Manual transaction - Users in Seattle: " + seattleUsers);
        
        System.out.println();
    }
}