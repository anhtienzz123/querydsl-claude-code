package hatien.querydsl.core.visitor;

import hatien.querydsl.core.path.EntityPath;
import hatien.querydsl.core.path.StringPath;
import hatien.querydsl.core.path.NumberPath;
import hatien.querydsl.core.predicate.BooleanExpression;
import hatien.querydsl.core.expression.AggregateExpression;
import hatien.querydsl.core.expression.CaseExpression;

/**
 * Visitor interface for processing different types of expressions. Implements
 * the Visitor pattern to allow type-safe processing of expression trees.
 *
 * @param <R> the return type of visit operations
 */
public interface ExpressionVisitor<R> {
	/**
	 * Visits an EntityPath expression.
	 *
	 * @param path the entity path to visit
	 * @return the result of processing this entity path
	 */
	R visit(EntityPath<?> path);

	/**
	 * Visits a StringPath expression.
	 *
	 * @param path the string path to visit
	 * @return the result of processing this string path
	 */
	R visit(StringPath path);

	/**
	 * Visits a NumberPath expression.
	 *
	 * @param path the number path to visit
	 * @return the result of processing this number path
	 */
	R visit(NumberPath<?> path);

	/**
	 * Visits a BooleanExpression.
	 *
	 * @param expression the boolean expression to visit
	 * @return the result of processing this boolean expression
	 */
	R visit(BooleanExpression expression);

	/**
	 * Visits an AggregateExpression (COUNT, SUM, AVG, MIN, MAX).
	 *
	 * @param expression the aggregate expression to visit
	 * @return the result of processing this aggregate expression
	 */
	R visit(AggregateExpression<?> expression);

	/**
	 * Visits a CaseExpression (CASE WHEN construct).
	 *
	 * @param expression the case expression to visit
	 * @return the result of processing this case expression
	 */
	R visit(CaseExpression<?> expression);
}