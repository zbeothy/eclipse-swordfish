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
package org.eclipse.swordfish.core.management.notification;

/**
 * Notification listener interface for notifications that inform management about relevant events.
 * <br/> Currently used only for message processing notifications, might be expanded later
 * 
 */
public interface ManagementNotificationListener {

    /**
     * End notification listener activities so that it can be safely destroyed.
     */
    void deactivate();

    /**
     * Checks if is active.
     * 
     * @return <code>true</code> if this listener is processing notifications <code>false</code>
     *         otherwise
     */
    boolean isActive();

    /**
     * use this to publish message processing notifications <br/>the actual processing of the events
     * is decoupled, so you can call this method without performance penalty.
     * 
     * @param notification
     *        about messaging event to be published
     */
    void sendNotification(ManagementNotification notification);
}
