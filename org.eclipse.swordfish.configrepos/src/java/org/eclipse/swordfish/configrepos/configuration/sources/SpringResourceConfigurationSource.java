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
package org.eclipse.swordfish.configrepos.configuration.sources;

import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.springframework.core.io.Resource;

/**
 * The Class SpringResourceConfigurationSource.
 * 
 */
public class SpringResourceConfigurationSource extends AbstractConfigurationSource {

    /** Resource whos contents is being internalized. */
    private Resource resource = null;

    /**
     * Instantiates a new spring resource configuration source.
     */
    public SpringResourceConfigurationSource() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     *         the exception
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().log(Level.FINEST, "destroy " + this.getBeanName());
        }

        this.resource = null;
        super.destroy();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * 
     * @return the configuration
     * 
     * @throws ConfigurationRepositoryConfigException
     *         the configuration repository config exception
     * 
     * @see org.eclipse.swordfish.configrepos.configuration.sources.ConfigurationSource#getConfiguration(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath)
     */
    public Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        XMLConfiguration result = new XMLConfiguration();
        try {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().entering(SpringResourceConfigurationSource.class.getName(), "getConfiguration");
                this.getLogger().log(Level.FINEST, "Reading configuration resource " + this.resource.getDescription());
            }
            try {
                result.load(this.resource.getInputStream());
                return result;
            } finally {
                if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                    this.getLogger().exiting(SpringResourceConfigurationSource.class.getName(), "getConfiguration", result);
                }
            }
        } catch (ConfigurationException e) {
            throw new ConfigurationRepositoryConfigException("Error while reading configuration from spring resource.", e);
        } catch (IOException e) {
            throw new ConfigurationRepositoryConfigException("Error while reading configuration from spring resource.", e);
        }
    }

    /**
     * Gets the resource.
     * 
     * @return Returns the resource.
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aInstance
     *        the a instance
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#resynchronize(java.lang.String)
     */
    @Override
    public void resynchronize(final String aInstance) {
        // NOOP
    }

    /**
     * Sets the resource.
     * 
     * @param resource
     *        The resource to set.
     */
    public void setResource(final Resource resource) {
        this.resource = resource;
    }
}
