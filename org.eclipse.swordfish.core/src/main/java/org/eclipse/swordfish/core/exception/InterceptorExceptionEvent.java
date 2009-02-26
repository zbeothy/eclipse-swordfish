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
package org.eclipse.swordfish.core.exception;


import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.core.event.EventImpl;


public class InterceptorExceptionEvent extends EventImpl {
	
    public static final String TOPIC_INTECEPTOR_EXCEPTOIN_EVENT = 
    	EventConstants.TOPIC_BASE + InterceptorExceptionEvent.class.getSimpleName();
	
	private Exception exception;
	private MessageExchange exchange;
	private Interceptor interceptor;
	

	public Exception getException() {
		return exception;
	}


	public InterceptorExceptionEvent(Exception exception,
			MessageExchange exchange, Interceptor interceptor) {
		super();
		this.exception = exception;
		this.exchange = exchange;
		this.interceptor = interceptor;
	}


	public void setException(Exception exception) {
		this.exception = exception;
	}


	public MessageExchange getExchange() {
		return exchange;
	}


	public void setExchange(MessageExchange exchange) {
		this.exchange = exchange;
	}


	public Interceptor getInterceptor() {
		return interceptor;
	}


	public void setInterceptor(Interceptor interceptor) {
		this.interceptor = interceptor;
	}


	public String getTopic() {
		return TOPIC_INTECEPTOR_EXCEPTOIN_EVENT;
	}
}
