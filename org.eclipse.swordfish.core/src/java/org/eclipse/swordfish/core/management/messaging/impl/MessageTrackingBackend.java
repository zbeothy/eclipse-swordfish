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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.messages.TrackingMessage;
import org.eclipse.swordfish.core.management.messaging.ExchangeStage;
import org.eclipse.swordfish.core.management.notification.TrackingLevel;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.core.management.operations.impl.OperationalMessageRecord;

/**
 * provides tracking of messages.
 * 
 */
public class MessageTrackingBackend implements MessagingMonitorBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(MessageTrackingBackend.class);

    /** The operations. */
    private Operations operations;

    /** The tracking level. */
    private TrackingLevel trackingLevel;

    /**
     * Destroy.
     */
    public void destroy() {
        this.operations = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedApplication(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedApplication(final ExchangeJournal journal) {
        if ((null != this.trackingLevel) && (this.trackingLevel.isIncluded(TrackingLevel.OPERATION))) {
            Long timestamp = journal.getTimestamp(ExchangeStage.CURRENT);
            String date = this.timestamp2Date(timestamp.longValue());
            String correlationId = journal.getCorrelationID();
            String messageId = journal.getMessageID();
            String operation = journal.getOperationName();
            String service = journal.getServiceName().toString();
            String role = String.valueOf(journal.getParticipantRole());
            Object[] args = {correlationId, service, operation, messageId, date, "app", role};
            this.operations.notify(TrackingMessage.TRACKING_ABORTED, args);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedNet(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedNet(final ExchangeJournal journal) {
        if ((null != this.trackingLevel) && (this.trackingLevel.isIncluded(TrackingLevel.OPERATION))) {
            Long timestamp = journal.getTimestamp(ExchangeStage.CURRENT);
            String date = this.timestamp2Date(timestamp.longValue());
            String correlationId = journal.getCorrelationID();
            String messageId = journal.getMessageID();
            String operation = journal.getOperationName();
            String service = journal.getServiceName().toString();
            String role = String.valueOf(journal.getParticipantRole());
            Object[] args = {correlationId, service, operation, messageId, date, "net", role};
            this.operations.notify(TrackingMessage.TRACKING_ABORTED, args);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleCompleted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleCompleted(final ExchangeJournal journal) {
        if ((null != this.trackingLevel) && (this.trackingLevel.isIncluded(TrackingLevel.OPERATION))) {
            Long timestamp = journal.getTimestamp(ExchangeStage.COMPLETED);
            String date = this.timestamp2Date(timestamp.longValue());
            Long duration = journal.getTotalProcessingTime();
            String correlationId = journal.getCorrelationID();
            String operation = journal.getOperationName();
            String service = journal.getServiceName().toString();
            Object[] args = {correlationId, service, operation, date, duration};
            this.operations.notify(TrackingMessage.TRACKING_FINISHED, args);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDelivered(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDelivered(final ExchangeJournal journal) {
        Long timestamp = journal.getTimestamp(ExchangeStage.DELIVERED);
        TrackingLevel effectiveLevel = TrackingLevel.TRACE;
        this.logActive(journal, timestamp, effectiveLevel, String.valueOf(journal.getCurrentEvent()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDoneLocal(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDoneLocal(final ExchangeJournal journal) {
        if ((null != this.trackingLevel) && (this.trackingLevel.isIncluded(TrackingLevel.TRACE))) {
            String role = String.valueOf(journal.getParticipantRole());
            String correlationId = journal.getCorrelationID();
            Long timestamp = journal.getTimestamp(ExchangeStage.DONE_LOCAL);
            String date = this.timestamp2Date(timestamp.longValue());
            Object[] args = {correlationId, role, date};
            this.operations.notify(TrackingMessage.DONE_LOCAL, args);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandback(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandback(final ExchangeJournal journal) {
        Long timestamp = journal.getTimestamp(ExchangeStage.HANDBACK);
        TrackingLevel effectiveLevel = TrackingLevel.DETAIL;
        this.logActive(journal, timestamp, effectiveLevel, String.valueOf(journal.getCurrentEvent()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandoff(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandoff(final ExchangeJournal journal) {
        Long timestamp = journal.getTimestamp(ExchangeStage.HANDOFF);
        TrackingLevel effectiveLevel = TrackingLevel.TRACE;
        this.logActive(journal, timestamp, effectiveLevel, String.valueOf(journal.getCurrentEvent()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleReceived(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleReceived(final ExchangeJournal journal) {
        Long timestamp = journal.getTimestamp(ExchangeStage.RECEIVED);
        TrackingLevel effectiveLevel = TrackingLevel.DETAIL;
        this.logActive(journal, timestamp, effectiveLevel, String.valueOf(journal.getCurrentEvent()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleStarted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleStarted(final ExchangeJournal journal) {
        if ((null != this.trackingLevel) && (this.trackingLevel.isIncluded(TrackingLevel.OPERATION))) {
            Long timestamp = journal.getTimestamp(ExchangeStage.RECEIVED);
            String date = "unknown";
            if (null != timestamp) {
                date = this.timestamp2Date(timestamp.longValue());
            }
            String correlationId = journal.getCorrelationID();
            String operation = journal.getOperationName();
            String service = journal.getServiceName().toString();
            String consumerPolicyId = journal.getConsumerPolicyId();
            String providerPolicyId = journal.getProviderPolicyId();
            Object[] args = {correlationId, service, operation, consumerPolicyId, providerPolicyId, date};
            this.operations.notify(TrackingMessage.TRACKING_METADATA, args);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleUnspecifiedEvent(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleUnspecifiedEvent(final ExchangeJournal journal) {
        /*
         * don't generate operational log messages - only local trace entries Long timestamp =
         * journal.getTimestamp(ExchangeStage.CURRENT); TrackingLevel effectiveLevel =
         * TrackingLevel.DETAIL; logActive(journal, timestamp, effectiveLevel,
         * String.valueOf(journal.currentEvent));
         */
        if (LOG.isInfoEnabled()) {
            UnifiedParticipantIdentity participant = journal.getParticipantId();
            Long timestamp = journal.getTimestamp(ExchangeStage.CURRENT);
            Object[] args = this.extractParams(journal, timestamp, "internal");
            OperationalMessageRecord record = new OperationalMessageRecord(participant, TrackingMessage.TRACKING_ACTIVE, args);
            String msg = record.getMessage();
            LOG.info(msg);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#setOperations(org.eclipse.swordfish.core.management.operations.Operations)
     */
    public void setOperations(final Operations ops) {
        this.operations = ops;
    }

    /**
     * Sets the tracking level.
     * 
     * @param level
     *        the new tracking level
     */
    public void setTrackingLevel(final TrackingLevel level) {
        this.trackingLevel = level;
    }

    /**
     * Extracts the parameters for active tracking messages from an ExchangeJournal.
     * 
     * @param journal
     *        the journal
     * @param timestamp
     *        the timestamp
     * @param event
     *        the event
     * 
     * @return the object[]
     */
    private Object[] extractParams(final ExchangeJournal journal, final Long timestamp, final String event) {
        String correlationId = journal.getCorrelationID();
        String messageId = journal.getMessageID();
        String role = String.valueOf(journal.getParticipantRole());
        String date = this.timestamp2Date(timestamp.longValue());
        Object[] args = {correlationId, messageId, role, date, event};
        return args;
    }

    /**
     * Log active.
     * 
     * @param journal
     *        the journal
     * @param timestamp
     *        the timestamp
     * @param effectiveLevel
     *        the effective level
     * @param event
     *        the event
     */
    private void logActive(final ExchangeJournal journal, final Long timestamp, final TrackingLevel effectiveLevel,
            final String event) {
        if ((null != this.trackingLevel) && (this.trackingLevel.isIncluded(effectiveLevel))) {
            Object[] args = this.extractParams(journal, timestamp, event);
            this.operations.notify(TrackingMessage.TRACKING_ACTIVE, args);
        }
    }

    /**
     * Timestamp2 date.
     * 
     * @param timestamp
     *        the timestamp
     * 
     * @return the string
     */
    private String timestamp2Date(final long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat iso8601UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        iso8601UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        String ret = iso8601UTC.format(date);
        return ret;
    }

}
