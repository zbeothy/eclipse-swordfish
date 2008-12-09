/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Mattes - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.api;

import java.util.Map;

/**
 * Handles the registration of the Swordfish components like interceptors,
 * strategies, exceptionListeners.
 *
 * @param <T> Type of components registered in the present registry
 */
public interface Registry<T> extends ReadOnlyRegistry<T> {

	/**
	 * Perform registration of an item with its properties.
	 * @param item The key i.e. the component for which properties are
	 * registered.
	 * @param properties The properties of the registered item. Must not
	 * be <code>null</code>, but if there are no properties, an empty
	 * Map must be provided.
	 * @throws SwordfishException risen in case of double registration
	 * of the same item, an invalid item, or an invalid <code>properties</code>
	 * argument.
	 */
	void register(T item, Map<String, ?> properties)
			throws SwordfishException;

	/**
	 * Unregister an item.
	 * @param item - to be unregistered. When the item is not registered,
	 * nothing happens.
	 * @throws SwordfishException if an item cannot be unregistered due to
	 * the internal state of the system.
	 */
	void unregister(T item, Map<String, ?> properties)
			throws SwordfishException;
 
}