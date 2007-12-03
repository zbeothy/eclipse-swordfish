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

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;

/**
 * The Class ServiceStateNotification.
 */
public class ServiceStateNotification implements ServiceNotification, EntityStateNotification {

    /** The participant identity. */
    private UnifiedParticipantIdentity participantIdentity;

    /** The role. */
    private ParticipantRole role;

    /** The service name. */
    private QName serviceName;

    /** The state. */
    private EntityState state;

    /**
     * Instantiates a new service state notification.
     * 
     * @param participant
     *        the participant
     * @param role
     *        the role
     * @param serviceName
     *        the service name
     * @param state
     *        the state
     */
    public ServiceStateNotification(final UnifiedParticipantIdentity participant, final ParticipantRole role,
            final QName serviceName, final EntityState state) {
        this.participantIdentity = participant;
        this.role = role;
        this.serviceName = serviceName;
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
     * Gets the participant role.
     * 
     * @return the participant role
     */
    public ParticipantRole getParticipantRole() {
        return this.role;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ServiceNotification#getServiceName()
     */
    public QName getServiceName() {
        return this.serviceName;
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
