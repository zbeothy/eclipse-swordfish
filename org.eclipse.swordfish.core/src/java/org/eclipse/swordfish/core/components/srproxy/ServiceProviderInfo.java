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
import javax.wsdl.Definition;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;

/**
 * The Interface ServiceProviderInfo.
 */
public interface ServiceProviderInfo {

    /**
     * Adds the provider policy.
     * 
     * @param policy
     *        the policy
     */
    void addProviderPolicy(ParticipantPolicy policy);

    /**
     * Gets the agreed policy.
     * 
     * @return AgreedPolicy agreed policy for the provider
     */
    AgreedPolicy getAgreedPolicy();

    /**
     * Gets the provider policies.
     * 
     * @return the provider policies
     */
    Collection getProviderPolicies();

    /**
     * Gets the provider policy.
     * 
     * @param policyId
     *        the policy id
     * 
     * @return the provider policy
     */
    ParticipantPolicy getProviderPolicy(String policyId);

    /**
     * Gets the service provider description.
     * 
     * @return Definition wsdl definition for Service provider description
     */
    Definition getServiceProviderDescription();

}
