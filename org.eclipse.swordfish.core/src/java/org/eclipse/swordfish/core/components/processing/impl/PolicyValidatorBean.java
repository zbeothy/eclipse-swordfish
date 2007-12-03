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
package org.eclipse.swordfish.core.components.processing.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.processing.PolicyValidator;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.resolver.PolicyResolver;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.selector.ClassSelector;
import org.eclipse.swordfish.policy.selector.Selector;
import org.eclipse.swordfish.policy.util.PolicyProcessor;
import org.eclipse.swordfish.policy.util.TermIterator;
import org.eclipse.swordfish.policy.util.UnexpectedPolicyProcessingException;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;
import org.eclipse.swordfish.policytrader.impl.OperationPolicyImpl;
import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;

/**
 * The Class PolicyValidatorBean.
 */
public class PolicyValidatorBean implements PolicyValidator {

    /** The Constant HASH_ALGORITHM. */
    public static final String HASH_ALGORITHM = "MD5";

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(PolicyValidatorBean.class);

    /** The Constant ASSERTION_SELECTOR. */
    private static final Selector ASSERTION_SELECTOR = new Selector() {

        public boolean isValid(Object obj) {
            return obj instanceof Assertion;
        }
    };

    /** The Constant POLICY_BYTES. */
    private static final byte[] POLICY_BYTES = "<Policy>".getBytes();

    /** The Constant ALL_BYTES. */
    private static final byte[] ALL_BYTES = "<All>".getBytes();

    /** The Constant EXACTLY_ONE_BYTES. */
    private static final byte[] EXACTLY_ONE_BYTES = "<ExactlyOne>".getBytes();

    /** The Constant NULL_BYTES. */
    private static final byte[] NULL_BYTES = "null".getBytes();

    /** The policy resolver. */
    private PolicyResolver policyResolver = null;

    /** The processor. */
    private PolicyProcessor processor;

    /** The policy cache. */
    private Cache policyCache = null;

    /** The policy cache entry refresh policy. */
    private EntryRefreshPolicy policyCacheEntryRefreshPolicy = null;

    /** The enabled. */
    private boolean enabled = true;

    /** The exclusion list. */
    private List exclusionList = new ArrayList(0);

    /** time in milliseconds for which the a cached check result should remain valid. */
    private long validityDuration = 3600000;

    /**
     * Gets the exclusion list.
     * 
     * @return the exclusion list
     */
    public List getExclusionList() {
        return this.exclusionList;
    }

    /**
     * Returns a hash value for all relevant parts of the policy The hash algorithm is determined by
     * org.eclipse.swordfish.core.components.processing.impl.PolicyValidatorBean.HASH_ALGORITHM, the
     * hash value is computed using java.security.MessageDigest.
     * 
     * @param providerPolicyIdentity
     *        the provider policy identity
     * @param operation
     *        the operation
     * @param agreedPolicy
     *        the agreed policy
     * 
     * @return the hash value for the policy
     * 
     * @throws NoSuchAlgorithmException
     * @throws PolicyViolatedException
     */
    public String getHash(final ParticipantPolicyIdentity providerPolicyIdentity, final String operation, final Policy agreedPolicy)
            throws PolicyViolatedException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Unsupported hash algorithm " + HASH_ALGORITHM + " - Caching of agreed policies is disabled.");
            return null;
        }
        String keyname = providerPolicyIdentity.getKeyName();
        if (null == keyname) throw new PolicyViolatedException("No key provided with ProviderPolicyIdentity");
        digest.update(keyname.getBytes());
        digest.update(operation.getBytes());
        TermIterator it = new TermIterator(agreedPolicy, ASSERTION_SELECTOR, true);
        while (it.hasNext()) {
            Assertion assertion = (Assertion) it.next();
            if (assertion instanceof PrimitiveAssertion) {
                PrimitiveAssertion prim = (PrimitiveAssertion) assertion;
                QName name = prim.getName();
                String namespaceURI = name.getNamespaceURI();
                if (PolicyConstants.SOP_ASSERTION_URI.equals(namespaceURI)) {
                    digest.update(name.getLocalPart().getBytes());
                    Hashtable attributes = prim.getAttributes();
                    for (Iterator iter = attributes.keySet().iterator(); iter.hasNext();) {
                        QName attrName = (QName) iter.next();
                        String attrValue = (String) attributes.get(attrName);
                        String attrURI = attrName.getNamespaceURI();
                        if ((null != attrURI) && (!"".equals(attrURI))) {
                            digest.update(attrURI.getBytes());
                        } else {
                            digest.update(NULL_BYTES);
                        }
                        digest.update(attrName.getLocalPart().getBytes());
                        digest.update(attrValue.getBytes());
                    }
                }
            } else if (assertion instanceof Policy) {
                digest.update(POLICY_BYTES);
            } else if (assertion instanceof All) {
                digest.update(ALL_BYTES);
            } else if (assertion instanceof ExactlyOne) {
                digest.update(EXACTLY_ONE_BYTES);
            }
        }
        byte[] bs = digest.digest();
        return new String(bs);
    }

    /**
     * Gets the policy cache.
     * 
     * @return the policy cache
     */
    public Cache getPolicyCache() {
        return this.policyCache;
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
     * Gets the validity duration.
     * 
     * @return the validity duration
     */
    public long getValidityDuration() {
        return this.validityDuration;
    }

    /**
     * Checks if is enabled.
     * 
     * @return true, if is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets the enabled.
     * 
     * @param enabled
     *        the new enabled
     */
    public void setEnabled(final boolean enabled) {
        String enableOverride = System.getProperty("org.eclipse.swordfish.policy.validation.enable", Boolean.toString(enabled));
        this.enabled = Boolean.valueOf(enableOverride).booleanValue();
    }

    /**
     * Sets the exclusion list.
     * 
     * @param exclusionList
     *        the new exclusion list
     */
    public void setExclusionList(final List exclusionList) {
        this.exclusionList = Collections.unmodifiableList(exclusionList);
    }

    /**
     * Sets the policy cache.
     * 
     * @param policyCache
     *        the new policy cache
     */
    public void setPolicyCache(final Cache policyCache) {
        this.policyCache = policyCache;
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
     * Sets the validity duration.
     * 
     * @param validityDuration
     *        the new validity duration
     */
    public void setValidityDuration(final long validityDuration) {
        this.validityDuration = validityDuration;
    }

    /**
     * Validate.
     * 
     * @param operationPolicy
     *        the operation policy
     * @param operation
     *        the operation
     * @param providerPolicyIdentity
     *        the provider policy identity
     * @param providerId
     *        the provider id
     * @param serviceName
     *        the service name
     * 
     * @throws PolicyViolatedException
     */
    public void validate(final Policy operationPolicy, final String operation,
            final ParticipantPolicyIdentity providerPolicyIdentity, final QName providerId, final QName serviceName)
            throws PolicyViolatedException {
        if (!this.isEnabled() || this.isExcluded(serviceName, operation)) return;
        String hash = this.getHash(providerPolicyIdentity, operation, operationPolicy);
        if (null != hash) {
            Object val = this.getFromCache(hash);
            if (null != val) // hash is already in cache and still valid
                return;
        }
        this.performValidation(operationPolicy, operation, providerPolicyIdentity, providerId);
        // errors during validtion result in exception -> if we reach this
        // point, the agreed
        // policy is valid
        if (null != hash) {
            this.putInCache(hash, new Long(System.currentTimeMillis() + this.validityDuration));
        }
    }

    /**
     * Destroy.
     */
    void destroy() {
        // destroy PolicyTrader
        LOG.info("PolicyValidator.destroy()");
        this.processor = null;
        this.policyResolver = null;
    }

    /**
     * Init.
     */
    void init() {
        LOG.info("PolicyValidator.init()");
        this.processor = new PolicyProcessor();
        this.policyCacheEntryRefreshPolicy = new EntryRefreshPolicy() {

            /**
             * 
             */
            private static final long serialVersionUID = 2918062850488070883L;

            public boolean needsRefresh(final CacheEntry entry) {
                Object content = entry.getContent();
                if (content instanceof Long) {
                    Long expiry = (Long) content;
                    long current = System.currentTimeMillis();
                    return (current > expiry.longValue());
                }
                return true;
            }
        };
        this.policyCache = new Cache(true, false, true);
    }

    /**
     * Adds a mode attribute to each primitive assertion.
     * 
     * @param assertion
     *        the assertion
     * @param mode
     *        the mode
     */
    private void addMode(final Assertion assertion, final String mode) {
        TermIterator it = new TermIterator(assertion, new ClassSelector(PrimitiveAssertion.class), true);
        while (it.hasNext()) {
            PrimitiveAssertion primitive = (PrimitiveAssertion) it.next();
            primitive.addAttribute(PolicyConstants.SOP_MODE_ATTRIBUTE, mode);
        }
    }

    /**
     * Gets the from cache.
     * 
     * @param key
     *        the key
     * 
     * @return the from cache
     */
    private Object getFromCache(final String key) {
        try {
            return this.policyCache.getFromCache(key);
        } catch (NeedsRefreshException e) {
            this.policyCache.cancelUpdate(key);
            return null;
        }
    }

    /**
     * Gets the operation policy id.
     * 
     * @param operation
     *        the operation
     * @param providerPolicyIdentity
     *        the provider policy identity
     * 
     * @return the operation policy id
     * 
     * @throws PolicyViolatedException
     */
    private OperationPolicyIdentity getOperationPolicyId(final String operation,
            final ParticipantPolicyIdentity providerPolicyIdentity) throws PolicyViolatedException {
        ParticipantPolicy providerPolicy = null;
        try {
            providerPolicy = this.policyResolver.resolveParticipantPolicy(providerPolicyIdentity);
        } catch (BackendException e) {
            throw new PolicyViolatedException(e);
        }
        OperationPolicyIdentity providerOperationPolicyId = providerPolicy.getPolicyIdentityForOperationOrDefault(operation);
        if (null == providerOperationPolicyId)
            throw new PolicyViolatedException("Provider policy " + providerPolicyIdentity.getKeyName()
                    + " does not specify a policy for requested operation " + operation);
        return providerOperationPolicyId;
    }

    /**
     * Gets the ws policy.
     * 
     * @param providerOperationPolicyId
     *        the provider operation policy id
     * 
     * @return the ws policy
     * 
     * @throws PolicyViolatedException
     */
    private Policy getWsPolicy(final OperationPolicyIdentity providerOperationPolicyId) throws PolicyViolatedException {
        OperationPolicy providerOperationPolicy = null;
        try {
            providerOperationPolicy = this.policyResolver.resolveOperationPolicy(providerOperationPolicyId);
        } catch (BackendException e) {
            throw new PolicyViolatedException(e);
        }
        if (null == providerOperationPolicy)
            throw new PolicyViolatedException("Could not resolve provider operation policy "
                    + providerOperationPolicyId.getKeyName());
        Policy providerWsPolicy = null;
        if (providerOperationPolicy instanceof OperationPolicyImpl) {
            providerWsPolicy = ((OperationPolicyImpl) providerOperationPolicy).getWsPolicy();
        } else
            throw new PolicyViolatedException("Can't validate for provider policy of type "
                    + providerOperationPolicy.getClass().getName()
                    + " - only org.eclipse.swordfish.policytrader.impl.OperationPolicyImpl is supported.");
        return providerWsPolicy;
    }

    /**
     * Intersect.
     * 
     * @param operationPolicy
     *        the operation policy
     * @param providerWsPolicy
     *        the provider ws policy
     * 
     * @return the policy
     * 
     * @throws PolicyViolatedException
     */
    private Policy intersect(final Policy operationPolicy, final Policy providerWsPolicy) throws PolicyViolatedException {
        Policy result = null;
        try {
            this.addMode(providerWsPolicy, "runtime");
            this.addMode(operationPolicy, "runtime");
            result = this.processor.matchAllResults(operationPolicy, providerWsPolicy);
        } catch (UnexpectedPolicyProcessingException e) {
            throw new PolicyViolatedException(e);
        } finally {
            this.removeMode(providerWsPolicy);
            this.removeMode(operationPolicy);
        }
        return result;
    }

    /**
     * Checks if is excluded.
     * 
     * @param serviceName
     *        the service name
     * @param operation
     *        the operation
     * 
     * @return true, if is excluded
     */
    private boolean isExcluded(final QName serviceName, final String operation) {
        boolean ret = (null == this.exclusionList) ? false : this.exclusionList.contains(serviceName.toString() + ";" + operation);
        return ret;
    }

    /**
     * Perform validation.
     * 
     * @param operationPolicy
     *        the operation policy
     * @param operation
     *        the operation
     * @param providerPolicyIdentity
     *        the provider policy identity
     * @param providerId
     *        the provider id
     * 
     * @throws PolicyViolatedException
     */
    private void performValidation(final Policy operationPolicy, final String operation,
            final ParticipantPolicyIdentity providerPolicyIdentity, final QName providerId) throws PolicyViolatedException {
        if (null == this.policyResolver) {
            Throwable t = new IllegalStateException("No PolicyResolver available");
            throw new PolicyViolatedException(t);
        }
        this.validateProviderPolicyId(providerPolicyIdentity, providerId);
        OperationPolicyIdentity providerOperationPolicyId = this.getOperationPolicyId(operation, providerPolicyIdentity);
        Policy providerWsPolicy = this.getWsPolicy(providerOperationPolicyId);
        Policy result = this.intersect(operationPolicy, providerWsPolicy);
        if (this.processor.isEmpty(result))
            throw new PolicyViolatedException("Agreed Policy for operation " + operation
                    + " does not match behavior specified in provider policy " + providerOperationPolicyId.getKeyName());
    }

    /**
     * Put in cache.
     * 
     * @param key
     *        the key
     * @param content
     *        the content
     */
    private void putInCache(final String key, final Object content) {
        this.policyCache.putInCache(key, content, this.policyCacheEntryRefreshPolicy);
    }

    /**
     * Removes all mode attributes from each primitive assertion.
     * 
     * @param assertion
     *        the assertion
     */
    private void removeMode(final Assertion assertion) {
        TermIterator it = new TermIterator(assertion, new ClassSelector(PrimitiveAssertion.class), true);
        while (it.hasNext()) {
            PrimitiveAssertion primitive = (PrimitiveAssertion) it.next();
            primitive.removeAttribute(PolicyConstants.SOP_MODE_ATTRIBUTE);
        }
    }

    /**
     * Validate provider policy id.
     * 
     * @param providerPolicyIdentity
     *        the provider policy identity
     * @param providerId
     *        the provider id
     * 
     * @throws PolicyViolatedException
     */
    private void validateProviderPolicyId(final ParticipantPolicyIdentity providerPolicyIdentity, final QName providerId)
            throws PolicyViolatedException {
        String providerPolicyKey = providerPolicyIdentity.getKeyName();
        try {
            List providerPolicies = this.policyResolver.getAssignedPolicyIds(providerId);
            if ((null == providerPolicies) || (!providerPolicies.contains(providerPolicyKey)))
                throw new PolicyViolatedException("No policy with id " + providerPolicyKey + " assigned to provider "
                        + providerId.toString());
        } catch (BackendException e) {
            throw new PolicyViolatedException("While trying to retrieve list of policies for " + providerId.toString(), e);
        }
    }

}
