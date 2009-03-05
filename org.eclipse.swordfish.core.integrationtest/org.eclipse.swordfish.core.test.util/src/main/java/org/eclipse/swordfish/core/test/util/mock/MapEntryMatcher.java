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
package org.eclipse.swordfish.core.test.util.mock;

import java.util.Map;

import org.easymock.IArgumentMatcher;

/**
 * An argument matcher that checks existence and value of an entry
 * in a map.
 * 
 * @author jkindler
 */
public class MapEntryMatcher implements IArgumentMatcher {
	private Map<?, ?> expectedMap;
	private Object expectedKey;

	/**
	 * Contruct the matcher.
	 * @param mapExpected - the map with the expected value
	 * @param keyExpected - a key that must be present within the map mapExpected.
	 */
	public MapEntryMatcher(Map<?, ?> mapExpected, Object keyExpected) {
		this.expectedMap = mapExpected;
		this.expectedKey = keyExpected;
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer buffer) {
		buffer.append("eqMap(");
		buffer.append(expectedMap.getClass().getName());
		buffer.append(" with key \"");
		buffer.append(expectedKey.toString());
		buffer.append("\"");
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean matches(Object argument) {
		if (!(argument instanceof Map)) {
			return false;
		}

		Map<Object, Object> testMap = (Map<Object, Object>) argument;
		return testMap.containsKey(expectedKey)
				&& expectedMap.get(expectedKey).equals(
						testMap.get(expectedKey));
	}
}
