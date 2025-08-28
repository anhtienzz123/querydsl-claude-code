package hatien.querydsl.core.path;

import hatien.querydsl.core.expression.Expression;

public interface Path<T> extends Expression<T> {
	/**
	 * Returns the name of this path segment.
	 *
	 * @return the name of this path
	 */
	String getName();

	/**
	 * Returns the parent path of this path, if any.
	 *
	 * @return the parent path, or null if this is a root path
	 */
	Path<?> getParent();

	/**
	 * Returns the full path string, including all parent path segments separated by
	 * dots.
	 *
	 * @return the complete path string from root to this path
	 */
	String getFullPath();
}