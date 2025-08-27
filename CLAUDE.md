# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a QueryDSL clone implementation built from scratch using pure Java 21 without external dependencies. It demonstrates advanced object-oriented design patterns and domain-specific language (DSL) construction techniques, providing a type-safe fluent API for constructing database queries.

## Development Commands

### JDBC Database Execution
The QueryDSL implementation now includes full JDBC integration for executing queries against real databases:

```bash
# Run JDBC execution examples (requires JDBC driver on classpath)
java -cp target/classes:h2-database.jar hatien.querydsl.examples.JdbcExecutionExamples

# Run JDBC integration tests (requires H2 driver)
java -cp target/classes:h2-database.jar hatien.querydsl.JdbcIntegrationTest
```

### Compilation Commands
```bash
# Compile all source files
javac -d target/classes -cp src/main/java src/main/java/hatien/querydsl/examples/QueryDSLExamples.java src/main/java/hatien/querydsl/examples/*.java src/main/java/hatien/querydsl/core/*/*.java

# Compile tests
javac -d target/classes -cp target/classes src/test/java/hatien/querydsl/QueryDSLTest.java
```

### Execution Commands
```bash
# Run the main examples
java -cp target/classes hatien.querydsl.examples.QueryDSLExamples

# Run the test suite
java -cp target/classes hatien.querydsl.QueryDSLTest
```

### Maven Commands
```bash
# Clean and compile with Maven
mvn clean compile

# Run main class with Maven
mvn exec:java -Dexec.mainClass="hatien.querydsl.examples.QueryDSLExamples"

# Run tests with Maven (if test plugins are configured)
mvn test
```

## Architecture Overview

The codebase follows a layered architecture with clear separation of concerns:

### Core Package Structure
- `hatien.querydsl.core.expression/` - Expression abstractions and implementations
- `hatien.querydsl.core.path/` - Type-safe property access paths (fields/columns)
- `hatien.querydsl.core.predicate/` - Boolean conditions and logical operators  
- `hatien.querydsl.core.query/` - Query builders and fluent API (both SQL generation and executable)
- `hatien.querydsl.core.metadata/` - Entity definitions and reflection
- `hatien.querydsl.core.visitor/` - Query transformation and SQL generation
- `hatien.querydsl.core.database/` - JDBC integration, connection management, and transactions
- `hatien.querydsl.examples/` - Sample entities and usage examples

### Key Design Patterns
- **Builder Pattern**: `QueryBuilder` provides fluent query construction
- **Factory Pattern**: `QueryFactory` for centralized query creation
- **Visitor Pattern**: `SQLVisitor` transforms expression trees to SQL
- **Composite Pattern**: `BooleanExpression` hierarchy for complex conditions
- **Fluent Interface**: Method chaining throughout the API
- **Connection Pool Pattern**: `ConnectionManager` manages database connections
- **Transaction Pattern**: `Transaction` provides ACID transaction boundaries

### Type Safety Architecture
The library uses a sophisticated type system:
- `Expression<T>` - Base interface for all expressions
- `SimpleExpression<T>` - Equality and null checks
- `ComparableExpression<T>` - Ordering operations
- `StringExpression` - String-specific operations (LIKE, contains, etc.)
- `NumberPath<T>` - Numeric operations and comparisons

### Entry Points
- **SQL Generation Only**: Start queries with `QueryFactory.create()`
- **Database Execution**: Start queries with `ExecutableQueryFactory.createH2InMemory("dbname")` or other database configs
- Use static imports: `import static hatien.querydsl.examples.QUser.user;`
- Main query patterns: 
  - **SELECT queries:**
    - `queryFactory.selectFrom(entity).where(conditions)` - Select all columns
    - `queryFactory.select(column).from(entity).where(conditions)` - Select single column
    - `queryFactory.select(col1, col2, col3).from(entity).where(conditions)` - Select multiple columns
    - `query.fetch()` - Execute and return List<T>
    - `query.fetchOne()` - Execute and return single result
    - `query.fetchFirst()` - Execute and return first result
    - `query.fetchCount()` - Execute count query
  - **INSERT queries:**
    - `queryFactory.insertInto(entity).set(column, value).execute()` - Insert with set() method
    - `queryFactory.insert().into(entity).columns(cols).values(vals).execute()` - Insert with columns/values
  - **UPDATE queries:**
    - `queryFactory.update(entity).set(column, value).where(conditions).execute()` - Update with conditions
    - `queryFactory.update(entity).set(column, expression).where(conditions).execute()` - Update with expressions
  - **DELETE queries:**
    - `queryFactory.deleteFrom(entity).where(conditions).execute()` - Delete with conditions
    - `queryFactory.deleteFrom(entity).execute()` - Delete all rows

### JDBC Database Integration
The QueryDSL implementation includes comprehensive JDBC support for real database execution:

#### Database Configuration
```java
// H2 in-memory database (ideal for testing)
ExecutableQueryFactory queryFactory = ExecutableQueryFactory.createH2InMemory("testdb");

// H2 file-based database
ExecutableQueryFactory queryFactory = ExecutableQueryFactory.createH2File("./data/myapp");

// Custom database configuration
DatabaseConfig config = DatabaseConfig.mysql("localhost", 3306, "myapp", "user", "password");
ExecutableQueryFactory queryFactory = new ExecutableQueryFactory(config);

// PostgreSQL configuration
DatabaseConfig config = DatabaseConfig.postgresql("localhost", 5432, "myapp", "user", "password");
ExecutableQueryFactory queryFactory = new ExecutableQueryFactory(config);
```

#### Transaction Management
```java
// Automatic transaction management
queryFactory.inTransaction(txFactory -> {
    txFactory.insertInto(user.getEntityPath())
        .set(user.firstName, "Alice")
        .execute();
    
    txFactory.update(user.getEntityPath())
        .set(user.email, "alice@example.com")
        .where(user.firstName.eq("Alice"))
        .execute();
}); // Automatically commits or rolls back

// Manual transaction management
try (Transaction transaction = queryFactory.beginTransaction()) {
    // Use TransactionalQueryExecutor for operations within this transaction
    transaction.commit(); // Must explicitly commit
} // Automatically rolls back if not committed
```

#### Connection Management
- Built-in connection pooling with configurable pool size
- Automatic connection lifecycle management
- Prepared statement parameter binding for security
- Result set mapping to entity objects

## Testing Strategy

The project includes comprehensive test coverage in `QueryDSLTest.java` that validates:
- **SELECT query generation:**
  - Basic query generation
  - String operations (LIKE, contains, startsWith)
  - Numeric operations (BETWEEN, IN, comparisons)
  - Boolean logic (AND, OR, NOT)
  - Complex predicate combinations
  - Column selection (single and multiple columns)
- **INSERT query generation:**
  - Insert with set() method (column-value pairs)
  - Insert with columns() and values() method
  - Insert with various data types including BigDecimal
- **UPDATE query generation:**
  - Update with single and multiple set clauses
  - Update with WHERE conditions
  - Update with complex boolean logic
  - Update with expression references
- **DELETE query generation:**
  - Delete with WHERE conditions
  - Delete with complex boolean logic (AND, OR, NOT)
  - Delete with IN conditions
  - Delete all rows (no WHERE clause)
- **JDBC Integration Testing:**
  - Actual database execution with H2 in-memory database
  - Insert, Update, Delete operations with row count validation
  - Select operations with entity mapping and result validation
  - Transaction management (commit/rollback scenarios)
  - Connection pooling and resource management

Tests use assertion statements to validate generated SQL output and actual database results.

## Code Conventions

- Uses Java 21 features including pattern matching in switch expressions
- No external dependencies - pure Java implementation
- Fluent interface pattern with method chaining
- Static factory methods for object creation
- Comprehensive JavaDoc documentation
- Package-private constructors with public factory methods