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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.srproxy.ServiceInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceNotFoundException;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderNotFoundException;
import org.eclipse.swordfish.core.components.srproxy.SrProxyCommunicationException;
import org.eclipse.swordfish.core.components.srproxy.SrProxyException;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;

/**
 * This class is a local proxy to the service registry. It only contains functionality to retrieve
 * descriptions from a local cache. Therefore, all abstract methods that would do the actual call to
 * the remote service registry, simply return null in this class.
 * 
 */
public class LocalSrProxyBean extends AbstractSrProxy {

    /**
     * Creates a new LocalSrProxy.
     * 
     * @throws Exception
     *         exception
     */
    public LocalSrProxyBean() throws Exception {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getOperationPolicy(java.lang.String)
     */
    @Override
    public OperationPolicy getOperationPolicy(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getParticipantPolicy(java.lang.String)
     */
    @Override
    public ParticipantPolicy getParticipantPolicy(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        // Auto-generated method stub
        return null;
    }

    /**
     * Init.
     * 
     * @throws Exception
     *         exception
     */
    public void init() throws Exception {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#findServiceProviderRemote(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    @Override
    ServiceProviderInfo findServiceProviderRemote(final QName qualifiedServiceName, final String policyId) throws SrProxyException {
        throw new ServiceProviderNotFoundException("Cannot find requested ServiceProviderDescription in the local cache.");
    }

    /**
     * Find service providers remote.
     * 
     * @param qName
     *        the q name
     * @param policyId
     *        the policy id
     * 
     * @return the collection
     * 
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.AbstractSrProxy#xfind_serviceProviderRemote(
     *      javax.xml.namespace.QName, org.eclipse.swordfish.policy.participant.ParticipantPolicy)
     */
    @Override
    Collection findServiceProvidersRemote(final QName qName, final String policyId) throws SrProxyException {
        throw new ServiceProviderNotFoundException("Cannot find requested ServiceProviderDescription in the local cache.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getOperationPolicyRemote(java.lang.String)
     */
    @Override
    OperationPolicy getOperationPolicyRemote(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getParticipantPolicyIdsRemote(javax.xml.namespace.QName)
     */
    @Override
    List getParticipantPolicyIdsRemote(final QName providerId) throws SrProxyCommunicationException, SrProxyException {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getParticipantPolicyRemote(java.lang.String)
     */
    @Override
    ParticipantPolicy getParticipantPolicyRemote(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.impl.AbstractSrProxy#getProviderPoliciesRemote(javax.xml.namespace.QName)
     */
    @Override
    List getProviderPoliciesRemote(final QName qualifiedServiceName) throws SrProxyCommunicationException, SrProxyException {
        throw new ServiceProviderNotFoundException("Cannot find requested Provider policies in the local cache.");
    }

    /**
     * Gets the service description remote.
     * 
     * @param qName
     *        the q name
     * 
     * @return the service description remote
     * 
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.AbstractSrProxy#
     *      xfind_serviceDescriptionRemote(javax.xml.namespace.QName)
     */
    @Override
    ServiceInfo getServiceDescriptionRemote(final QName qName) throws SrProxyException {
        throw new ServiceNotFoundException("Cannot find requested ServiceDescription in the local cache.");
    }

    /**
     * Gets the service provider remote.
     * 
     * @param serviceQName
     *        the service Q name
     * 
     * @return the service provider remote
     * 
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.AbstractSrProxy#xfind_serviceProviderRemote(
     *      javax.xml.namespace.QName, org.eclipse.swordfish.policy.participant.ParticipantPolicy)
     */
    @Override
    ServiceProviderInfo getServiceProviderRemote(final QName serviceQName) throws SrProxyException {
        throw new ServiceProviderNotFoundException("Cannot find requested ServiceProviderDescription in the local cache.");
    }

}
