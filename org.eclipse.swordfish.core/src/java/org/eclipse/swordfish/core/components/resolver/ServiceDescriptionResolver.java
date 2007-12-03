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
package org.eclipse.swordfish.core.components.resolver;

import java.util.Collection;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;

/**
 * The Interface ServiceDescriptionResolver.
 * 
 */
public interface ServiceDescriptionResolver {

    /** the role of this interface to look it up. */
    String ROLE = ServiceDescriptionResolver.class.getName();

    /**
     * This method retrieves the service descriptions of all service providers which provide the
     * logical service denoted by the specified QName under at least one participant policy that
     * matches the supplied consumer's participant policy. The actual policy matching is done by a
     * remote service registry. (called on service proxy creation, usually on consumer side)
     * 
     * @param service
     *        QName of the logical service (WSDL PortType name)
     * @param policyId
     *        the policy id
     * 
     * @return a Collection of CompoundServiceDescriptions containing the logical and physical
     *         descriptions of all matching providers
     * 
     * @throws ServiceAddressingException
     *         in case service description retrieval fails
     */
    Collection fetchAllServiceDescription(QName service, String policyId) throws InternalServiceDiscoveryException;

    /**
     * This method retrieves the service description of the service provider which provides the
     * logical service denoted by the specified QName and whose name is equal to the providerID
     * (called on service skeleton creation, usually on provider side).
     * 
     * @param serviceName
     *        QName of the logical service (WSDL PortType name)
     * @param providerID
     *        QName of the service provider (WSDL Service name)
     * @param defaultConsumerPolicyId
     *        the default consumer policy id
     * 
     * @return a CompoundServiceDescription containing the provider's logical and physical
     *         description
     * 
     * @throws ServiceAddressingException
     *         in case service description retrieval fails
     */
    CompoundServiceDescription fetchServiceDescription(QName serviceName, QName providerID, String defaultConsumerPolicyId)
            throws InternalServiceDiscoveryException;

    /**
     * This method retrieves the service description of a service provider which provides the
     * logical service denoted by the specified QName under at least one participant policy that
     * matches the supplied consumer's participant policy. The actual policy matching is done by a
     * remote service registry. (called on service proxy creation, usually on consumer side)
     * 
     * @param service
     *        QName of the logical service (WSDL PortType name)
     * @param policyId
     *        the policy id
     * 
     * @return a CompoundServiceDescription containing the logical and physical description of a
     *         provider
     * 
     * @throws ServiceAddressingException
     *         in case service description retrieval fails
     */
    CompoundServiceDescription fetchServiceDescription(QName service, String policyId) throws InternalServiceDiscoveryException;

    /**
     * Gets the service description.
     * 
     * @param serviceName
     *        the service name
     * 
     * @return the service description
     */
    CompoundServiceDescription getServiceDescription(QName serviceName);

}
