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
package org.eclipse.swordfish.core.management.notification.impl;

import javax.jbi.messaging.MessageExchange;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.MessageTrackingNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.notification.TrackingLevel;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.utils.HeaderUtil;

/**
 * The Class MessageTrackingNotificationBean.
 */
public class MessageTrackingNotificationBean implements MessageTrackingNotification {

    /** The tracking level. */
    private TrackingLevel trackingLevel;

    /** The participant role. */
    private ParticipantRole participantRole;

    /** The event type. */
    private EventType eventType;

    /** The service name. */
    private QName serviceName;

    /** The operation name. */
    private String operationName;

    /** The correlation ID. */
    private String correlationID;

    /** The participant identity. */
    private UnifiedParticipantIdentity participantIdentity;

    /**
     * Instantiates a new message tracking notification bean.
     * 
     * @param exchange
     *        the exchange
     */
    public MessageTrackingNotificationBean(final MessageExchange exchange) {
        this.eventType = EventType.INTERNAL_PRE;
        this.serviceName = exchange.getService();
        this.operationName = exchange.getOperation().getLocalPart();
        CallContextExtension callContextExtension = HeaderUtil.getCallContextExtension(exchange);
        this.correlationID = callContextExtension.getCorrelationID();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ExchangeNotification#getCorrelationID()
     */
    public String getCorrelationID() {
        return this.correlationID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotification#getEventType()
     */
    public EventType getEventType() {
        EventType ret = this.eventType;
        if (null == ret) {
            ret = EventType.UNKNOWN;
        }
        return ret;
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
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.ExchangeNotification#getParticipantRole()
     */
    public ParticipantRole getParticipantRole() {
        return this.participantRole;
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
     * @see org.eclipse.swordfish.core.management.notification.MessageTrackingNotification#getTrackingLevel()
     */
    public TrackingLevel getTrackingLevel() {
        return this.trackingLevel;
    }

    /**
     * Sets the participant identity.
     * 
     * @param participantIdentity
     *        the new participant identity
     */
    public void setParticipantIdentity(final UnifiedParticipantIdentity participantIdentity) {
        this.participantIdentity = participantIdentity;
    }

    /**
     * Sets the participant role.
     * 
     * @param participantRole
     *        the new participant role
     */
    public void setParticipantRole(final ParticipantRole participantRole) {
        this.participantRole = participantRole;
    }

    /**
     * Sets the tracking level.
     * 
     * @param trackingLevel
     *        the new tracking level
     */
    public void setTrackingLevel(final TrackingLevel trackingLevel) {
        this.trackingLevel = trackingLevel;
    }

}
