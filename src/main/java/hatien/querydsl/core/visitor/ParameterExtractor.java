package hatien.querydsl.core.visitor;

import hatien.querydsl.core.path.EntityPath;
import hatien.querydsl.core.path.StringPath;
import hatien.querydsl.core.path.NumberPath;
import hatien.querydsl.core.predicate.BooleanExpression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Visitor implementation that extracts parameter values from expressions for prepared statements.
 * This class collects all literal values that should be parameterized in SQL queries.
 */
public class ParameterExtractor implements ExpressionVisitor<List<Object>> {
    
    /**
     * {@inheritDoc}
     * 
     * EntityPath expressions don't contain parameters.
     */
    @Override
    public List<Object> visit(EntityPath<?> path) {
        return new ArrayList<>();
    }
    
    /**
     * {@inheritDoc}
     * 
     * StringPath expressions don't contain parameters.
     */
    @Override
    public List<Object> visit(StringPath path) {
        return new ArrayList<>();
    }
    
    /**
     * {@inheritDoc}
     * 
     * NumberPath expressions don't contain parameters.
     */
    @Override
    public List<Object> visit(NumberPath<?> path) {
        return new ArrayList<>();
    }
    
    /**
     * {@inheritDoc}
     * 
     * Extracts parameter values from boolean expressions based on the predicate type.
     * Returns all literal values that should be parameterized in prepared statements.
     */
    @Override
    public List<Object> visit(BooleanExpression expression) {
        List<Object> parameters = new ArrayList<>();
        Object[] operands = expression.getOperands();
        
        switch (expression.getPredicateType()) {
            case EQ, NE, LT, GT, LOE, GOE, LIKE -> {
                // Binary operations: left operand is usually a path, right operand is the parameter
                if (operands.length >= 2) {
                    // First operand (left side) - extract parameters if it's an expression
                    if (operands[0] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[0]));
                    }
                    // Second operand (right side) - this is usually the parameter value
                    if (!(operands[1] instanceof hatien.querydsl.core.expression.Expression)) {
                        parameters.add(operands[1]);
                    } else if (operands[1] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[1]));
                    }
                }
            }
            case IS_NULL, IS_NOT_NULL, IS_EMPTY, IS_NOT_EMPTY -> {
                // Unary operations: extract parameters from the operand if it's an expression
                if (operands.length >= 1 && operands[0] instanceof BooleanExpression) {
                    parameters.addAll(visit((BooleanExpression) operands[0]));
                }
            }
            case CONTAINS -> {
                // CONTAINS: value needs % wildcards around it
                if (operands.length >= 2) {
                    if (operands[0] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[0]));
                    }
                    Object searchValue = operands[1];
                    if (!(searchValue instanceof hatien.querydsl.core.expression.Expression)) {
                        parameters.add("%" + searchValue + "%");
                    }
                }
            }
            case STARTS_WITH -> {
                // STARTS_WITH: value needs % wildcard after it
                if (operands.length >= 2) {
                    if (operands[0] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[0]));
                    }
                    Object searchValue = operands[1];
                    if (!(searchValue instanceof hatien.querydsl.core.expression.Expression)) {
                        parameters.add(searchValue + "%");
                    }
                }
            }
            case ENDS_WITH -> {
                // ENDS_WITH: value needs % wildcard before it
                if (operands.length >= 2) {
                    if (operands[0] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[0]));
                    }
                    Object searchValue = operands[1];
                    if (!(searchValue instanceof hatien.querydsl.core.expression.Expression)) {
                        parameters.add("%" + searchValue);
                    }
                }
            }
            case BETWEEN -> {
                // BETWEEN operation: operands[1] and operands[2] are the min and max values
                if (operands.length >= 3) {
                    if (operands[0] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[0]));
                    }
                    // Min value
                    if (!(operands[1] instanceof hatien.querydsl.core.expression.Expression)) {
                        parameters.add(operands[1]);
                    }
                    // Max value
                    if (!(operands[2] instanceof hatien.querydsl.core.expression.Expression)) {
                        parameters.add(operands[2]);
                    }
                }
            }
            case IN -> {
                // IN operation: operands[1] is an array of values
                if (operands.length >= 2) {
                    if (operands[0] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[0]));
                    }
                    // Array of values
                    if (operands[1] instanceof Object[]) {
                        Object[] values = (Object[]) operands[1];
                        parameters.addAll(Arrays.asList(values));
                    }
                }
            }
            case AND, OR -> {
                // Logical operations: extract parameters from both operands
                if (operands.length >= 2) {
                    if (operands[0] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[0]));
                    }
                    if (operands[1] instanceof BooleanExpression) {
                        parameters.addAll(visit((BooleanExpression) operands[1]));
                    }
                }
            }
            case NOT -> {
                // NOT operation: extract parameters from the operand
                if (operands.length >= 1 && operands[0] instanceof BooleanExpression) {
                    parameters.addAll(visit((BooleanExpression) operands[0]));
                }
            }
        }
        
        return parameters;
    }
}