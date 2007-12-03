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
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.messages.ServiceLifecycleMessage;
import org.eclipse.swordfish.core.management.messaging.ServiceMonitorBackendFactory;
import org.eclipse.swordfish.core.management.notification.EntityStateNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.notification.ServiceStateNotification;
import org.eclipse.swordfish.core.management.objectname.ObjectNameFactory;
import org.eclipse.swordfish.core.management.operations.Operations;

/**
 * Factory that returns the correct OperationMonitor to use for a given.
 * 
 */
public class ServiceMonitorBackendFactoryBean implements ServiceMonitorBackendFactory {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ServiceMonitorBackendFactoryBean.class);

    /** The operations. */
    private Operations operations;

    /** The instrumentation manager. */
    private InstrumentationManagerBean instrumentationManager;

    /**
     * Existing operation backends key: result of getOperationIdString value:
     * OperationMonitorBackend instance.
     */
    private HashMap backends;

    /**
     * Instantiates a new service monitor backend factory bean.
     */
    public ServiceMonitorBackendFactoryBean() {
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
                ServiceMonitorBackend backend = (ServiceMonitorBackend) old.get(key);
                if (null != backend) {
                    backend.destroy();
                } else {
                    LOG.warn("Handle " + key + " still present, but backend is null.");
                }
            }
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
        QName service = journal.getServiceName();
        ParticipantRole role = journal.getParticipantRole();
        String serviceIdString = this.getServiceIdString(participantId, service, role);
        ServiceMonitorBackend ret = (ServiceMonitorBackend) this.backends.get(serviceIdString);
        if (null == ret) {
            ret = this.getServiceMonitorBackend(participantId, service, role);
            LOG.warn("Received ProcessingNotification for unregistered service " + String.valueOf(serviceIdString));
        }
        return ret;
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
        if (!(not instanceof ServiceStateNotification)) return;
        ServiceStateNotification notification = (ServiceStateNotification) not;
        String serviceIdString = this.getServiceIdString(notification);
        ServiceMonitorBackend ret = (ServiceMonitorBackend) this.backends.get(serviceIdString);
        if (null == ret) {
            this.getServiceMonitorBackend(notification);
            if (LOG.isInfoEnabled()) {
                LOG.info("Successfully registered monitor for " + serviceIdString);
            }
            String participantId = String.valueOf(notification.getParticipantIdentity());
            String service = String.valueOf(notification.getServiceName());
            ParticipantRole role = notification.getParticipantRole();
            if (null != this.operations) {
                Object[] params = {participantId, "registered", service};
                ServiceLifecycleMessage msg =
                        (ParticipantRole.PROVIDER.equals(role)) ? ServiceLifecycleMessage.SERVICE_PROVIDER_REGISTRATION
                                : ServiceLifecycleMessage.SERVICE_CONSUMER_REGISTRATION;
                this.operations.notify(msg, params);
            }
        } else {
            LOG.warn("Tried to register monitor for " + serviceIdString + " but was already registered.");
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
     * (non-Javadoc).
     * 
     * @param not
     *        the not
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#unregister(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.management.notification.ParticipantRole)
     */
    public synchronized void unregister(final EntityStateNotification not) {
        if (!(not instanceof ServiceStateNotification)) return;
        ServiceStateNotification notification = (ServiceStateNotification) not;
        String serviceIdString = this.getServiceIdString(notification);
        ServiceMonitorBackend del = (ServiceMonitorBackend) this.backends.remove(serviceIdString);
        if (null != del) {
            del.destroy();
            LOG.info("Successfully unregistered monitor for " + String.valueOf(serviceIdString));
            String participantId = String.valueOf(notification.getParticipantIdentity());
            String service = String.valueOf(notification.getServiceName());
            ParticipantRole role = notification.getParticipantRole();
            if (null != this.operations) {
                Object[] params = {participantId, "unregistered", service};
                ServiceLifecycleMessage msg =
                        (ParticipantRole.PROVIDER.equals(role)) ? ServiceLifecycleMessage.SERVICE_PROVIDER_REGISTRATION
                                : ServiceLifecycleMessage.SERVICE_CONSUMER_REGISTRATION;
                this.operations.notify(msg, params);
            }
        } else {
            LOG.warn("Tried to unregister monitor for " + String.valueOf(serviceIdString) + " but was not registered.");
        }
    }

    /**
     * Gets the service id string.
     * 
     * @param notification
     *        the notification
     * 
     * @return the service id string
     */
    private String getServiceIdString(final ServiceStateNotification notification) {
        UnifiedParticipantIdentity participantId = notification.getParticipantIdentity();
        QName service = notification.getServiceName();
        ParticipantRole role = notification.getParticipantRole();
        return this.getServiceIdString(participantId, service, role);
    }

    /**
     * Gets the service id string.
     * 
     * @param participantId
     *        the participant id
     * @param service
     *        the service
     * @param role
     *        the role
     * 
     * @return the service id string
     */
    private String getServiceIdString(final UnifiedParticipantIdentity participantId, final QName service,
            final ParticipantRole role) {
        StringBuffer ret = new StringBuffer(String.valueOf(participantId)).append("/");
        ret.append(String.valueOf(service)).append("/").append(String.valueOf(role)).append("/");
        return ret.toString();
    }

    /**
     * Gets the service monitor backend.
     * 
     * @param notification
     *        the notification
     * 
     * @return the service monitor backend
     */
    private ServiceMonitorBackend getServiceMonitorBackend(final ServiceStateNotification notification) {
        UnifiedParticipantIdentity participantId = notification.getParticipantIdentity();
        QName service = notification.getServiceName();
        ParticipantRole role = notification.getParticipantRole();
        return this.getServiceMonitorBackend(participantId, service, role);

    }

    /**
     * Gets the service monitor backend.
     * 
     * @param participantId
     *        the participant id
     * @param service
     *        the service
     * @param role
     *        the role
     * 
     * @return the service monitor backend
     */
    private ServiceMonitorBackend getServiceMonitorBackend(final UnifiedParticipantIdentity participantId, final QName service,
            final ParticipantRole role) {
        String serviceIdString = this.getServiceIdString(participantId, service, role);
        ServiceMonitorBackend ret = (ServiceMonitorBackend) this.backends.get(serviceIdString);
        if (ret == null) {
            ret = new ServiceMonitorBackend(participantId, service, role, this.instrumentationManager);
            ret.setOperations(this.operations);
            this.backends.put(serviceIdString, ret);
        }
        return ret;
    }

}
