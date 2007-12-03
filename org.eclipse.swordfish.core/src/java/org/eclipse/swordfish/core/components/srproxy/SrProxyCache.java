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

import java.util.List;
import java.util.Map;
import javax.wsdl.Definition;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * The Interface SrProxyCache.
 */
public interface SrProxyCache {

    /**
     * Gets the agreed policy.
     * 
     * @param service
     *        the service
     * @param providerId
     *        the provider id
     * 
     * @return the agreed policy
     */
    AgreedPolicy getAgreedPolicy(String service, String providerId);

    /**
     * Gets the policies.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return the policies
     */
    List/* <ParticipantPolicy> */getPolicies(String providerId);

    /**
     * Gets the providers.
     * 
     * @param service
     *        the service
     * @param policyId
     *        the policy id
     * 
     * @return the providers
     */
    Map getProviders(String service, String policyId);

    /**
     * Gets the SDX.
     * 
     * @param service
     *        the service
     * 
     * @return the SDX
     */
    Definition getSDX(String service);

    /**
     * Gets the SPDX.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return the SPDX
     */
    Definition getSPDX(String providerId);

    /**
     * Gets the uddi key.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return the uddi key
     */
    String getUddiKey(String providerId);

    /**
     * Checks for SDX.
     * 
     * @param service
     *        the service
     * 
     * @return true, if successful
     */
    boolean hasSDX(String service);

    /**
     * Checks for SPDX.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return true, if successful
     */
    boolean hasSPDX(String providerId);

    /**
     * Put agreed policy.
     * 
     * @param policy
     *        the policy
     */
    void putAgreedPolicy(AgreedPolicy policy);

    /**
     * Put SDX.
     * 
     * @param definition
     *        the definition
     */
    void putSDX(Definition definition);

    /**
     * Put SPDX.
     * 
     * @param definition
     *        the definition
     */
    void putSPDX(Definition definition);

    /**
     * Removes the agreed policy.
     * 
     * @param policy
     *        the policy
     */
    void removeAgreedPolicy(AgreedPolicy policy);

}
