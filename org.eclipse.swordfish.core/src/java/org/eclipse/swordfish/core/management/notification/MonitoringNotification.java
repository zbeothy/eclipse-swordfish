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
 * Notification that informs the messaging monitoring system about response time monitoring
 * requirements.
 * 
 */
public interface MonitoringNotification extends ExchangeNotification {

    /**
     * Gets the created timestamp.
     * 
     * @return the timestamp (as returned by System.getCurrentTimeMillis) of the message creation
     *         (APP_IN_PRE on consumer side)
     */
    long getCreatedTimestamp();

    /**
     * Gets the max response time.
     * 
     * @return the maximum allowed response time in milliseconds
     */
    int getMaxResponseTime();

    /**
     * Gets the related timestamp.
     * 
     * @return the timestamp (as returned by System.getCurrentTimeMillis) of message handoff to
     *         provider app (APP_OUT_PRE on provider side)
     */
    long getRelatedTimestamp();

}
