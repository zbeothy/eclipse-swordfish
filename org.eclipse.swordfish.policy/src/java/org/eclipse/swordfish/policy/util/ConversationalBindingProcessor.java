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
package org.eclipse.swordfish.policy.util;

import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.selector.Selector;

/**
 * Process extension assertions
 * 
 * Special handling of optional assertions is necessary to prevent assertion in intersection result
 * if both participants specify optional="true".
 * 
 */
public class ConversationalBindingProcessor extends AbstractAssertionProcessor {

    /** The Constant CONVERSATIONAL_BINDING_QNAME. */
    public static final QName CONVERSATIONAL_BINDING_QNAME =
            new QName(PolicyConstants.SOP_ASSERTION_URI, "ConversationalBinding", "sopa");

    /** The Constant SCOPE_ATTRIBUTE. */
    public static final QName SCOPE_ATTRIBUTE = new QName("scope");

    /**
     * Instantiates a new conversational binding processor.
     */
    public ConversationalBindingProcessor() {

    }

    /**
     * prefer alternatives with the lowest binding.
     * 
     * @param alternative
     *        the alternative
     * @param assertion
     *        the assertion
     * 
     * @return the cost
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessor#getCost(org.apache.ws.policy.All,
     *      org.apache.ws.policy.PrimitiveAssertion)
     */
    public int getCost(final All alternative, final PrimitiveAssertion assertion) {
        int ret = 0;
        String scope = assertion.getAttribute(SCOPE_ATTRIBUTE);
        if ("provider".equals(scope)) {
            ret = 10;
        } else {
            ret = 20;
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#getSupportedAssertion()
     */
    @Override
    public QName getSupportedAssertion() {
        return CONVERSATIONAL_BINDING_QNAME;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#postProcessAlternative(org.apache.ws.policy.All,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean postProcessAlternative(final All alternative, final Object preProcessResultConsumer,
            final Object preProcessResultProvider) {
        if (null != preProcessResultProvider) // log corrupted policy
            return false;
        if (null != preProcessResultConsumer) {
            if (1 == ((ArrayList) preProcessResultConsumer).size()) {
                PrimitiveAssertion assertion = (PrimitiveAssertion) ((ArrayList) preProcessResultConsumer).get(0);
                PrimitiveAssertion newAssertion = new PrimitiveAssertion(CONVERSATIONAL_BINDING_QNAME);
                newAssertion.setAttributes(assertion.getAttributes());
                alternative.addTerm(newAssertion);
            } else
                // log corrupted policy
                return false;
        }
        return true;
    }

    /**
     * Pre process alternative.
     * 
     * @param alternative
     *        the alternative
     * 
     * @return the object
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#preProcessAlternative(org.apache.ws.policy.All)
     */
    @Override
    public Object preProcessAlternative(final All alternative) {
        Selector selector = new PrimitiveAssertionSelector(CONVERSATIONAL_BINDING_QNAME);
        TermIterator termIt = new TermIterator(alternative, selector);
        Object ret = super.extractAssertions(alternative, termIt);
        return ret;
    }

}
