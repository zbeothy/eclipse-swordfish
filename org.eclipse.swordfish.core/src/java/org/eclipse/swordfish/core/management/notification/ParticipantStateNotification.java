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
 * The Class ParticipantStateNotification.
 */
public class ParticipantStateNotification implements ManagementNotification, EntityStateNotification {

    /** The participant identity. */
    private UnifiedParticipantIdentity participantIdentity;

    /** The state. */
    private EntityState state;

    /**
     * Instantiates a new participant state notification.
     * 
     * @param id
     *        the id
     * @param state
     *        the state
     */
    public ParticipantStateNotification(final UnifiedParticipantIdentity id, final EntityState state) {
        this.participantIdentity = id;
        this.state = state;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotification#getEventType()
     */
    public EventType getEventType() {
        return EventType.INTERNAL_PRE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotification#getParticipantIdentity()
     */
    public UnifiedParticipantIdentity getParticipantIdentity() {
        return this.participantIdentity;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.EntityStateNotification#getState()
     */
    public EntityState getState() {
        return this.state;
    }

}
