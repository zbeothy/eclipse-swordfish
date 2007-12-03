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
 * The Class AuthorizationAssertionTransformer.
 */
public class AuthorizationAssertionTransformer implements AssertionTransformer {

    /** The Constant LOCATION_ATTRIB. */
    public static final String LOCATION_ATTRIB = "location";

    /** The Constant LOCATION_QNAME. */
    private static final QName LOCATION_QNAME = new QName(LOCATION_ATTRIB);

    /**
     * Instantiates a new authorization assertion transformer.
     */
    public AuthorizationAssertionTransformer() {
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
        final String location = wsAssertion.getAttribute(LOCATION_QNAME);
        final AuthorizationAssertion assertion = new AuthorizationAssertion();
        if ("both".equals(location)) {
            classicPolicy.getRequestSender().addAssertion(assertion);
        }
        classicPolicy.getRequestReceiver().addAssertion(assertion);
    }

}
