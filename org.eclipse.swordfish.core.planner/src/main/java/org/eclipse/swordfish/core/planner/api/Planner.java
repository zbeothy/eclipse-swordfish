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
package org.eclipse.swordfish.core.planner.api;

import java.util.List;
import java.util.Set;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.FilterStrategy;
import org.eclipse.swordfish.api.HintExtractor;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;
import org.eclipse.swordfish.api.SortingStrategy;

/**
 * Creates the interceptor chain for each messageExchange
 *
 */
public interface Planner {

	public void setInterceptorRegistry(ReadOnlyRegistry<Interceptor> interceptorRegistry);

	public void setHintExtractor(HintExtractor hintExtractor);

	public void setSortingStrategy(SortingStrategy sortingStrategy);

	public void setFilterStrategy(FilterStrategy filterStrategy);

	/**
	 * Based on hintExtractor, sorting and filtering strategies creates the interceptor chain for each messageExchange
	 * @param interceptors - registered interceptors
	 * @param messageExchange - giveb=n jbi messageExchange
	 * @return
	 */
	public List<Interceptor> getInterceptorChain(Set<Interceptor> interceptors, MessageExchange messageExchange);

	public Set<Interceptor> getRegisteredInterceptors();

}