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
package org.eclipse.swordfish.core.components.srproxy;

import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;

/**
 * This interface defines the Service Registry operations that can be executed using the Service
 * Registry Proxy.
 * 
 */
public interface SrProxy {

    /** The type identifier of the <code>org.eclipse.swordfish.core.components.srproxy.SrProxy</code>. */
    String ROLE = SrProxy.class.getName();

    /**
     * Find service provider.
     * 
     * @param service
     *        the service
     * @param policyId
     *        the policy id
     * 
     * @return the service provider info
     * 
     * @throws SrProxyException
     */
    ServiceProviderInfo findServiceProvider(QName service, String policyId) throws SrProxyException;

    /**
     * Service Registry operation xfind_serviceProvider.
     * 
     * @param portTypeQName
     *        a QName that identifies the service provider (namespace=targetNamespace of service
     *        provider description, localPart=/definitions/service@name)
     * @param policyId
     *        the policy id
     * 
     * @return a ServiceProviderInfo containing the agreed policy and the service provider
     *         description as a result of a request with the given parameter(s)
     * 
     * @throws SrProxyException
     *         if the operation cannot be completed. A ServiceProviderNotFoundException will be
     *         thrown if either no provider was registered for a service with the given QName or the
     *         given policy does not match to any provider registered for a service with the given
     *         QName.
     */
    Collection findServiceProviders(QName portTypeQName, String policyId) throws SrProxyException;

    /**
     * Gets the operation policy.
     * 
     * @param policyId
     *        the policy id
     * 
     * @return Policy
     * 
     * @throws SrProxyException
     *         exception
     * @throws SrProxyCommunicationException
     */
    OperationPolicy getOperationPolicy(String policyId) throws SrProxyCommunicationException, SrProxyException;

    /**
     * Gets the participant policy.
     * 
     * @param policyId
     *        the policy id
     * 
     * @return ParticipantPolicy
     * 
     * @throws SrProxyException
     *         exception
     * @throws SrProxyCommunicationException
     */
    ParticipantPolicy getParticipantPolicy(String policyId) throws SrProxyCommunicationException, SrProxyException;

    /**
     * Gets the participant policy ids.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return List of policy ids
     * 
     * @throws SrProxyException
     *         exception
     * @throws SrProxyCommunicationException
     */
    List/* <String> */getParticipantPolicyIds(QName providerId) throws SrProxyCommunicationException, SrProxyException;

    /**
     * Gets the provider policies.
     * 
     * @param serviceQName
     *        wsd service Qnam
     * 
     * @return List of ParticipantPolicy
     * 
     * @throws SrProxyException
     *         exception
     * @throws SrProxyCommunicationException
     */
    List/* <ParticipantPolicy> */getProviderPolicies(QName serviceQName) throws SrProxyCommunicationException, SrProxyException;

    /**
     * Service Registry operation xfind_and_get_serviceDescription.
     * 
     * @param qualifiedServiceName
     *        a QName that identifies the service (namespace=targetNamespace of service description,
     *        localPart=/definitions/portType@name)
     * 
     * @return a service description, or null if the Service Registry returns an empty list of WSDLs
     *         as a result of a request with the given parameter(s)
     * 
     * @throws SrProxyException
     *         if the operation cannot be completed. A ServiceNotFoundException will be thrown if
     *         the service with the given QName cannot be found in the Service Registry.
     */
    ServiceInfo getServiceDescription(QName qualifiedServiceName) throws SrProxyException;

    /**
     * Gets the service provider.
     * 
     * @param serviceQName
     *        wsd service Qname
     * 
     * @return ServiceProviderInfo ServiceProviderInfo
     * 
     * @throws SrProxyException
     *         exception
     */
    ServiceProviderInfo getServiceProvider(QName serviceQName) throws SrProxyException;

}
