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

import java.util.Map;
import java.util.Set;

/**
 * Read-Only interface to internal Swordfish registries. When a component
 * known to Swordfish is registered as OSGI service, Swordfish collects
 * Properties and configuration information assigned to the component
 * and stores them in a registry using the component identity (mostly
 * the component instance) as key. The present read-only interface to
 * such a registry is provided as argument where appropriate.
 *
 * @param <T> Type of key object (mostly component type) used in the
 * registry.
 */
public interface ReadOnlyRegistry<T> {

	/**
	 * Read access to the components for which properties are registered.
	 * @return the keys of the registry, i.e. the components registered.
	 */
	Set<T> getKeySet();

	/**
	 * Read access to the property maps.
	 * @param key the component for which the properties are registerd.
	 * @return the properties for the specified component as unmodifiable
	 * Map. A return value of <code>null</code> indicates that a component
	 * has not been registered. A component registered with no properties
	 * will provide an empty Map.
	 */
	Map<String, ?> getProperties(T key);

}