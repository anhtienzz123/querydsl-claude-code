package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.path.Path;
import hatien.querydsl.core.predicate.Predicate;
import hatien.querydsl.core.visitor.SQLVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the UpdateQuery interface that builds SQL UPDATE statements using a fluent API.
 * This class maintains internal lists for the target entity, set clauses, and where conditions.
 *
 * @param <T> the type of the entity being updated
 */
public class UpdateQueryBuilder<T> implements UpdateQuery<T> {
    private Expression<T> targetEntity;
    private final Map<Path<?>, Object> setValues = new LinkedHashMap<>();
    private final Map<Path<?>, Expression<?>> setExpressions = new LinkedHashMap<>();
    private final List<Predicate> whereList = new ArrayList<>();
    
    private final SQLVisitor sqlVisitor = new SQLVisitor();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateQuery<T> table(Expression<T> entity) {
        this.targetEntity = entity;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <V> UpdateQuery<T> set(Path<V> column, V value) {
        setValues.put(column, value);
        setExpressions.remove(column); // Remove any conflicting expression
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <V> UpdateQuery<T> set(Path<V> column, Expression<V> expression) {
        setExpressions.put(column, expression);
        setValues.remove(column); // Remove any conflicting value
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateQuery<T> where(Predicate... predicates) {
        whereList.addAll(Arrays.asList(predicates));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long execute() {
        throw new UnsupportedOperationException("Query execution not implemented in this simplified version");
    }
    
    /**
     * {@inheritDoc}
     * 
     * Constructs an UPDATE SQL string with SET and optional WHERE clauses:
     * UPDATE table SET col1 = val1, col2 = val2 WHERE condition
     */
    @Override
    public String toSQL() {
        if (targetEntity == null) {
            throw new IllegalStateException("Target entity must be specified using table()");
        }
        
        if (setValues.isEmpty() && setExpressions.isEmpty()) {
            throw new IllegalStateException("At least one SET clause must be specified");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(targetEntity.accept(sqlVisitor));
        
        // SET clause
        sql.append(" SET ");
        List<String> setClauses = new ArrayList<>();
        
        // Add value-based set clauses
        for (Map.Entry<Path<?>, Object> entry : setValues.entrySet()) {
            String columnName = entry.getKey().accept(sqlVisitor);
            String value = formatValue(entry.getValue());
            setClauses.add(columnName + " = " + value);
        }
        
        // Add expression-based set clauses
        for (Map.Entry<Path<?>, Expression<?>> entry : setExpressions.entrySet()) {
            String columnName = entry.getKey().accept(sqlVisitor);
            String expression = entry.getValue().accept(sqlVisitor);
            setClauses.add(columnName + " = " + expression);
        }
        
        sql.append(String.join(", ", setClauses));
        
        // WHERE clause
        if (!whereList.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereList.stream()
                    .map(predicate -> predicate.accept(sqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }
    
    /**
     * Formats a value for use in SQL, adding quotes around strings.
     *
     * @param value the value to format
     * @return formatted SQL value (strings are quoted, others converted to string)
     */
    private String formatValue(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return String.valueOf(value);
    }
}