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

import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;

/**
 * Common interface for management notifications.
 * 
 */
public interface ManagementNotification {

    /**
     * Gets the event type.
     * 
     * @return type of event that led to creation of notification
     */
    EventType getEventType();

    /**
     * Gets the participant identity.
     * 
     * @return the <code>InternalParticipantIdentity</code> of the participant who processes the
     *         message
     */
    UnifiedParticipantIdentity getParticipantIdentity();

}
