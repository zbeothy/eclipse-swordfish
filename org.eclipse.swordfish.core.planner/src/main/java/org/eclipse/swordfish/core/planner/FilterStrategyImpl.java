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
package org.eclipse.swordfish.core.planner;

import java.util.List;

import org.eclipse.swordfish.api.FilterStrategy;
import org.eclipse.swordfish.api.Hint;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;
import org.eclipse.swordfish.api.SwordfishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dwolz
 *
 */
public class FilterStrategyImpl implements FilterStrategy {

	private Logger logger = LoggerFactory.getLogger(PlannerImpl.class);

	private List<FilterStrategy> filterStrategies;

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.FilterStrategy#filter(java.util.List, org.eclipse.swordfish.api.ReadOnlyRegistry, java.util.List)
	 */
	public List<Interceptor> filter(List<Interceptor> interceptors,
			ReadOnlyRegistry<Interceptor> registry, List<Hint<?>> hints) {
		if (filterStrategies != null && filterStrategies.size() > 0) {
			List<Interceptor> sorted = null;
			for (FilterStrategy strategy: filterStrategies) {
				try {
					sorted = strategy.filter(interceptors, registry, hints);
					break; // success
				} catch (SwordfishException e) {
					continue; // try next
				}
			}
			return sorted;
		} else {
			logger.info("No filter strategy defined");
			return interceptors;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.FilterStrategy#getPriority()
	 */
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setFilterStrategies(List<FilterStrategy> filterStrategies) {
		this.filterStrategies = filterStrategies;
	}

}
