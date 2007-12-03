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
package org.eclipse.swordfish.core.components.locatorproxy;

import java.util.List;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * The interface to the iona locator se. All functionality that is needed to interact with the iona
 * se is described through this interface. This includes three major use-cases:
 * <ul>
 * <li> dynamic deployment and undeployment of configurations for the iona se
 * <li> registration and unregistration of WSA address fragments for a service QName and endpoint
 * name
 * <li> look up of the physical address information given a service name and endpoint name
 * </ul>
 */
public interface LocatorProxyInstanceConfigurer {

    /** this classes role for spring identification reason. */
    String ROLE = LocatorProxyInstanceConfigurer.class.getName();

    /**
     * deploys a locator cluster artifact for the dynamic deployment of cluster configurations.
     * 
     * @param locatorId
     *        the ld of the locator cluster for which this configuration is deployed
     * @param deploymentArtifact
     *        the String representation of the configuration needed by IONA locator proxy
     * 
     * @throws InternalInfrastructureException
     */
    void deploy(String locatorId, String deploymentArtifact) throws InternalInfrastructureException;

    /**
     * Gets the location names.
     * 
     * @return the location names
     */
    List getLocationNames();

    /**
     * Gets the prefered location.
     * 
     * @return the prefered location
     */
    String getPreferedLocation();

    /**
     * undeploys the configuration for the given locatorId.
     * 
     * @param locatorId
     *        the Id of the locator (location attribute) from which a configuration should be
     *        undeployed
     * 
     * @throws InternalInfrastructureException
     */
    void undeploy(String locatorId) throws InternalInfrastructureException;
}
