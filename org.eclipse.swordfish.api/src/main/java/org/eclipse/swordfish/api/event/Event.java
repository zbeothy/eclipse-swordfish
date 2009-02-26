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

import java.util.Map;

/**
 * Generic Swordfish event
 */
public interface Event {

	/**
	 * Topic identifier.
	 * @return topic String, never <code>null</code> or empty String.
	 */
	String getTopic();

	/**
	 * Event properties Map.
	 * @return a Map, may be empty, but never <code>null</code>.
	 */
	Map<String, ?> getProperties();

	/**
	 * Convenience method for property access.
	 * @param key property key String.
	 * @return property value or <code>null</code> if not set.
	 */
	Object getProperty(String key);
}
