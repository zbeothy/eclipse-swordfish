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
package org.eclipse.swordfish.papi.internal.extension.configrepos;

import java.io.InputStream;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationRepositoryException;
import org.eclipse.swordfish.papi.internal.extension.configrepos.event.InternalConfigurationRepositoryEventListener;

/**
 * .
 */
public interface InternalConfigurationRepositoryManagerProxy {

    /**
     * Register a event listener for all events issued by this configuration manager.
     * 
     * @param aEventListener
     *        which should be registered
     */
    void addConfigurationRepositoryEventListner(final InternalConfigurationRepositoryEventListener aEventListener);

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        which should filter those events which this event listener is interested in.
     * @param aEventListener
     *        which should be registered
     */
    void addConfigurationRepositoryEventListner(final String aTreeQualifier,
            final InternalConfigurationRepositoryEventListener aEventListener);

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#getConfiguration(java.lang.String,
     *      java.lang.String)
     * @param aTreeQualifier
     * @param aScopePathString
     * @throws InternalConfigurationRepositoryException
     * @return String the String.
     */
    String getConfiguration(final String aTreeQualifier, final String aScopePathString)
            throws InternalConfigurationRepositoryException;

    /**
     * Fetch a configuration based on PAPI specific objects.
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#getConfiguration(java.lang.String,
     *      java.lang.String, org.eclipse.swordfish.papi.InternalParticipantIdentity)
     * @param aTreeQualifier
     * @param aLocation
     * @param aPartId
     * @throws InternalConfigurationRepositoryException
     * @return String the String.
     * 
     */
    String getConfiguration(final String aTreeQualifier, final String aLocation, final InternalParticipantIdentity aPartId)
            throws InternalConfigurationRepositoryException;

    /**
     * Return the value of the local base path.
     * 
     * @return the base path
     */
    String getLocalResourceBase();

    /**
     * Fetch a resource based on papi specific objects.
     * 
     * @see org.eclipse.swordfish.papi.extension.configrepos.InternalConfigurationRepositoryManager#getResource(java.lang.String,
     *      java.lang.String, org.eclipse.swordfish.papi.InternalParticipantIdentity,
     *      java.lang.String)
     * @param aTreeQualifier
     * @param aLocation
     * @param aPartId
     * @param aComponent
     * @param aResourceName
     * @throws InternalConfigurationRepositoryException
     * @return InputStream
     */
    InputStream getResource(final String aTreeQualifier, final String aLocation, final InternalParticipantIdentity aPartId,
            final String aComponent, final String aResourceName) throws InternalConfigurationRepositoryException;

    /**
     * .
     * 
     * @param aTreeQualifier
     *        defines the tree to browse
     * @param aScopePathString
     *        holds a string representation of the scope path
     * @param aComponent
     *        containes the name of the component
     * @param aResourceName
     *        defines the component to fetch
     * @return an InputStream to the resource
     * @throws InternalConfigurationRepositoryException
     *         in case either the resource could not be found, or the a problem was encountered when
     *         parsing the parameters
     */
    InputStream getResource(final String aTreeQualifier, final String aScopePathString, final String aComponent,
            final String aResourceName) throws InternalConfigurationRepositoryException;

    /**
     * Check whether the configuration repository proxy instance skipping the usage of a remote
     * configuration repository or not.
     * 
     * @return if remote calls are configured to be skipped
     */
    boolean isSkipRemoteRepositoryCalls();

    /**
     * (non-Javadoc).
     * 
     * @param aEventListener
     *        which should be registered
     */
    void removeConfigurationRepositoryEventListner(final InternalConfigurationRepositoryEventListener aEventListener);

    /**
     * .
     * 
     * @param aBasePath
     *        to be used
     */
    void setLocalResourceBase(final String aBasePath);

    /**
     * This flag defines whether the configuration repository proxy instance shall skip remote calls
     * and service configurations and resources from the local sources. The respective configuration
     * defined in the SBB deployment will not be changed and will be available at the moment the
     * next manager is instantiated.
     * 
     * @param aSkip
     *        whether remote calls should be skipped
     */
    void setSkipRemoteRepositoryCalls(final boolean aSkip);
}
