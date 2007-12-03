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
import java.util.HashMap;
import java.util.Map;
import javax.wsdl.Definition;
import org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;

/**
 * The Class ServiceProviderInfoImpl.
 */
public class ServiceProviderInfoImpl implements ServiceProviderInfo {

    /** service provider description. */
    private Definition serviceProviderDesc = null;

    /** agreed policy. */
    private AgreedPolicy agreedPolicy = null;

    /** The provider policy map. */
    private Map providerPolicyMap = new HashMap();

    /**
     * The Constructor.
     * 
     * @param serviceProviderDesc
     *        provider desc
     * @param policy
     *        policy
     */
    public ServiceProviderInfoImpl(final Definition serviceProviderDesc, final AgreedPolicy policy) {
        this.serviceProviderDesc = serviceProviderDesc;
        this.agreedPolicy = policy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo#addProviderPolicy(org.eclipse.swordfish.policytrader.ParticipantPolicy)
     */
    public void addProviderPolicy(final ParticipantPolicy policy) {
        this.providerPolicyMap.put(policy.getId(), policy);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the agreed policy
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo#getAgreedPolicy()
     */
    public AgreedPolicy getAgreedPolicy() {
        return this.agreedPolicy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo#getProviderPolicies()
     */
    public Collection getProviderPolicies() {
        return this.providerPolicyMap.values();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo#getProviderPolicy(java.lang.String)
     */
    public ParticipantPolicy getProviderPolicy(final String policyId) {
        return (ParticipantPolicy) this.providerPolicyMap.get(policyId);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the service provider description
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.ServiceProviderInfo#getServiceProviderDescription()
     */
    public Definition getServiceProviderDescription() {
        return this.serviceProviderDesc;
    }

}
