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
 * The Class ExtensionAssertionTransformer.
 */
public class ExtensionAssertionTransformer implements AssertionTransformer {

    /** The Constant NAME_ATTRIB. */
    public static final String NAME_ATTRIB = "name";

    /** The Constant VALUE_ATTRIB. */
    public static final String VALUE_ATTRIB = "value";

    /** The Constant NAME_QNAME. */
    private static final QName NAME_QNAME = new QName(NAME_ATTRIB);

    /** The Constant VALUE_QNAME. */
    private static final QName VALUE_QNAME = new QName(VALUE_ATTRIB);

    /**
     * Instantiates a new extension assertion transformer.
     */
    public ExtensionAssertionTransformer() {
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
        final String name = wsAssertion.getAttribute(NAME_QNAME);
        final String value = wsAssertion.getAttribute(VALUE_QNAME);
        final ExtensionAssertion ea = new ExtensionAssertion(name, value);
        classicPolicy.getRequestSender().addAssertion(ea);
        classicPolicy.getRequestReceiver().addAssertion(ea);
        classicPolicy.getResponseSender().addAssertion(ea);
        classicPolicy.getResponseReceiver().addAssertion(ea);
    }
}
