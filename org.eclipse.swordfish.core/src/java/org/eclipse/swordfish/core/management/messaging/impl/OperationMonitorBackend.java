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

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.messages.TrackingMessage;
import org.eclipse.swordfish.core.management.monitor.OperationMonitor;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * The Class OperationMonitorBackend.
 * 
 */
public class OperationMonitorBackend implements MessagingMonitorBackend {

    /** The Constant log. */
    protected final static Log LOG = SBBLogFactory.getLog(OperationMonitorBackend.class);

    /** The Constant BEAN_ID. */
    private final static String BEAN_ID = "org.eclipse.swordfish.core.management.monitor.OperationMonitor";

    /** The Constant DESCRIPTION_FILE. */
    private final static String DESCRIPTION_FILE = "/org/eclipse/swordfish/core/management/monitor/OperationMonitorDesc.xml";

    /** The Constant DOMAIN_PARTS. */
    private final static String DOMAIN_PARTS = null;

    /** used to convert timestamps in XML Schema dateformat. */
    private SimpleDateFormat iso8601UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /** MBean to report InternalSBB data. */
    private OperationMonitor myOperationMonitor;

    /** The service. */
    private QName service;

    /** The operation name. */
    private String operationName;

    /** The role. */
    private ParticipantRole role;

    /** The operations. */
    private Operations operations;

    /** The participant. */
    private UnifiedParticipantIdentity participant;

    /** Tracking message to use. Depends on interaction style and participant role */
    private TrackingMessage trackingMessage;

    /** The instrumentation manager. */
    private InstrumentationManagerBean instrumentationManager;

    /** Cached to prevent classloading issues during shutdown. */
    private Class clazz = this.getClass();

    /**
     * Constructor.
     * 
     * @param participant
     *        the participant
     * @param serviceName
     *        the service name
     * @param opName
     *        the op name
     * @param role
     *        the role
     * @param instrumentationManager
     *        the instrumentation manager
     */
    public OperationMonitorBackend(final UnifiedParticipantIdentity participant, final QName serviceName, final String opName,
            final ParticipantRole role, final InstrumentationManagerBean instrumentationManager) {
        this.iso8601UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        // initialize monitor
        this.participant = participant;
        this.service = serviceName;
        this.operationName = opName;
        this.role = role;
        this.instrumentationManager = instrumentationManager;
        this.myOperationMonitor = new OperationMonitor(participant, this.service, this.operationName, role);
        this.myOperationMonitor.initialize();
        this.registerMBean(participant, this.service, this.operationName, role, instrumentationManager);
    }

    /**
     * Destroy.
     */
    public void destroy() {
        this.publishSummary();
        if (null != this.myOperationMonitor) {
            try {
                this.instrumentationManager.unregisterInstrumentation(this.myOperationMonitor);
            } catch (InternalInfrastructureException e) {
                LOG.error("Could not unregister OperationMonitor " + this.getIdString() + "from MBeanServer.\n"
                        + "Monitor might still be visible for operators, but will not work properly. Reason:\n" + e.getMessage());
            }
            this.myOperationMonitor.destroy();
        }
    }

    /**
     * Gets the id string.
     * 
     * @return a <code>String</code> that identifies this OperationMonitorBackend
     */
    public String getIdString() {
        return String.valueOf(this.participant) + "/" + String.valueOf(this.service) + "#" + this.operationName + ":"
                + String.valueOf(this.role);
    }

    public SimpleDateFormat getIso8601UTC() {
        return this.iso8601UTC;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public Operations getOperations() {
        return this.operations;
    }

    public UnifiedParticipantIdentity getParticipant() {
        return this.participant;
    }

    public ParticipantRole getRole() {
        return this.role;
    }

    public QName getService() {
        return this.service;
    }

    public TrackingMessage getTrackingMessage() {
        return this.trackingMessage;
    }

    /**
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedApplication(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedApplication(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling aborted app/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
        this.myOperationMonitor.handleAppFailEvent();
    }

    /**
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedNet(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedNet(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling aborted net/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
        this.myOperationMonitor.handleNetFailEvent();
    }

    /**
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleCompleted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleCompleted(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling completed/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDelivered(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDelivered(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling delivered/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
        Long processingTime = journal.getTotalProcessingTime();
        if (null != processingTime) {
            this.myOperationMonitor.addResponseTime(processingTime.longValue());
            if (LOG.isInfoEnabled()) {
                LOG.info("added response time for operation " + journal.getOperationName() + " : " + processingTime);
            }
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not determine processing time for delivered exchange " + journal.getCorrelationID());
            }
        }
    }

    /**
     * Handle done local.
     * 
     * @param journal
     *        the journal
     */
    public void handleDoneLocal(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling done local/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandback(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandback(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling handback/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandoff(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandoff(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling handoff/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleReceived(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleReceived(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling received/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
        this.myOperationMonitor.handleRequestEvent();
    }

    /**
     * Handle started.
     * 
     * @param journal
     *        the journal
     */
    public void handleStarted(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling started/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleUnspecifiedEvent(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleUnspecifiedEvent(final ExchangeJournal journal) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("handling unspecified event/" + String.valueOf(this.role) + " for " + journal.getCorrelationID());
        }
    }

    /**
     * Publish summary.
     */
    public void publishSummary() {
        // nothing to do for default implementation
    }

    public void setIso8601UTC(final SimpleDateFormat iso8601UTC) {
        this.iso8601UTC = iso8601UTC;
    }

    public void setOperationName(final String operationName) {
        this.operationName = operationName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#setOperations(org.eclipse.swordfish.core.management.operations.Operations)
     */
    public void setOperations(final Operations ops) {
        this.operations = ops;
    }

    public void setParticipant(final UnifiedParticipantIdentity participant) {
        this.participant = participant;
    }

    public void setRole(final ParticipantRole role) {
        this.role = role;
    }

    public void setService(final QName service) {
        this.service = service;
    }

    public void setTrackingMessage(final TrackingMessage trackingMessage) {
        this.trackingMessage = trackingMessage;
    }

    /**
     * Timestamp to datestring.
     * 
     * @param timestamp
     *        the timestamp
     * 
     * @return the string
     */
    protected String timestampToDatestring(final long timestamp) {
        Date date = new Date(timestamp);
        String ret = this.iso8601UTC.format(date);
        return ret;
    }

    /**
     * creates the monitor MBean and registers it through the InstrumentationMangaer.
     * 
     * @param participantId
     *        the participant id
     * @param qService
     *        the service
     * @param sOperationName
     *        the operation name
     * @param parsRole
     *        the role
     * @param instrumentManager
     *        the instrumentation manager
     */
    private void registerMBean(final UnifiedParticipantIdentity participantId, final QName qService, final String sOperationName,
            final ParticipantRole parsRole, final InstrumentationManagerBean instrumentManager) {
        InputStream is = this.clazz.getResourceAsStream(DESCRIPTION_FILE);
        ArrayList domainParts = new ArrayList();
        domainParts.add(DOMAIN_PARTS);
        Properties nameProperties = new Properties();
        nameProperties.put("pid", String.valueOf(participantId));
        nameProperties.put("role", String.valueOf(parsRole));
        nameProperties.put("operation", String.valueOf(qService) + "#" + sOperationName);
        nameProperties.put("type", "monitor");
        nameProperties.put("name", "operationmonitor");
        if (null != this.operationName) {
            nameProperties.put("id", new Integer(this.operationName.hashCode()).toString());
        } else {
            nameProperties.put("id", new Integer(this.hashCode()).toString());
        }
        try {
            instrumentManager.registerInstrumentation(this.myOperationMonitor, is, domainParts, nameProperties, BEAN_ID);
        } catch (Exception e) {
            LOG.error("Could not register OperationMonitor " + this.getIdString() + "with MBeanServer.\n"
                    + "Monitor will not be visible for operators. Reason:\n" + e.getMessage());
        }
    }

}
