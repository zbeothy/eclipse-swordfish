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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory;
import org.eclipse.swordfish.core.management.notification.ExchangeNotification;
import org.eclipse.swordfish.core.management.notification.ManagementNotification;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.MessageTrackingNotification;
import org.eclipse.swordfish.core.management.notification.MonitoringNotification;
import org.eclipse.swordfish.core.management.notification.NotificationProcessor;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.notification.impl.ExpiryNotificationBean;
import org.eclipse.swordfish.core.management.notification.impl.MonitoringNotificationBean;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.core.utils.BeanInspector;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Manages message exchange journals and reporting backends.
 * 
 */
public class MessagingMonitorBean implements NotificationProcessor, ApplicationContextAware {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(MessagingMonitorBean.class);

    /** Journals of pending message exchanges key: correlationID value: corresponding journal. */
    private HashMap pending;

    /** Factory instances to generate reporting backends value: MessagingMonitorBackendFactory. */
    private Collection backendFactories;

    /** The operations. */
    private Operations operations;

    /** The application context. */
    private ApplicationContext applicationContext;

    /** The destroy. */
    private boolean destroy = false;

    /**
     * Instantiates a new messaging monitor bean.
     */
    public MessagingMonitorBean() {
        this.pending = new HashMap();
        this.backendFactories = new ArrayList();
        this.backendFactories.add(new MessagingTracerFactoryBean());
        LOG.info("created");
    }

    /**
     * Destroy.
     */
    public void destroy() {
        this.destroy = true;
        if ((null != this.pending) && (this.pending.size() != 0)) {
            if (LOG.isInfoEnabled()) {
                StringBuffer msg = new StringBuffer("Pending message exchanges on shutdown. CorrelationIDs:\n");
                for (Iterator iter = this.pending.values().iterator(); iter.hasNext();) {
                    ExchangeJournal journal = (ExchangeJournal) iter.next();
                    msg.append(journal.getCorrelationID()).append("\n");
                }
                LOG.info(msg.toString());
            }
            this.pending.clear();
            this.pending = null;
        }
        if (null != this.backendFactories) {
            this.backendFactories.clear();
            this.backendFactories = null;
        }
        this.operations = null;
        this.applicationContext = null;
    }

    public Collection getBackendFactories() {
        return this.backendFactories;
    }

    public HashMap getPending() {
        return this.pending;
    }

    /**
     * Process one notification.
     * 
     * @param notification
     *        the notification
     * 
     * @see org.eclipse.swordfish.core.management.notification.NotificationProcessor#process(org.eclipse.swordfish.core.management.notification.MessageProcessingNotification)
     */
    public void process(final ManagementNotification notification) {
        if (this.destroy) return;
        if (notification instanceof MessageProcessingNotification) {
            MessageProcessingNotification procNotification = (MessageProcessingNotification) notification;
            if (LOG.isDebugEnabled()) {
                String role = this.getRoleAbbrev(procNotification.getParticipantRole());
                LOG
                    .debug("Processing MesssageProcessingNotification for exchange " + role + ":"
                            + procNotification.getCorrelationID() + " Event: " + notification.getEventType() + "(" + notification
                            + ")");
            }
            this.processInternal(procNotification);
        } else if (notification instanceof MonitoringNotification) {
            MonitoringNotification monitoringNotification = (MonitoringNotification) notification;
            if (LOG.isDebugEnabled()) {
                String role = this.getRoleAbbrev(monitoringNotification.getParticipantRole());
                LOG.debug("Processing MonitoringNotification  for exchange " + role + ":"
                        + monitoringNotification.getCorrelationID() + "(" + notification + ")");
            }
            this.processInternal(monitoringNotification);
        } else if (notification instanceof MessageTrackingNotification) {
            MessageTrackingNotification trackingNotification = (MessageTrackingNotification) notification;
            if (LOG.isDebugEnabled()) {
                String role = this.getRoleAbbrev(trackingNotification.getParticipantRole());
                LOG.debug("Processing MessageTrackingNotification for exchange " + role + ":"
                        + trackingNotification.getCorrelationID() + "(" + notification + ")");
            }
            this.processInternal(trackingNotification);
        }
    }

    /**
     * processes MessageProcessingNotifications by handing over the notification to the
     * ExchangeJournal for the MessageExchange, creating the journal if necessary.
     * 
     * @param notification
     *        to process
     */
    public void processInternal(final MessageProcessingNotification notification) {
        if (LOG.isTraceEnabled()) {
            String dump = BeanInspector.beanToString(notification);
            LOG.trace("Processing MessageProcessingNotification " + dump);
        }
        String journalId = notification.getParticipantRole() + notification.getCorrelationID();
        ExchangeJournal journal = (ExchangeJournal) this.pending.get(journalId);
        if (null == journal) {
            journal = ExchangeJournalFactory.createJournal(notification);
            Vector backends = new Vector(this.backendFactories.size());
            for (Iterator iter = this.backendFactories.iterator(); iter.hasNext();) {
                MessagingMonitorBackendFactory factory = (MessagingMonitorBackendFactory) iter.next();
                MessagingMonitorBackend backend = factory.getBackend(journal);
                backends.add(backend);
            }
            BackendDispatcher dispatcher = new BackendDispatcher(backends);
            journal.setBackend(dispatcher);
            this.pending.put(journalId, journal);
        }
        journal.process(notification);
        if (journal.isFinished()) {
            this.pending.remove(journalId);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.applicationContext = arg0;
    }

    public void setBackendFactories(final Collection backendFactories) {
        this.backendFactories = backendFactories;
    }

    /**
     * Updates the list of currently used backend factories - normally called by container<br/>
     * Note that this does not update the backends for already instantiated journals.
     * 
     * @param factories
     *        <code>List</code> of <code>MessagingMonitorBackendFactory</code> s
     */
    public void setBackendFactories(final List factories) {
        this.backendFactories = factories;
    }

    /**
     * Sets the operations.
     * 
     * @param operations
     *        the new operations
     */
    public void setOperations(final Operations operations) {
        this.operations = operations;
    }

    public void setPending(final HashMap pending) {
        this.pending = pending;
    }

    /**
     * Checks if there is already a backend of the required class, creates one if not.
     * 
     * @param journal
     *        the journal
     * @param backendClass
     *        the backend class
     * 
     * @return the backend
     */
    private MessagingMonitorBackend getBackend(final ExchangeJournal journal, final Class backendClass) {
        // check for ResponseTimeMonitor in journal's backend
        MessagingMonitorBackend res = null;
        MessagingMonitorBackend presentBackend = journal.getBackend();
        if (backendClass.isAssignableFrom(presentBackend.getClass())) {
            res = presentBackend;
        } else if (presentBackend instanceof BackendDispatcher) {
            BackendDispatcher dispatcher = (BackendDispatcher) presentBackend;
            for (Iterator iter = dispatcher.getBackends().iterator(); iter.hasNext();) {
                MessagingMonitorBackend backend = (MessagingMonitorBackend) iter.next();
                if (backendClass.isAssignableFrom(backend.getClass())) {
                    res = backend;
                    break;
                }
            }
        }
        if (null == res) {
            // not there yet, so add it
            String backendClassName = backendClass.getName();
            try {
                res = (MessagingMonitorBackend) this.applicationContext.getBean(backendClassName);
            } catch (Exception e) {
                this.logException(e);
            }
            res.setOperations(this.operations);
            if (presentBackend instanceof BackendDispatcher) {
                ((BackendDispatcher) presentBackend).getBackends().add(res);
            } else {
                Vector backends = new Vector(2);
                backends.add(presentBackend);
                backends.add(res);
                BackendDispatcher dispatcher = new BackendDispatcher(backends);
                journal.setBackend(dispatcher);
            }
        }
        return res;
    }

    /**
     * Gets the journal.
     * 
     * @param notification
     *        the notification
     * 
     * @return the journal
     */
    private ExchangeJournal getJournal(final ExchangeNotification notification) {
        String journalId = notification.getParticipantRole() + notification.getCorrelationID();
        ExchangeJournal journal = (ExchangeJournal) this.pending.get(journalId);
        if (null == journal) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Journal for MessageExchange " + journalId + " not found.");
            }
        }
        return journal;
    }

    /**
     * Gets the role abbrev.
     * 
     * @param role
     *        the role
     * 
     * @return the role abbrev
     */
    private String getRoleAbbrev(final ParticipantRole role) {
        String ret = ParticipantRole.CONSUMER.equals(role) ? "Cons" : "Prov";
        return ret;
    }

    /**
     * Log exception.
     * 
     * @param e
     *        the e
     */
    private void logException(final Exception e) {
        Object[] stackTrace = e.getStackTrace();
        StringBuffer msg =
                new StringBuffer("Unexpected exception when creating MessagingMonitorBackend - report to SOPSOLUTIONS support."
                        + e.getMessage());
        for (int i = 0; i < stackTrace.length; i++) {
            msg.append("\n").append(stackTrace[i].toString());
        }
        LOG.error(new String(msg));
    }

    /**
     * Process internal.
     * 
     * @param trackingNotification
     *        the tracking notification
     */
    private void processInternal(final MessageTrackingNotification trackingNotification) {
        ExchangeJournal journal = this.getJournal(trackingNotification);
        if (null == journal) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not enable message tracking for MessageExchange " + trackingNotification.getCorrelationID()
                        + " - no journal for MessageExchange");
            }
            return;
        }
        MessageTrackingBackend backend = (MessageTrackingBackend) this.getBackend(journal, MessageTrackingBackend.class);
        if (null != backend) {
            backend.setTrackingLevel(trackingNotification.getTrackingLevel());
            journal.replay(backend);
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not enable message tracking for MessageExchange " + trackingNotification.getCorrelationID()
                        + " - see previous entry for reason");
            }
        }
    }

    /**
     * Process internal.
     * 
     * @param monitoringNotification
     *        the monitoring notification
     */
    private void processInternal(final MonitoringNotification monitoringNotification) {
        if (monitoringNotification instanceof MonitoringNotificationBean) {
            ExchangeJournal journal = this.getJournal(monitoringNotification);
            if (null == journal) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not enable response time monitoring for MessageExchange "
                            + monitoringNotification.getCorrelationID() + " - no journal present for MesssageExchange.");
                }
                return;
            }
            ResponseTimeMonitoringBackend responseTimeBackend =
                    (ResponseTimeMonitoringBackend) this.getBackend(journal, ResponseTimeMonitoringBackend.class);
            if (null != responseTimeBackend) {
                Long timestamp = null;
                if (0 != monitoringNotification.getCreatedTimestamp()) {
                    timestamp = new Long(monitoringNotification.getCreatedTimestamp());
                    journal.setCreatedTimestamp(timestamp);
                }
                if (0 != monitoringNotification.getRelatedTimestamp()) {
                    timestamp = new Long(monitoringNotification.getRelatedTimestamp());
                    journal.setRelatedTimestamp(timestamp);
                }
                responseTimeBackend.init(monitoringNotification, timestamp);
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not enable response time monitoring for MessageExchange "
                            + monitoringNotification.getCorrelationID() + " - see previous entry for reason");
                }
            }
        } else if (monitoringNotification instanceof ExpiryNotificationBean) {
            ((ExpiryNotificationBean) monitoringNotification).execute();
        } else {
            LOG.warn("Encountered unexpected internal notification of type " + monitoringNotification.getClass().getName()
                    + "\nPlease submit error message to development for investigation");
        }
    }

}
