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

import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.messaging.ServiceMonitorBackendFactory;
import org.eclipse.swordfish.core.management.notification.EntityState;
import org.eclipse.swordfish.core.management.notification.ManagementNotification;
import org.eclipse.swordfish.core.management.notification.NotificationProcessor;
import org.eclipse.swordfish.core.management.notification.ServiceStateNotification;

/**
 * The Class ServiceRegistrationBean.
 */
public class ServiceRegistrationBean implements NotificationProcessor {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ServiceRegistrationBean.class);

    /** The factory. */
    private ServiceMonitorBackendFactory factory;

    /**
     * Destroy.
     */
    public void destroy() {
        this.factory = null;
    }

    /**
     * Gets the factory.
     * 
     * @return the factory
     */
    public ServiceMonitorBackendFactory getFactory() {
        return this.factory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.notification.NotificationProcessor#process(org.eclipse.swordfish.core.management.notification.ManagementNotification)
     */
    public void process(final ManagementNotification notification) {
        if (notification instanceof ServiceStateNotification) {
            ServiceStateNotification opNotification = (ServiceStateNotification) notification;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processing notification "
                        + notification
                        + ":"
                        + opNotification.getState()
                        + " for participant/service "
                        + String.valueOf(opNotification.getParticipantIdentity() + "/"
                                + String.valueOf(opNotification.getServiceName())));
            }
            this.processInternal(opNotification);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Processing complete");
            }
        }
    }

    /**
     * processes OperationStateNotifications by registering/unregistering the operation with all
     * registered <code>MessagingMonitorBackendFactory</code>s.
     * 
     * @param notification
     *        to process
     */
    public void processInternal(final ServiceStateNotification notification) {
        if (EntityState.ADDED.equals(notification.getState())) {
            this.factory.register(notification);
        } else if (EntityState.REMOVED.equals(notification.getState())) {
            this.factory.unregister(notification);
        } else {
            LOG.error("Trying to set operation state to unknown value of " + notification.getState() + " for service "
                    + String.valueOf(notification.getParticipantIdentity()) + "/" + String.valueOf(notification.getServiceName())
                    + " - ignored");
        }
    }

    /**
     * Sets the factory.
     * 
     * @param factory
     *        the new factory
     */
    public void setFactory(final ServiceMonitorBackendFactory factory) {
        this.factory = factory;
    }

}
