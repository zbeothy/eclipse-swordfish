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
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;

/**
 * The Class OperationStateNotification.
 */
public class OperationStateNotification implements OperationNotification, EntityStateNotification {

    /** The state. */
    private EntityState state;

    /** The service name. */
    private QName serviceName;

    /** The operation name. */
    private String operationName;

    /** The role. */
    private ParticipantRole role;

    /** The participant identity. */
    private UnifiedParticipantIdentity participantIdentity;

    /** The style. */
    private InternalCommunicationStyle style;

    /**
     * Instantiates a new operation state notification.
     * 
     * @param state
     *        the state
     * @param op
     *        the op
     * @param participantIdentity
     *        the participant identity
     * @param role
     *        the role
     */
    public OperationStateNotification(final EntityState state, final InternalOperation op,
            final UnifiedParticipantIdentity participantIdentity, final ParticipantRole role) {
        this.state = state;
        this.serviceName = op.getService().getName();
        this.operationName = op.getName();
        this.style = op.getCommunicationStyle();
        this.role = role;
        this.participantIdentity = participantIdentity;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotification#getEventType()
     */
    public EventType getEventType() {
        return this.state == EntityState.ADDED ? EventType.OPERATION_ADDED : EventType.OPERATION_REMOVED;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.OperationNotification#getOperationName()
     */
    public String getOperationName() {
        return this.operationName;
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

    /**
     * Gets the style.
     * 
     * @return the style
     */
    public InternalCommunicationStyle getStyle() {
        return this.style;
    }
}
