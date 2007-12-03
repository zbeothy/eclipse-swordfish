/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.core.components.messaging;

/**
 * interface of the listener to the delivery channel.
 */
public interface DeliveryChannelListener extends Runnable {

    /** role of this interface. */
    String ROLE = DeliveryChannelListener.class.getName();

    /**
     * do initialization work.
     */
    void init();

    /**
     * shut down the listener.
     */
    void shutdown();

    /**
     * make this listener to start listening for new units of work.
     */
    void start();

    /**
     * make this listener to stop listening for new units of work.
     */

    void stop();
}
