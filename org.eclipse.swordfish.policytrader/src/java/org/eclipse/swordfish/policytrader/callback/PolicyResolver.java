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
package org.eclipse.swordfish.policytrader.callback;

import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;
import org.eclipse.swordfish.policytrader.PolicyTrader;
import org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;

/**
 * Interface which needs to be implemented by clients of the {@link PolicyTrader}.
 */
public interface PolicyResolver {

    /**
     * Returns an operation policy for a given key. The {@link PolicyFactory} must be used in order
     * to create policy objects from raw data.
     * 
     * @param identity
     *        policy key
     * 
     * @return policy resolved from the key or <code>null</code> if none
     * 
     * @throws BackendException
     *         on any failure in the backend of policy resolution
     */
    OperationPolicy resolveOperationPolicy(OperationPolicyIdentity identity) throws BackendException;

    /**
     * Return a participant policy for a given key. The {@link PolicyFactory} must be used in order
     * to create participant policy objects from raw data.
     * 
     * @param identity
     *        participant policy key
     * 
     * @return participant policy resolved from the key or <code>null</code> if none
     * 
     * @throws BackendException
     *         on any failure in the backend of policy resolution
     */
    ParticipantPolicy resolveParticipantPolicy(ParticipantPolicyIdentity identity) throws BackendException;

    /**
     * Return a service descriptor for a given service name. The {@link PolicyFactory} must be used
     * in order to create participant service descriptor objects from service description data.
     * 
     * @param identity
     *        wrapper for the <code>service</code> attribute from the corresponding participant
     *        policy and an optional <code>location</code> attribute
     * 
     * @return service descriptor for service <code>null</code> if none
     * 
     * @throws BackendException
     *         on any failure in the backend of service description resolution
     */
    ServiceDescriptor resolveServiceDescriptor(ServiceDescriptionIdentity identity) throws BackendException;
}
