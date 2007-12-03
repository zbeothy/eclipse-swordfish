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
package org.eclipse.swordfish.policytrader;

import java.util.Properties;
import org.eclipse.swordfish.policy.util.PolicyProcessor;
import org.eclipse.swordfish.policytrader.callback.PolicyResolver;
import org.eclipse.swordfish.policytrader.callback.PolicyTradingListener;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedPolicyException;

/**
 * Main component for policy trading.
 */
public interface PolicyTrader {

    /** Agreed policy validity duration in milliseconds. */
    String VALIDITY_DURATION = "org.eclipse.swordfish.policytrader.ValidityDuration";

    /** Network overhead in milliseconds to consider when computing MaxResponseTime. */
    String NETWORK_OVERHEAD = PolicyProcessor.NETWORK_OVERHEAD;

    /**
     * Add an optional Listener which is informed about policy trading progress and reasons for
     * failure. The listener must be implemented by the client.
     * 
     * @param listener
     *        listener to be added
     */
    void addListener(PolicyTradingListener listener);

    /**
     * Get the factory for any PolicyTrader-related object.
     * 
     * @return the factory
     */
    PolicyFactory getPolicyFactory();

    /**
     * Get the current {@link PolicyResolver}.
     * 
     * The {@link PolicyResolver} has to be provided by the client of the PolicyTrader in order to
     * resolve the policy identifiers to real policies. Calls to
     * {@link tradePolicies(PolicyIdentity consumerPolicyID, PolicyIdentity providerPolicyID)} will
     * fail if a {@link PolicyResolver} has not been set.
     * 
     * @return the policy resolver
     */
    PolicyResolver getPolicyResolver();

    /**
     * Invalidate all policies and participant policies internally cached. They have to be newly
     * resolved by the {@link PolicyResolver} as soon as they are needed.
     */
    void invalidateAll();

    /**
     * Invalidate the {@link Policy} internally cached for a given key. The {@link Policy} for this
     * key has to be newly resolved by the {@link PolicyResolver} as soon as it is needed.
     * 
     * @param identity
     *        key for which the Policy is invalidated
     */
    void invalidateOperationPolicy(OperationPolicyIdentity identity);

    /**
     * Invalidate the {@link ParticipantPolicy} internally cached for a given key. The
     * {@link ParticipantPolicy} for this key has to be newly resolved by the {@link PolicyResolver}
     * as soon as it is needed. The policies referenced by the ParticipantPolicy are not
     * invalidated.
     * 
     * @param identity
     *        key for which the ParticipantPolicy is invalidated
     */
    void invalidateParticipantPolicy(ParticipantPolicyIdentity identity);

    /**
     * Invalidate the {@link ParticipantPolicy} internally cached for a given key together with all
     * policies referenced by the ParticipantPolicy. The {@link ParticipantPolicy} for this key and
     * referenced policies have to be newly resolved by the {@link PolicyResolver} as soon as they
     * are needed.
     * 
     * @param identity
     *        key for which the ParticipantPolicy is invalidated
     */
    void invalidateParticipantPolicyWithOperationPolicies(ParticipantPolicyIdentity identity);

    /**
     * Remove all policy trading listeners.
     */
    void removeAllListeners();

    /**
     * Remove a policy trading listener.
     * 
     * @param listener
     *        listener to be removed
     */
    void removeListener(PolicyTradingListener listener);

    /**
     * Set the current {@link PolicyResolver}.
     * 
     * The {@link PolicyResolver} has to be provided by the client of the PolicyTrader in order to
     * resolve the policy identifiers to real policies. Calls to
     * {@link tradePolicies(PolicyIdentity consumerPolicyID, PolicyIdentity providerPolicyID)} will
     * fail if a {@link PolicyResolver} has not been set.
     * 
     * @param policyResolver
     *        the policy resolver.
     */
    void setPolicyResolver(PolicyResolver policyResolver);

    /**
     * Sets the externally changeable properties.
     * 
     * @param properties
     *        the properties
     */
    void setProperties(Properties properties);

    /**
     * Trade two policies referenced by their respective identifiers. Service identifier must be
     * present and correct in both referenced policies
     * 
     * @param consumerPolicyID
     *        identifier for the consumer-side policy
     * @param providerPolicyID
     *        identifier for the provider-side policy
     * 
     * @return trading result as {@link AgreedPolicy} object or <code>null</code> if both policies
     *         cannot be matched
     * 
     * @throws BackendException
     *         any BackendException thrown by the PolicyResolver
     * @throws CorruptedPolicyException
     *         if a policy proves to be semantically invalid
     */
    AgreedPolicy tradePolicies(ParticipantPolicyIdentity consumerPolicyID, ParticipantPolicyIdentity providerPolicyID)
            throws BackendException, CorruptedPolicyException;

    /**
     * Trade two policies referenced by their respective identifiers.
     * 
     * @param consumerPolicyID
     *        identifier for the consumer-side policy
     * @param providerPolicyID
     *        identifier for the provider-side policy
     * @param serviceDescriptionID
     *        identifier for the service <br/>If <code>null</code> the service ID is take from the
     *        provider policy
     * 
     * @return trading result as {@link AgreedPolicy} object or <code>null</code> if both policies
     *         cannot be matched
     * 
     * @throws BackendException
     *         any BackendException thrown by the PolicyResolver
     * @throws CorruptedPolicyException
     *         if a policy proves to be semantically invalid
     */
    AgreedPolicy tradePolicies(ParticipantPolicyIdentity consumerPolicyID, ParticipantPolicyIdentity providerPolicyID,
            ServiceDescriptionIdentity serviceDescriptionID) throws BackendException, CorruptedPolicyException;

    /**
     * Trade two policies referenced by their respective identifiers using a special policy
     * resolver.
     * 
     * @param consumerPolicyID
     *        identifier for the consumer-side policy
     * @param providerPolicyID
     *        identifier for the provider-side policy
     * @param serviceDescriptionID
     *        identifier for the service <br/>If <code>null</code> the service ID is take from the
     *        provider policy
     * @param resolver
     *        <code>PolicyResolver</code> to use when fetching additional participant policy,
     *        operation policy or service description during execution of this call <br/>This
     *        resolver overrides the one set by <code>setPolicyResolver</code> <i>for this call
     *        only</i>. The parameter may not be null.
     * 
     * @return trading result as {@link AgreedPolicy} object or <code>null</code> if both policies
     *         cannot be matched
     * 
     * @throws BackendException
     *         any BackendException thrown by the PolicyResolver
     * @throws CorruptedPolicyException
     *         if a policy proves to be semantically invalid
     */
    AgreedPolicy tradePolicies(ParticipantPolicyIdentity consumerPolicyID, ParticipantPolicyIdentity providerPolicyID,
            ServiceDescriptionIdentity serviceDescriptionID, PolicyResolver resolver) throws BackendException,
            CorruptedPolicyException;
}
