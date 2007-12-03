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
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.monitor.ServiceMonitor;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * The Class ServiceMonitorBackend.
 */
public class ServiceMonitorBackend implements MessagingMonitorBackend {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ServiceMonitorBackend.class);

    /** The Constant BEAN_ID. */
    private final static String BEAN_ID = "org.eclipse.swordfish.core.management.monitor.ServiceMonitor";

    /** The Constant DESCRIPTION_FILE. */
    private final static String DESCRIPTION_FILE = "/org/eclipse/swordfish/core/management/monitor/ServiceMonitorDesc.xml";

    /** The Constant DOMAIN_PARTS. */
    private final static String DOMAIN_PARTS = null;

    /** The participant id. */
    private UnifiedParticipantIdentity participantId;

    /** The monitor. */
    private ServiceMonitor monitor;

    /** The instrumenation manager. */
    private InstrumentationManagerBean instrumenationManager;

    /**
     * Instantiates a new service monitor backend.
     * 
     * @param participantId
     *        the participant id
     * @param service
     *        the service
     * @param role
     *        the role
     * @param instrumentationManager
     *        the instrumentation manager
     */
    public ServiceMonitorBackend(final UnifiedParticipantIdentity participantId, final QName service, final ParticipantRole role,
            final InstrumentationManagerBean instrumentationManager) {
        this.participantId = participantId;
        this.monitor = new ServiceMonitor(participantId, service, role);
        this.monitor.initialize();
        this.instrumenationManager = instrumentationManager;
        this.registerMBean(participantId, service, role, instrumentationManager);
    }

    /**
     * Destroy.
     */
    public void destroy() {
        try {
            this.instrumenationManager.unregisterInstrumentation(this.monitor);
        } catch (InternalInfrastructureException e) {
            LOG.error("Unexpected exception while unregistering MBean - might still be accessible to operator.\n" + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedApplication(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedApplication(final ExchangeJournal journal) {
        this.monitor.handleAppFailEvent();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleAbortedNet(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleAbortedNet(final ExchangeJournal journal) {
        this.monitor.handleNetFailEvent();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleCompleted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleCompleted(final ExchangeJournal journal) {
        // don't monitor response times - meaningless for service
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleDelivered(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleDelivered(final ExchangeJournal journal) {
        // don't monitor response times - meaningless for service
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
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandback(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandback(final ExchangeJournal journal) {
        // don't monitor response times - meaningless for service
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleHandoff(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleHandoff(final ExchangeJournal journal) {
        // don't monitor response times - meaningless for service
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleReceived(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleReceived(final ExchangeJournal journal) {
        this.monitor.handleRequestEvent();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleStarted(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleStarted(final ExchangeJournal journal) {
        // don't monitor response times - meaningless for service
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#handleUnspecifiedEvent(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public void handleUnspecifiedEvent(final ExchangeJournal journal) {
        // noop
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.impl.MessagingMonitorBackend#setOperations(org.eclipse.swordfish.core.management.operations.Operations)
     */
    public void setOperations(final Operations ops) {
    }

    /**
     * Register M bean.
     * 
     * @param unifiedParticipantId
     *        the participant id
     * @param role
     *        the role
     * @param qService
     *        the service
     * @param instrumentationManager
     *        the instrumentation manager
     */
    private void registerMBean(final UnifiedParticipantIdentity unifiedParticipantId, final QName qService,
            final ParticipantRole role, final InstrumentationManagerBean instrumentationManager) {
        InputStream is = this.getClass().getResourceAsStream(DESCRIPTION_FILE);
        ArrayList domainParts = new ArrayList();
        domainParts.add(DOMAIN_PARTS);
        Properties nameProperties = new Properties();
        nameProperties.put("pid", String.valueOf(unifiedParticipantId));
        nameProperties.put("role", String.valueOf(role));
        nameProperties.put("service", String.valueOf(qService));
        nameProperties.put("type", "ServiceMonitor");
        nameProperties.put("id", String.valueOf(this.monitor.hashCode()));
        try {
            instrumentationManager.registerInstrumentation(this.monitor, is, domainParts, nameProperties, BEAN_ID);
        } catch (Exception e) {
            LOG.error("Could not register ParticipantMonitor " + String.valueOf(this.participantId) + "with MBeanServer.\n"
                    + "Monitor will not be visible for operators. Reason:\n" + e.getMessage());
        }
    }

}
