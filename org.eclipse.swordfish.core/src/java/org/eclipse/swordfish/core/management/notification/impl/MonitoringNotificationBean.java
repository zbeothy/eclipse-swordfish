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
import org.eclipse.swordfish.core.management.notification.MonitoringNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.utils.HeaderUtil;

/**
 * The Class MonitoringNotificationBean.
 */
public class MonitoringNotificationBean implements MonitoringNotification {

    /** The correlation ID. */
    private String correlationID;

    /** The operation name. */
    private String operationName;

    /** The service name. */
    private QName serviceName;

    /** The max response time. */
    private int maxResponseTime;

    /** The participant role. */
    private ParticipantRole participantRole;

    /** The participant identity. */
    private UnifiedParticipantIdentity participantIdentity;

    /** Timestamp for message creation (APP_IN_PRE on consumer side). */
    private long createdTimestamp;

    /** Timestamp for message handoff to provider application (APP_OUT_PRE on provider side). */
    private long relatedTimestamp;

    /**
     * Instantiates a new monitoring notification bean.
     * 
     * @param exchange
     *        the exchange
     */
    public MonitoringNotificationBean(final MessageExchange exchange) {
        this.serviceName = exchange.getService();
        QName operation = exchange.getOperation();
        this.operationName = operation.getLocalPart();
        CallContextExtension callContextExtension = HeaderUtil.getCallContextExtension(exchange);
        this.correlationID = callContextExtension.getCorrelationID();
        this.createdTimestamp = callContextExtension.getCreatedTimestamp();
        this.relatedTimestamp = callContextExtension.getRelatedTimestamp();
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
     * @see org.eclipse.swordfish.core.management.notification.MonitoringNotification#getCreatedTimestamp()
     */
    public long getCreatedTimestamp() {
        return this.createdTimestamp;
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
     * @see org.eclipse.swordfish.core.management.notification.MonitoringNotification#getMaxResponseTime()
     */
    public int getMaxResponseTime() {
        return this.maxResponseTime;
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
     * @see org.eclipse.swordfish.core.management.notification.MonitoringNotification#getRelatedTimestamp()
     */
    public long getRelatedTimestamp() {
        return this.relatedTimestamp;
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
     * Sets the max response time.
     * 
     * @param maxResponseTime
     *        the new max response time
     */
    public void setMaxResponseTime(final int maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
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
     * @param role
     *        the new participant role
     */
    public void setParticipantRole(final ParticipantRole role) {
        this.participantRole = role;
    }

}
