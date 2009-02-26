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
package org.eclipse.swordfish.core.event.test;

import javax.jbi.messaging.MessageExchange;

import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.core.event.TrackingEventImpl;

public class SimpleTrackingEvent extends TrackingEventImpl {

	public static final String TOPIC = EventConstants.TOPIC_TRACKING_EVENT + "TEST";
	
	public SimpleTrackingEvent(MessageExchange exchange) {
		super(exchange);
	}

	public String getTopic() {
		return TOPIC;
	}
	
}