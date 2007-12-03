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

import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;

/**
 * The Class SingleConfigurationSource.
 * 
 */
public class SingleConfigurationSource extends AbstractConfigurationSource {

    /** The config. */
    private XMLConfiguration config = null;

    /**
     * The Constructor.
     * 
     * @param aSource
     *        that should be read
     */
    public SingleConfigurationSource(final XMLConfiguration aSource) {
        super();
        this.config = aSource;
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

        this.config = null;
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
        return this.config;
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
        // Auto-generated method stub

    }

}
