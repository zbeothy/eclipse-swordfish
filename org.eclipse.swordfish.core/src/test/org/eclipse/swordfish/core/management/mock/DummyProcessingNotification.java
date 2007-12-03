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
package org.eclipse.swordfish.core.management.mock;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.ExchangePattern;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.InteractionStyle;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * Dummy implementation of <code>MessageProcessingNotification</code> that implements only the
 * functionality needed for testing.
 * 
 */
public class DummyProcessingNotification implements MessageProcessingNotification, Cloneable {

    /** The message ID. */
    String messageID;

    /** The correlation ID. */
    String correlationID;

    /** The service name. */
    QName serviceName;

    /** The operation name. */
    String operationName;

    /** The exchange state. */
    ExchangeState exchangeState;

    /** The event type. */
    EventType eventType;

    /** The timestamp. */
    long timestamp;

    /** The exchange pattern. */
    ExchangePattern exchangePattern;

    /** The interaction style. */
    InteractionStyle interactionStyle;

    /** The reporter. */
    Object reporter;

    /** The participant role. */
    ParticipantRole participantRole;

    /** The provider policy ID. */
    private String providerPolicyID = "unknown";

    /** The consumer policy ID. */
    private String consumerPolicyID = "unknown";

    /** The participant identity. */
    private UnifiedParticipantIdentity participantIdentity;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        Object ret = null;
        try {
            ret = super.clone();
        } catch (CloneNotSupportedException e) {
            // should not happen
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getConsumerPolicyID()
     */
    public String getConsumerPolicyID() {
        return this.consumerPolicyID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.ExchangeNotification#getCorrelationID()
     */
    public String getCorrelationID() {
        return this.correlationID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotification#getEventType()
     */
    public EventType getEventType() {
        return this.eventType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getExchangePattern()
     */
    public ExchangePattern getExchangePattern() {
        return this.exchangePattern;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getExchangeState()
     */
    public ExchangeState getExchangeState() {
        return this.exchangeState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getInteractionStyle()
     */
    public InteractionStyle getInteractionStyle() {
        return this.interactionStyle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getMessageID()
     */
    public String getMessageID() {
        return this.messageID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.OperationNotification#getOperationName()
     */
    public String getOperationName() {
        return this.operationName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.ManagementNotification#getParticipantIdentity()
     */
    public UnifiedParticipantIdentity getParticipantIdentity() {
        return this.participantIdentity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.ExchangeNotification#getParticipantRole()
     */
    public ParticipantRole getParticipantRole() {
        return this.participantRole;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getProviderPolicyID()
     */
    public String getProviderPolicyID() {
        return this.providerPolicyID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.MessageProcessingNotification#getReporter()
     */
    public Object getReporter() {
        return this.reporter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.notification.ServiceNotification#getServiceName()
     */
    public QName getServiceName() {
        return this.serviceName;
    }

    /*
     * (non-Javadoc)
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
     * Sets the correlation ID.
     * 
     * @param corellationID
     *        the new correlation ID
     */
    public void setCorrelationID(final String corellationID) {
        this.correlationID = corellationID;
    }

    /**
     * Sets the event type.
     * 
     * @param eventType
     *        the new event type
     */
    public void setEventType(final EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Sets the exchange pattern.
     * 
     * @param exchangePattern
     *        the new exchange pattern
     */
    public void setExchangePattern(final ExchangePattern exchangePattern) {
        this.exchangePattern = exchangePattern;
    }

    /**
     * Sets the exchange state.
     * 
     * @param exchangeState
     *        the new exchange state
     */
    public void setExchangeState(final ExchangeState exchangeState) {
        this.exchangeState = exchangeState;
    }

    /**
     * Sets the interaction style.
     * 
     * @param interactionStyle
     *        the new interaction style
     */
    public void setInteractionStyle(final InteractionStyle interactionStyle) {
        this.interactionStyle = interactionStyle;
    }

    /**
     * Sets the message ID.
     * 
     * @param messageID
     *        the new message ID
     */
    public void setMessageID(final String messageID) {
        this.messageID = messageID;
    }

    /**
     * Sets the operation name.
     * 
     * @param operationName
     *        the new operation name
     */
    public void setOperationName(final String operationName) {
        this.operationName = operationName;
    }

    /**
     * Sets the participant identity.
     * 
     * @param id
     *        the new participant identity
     */
    public void setParticipantIdentity(final UnifiedParticipantIdentity id) {
        this.participantIdentity = id;
    }

    /**
     * Sets the participant role.
     * 
     * @param aRole
     *        the new participant role
     */
    public void setParticipantRole(final ParticipantRole aRole) {
        this.participantRole = aRole;
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

    /**
     * Sets the reporter.
     * 
     * @param reporter
     *        the new reporter
     */
    public void setReporter(final Object reporter) {
        this.reporter = reporter;
    }

    /**
     * Sets the service name.
     * 
     * @param serviceName
     *        the new service name
     */
    public void setServiceName(final QName serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Sets the timestamp.
     * 
     * @param timestamp
     *        the new timestamp
     */
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

}
