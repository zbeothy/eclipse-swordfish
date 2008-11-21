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

import java.util.List;


/**
 * The strategy that can remove some interceptors from the interceptor chain based on the supplied hints.
 * Can be plugged into the Swordfish framework as an osgi service
 *
 */
public interface FilterStrategy extends Strategy {

	/**
	 * @param interceptors - the interceptor chain to be filtered
	 * @param registry - the interceptor registry containing
	 * @param hints - TODO: Andreas, Dietmar could you write the description for this item
	 * @return the filtered interceptor chain
	 */
	public List<Interceptor> filter(List<Interceptor> interceptors,
			ReadOnlyRegistry<Interceptor> registry, List<Hint<?>> hints);

}
