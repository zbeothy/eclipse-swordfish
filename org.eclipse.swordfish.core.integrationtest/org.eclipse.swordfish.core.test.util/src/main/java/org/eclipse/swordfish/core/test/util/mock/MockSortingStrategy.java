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
import java.util.Set;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.ReadOnlyRegistry;
import org.eclipse.swordfish.api.SortingStrategy;
import org.eclipse.swordfish.api.SwordfishException;

/**
 * @author dwolz
 *
 */
public class MockSortingStrategy implements SortingStrategy {

	public List<Interceptor> sort(Set<Interceptor> interceptorSet,
			ReadOnlyRegistry<Interceptor> arg1) throws SwordfishException {
		// TODO Auto-generated method stub
		return new ArrayList<Interceptor>(interceptorSet);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swordfish.api.FilterStrategy#getPriority()
	 */
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
