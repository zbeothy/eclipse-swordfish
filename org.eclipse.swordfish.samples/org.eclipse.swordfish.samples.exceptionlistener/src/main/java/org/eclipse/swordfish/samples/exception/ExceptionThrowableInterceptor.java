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
package org.eclipse.swordfish.samples.exception;

import java.util.Map;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SwordfishException;


public class ExceptionThrowableInterceptor implements Interceptor {
	
	private boolean needToThrow;
	

	public boolean isNeedToThrow() {
		return needToThrow;
	}

	public void setNeedToThrow(boolean needToThrow) {
		this.needToThrow = needToThrow;
	}

	public void process(MessageExchange exchange) throws SwordfishException {
		throw new SwordfishException("This exception for testing Interceptor Exception Listener");
	}

	public Map<String, ?> getProperties() {
		return null;
	}

	public Class<?> getType() {
		return this.getClass();
	}

}
