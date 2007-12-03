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
package org.eclipse.swordfish.core.components.resolver.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescriptionFactory;
import org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver;
import org.eclipse.swordfish.core.components.srproxy.ServiceInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo;
import org.eclipse.swordfish.core.components.srproxy.SrProxy;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;

/**
 * The Class ServiceDescriptionResolverBean.
 */
public class ServiceDescriptionResolverBean implements ServiceDescriptionResolver {

    /** The log. */
    private static Log log = SBBLogFactory.getLog(ServiceDescriptionResolver.class);

    /** The sr proxy. */
    private SrProxy srProxy;

    /** The csd factory. */
    private CompoundServiceDescriptionFactory csdFactory;

    /** The kernel. */
    private Kernel kernel;

    /** The service descriptions. */
    private ConcurrentMap serviceDescriptions = new ConcurrentHashMap();

    /**
     * destroy method.
     */
    public void destroy() {
        this.srProxy = null;
        this.csdFactory = null;
        this.kernel = null;
        if (this.serviceDescriptions != null) {
            this.serviceDescriptions.clear();
            this.serviceDescriptions = null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver#fetchAllServiceDescription(javax.xml.namespace.QName,
     *      org.eclipse.swordfish.core.components.policy.participant.ParticipantPolicy)
     */
    public Collection fetchAllServiceDescription(final QName service, final String policyId)
            throws InternalServiceDiscoveryException {
        ServiceInfo serviceInfo;
        ServiceProviderInfo serviceProviderInfo;
        Collection providerCollection = null;
        CompoundServiceDescription serviceDescription;
        try {
            serviceInfo = this.srProxy.getServiceDescription(service);
        } catch (Exception e) {
            throw new InternalServiceDiscoveryException("Retrieval of service description for service " + service + " failed. ", e);
        }

        try {
            providerCollection = this.srProxy.findServiceProviders(service, policyId);

        } catch (Exception e) {
            throw new InternalServiceDiscoveryException("Retrieval of service provider description for service " + service
                    + " failed. ", e);
        }

        Iterator providerIterator = providerCollection.iterator();
        ArrayList providerList = new ArrayList();
        while (providerIterator.hasNext()) {
            serviceProviderInfo = (ServiceProviderInfo) providerIterator.next();
            try {
                serviceDescription =
                        this.getCsdFactory().createCompoundServiceDescription(serviceInfo.getServiceDescription(),
                                serviceProviderInfo.getServiceProviderDescription(), serviceProviderInfo.getAgreedPolicy());
                providerList.add(serviceDescription);
                this.serviceDescriptions.put(serviceDescription.getServiceQName(), serviceDescription);
            } catch (Exception e) {
                throw new InternalServiceDiscoveryException(e.getMessage(), e);
            }
        }
        return providerList;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver#fetchServiceDescription(javax.xml.namespace.QName,
     *      javax.xml.namespace.QName, java.lang.String)
     */
    public CompoundServiceDescription fetchServiceDescription(final QName serviceName, final QName providerID,
            final String defaultConsumerPolicyId) throws InternalServiceDiscoveryException {
        ServiceInfo serviceInfo = null;
        ServiceProviderInfo serviceProviderInfo = null;
        CompoundServiceDescription serviceDescription = null;
        Collection providerCollection = null;

        try {
            // 1. get the service description
            serviceInfo = this.srProxy.getServiceDescription(serviceName);
        } catch (Exception e) {
            throw new InternalServiceDiscoveryException(
                    "Retrieval of service description for service " + serviceName + " failed. ", e);
        }

        // 2. get the service provider description
        if (null != defaultConsumerPolicyId) {
            // a. if we have a default consumer policy, we do a regular
            // service lookup w/ policy matching
            try {
                providerCollection = this.srProxy.findServiceProviders(serviceName, defaultConsumerPolicyId);
                // now we iterate over the returned list of matching service
                // providers to find one that matches our own
                // provider ID (in other words: we're looking for ourselves)
                for (Iterator iter = providerCollection.iterator(); iter.hasNext();) {
                    serviceProviderInfo = (ServiceProviderInfo) iter.next();
                    if (null != serviceProviderInfo.getServiceProviderDescription().getService(providerID)) {
                        break;
                    } else {
                        serviceProviderInfo = null;
                    }
                }
                // if at his point serviceProviderInfo not null, we have a
                // serviceProviderInfo with an agreed policy that matches
                // our
                // default consumer policy. otherwise there either was no
                // match
                // or ours was included in the list of matching providers
            } catch (Exception e) {
                try {
                    serviceProviderInfo = this.srProxy.getServiceProvider(providerID);
                    log.info("Service lookup with DefaultConsumerPolicy failed for provider " + serviceName.toString()
                            + ", continuing without agreed policy.");
                } catch (Exception e2) {
                    throw new InternalServiceDiscoveryException(
                            "No service provider description could be retrieved from Service Registry for service "
                                    + serviceName.toString() + " and ProviderID " + providerID.toString());
                }
            }
        }
        if (null == serviceProviderInfo) {
            // b. if we don't have a default consumer policy
            // we simply get our service provider description w/o policy
            // matching.
            // in this case, serviceProviderInfo won't include an agreed
            // policy (translates to: we won't support being called by
            // consumers which don't send an agreed policy)
            try {
                serviceProviderInfo = this.srProxy.getServiceProvider(providerID);
            } catch (Exception e2) {
                throw new InternalServiceDiscoveryException(
                        "No service provider description could be retrieved from Service Registry for service "
                                + serviceName.toString() + " and ProviderID " + providerID.toString());
            }

        }
        if (null == serviceProviderInfo)
            throw new InternalServiceDiscoveryException(
                    "No service provider description could be retrieved from Service Registry for service "
                            + serviceName.toString() + " and ProviderID " + providerID.toString());
        // 3. now try to fetch the provider policies
        List providerPolicies = null;
        try {
            providerPolicies = this.srProxy.getProviderPolicies(providerID);
        } catch (Exception e) {
            log.debug("Could not retrieve provider policies for provider " + serviceName.toString() + ": " + e);
        }
        // 4. now that we have both a serviceInfo and a serviceProviderInfo, we
        // create a CompoundServiceDescription and store it
        // in our map of available service descriptions
        try {
            serviceDescription =
                    this.getCsdFactory().createCompoundServiceDescription(serviceInfo.getServiceDescription(),
                            serviceProviderInfo.getServiceProviderDescription(), serviceProviderInfo.getAgreedPolicy(),
                            providerPolicies);
            this.serviceDescriptions.put(serviceDescription.getServiceQName(), serviceDescription);
        } catch (Exception e) {
            throw new InternalServiceDiscoveryException(e.getMessage(), e);
        }
        return serviceDescription;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver#fetchServiceDescription(javax.xml.namespace.QName,
     *      org.eclipse.swordfish.core.components.policy.participant.ParticipantPolicy)
     */
    public CompoundServiceDescription fetchServiceDescription(final QName service, final String policyId)
            throws InternalServiceDiscoveryException {
        ServiceInfo serviceInfo;
        ServiceProviderInfo serviceProviderInfo;
        CompoundServiceDescription serviceDescription;

        try {
            serviceInfo = this.srProxy.getServiceDescription(service);
        } catch (Exception e) {
            throw new InternalServiceDiscoveryException("Retrieval of service description for service " + service + " failed. ", e);
        }

        try {
            serviceProviderInfo = this.srProxy.findServiceProvider(service, policyId);
        } catch (Exception e) {
            throw new InternalServiceDiscoveryException("Retrieval of service provider description for service " + service
                    + " failed. ", e);
        }

        try {
            serviceDescription =
                    this.getCsdFactory().createCompoundServiceDescription(serviceInfo.getServiceDescription(),
                            serviceProviderInfo.getServiceProviderDescription(), serviceProviderInfo.getAgreedPolicy());
        } catch (Exception e) {
            throw new InternalServiceDiscoveryException(e.getMessage(), e);
        }

        this.serviceDescriptions.put(serviceDescription.getServiceQName(), serviceDescription);
        return serviceDescription;
    }

    /**
     * Gets the csd factory.
     * 
     * @return the csd factory
     */
    public CompoundServiceDescriptionFactory getCsdFactory() {
        return this.csdFactory;
    }

    /**
     * Gets the kernel.
     * 
     * @return the kernel
     */
    public Kernel getKernel() {
        return this.kernel;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver#getServiceDescription(javax.xml.namespace.QName)
     */
    public CompoundServiceDescription getServiceDescription(final QName serviceName) {
        return (CompoundServiceDescription) this.serviceDescriptions.get(serviceName);
    }

    /**
     * Gets the sr proxy.
     * 
     * @return the sr proxy
     */
    public SrProxy getSrProxy() {
        return this.srProxy;
    }

    /**
     * Sets the csd factory.
     * 
     * @param csdFactory
     *        the new csd factory
     */
    public void setCsdFactory(final CompoundServiceDescriptionFactory csdFactory) {
        this.csdFactory = csdFactory;
    }

    /**
     * Sets the kernel.
     * 
     * @param kernel
     *        the new kernel
     */
    public void setKernel(final Kernel kernel) {
        this.kernel = kernel;
    }

    /**
     * Sets the sr proxy.
     * 
     * @param srProxy
     *        the new sr proxy
     */
    public void setSrProxy(final SrProxy srProxy) {
        this.srProxy = srProxy;
    }

}
