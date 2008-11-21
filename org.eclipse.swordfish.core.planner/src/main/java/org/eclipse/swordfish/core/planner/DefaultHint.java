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

import java.util.Map;

import org.eclipse.swordfish.api.Hint;

public class DefaultHint implements Hint<Map<String,Boolean>> {

	private Map<String,Boolean> useInterceptor;

	public DefaultHint(Map<String,Boolean> useInterceptor) {
		setUseInterceptor(useInterceptor);
	}

	/**
	 * @param useInterceptor the useInterceptor to set
	 */
	public void setUseInterceptor(Map<String,Boolean> useInterceptor) {
		this.useInterceptor = useInterceptor;
	}

	public Map<String,Boolean> getInfo() {
		// TODO Auto-generated method stub
		return useInterceptor;
	}

	public Class<Map<String,Boolean>> getType() {
		// TODO Auto-generated method stub
		return (Class<Map<String,Boolean>>) useInterceptor.getClass();
	}


}
