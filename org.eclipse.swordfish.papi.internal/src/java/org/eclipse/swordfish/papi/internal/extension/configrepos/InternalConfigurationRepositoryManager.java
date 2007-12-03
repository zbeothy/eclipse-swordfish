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
 * InternalConfigurationRepositoryManager instances provide access to the configuration sub-system
 * of the SOP infrastructure. They allow fetching configuration and resource data from the mentioned
 * sub-system, by providing selection criteria defined by the configuration framework.
 */
public interface InternalConfigurationRepositoryManager {

    /**
     * Registers a specific configuration repository event listener. This method is provided in
     * conjunction with the notification mechanisms of the configuration repository.
     * 
     * @param aListener
     *        is the object which will receive the notification event.
     */
    void addConfigurationRepositoryEventListner(final InternalConfigurationRepositoryEventListener aListener);

    /**
     * Registers a specific configuration repository event listener. This method is provided in
     * conjunction with the notification mechanisms of the configuration repository.
     * 
     * @param aTreeQualifier
     *        is the tree which the provided listener shall be informed on.
     * @param aListener
     *        is the object which will receive the notification event.
     */
    void addConfigurationRepositoryEventListner(final String aTreeQualifier,
            final InternalConfigurationRepositoryEventListener aListener);

    /**
     * Fetches a specific configuration.
     * 
     * @param aTreeQualifier
     *        for the configuration tree which shall be use to fetch the configuration from.
     * @param aScopePath
     *        provided as a part of the path identifier.
     * @return Configuration found for the provided parameters, or null in case non was available.
     * @throws InternalConfigurationRepositoryException
     *         in case the source tree was not able to fetch the specific configuration
     */
    InputStream getConfiguration(final String aTreeQualifier, final String aScopePath)
            throws InternalConfigurationRepositoryException;

    /**
     * Fetches a specific configuration.
     * 
     * @param aTreeQualifier
     *        for the configuration tree which shall be use to fetch the configuration from.
     * @param aLocation
     *        the identifier which will be used as a location identifier.
     * @param aParticipantIdentity
     *        provided as a part of the path identifier.
     * @return Configuration found for the provided parameters, or null in case non was available.
     * @throws InternalConfigurationRepositoryException
     *         in case the source tree was not able to fetch the specific configuration
     */
    InputStream getConfiguration(final String aTreeQualifier, final String aLocation,
            final InternalParticipantIdentity aParticipantIdentity) throws InternalConfigurationRepositoryException;

    /**
     * Return the value of the local basepath which is used to find configurations and resources.
     * This property is being set to 'classpath:./conf' as default.
     * 
     * @return the base path string
     */
    String getLocalResourceBase();

    /**
     * Fetch a resource byte array for a specific identifier and tree-qualifier.
     * 
     * @param aTreeQualifier
     *        which pre-selects the tree which should be browsed for the resource data.
     * @param aLocation
     *        the identifier which will be used as a location identifier.
     * @param aParticipantIdentity
     *        provided as a part of the path identifier.
     * @param aComponent
     *        for which the resource should be fetched
     * @param aResourceIdentifier
     *        identifier for the specific resource.
     * @return the resource byte array which was found for the identifier. Null in case nothing was
     *         found.
     * @throws InternalConfigurationRepositoryException
     *         in case a technical error occured.
     */
    InputStream getResource(final String aTreeQualifier, final String aLocation,
            final InternalParticipantIdentity aParticipantIdentity, final String aComponent, final String aResourceIdentifier)
            throws InternalConfigurationRepositoryException;

    /**
     * Fetch a resource byte array for a specific identifier and tree-qualifier.
     * 
     * @param aTreeQualifier
     *        which pre-selects the tree which should be browsed for the resource data.
     * @param aScopePath
     *        provided as a part of the path identifier.
     * @param aComponent
     *        which owns the resource
     * @param aResourceIdentifier
     *        identifier for the specific resource.
     * @return the resource byte array which was found for the identifier. Null in case nothing was
     *         found.
     * @throws InternalConfigurationRepositoryException
     *         in case a technical error occured.
     */
    InputStream getResource(final String aTreeQualifier, final String aScopePath, final String aComponent,
            final String aResourceIdentifier) throws InternalConfigurationRepositoryException;

    /**
     * Check whether the configuration repository proxy instance skipping the usage of a remote
     * configuration repository or not.
     * 
     * @return if remote calls are configured to be skipped
     */
    boolean isSkipRemoteRepositoryCalls();

    /**
     * Removes the provided configuration repository event listener from the list of this
     * InternalConfigurationRepositoryManagerImpl. This method is provided in conjunction with the
     * notification mechanisms of the configuration repository.
     * 
     * @param aListener
     *        which had been registered for receiving events.
     */
    void removeConfigurationRepositoryEventListner(final InternalConfigurationRepositoryEventListener aListener);

    /**
     * Set the base path for the configuration manager. This can be either a file path URL
     * ('file:...') or a classpath URL ('classpath:'). The default value is set to
     * 'classpath:./conf', which would allow deploying configurations and resources below a
     * directory './conf' relative to any root of the classpath.
     * 
     * @param aFilePath
     *        to be used as a base path
     */
    void setLocalResourceBase(final String aFilePath);

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
