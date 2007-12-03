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
package org.eclipse.swordfish.core.management.messaging.impl;

import javax.jbi.messaging.MessageExchange;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.ExchangePattern;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.InteractionStyle;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * The Class MessageProcessingNotificationImpl.
 */
public class MessageProcessingNotificationImpl implements MessageProcessingNotification {

    /** The message ID. */
    private String messageID = null;

    /** The correlation ID. */
    private String correlationID = null;;

    /** The service name. */
    private QName serviceName = null;

    /** The operation name. */
    private String operationName = null;;

    /** The exchange state. */
    private ExchangeState exchangeState = null;

    /** The event type. */
    private EventType eventType = null;

    /** The timestamp. */
    private long timestamp = 0;

    /** The exchange pattern. */
    private ExchangePattern exchangePattern = null;

    /** The interaction style. */
    private InteractionStyle interactionStyle = null;

    /** The reporter. */
    private Object reporter = null;

    /** The participant identity. */
    private UnifiedParticipantIdentity participantIdentity;

    /** The provider policy ID. */
    private String providerPolicyID = "unknown";

    /** The consumer policy ID. */
    private String consumerPolicyID = "unknown";

    /**
     * Instantiates a new message processing notification impl.
     * 
     * @param event
     *        the event
     * @param exchange
     *        the exchange
     * @param participantIdentity
     *        the participant identity
     */
    MessageProcessingNotificationImpl(final EventType event, final MessageExchange exchange,
            final UnifiedParticipantIdentity participantIdentity) {

        this.serviceName = exchange.getService();
        this.operationName = exchange.getOperation().getLocalPart();
        this.eventType = event;
        this.participantIdentity = participantIdentity;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getConsumerPolicyID()
     */
    public String getConsumerPolicyID() {
        return this.consumerPolicyID;
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
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getExchangePattern()
     */
    public ExchangePattern getExchangePattern() {
        return this.exchangePattern;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getExchangeState()
     */
    public ExchangeState getExchangeState() {
        return this.exchangeState;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getInteractionStyle()
     */
    public InteractionStyle getInteractionStyle() {
        return this.interactionStyle;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getMessageID()
     */
    public String getMessageID() {
        return this.messageID;
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
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getProviderPolicyID()
     */
    public String getProviderPolicyID() {
        return this.providerPolicyID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getReporter()
     */
    public Object getReporter() {
        return this.reporter;
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
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getTimestamp()
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Sets the consumer policy ID.
     * 
     * @param consumerPolicyID
     *        the new consumer policy ID
     */
    public void setConsumerPolicyID(final String consumerPolicyID) {
        this.consumerPolicyID = consumerPolicyID;
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
     * Sets the provider policy ID.
     * 
     * @param providerPolicyID
     *        the new provider policy ID
     */
    public void setProviderPolicyID(final String providerPolicyID) {
        this.providerPolicyID = providerPolicyID;
    }

}
