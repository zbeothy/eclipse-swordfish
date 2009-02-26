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
/**
 *
 */
package org.eclipse.swordfish.core.test.util.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swordfish.api.FilterStrategy;
import org.eclipse.swordfish.api.Hint;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;

/**
 * @author dwolz
 *
 */
public class DefaultHintFilterStrategy implements FilterStrategy {


	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.FilterStrategy#filter(java.util.List, org.eclipse.swordfish.api.ReadOnlyRegistry, java.util.List)
	 */
	public List<Interceptor> filter(List<Interceptor> interceptors,
			ReadOnlyRegistry<Interceptor> registry, List<Hint<?>> hints) {
		List<Interceptor> sorted = new ArrayList<Interceptor>();
		for (Interceptor interceptor: interceptors) {
			for (Hint<?> hint: hints) {
				Class<?> clazz = hint.getType();
				if (clazz.getInterfaces()[0].equals(Map.class)) {
					Map<String,Boolean> map = (Map<String,Boolean>) hint.getInfo();
					String key = interceptor.getClass().getCanonicalName();
					if (map.containsKey(key) && map.get(key)) {
						sorted.add(interceptor);
						break;
					}
				}
			}
		}
		return sorted;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.FilterStrategy#getPriority()
	 */
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
