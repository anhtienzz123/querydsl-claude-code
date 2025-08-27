package hatien.querydsl.core.visitor;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.expression.AggregateExpression;
import hatien.querydsl.core.expression.CaseExpression;
import hatien.querydsl.core.expression.WhenClause;
import hatien.querydsl.core.path.EntityPath;
import hatien.querydsl.core.path.StringPath;
import hatien.querydsl.core.path.NumberPath;
import hatien.querydsl.core.predicate.BooleanExpression;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Visitor implementation that converts expressions to parameterized SQL with placeholders.
 * This class generates SQL suitable for PreparedStatement execution with ? placeholders.
 */
public class ParameterizedSQLVisitor implements ExpressionVisitor<String> {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(EntityPath<?> path) {
        return path.getFullPath();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(StringPath path) {
        return path.getFullPath();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String visit(NumberPath<?> path) {
        return path.getFullPath();
    }
    
    /**
     * {@inheritDoc}
     * 
     * Converts boolean expressions to their parameterized SQL string representations.
     * Uses ? placeholders for parameter values instead of literal values.
     */
    @Override
    public String visit(BooleanExpression expression) {
        return switch (expression.getPredicateType()) {
            case EQ -> formatBinary(expression, "=");
            case NE -> formatBinary(expression, "!=");
            case LT -> formatBinary(expression, "<");
            case GT -> formatBinary(expression, ">");
            case LOE -> formatBinary(expression, "<=");
            case GOE -> formatBinary(expression, ">=");
            case IS_NULL -> formatUnary(expression, "IS NULL");
            case IS_NOT_NULL -> formatUnary(expression, "IS NOT NULL");
            case LIKE -> formatBinary(expression, "LIKE");
            case CONTAINS -> formatContains(expression);
            case STARTS_WITH -> formatStartsWith(expression);
            case ENDS_WITH -> formatEndsWith(expression);
            case IS_EMPTY -> formatIsEmpty(expression);
            case IS_NOT_EMPTY -> formatIsNotEmpty(expression);
            case BETWEEN -> formatBetween(expression);
            case IN -> formatIn(expression);
            case AND -> formatBinary(expression, "AND");
            case OR -> formatBinary(expression, "OR");
            case NOT -> formatNot(expression);
        };
    }
    
    /**
     * Formats binary operations with parameterized placeholders.
     */
    private String formatBinary(BooleanExpression expression, String operator) {
        Object[] operands = expression.getOperands();
        String left = visitOperand(operands[0]);
        String right;
        
        // For logical operators (AND, OR), both operands should be visited as expressions
        // For comparison operators, the right operand should be parameterized
        if ("AND".equals(operator) || "OR".equals(operator)) {
            right = visitOperand(operands[1]);
        } else {
            right = isExpression(operands[1]) ? visitOperand(operands[1]) : "?";
        }
        
        return String.format("(%s %s %s)", left, operator, right);
    }
    
    /**
     * Formats unary operations.
     */
    private String formatUnary(BooleanExpression expression, String operator) {
        Object[] operands = expression.getOperands();
        String operand = visitOperand(operands[0]);
        return String.format("(%s %s)", operand, operator);
    }
    
    /**
     * Formats BETWEEN operations with parameterized placeholders.
     */
    private String formatBetween(BooleanExpression expression) {
        Object[] operands = expression.getOperands();
        String field = visitOperand(operands[0]);
        String min = isExpression(operands[1]) ? visitOperand(operands[1]) : "?";
        String max = isExpression(operands[2]) ? visitOperand(operands[2]) : "?";
        return String.format("(%s BETWEEN %s AND %s)", field, min, max);
    }
    
    /**
     * Formats IN operations with parameterized placeholders.
     */
    private String formatIn(BooleanExpression expression) {
        Object[] operands = expression.getOperands();
        String field = visitOperand(operands[0]);
        
        if (operands[1] instanceof Object[]) {
            Object[] values = (Object[]) operands[1];
            String placeholders = Arrays.stream(values)
                    .map(v -> "?")
                    .collect(Collectors.joining(", "));
            return String.format("(%s IN (%s))", field, placeholders);
        }
        
        return String.format("(%s IN (?))", field);
    }
    
    /**
     * Formats CONTAINS operations with parameterized placeholder.
     */
    private String formatContains(BooleanExpression expression) {
        Object[] operands = expression.getOperands();
        String field = visitOperand(operands[0]);
        return String.format("(%s LIKE ?)", field);
    }
    
    /**
     * Formats STARTS_WITH operations with parameterized placeholder.
     */
    private String formatStartsWith(BooleanExpression expression) {
        Object[] operands = expression.getOperands();
        String field = visitOperand(operands[0]);
        return String.format("(%s LIKE ?)", field);
    }
    
    /**
     * Formats ENDS_WITH operations with parameterized placeholder.
     */
    private String formatEndsWith(BooleanExpression expression) {
        Object[] operands = expression.getOperands();
        String field = visitOperand(operands[0]);
        return String.format("(%s LIKE ?)", field);
    }
    
    /**
     * Formats IS_EMPTY operations.
     */
    private String formatIsEmpty(BooleanExpression expression) {
        Object[] operands = expression.getOperands();
        String field = visitOperand(operands[0]);
        return String.format("(%s = '' OR %s IS NULL)", field, field);
    }
    
    /**
     * Formats IS_NOT_EMPTY operations.
     */
    private String formatIsNotEmpty(BooleanExpression expression) {
        Object[] operands = expression.getOperands();
        String field = visitOperand(operands[0]);
        return String.format("(%s != '' AND %s IS NOT NULL)", field, field);
    }
    
    /**
     * Formats NOT operations.
     */
    private String formatNot(BooleanExpression expression) {
        Object[] operands = expression.getOperands();
        String operand = visitOperand(operands[0]);
        return String.format("(NOT %s)", operand);
    }
    
    /**
     * Visits an operand, converting it to SQL string representation.
     */
    private String visitOperand(Object operand) {
        if (operand instanceof Expression) {
            return ((Expression<?>) operand).accept(this);
        }
        return String.valueOf(operand);
    }
    
    /**
     * Checks if an operand is an expression.
     */
    private boolean isExpression(Object operand) {
        return operand instanceof Expression;
    }

    /**
     * {@inheritDoc}
     * 
     * Converts aggregate expressions to their parameterized SQL representations.
     * Aggregate functions typically don't have parameters themselves, but their
     * inner expressions might.
     */
    @Override
    public String visit(AggregateExpression<?> expression) {
        String functionName = expression.getAggregateType().toString();
        
        if (expression.getExpression() == null) {
            // This is COUNT(*)
            return functionName + "(*)";
        }
        
        String arg = visitOperand(expression.getExpression());
        return String.format("%s(%s)", functionName, arg);
    }

    /**
     * {@inheritDoc}
     * 
     * Converts CASE expressions to their parameterized SQL representations.
     * Parameters in CASE expressions come from the WHEN conditions and values.
     */
    @Override
    public String visit(CaseExpression<?> expression) {
        StringBuilder sql = new StringBuilder("CASE");
        
        for (WhenClause<?> whenClause : expression.getWhenClauses()) {
            sql.append(" WHEN ").append(visitOperand(whenClause.getCondition()));
            sql.append(" THEN ").append(visitOperand(whenClause.getValue()));
        }
        
        if (expression.getElseExpression() != null) {
            sql.append(" ELSE ").append(visitOperand(expression.getElseExpression()));
        }
        
        sql.append(" END");
        return sql.toString();
    }
}