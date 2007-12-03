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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.messages.ServiceLifecycleMessage;
import org.eclipse.swordfish.core.management.messaging.OperationMonitorBackendFactory;
import org.eclipse.swordfish.core.management.notification.EntityStateNotification;
import org.eclipse.swordfish.core.management.notification.OperationStateNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.objectname.ObjectNameFactory;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;

/**
 * Factory that returns the correct OperationMonitor to use for a given.
 * 
 */
public class OperationMonitorBackendFactoryBean implements OperationMonitorBackendFactory {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(OperationMonitorBackendFactoryBean.class);

    /** Default period in milliseconds between summaries for registered backends. */
    private static final long DEFAULT_SUMMARY_INTERVAL = 900000;

    /** period in milliseconds between summaries for registered backends. */
    private long summaryInterval = DEFAULT_SUMMARY_INTERVAL;

    /** The operations. */
    private Operations operations;

    /** The instrumentation manager. */
    private InstrumentationManagerBean instrumentationManager;

    /**
     * Existing operation backends key: result of getOperationIdString value:
     * OperationMonitorBackend instance.
     */
    private final HashMap backends;

    /** Timer used to control the creation of processing summaries. */
    private Timer managementTimer;

    /** The timer task. */
    private BackendTimerTask timerTask;

    /**
     * Instantiates a new operation monitor backend factory bean.
     */
    public OperationMonitorBackendFactoryBean() {
        this.backends = new HashMap();
        if (LOG.isDebugEnabled()) {
            LOG.debug("instantiated");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#destroy()
     */
    public synchronized void destroy() {
        if (this.backends.size() != 0) {
            HashMap old = new HashMap(this.backends);
            this.backends.clear();
            LOG.warn("Registered monitors on shutdown - releasing.");
            for (Iterator iter = old.keySet().iterator(); iter.hasNext();) {
                Object key = iter.next();
                LOG.info("Releasing" + String.valueOf(key));
                OperationMonitorBackend backend = (OperationMonitorBackend) old.get(key);
                if (null != backend) {
                    backend.destroy();
                } else {
                    LOG.warn("Handle " + key + " still present, but backend is null.");
                }
            }
        }
        if (null != this.timerTask) {
            this.timerTask.cancel();
        }
        this.timerTask = null;
        this.instrumentationManager = null;
        this.operations = null;
        this.managementTimer = null;
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroyed");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#getBackend(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public MessagingMonitorBackend getBackend(final ExchangeJournal journal) {
        // OperationMonitorBackend ret =
        // getOperationMonitorBackend(protocol.getServiceName(),
        // protocol.getOperationName(), protocol.getParticipantRole(),
        // protocol.getParticipantId());
        QName serviceName = journal.getServiceName();
        String operationName = journal.getOperationName();
        ParticipantRole role = journal.getParticipantRole();
        UnifiedParticipantIdentity participantId = journal.getParticipantId();
        InternalCommunicationStyle style = journal.getStyle();
        String fqOperationName = this.getOperationIdString(serviceName, operationName, role, participantId);
        OperationMonitorBackend ret = (OperationMonitorBackend) this.backends.get(fqOperationName);
        if (null == ret) {
            ret = this.getOperationMonitorBackend(serviceName, operationName, role, participantId, style);
            LOG.warn("Received ProcessingNotification for unregistered operation " + fqOperationName);
        }
        return ret;
    }

    public HashMap getBackends() {
        return this.backends;
    }

    /**
     * Gets the management timer.
     * 
     * @return the management timer
     */
    public Timer getManagementTimer() {
        return this.managementTimer;
    }

    /**
     * Gets the operations.
     * 
     * @return the operations
     */
    public Operations getOperations() {
        return this.operations;
    }

    /**
     * Gets the summary interval.
     * 
     * @return the summary interval
     */
    public long getSummaryInterval() {
        return this.summaryInterval;
    }

    /**
     * (non-Javadoc).
     * 
     * @param not
     *        the not
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#register(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.management.notification.ParticipantRole)
     */
    public synchronized void register(final EntityStateNotification not) {
        if (!(not instanceof OperationStateNotification)) return;
        OperationStateNotification notification = (OperationStateNotification) not;
        QName serviceName = notification.getServiceName();
        String operationName = notification.getOperationName();
        ParticipantRole role = notification.getParticipantRole();
        UnifiedParticipantIdentity participantId = notification.getParticipantIdentity();
        InternalCommunicationStyle style = notification.getStyle();
        String fqOperationName = this.getOperationIdString(serviceName, operationName, role, participantId);
        OperationMonitorBackend ret = (OperationMonitorBackend) this.backends.get(fqOperationName);
        if (null == ret) {
            this.getOperationMonitorBackend(serviceName, operationName, role, participantId, style);
            if (LOG.isInfoEnabled()) {
                LOG.info("Successfully registered monitor for " + fqOperationName);
            }
            ServiceLifecycleMessage msg =
                    (ParticipantRole.PROVIDER.equals(role)) ? ServiceLifecycleMessage.OPERATION_PROVIDER_REGISTRATION
                            : ServiceLifecycleMessage.OPERATION_CONSUMER_REGISTRATION;
            if (null != this.operations) {
                Object[] params = {String.valueOf(participantId), "registered", fqOperationName};
                this.operations.notify(msg, params);
            }
        } else {
            LOG.warn("Tried to register monitor for " + fqOperationName + " but was already registered.");
        }
    }

    /**
     * Sets the instrumentation manager.
     * 
     * @param instrumentationManager
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManagerBean instrumentationManager) {
        this.instrumentationManager = instrumentationManager;
    }

    /**
     * Sets the management timer.
     * 
     * @param managementTimer
     *        the new management timer
     */
    public void setManagementTimer(final Timer managementTimer) {
        this.managementTimer = managementTimer;
        this.timerTask = new BackendTimerTask();
        managementTimer.schedule(this.timerTask, this.summaryInterval, this.summaryInterval);
        if (LOG.isDebugEnabled()) {
            LOG.debug("ManagementTimer set to " + managementTimer + " ,created TimerTask " + this.timerTask);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MBeanBackendFactory#setObjectNameFactory(org.eclipse.swordfish.core.management.objectname.ObjectNameFactory)
     */
    public void setObjectNameFactory(final ObjectNameFactory onf) {
        // not needed anymore, now using InternalInstrumentationManager
        // TODO: clean up in hierarchy
    }

    /**
     * Sets the operations.
     * 
     * @param operations
     *        the new operations
     */
    public void setOperations(final Operations operations) {
        this.operations = operations;
        for (Iterator iter = this.backends.values().iterator(); iter.hasNext();) {
            OperationMonitorBackend backend = (OperationMonitorBackend) iter.next();
            backend.setOperations(operations);
        }
    }

    /**
     * Sets the summary interval.
     * 
     * @param summaryInterval
     *        the new summary interval
     */
    public void setSummaryInterval(final long summaryInterval) {
        this.summaryInterval = summaryInterval;
        if ((null != this.managementTimer) && (null != this.timerTask)) {
            this.managementTimer.schedule(this.timerTask, summaryInterval, summaryInterval);
            if (LOG.isDebugEnabled()) {
                LOG.debug("SummaryInterval for operation summaries set to " + summaryInterval);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Tried to set SummaryInterval for operation summaries, but managementTimer=" + this.managementTimer
                        + " ,timerTask=" + this.timerTask);
            }
        }

    }

    /**
     * (non-Javadoc).
     * 
     * @param not
     *        the not
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#unregister(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.management.notification.ParticipantRole)
     */
    public synchronized void unregister(final EntityStateNotification not) {
        if (!(not instanceof OperationStateNotification)) return;
        OperationStateNotification notification = (OperationStateNotification) not;
        QName serviceName = notification.getServiceName();
        String operationName = notification.getOperationName();
        ParticipantRole role = notification.getParticipantRole();
        UnifiedParticipantIdentity participantId = notification.getParticipantIdentity();
        String fqOperationName = this.getOperationIdString(serviceName, operationName, role, participantId);
        OperationMonitorBackend del = (OperationMonitorBackend) this.backends.remove(fqOperationName);
        if (null != del) {
            del.destroy();
            if (LOG.isInfoEnabled()) {
                LOG.info("Successfully unregistered monitor for " + fqOperationName);
            }
            if (null != this.operations) {
                ServiceLifecycleMessage msg =
                        (ParticipantRole.PROVIDER.equals(role)) ? ServiceLifecycleMessage.OPERATION_PROVIDER_REGISTRATION
                                : ServiceLifecycleMessage.OPERATION_CONSUMER_REGISTRATION;
                Object[] params = {String.valueOf(participantId), "unregistered", fqOperationName};
                this.operations.notify(msg, params);
            }
        } else {
            LOG.warn("Tried to unregister monitor for " + fqOperationName + " but was not registered.");
        }
    }

    /**
     * Gets the operation id string.
     * 
     * @param participantId
     *        the participant id
     * @param serviceName
     *        the service name
     * @param opName
     *        the op name
     * @param role
     *        the role
     * 
     * @return a String that contains a unique identifier for an operation
     */
    private String getOperationIdString(final QName serviceName, final String opName, final ParticipantRole role,
            final UnifiedParticipantIdentity participantId) {
        String participantString = String.valueOf(participantId);
        String roleString = (null != role) ? role.toString() : "NULL";
        String serviceString = (null != serviceName) ? serviceName.toString() : "NULL";
        StringBuffer ret =
                new StringBuffer(participantString).append(":").append(roleString).append("-").append(serviceString).append("#")
                    .append(opName);
        return ret.toString();
        // return participantString + ":" + roleString + "-" + serviceString +
        // "#" + opName;
    }

    /**
     * Gets the operation monitor backend.
     * 
     * @param serviceName
     *        the service name
     * @param opName
     *        the op name
     * @param role
     *        the role
     * @param participantId
     *        the participant id
     * @param style
     *        the style
     * 
     * @return the operation monitor backend
     */
    private OperationMonitorBackend getOperationMonitorBackend(final QName serviceName, final String opName,
            final ParticipantRole role, final UnifiedParticipantIdentity participantId, final InternalCommunicationStyle style) {
        String fqOperationName = this.getOperationIdString(serviceName, opName, role, participantId);
        OperationMonitorBackend ret = (OperationMonitorBackend) this.backends.get(fqOperationName);
        if (ret == null) {
            if (InternalCommunicationStyle.REQUEST_RESPONSE.equals(style)) {
                ret = new InOutMonitorBackend(participantId, serviceName, opName, role, this.instrumentationManager);
            } else if (InternalCommunicationStyle.ONEWAY.equals(style)) {
                ret = new InOnlyMonitorBackend(participantId, serviceName, opName, role, this.instrumentationManager);
            } else if (InternalCommunicationStyle.NOTIFICATION.equals(style)) {
                ret = new OutOnlyMonitorBackend(participantId, serviceName, opName, role, this.instrumentationManager);
            }
            if (null == ret) {
                ret = new OperationMonitorBackend(participantId, serviceName, opName, role, this.instrumentationManager);
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not determine concrete backend for " + fqOperationName + String.valueOf(style)
                            + "\n. Using base implementation, tracking summary disabled");
                }
            }
            ret.setOperations(this.operations);
            this.backends.put(fqOperationName, ret);
        }
        return ret;
    }

    /**
     * The Class BackendTimerTask.
     */
    private class BackendTimerTask extends TimerTask {

        /**
         * (non-Javadoc).
         * 
         * @see java.util.TimerTask#run()
         */
        @Override
        public synchronized void run() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("timer task running");
            }
            for (Iterator iter = OperationMonitorBackendFactoryBean.this.backends.values().iterator(); iter.hasNext();) {
                OperationMonitorBackend backend = (OperationMonitorBackend) iter.next();
                backend.publishSummary();
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Published summary for " + backend.getOperationName());
                }
            }
        }

    }

}
