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
package org.eclipse.swordfish.core.components.dynamicendpointhandler;

import javax.management.ObjectName;

/**
 * The Interface BCAdapter.
 */
public interface BCAdapter {

    /**
     * Can handle.
     * 
     * @param dynamicInboundEndpoint
     *        the dynamic inbound endpoint
     * 
     * @return true, if successful
     */
    boolean canHandle(DynamicInboundEndpoint dynamicInboundEndpoint);

    /**
     * Gets the BC name.
     * 
     * @return the BC name
     */
    String getBCName();

    /**
     * Sets the BC name.
     * 
     * @param bcName
     *        the new BC name
     */
    void setBCName(String bcName);

    /**
     * Sets the extension M bean name.
     * 
     * @param extensionMBeanName
     *        the new extension M bean name
     */
    void setExtensionMBeanName(ObjectName extensionMBeanName);

    /**
     * Start dynamic inbound endpoint.
     * 
     * @param dynamicInboundEndpoint
     *        the dynamic inbound endpoint
     * 
     * @throws DynamicInboundEndpointDeploymentException
     */
    void startDynamicInboundEndpoint(DynamicInboundEndpoint dynamicInboundEndpoint)
            throws DynamicInboundEndpointDeploymentException;

    /**
     * Stop dynamic inbound endpoint.
     * 
     * @param id
     *        the id
     * 
     * @throws DynamicInboundEndpointDeploymentException
     */
    void stopDynamicInboundEndpoint(String id) throws DynamicInboundEndpointDeploymentException;

}
