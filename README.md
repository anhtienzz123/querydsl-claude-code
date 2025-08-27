# QueryDSL Clone - Type-Safe Query DSL in Pure Java

A simplified implementation of QueryDSL from scratch using pure Java (Java 21), demonstrating advanced object-oriented design patterns and domain-specific language (DSL) construction techniques.

## 🎯 Project Overview

This project recreates the core functionality of QueryDSL, providing a type-safe, fluent API for constructing database queries. It demonstrates how to build a sophisticated DSL using pure Java without external dependencies, showcasing enterprise-level software architecture patterns.

## 🏗️ Architecture

### Core Components

```
hatien.querydsl.core/
├── expression/          # Expression abstractions and implementations
├── path/               # Property access paths (fields/columns)  
├── predicate/          # Boolean conditions and operators
├── query/              # Query builders and execution
├── metadata/           # Entity definitions and reflection
└── visitor/            # Query transformation and serialization
```

### Layer Architecture

1. **Expression Layer** - Core abstractions for all query expressions
2. **Path Layer** - Type-safe property/field access paths  
3. **Predicate Layer** - Boolean conditions and logical operators
4. **Query Builder Layer** - Fluent DSL interface for query construction
5. **Metadata Layer** - Entity definitions and type information
6. **Visitor Layer** - Query serialization and transformation

## 🎨 Design Patterns Applied

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Builder Pattern** | `QueryBuilder` class | Fluent query construction with method chaining |
| **Fluent Interface** | All query methods return `Query<T>` | Enable readable, natural language-like queries |
| **Factory Pattern** | `QueryFactory`, `Predicates` | Centralized creation of queries and predicates |
| **Composite Pattern** | `BooleanExpression` hierarchy | Build complex conditions from simple ones |
| **Visitor Pattern** | `ExpressionVisitor`, `SQLVisitor` | Transform expressions into SQL without modifying core classes |
| **Template Method** | `Query` interface | Define query execution framework structure |
| **Strategy Pattern** | Different expression types | Handle various query operations (SELECT, WHERE, etc.) |
| **Chain of Responsibility** | Expression processing | Process complex expressions through transformation pipeline |

### Pattern Justifications

- **Builder + Fluent Interface**: Enables intuitive query construction like `queryFactory.selectFrom(user).where(user.name.eq("John"))`
- **Composite Pattern**: Allows complex boolean expressions like `condition1.and(condition2.or(condition3))`
- **Visitor Pattern**: Separates SQL generation logic from expression structure, enabling multiple output formats
- **Factory Pattern**: Centralizes object creation and provides consistent API entry points

## 🚀 Quick Start

### 1. Define Your Entities

```java
// User.java - Regular POJO
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private String city;
    
    // constructors, getters, setters...
}
```

### 2. Create Query Metadata

```java
// QUser.java - Query metadata class
public class QUser extends EntityMetadata<User> {
    public static final QUser user = new QUser("user");
    
    public final NumberPath<Long> id;
    public final StringPath firstName;
    public final StringPath lastName;
    public final StringPath email;
    public final NumberPath<Integer> age;
    public final StringPath city;
    
    public QUser(String alias) {
        super(User.class, alias);
        this.id = createNumber("id", Long.class);
        this.firstName = createString("firstName");
        this.lastName = createString("lastName");
        this.email = createString("email");
        this.age = createNumber("age", Integer.class);
        this.city = createString("city");
    }
}
```

### 3. Write Type-Safe Queries

```java
import static hatien.querydsl.examples.QUser.user;

QueryFactory queryFactory = QueryFactory.create();

// Simple equality query
Query<User> query1 = queryFactory
    .selectFrom(user.getEntityPath())
    .where(user.firstName.eq("John"));

System.out.println(query1.toSQL());
// Output: SELECT user FROM user WHERE (user.firstName = 'John')
```

## 📖 Usage Examples

### Basic Queries

```java
// Select all users
Query<User> allUsers = queryFactory.selectFrom(user.getEntityPath());

// Select with simple condition
Query<User> adults = queryFactory
    .selectFrom(user.getEntityPath())
    .where(user.age.goe(18));

// Multiple conditions (AND)
Query<User> query = queryFactory
    .selectFrom(user.getEntityPath())
    .where(user.age.goe(18), user.city.eq("New York"));
```

### String Operations

```java
// LIKE pattern matching
queryFactory.selectFrom(user.getEntityPath())
    .where(user.email.like("%@gmail.com"));

// Contains substring
queryFactory.selectFrom(user.getEntityPath())
    .where(user.firstName.contains("oh"));

// Starts with prefix
queryFactory.selectFrom(user.getEntityPath())
    .where(user.lastName.startsWith("Sm"));

// Ends with suffix
queryFactory.selectFrom(user.getEntityPath())
    .where(user.email.endsWith(".com"));

// Empty/null checks
queryFactory.selectFrom(user.getEntityPath())
    .where(user.email.isNotEmpty().and(user.email.isNotNull()));
```

### Numeric Comparisons

```java
// Range queries
queryFactory.selectFrom(user.getEntityPath())
    .where(user.age.between(25, 65));

// Comparison operators
queryFactory.selectFrom(user.getEntityPath())
    .where(user.age.gt(25).and(user.age.lt(65)));

// IN clause
queryFactory.selectFrom(user.getEntityPath())
    .where(user.age.in(25, 30, 35, 40));
```

### Boolean Logic

```java
BooleanExpression condition1 = user.firstName.eq("John");
BooleanExpression condition2 = user.age.gt(25);
BooleanExpression condition3 = user.city.eq("Boston");

// AND conditions
queryFactory.selectFrom(user.getEntityPath())
    .where(condition1.and(condition2));

// OR conditions  
queryFactory.selectFrom(user.getEntityPath())
    .where(condition1.or(condition3));

// NOT conditions
queryFactory.selectFrom(user.getEntityPath())
    .where(condition1.and(condition2.not()));

// Complex nested conditions
queryFactory.selectFrom(user.getEntityPath())
    .where(condition1.and(condition2.or(condition3)));
```

### Product Query Examples

```java
import static hatien.querydsl.examples.QProduct.product;

// Find expensive products
queryFactory.selectFrom(product.getEntityPath())
    .where(product.price.gt(new BigDecimal("100.00")));

// Category and stock filtering
queryFactory.selectFrom(product.getEntityPath())
    .where(product.category.eq("Electronics")
           .and(product.stockQuantity.gt(0)));

// Search in multiple fields
queryFactory.selectFrom(product.getEntityPath())
    .where(product.name.contains("Laptop")
           .or(product.description.contains("laptop")));
```

## 🏃‍♂️ Running the Examples

### Compile and Run Examples

```bash
# Compile all classes
javac -d target/classes -cp src/main/java src/main/java/hatien/querydsl/examples/QueryDSLExamples.java src/main/java/hatien/querydsl/examples/*.java src/main/java/hatien/querydsl/core/*/*.java

# Run the examples
java -cp target/classes hatien.querydsl.examples.QueryDSLExamples
```

### Run Test Suite

```bash
# Compile tests  
javac -d target/classes -cp target/classes src/test/java/hatien/querydsl/QueryDSLTest.java

# Run tests
java -cp target/classes hatien.querydsl.QueryDSLTest
```

## 🔧 Implementation Details

### Expression Type System

The library uses a sophisticated type system to ensure compile-time safety:

- `Expression<T>` - Base interface for all expressions
- `SimpleExpression<T>` - Expressions supporting equality and null checks
- `ComparableExpression<T>` - Expressions supporting ordering operations
- `StringExpression` - String-specific operations (LIKE, contains, etc.)
- `NumberPath<T>` - Numeric operations (ranges, comparisons)

### Visitor Pattern for SQL Generation

The `SQLVisitor` implements the Visitor pattern to transform expression trees into SQL:

```java
public class SQLVisitor implements ExpressionVisitor<String> {
    @Override
    public String visit(BooleanExpression expression) {
        return switch (expression.getPredicateType()) {
            case EQ -> formatBinary(expression, "=");
            case LIKE -> formatBinary(expression, "LIKE");
            case AND -> formatBinary(expression, "AND");
            // ... other cases
        };
    }
}
```

### Fluent Interface Implementation

Method chaining is achieved by returning the same query instance:

```java
public class QueryBuilder<T> implements Query<T> {
    @Override
    public Query<T> where(Predicate... predicates) {
        whereList.addAll(Arrays.asList(predicates));
        return this; // Enable method chaining
    }
}
```

## 🎓 Key Learning Outcomes

This implementation demonstrates:

1. **DSL Design**: How to create intuitive, domain-specific APIs
2. **Type Safety**: Leveraging Java's type system for compile-time guarantees  
3. **Design Patterns**: Real-world application of enterprise patterns
4. **Visitor Pattern**: Separating algorithms from object structure
5. **Builder Pattern**: Constructing complex objects step-by-step
6. **Composite Pattern**: Building complex structures from simple components

## 🔮 Extensions & Improvements

Potential enhancements for a production system:

- **Query Execution**: Add actual database connectivity
- **Join Support**: Implement table joins and relationships
- **Aggregation Functions**: COUNT, SUM, AVG, etc.
- **Subqueries**: Nested query support
- **Order By**: Sorting with ASC/DESC
- **Group By/Having**: Aggregation grouping
- **Limit/Offset**: Pagination support
- **Code Generation**: Automatic Q-class generation from entities

## 📝 License

This educational project is provided as-is for learning purposes.

---

**Built with ❤️ using Pure Java 21 - No external dependencies!**