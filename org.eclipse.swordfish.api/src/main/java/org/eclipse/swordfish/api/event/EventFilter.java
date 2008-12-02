package org.eclipse.swordfish.api.event;
/**
 * interface for filtering events base on its properties values 
 * @author akopachevsky
 *
 */
public interface EventFilter {

	/**
	 * Selector-style expression for event filtering.
	 * @return Expression String, never <code>null</code> or empty.
	 * TODO Syntax description.
	 */
	String getExpression();
	
}
