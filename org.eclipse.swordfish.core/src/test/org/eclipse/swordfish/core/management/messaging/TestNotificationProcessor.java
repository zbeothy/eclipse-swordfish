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
package org.eclipse.swordfish.core.management.messaging;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swordfish.core.management.notification.ManagementNotification;
import org.eclipse.swordfish.core.management.notification.NotificationProcessor;

/**
 * Dummy implementation of <code>NotificationProcessor</code> that implements only the
 * functionality needed for testing.
 * 
 */
public class TestNotificationProcessor implements NotificationProcessor {

    /** The notifications. */
    ArrayList notifications;

    /**
     * Instantiates a new test notification processor.
     */
    public TestNotificationProcessor() {
        this.notifications = new ArrayList();
    }

    /**
     * Gets the count.
     * 
     * @return the count
     */
    public int getCount() {
        return this.notifications.size();
    }

    /**
     * Gets the notifications.
     * 
     * @return the notifications
     */
    public List getNotifications() {
        return this.notifications;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.NotificationProcessor#process(org.eclipse.swordfish.core.management.notification.ManagementNotification)
     */
    public void process(final ManagementNotification notification) {
        this.notifications.add(notification);
    }

}
