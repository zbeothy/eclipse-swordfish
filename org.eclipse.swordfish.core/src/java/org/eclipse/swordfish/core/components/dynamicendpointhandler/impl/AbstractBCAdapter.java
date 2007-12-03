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
package org.eclipse.swordfish.core.components.dynamicendpointhandler.impl;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.BCAdapter;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpointDeploymentException;

/**
 * The Class AbstractBCAdapter.
 */
public abstract class AbstractBCAdapter implements BCAdapter {

    /** The mbean server. */
    private MBeanServer mbeanServer;

    /** The extension M bean name. */
    private ObjectName extensionMBeanName;

    /** The bc name. */
    private String bcName;

    /**
     * Can handle.
     * 
     * @param dynamicInboundEndpoint
     *        the dynamic inbound endpoint
     * 
     * @return true, if successful
     */
    public abstract boolean canHandle(DynamicInboundEndpoint dynamicInboundEndpoint);

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.BCAdapter#getBCName()
     */
    public String getBCName() {
        return this.bcName;
    }

    /**
     * Gets the extension M bean name.
     * 
     * @return the extension M bean name
     */
    public ObjectName getExtensionMBeanName() {
        return this.extensionMBeanName;
    }

    /**
     * Gets the mbean server.
     * 
     * @return the mbean server
     */
    public MBeanServer getMbeanServer() {
        return this.mbeanServer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.BCAdapter#setBCName(java.lang.String)
     */
    public void setBCName(final String sBcName) {
        this.bcName = sBcName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.BCAdapter#setExtensionMBeanName(javax.management.ObjectName)
     */
    public void setExtensionMBeanName(final ObjectName extensionMBeanName) {
        this.extensionMBeanName = extensionMBeanName;

    }

    /**
     * Sets the mbean server.
     * 
     * @param mbeanServer
     *        the new mbean server
     */
    public void setMbeanServer(final MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }

    /**
     * Start dynamic inbound endpoint.
     * 
     * @param dynamicInboundEndpoint
     *        the dynamic inbound endpoint
     * 
     * @throws DynamicInboundEndpointDeploymentException
     */
    public abstract void startDynamicInboundEndpoint(DynamicInboundEndpoint dynamicInboundEndpoint)
            throws DynamicInboundEndpointDeploymentException;
}
