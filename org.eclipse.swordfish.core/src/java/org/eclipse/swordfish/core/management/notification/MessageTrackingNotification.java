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
 * Notification about tracking level for a message exchange.
 * 
 */
public interface MessageTrackingNotification extends ExchangeNotification {

    /**
     * Gets the tracking level.
     * 
     * @return the <code>TrackingLevel</code> that should be used for this operation
     */
    TrackingLevel getTrackingLevel();

}
