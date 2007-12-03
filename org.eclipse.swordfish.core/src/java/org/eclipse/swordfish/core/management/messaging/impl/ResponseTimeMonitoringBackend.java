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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.messages.TrackingMessage;
import org.eclipse.swordfish.core.management.messaging.ExchangeStage;
import org.eclipse.swordfish.core.management.notification.ManagementNotificationListener;
import org.eclipse.swordfish.core.management.notification.MonitoringNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.notification.impl.ExpiryNotificationBean;
import org.eclipse.swordfish.core.management.operations.Operations;

/**
 * The Class ResponseTimeMonitoringBackend.
 */
public class ResponseTimeMonitoringBackend implements MessagingMonitorBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ResponseTimeMonitoringBackend.class);

    /** The agreed maximum time time for the message exchange in milliseconds. */
    private int maxResponseTime;

    /** InternalOperations bean used for sending management notifications. */
    private Operations operations;

    /** <code>java.util.Timer</code> instance used by all management components */
    private Timer managementTimer;

    /** Timestamp for start of messurement period. */
    private Long startTime;

    /** notification listener for all management notifications. */
    private ManagementNotificationListener listener;

    /**
     * TimerTask that will send an expiry notification when <code>endtime</code> occurs <br/>This
     * mechanism is necessary since it is possible that the exchange is already completed, but the
     * corresponding notification is still in the queue for internal management notificatations at
     * the time the task is executed. Since the queue is processed sequentially, it is guaranteed
     * that the exchange has exceeded the agreed response time if the expiry notification is
     * received from the queue before a notification about completion of the exchange.
     */
    private TimerTask task;

    /** indicates whether a management notification has already been generated for a late message. */
    private boolean notified;

    /** The finished. */
    private boolean finished;

    /** The initialized. */
    private boolean initialized = false;

    /**
     * Destroy.
     */
    public void destroy() {
        if (null != this.task) {
            this.task.cancel();
        }
        this.operations = null;
        this.managementTimer = null;
        this.listener = null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroyed");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedApplication(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public synchronized void handleAbortedApplication(final ExchangeJournal journal) {
        this.reportAbortedExchange(journal, "aborted(app)");

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedNet(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public synchronized void handleAbortedNet(final ExchangeJournal journal) {
        this.reportAbortedExchange(journal, "aborted(net)");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleCompleted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleCompleted(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Completed " + journal.getCorrelationID() + " for " + journal.getParticipantRole());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDelivered(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDelivered(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Delivered " + journal.getCorrelationID() + " for " + journal.getParticipantRole());
        }
        if (ParticipantRole.CONSUMER.equals(journal.getParticipantRole())) {
            synchronized (this) {
                Long responseTime = journal.getTotalProcessingTime();
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Delivery of exchange " + journal.getCorrelationID());
                }
                this.checkResponseTime(journal, responseTime);
                this.finished = true;
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDoneLocal(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDoneLocal(final ExchangeJournal journal) {
        // no-op
    }

    /**
     * Handle expiry notification.
     * 
     * @param notification
     *        the notification
     */
    public synchronized void handleExpiryNotification(final ExpiryNotificationBean notification) {
        if ((this.notified) || (this.finished)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignoring ExpiryNotification for " + notification.getCorrelationID() + " since notified=" + this.notified
                        + " and finished=" + this.finished);
            }
            return;
        }
        String correlationID = notification.getCorrelationID();
        String serviceName = notification.getServiceName().toString();
        String operationName = notification.getOperationName();
        String role = String.valueOf(notification.getParticipantRole());
        long duration = notification.getReportedTimestamp() - this.startTime.longValue();
        LOG.debug("Handling expiry notification for " + role + "/" + correlationID + " agreed " + this.maxResponseTime
                + " duration " + duration + " at start + " + (System.currentTimeMillis() - this.startTime.longValue()));
        this.reportExchange("ongoing", new Long(duration), correlationID, serviceName, operationName, role);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandback(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandback(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handback " + journal.getCorrelationID() + " for " + journal.getParticipantRole());
        }
        if (ParticipantRole.PROVIDER.equals(journal.getParticipantRole())) {
            synchronized (this) {
                Long responseTime = journal.getBackendProcessingTime();
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Handback of exchange " + journal.getCorrelationID());
                }
                this.checkResponseTime(journal, responseTime);
                this.finished = true;
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandoff(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandoff(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handoff " + journal.getCorrelationID() + " for " + journal.getParticipantRole());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleReceived(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleReceived(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received " + journal.getCorrelationID() + " for " + journal.getParticipantRole());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleStarted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleStarted(final ExchangeJournal journal) {
        // no-op
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleUnspecifiedEvent(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleUnspecifiedEvent(final ExchangeJournal journal) {
        // no-op
    }

    /**
     * Init.
     * 
     * @param monitoringNotification
     *        the monitoring notification
     * @param starttime
     *        the starttime
     */
    public synchronized void init(final MonitoringNotification monitoringNotification, final Long starttime) {
        if (this.initialized) return;
        this.maxResponseTime = monitoringNotification.getMaxResponseTime();
        this.startTime = starttime;
        final ResponseTimeMonitoringBackend instance = this;
        if ((null != this.managementTimer) && (null != this.listener)) {
            Date endDate = new Date(starttime.longValue() + this.maxResponseTime);
            this.task = new TimerTask() {

                @Override
                public void run() {
                    ExpiryNotificationBean expiryNotification = new ExpiryNotificationBean(monitoringNotification, instance);
                    ResponseTimeMonitoringBackend.this.listener.sendNotification(expiryNotification);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Sent expiry notification for exchange " + monitoringNotification.getCorrelationID()
                                + " at start + " + (System.currentTimeMillis() - starttime.longValue()));
                    }
                }
            };

            if (System.currentTimeMillis() < endDate.getTime()) {
                this.managementTimer.schedule(this.task, endDate);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Scheduled task for execution time " + endDate + "(" + endDate.getTime() + ")" + " at "
                            + System.currentTimeMillis());
                }
            } else {
                this.task.run();
            }
        }
        this.initialized = true;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initiated response monitor for " + monitoringNotification.getCorrelationID() + "/"
                    + monitoringNotification.getParticipantRole() + "/" + monitoringNotification.getParticipantIdentity()
                    + " started " + this.startTime + " at start + " + (System.currentTimeMillis() - starttime.longValue()));
        }
    }

    /**
     * Sets the management timer.
     * 
     * @param managementTimer
     *        the new management timer
     */
    public void setManagementTimer(final Timer managementTimer) {
        this.managementTimer = managementTimer;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set management timer to " + managementTimer);
        }
    }

    /**
     * Sets the notification listener.
     * 
     * @param notificationListener
     *        the new notification listener
     */
    public void setNotificationListener(final ManagementNotificationListener notificationListener) {
        this.listener = notificationListener;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set notification listener to " + notificationListener);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#setOperations(org.eclipse.swordfish.core.management.operations.Operations)
     */
    public void setOperations(final Operations ops) {
        this.operations = ops;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set operations to " + ops.toString());
        }
    }

    /**
     * Check response time.
     * 
     * @param journal
     *        the journal
     * @param responseTime
     *        the response time
     */
    private void checkResponseTime(final ExchangeJournal journal, final Long responseTime) {
        if (null != this.task) {
            this.task.cancel();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Evaluating response time for exchange " + journal.getCorrelationID() + "\nMaximum: " + this.maxResponseTime
                    + " actual: " + responseTime + " at start + " + (System.currentTimeMillis() - this.startTime.longValue()));
        }
        if (responseTime.intValue() > this.maxResponseTime) {
            this.finished = true;
            String correlationID = journal.getCorrelationID();
            String serviceName = journal.getServiceName().toString();
            String operationName = journal.getOperationName();
            String role = String.valueOf(journal.getParticipantRole());
            this.reportExchange("finished", responseTime, correlationID, serviceName, operationName, role);
        }
    }

    /**
     * Report aborted exchange.
     * 
     * @param journal
     *        the journal
     * @param state
     *        the state
     */
    private void reportAbortedExchange(final ExchangeJournal journal, final String state) {
        if (null != this.task) {
            this.task.cancel();
        }
        Long duration = new Long(this.startTime.longValue() - journal.getTimestamp(ExchangeStage.CURRENT).longValue());
        String correlationID = journal.getCorrelationID();
        String serviceName = journal.getServiceName().toString();
        String operationName = journal.getOperationName();
        String role = String.valueOf(journal.getParticipantRole());
        this.reportExchange(state, duration, correlationID, serviceName, operationName, role);
        this.finished = true;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sent abort message for " + correlationID + "/" + role + " at start + "
                    + (System.currentTimeMillis() - this.startTime.longValue()));
        }
    }

    /**
     * Report exchange.
     * 
     * @param state
     *        the state
     * @param duration
     *        the duration
     * @param correlationID
     *        the correlation ID
     * @param serviceName
     *        the service name
     * @param operationName
     *        the operation name
     * @param role
     *        the role
     */
    private void reportExchange(final String state, final Long duration, final String correlationID, final String serviceName,
            final String operationName, final String role) {
        this.notified = true;
        Object[] params = {correlationID, serviceName, operationName, role, new Integer(this.maxResponseTime), state, duration};
        this.operations.notify(TrackingMessage.RESPONSETIME_EXCEEDED, params);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Notified " + correlationID + " role: " + role + " state: " + state + " at start + "
                    + (System.currentTimeMillis() - this.startTime.longValue()));
        }
    }

}
