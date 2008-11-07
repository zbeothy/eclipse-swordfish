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
import java.util.Set;


/**
 * Can be plugged into the nmr to sort the registered interceptors
 *
 */
public interface SortingStrategy extends Strategy {

	public List<Interceptor> sort(Set<Interceptor> interceptors,
			ReadOnlyRegistry<Interceptor> registry);

}
