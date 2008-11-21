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

import java.util.Dictionary;
import java.util.List;
import java.util.Set;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.FilterStrategy;
import org.eclipse.swordfish.api.Hint;
import org.eclipse.swordfish.api.HintExtractor;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;
import org.eclipse.swordfish.api.SortingStrategy;
import org.eclipse.swordfish.core.planner.api.Planner;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlannerImpl implements Planner, ManagedService {

	private Logger logger = LoggerFactory.getLogger(PlannerImpl.class);

    private ReadOnlyRegistry<Interceptor> interceptorRegistry;
    private SortingStrategy sortingStrategy;
    private FilterStrategy filterStrategy;
    private HintExtractor hintExtractor;

	public List<Interceptor> getInterceptorChain(
			Set<Interceptor> interceptors, MessageExchange messageExchange) {
		List<Interceptor> sorted = sortingStrategy.sort(interceptors, interceptorRegistry);
		List<Hint<?>> hints = hintExtractor.extractHints(messageExchange);
		List<Interceptor> filtered = filterStrategy.filter(sorted, interceptorRegistry, hints);
		return filtered;
	}

	public Set<Interceptor> getRegisteredInterceptors() {
		return interceptorRegistry.getKeySet();
	}

	public void setSortingStrategy(SortingStrategy sortingStrategy) {
		this.sortingStrategy = sortingStrategy;
	}

	public void setFilterStrategy(FilterStrategy filterStrategy) {
		this.filterStrategy = filterStrategy;
	}

	public void setHintExtractor(HintExtractor hintExtractor) {
		this.hintExtractor = hintExtractor;
	}

	public void setInterceptorRegistry(
			ReadOnlyRegistry<Interceptor> interceptorRegistry) {
		this.interceptorRegistry = interceptorRegistry;
	}

	public void updated(Dictionary dictionary) throws ConfigurationException {
		// TODO Auto-generated method stub
	}

}
