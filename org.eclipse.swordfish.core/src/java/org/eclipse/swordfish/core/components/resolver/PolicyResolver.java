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

import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;

/**
 * the interface of the class that can resolve a policy by its name.
 */
public interface PolicyResolver extends org.eclipse.swordfish.policytrader.callback.PolicyResolver {

    /** the role of this interface to look it up. */
    String ROLE = PolicyResolver.class.getName();

    /**
     * Gets the assigned policy ids.
     * 
     * @param providerId
     *        the provider id
     * 
     * @return the assigned policy ids
     * 
     * @throws BackendException
     */
    List/* <String> */getAssignedPolicyIds(QName providerId) throws BackendException;

    /**
     * Gets the default consumer policy ID.
     * 
     * @return the default consumer policy ID
     */
    String getDefaultConsumerPolicyID();

    /**
     * Gets the default policy ID.
     * 
     * @return the default policy ID
     */
    String getDefaultPolicyID();

    /**
     * resolves the policy id from configuration by the policy name passed on papi.
     * 
     * @param policyName
     *        policy name
     * 
     * @return Qname, the policy id
     */
    String resolvePolicyID(String policyName);
}
