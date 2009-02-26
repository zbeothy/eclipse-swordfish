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
package org.eclipse.swordfish.core.event;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.TrackingEvent;

public class TrackingEventImpl extends EventImpl implements TrackingEvent {

	private MessageExchange exchange;
	
    public TrackingEventImpl(MessageExchange exchange) {
		super();
		this.exchange = exchange;
	}

	public String getTopic() {
       return EventConstants.TOPIC_TRACKING_EVENT;
    }

    public int getMessageExchangeId() {
        throw new UnsupportedOperationException("method not implemented yet");
    }

	public int getSeverity() {
		return (Integer)getProperty(EventConstants.EVENT_SEVERITY);
	}

	public void setSeverity(int severity) {
		setProperty(EventConstants.EVENT_SEVERITY, severity);
	}

	public MessageExchange getExchange() {
		return exchange;
	}
}
