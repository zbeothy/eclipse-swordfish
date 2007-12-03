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
package org.eclipse.swordfish.core.components.srproxy.mock;

import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.srproxy.ServiceInfo;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo;
import org.eclipse.swordfish.core.components.srproxy.SrProxy;
import org.eclipse.swordfish.core.components.srproxy.SrProxyCommunicationException;
import org.eclipse.swordfish.core.components.srproxy.SrProxyException;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import com.opensymphony.oscache.base.Cache;

/**
 * The Class SrProxyBean.
 */
public class SrProxyBean implements SrProxy {

    /** The cache. */
    private Cache cache = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#findServiceProvider(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public ServiceProviderInfo findServiceProvider(final QName service, final String policy) throws SrProxyException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#findServiceProviders(javax.xml.namespace.QName,
     *      org.eclipse.swordfish.core.components.policy.participant.ParticipantPolicy)
     */
    public Collection findServiceProviders(final QName portTypeQName, final String policy) throws SrProxyException {
        return null;
    }

    /**
     * Gets the cache.
     * 
     * @return Returns the cache.
     */
    public Cache getCache() {
        return this.cache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getOperationPolicy(java.lang.String)
     */
    public OperationPolicy getOperationPolicy(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getParticipantPolicy(java.lang.String)
     */
    public ParticipantPolicy getParticipantPolicy(final String policyId) throws SrProxyCommunicationException, SrProxyException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getParticipantPolicyIds(javax.xml.namespace.QName)
     */
    public List getParticipantPolicyIds(final QName providerId) throws SrProxyCommunicationException, SrProxyException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getProviderPolicies(javax.xml.namespace.QName)
     */
    public List getProviderPolicies(final QName serviceQName) throws SrProxyCommunicationException, SrProxyException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getServiceDescription(javax.xml.namespace.QName)
     */
    public ServiceInfo getServiceDescription(final QName qualifiedServiceName) throws SrProxyException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SrProxy#getServiceProvider(javax.xml.namespace.QName)
     */
    public ServiceProviderInfo getServiceProvider(final QName serviceQName) throws SrProxyException {
        return null;
    }

    /**
     * Gets the service provider.
     * 
     * @param portTypeQName
     *        the port type Q name
     * @param policy
     *        the policy
     * 
     * @return the service provider
     * 
     * @throws SrProxyException
     */
    public ServiceProviderInfo getServiceProvider(final QName portTypeQName, final String policy) throws SrProxyException {
        return null;
    }

    /**
     * Sets the cache.
     * 
     * @param cache
     *        The cache to set.
     */
    public void setCache(final Cache cache) {
        this.cache = cache;
    }
}
