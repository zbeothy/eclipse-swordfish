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
package org.eclipse.swordfish.core.tracking;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchange.Role;

import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.EventFilter;
import org.eclipse.swordfish.api.event.EventHandler;
import org.eclipse.swordfish.api.event.TrackingEvent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrackingEventHandler implements EventHandler<TrackingEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(TrackingEventHandler.class);
	
	public EventFilter getEventFilter() {
		return null;
	}

	public String getSubscribedTopic() {
		return EventConstants.TOPIC_TRACKING_EVENT;
	}

	public void handleEvent(TrackingEvent event) {
		MessageExchange exchange = event.getExchange();
		
		String messageType = (exchange.getRole().equals(Role.CONSUMER)) ? "Outgoing" : "Incoming";
		String output = String.format("%s event with id=[%s]", messageType, exchange.getExchangeId());
		if(exchange.getMessage("in") != null ){
			output += "\n    in message : " + exchange.getMessage("in").getContent().toString();
		}
		if(exchange.getMessage("out") != null && exchange.getMessage("out").getContent() != null){
			output += "\n    out message : " + exchange.getMessage("out").getContent().toString();
		}
		if(exchange.getError() != null){
		     output += "\n    exception class   : " + exchange.getError().getClass().getCanonicalName();
		     output += "\n    exception message : " + exchange.getError().getMessage();
		}
		LOG.debug(output);
		//System.out.println(output);
	}

}
