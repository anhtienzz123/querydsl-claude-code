package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.path.Path;
import hatien.querydsl.core.visitor.SQLVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the InsertQuery interface that builds SQL INSERT statements using a fluent API.
 * This class maintains internal lists for the target entity, columns, and values.
 *
 * @param <T> the type of the entity being inserted
 */
public class InsertQueryBuilder<T> implements InsertQuery<T> {
    private Expression<T> targetEntity;
    private final List<Path<?>> columnList = new ArrayList<>();
    private final List<Object> valuesList = new ArrayList<>();
    private final Map<Path<?>, Object> setValues = new LinkedHashMap<>();
    
    private final SQLVisitor sqlVisitor = new SQLVisitor();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public InsertQuery<T> into(Expression<T> entity) {
        this.targetEntity = entity;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public InsertQuery<T> columns(Path<?>... columns) {
        columnList.addAll(Arrays.asList(columns));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public InsertQuery<T> values(Object... values) {
        valuesList.addAll(Arrays.asList(values));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <V> InsertQuery<T> set(Path<V> column, V value) {
        setValues.put(column, value);
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
     * Constructs an INSERT SQL string. Supports two modes:
     * 1. Column/Values mode: INSERT INTO table (col1, col2) VALUES (val1, val2)
     * 2. Set mode: INSERT INTO table (col1, col2) VALUES (val1, val2) using set() calls
     */
    @Override
    public String toSQL() {
        if (targetEntity == null) {
            throw new IllegalStateException("Target entity must be specified using into()");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(targetEntity.accept(sqlVisitor));
        
        List<Path<?>> columnsToUse;
        List<Object> valuesToUse;
        
        if (!setValues.isEmpty()) {
            columnsToUse = new ArrayList<>(setValues.keySet());
            valuesToUse = new ArrayList<>(setValues.values());
        } else {
            columnsToUse = columnList;
            valuesToUse = valuesList;
        }
        
        if (!columnsToUse.isEmpty()) {
            sql.append(" (");
            sql.append(columnsToUse.stream()
                    .map(column -> column.accept(sqlVisitor))
                    .collect(Collectors.joining(", ")));
            sql.append(")");
        }
        
        if (!valuesToUse.isEmpty()) {
            sql.append(" VALUES (");
            sql.append(valuesToUse.stream()
                    .map(this::formatValue)
                    .collect(Collectors.joining(", ")));
            sql.append(")");
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