/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Volodymyr Zhabiuk - initial implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.planner.api;

import java.util.Map;

import org.eclipse.swordfish.api.ReadOnlyRegistry;
import org.eclipse.swordfish.api.SwordfishException;

/**
 * Handles the registration of the Swordfish components like interceptors, strategies, exceptionListeners
 *
 * @param <T>
 */
public interface Registry<T> extends ReadOnlyRegistry<T> {

	/**
	 * @param item
	 * @param properties the properties of the restered item
	 * @throws SwordfishException
	 */
	public void register(T item, Map<String, ?> properties)
			throws SwordfishException;

	/**
	 * @param item - to be unregistered
	 * @param properties
	 * @throws SwordfishException
	 */
	public void unregister(T item, Map<String, ?> properties)
			throws SwordfishException;

}