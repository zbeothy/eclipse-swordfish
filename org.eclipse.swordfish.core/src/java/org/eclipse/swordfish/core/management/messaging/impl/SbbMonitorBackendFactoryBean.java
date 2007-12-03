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
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.messaging.SbbMonitorBackendFactory;
import org.eclipse.swordfish.core.management.notification.EntityStateNotification;
import org.eclipse.swordfish.core.management.objectname.ObjectNameFactory;

/**
 * The Class SbbMonitorBackendFactoryBean.
 */
public class SbbMonitorBackendFactoryBean implements SbbMonitorBackendFactory {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(SbbMonitorBackendFactoryBean.class);

    /** The backend. */
    private SbbMonitorBackend backend;

    /**
     * Instantiates a new sbb monitor backend factory bean.
     */
    public SbbMonitorBackendFactoryBean() {
        this.backend = new SbbMonitorBackend();
        if (LOG.isDebugEnabled()) {
            LOG.debug("instantiated");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#destroy()
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        this.backend.destroy();
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * Gets the backend.
     * 
     * @return the backend
     */
    public SbbMonitorBackend getBackend() {
        return this.backend;
    }

    /**
     * (non-Javadoc).
     * 
     * @param protocol
     *        the protocol
     * 
     * @return the backend
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#getBackend(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public MessagingMonitorBackend getBackend(final ExchangeJournal protocol) {
        return this.backend;
    }

    /**
     * Init.
     */
    public void init() {
        this.backend.init();
        LOG.debug("initialized");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#register(org.eclipse.swordfish.core.management.notification.EntityStateNotification)
     */
    public void register(final EntityStateNotification notification) {
        // no-op
    }

    public void setBackend(final SbbMonitorBackend backend) {
        this.backend = backend;
    }

    /**
     * Sets the instrumentation manager.
     * 
     * @param manager
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManagerBean manager) {
        this.backend.setInstrumentationManager(manager);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MBeanBackendFactory#setObjectNameFactory(org.eclipse.swordfish.core.management.objectname.ObjectNameFactory)
     */
    public void setObjectNameFactory(final ObjectNameFactory onf) {
        // no-op
        // TODO: remove from hierarchy
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#unregister(org.eclipse.swordfish.core.management.notification.EntityStateNotification)
     */
    public void unregister(final EntityStateNotification notification) {
        // no-op
    }

}
