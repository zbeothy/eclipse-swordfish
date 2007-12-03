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
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.components.LifecycleComponent;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.messages.ServiceLifecycleMessage;
import org.eclipse.swordfish.core.management.messaging.ParticipantMonitorBackendFactory;
import org.eclipse.swordfish.core.management.monitor.SbbMonitor;
import org.eclipse.swordfish.core.management.notification.EntityStateNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantStateNotification;
import org.eclipse.swordfish.core.management.objectname.ObjectNameFactory;
import org.eclipse.swordfish.core.management.operations.Operations;

/**
 * Factory that returns the correct OperationMonitor to use for a given.
 * 
 */
public class ParticipantMonitorBackendFactoryBean extends LifecycleComponent implements ParticipantMonitorBackendFactory {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ParticipantMonitorBackendFactoryBean.class);

    /** The operations. */
    private Operations operations;

    /** The instrumentation manager. */
    private InstrumentationManagerBean instrumentationManager;

    /**
     * Existing operation backends key: result of getOperationIdString value:
     * OperationMonitorBackend instance.
     */
    private HashMap backends;

    /** The sbb monitor. */
    private SbbMonitor sbbMonitor;

    /**
     * Instantiates a new participant monitor backend factory bean.
     */
    public ParticipantMonitorBackendFactoryBean() {
        this.backends = new HashMap();
        if (LOG.isTraceEnabled()) {
            LOG.trace("instantiated");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.components.LifecycleComponent#destroy()
     */
    @Override
    public synchronized void destroy() {

        if (this.backends.size() != 0) {
            HashMap old = this.backends;
            this.backends.clear();
            LOG.warn("Registered monitors on shutdown - releasing.");
            for (Iterator iter = old.keySet().iterator(); iter.hasNext();) {
                Object key = iter.next();
                LOG.info("Releasing" + String.valueOf(key));
                ParticipantMonitorBackend backend = (ParticipantMonitorBackend) old.get(key);
                if (null != backend) {
                    backend.destroy();
                } else {
                    LOG.warn("Handle " + key + " for ParticipantMonitorBackend still present, but backend is null.");
                }
            }
            old.clear();
        }
        this.operations = null;
        this.instrumentationManager = null;
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
        UnifiedParticipantIdentity participantId = journal.getParticipantId();
        ParticipantMonitorBackend ret = (ParticipantMonitorBackend) this.backends.get(participantId);
        if (null == ret) {
            ret = this.getParticipantMonitorBackend(participantId);
            LOG.warn("Received ProcessingNotification for unregistered participant " + String.valueOf(participantId));
        }
        return ret;
    }

    public HashMap getBackends() {
        return this.backends;
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
     * (non-Javadoc).
     * 
     * @param not
     *        the not
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#register(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.management.notification.ParticipantRole)
     */
    public synchronized void register(final EntityStateNotification not) {
        if (!(not instanceof ParticipantStateNotification)) return;
        ParticipantStateNotification notification = (ParticipantStateNotification) not;
        UnifiedParticipantIdentity participantId = notification.getParticipantIdentity();
        ParticipantMonitorBackend ret = (ParticipantMonitorBackend) this.backends.get(participantId);
        if (null == ret) {
            this.getParticipantMonitorBackend(participantId);
            if (LOG.isInfoEnabled()) {
                LOG.info("Successfully registered monitor for " + String.valueOf(participantId));
            }
            if (null != this.operations) {
                this.operations.notify(ServiceLifecycleMessage.PARTICIPANT_REGISTRATION, String.valueOf(participantId),
                        "registered");
            }
        } else {
            LOG.warn("Tried to register monitor for " + String.valueOf(participantId) + " but was already registered.");
        }
    }

    public void setBackends(final HashMap backends) {
        this.backends = backends;
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
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MBeanBackendFactory#setObjectNameFactory(org.eclipse.swordfish.core.management.objectname.ObjectNameFactory)
     */
    public void setObjectNameFactory(final ObjectNameFactory onf) {
        // not needed here
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
            ParticipantMonitorBackend backend = (ParticipantMonitorBackend) iter.next();
            backend.setOperations(operations);
        }
    }

    /**
     * Sets the sbb monitor backend factory.
     * 
     * @param sbbFactory
     *        the new sbb monitor backend factory
     */
    public void setSbbMonitorBackendFactory(final SbbMonitorBackendFactoryBean sbbFactory) {
        this.sbbMonitor = sbbFactory.getBackend().getMonitor();
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
        if (!(not instanceof ParticipantStateNotification)) return;
        ParticipantStateNotification notification = (ParticipantStateNotification) not;
        UnifiedParticipantIdentity participantId = notification.getParticipantIdentity();
        ParticipantMonitorBackend del = (ParticipantMonitorBackend) this.backends.remove(participantId);
        if (null != del) {
            this.sbbMonitor.removeParticipantMonitor(del.getMonitor());
            del.destroy();
            LOG.info("Successfully unregistered monitor for " + String.valueOf(participantId));
            if (null != this.operations) {
                this.operations.notify(ServiceLifecycleMessage.PARTICIPANT_REGISTRATION, String.valueOf(participantId),
                        "unregistered");
            }
        } else {
            LOG.warn("Tried to unregister monitor for " + String.valueOf(participantId) + " but was not registered.");
        }
    }

    /**
     * Gets the participant monitor backend.
     * 
     * @param participantId
     *        the participant id
     * 
     * @return the participant monitor backend
     */
    private ParticipantMonitorBackend getParticipantMonitorBackend(final UnifiedParticipantIdentity participantId) {
        ParticipantMonitorBackend ret = (ParticipantMonitorBackend) this.backends.get(participantId);
        if (ret == null) {
            ret = new ParticipantMonitorBackend(participantId, this.instrumentationManager);
            ret.setOperations(this.operations);
            this.backends.put(participantId, ret);
            this.sbbMonitor.addParticipantMonitor(ret.getMonitor());
        }
        return ret;
    }

}
