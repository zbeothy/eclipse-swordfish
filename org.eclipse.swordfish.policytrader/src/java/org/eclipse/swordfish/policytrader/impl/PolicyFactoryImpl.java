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

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.ws.policy.util.DOMPolicyReader;
import org.apache.ws.policy.util.PolicyReader;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.eclipse.swordfish.policytrader.exceptions.UnreadableSourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Implementation of the Factory for Policy-related objects.
 */
public class PolicyFactoryImpl implements PolicyFactory {

    /** Toolbox for XML processing. */
    private final Toolbox toolbox = new Toolbox();

    /**
     * Standard Constructor.
     */
    public PolicyFactoryImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createAgreedPolicy(org.w3c.dom.Element)
     */
    public AgreedPolicy createAgreedPolicy(final Element agreedPolicyRootElement) throws UnreadableSourceException {
        AgreedPolicyFactory factory;
        try {
            factory = AgreedPolicyFactory.getInstance();
        } catch (ParserConfigurationException e) {
            throw new UnreadableSourceException("Could not instantiate parser to read input stream", e);
        }
        AgreedPolicy ret = null;
        ret = factory.createFrom(agreedPolicyRootElement);
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createAgreedPolicy(java.io.InputStream)
     */
    public AgreedPolicy createAgreedPolicy(final InputStream agreedPolicyData) throws UnreadableSourceException,
            CorruptedSourceException {
        AgreedPolicyFactory factory;
        try {
            factory = AgreedPolicyFactory.getInstance();
        } catch (ParserConfigurationException e) {
            throw new UnreadableSourceException("Could not instantiate parser to read input stream", e);
        }
        AgreedPolicy ret = null;
        try {
            ret = factory.createFrom(agreedPolicyData);
        } catch (SAXException e) {
            throw new CorruptedSourceException("While reading from input stream", e);
        } catch (IOException e) {
            throw new CorruptedSourceException("While reading from input stream", e);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createOperationPolicy(org.w3c.dom.Element)
     */
    public OperationPolicy createOperationPolicy(final Element policyRootElement) throws UnreadableSourceException,
            CorruptedSourceException {
        final DOMPolicyReader pr =
                (DOMPolicyReader) org.apache.ws.policy.util.PolicyFactory
                    .getPolicyReader(org.apache.ws.policy.util.PolicyFactory.DOM_POLICY_READER);

        return new OperationPolicyImpl(pr.readPolicy(policyRootElement));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createOperationPolicy(java.io.InputStream)
     */
    public OperationPolicy createOperationPolicy(final InputStream policyData) throws UnreadableSourceException,
            CorruptedSourceException {
        final PolicyReader pr =
                org.apache.ws.policy.util.PolicyFactory.getPolicyReader(org.apache.ws.policy.util.PolicyFactory.DOM_POLICY_READER);
        return new OperationPolicyImpl(pr.readPolicy(policyData));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createOperationPolicyIdentity(java.lang.String)
     */
    public OperationPolicyIdentity createOperationPolicyIdentity(final String identifier) {
        if (ParticipantPolicy.UNUSED_OPERATION.equals(identifier)) return ParticipantPolicy.VOID_POLICY_ID;
        return new StandardOperationPolicyIdentity(identifier);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createOperationPolicyIdentity(java.lang.String,
     *      java.lang.String)
     */
    public OperationPolicyIdentity createOperationPolicyIdentity(final String identifier, final String location) {
        if (ParticipantPolicy.UNUSED_OPERATION.equals(identifier)) return ParticipantPolicy.VOID_POLICY_ID;
        final OperationPolicyIdentity res = new StandardOperationPolicyIdentity(identifier);
        res.setLocation(location);
        return res;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createParticipantPolicy(org.w3c.dom.Element)
     */
    public ParticipantPolicy createParticipantPolicy(final Element policyRootElement) throws UnreadableSourceException,
            CorruptedSourceException {
        final ParticipantPolicyReader rd = new ParticipantPolicyReader(policyRootElement, this.toolbox);
        return rd.readParticipantPolicy();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createParticipantPolicy(java.io.InputStream)
     */
    public ParticipantPolicy createParticipantPolicy(final InputStream policyData) throws UnreadableSourceException,
            CorruptedSourceException {
        final ParticipantPolicyReader rd = new ParticipantPolicyReader(policyData, this.toolbox);
        return rd.readParticipantPolicy();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createParticipantPolicyIdentity(java.lang.String)
     */
    public ParticipantPolicyIdentity createParticipantPolicyIdentity(final String identifier) {
        return new StandardParticipantPolicyIdentity(identifier);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createParticipantPolicyIdentity(java.lang.String,
     *      java.lang.String)
     */
    public ParticipantPolicyIdentity createParticipantPolicyIdentity(final String identifier, final String location) {
        final ParticipantPolicyIdentity res = new StandardParticipantPolicyIdentity(identifier);
        res.setLocation(location);
        return res;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyFactory#createServiceDescriptor(java.io.InputStream)
     */
    public ServiceDescriptor createServiceDescriptor(final InputStream serviceDescriptionData) throws UnreadableSourceException,
            CorruptedSourceException {
        final Document document = this.toolbox.streamToDocument(serviceDescriptionData);
        return new ServiceDescriptorImpl(document);
    }
}
