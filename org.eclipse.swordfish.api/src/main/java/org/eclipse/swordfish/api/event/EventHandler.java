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
 * Interface providing ability to listen for Swordifish messages sending on specified topic.
 * @param T base event class.
 */
public interface EventHandler<T extends Event> {

	/**
      * specified event topic name
      * @return topic name, must not be <code>null</code> or an empty String.
      */
     String getSubscribedTopic();

     /**
      * Call-back invoked asynchronously with event creator thread, used osgi EventAdmin
      * service as a transport.
      * @param event the event received.
      */
     void handleEvent(T event);

     /**
      * returns event filter to filter incoming event depending its properties values.
      * @return an event filter or <code>null</code> to receive all events for the subscribed
      * topic.
      */
     EventFilter getEventFilter();
}
