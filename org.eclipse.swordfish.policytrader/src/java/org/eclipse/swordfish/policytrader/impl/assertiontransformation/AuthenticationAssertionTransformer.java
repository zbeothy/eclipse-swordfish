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
 * The Class AuthenticationAssertionTransformer.
 */
public class AuthenticationAssertionTransformer implements AssertionTransformer {

    /** The Constant MESSAGE_ATTRIB. */
    public static final String MESSAGE_ATTRIB = "message";

    /** The Constant TYPE_ATTRIB. */
    public static final String TYPE_ATTRIB = "type";

    /** The Constant TYPE_QNAME. */
    private static final QName TYPE_QNAME = new QName(TYPE_ATTRIB);

    /** The Constant MESSAGE_QNAME. */
    private static final QName MESSAGE_QNAME = new QName(MESSAGE_ATTRIB);

    /**
     * Instantiates a new authentication assertion transformer.
     */
    public AuthenticationAssertionTransformer() {
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
        final String type = wsAssertion.getAttribute(TYPE_QNAME);
        final String message = wsAssertion.getAttribute(MESSAGE_QNAME);
        final AuthenticationAssertion assertion = new AuthenticationAssertion();
        assertion.setType(type);
        AbstractAssertionBag classicBag = null;
        if (("request".equals(message)) || ("both".equals(message))) {
            classicBag = classicPolicy.getRequestSender();
            this.handleBag(classicBag, assertion);
            classicBag = classicPolicy.getRequestReceiver();
            this.handleBag(classicBag, assertion);
        }
        if (("response".equals(message)) || ("both".equals(message))) {
            classicBag = classicPolicy.getResponseSender();
            this.handleBag(classicBag, assertion);
            classicBag = classicPolicy.getResponseReceiver();
            this.handleBag(classicBag, assertion);
        }
    }

    /**
     * Adds a custom validation assertion to the <code>CustomValidations</code> bag in the classic
     * assertions, creating the bag if it is not already there !TODO: unify with other handleBag
     * implementations.
     * 
     * @param classicAssertions
     *        the classic assertions
     * @param authenticationAssertion
     *        the authentication assertion
     */
    private void handleBag(final AbstractAssertionBag classicAssertions, final AuthenticationAssertion authenticationAssertion) {
        AuthenticationAssertionBag bag =
                (AuthenticationAssertionBag) classicAssertions.getFirstAssertion(CLASSIC_AUTHENTICATION_BAG);
        if (null == bag) {
            bag = new AuthenticationAssertionBag();
            classicAssertions.addAssertion(bag);
        }
        bag.addAssertion(authenticationAssertion);
    }

}
