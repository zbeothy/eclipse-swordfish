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

import javax.xml.namespace.QName;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * Handles the transformation from WS-Policy <code>Transformation</code> assertions to classic
 * agreed policy.
 * 
 * TODO: unify with CustomValidationAssertion
 * 
 */
public class TransformationAssertionTransformer implements AssertionTransformer {

    /** The Constant DESTINATION_ATTRIB. */
    public static final String DESTINATION_ATTRIB = "destination";

    /** The Constant DESTINATION_LOCAL. */
    public static final String DESTINATION_LOCAL = "local";

    /** The Constant DESTINATION_LOCAL_CONSUMER. */
    public static final String DESTINATION_LOCAL_CONSUMER = "consumer";

    /** The Constant DESTINATION_LOCAL_PROVIDER. */
    public static final String DESTINATION_LOCAL_PROVIDER = "provider";

    /** The Constant RULE_SOURCE_PATH_ATTRIB. */
    public static final String RULE_SOURCE_PATH_ATTRIB = "ruleSourcePath";

    /** The Constant RULE_ID_ATTRIB. */
    public static final String RULE_ID_ATTRIB = "ruleId";

    /** The Constant MESSAGE_ATTRIB. */
    public static final String MESSAGE_ATTRIB = "message";

    /** The Constant MESSAGE_REQUEST. */
    public static final String MESSAGE_REQUEST = "request";

    /** The Constant MESSAGE_RESPONSE. */
    public static final String MESSAGE_RESPONSE = "response";

    /** The Constant DESTINATION_QNAME. */
    private static final QName DESTINATION_QNAME = new QName(DESTINATION_ATTRIB);

    /** The Constant RULE_SOURCE_PATH_QNAME. */
    private static final QName RULE_SOURCE_PATH_QNAME = new QName(RULE_SOURCE_PATH_ATTRIB);

    /** The Constant RULE_ID_QNAME. */
    private static final QName RULE_ID_QNAME = new QName(RULE_ID_ATTRIB);

    /** The Constant MESSAGE_QNAME. */
    private static final QName MESSAGE_QNAME = new QName(MESSAGE_ATTRIB);

    /**
     * Instantiates a new transformation assertion transformer.
     */
    public TransformationAssertionTransformer() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.AssertionTransformer#assertion(org.eclipse.swordfish.policytrader.impl.assertiontransformation.QKey,
     *      org.apache.ws.policy.PrimitiveAssertion,
     *      org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicOperationPolicy)
     */
    public void assertion(final QKey wspAssertionName, final PrimitiveAssertion wsAssertion,
            final ClassicOperationPolicy classicPolicy) {
        final String destination = wsAssertion.getAttribute(DESTINATION_QNAME);
        final String ruleSourcePath = wsAssertion.getAttribute(RULE_SOURCE_PATH_QNAME);
        final String ruleId = wsAssertion.getAttribute(RULE_ID_QNAME);
        final String message = wsAssertion.getAttribute(MESSAGE_QNAME);
        final TransformationAssertion ta = new TransformationAssertion(DESTINATION_LOCAL, ruleSourcePath, ruleId);
        AbstractAssertionBag classicBag = null;
        if (MESSAGE_REQUEST.equals(message)) {
            if (DESTINATION_LOCAL_CONSUMER.equals(destination)) {
                classicBag = classicPolicy.getRequestSender();
            } else if (DESTINATION_LOCAL_PROVIDER.equals(destination)) {
                classicBag = classicPolicy.getRequestReceiver();
            }
        } else if (MESSAGE_RESPONSE.equals(message)) {
            if (DESTINATION_LOCAL_CONSUMER.equals(destination)) {
                classicBag = classicPolicy.getResponseReceiver();
            } else if (DESTINATION_LOCAL_PROVIDER.equals(destination)) {
                classicBag = classicPolicy.getResponseSender();
            }
        }
        this.handleBag(classicBag, ta);
    }

    /**
     * Adds a transformation assertion to the <code>Transformations</code> bag in the classic
     * assertions, creating the <code>Transformations</code> bag if it is not already there.
     * 
     * @param classicAssertions
     *        the classic assertions
     * @param ta
     *        the ta
     */
    private void handleBag(final AbstractAssertionBag classicAssertions, final TransformationAssertion ta) {
        TransformationAssertionBag bag =
                (TransformationAssertionBag) classicAssertions.getFirstAssertion(CLASSIC_TRANSFORMATION_BAG);
        if (null == bag) {
            bag = new TransformationAssertionBag();
            classicAssertions.addAssertion(bag);
        }
        bag.addAssertion(ta);
    }

}
