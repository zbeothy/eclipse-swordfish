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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.policy.util.PolicyProcessor;
import org.eclipse.swordfish.policy.util.UnexpectedPolicyProcessingException;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;
import org.eclipse.swordfish.policytrader.PolicyTrader;
import org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.callback.PolicyResolver;
import org.eclipse.swordfish.policytrader.callback.PolicyTradingListener;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedPolicyException;
import org.eclipse.swordfish.policytrader.exceptions.MissingPolicyResolverException;

/**
 * Implementation of the policy trader component.
 */
public class PolicyTraderImpl implements PolicyTrader {

    /** The Constant log. */
    private static final Log LOG = LogFactory.getLog(PolicyTraderImpl.class);

    /** The props. */
    private static Properties props;

    /** Processor for operation policies (a.k.a. WS-Policies) */
    private static PolicyProcessor processor = new PolicyProcessor();

    /** Factory for policy related objects. */
    private final PolicyFactoryImpl factory = new PolicyFactoryImpl();

    /** The policy cache. */
    private final PolicyCacheImpl policyCache = new PolicyCacheImpl();

    /** The listeners. */
    private List listeners = null;

    /**
     * Standard constructor.
     */
    public PolicyTraderImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#addListener(org.eclipse.swordfish.policytrader.callback.PolicyTradingListener)
     */
    public void addListener(final PolicyTradingListener listener) {
        if (null == listener) return;
        if (this.listeners == null) {
            this.listeners = new LinkedList();
            this.listeners.add(listener);
        } else {
            for (Iterator i = this.listeners.iterator(); i.hasNext();) {
                if (listener == i.next()) return;
            }
            this.listeners.add(listener);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Listener added: " + listener.toString());
        }
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#getPolicyFactory()
     */
    public PolicyFactory getPolicyFactory() {
        return this.factory;
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#getPolicyResolver()
     */
    public PolicyResolver getPolicyResolver() {
        return this.policyCache.getPolicyResolver();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#invalidateAll()
     */
    public void invalidateAll() {
        this.policyCache.invalidateAll();
        if (LOG.isInfoEnabled()) {
            LOG.info("Invalidated complete cache");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#invalidateOperationPolicy(org.eclipse.swordfish.policytrader.OperationPolicyIdentity)
     */
    public void invalidateOperationPolicy(final OperationPolicyIdentity identity) {
        this.policyCache.invalidateOperationPolicy(identity);
        if (LOG.isInfoEnabled()) {
            LOG.info("Invalidated OperationPolicy " + identity.getKeyName());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#invalidateParticipantPolicy(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity)
     */
    public void invalidateParticipantPolicy(final ParticipantPolicyIdentity identity) {
        this.policyCache.invalidateParticipantPolicy(identity);
        if (LOG.isInfoEnabled()) {
            LOG.info("Invalidated ParticipantPolicy " + identity.getKeyName());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#invalidateParticipantPolicyWithOperationPolicies(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity)
     */
    public void invalidateParticipantPolicyWithOperationPolicies(final ParticipantPolicyIdentity identity) {
        this.policyCache.invalidateParticipantPolicyWithOperationPolicies(identity);
        if (LOG.isInfoEnabled()) {
            LOG.info("Invalidated Participant + OperationPolicies " + identity.getKeyName());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#removeAllListeners()
     */
    public void removeAllListeners() {
        this.listeners.clear();
        if (LOG.isInfoEnabled()) {
            LOG.info("Removed all listeners");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#removeListener(org.eclipse.swordfish.policytrader.callback.PolicyTradingListener)
     */
    public void removeListener(final PolicyTradingListener listener) {
        if ((null == listener) || (null == this.listeners)) return;
        for (Iterator i = this.listeners.iterator(); i.hasNext();) {
            if (listener == i.next()) {
                i.remove();
                if (this.listeners.size() == 0) {
                    this.listeners = null;
                }
                return;
            }
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Removed listener " + listener.toString());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#setPolicyResolver(org.eclipse.swordfish.policytrader.callback.PolicyResolver)
     */
    public void setPolicyResolver(final PolicyResolver policyResolver) {
        this.policyCache.setPolicyResolver(policyResolver);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#setProperties(java.util.Properties)
     */
    public void setProperties(final Properties properties) {
        props = properties;
        String validityDurationSpec = props.getProperty(PolicyTrader.VALIDITY_DURATION);
        if (null != validityDurationSpec) {
            Long validityDuration = null;
            try {
                validityDuration = Long.decode(validityDurationSpec);
                AbstractAgreedPolicy.setValidityDuration(validityDuration.longValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Could not convert value for org.eclipse.swordfish.policytrader.ValidityDuration (" + validityDurationSpec
                                + " to Long");
            }
        }
        processor.setProperties(properties);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#tradePolicies(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity,
     *      org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity)
     */
    public AgreedPolicy tradePolicies(final ParticipantPolicyIdentity consumerPolicyID,
            final ParticipantPolicyIdentity providerPolicyID) throws BackendException, CorruptedPolicyException {
        return this.tradePolicies(consumerPolicyID, providerPolicyID, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#tradePolicies(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity,
     *      org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity,
     *      org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity)
     */
    public AgreedPolicy tradePolicies(final ParticipantPolicyIdentity consumerPolicyID,
            final ParticipantPolicyIdentity providerPolicyID, final ServiceDescriptionIdentity serviceID) throws BackendException,
            CorruptedPolicyException {
        return this.tradePolicies(consumerPolicyID, providerPolicyID, serviceID, this.policyCache.getPolicyResolver());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyTrader#tradePolicies(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity,
     *      org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity)
     */
    public AgreedPolicy tradePolicies(final ParticipantPolicyIdentity consumerPolicyID,
            final ParticipantPolicyIdentity providerPolicyID, final ServiceDescriptionIdentity serviceID,
            final PolicyResolver resolver) throws BackendException, CorruptedPolicyException {
        if (LOG.isInfoEnabled()) {
            String msg =
                    "Trying to trade policies" + "\nConsumer " + consumerPolicyID.getKeyName() + "\nProvider "
                            + providerPolicyID.getKeyName() + "\nServiceDescription ";
            msg += (null == serviceID) ? "null" : serviceID.getKeyName();
            LOG.info(msg);
        }
        if (null == resolver) throw new MissingPolicyResolverException("No policy resolver set");
        final StandardParticipantPolicyIdentity consumerPID = this.asStandardId(consumerPolicyID);
        final StandardParticipantPolicyIdentity providerPID = this.asStandardId(providerPolicyID);

        final ParticipantPolicy consumerPolicy = this.policyCache.resolveParticipantPolicy(consumerPID, resolver);
        final ParticipantPolicy providerPolicy = this.policyCache.resolveParticipantPolicy(providerPID, resolver);
        if ((consumerPolicy == null) || (providerPolicy == null)) throw new CorruptedPolicyException("Missing participant policy");
        StandardServiceDescriptionIdentity sid;
        if (null == serviceID) {
            final String service = providerPolicy.getService();
            if ((service == null) || (service.length() == 0)
            /* || !service.equals(consumerPolicy.getService()) */)
                throw new CorruptedPolicyException("Missing or inconsistent service name");
            String location = providerPolicy.getServiceLocation();
            if (null == location) {
                location = consumerPolicy.getServiceLocation();
            }
            sid = new StandardServiceDescriptionIdentity(service, location);
        } else {
            sid = this.asStandardId(serviceID);
        }

        TradingResult result = this.policyCache.cachedTradingResult(consumerPID, providerPID, sid);
        if (null != result) {
            if (LOG.isInfoEnabled()) {
                LOG.info("returning cached result");
            }
            TradingResult newPolicy = new TradingResult(result);
            long currentTime = System.currentTimeMillis();
            Date validSince = new Date(currentTime);
            Date validThrough = new Date(currentTime + AbstractAgreedPolicy.getValidityDuration());
            newPolicy.setValid(validSince, validThrough);
            return newPolicy;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("trying to resolve ServiceDescription " + sid.getKeyName());
        }
        final ServiceDescriptor sd = this.policyCache.resolveServiceDescriptor(sid, resolver);
        final List operations = sd.getOperationNames();
        final Map operationPolicies = new HashMap();
        for (Iterator i = operations.iterator(); i.hasNext();) {
            final String opName = (String) i.next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Matching " + opName);
            }
            final OperationPolicyIdentity cPID = consumerPolicy.getPolicyIdentityForOperationOrDefault(opName);
            final OperationPolicyIdentity pPID = providerPolicy.getPolicyIdentityForOperationOrDefault(opName);
            if (null == cPID)
                throw new CorruptedPolicyException("Missing consumer-side operation policy definition for " + opName);
            if (null == pPID)
                throw new CorruptedPolicyException("Missing provider-side operation policy definition for " + opName);
            if (ParticipantPolicy.VOID_POLICY_ID == cPID) {
                // don't put in operationPolicies
                // core expects no unused operations in agreed policies
                this.agreementUnusedOperation(opName, cPID, pPID);
                continue;
            } else if (ParticipantPolicy.VOID_POLICY_ID == pPID) {
                // consumer policy is not void for this operation, so
                // provider must provide it to match
                this.agreementFailedAtOperation(opName, cPID, pPID);
                if (LOG.isInfoEnabled()) {
                    LOG.info("matching failed");
                }
                return ParticipantPolicy.FAILED_AGREEMENT_POLICY;
            }
            final StandardOperationPolicyIdentity scPID = this.asStandardId(cPID);
            final StandardOperationPolicyIdentity spPID = this.asStandardId(pPID);
            Policy traded = this.policyCache.cachedTradedPolicy(scPID, spPID);
            if (null != traded) {
                operationPolicies.put(opName, traded);
                this.agreementSucceededAtOperation(opName, cPID, pPID);
                continue;
            }
            Policy cPol = this.policyCache.resolveOperationPolicy(scPID, resolver);
            if (cPol == null) throw new CorruptedPolicyException("Missing consumer-side operation policy for " + opName);
            Policy pPol = this.policyCache.resolveOperationPolicy(spPID, resolver);
            if (null == pPol) throw new CorruptedPolicyException("Missing provider-side operation policy for " + opName);
            try {
                synchronized (processor) {
                    traded = processor.match(cPol, pPol);
                }
                if (processor.isEmpty(traded)) {
                    this.agreementFailedAtOperation(opName, cPID, pPID);
                    if (LOG.isInfoEnabled()) {
                        LOG.info("matching failed");
                    }
                    return ParticipantPolicy.FAILED_AGREEMENT_POLICY;
                }
            } catch (UnexpectedPolicyProcessingException e) {
                this.agreementFailedAtOperation(opName, cPID, pPID);
                LOG
                    .error(
                            "Unexpected error while matching operation policies."
                                    + "\nConsumer: "
                                    + cPID
                                    + "\nProvider: "
                                    + pPID
                                    + "\nPlease provide policies and stacktrace to customer support at mailto://SOP-Support_BN@DeutschePost.de for analysis.",
                            e);
                return ParticipantPolicy.FAILED_AGREEMENT_POLICY;
            }
            operationPolicies.put(opName, traded);
            this.policyCache.cacheTradedPolicy(scPID, spPID, traded);
            this.agreementSucceededAtOperation(opName, cPID, pPID);
        }
        result =
                new TradingResult(consumerPID, providerPID, sid.getName().toString(), providerPolicy.getProvider(),
                        operationPolicies);
        this.policyCache.cacheTradingResult(result);
        if (LOG.isInfoEnabled()) {
            LOG.info("matching succeded");
        }
        return new TradingResult(result);
    }

    /**
     * Notify the listeners of failed operation policy trading.
     * 
     * @param operationName
     *        operation
     * @param consumerOpPolicyID
     *        ID of consumer operation policy
     * @param providerOpPolicyID
     *        ID of provider operation policy
     */
    private void agreementFailedAtOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
            final OperationPolicyIdentity providerOpPolicyID) {
        if (null == this.listeners) return;
        for (Iterator i = this.listeners.iterator(); i.hasNext();) {
            ((PolicyTradingListener) i.next()).agreementFailedAtOperation(operationName, consumerOpPolicyID, providerOpPolicyID);
        }
    }

    /**
     * Notify the listeners of successful operation policy trading.
     * 
     * @param operationName
     *        operation
     * @param consumerOpPolicyID
     *        ID of consumer operation policy
     * @param providerOpPolicyID
     *        ID of provider operation policy
     */
    private void agreementSucceededAtOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
            final OperationPolicyIdentity providerOpPolicyID) {
        if (null == this.listeners) return;
        for (Iterator i = this.listeners.iterator(); i.hasNext();) {
            ((PolicyTradingListener) i.next()).agreementSucceededAtOperation(operationName, consumerOpPolicyID, providerOpPolicyID);
        }
    }

    /**
     * Notify the listeners of an operation which has been declared "unused" by at least one of the
     * policies.
     * 
     * @param operationName
     *        operation
     * @param consumerOpPolicyID
     *        ID of consumer operation policy
     * @param providerOpPolicyID
     *        ID of provider operation policy
     */
    private void agreementUnusedOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
            final OperationPolicyIdentity providerOpPolicyID) {
        if (null == this.listeners) return;
        for (Iterator i = this.listeners.iterator(); i.hasNext();) {
            ((PolicyTradingListener) i.next()).agreementUnusedOperation(operationName, consumerOpPolicyID, providerOpPolicyID);
        }
    }

    /**
     * Convert an operation policy identifier into its standard implementation which can be used as
     * Map key.
     * 
     * @param source
     *        any operation policy identifier
     * 
     * @return an equivalent standard implementation instance
     */
    private StandardOperationPolicyIdentity asStandardId(final OperationPolicyIdentity source) {
        return (source instanceof StandardOperationPolicyIdentity) ? (StandardOperationPolicyIdentity) source
                : new StandardOperationPolicyIdentity(source);
    }

    /**
     * Convert a participant policy identifier into its standard implementation which can be used as
     * Map key.
     * 
     * @param source
     *        any participant policy identifier
     * 
     * @return an equivalent standard implementation instance
     */
    private StandardParticipantPolicyIdentity asStandardId(final ParticipantPolicyIdentity source) {
        return (source instanceof StandardParticipantPolicyIdentity) ? (StandardParticipantPolicyIdentity) source
                : new StandardParticipantPolicyIdentity(source);
    }

    /**
     * Convert a service description identifier into its standard implementation which can be used
     * as Map key.
     * 
     * @param source
     *        any service description identifier
     * 
     * @return an equivalent standard implementation instance
     */
    private StandardServiceDescriptionIdentity asStandardId(final ServiceDescriptionIdentity source) {
        return (source instanceof StandardServiceDescriptionIdentity) ? (StandardServiceDescriptionIdentity) source
                : new StandardServiceDescriptionIdentity(source);
    }
}
