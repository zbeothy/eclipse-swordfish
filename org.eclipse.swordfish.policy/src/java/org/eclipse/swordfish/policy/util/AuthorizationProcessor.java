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
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.selector.Selector;

/**
 * Process transport assertions
 * 
 * Include assertion if specified by provider. Set location attribute to "both" if consumer
 * specifies matching assertion, "provider" otherwise
 * 
 */
public class AuthorizationProcessor extends AbstractAssertionProcessor {

    /** The Constant AUTHORIZATION_QNAME. */
    public static final QName AUTHORIZATION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Authorization", "sopa");

    /** The Constant LOCATION_ATTRIBUTE. */
    public static final QName LOCATION_ATTRIBUTE = new QName("location");

    /**
     * Instantiates a new authorization processor.
     */
    public AuthorizationProcessor() {

    }

    /**
     * prefer alternatives without authorization (saves load).
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
        return 10;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#getSupportedAssertion()
     */
    @Override
    public QName getSupportedAssertion() {
        return AUTHORIZATION_QNAME;
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
        ArrayList consumerAssertions = (ArrayList) preProcessResultConsumer;
        ArrayList providerAssertions = (ArrayList) preProcessResultProvider;
        if (providerAssertions.size() >= 1) {
            PrimitiveAssertion result = new PrimitiveAssertion(AUTHORIZATION_QNAME);
            if (consumerAssertions.size() >= 1) {
                result.addAttribute(LOCATION_ATTRIBUTE, "both");
            } else {
                PrimitiveAssertion providerAssertion = (PrimitiveAssertion) providerAssertions.get(0);
                String mode = providerAssertion.getAttribute(PolicyConstants.SOP_MODE_ATTRIBUTE);
                if ("runtime" != mode) {
                    result.addAttribute(LOCATION_ATTRIBUTE, "provider");
                } else
                    // during runtime, this means possible forged agreed policy
                    return false;
            }
            alternative.addTerm(result);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#preNormalization(org.apache.ws.policy.PrimitiveAssertion)
     */
    @Override
    public void preNormalization(final PrimitiveAssertion assertion) {
        // no-op
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
        Selector selector = new PrimitiveAssertionSelector(AUTHORIZATION_QNAME);
        TermIterator termIt = new TermIterator(alternative, selector);
        ArrayList ret = new ArrayList();
        while (termIt.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) termIt.next();
            ret.add(assertion);
        }
        for (Iterator listIter = ret.iterator(); listIter.hasNext();) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) listIter.next();
            alternative.remove(assertion);
        }
        return ret;
    }

}
