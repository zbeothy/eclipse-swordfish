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
package org.eclipse.swordfish.core.components.resolver.mock;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.PolicyReader;
import org.eclipse.swordfish.core.components.resolver.PolicyResolver;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;
import org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.eclipse.swordfish.policytrader.exceptions.UnreadableSourceException;
import org.eclipse.swordfish.policytrader.impl.PolicyFactoryImpl;

/**
 * The Class MockPolicyResolver.
 */
public class MockPolicyResolver implements PolicyResolver {

    /** The reader. */
    PolicyReader reader =
            org.apache.ws.policy.util.PolicyFactory.getPolicyReader(org.apache.ws.policy.util.PolicyFactory.DOM_POLICY_READER);

    /** The policy factory. */
    private PolicyFactory policyFactory = new PolicyFactoryImpl();

    /** keyname -> OperationPolicy. */
    private Map operationPolicies = new HashMap();

    /** keyname -> ParticipantPolicy. */
    private Map participantPolicies = new HashMap();

    /** keyname -> Policy. */
    private Map agreedPolicies = new HashMap();

    /**
     * Adds the agreed policy.
     * 
     * @param keyname
     *        the keyname
     * @param is
     *        the is
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    public void addAgreedPolicy(final String keyname, final InputStream is) throws UnreadableSourceException,
            CorruptedSourceException {
        Policy policy = this.reader.readPolicy(is);
        this.agreedPolicies.put(keyname, policy);
    }

    /**
     * Adds the agreed policy.
     * 
     * @param keyname
     *        the keyname
     * @param classname
     *        the classname
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    public void addAgreedPolicy(final String keyname, final String classname) throws UnreadableSourceException,
            CorruptedSourceException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(classname);
        this.addAgreedPolicy(keyname, is);
    }

    /**
     * Adds the operation policy.
     * 
     * @param keyname
     *        the keyname
     * @param is
     *        the is
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    public void addOperationPolicy(final String keyname, final InputStream is) throws UnreadableSourceException,
            CorruptedSourceException {
        OperationPolicy policy = this.policyFactory.createOperationPolicy(is);
        this.operationPolicies.put(keyname, policy);
    }

    /**
     * Adds the operation policy.
     * 
     * @param keyname
     *        the keyname
     * @param classname
     *        the classname
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    public void addOperationPolicy(final String keyname, final String classname) throws UnreadableSourceException,
            CorruptedSourceException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(classname);
        this.addOperationPolicy(keyname, is);
    }

    /**
     * Adds the participant policy.
     * 
     * @param keyname
     *        the keyname
     * @param is
     *        the is
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    public void addParticipantPolicy(final String keyname, final InputStream is) throws UnreadableSourceException,
            CorruptedSourceException {
        ParticipantPolicy policy = this.policyFactory.createParticipantPolicy(is);
        this.participantPolicies.put(keyname, policy);
    }

    /**
     * Adds the participant policy.
     * 
     * @param keyname
     *        the keyname
     * @param classname
     *        the classname
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    public void addParticipantPolicy(final String keyname, final String classname) throws UnreadableSourceException,
            CorruptedSourceException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(classname);
        this.addParticipantPolicy(keyname, is);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getAssignedPolicyIds(javax.xml.namespace.QName)
     */
    public List getAssignedPolicyIds(final QName providerId) throws BackendException {
        return new ArrayList(this.participantPolicies.keySet());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getDefaultConsumerPolicyID()
     */
    public String getDefaultConsumerPolicyID() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getDefaultPolicyID()
     */
    public String getDefaultPolicyID() {
        return null;
    }

    /**
     * Resolve agreed operation policy.
     * 
     * @param key
     *        the key
     * 
     * @return the policy
     */
    public Policy resolveAgreedOperationPolicy(final String key) {
        return (Policy) this.agreedPolicies.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveOperationPolicy(org.eclipse.swordfish.policytrader.OperationPolicyIdentity)
     */
    public OperationPolicy resolveOperationPolicy(final OperationPolicyIdentity identity) throws BackendException {
        String key = identity.getKeyName();
        return (OperationPolicy) this.operationPolicies.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveParticipantPolicy(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity)
     */
    public ParticipantPolicy resolveParticipantPolicy(final ParticipantPolicyIdentity identity) throws BackendException {
        String key = identity.getKeyName();
        return (ParticipantPolicy) this.participantPolicies.get(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#resolvePolicyID(java.lang.String)
     */
    public String resolvePolicyID(final String policyName) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveServiceDescriptor(org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity)
     */
    public ServiceDescriptor resolveServiceDescriptor(final ServiceDescriptionIdentity identity) throws BackendException {
        return null;
    }
}
