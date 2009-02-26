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

/**
 *  Basic interface for sending Swordfish events inside Swordfish runtime environment
 *  Should be implemented via delegation to 
 *  org.osgi.service.event.EventAdmin service and implementation must be registered as a OSGI service itself
 *  under org.eclipse.swordfish.api.event.Eventrvice interface name.
 */

public interface EventService {
    /**
     * Initiate asynchronous delivery of an event by invoking org.osgi.service.event.EventAdmin.postEvent() method.
     * Method returns to the caller before delivery of the event is completed.
     * Method supports events with topic name started with Swordfish prefix (org/eclipse/runtime/swordfish)
     * 
     * osgi Event Admin methanizm using for broadcasting Swordfish events. Swordfish broadcasts as 
     * org.osgi.service.event.Event property with name specified 
     * in org.osgi.service.event.EventConstants.EVENT constant.
     * @param event the event to be posted.
     */
    void postEvent(Event event);
}
