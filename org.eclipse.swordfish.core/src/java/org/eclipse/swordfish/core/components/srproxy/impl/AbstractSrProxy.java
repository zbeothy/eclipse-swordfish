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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.Definition;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.srproxy.ServiceInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceNotFoundException;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderNotFoundException;
import org.eclipse.swordfish.core.components.srproxy.SrProxy;
import org.eclipse.swordfish.core.components.srproxy.SrProxyCache;
import org.eclipse.swordfish.core.components.srproxy.SrProxyCommunicationException;
import org.eclipse.swordfish.core.components.srproxy.SrProxyException;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;

/**
 * This class is a proxy for the Service Registry. The methods of the SrProxyInterface are
 * implemented such that they retrieve descriptions from a local cache. For each such method, an
 * abstract method (xxxRemote) is defined that can be implemented to do the actual call to the
 * remote service registry.
 * 
 */
public abstract class AbstractSrProxy implements SrProxy {

    /** Cache *. */
    private SrProxyCache cache;

    /** definition helper. */
    private DefinitionHelper definitionHelper = null;

    /**
     * constructor.
     * 
     * @throws Exception
     *         exception
     */
    public AbstractSrProxy() throws Exception {
        this.definitionHelper = DefinitionHelper.getInstance();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#findServiceProvider(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public ServiceProviderInfo findServiceProvider(final QName porttypeQName, final String policyId) throws SrProxyException {
        ServiceProviderInfo result = null;

        Map providers = this.getCache().getProviders(porttypeQName.toString(), policyId.toString()); // TODOpolicy.getId());
        if ((providers != null) && !providers.isEmpty()) {
            for (Iterator iter = providers.keySet().iterator(); iter.hasNext();) {
                String providerId = (String) iter.next();
                Definition definition = this.getCache().getSPDX(providerId);
                AgreedPolicy agreedPolicy = (AgreedPolicy) providers.get(providerId);
                result = new ServiceProviderInfoImpl(definition, agreedPolicy);
                break;
            }
        } else {
            try {
                result = this.findServiceProviderRemote(porttypeQName, policyId);
            } catch (Exception e) {
                throw new SrProxyException(e);
            }
        }
        if (null == result)
            throw new ServiceProviderNotFoundException("No service provider found for service name " + porttypeQName.toString()
                    + " and policy ID " + policyId); // TODOpolicy.getId());
        return result;
    }

    /**
     * Find service providers.
     * 
     * @param porttypeQName
     *        the porttype Q name
     * @param policyId
     *        the policy id
     * 
     * @return the collection
     * 
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxy#xfind_serviceProviderDescription(
     *      javax.xml.namespace.QName, org.eclipse.swordfish.policy.Policy)
     */
    public Collection/* <ServiceProviderInfo> */findServiceProviders(final QName porttypeQName, final String policyId)
            throws SrProxyException {

        Collection providerList = null;

        Map providers = this.getCache().getProviders(porttypeQName.toString(), policyId); // TODOpolicy.getId());
        if ((null != providers) && !providers.isEmpty()) {
            for (Iterator iter = providers.keySet().iterator(); iter.hasNext();) {
                String providerId = (String) iter.next();
                Definition definition = this.getCache().getSPDX(providerId);
                AgreedPolicy agreedPolicy = (AgreedPolicy) providers.get(providerId);
                if (null == providerList) {
                    providerList = new ArrayList();
                }

                if ((definition != null) && (agreedPolicy != null)) {
                    providerList.add(new ServiceProviderInfoImpl(definition, agreedPolicy));
                }
            }
        } else {
            try {
                providerList = this.findServiceProvidersRemote(porttypeQName, policyId);
            } catch (Exception e) {
                throw new SrProxyException(e);
            }
        }

        if ((null == providerList) || (providerList.size() == 0))
            throw new ServiceProviderNotFoundException("No service providers found for service name " + porttypeQName.toString()
                    + " and policy ID " + policyId); // TODOpolicy.getId());
        return providerList;
    }

    /**
     * Gets the cache.
     * 
     * @return Returns the cache.
     */
    public SrProxyCache getCache() {
        return this.cache;
    }

    public DefinitionHelper getDefinitionHelper() {
        return this.definitionHelper;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getOperationPolicy(java.lang.String)
     */
    public OperationPolicy getOperationPolicy(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        return this.getOperationPolicyRemote(policyId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getParticipantPolicy(java.lang.String)
     */
    public ParticipantPolicy getParticipantPolicy(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        return this.getParticipantPolicyRemote(policyId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getParticipantPolicyIds(javax.xml.namespace.QName)
     */
    public List/* <String> */getParticipantPolicyIds(final QName providerId) throws SrProxyCommunicationException,
            SrProxyException {
        return this.getParticipantPolicyIdsRemote(providerId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getProviderPolicies(javax.xml.namespace.QName)
     */
    public List/* <ParticipantPolicy> */getProviderPolicies(final QName serviceQName) throws SrProxyCommunicationException,
            SrProxyException {
        return new ArrayList();
        /*
         * List result = null; result = getCache().getPolicies(serviceQName.toString()); if
         * (result.isEmpty()) { try { result = getProviderPoliciesRemote(serviceQName); } catch
         * (Exception e) { throw new SrProxyException(e); } } return result;
         */
    }

    /**
     * Gets the service description.
     * 
     * @param portTypeQName
     *        the port type Q name
     * 
     * @return the service description
     * 
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxy#xfind_and_get_serviceDescription(QName)
     */
    public ServiceInfo getServiceDescription(final QName portTypeQName) throws SrProxyException {

        ServiceInfo result = null;

        Definition serviceDescription = this.getCache().getSDX(portTypeQName.toString());
        if (null != serviceDescription) {
            result = new ServiceInfoImpl(serviceDescription);
        } else {
            try {
                result = this.getServiceDescriptionRemote(portTypeQName);
            } catch (Exception e) {
                throw new SrProxyException(e);
            }
        }

        if (null == result) throw new ServiceNotFoundException("No service description found for ", portTypeQName.toString());
        return result;
    }

    /**
     * Gets the service provider.
     * 
     * @param serviceQName
     *        the service Q name
     * 
     * @return the service provider
     * 
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxy#xfind_serviceProviderDescription(
     *      javax.xml.namespace.QName, org.eclipse.swordfish.policy.Policy)
     */
    public ServiceProviderInfo getServiceProvider(final QName serviceQName) throws SrProxyException {

        ServiceProviderInfo result = null;

        Definition serviceProviderDescription = this.getCache().getSPDX(serviceQName.toString());
        if (null != serviceProviderDescription) {
            result = new ServiceProviderInfoImpl(serviceProviderDescription, null);
        } else {
            try {
                result = this.getServiceProviderRemote(serviceQName);
            } catch (Exception e) {
                throw new SrProxyException(e);
            }
        }
        if (null == result)
            throw new ServiceProviderNotFoundException("No data found for service provider", serviceQName.toString());
        return result;
    }

    /**
     * Sets the cache.
     * 
     * @param cache
     *        The cache to set.
     */
    public void setCache(final SrProxyCache cache) {
        this.cache = cache;
    }

    public void setDefinitionHelper(final DefinitionHelper definitionHelper) {
        this.definitionHelper = definitionHelper;
    }

    /**
     * Find service provider remote.
     * 
     * @param qualifiedServiceName
     *        the qualified service name
     * @param policyId
     *        the policy id
     * 
     * @return the service provider info
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     */
    abstract ServiceProviderInfo findServiceProviderRemote(QName qualifiedServiceName, String policyId)
            throws SrProxyCommunicationException, SrProxyException;

    /**
     * Does the call to the remote service registry.
     * 
     * @param qualifiedServiceName
     *        the qualified service name
     * @param policyId
     *        the policy id
     * 
     * @return the collection
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxy#xfind_serviceProvider(QName, ParticipantPolicy)
     */
    abstract Collection findServiceProvidersRemote(QName qualifiedServiceName, String policyId)
            throws SrProxyCommunicationException, SrProxyException;

    /**
     * Gets the operation policy remote.
     * 
     * @param policyId
     *        the policy id
     * 
     * @return the operation policy remote
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     */
    abstract OperationPolicy getOperationPolicyRemote(String policyId) throws SrProxyCommunicationException, SrProxyException;

    /**
     * Gets the participant policy ids remote.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return the participant policy ids remote
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     */
    abstract List/* <String> */getParticipantPolicyIdsRemote(QName providerId) throws SrProxyCommunicationException,
            SrProxyException;

    /**
     * Gets the participant policy remote.
     * 
     * @param policyId
     *        the policy id
     * 
     * @return the participant policy remote
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     */
    abstract ParticipantPolicy getParticipantPolicyRemote(String policyId) throws SrProxyCommunicationException, SrProxyException;

    /**
     * Does the call to the remote service registry.
     * 
     * @param qualifiedServiceName
     *        the qualified service name
     * 
     * @return the provider policies remote
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxy#xfind_serviceProvider(QName, ParticipantPolicy)
     */
    abstract List/* <ParticipantPolicy> */getProviderPoliciesRemote(QName qualifiedServiceName)
            throws SrProxyCommunicationException, SrProxyException;

    /**
     * Does the call to the remote service registry.
     * 
     * @param qualifiedServiceName
     *        the qualified service name
     * 
     * @return the service description remote
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxy#xfind_and_get_serviceDescription(QName)
     */
    abstract ServiceInfo getServiceDescriptionRemote(QName qualifiedServiceName) throws SrProxyCommunicationException,
            SrProxyException;

    /**
     * Does the call to the remote service registry.
     * 
     * @param qualifiedServiceName
     *        the qualified service name
     * 
     * @return the service provider remote
     * 
     * @throws SrProxyCommunicationException
     * @throws SrProxyException
     * 
     * @see org.eclipse.swordfish.sregproxy.SrProxy#xfind_serviceProvider(QName, ParticipantPolicy)
     */
    abstract ServiceProviderInfo getServiceProviderRemote(QName qualifiedServiceName) throws SrProxyCommunicationException,
            SrProxyException;
}
