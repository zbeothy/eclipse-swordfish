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
 * The Class TrackingLevelAssertionTransformer.
 */
public class TrackingLevelAssertionTransformer implements AssertionTransformer {

    /** The Constant VALUE_ATTRIB. */
    public static final String VALUE_ATTRIB = "value";

    /** The Constant VALUE_QNAME. */
    private static final QName VALUE_QNAME = new QName(VALUE_ATTRIB);

    /**
     * Instantiates a new tracking level assertion transformer.
     */
    public TrackingLevelAssertionTransformer() {
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
        final TrackingLevelAssertion tla = new TrackingLevelAssertion(value);
        classicPolicy.getRequestSender().addAssertion(tla);
        classicPolicy.getRequestReceiver().addAssertion(tla);
        classicPolicy.getResponseSender().addAssertion(tla);
        classicPolicy.getResponseReceiver().addAssertion(tla);
    }
}
