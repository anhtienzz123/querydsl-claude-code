package hatien.querydsl.core.visitor;

import hatien.querydsl.core.expression.Expression;
import hatien.querydsl.core.expression.AggregateExpression;
import hatien.querydsl.core.expression.CaseExpression;
import hatien.querydsl.core.expression.WhenClause;
import hatien.querydsl.core.path.EntityPath;
import hatien.querydsl.core.path.StringPath;
import hatien.querydsl.core.path.NumberPath;
import hatien.querydsl.core.predicate.BooleanExpression;
import hatien.querydsl.core.query.JoinExpression;
import hatien.querydsl.core.query.TableSource;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Visitor implementation that converts expressions to SQL string
 * representations. This class handles the conversion of QueryDSL expressions
 * into their SQL equivalents.
 */
public class SQLVisitor implements ExpressionVisitor<String> {

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
	 * Converts boolean expressions to their SQL string representations based on the
	 * predicate type. Handles all supported predicate operations including
	 * comparisons, null checks, string operations, logical operations, and complex
	 * predicates like BETWEEN and IN.
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
	 * Formats binary operations (two operands) with the specified operator.
	 *
	 * @param expression the boolean expression containing the operands
	 * @param operator   the SQL operator to use between operands
	 * @return formatted SQL string with the binary operation
	 */
	private String formatBinary(BooleanExpression expression, String operator) {
		Object[] operands = expression.getOperands();
		String left = visitOperand(operands[0]);
		String right;

		// For logical operators (AND, OR), both operands should be visited as
		// expressions
		// For comparison operators, the right operand should be formatted as a value
		if ("AND".equals(operator) || "OR".equals(operator)) {
			right = visitOperand(operands[1]);
		} else {
			right = formatValue(operands[1]);
		}

		return String.format("(%s %s %s)", left, operator, right);
	}

	/**
	 * Formats unary operations (single operand) with the specified operator.
	 *
	 * @param expression the boolean expression containing the operand
	 * @param operator   the SQL operator to apply to the operand
	 * @return formatted SQL string with the unary operation
	 */
	private String formatUnary(BooleanExpression expression, String operator) {
		Object[] operands = expression.getOperands();
		String operand = visitOperand(operands[0]);
		return String.format("(%s %s)", operand, operator);
	}

	/**
	 * Formats BETWEEN operations for range comparisons.
	 *
	 * @param expression the boolean expression with field, min, and max values
	 * @return formatted SQL BETWEEN clause
	 */
	private String formatBetween(BooleanExpression expression) {
		Object[] operands = expression.getOperands();
		String field = visitOperand(operands[0]);
		String min = formatValue(operands[1]);
		String max = formatValue(operands[2]);
		return String.format("(%s BETWEEN %s AND %s)", field, min, max);
	}

	/**
	 * Formats IN operations for testing membership in a set of values.
	 *
	 * @param expression the boolean expression with field and array of values
	 * @return formatted SQL IN clause with comma-separated values
	 */
	private String formatIn(BooleanExpression expression) {
		Object[] operands = expression.getOperands();
		String field = visitOperand(operands[0]);
		Object[] values = (Object[]) operands[1];
		String valuesList = Arrays.stream(values).map(this::formatValue).collect(Collectors.joining(", "));
		return String.format("(%s IN (%s))", field, valuesList);
	}

	/**
	 * Formats CONTAINS operations as LIKE with surrounding wildcards.
	 *
	 * @param expression the boolean expression with field and substring
	 * @return formatted SQL LIKE clause with % wildcards around the value
	 */
	private String formatContains(BooleanExpression expression) {
		Object[] operands = expression.getOperands();
		String field = visitOperand(operands[0]);
		String value = formatValue("%" + operands[1] + "%");
		return String.format("(%s LIKE %s)", field, value);
	}

	/**
	 * Formats STARTS_WITH operations as LIKE with trailing wildcard.
	 *
	 * @param expression the boolean expression with field and prefix
	 * @return formatted SQL LIKE clause with % wildcard after the value
	 */
	private String formatStartsWith(BooleanExpression expression) {
		Object[] operands = expression.getOperands();
		String field = visitOperand(operands[0]);
		String value = formatValue(operands[1] + "%");
		return String.format("(%s LIKE %s)", field, value);
	}

	/**
	 * Formats ENDS_WITH operations as LIKE with leading wildcard.
	 *
	 * @param expression the boolean expression with field and suffix
	 * @return formatted SQL LIKE clause with % wildcard before the value
	 */
	private String formatEndsWith(BooleanExpression expression) {
		Object[] operands = expression.getOperands();
		String field = visitOperand(operands[0]);
		String value = formatValue("%" + operands[1]);
		return String.format("(%s LIKE %s)", field, value);
	}

	/**
	 * Formats IS_EMPTY operations to check for empty string or null values.
	 *
	 * @param expression the boolean expression with the field to check
	 * @return formatted SQL condition checking for empty string or null
	 */
	private String formatIsEmpty(BooleanExpression expression) {
		Object[] operands = expression.getOperands();
		String field = visitOperand(operands[0]);
		return String.format("(%s = '' OR %s IS NULL)", field, field);
	}

	/**
	 * Formats IS_NOT_EMPTY operations to check for non-empty, non-null values.
	 *
	 * @param expression the boolean expression with the field to check
	 * @return formatted SQL condition checking for non-empty and non-null values
	 */
	private String formatIsNotEmpty(BooleanExpression expression) {
		Object[] operands = expression.getOperands();
		String field = visitOperand(operands[0]);
		return String.format("(%s != '' AND %s IS NOT NULL)", field, field);
	}

	/**
	 * Formats NOT operations to negate boolean expressions.
	 *
	 * @param expression the boolean expression with the operand to negate
	 * @return formatted SQL NOT clause
	 */
	private String formatNot(BooleanExpression expression) {
		Object[] operands = expression.getOperands();
		String operand = visitOperand(operands[0]);
		return String.format("(NOT %s)", operand);
	}

	/**
	 * Visits an operand, converting it to SQL string representation. If the operand
	 * is an Expression, delegates to its accept method. Otherwise, converts to
	 * string.
	 *
	 * @param operand the operand to visit
	 * @return SQL string representation of the operand
	 */
	private String visitOperand(Object operand) {
		if (operand instanceof Expression) {
			return ((Expression<?>) operand).accept(this);
		}
		return String.valueOf(operand);
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

	/**
	 * {@inheritDoc}
	 * 
	 * Converts aggregate expressions (COUNT, SUM, AVG, MIN, MAX) to their SQL
	 * representations.
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
	 * Converts CASE expressions to their SQL representations.
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

	/**
	 * {@inheritDoc}
	 * 
	 * Converts JOIN expressions to their SQL representations.
	 */
	@Override
	public String visit(JoinExpression<?> expression) {
		StringBuilder sql = new StringBuilder();
		sql.append(expression.getJoinType().getSqlKeyword()).append(" ");
		sql.append(visitOperand(expression.getTarget()));

		if (expression.hasAlias()) {
			sql.append(" AS ").append(expression.getAlias());
		}

		// Add ON condition for all joins except CROSS JOIN
		if (expression.getCondition() != null) {
			sql.append(" ON ").append(visitOperand(expression.getCondition()));
		}

		return sql.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Converts TableSource expressions to their SQL representations.
	 */
	@Override
	public String visit(TableSource<?> tableSource) {
		StringBuilder sql = new StringBuilder();
		sql.append(visitOperand(tableSource.getSource()));

		if (tableSource.hasAlias()) {
			sql.append(" AS ").append(tableSource.getAlias());
		}

		return sql.toString();
	}
}