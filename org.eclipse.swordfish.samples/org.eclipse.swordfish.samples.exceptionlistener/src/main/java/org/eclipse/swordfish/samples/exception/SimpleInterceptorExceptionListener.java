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

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.InterceptorExceptionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleInterceptorExceptionListener implements InterceptorExceptionListener{

	private static final Logger LOG = LoggerFactory.getLogger(SimpleInterceptorExceptionListener.class);
	
	public void handle(Exception exception, MessageExchange exchange,
			Interceptor interceptor) {
		String message = 
			String.format("SimpleExceptionlistener receive exception [%s] thrown during [%s] interceptor work " +
				  "for message exchange [%s]", exception, interceptor.getClass().getName(), exchange);
		LOG.info(message);
		System.out.println(message);
	}

}
