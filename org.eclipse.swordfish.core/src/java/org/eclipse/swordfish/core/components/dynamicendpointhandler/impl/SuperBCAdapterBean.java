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

import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpointDeploymentException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class SuperBCAdapterBean.
 */
public class SuperBCAdapterBean extends AbstractBCAdapter {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(SuperBCAdapterBean.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.impl.AbstractBCAdapter#canHandle(org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint)
     */
    @Override
    public boolean canHandle(final DynamicInboundEndpoint dynamicInboundEndpoint) {
        // TODO TO BE CHECKED FOR all JBI Implementations
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.impl.AbstractBCAdapter#startDynamicInboundEndpoint(org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint)
     */
    @Override
    public void startDynamicInboundEndpoint(final DynamicInboundEndpoint dynamicInboundEndpoint)
            throws DynamicInboundEndpointDeploymentException {
        String xml = dynamicInboundEndpoint.asString();
        ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            LOG.debug("Try to start dynamic inbound endpoint " + xml);
            this.getMbeanServer().invoke(this.getExtensionMBeanName(), "startDynamicEndpoint", new Object[] {xml},
                    new String[] {String.class.getName()});
            LOG.debug("Started dynamic inbound endpoint " + xml);
        } catch (Exception e) {
            LOG.debug("Failed to start dynamic inbound endpoint " + xml);
            throw new DynamicInboundEndpointDeploymentException("Failed to start dynamic inbound endpoint.", e);
        } finally {
            Thread.currentThread().setContextClassLoader(currThreadLoader);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.BCAdapter#stopDynamicInboundEndpoint(java.lang.String)
     */
    public void stopDynamicInboundEndpoint(final String id) throws DynamicInboundEndpointDeploymentException {
        ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            LOG.debug("Try to stop dynamic inbound endpoint " + id);
            this.getMbeanServer().invoke(this.getExtensionMBeanName(), "stopDynamicEndpoint", new Object[] {id},
                    new String[] {String.class.getName()});
        } catch (Exception e) {
            LOG.warn("Failed to stop dynamic inbound endpoint " + id);
            throw new DynamicInboundEndpointDeploymentException("Failed to stop dynamic inbound endpoint.", e);
        } finally {
            Thread.currentThread().setContextClassLoader(currThreadLoader);
        }
    }
}
