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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.messaging.ExchangeStage;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.ExchangePattern;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.InteractionStyle;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.utils.BeanInspector;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;

/**
 * Processing protocol for MessageExchange.
 * 
 */
public abstract class ExchangeJournal {

    /** The Constant log. */
    protected final static Log LOG = SBBLogFactory.getLog(ExchangeJournal.class);

    /** Comment for <code>finished</code>. */
    private boolean finished;

    /** The last event. */
    private EventType lastEvent;

    /** The current event. */
    private EventType currentEvent;

    /** The backend. */
    private MessagingMonitorBackend backend;

    /** The correlation ID. */
    private String correlationID;

    /** MessageID for the most recent message that was received for this journal. */
    private String messageID;

    /** The exchange pattern. */
    private ExchangePattern exchangePattern;

    /** The exchange state. */
    private ExchangeState exchangeState;

    /** The interaction style. */
    private InteractionStyle interactionStyle;

    /** The participant role. */
    private ParticipantRole participantRole;

    /** The participant id. */
    private UnifiedParticipantIdentity participantId;

    /** The service name. */
    private QName serviceName;

    /** The operation name. */
    private String operationName;

    /** The consumer policy id. */
    private String consumerPolicyId;

    /** The provider policy id. */
    private String providerPolicyId;

    /**
     * Timestamps for important processing events key: EventStage value: <code>LONG</code>
     * timestamp the stage was entered.
     */
    private HashMap times = new HashMap(4);

    /** List of already processed notifications (needed for tracking). */
    private ArrayList notifications = new ArrayList(6);

    /** The total processing time. */
    private Long totalProcessingTime;

    /** The backend processing time. */
    private Long backendProcessingTime;

    /**
     * Instantiates a new exchange journal.
     * 
     * @param scaffold
     *        the scaffold
     */
    protected ExchangeJournal(final ExchangeJournal scaffold) {
        this.participantRole = scaffold.participantRole;
        this.participantId = scaffold.participantId;
        this.lastEvent = null;
        this.correlationID = scaffold.correlationID;
        this.exchangePattern = scaffold.exchangePattern;
        this.exchangeState = scaffold.exchangeState;
        this.interactionStyle = scaffold.interactionStyle;
        this.serviceName = scaffold.serviceName;
        this.operationName = scaffold.operationName;
        this.consumerPolicyId = scaffold.consumerPolicyId;
        this.providerPolicyId = scaffold.providerPolicyId;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Created from scaffold for " + this.correlationID);
        }
    }

    /**
     * Instantiates a new exchange journal.
     * 
     * @param role
     *        the role
     * @param notification
     *        the notification
     */
    protected ExchangeJournal(final ParticipantRole role, final MessageProcessingNotification notification) {
        this.participantRole = role;
        this.participantId = notification.getParticipantIdentity();
        this.lastEvent = null;
        this.init(notification);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Created journal for MessageExchange" + notification.getCorrelationID());
            if (LOG.isTraceEnabled()) {
                LOG.trace(BeanInspector.beanToString(notification));
            }
        }
    }

    public MessagingMonitorBackend getBackend() {
        return this.backend;
    }

    /**
     * Gets the backend processing time.
     * 
     * @return the backend processing time
     */
    public Long getBackendProcessingTime() {
        if (null == this.backendProcessingTime) {
            Long out = (Long) this.times.get(ExchangeStage.HANDOFF);
            Long in = (Long) this.times.get(ExchangeStage.HANDBACK);
            if ((null != in) && (null != out)) {
                this.backendProcessingTime = new Long(in.longValue() - out.longValue());
            }
        }
        return this.backendProcessingTime;
    }

    /**
     * Gets the consumer policy id.
     * 
     * @return the consumer policy id
     */
    public String getConsumerPolicyId() {
        return this.consumerPolicyId;
    }

    /**
     * Gets the correlation ID.
     * 
     * @return the correlation ID
     */
    public String getCorrelationID() {
        return this.correlationID;
    }

    public EventType getCurrentEvent() {
        return this.currentEvent;
    }

    /**
     * Gets the exchange pattern.
     * 
     * @return the exchange pattern
     */
    public ExchangePattern getExchangePattern() {
        return this.exchangePattern;
    }

    /**
     * Gets the exchange state.
     * 
     * @return the exchange state
     */
    public ExchangeState getExchangeState() {
        return this.exchangeState;
    }

    /**
     * Gets the interaction style.
     * 
     * @return the interaction style
     */
    public InteractionStyle getInteractionStyle() {
        return this.interactionStyle;
    }

    public EventType getLastEvent() {
        return this.lastEvent;
    }

    /**
     * Gets the message ID.
     * 
     * @return the message ID
     */
    public String getMessageID() {
        return this.messageID;
    }

    /**
     * Gets the notifications.
     * 
     * @return the notifications
     */
    public ArrayList getNotifications() {
        return this.notifications;
    }

    /**
     * Gets the operation name.
     * 
     * @return the operation name
     */
    public String getOperationName() {
        return this.operationName;
    }

    /**
     * Gets the participant id.
     * 
     * @return the participant id
     */
    public UnifiedParticipantIdentity getParticipantId() {
        return this.participantId;
    }

    /**
     * Gets the participant role.
     * 
     * @return the participant role
     */
    public ParticipantRole getParticipantRole() {
        return this.participantRole;
    }

    /**
     * Gets the provider policy id.
     * 
     * @return the provider policy id
     */
    public String getProviderPolicyId() {
        return this.providerPolicyId;
    }

    /**
     * Gets the service name.
     * 
     * @return the service name
     */
    public QName getServiceName() {
        return this.serviceName;
    }

    /**
     * Gets the style.
     * 
     * @return the <code>InternalCommunicationStyle</code> of the operation this journal is about
     */
    public abstract InternalCommunicationStyle getStyle();

    public HashMap getTimes() {
        return this.times;
    }

    /**
     * Gets the timestamp.
     * 
     * @param stage
     *        the stage
     * 
     * @return the timestamp
     */
    public Long getTimestamp(final ExchangeStage stage) {
        return (Long) this.times.get(stage);
    }

    /**
     * Gets the total processing time.
     * 
     * @return the total processing time
     */
    public Long getTotalProcessingTime() {
        if (null == this.totalProcessingTime) {
            Long in = (Long) this.times.get(ExchangeStage.RECEIVED);
            Long out = (Long) this.times.get(ExchangeStage.DELIVERED);
            if ((null != in) && (null != out)) {
                this.totalProcessingTime = new Long(out.longValue() - in.longValue());
            }
        }
        return this.totalProcessingTime;
    }

    /**
     * Checks if is finished.
     * 
     * @return <code>true</code> if final event has been processed for this exchange
     */
    public boolean isFinished() {
        return this.finished;
    }

    /**
     * replays already received notifications into <code>backend</code>.
     * 
     * @param backend
     *        <code>MessagingMonitorBackend</code> to replay notifications to
     */
    public abstract void replay(MessagingMonitorBackend backend);

    /**
     * Sets the backend.
     * 
     * @param backend -
     *        backend that processes events recognized by this protocol
     */
    public void setBackend(final MessagingMonitorBackend backend) {
        this.backend = backend;
    }

    /**
     * Sets the created timestamp.
     * 
     * @param timestamp
     *        the new created timestamp
     */
    public abstract void setCreatedTimestamp(Long timestamp);

    public void setCurrentEvent(final EventType currentEvent) {
        this.currentEvent = currentEvent;
    }

    public void setLastEvent(final EventType lastEvent) {
        this.lastEvent = lastEvent;
    }

    /**
     * Sets the notifications.
     * 
     * @param notifications
     *        the new notifications
     */
    public void setNotifications(final ArrayList notifications) {
        this.notifications = notifications;
    }

    public void setParticipantRole(final ParticipantRole participantRole) {
        this.participantRole = participantRole;
    }

    /**
     * Sets the related timestamp.
     * 
     * @param timestamp
     *        the new related timestamp
     */
    public abstract void setRelatedTimestamp(Long timestamp);

    public void setTimes(final HashMap times) {
        this.times = times;
    }

    /**
     * MessageExchange is aborted by this participant due to application failure.
     * 
     * @param notification
     *        the notification
     */
    protected void handleAbortedApplication(final MessageProcessingNotification notification) {
        this.setFinished(true);
        this.backend.handleAbortedApplication(this);
    }

    /**
     * MessageExchange is aborted by this participant due to network problems.
     * 
     * @param notification
     *        the notification
     */
    protected void handleAbortedNet(final MessageProcessingNotification notification) {
        this.setFinished(true);
        this.backend.handleAbortedNet(this);
    }

    /**
     * MessageExchange is finished as expected by this participant.
     * 
     * @param notification
     *        the notification
     */
    protected void handleCompleted(final MessageProcessingNotification notification) {
        this.setFinished(true);
        this.times.put(ExchangeStage.COMPLETED, new Long(notification.getTimestamp()));
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.correlationID + " set completed timestamp to " + notification.getTimestamp());
        }
        this.backend.handleCompleted(this);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Completed journal for MessageExchange " + this.correlationID);
        }
    }

    /**
     * MessageExchange delivered back to frontend If this is a consumer, the frontend is the
     * application. If this is a provider, the frontend is the net.
     * 
     * @param notification
     *        the notification
     */
    protected void handleDelivered(final MessageProcessingNotification notification) {
        this.times.put(ExchangeStage.DELIVERED, new Long(notification.getTimestamp()));
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.correlationID + " set delivered timestamp to " + notification.getTimestamp());
        }
        this.backend.handleDelivered(this);
    }

    /**
     * MessageExchange is finished at local participant, but will continue at another.
     * 
     * @param notification
     *        the notification
     */
    protected void handleDoneLocal(final MessageProcessingNotification notification) {
        this.setFinished(true);
        this.times.put(ExchangeStage.DONE_LOCAL, new Long(notification.getTimestamp()));
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.correlationID + " set done_local timestamp to " + notification.getTimestamp());
        }
        this.backend.handleDoneLocal(this);
    }

    /**
     * Handle event.
     * 
     * @param notification
     *        the notification
     */
    protected void handleEvent(final MessageProcessingNotification notification) {
        this.backend.handleUnspecifiedEvent(this);
    }

    /**
     * MessageExchange received back from backend If this is a consumer, the backend is the net. If
     * this is a provider, the backend is the application.
     * 
     * @param notification
     *        the notification
     */
    protected void handleHandback(final MessageProcessingNotification notification) {
        this.times.put(ExchangeStage.HANDBACK, new Long(notification.getTimestamp()));
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.correlationID + " set handback timestamp to " + notification.getTimestamp());
        }
        this.backend.handleHandback(this);
    }

    /**
     * MessageExchange handed off to backend If this is a consumer, the backend is the net. If this
     * is a provider, the backend is the application.
     * 
     * @param notification
     *        the notification
     */
    protected void handleHandoff(final MessageProcessingNotification notification) {
        this.times.put(ExchangeStage.HANDOFF, new Long(notification.getTimestamp()));
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.correlationID + " set handoff timestamp to " + notification.getTimestamp());
        }
        this.backend.handleHandoff(this);
    }

    /**
     * MessageExchange received from frontend If this is a consumer, the frontend is the
     * application. If this is a provider, the frontend is the net.
     * 
     * @param notification
     *        the notification
     */
    protected void handleReceived(final MessageProcessingNotification notification) {
        this.times.put(ExchangeStage.RECEIVED, new Long(notification.getTimestamp()));
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.correlationID + " set received timestamp to " + notification.getTimestamp());
        }
        this.backend.handleReceived(this);
    }

    /**
     * Handle sequence error.
     * 
     * @param notification
     *        the notification
     */
    protected void handleSequenceError(final MessageProcessingNotification notification) {
        if (LOG.isWarnEnabled()) {
            LOG.warn("Unexpected internal notification for MessageExchange " + notification.getCorrelationID() + ": " + "after "
                    + String.valueOf(this.lastEvent) + " received " + String.valueOf(notification.getEventType())
                    + "\nMessage tracking results might be inaccurate for this message!");
        }
    }

    /**
     * MessageExchange is being initiated at local participant. Give backends a chance to handle
     * this event.
     * 
     * @param notification
     *        the notification
     */
    protected void handleStarted(final MessageProcessingNotification notification) {
        this.times.put(ExchangeStage.RECEIVED, new Long(notification.getTimestamp()));
        if (LOG.isDebugEnabled()) {
            LOG.debug(this.correlationID + " set received timestamp to " + notification.getTimestamp());
        }
        this.backend.handleStarted(this);
    }

    /**
     * Handle a new notification for this exchange.
     * 
     * @param notification
     *        to handle
     */
    protected void process(final MessageProcessingNotification notification) {
        // TODO: check if parameters match
        this.messageID = notification.getMessageID();
        this.currentEvent = notification.getEventType();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got notification for " + this.messageID + " (" + this.currentEvent.toString() + ")");
            LOG.debug(this.correlationID + " set current timestamp to " + notification.getTimestamp());
        }
        this.times.put(ExchangeStage.CURRENT, new Long(notification.getTimestamp()));
        if (null != this.notifications) {
            this.notifications.add(notification);
            if ((LOG.isTraceEnabled()) && (6 < this.notifications.size())) {
                LOG.trace("Stored " + this.notifications.size() + " notifications");
            }
        }
    }

    /**
     * replays already received notifications into <code>backend</code>.
     * 
     * @param msgBackend
     *        <code>MessagingMonitorBackend</code> to replay notifications to
     * @param tempJournal
     *        the temp journal
     */
    protected void replay(final MessagingMonitorBackend msgBackend, final ExchangeJournal tempJournal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Start replaying notifications for MessageExchange " + tempJournal.correlationID);
        }
        ArrayList temp = this.notifications;
        this.notifications = null;
        tempJournal.setBackend(msgBackend);
        Iterator iter = temp.iterator();
        while (iter.hasNext()) {
            MessageProcessingNotification notification = (MessageProcessingNotification) iter.next();
            tempJournal.process(notification);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Finished replaying notifications for MessageExchange " + tempJournal.correlationID);
        }
    }

    /**
     * Sets the finished.
     * 
     * @param val
     *        the new finished
     */
    protected void setFinished(final boolean val) {
        this.finished = val;
    }

    /**
     * Initialize protocol with attributes from initial notification.
     * 
     * @param notification
     *        the notification
     */
    private void init(final MessageProcessingNotification notification) {
        this.correlationID = notification.getCorrelationID();
        this.exchangePattern = notification.getExchangePattern();
        this.exchangeState = notification.getExchangeState();
        this.interactionStyle = notification.getInteractionStyle();
        this.participantRole = notification.getParticipantRole();
        this.serviceName = notification.getServiceName();
        this.operationName = notification.getOperationName();
        this.consumerPolicyId = notification.getConsumerPolicyID();
        this.providerPolicyId = notification.getProviderPolicyID();
    }

}
