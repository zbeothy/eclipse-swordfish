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

import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.management.messaging.MessagingTracerFactory;
import org.eclipse.swordfish.core.management.notification.EntityStateNotification;

/**
 * The Class MessagingTracerFactoryBean.
 */
public class MessagingTracerFactoryBean implements MessagingTracerFactory {

    /** The backend. */
    private MessagingTracer backend;

    /**
     * Instantiates a new messaging tracer factory bean.
     */
    public MessagingTracerFactoryBean() {
        this.backend = new MessagingTracer();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#destroy()
     */
    public void destroy() {
        this.backend = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#getBackend(org.eclipse.swordfish.core.management.messaging.impl.ExchangeJournal)
     */
    public MessagingMonitorBackend getBackend(final ExchangeJournal protocol) {
        return this.backend;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#register(org.eclipse.swordfish.core.management.notification.EntityStateNotification)
     */
    public void register(final EntityStateNotification notification) {
        // no action necessary
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingTracerFactory#setKernel(org.eclipse.swordfish.core.components.iapi.Kernel)
     */
    public void setKernel(final Kernel aKernel) {
        // Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.messaging.MessagingMonitorBackendFactory#unregister(org.eclipse.swordfish.core.management.notification.EntityStateNotification)
     */
    public void unregister(final EntityStateNotification notification) {
        // no action necessary
    }

}
