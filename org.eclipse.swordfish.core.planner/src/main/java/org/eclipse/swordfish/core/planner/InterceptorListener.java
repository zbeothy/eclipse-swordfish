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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swordfish.api.Interceptor;

public class InterceptorListener {

	private InterceptorRegistry interceptorRegistry;

	public void onBindInterceptor(Object interceptor, Map properties) {
		Map interceptorProps = new HashMap();
		if (properties != null) {
			interceptorProps.putAll(properties);
		}
		if (((Interceptor)interceptor).getProperties() != null) {
			interceptorProps.putAll(((Interceptor)interceptor).getProperties());
		}
		interceptorRegistry.register((Interceptor) interceptor,
				interceptorProps);
	}

	public void onUnbindInterceptor(Object interceptor,
			Map<String, ?> properties) {
		interceptorRegistry.unregister((Interceptor) interceptor, properties);
	}

	public void setInterceptorRegistry(InterceptorRegistry interceptorRegistry) {
		this.interceptorRegistry = interceptorRegistry;
	}

}
