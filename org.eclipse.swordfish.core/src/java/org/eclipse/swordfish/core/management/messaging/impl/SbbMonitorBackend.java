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
import java.util.ArrayList;
import java.util.Properties;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.monitor.SbbMonitor;
import org.eclipse.swordfish.core.management.operations.Operations;

/**
 * The Class SbbMonitorBackend.
 */
public class SbbMonitorBackend implements MessagingMonitorBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(SbbMonitorBackend.class);

    /** The Constant BEAN_ID. */
    private final static String BEAN_ID = "org.eclipse.swordfish.core.management.monitor.SbbMonitor";

    /** The Constant DESCRIPTION_FILE. */
    private final static String DESCRIPTION_FILE = "/org/eclipse/swordfish/core/management/monitor/SbbMonitorDesc.xml";

    /** The Constant DOMAIN_PARTS. */
    private final static String DOMAIN_PARTS = null;

    /** MBean to report InternalSBB data. */
    private SbbMonitor sbbMonitor;

    /** The instrumentation manager. */
    private InstrumentationManagerBean instrumentationManager;

    /**
     * Constructur - create the MBean.
     */
    public SbbMonitorBackend() {
        this.sbbMonitor = new SbbMonitor();
        this.sbbMonitor.initialize();
    }

    /**
     * Destroy.
     */
    public void destroy() {
        if (null != this.sbbMonitor) {
            try {
                this.instrumentationManager.unregisterInstrumentation(this.sbbMonitor);
            } catch (Exception e) {
                LOG.error("Could not unregister SbbMonitor from MBeanServer.\n"
                        + "Monitor might still be visible for operators, but is inoperational. Reason:\n" + e.getMessage());
            }
            this.sbbMonitor.destroy();
            this.sbbMonitor = null;
        }
        this.instrumentationManager = null;
    }

    /**
     * Gets the instrumentation manager.
     * 
     * @return the instrumentation manager
     */
    public InstrumentationManagerBean getInstrumentationManager() {
        return this.instrumentationManager;
    }

    /**
     * Gets the monitor.
     * 
     * @return the monitor
     */
    public SbbMonitor getMonitor() {
        return this.sbbMonitor;
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
        this.sbbMonitor.handleAppFailEvent();
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
        this.sbbMonitor.handleNetFailEvent();
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
        // no-op
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
        Long processingTime = journal.getTotalProcessingTime();
        if (null != processingTime) {
            this.sbbMonitor.addResponseTime(processingTime.longValue());
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Could not determine processing time for delivered exchange " + journal.getCorrelationID());
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDoneLocal(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDoneLocal(final ExchangeJournal journal) {
        // noop
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
        // no-op
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
        // no-op
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
        this.sbbMonitor.handleRequestEvent();
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
     * (non-Javadoc).
     * 
     * @param journal
     *        the journal
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleUnspecifiedEvent(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleUnspecifiedEvent(final ExchangeJournal journal) {
        // no-op
    }

    /**
     * Init.
     */
    public void init() {
        this.registerMBean(this.instrumentationManager);
    }

    /**
     * Sets the instrumentation manager.
     * 
     * @param manager
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManagerBean manager) {
        this.instrumentationManager = manager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#setOperations(org.eclipse.swordfish.core.management.operations.Operations)
     */
    public void setOperations(final Operations ops) {
        // no-op for now
    }

    /**
     * Register M bean.
     * 
     * @param instrumentationManagerBean
     *        the instrumentation manager
     */
    private void registerMBean(final InstrumentationManagerBean instrumentationManagerBean) {
        InputStream is = this.getClass().getResourceAsStream(DESCRIPTION_FILE);
        ArrayList domainParts = new ArrayList();
        domainParts.add(DOMAIN_PARTS);
        Properties nameProperties = new Properties();
        nameProperties.put("pid", "[sbb]");
        nameProperties.put("name", "sbbmonitor");
        nameProperties.put("type", "adapter");
        nameProperties.put("id", String.valueOf(this.sbbMonitor.hashCode()));
        try {
            instrumentationManagerBean.registerInstrumentation(this.sbbMonitor, is, domainParts, nameProperties, BEAN_ID);
        } catch (Exception e) {
            LOG.error("Could not register SbbMonitor with MBeanServer.\n" + "Monitor will not be visible for operators. Reason:\n"
                    + e.getMessage());
        }
    }
}
