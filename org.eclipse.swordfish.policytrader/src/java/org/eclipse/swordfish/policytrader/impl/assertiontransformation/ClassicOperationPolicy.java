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
package org.eclipse.swordfish.policytrader.impl.assertiontransformation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * The Class ClassicOperationPolicy.
 */
public class ClassicOperationPolicy extends Writeable {

    /** The Constant REQUEST. */
    private static final QKey REQUEST = new QKey(AssertionTransformer.CLASSIC_AGREED_POLICY_NAMESPACE, "Request");

    /** The Constant RESPONSE. */
    private static final QKey RESPONSE = new QKey(AssertionTransformer.CLASSIC_AGREED_POLICY_NAMESPACE, "Response");

    /** The Constant SENDER. */
    private static final QKey SENDER = new QKey(AssertionTransformer.CLASSIC_AGREED_POLICY_NAMESPACE, "Sender");

    /** The Constant RECEIVER. */
    private static final QKey RECEIVER = new QKey(AssertionTransformer.CLASSIC_AGREED_POLICY_NAMESPACE, "Receiver");

    /** The Constant transformers. */
    private static final Map TRANSFORMERS = createTransformers();

    /**
     * Creates the transformers.
     * 
     * @return the map
     */
    private static Map createTransformers() {
        final Map result = new HashMap();
        result.put(AssertionTransformer.WSP_CUSTOM_VALIDATION, new CustomValidationAssertionTransformer());
        result.put(AssertionTransformer.WSP_AUTHENTICATION, new AuthenticationAssertionTransformer());
        result.put(AssertionTransformer.WSP_AUTHORIZATION, new AuthorizationAssertionTransformer());
        result.put(AssertionTransformer.WSP_MAX_RESPONSE_TIME, new MaxResponseTimeAssertionTransformer());
        result.put(AssertionTransformer.WSP_TRACKING_LEVEL, new TrackingLevelAssertionTransformer());
        result.put(AssertionTransformer.WSP_TRANSFORMATION, new TransformationAssertionTransformer());
        result.put(AssertionTransformer.WSP_EXTENSION, new ExtensionAssertionTransformer());
        final TransportAssertionTransformer tat = new TransportAssertionTransformer();
        for (int i = 0; i < AssertionTransformer.WSP_TRANSPORT_ASSERTIONS.length; i++) {
            result.put(AssertionTransformer.WSP_TRANSPORT_ASSERTIONS[i], tat);
        }
        return result;
    }

    /**
     * Gets the transformer.
     * 
     * @param wspAssertionName
     *        the wsp assertion name
     * 
     * @return the transformer
     */
    private static AssertionTransformer getTransformer(final QKey wspAssertionName) {
        return (AssertionTransformer) TRANSFORMERS.get(wspAssertionName);
    }

    /** The request sender. */
    private final PolicyAssertionBag requestSender = new PolicyAssertionBag(SENDER);

    /** The request receiver. */
    private final PolicyAssertionBag requestReceiver = new PolicyAssertionBag(RECEIVER);

    /** The response sender. */
    private final PolicyAssertionBag responseSender = new PolicyAssertionBag(SENDER);

    /** The response receiver. */
    private final PolicyAssertionBag responseReceiver = new PolicyAssertionBag(RECEIVER);

    /**
     * Instantiates a new classic operation policy.
     */
    public ClassicOperationPolicy() {
        super();
    }

    /**
     * Apply.
     * 
     * @param wsOperationPolicy
     *        the ws operation policy
     */
    public void apply(final Policy wsOperationPolicy) {
        try {
            final ExactlyOne outerEO = (ExactlyOne) wsOperationPolicy.getTerms().get(0);
            final All outerAll = (All) outerEO.getTerms().get(0);
            this.doApply(outerAll);
        } catch (ClassCastException e) {
            throw new RuntimeException("corrupted agreed policy", e);
        }
    }

    /**
     * Gets the request receiver.
     * 
     * @return the request receiver
     */
    public PolicyAssertionBag getRequestReceiver() {
        return this.requestReceiver;
    }

    /**
     * Gets the request sender.
     * 
     * @return the request sender
     */
    public PolicyAssertionBag getRequestSender() {
        return this.requestSender;
    }

    /**
     * Gets the response receiver.
     * 
     * @return the response receiver
     */
    public PolicyAssertionBag getResponseReceiver() {
        return this.responseReceiver;
    }

    /**
     * Gets the response sender.
     * 
     * @return the response sender
     */
    public PolicyAssertionBag getResponseSender() {
        return this.responseSender;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.Writeable#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    public void writeTo(final XMLStreamWriter writer) throws XMLStreamException {
        writeStartElement(REQUEST, writer);
        this.requestSender.writeTo(writer);
        this.requestReceiver.writeTo(writer);
        writer.writeEndElement();
        writeStartElement(RESPONSE, writer);
        this.responseSender.writeTo(writer);
        this.responseReceiver.writeTo(writer);
        writer.writeEndElement();
    }

    /**
     * Do apply.
     * 
     * @param assertion
     *        the assertion
     */
    private void doApply(final Assertion assertion) {
        if (assertion.getType() == Assertion.PRIMITIVE) {
            final PrimitiveAssertion pa = (PrimitiveAssertion) assertion;
            final QKey assertionName = new QKey(pa.getName());
            final AssertionTransformer t = getTransformer(assertionName);
            if (null != t) {
                t.assertion(assertionName, pa, this);
            }
        } else {
            for (Iterator i = assertion.getTerms().iterator(); i.hasNext();) {
                this.doApply((Assertion) i.next());
            }
        }
    }
}
