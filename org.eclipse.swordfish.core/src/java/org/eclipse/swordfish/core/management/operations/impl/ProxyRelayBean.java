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
package org.eclipse.swordfish.core.management.operations.impl;

import org.eclipse.swordfish.core.components.internalproxy.InternalProxy;

/**
 * Small utility class that relays an instance's proxy to the SbbPublisherBackend.
 * 
 */
public class ProxyRelayBean {

    /** The internal proxy. */
    private InternalProxy internalProxy;

    /** The sbb publisher backend. */
    private SbbPublisherBackendBean sbbPublisherBackend;

    /**
     * Destroy.
     */
    public void destroy() {
        this.sbbPublisherBackend.removeInternalProxy(this.internalProxy);
        this.internalProxy = null;
        this.sbbPublisherBackend = null;
    }

    /**
     * Init.
     */
    public void init() {
        this.sbbPublisherBackend.addInternalProxy(this.internalProxy);
    }

    /**
     * Sets the internal proxy.
     * 
     * @param proxy
     *        the new internal proxy
     */
    public void setInternalProxy(final InternalProxy proxy) {
        this.internalProxy = proxy;
    }

    /**
     * Sets the sbb publisher backend.
     * 
     * @param backend
     *        the new sbb publisher backend
     */
    public void setSbbPublisherBackend(final SbbPublisherBackendBean backend) {
        this.sbbPublisherBackend = backend;
    }

}
