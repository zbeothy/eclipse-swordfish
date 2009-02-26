/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
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
