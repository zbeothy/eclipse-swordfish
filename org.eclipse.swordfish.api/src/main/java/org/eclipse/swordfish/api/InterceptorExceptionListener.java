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

import javax.jbi.messaging.MessageExchange;

/**
 * Provides error handling logic. Can be plugged into the Swordfish framework as an osgi service
 * to receive asynchronous notifications about exception thrown 
 * during interceptor chain invocation should implement current interface and register 
 * implementation class as OSGI service with name
 * "org.eclipse.swordfish.api.InterceptorExceptionListener"
 */
public interface InterceptorExceptionListener {

    /**
     * invoked each time exception thrown inside 
     * org.eclipse.swordfish.api.Interceptor.process(MessageExchange exchange) method
	 * @param exception - the thrown exception to be handled
	 * @param exchange - the messageExchange that caused the processing error
	 * @param interceptor - that threw the exception
	 */
	void handle(Exception exception, MessageExchange exchange, Interceptor interceptor);
}
