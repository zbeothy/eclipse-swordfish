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
package org.eclipse.swordfish.api.event;

import javax.jbi.messaging.MessageExchange;

/**
 * message tracking events are used to track the progress of message processing in the core, 
 * operational events are used to notify administrators or other software components of events 
 * like failure situations etc.
 * @author akopachevsky
 */
public interface TrackingEvent extends Event {

	/**
	 * ID of the tracked message exchange.
	 * @return numeric message exchange identifier.
	 */
	int getMessageExchangeId();

	/**
	 * Event severity value as defined in the {@link Severity} interface.
	 * @return numeric Severity value.
	 */
	int getSeverity();
	
	/**
	 * jbi message exchange object
	 */
	MessageExchange getExchange();
}
