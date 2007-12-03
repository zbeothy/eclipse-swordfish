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
public class SubscriptionProcessor extends AbstractAssertionProcessor {

    /** The Constant SUBSCRIPTION_QNAME. */
    public static final QName SUBSCRIPTION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Subscription");

    /** The Constant TYPE_ATTRIBUTE. */
    public static final QName TYPE_ATTRIBUTE = new QName("type");

    /**
     * Instantiates a new subscription processor.
     */
    public SubscriptionProcessor() {

    }

    /**
     * prefer non-durable over durable subscription.
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
        String type = assertion.getAttribute(TYPE_ATTRIBUTE);
        if ("durable".equals(type)) {
            ret = 20;
        } else {
            ret = 10;
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
        return SUBSCRIPTION_QNAME;
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
        boolean ret = true;
        PrimitiveAssertion consumerSubscription = null;
        if (null != preProcessResultConsumer) {
            consumerSubscription = (PrimitiveAssertion) ((ArrayList) preProcessResultConsumer).get(0);
        } else
            // no subscription by consumer -> no action necessary
            return true;
        PrimitiveAssertion providerSubscription = null;
        if (null != preProcessResultProvider) {
            providerSubscription = (PrimitiveAssertion) ((ArrayList) preProcessResultProvider).get(0);
            String consumerType = consumerSubscription.getAttribute(TYPE_ATTRIBUTE);
            String providerType = providerSubscription.getAttribute(TYPE_ATTRIBUTE);
            if ((null != consumerType) && !(consumerType.equals(providerType))) return false;
        }
        alternative.addTerm(consumerSubscription);
        return ret;
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
        Selector selector = new PrimitiveAssertionSelector(SUBSCRIPTION_QNAME);
        TermIterator termIt = new TermIterator(alternative, selector);
        Object ret = super.extractAssertions(alternative, termIt);
        return ret;
    }

}
