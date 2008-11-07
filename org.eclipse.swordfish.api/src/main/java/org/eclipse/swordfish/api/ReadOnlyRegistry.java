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
 * The generic strategy that can store items along with their properties. The content can not be modified
 *
 * @param <T>
 */
public interface ReadOnlyRegistry<T> {

	/**
	 * @return the content of the registry
	 */
	public Set<T> getKeySet();

	/**
	 * @param key
	 * @return the properties for the specified key
	 */
	public Map<String, ?> getProperties(T key);

}