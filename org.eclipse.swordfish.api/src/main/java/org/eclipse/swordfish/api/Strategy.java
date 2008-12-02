/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Oliver Wolf - initial API and implementation
 *******************************************************************************/

package org.eclipse.swordfish.api;

/**
 * The base class for all strategies e.g. FilteringStrategy, SortingStrategy.
 *
 */
public interface Strategy {

	/**
	 * Indicates the ordering if several unified strategies can be applied.
	 */
	int getPriority();

}
