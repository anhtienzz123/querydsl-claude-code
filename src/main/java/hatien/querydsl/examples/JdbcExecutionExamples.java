package hatien.querydsl.examples;

import hatien.querydsl.core.database.DatabaseConfig;
import hatien.querydsl.core.query.ExecutableQueryFactory;
import java.math.BigDecimal;

/**
 * Examples demonstrating JDBC integration with QueryDSL.
 * 
 * This class shows how to use the ExecutableQueryFactory to perform actual database operations.
 * Note: These examples require a JDBC driver (like H2, MySQL, PostgreSQL) to be available on the classpath.
 */
public class JdbcExecutionExamples {
    
    private static final QUser user = QUser.user;
    private static final QProduct product = QProduct.product;
    
    public static void main(String[] args) {
        System.out.println("=== QueryDSL JDBC Execution Examples ===\n");
        
        demonstrateConnectionConfiguration();
        demonstrateQueryExecution();
        demonstrateTransactionUsage();
        
        System.out.println("Note: To run these examples with actual database execution,");
        System.out.println("add a JDBC driver to your classpath and uncomment the execution code.\n");
    }
    
    private static void demonstrateConnectionConfiguration() {
        System.out.println("1. Database Configuration Examples:");
        
        // H2 in-memory database (for testing)
        System.out.println("   H2 in-memory database configuration:");
        DatabaseConfig h2Config = DatabaseConfig.h2InMemory("testdb");
        System.out.println("   URL: " + h2Config.getUrl());
        System.out.println("   Driver: " + h2Config.getDriverClassName());
        
        // H2 file-based database
        System.out.println("\n   H2 file-based database configuration:");
        DatabaseConfig h2FileConfig = DatabaseConfig.h2File("./data/myapp");
        System.out.println("   URL: " + h2FileConfig.getUrl());
        
        // MySQL database
        System.out.println("\n   MySQL database configuration:");
        DatabaseConfig mysqlConfig = DatabaseConfig.mysql("localhost", 3306, "myapp", "user", "password");
        System.out.println("   URL: " + mysqlConfig.getUrl());
        System.out.println("   Driver: " + mysqlConfig.getDriverClassName());
        
        // PostgreSQL database
        System.out.println("\n   PostgreSQL database configuration:");
        DatabaseConfig pgConfig = DatabaseConfig.postgresql("localhost", 5432, "myapp", "user", "password");
        System.out.println("   URL: " + pgConfig.getUrl());
        System.out.println("   Driver: " + pgConfig.getDriverClassName());
        
        System.out.println();
    }
    
    private static void demonstrateQueryExecution() {
        System.out.println("2. Query Execution Examples:");
        
        System.out.println("   Code to create executable query factory:");
        System.out.println("   ```java");
        System.out.println("   ExecutableQueryFactory queryFactory = ExecutableQueryFactory.createH2InMemory(\"testdb\");");
        System.out.println("   ```");
        
        System.out.println("\n   INSERT operations:");
        System.out.println("   ```java");
        System.out.println("   long insertCount = queryFactory");
        System.out.println("       .insertInto(user.getEntityPath())");
        System.out.println("       .set(user.firstName, \"John\")");
        System.out.println("       .set(user.lastName, \"Doe\")");
        System.out.println("       .set(user.email, \"john.doe@example.com\")");
        System.out.println("       .set(user.age, 30)");
        System.out.println("       .execute();");
        System.out.println("   ```");
        
        System.out.println("\n   SELECT operations:");
        System.out.println("   ```java");
        System.out.println("   List<User> users = queryFactory");
        System.out.println("       .selectFrom(user.getEntityPath())");
        System.out.println("       .where(user.age.gt(25))");
        System.out.println("       .fetch();");
        System.out.println("   ```");
        
        System.out.println("\n   UPDATE operations:");
        System.out.println("   ```java");
        System.out.println("   long updateCount = queryFactory");
        System.out.println("       .update(user.getEntityPath())");
        System.out.println("       .set(user.email, \"newemail@example.com\")");
        System.out.println("       .where(user.firstName.eq(\"John\"))");
        System.out.println("       .execute();");
        System.out.println("   ```");
        
        System.out.println("\n   DELETE operations:");
        System.out.println("   ```java");
        System.out.println("   long deleteCount = queryFactory");
        System.out.println("       .deleteFrom(user.getEntityPath())");
        System.out.println("       .where(user.age.lt(18))");
        System.out.println("       .execute();");
        System.out.println("   ```");
        
        System.out.println("\n   COUNT operations:");
        System.out.println("   ```java");
        System.out.println("   long userCount = queryFactory");
        System.out.println("       .selectFrom(user.getEntityPath())");
        System.out.println("       .where(user.city.eq(\"New York\"))");
        System.out.println("       .fetchCount();");
        System.out.println("   ```");
        
        System.out.println();
        
        /* Uncomment this section when H2 driver is available:
        
        try {
            ExecutableQueryFactory queryFactory = ExecutableQueryFactory.createH2InMemory("demodb");
            
            // Create tables (this would normally be done with a schema migration tool)
            // setupDemoTables(queryFactory);
            
            // INSERT example
            long insertCount = queryFactory
                .insertInto(user.getEntityPath())
                .set(user.firstName, "John")
                .set(user.lastName, "Doe")
                .set(user.email, "john.doe@example.com")
                .set(user.age, 30)
                .set(user.city, "New York")
                .execute();
            
            System.out.println("✓ Inserted " + insertCount + " user record");
            
            // SELECT example
            List<User> users = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.age.gt(25))
                .fetch();
            
            System.out.println("✓ Found " + users.size() + " users over 25");
            
            // UPDATE example
            long updateCount = queryFactory
                .update(user.getEntityPath())
                .set(user.email, "john.doe.updated@example.com")
                .where(user.firstName.eq("John"))
                .execute();
            
            System.out.println("✓ Updated " + updateCount + " user records");
            
            // COUNT example
            long userCount = queryFactory
                .selectFrom(user.getEntityPath())
                .where(user.city.eq("New York"))
                .fetchCount();
            
            System.out.println("✓ Users in New York: " + userCount);
            
            queryFactory.close();
            
        } catch (Exception e) {
            System.err.println("Database execution failed: " + e.getMessage());
        }
        */
    }
    
    private static void demonstrateTransactionUsage() {
        System.out.println("3. Transaction Usage Examples:");
        
        System.out.println("   Automatic transaction management:");
        System.out.println("   ```java");
        System.out.println("   queryFactory.inTransaction(txFactory -> {");
        System.out.println("       txFactory.insertInto(user.getEntityPath())");
        System.out.println("           .set(user.firstName, \"Alice\")");
        System.out.println("           .set(user.lastName, \"Johnson\")");
        System.out.println("           .execute();");
        System.out.println("       ");
        System.out.println("       txFactory.insertInto(user.getEntityPath())");
        System.out.println("           .set(user.firstName, \"Bob\")");
        System.out.println("           .set(user.lastName, \"Smith\")");
        System.out.println("           .execute();");
        System.out.println("   }); // Transaction is automatically committed or rolled back");
        System.out.println("   ```");
        
        System.out.println("\n   Manual transaction management:");
        System.out.println("   ```java");
        System.out.println("   try (Transaction transaction = queryFactory.beginTransaction()) {");
        System.out.println("       ExecutableQueryFactory txFactory = new ExecutableQueryFactory(");
        System.out.println("           new TransactionalQueryExecutorWrapper(");
        System.out.println("               new TransactionalQueryExecutor(transaction)");
        System.out.println("           )");
        System.out.println("       );");
        System.out.println("       ");
        System.out.println("       txFactory.insertInto(user.getEntityPath())");
        System.out.println("           .set(user.firstName, \"Charlie\")");
        System.out.println("           .execute();");
        System.out.println("       ");
        System.out.println("       transaction.commit(); // Manual commit");
        System.out.println("   } // Transaction is rolled back if not committed");
        System.out.println("   ```");
        
        System.out.println();
        
        /* Uncomment this section when database is available:
        
        try {
            ExecutableQueryFactory queryFactory = ExecutableQueryFactory.createH2InMemory("txdemo");
            
            // Automatic transaction example
            queryFactory.inTransaction(txFactory -> {
                txFactory.insertInto(user.getEntityPath())
                    .set(user.firstName, "Alice")
                    .set(user.lastName, "Johnson")
                    .set(user.age, 28)
                    .execute();
                
                txFactory.insertInto(user.getEntityPath())
                    .set(user.firstName, "Bob")
                    .set(user.lastName, "Smith")
                    .set(user.age, 32)
                    .execute();
            });
            
            System.out.println("✓ Transaction completed successfully");
            
            // Manual transaction example
            try (Transaction transaction = queryFactory.beginTransaction()) {
                ExecutableQueryFactory txFactory = new ExecutableQueryFactory(
                    new TransactionalQueryExecutorWrapper(
                        new TransactionalQueryExecutor(transaction)
                    )
                );
                
                txFactory.insertInto(user.getEntityPath())
                    .set(user.firstName, "Charlie")
                    .set(user.lastName, "Brown")
                    .set(user.age, 35)
                    .execute();
                
                transaction.commit();
            }
            
            System.out.println("✓ Manual transaction completed successfully");
            
            queryFactory.close();
            
        } catch (Exception e) {
            System.err.println("Transaction demo failed: " + e.getMessage());
        }
        */
    }
}