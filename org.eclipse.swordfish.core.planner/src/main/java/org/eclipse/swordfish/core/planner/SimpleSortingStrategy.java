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
package org.eclipse.swordfish.core.planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;
import org.eclipse.swordfish.api.SortingStrategy;

/**
 * @author dwolz
 *
 */
public class SimpleSortingStrategy implements SortingStrategy {
	public static final String	PRIORITY_KEYWORD = "priority";

	private int getInterceptorPriority(Interceptor interceptor, ReadOnlyRegistry<Interceptor> registry) {
		Map props = registry.getProperties(interceptor);
		if (props == null || props.isEmpty()) {
			return 0;
		}
		if (!props.containsKey(PRIORITY_KEYWORD)) {
			return 0;
		}
		Object priority = props.get(PRIORITY_KEYWORD);
		if (priority instanceof Integer) {
			return (Integer) priority;
		} else if (priority instanceof String) {
			return Integer.valueOf((String) priority);
		} else {
			throw new UnsupportedOperationException(priority.getClass()+" could not be transformed to integer");
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.SortingStrategy#filter(java.util.List, org.eclipse.swordfish.api.ReadOnlyRegistry)
	 */
	public List<Interceptor> sort(Set<Interceptor> interceptors,
			final ReadOnlyRegistry<Interceptor> registry) {
		List<Interceptor> sorted = new ArrayList<Interceptor>(interceptors);
		Collections.sort(sorted, new Comparator<Interceptor>() {
			public int compare(Interceptor o1, Interceptor o2) {
				return getInterceptorPriority(o2, registry) - getInterceptorPriority(o1, registry);
			}
		});
		return sorted;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.SortingStrategy#getPriority()
	 */
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
