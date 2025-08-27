package hatien.querydsl.core.query;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.predicate.Predicate;
import hatien.querydsl.core.visitor.SQLVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the DeleteQuery interface that builds SQL DELETE statements using a fluent API.
 * This class maintains internal lists for the target entity and where conditions.
 *
 * @param <T> the type of the entity being deleted
 */
public class DeleteQueryBuilder<T> implements DeleteQuery<T> {
    private Expression<T> targetEntity;
    private final List<Predicate> whereList = new ArrayList<>();
    
    private final SQLVisitor sqlVisitor = new SQLVisitor();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteQuery<T> from(Expression<T> entity) {
        this.targetEntity = entity;
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteQuery<T> where(Predicate... predicates) {
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
     * Constructs a DELETE SQL string with optional WHERE clause:
     * DELETE FROM table WHERE condition
     */
    @Override
    public String toSQL() {
        if (targetEntity == null) {
            throw new IllegalStateException("Target entity must be specified using from()");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(targetEntity.accept(sqlVisitor));
        
        // WHERE clause
        if (!whereList.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(whereList.stream()
                    .map(predicate -> predicate.accept(sqlVisitor))
                    .collect(Collectors.joining(" AND ")));
        }
        
        return sql.toString();
    }
}