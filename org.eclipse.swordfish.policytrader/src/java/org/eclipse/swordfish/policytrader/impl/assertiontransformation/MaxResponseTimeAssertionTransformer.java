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
 * The Class MaxResponseTimeAssertionTransformer.
 */
public class MaxResponseTimeAssertionTransformer implements AssertionTransformer {

    /** The Constant VALUE_ATTRIB. */
    public static final String VALUE_ATTRIB = "value";

    /** The Constant LOCATION_ATTRIB. */
    public static final String LOCATION_ATTRIB = "location";

    /** The Constant LOCATION_CONSUMER. */
    public static final String LOCATION_CONSUMER = "consumer";

    /** The Constant LOCATION_PROVIDER. */
    public static final String LOCATION_PROVIDER = "provider";

    /** The Constant GRACE_ATTRIB. */
    public static final String GRACE_ATTRIB = "grace";

    // public static final String CHECK_ATTRIB = "check";

    /** The Constant VALUE_QNAME. */
    private static final QName VALUE_QNAME = new QName(VALUE_ATTRIB);

    /** The Constant LOCATION_QNAME. */
    private static final QName LOCATION_QNAME = new QName(LOCATION_ATTRIB);

    /** The Constant GRACE_QNAME. */
    private static final QName GRACE_QNAME = new QName(GRACE_ATTRIB);

    // private static final QName CHECK_QNAME = new QName(CHECK_ATTRIB);

    /**
     * Instantiates a new max response time assertion transformer.
     */
    public MaxResponseTimeAssertionTransformer() {
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
        final String value = wsAssertion.getAttribute(VALUE_QNAME);
        final String location = wsAssertion.getAttribute(LOCATION_QNAME);
        final String grace = wsAssertion.getAttribute(GRACE_QNAME);
        // final String check = wsAssertion.getAttribute(CHECK_QNAME);
        final MaxResponseTimeAssertion mra = new MaxResponseTimeAssertion(value, location, grace);
        if (LOCATION_CONSUMER.equals(location)) {
            classicPolicy.getRequestSender().addAssertion(mra);
            classicPolicy.getResponseReceiver().addAssertion(mra);
        } else if (LOCATION_PROVIDER.equals(location)) {
            classicPolicy.getRequestReceiver().addAssertion(mra);
            classicPolicy.getResponseSender().addAssertion(mra);
        }
    }
}
