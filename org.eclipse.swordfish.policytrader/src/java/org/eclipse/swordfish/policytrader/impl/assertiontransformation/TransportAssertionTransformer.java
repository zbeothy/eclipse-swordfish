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
import java.util.Map;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * The Class TransportAssertionTransformer.
 */
public class TransportAssertionTransformer implements AssertionTransformer {

    /**
     * Creates the classic transport assertions.
     * 
     * @return the map
     */
    private static Map createClassicTransportAssertions() {
        final Map result = new HashMap();
        for (int i = 0; i < CLASSIC_TRANSPORT_TYPES.length; i++) {
            final String type = CLASSIC_TRANSPORT_TYPES[i];
            final QKey wspName = WSP_TRANSPORT_ASSERTIONS[i];
            final TransportAssertion a = new TransportAssertion(type);
            result.put(wspName, a);
        }
        return result;
    }

    /** The classic transport assertions. */
    private final Map classicTransportAssertions = createClassicTransportAssertions();

    /**
     * Instantiates a new transport assertion transformer.
     */
    public TransportAssertionTransformer() {
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
        final TransportAssertion ta = this.getClassicTransportAssertion(wspAssertionName);
        final AbstractAssertionBag reqs = classicPolicy.getRequestSender();
        TransportAssertionBag tbag = (TransportAssertionBag) reqs.getFirstAssertion(CLASSIC_TRANSPORT_BAG);
        if (null == tbag) {
            tbag = new TransportAssertionBag();
            reqs.addAssertion(tbag);
        }
        tbag.addAssertion(ta);
        final AbstractAssertionBag rsps = classicPolicy.getResponseSender();
        tbag = (TransportAssertionBag) rsps.getFirstAssertion(CLASSIC_TRANSPORT_BAG);
        if (null == tbag) {
            tbag = new TransportAssertionBag();
            rsps.addAssertion(tbag);
        }
        tbag.addAssertion(ta);
    }

    /**
     * Gets the classic transport assertion.
     * 
     * @param wspAssertionName
     *        the wsp assertion name
     * 
     * @return the classic transport assertion
     */
    private TransportAssertion getClassicTransportAssertion(final QKey wspAssertionName) {
        return (TransportAssertion) this.classicTransportAssertions.get(wspAssertionName);
    }
}
