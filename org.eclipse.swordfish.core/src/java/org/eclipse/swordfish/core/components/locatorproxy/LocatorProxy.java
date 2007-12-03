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

import javax.xml.namespace.QName;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.w3c.dom.DocumentFragment;

/**
 * The Interface LocatorProxy.
 */
public interface LocatorProxy {

    /** The ROLE. */
    String ROLE = LocatorProxy.class.getName();

    /**
     * deploys a cluster for a location into the locator proxy.
     * 
     * @param locatorId
     *        the locator id
     * @param configuration
     *        the configuration
     * 
     * @throws InternalInfrastructureException
     */
    void deploy(String locatorId, String configuration) throws InternalInfrastructureException;

    /**
     * Checks if is active.
     * 
     * @return true if the usage of the proxy is active
     */
    boolean isActive();

    /**
     * registers a WSA adress fragment for a service endpoint in the identified locator cluster.
     * 
     * @param locatorId
     *        the idetifier of the cluster. In InternalSBB this value equals to location
     * @param service
     *        the service QName that is going to be regitered
     * @param endpoint
     *        the name of the endpoint that is going to be regitered
     * @param address
     *        the WSA address fragment that defines the physical address for the service
     * 
     * @throws InternalInfrastructureException
     */
    void register(String locatorId, QName service, String endpoint, DocumentFragment address)
            throws InternalInfrastructureException;

    /**
     * undeploys a cluster for a location into the locator proxy.
     * 
     * @param locatorId
     *        the locator id
     * 
     * @throws InternalInfrastructureException
     */
    void undeploy(String locatorId) throws InternalInfrastructureException;

    /**
     * unregisters a previously registered service in a given locator cluster.
     * 
     * @param locatorId
     *        the cluster identifier that maps to the location value in InternalSBB
     * @param service
     *        the QName of the service to be unregistered
     * @param endpoint
     *        the name of the endpoint to be unregistered
     * 
     * @throws InternalInfrastructureException
     */
    void unregister(String locatorId, QName service, String endpoint) throws InternalInfrastructureException;

}
