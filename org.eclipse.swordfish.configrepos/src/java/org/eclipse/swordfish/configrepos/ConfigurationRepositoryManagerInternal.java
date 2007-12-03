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
package org.eclipse.swordfish.configrepos;

import java.io.InputStream;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.configuration.sources.ConfigurationSource;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.resource.sources.ResourceSource;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * Interface available to internal components of the SBB.
 * 
 */
public interface ConfigurationRepositoryManagerInternal extends ConfigurationSource, ResourceSource {

    /** Default separator. */
    String CONFIGREPOS_DEFAULTSCOPEPATHSEPARATOR_PROPERTYKEY = ".";

    /**
     * Register a event listener for all events issued by this configuration manager.
     * 
     * @param aEventListener
     *        which should be registered
     */
    void addConfigurationRepositoryEventListner(final ConfigurationRepositoryEventListenerInternal aEventListener);

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        which should filter those events which this event listener is interested in.
     * @param aEventListener
     *        which should be registered
     */
    void addConfigurationRepositoryEventListner(final String aTreeQualifier,
            final ConfigurationRepositoryEventListenerInternal aEventListener);

    /**
     * Get the boot configuration.
     * 
     * @return configuration
     */
    Configuration getBootConfiguration();

    /**
     * Fetch the configuration, based on tree and scopepath.
     * 
     * @param aTreeQualifier
     *        is the name of the tree to be used.
     * @param aScopePath
     *        is the name of the scopepath to be traversed.
     * 
     * @return the configuration found under the scopepath in the specified tree.
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case the configuration could not successfully be fetched.
     * 
     * @see org.eclipse.swordfish.configrepos.configuration.sources.ConfigurationSource#getConfiguration(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.ScopePath)
     */
    Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException;

    /**
     * Return the default tree qualifier assigned to this manager. Return null if non is assigned.
     * 
     * @return TreeQualifier that is assigned as default to the manager.
     */
    String getDefaultTreeQualifier();

    /**
     * Get the fixed scope path assigned to this manager. Return null, if non is set.
     * 
     * @return ScopePath
     */
    ScopePath getFixedScopePath();

    /**
     * Gets the local resource base.
     * 
     * @return the current value of the local resource base
     */
    String getLocalResourceBase();

    /**
     * Fetch the resource, based on tree and scopepath.
     * 
     * @param aTreeQualifier
     *        is the name of the tree to be used.
     * @param aScopePath
     *        is the name of the scopepath to be traversed.
     * @param aComponent
     *        is the component name that owns the resource.
     * @param aResourceIdentifier
     *        is the identifying name of the resource.
     * 
     * @return the a resource found under the scopepath in the specified tree.
     * 
     * @throws ConfigurationRepositoryResourceException
     *         in case the resource could not successfully be fetched.
     * 
     * @see org.eclipse.swordfish.configrepos.resource.sources.ResourceSource#getResource(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.ScopePath, java.lang.String,
     *      java.lang.String)
     */
    InputStream getResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResourceIdentifier) throws ConfigurationRepositoryResourceException;

    /**
     * Check whether this manager would skip remote calls.
     * 
     * @return current setting
     */
    boolean isSkipRemoteRepositoryCalls();

    /**
     * (non-Javadoc).
     * 
     * @param aEventListener
     *        which should be registered
     */
    void removeConfigurationRepositoryEventListner(final ConfigurationRepositoryEventListenerInternal aEventListener);

    /**
     * Resynchronize the manager and its components.
     * 
     * @param aScopePath
     *        is the path that should be synchronized.
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#resynchronize(java.lang.String)
     */
    void resynchronize(final String aScopePath);

    /**
     * Sets the local resource base.
     * 
     * @param aBase
     *        base path
     */
    void setLocalResourceBase(final String aBase);

    /**
     * Set the participant id.
     * 
     * c
     */
    void setParticipantId(final String appId, final String instId);

    /**
     * Set whether this manager would skip remote calls.
     * 
     * @param aSkip
     *        to true in case no remote calls should be done
     */
    void setSkipRemoteRepositoryCalls(final boolean aSkip);
}
