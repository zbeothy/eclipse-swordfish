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
package org.eclipse.swordfish.core.components.helpers.impl;

import java.net.URL;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.extension.infrastructure.InternalInfrastructureInstance;

/**
 * The Class InfrastructureInstanceImpl.
 */
public class InfrastructureInstanceImpl implements InternalInfrastructureInstance {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(InfrastructureInstanceImpl.class);

    /** The configuration service URL. */
    private String configurationServiceURL = null;

    /** The service registry URL. */
    private String serviceRegistryURL = null;

    /**
     * Instantiates a new infrastructure instance impl.
     * 
     * @param confURL
     *        the conf URL
     * @param srURL
     *        the sr URL
     */
    public InfrastructureInstanceImpl(final String confURL, final String srURL) {
        this.configurationServiceURL = confURL;
        this.serviceRegistryURL = srURL;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.infrastructure.InternalInfrastructureInstance#getConfigurationServiceURL()
     */
    public URL getConfigurationServiceURL() {
        URL url = null;
        try {
            url = new URL(this.configurationServiceURL);
        } catch (Exception e) {
            LOG.error("Error while creating URL from " + this.configurationServiceURL, e);
        }
        return url;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.infrastructure.InternalInfrastructureInstance#getServiceRegistryURL()
     */
    public URL getServiceRegistryURL() {
        URL url = null;
        try {
            url = new URL(this.serviceRegistryURL);
        } catch (Exception e) {
            LOG.error("Error while creating URL from " + this.serviceRegistryURL, e);
        }
        return url;
    }

}
