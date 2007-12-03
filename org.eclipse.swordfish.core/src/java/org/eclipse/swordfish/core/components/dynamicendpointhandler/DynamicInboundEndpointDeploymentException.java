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

import org.eclipse.swordfish.core.exception.ComponentException;

/**
 * The Class DynamicInboundEndpointDeploymentException.
 */
public class DynamicInboundEndpointDeploymentException extends ComponentException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 279883439212088140L;

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     */
    public DynamicInboundEndpointDeploymentException() {
        super();
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey) {
        super(resourceKey);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final String parameter1) {
        super(resourceKey, parameter1);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param parameter2
     *        the parameter2
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final String parameter1, final String parameter2) {
        super(resourceKey, parameter1, parameter2);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param parameter2
     *        the parameter2
     * @param parameter3
     *        the parameter3
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final String parameter1, final String parameter2,
            final String parameter3) {
        super(resourceKey, parameter1, parameter2, parameter3);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param parameter2
     *        the parameter2
     * @param parameter3
     *        the parameter3
     * @param cause
     *        the cause
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final String parameter1, final String parameter2,
            final String parameter3, final Throwable cause) {
        super(resourceKey, parameter1, parameter2, parameter3, cause);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param parameter2
     *        the parameter2
     * @param cause
     *        the cause
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final String parameter1, final String parameter2,
            final Throwable cause) {
        super(resourceKey, parameter1, parameter2, cause);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param cause
     *        the cause
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final String parameter1, final Throwable cause) {
        super(resourceKey, parameter1, cause);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param messageParameters
     *        the message parameters
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final String[] messageParameters) {
        super(resourceKey, messageParameters);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param messageParameters
     *        the message parameters
     * @param cause
     *        the cause
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final String[] messageParameters,
            final Throwable cause) {
        super(resourceKey, messageParameters, cause);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param cause
     *        the cause
     */
    public DynamicInboundEndpointDeploymentException(final String resourceKey, final Throwable cause) {
        super(resourceKey, cause);
    }

    /**
     * Instantiates a new dynamic inbound endpoint deployment exception.
     * 
     * @param t
     *        the t
     */
    public DynamicInboundEndpointDeploymentException(final Throwable t) {
        super(t);
    }

}
