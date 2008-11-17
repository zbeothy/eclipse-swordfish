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
package org.eclipse.swordfish.core.planner;

import java.util.List;
import java.util.Set;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;
import org.eclipse.swordfish.api.SortingStrategy;
import org.eclipse.swordfish.api.SwordfishException;


public class SortingStrategyImpl implements SortingStrategy {

	private List<SortingStrategy> sortingStrategies;

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.SortingStrategy#filter(java.util.List, org.eclipse.swordfish.api.ReadOnlyRegistry)
	 */
	public List<Interceptor> sort(Set<Interceptor> interceptors,
			ReadOnlyRegistry<Interceptor> registry) {
		if (sortingStrategies != null && sortingStrategies.size() > 0) {
			List<Interceptor> sorted = null;
			for (SortingStrategy strategy: sortingStrategies) {
				try {
					sorted = strategy.sort(interceptors, registry);
					break; // success
				} catch (SwordfishException e) {
					continue; // try next
				}
			}
			return sorted;
		} else {
			throw new SwordfishException("No sorting strategy defined");
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.SortingStrategy#getPriority()
	 */
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSortingStrategies(List<SortingStrategy> sortingStrategies) {
		this.sortingStrategies = sortingStrategies;
	}

}
