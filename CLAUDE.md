# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a QueryDSL clone implementation built from scratch using pure Java 21 without external dependencies. It demonstrates advanced object-oriented design patterns and domain-specific language (DSL) construction techniques, providing a type-safe fluent API for constructing database queries.

## Development Commands

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
- `hatien.querydsl.core.query/` - Query builders and fluent API
- `hatien.querydsl.core.metadata/` - Entity definitions and reflection
- `hatien.querydsl.core.visitor/` - Query transformation and SQL generation
- `hatien.querydsl.examples/` - Sample entities and usage examples

### Key Design Patterns
- **Builder Pattern**: `QueryBuilder` provides fluent query construction
- **Factory Pattern**: `QueryFactory` for centralized query creation
- **Visitor Pattern**: `SQLVisitor` transforms expression trees to SQL
- **Composite Pattern**: `BooleanExpression` hierarchy for complex conditions
- **Fluent Interface**: Method chaining throughout the API

### Type Safety Architecture
The library uses a sophisticated type system:
- `Expression<T>` - Base interface for all expressions
- `SimpleExpression<T>` - Equality and null checks
- `ComparableExpression<T>` - Ordering operations
- `StringExpression` - String-specific operations (LIKE, contains, etc.)
- `NumberPath<T>` - Numeric operations and comparisons

### Entry Points
- Start queries with `QueryFactory.create()`
- Use static imports: `import static hatien.querydsl.examples.QUser.user;`
- Main query patterns: `queryFactory.selectFrom(entity).where(conditions)`

## Testing Strategy

The project includes comprehensive test coverage in `QueryDSLTest.java` that validates:
- Basic query generation
- String operations (LIKE, contains, startsWith)
- Numeric operations (BETWEEN, IN, comparisons)
- Boolean logic (AND, OR, NOT)
- Complex predicate combinations

Tests use assertion statements to validate generated SQL output.

## Code Conventions

- Uses Java 21 features including pattern matching in switch expressions
- No external dependencies - pure Java implementation
- Fluent interface pattern with method chaining
- Static factory methods for object creation
- Comprehensive JavaDoc documentation
- Package-private constructors with public factory methods