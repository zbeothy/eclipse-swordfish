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
 * Handles the transformation from WS-Policy <code>CustomValidation</code> assertions to classic
 * agreed policy.
 * 
 * TODO: unify with TransformationAssertion
 * 
 */

public class CustomValidationAssertionTransformer implements AssertionTransformer {

    /** The Constant DESTINATION_ATTRIB. */
    public static final String DESTINATION_ATTRIB = "destination";

    /** The Constant DESTINATION_CONSUMER. */
    public static final String DESTINATION_CONSUMER = "consumer";

    /** The Constant DESTINATION_PROVIDER. */
    public static final String DESTINATION_PROVIDER = "provider";

    /** The Constant SCHEMA_SOURCE_PATH_ATTRIB. */
    public static final String SCHEMA_SOURCE_PATH_ATTRIB = "schemaSourcePath";

    /** The Constant SCHEMA_ID_ATTRIB. */
    public static final String SCHEMA_ID_ATTRIB = "schemaId";

    /** The Constant MESSAGE_ATTRIB. */
    public static final String MESSAGE_ATTRIB = "message";

    /** The Constant MESSAGE_REQUEST. */
    public static final String MESSAGE_REQUEST = "request";

    /** The Constant MESSAGE_RESPONSE. */
    public static final String MESSAGE_RESPONSE = "response";

    /** The Constant DESTINATION_QNAME. */
    private static final QName DESTINATION_QNAME = new QName(DESTINATION_ATTRIB);

    /** The Constant SCHEMA_SOURCE_PATH_QNAME. */
    private static final QName SCHEMA_SOURCE_PATH_QNAME = new QName(SCHEMA_SOURCE_PATH_ATTRIB);

    /** The Constant SCHEMA_ID_QNAME. */
    private static final QName SCHEMA_ID_QNAME = new QName(SCHEMA_ID_ATTRIB);

    /** The Constant MESSAGE_QNAME. */
    private static final QName MESSAGE_QNAME = new QName(MESSAGE_ATTRIB);

    /**
     * Instantiates a new custom validation assertion transformer.
     */
    public CustomValidationAssertionTransformer() {
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
        final String schemaSourcePath = wsAssertion.getAttribute(SCHEMA_SOURCE_PATH_QNAME);
        final String schemaId = wsAssertion.getAttribute(SCHEMA_ID_QNAME);
        final String message = wsAssertion.getAttribute(MESSAGE_QNAME);
        final CustomValidationAssertion validationAssertion = new CustomValidationAssertion(schemaSourcePath, schemaId);
        AbstractAssertionBag classicBag = null;
        if (MESSAGE_REQUEST.equals(message)) {
            if (DESTINATION_CONSUMER.equals(destination)) {
                classicBag = classicPolicy.getRequestSender();
            } else if (DESTINATION_PROVIDER.equals(destination)) {
                classicBag = classicPolicy.getRequestReceiver();
            }
        } else if (MESSAGE_RESPONSE.equals(message)) {
            if (DESTINATION_CONSUMER.equals(destination)) {
                classicBag = classicPolicy.getResponseReceiver();
            } else if (DESTINATION_PROVIDER.equals(destination)) {
                classicBag = classicPolicy.getResponseSender();
            }
        }
        if (null == classicBag)
            throw new RuntimeException("corrupted CustomValidationAssertion in agreed policy:" + "\nmessage: " + message
                    + "\ndestination: " + destination);
        this.handleBag(classicBag, validationAssertion);
    }

    /**
     * Adds a custom validation assertion to the <code>CustomValidations</code> bag in the classic
     * assertions, creating the bag if it is not already there.
     * 
     * @param classicAssertions
     *        the classic assertions
     * @param validationAssertion
     *        the validation assertion
     */
    private void handleBag(final AbstractAssertionBag classicAssertions, final CustomValidationAssertion validationAssertion) {
        CustomValidationAssertionBag bag =
                (CustomValidationAssertionBag) classicAssertions.getFirstAssertion(CLASSIC_CUSTOM_VALIDATION_BAG);
        if (null == bag) {
            bag = new CustomValidationAssertionBag();
            classicAssertions.addAssertion(bag);
        }
        bag.addAssertion(validationAssertion);
    }
}
