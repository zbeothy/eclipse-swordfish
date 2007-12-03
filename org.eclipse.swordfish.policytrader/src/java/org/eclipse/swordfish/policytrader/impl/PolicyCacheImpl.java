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
package org.eclipse.swordfish.policytrader.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.callback.PolicyResolver;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;

/**
 * The Class PolicyCacheImpl.
 */
class PolicyCacheImpl {

    /** Participant policies cached by their identities as keys. */
    private final Map participantPolicies = new HashMap();

    /** Operation policies cached by their identities as keys. */
    private final Map operationPolicies = new HashMap();

    /** Index for cached trading results. */
    private final Map tradingResultsIndex = new HashMap();

    /** Map of cached trading results. */
    private final Map tradingResults = new HashMap();

    /** Index for cached traded operation agreed policies. */
    private final Map tradedPoliciesIndex = new HashMap();

    /** Map of cached traded operation agreed policies. */
    private final Map tradedPolicies = new HashMap();

    /** Slot for policy resolver set by client. */
    private PolicyResolver policyResolver = null;

    /**
     * Instantiates a new policy cache impl.
     */
    protected PolicyCacheImpl() {
        super();
    }

    /**
     * Gets the policy resolver.
     * 
     * @return the policy resolver
     */
    public PolicyResolver getPolicyResolver() {
        return this.policyResolver;
    }

    /**
     * Sets the policy resolver.
     * 
     * @param policyResolver
     *        the new policy resolver
     */
    public void setPolicyResolver(final PolicyResolver policyResolver) {
        this.policyResolver = policyResolver;
    }

    /**
     * Get a traded operation agreed policy from cache.
     * 
     * @param consumerPID
     *        consumer-side operation policy identifier
     * @param providerPID
     *        provider-side operation policy identifier
     * 
     * @return cached policy or <code>null</code> if none has been cached
     */
    Policy cachedTradedPolicy(final StandardOperationPolicyIdentity consumerPID, final StandardOperationPolicyIdentity providerPID) {
        synchronized (this.tradedPolicies) {
            return (Policy) this.tradedPolicies.get(new PolicyKey(consumerPID, providerPID));
        }
    }

    /**
     * Get the cache content for a trading result.
     * 
     * @param consumerPID
     *        consumer-side participant policy identifier
     * @param providerPID
     *        provider-side participant policy identifier
     * @param sid
     *        the sid
     * 
     * @return trading result or <code>null</code> if nothing has been cached
     */
    TradingResult cachedTradingResult(final StandardParticipantPolicyIdentity consumerPID,
            final StandardParticipantPolicyIdentity providerPID, final StandardServiceDescriptionIdentity sid) {
        synchronized (this.tradingResults) {
            return (TradingResult) this.tradingResults.get(new TradingResult(consumerPID, providerPID, sid));
        }
    }

    /**
     * Put a traded operation agreed policy into cache.
     * 
     * @param consumerPID
     *        consumer-side operation policy identifier
     * @param providerPID
     *        provider-side operation policy identifier
     * @param policy
     *        policy to be cached
     */
    void cacheTradedPolicy(final StandardOperationPolicyIdentity consumerPID, final StandardOperationPolicyIdentity providerPID,
            final Policy policy) {
        synchronized (this.tradedPolicies) {
            final PolicyKey key = new PolicyKey(consumerPID, providerPID);
            this.tradedPolicies.put(key, policy);
            Set s = (Set) this.tradedPoliciesIndex.get(consumerPID);
            if (null == s) {
                s = new HashSet();
                this.tradedPoliciesIndex.put(consumerPID, s);
            }
            s.add(key);
            s = (Set) this.tradedPoliciesIndex.get(providerPID);
            if (null == s) {
                s = new HashSet();
                this.tradedPoliciesIndex.put(providerPID, s);
            }
            s.add(key);
        }
    }

    /**
     * Put a trading result into cache.
     * 
     * @param tradingResult
     *        trading result to be cached.
     */
    void cacheTradingResult(final TradingResult tradingResult) {
        synchronized (this.tradingResults) {
            this.tradingResults.put(tradingResult, tradingResult);
            Set s = (Set) this.tradingResultsIndex.get(tradingResult.getConsumerPID());
            if (null == s) {
                s = new HashSet();
                this.tradingResultsIndex.put(tradingResult.getConsumerPID(), s);
            }
            s.add(tradingResult);
            s = (Set) this.tradingResultsIndex.get(tradingResult.getProviderPID());
            if (null == s) {
                s = new HashSet();
                this.tradingResultsIndex.put(tradingResult.getProviderPID(), s);
            }
            s.add(tradingResult);
        }
    }

    /**
     * Invalidate all.
     */
    void invalidateAll() {
        synchronized (this.participantPolicies) {
            this.participantPolicies.clear();
        }
        synchronized (this.operationPolicies) {
            this.operationPolicies.clear();
        }
        synchronized (this.tradedPolicies) {
            this.tradedPolicies.clear();
            this.tradedPoliciesIndex.clear();
        }
        synchronized (this.tradingResults) {
            this.tradingResults.clear();
            this.tradingResultsIndex.clear();
        }
    }

    /**
     * Invalidate operation policy.
     * 
     * @param identity
     *        the identity
     */
    void invalidateOperationPolicy(final OperationPolicyIdentity identity) {
        // TODO specific invalidation
        this.invalidateAll();
    }

    /**
     * Invalidate participant policy.
     * 
     * @param identity
     *        the identity
     */
    void invalidateParticipantPolicy(final ParticipantPolicyIdentity identity) {
        // TODO specific invalidation
        this.invalidateAll();
    }

    /**
     * Invalidate participant policy with operation policies.
     * 
     * @param identity
     *        the identity
     */
    void invalidateParticipantPolicyWithOperationPolicies(final ParticipantPolicyIdentity identity) {
        // TODO specific invalidation
        this.invalidateAll();
    }

    /**
     * Get a WS-Policy either from cache or invoke the client-supplied policy resolver if none has
     * been cached.
     * 
     * @param id
     *        operation policy identifier
     * 
     * @return WS-Policy for the operation
     * 
     * @throws BackendException
     *         any exception thrown by the client-supplied resolver
     */
    Policy resolveOperationPolicy(final OperationPolicyIdentity id) throws BackendException {
        return this.resolveOperationPolicy(id, this.policyResolver);
    }

    /**
     * Get a WS-Policy either from cache or invoke the client-supplied policy resolver if none has
     * been cached.
     * 
     * @param id
     *        operation policy identifier
     * @param resolver
     *        the resolver
     * 
     * @return WS-Policy for the operation
     * 
     * @throws BackendException
     *         any exception thrown by the client-supplied resolver
     */
    Policy resolveOperationPolicy(final OperationPolicyIdentity id, final PolicyResolver resolver) throws BackendException {
        synchronized (this.operationPolicies) {
            Policy ret = (Policy) this.operationPolicies.get(id);
            if (null == ret) {
                final OperationPolicyImpl oPol = (OperationPolicyImpl) resolver.resolveOperationPolicy(id);
                ret = (null == oPol) ? null : oPol.getWsPolicy();
                if (null != ret) {
                    this.operationPolicies.put(id, ret);
                }
            }
            return ret;
        }
    }

    /**
     * Get a participant policy either from cache or invoke the standard client-supplied policy
     * resolver if none has been cached.
     * 
     * @param id
     *        participant policy identifier
     * 
     * @return participant policy
     * 
     * @throws BackendException
     *         any exception thrown by the client-supplied resolver
     */
    ParticipantPolicy resolveParticipantPolicy(final StandardParticipantPolicyIdentity id) throws BackendException {
        return this.resolveParticipantPolicy(id, this.policyResolver);
    }

    /**
     * Get a participant policy either from cache or invoke the client-supplied policy resolver if
     * none has been cached.
     * 
     * @param id
     *        participant policy identifier
     * @param resolver
     *        the resolver to use when fetching unknown policies
     * 
     * @return participant policy
     * 
     * @throws BackendException
     *         any exception thrown by the client-supplied resolver
     */
    ParticipantPolicy resolveParticipantPolicy(final StandardParticipantPolicyIdentity id, final PolicyResolver resolver)
            throws BackendException {
        synchronized (this.participantPolicies) {
            ParticipantPolicy pp = (ParticipantPolicy) this.participantPolicies.get(id);
            if (null == pp) {
                pp = resolver.resolveParticipantPolicy(id);
                this.participantPolicies.put(id, pp);
            }
            return pp;
        }
    }

    /**
     * Resolve service descriptor.
     * 
     * @param id
     *        the id
     * 
     * @return the service descriptor
     * 
     * @throws BackendException
     */
    ServiceDescriptor resolveServiceDescriptor(final StandardServiceDescriptionIdentity id) throws BackendException {
        return this.resolveServiceDescriptor(id, this.policyResolver);
    }

    /**
     * Resolve service descriptor.
     * 
     * @param id
     *        the id
     * @param resolver
     *        the resolver
     * 
     * @return the service descriptor
     * 
     * @throws BackendException
     */
    ServiceDescriptor resolveServiceDescriptor(final StandardServiceDescriptionIdentity id, final PolicyResolver resolver)
            throws BackendException {
        return resolver.resolveServiceDescriptor(id);
    }

    /**
     * Key for caching traded operation policies.
     */
    private static class PolicyKey {

        /** Consumer-side operation policy identifier. */
        private final StandardOperationPolicyIdentity consumerPID;

        /** Provider-side operation policy identifier. */
        private final StandardOperationPolicyIdentity providerPID;

        /**
         * Internal constructor.
         * 
         * @param consumerPID
         *        consumer-side operation policy identifier
         * @param providerPID
         *        provider-side operation policy identifier
         */
        protected PolicyKey(final StandardOperationPolicyIdentity consumerPID, final StandardOperationPolicyIdentity providerPID) {
            super();
            this.consumerPID = consumerPID;
            this.providerPID = providerPID;
        }

        /*
         * {@inheritDoc}
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object other) {
            if ((null == other) || !(other instanceof PolicyKey)) return false;
            final PolicyKey o = (PolicyKey) other;
            if (null == this.consumerPID) {
                if (null != o.getConsumerPID()) return false;
            } else {
                if (!this.consumerPID.equals(o.getConsumerPID())) return false;
            }
            if (null == this.providerPID) {
                if (null != o.getProviderPID()) return false;
            } else {
                if (!this.providerPID.equals(o.getProviderPID())) return false;
            }
            return true;
        }

        /**
         * Get the consumer-side operation policy identifier.
         * 
         * @return operation policy identifier
         */
        public StandardOperationPolicyIdentity getConsumerPID() {
            return this.consumerPID;
        }

        /**
         * Get the provider-side operation policy identifier.
         * 
         * @return operation policy identifier
         */
        public StandardOperationPolicyIdentity getProviderPID() {
            return this.providerPID;
        }

        /*
         * {@inheritDoc}
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return (null == this.consumerPID ? 0 : this.consumerPID.hashCode())
                    + (null == this.providerPID ? 0 : this.providerPID.hashCode());
        }

    }

}
