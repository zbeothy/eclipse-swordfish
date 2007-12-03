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
import org.apache.commons.lang.math.IntRange;
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
public class PriorityProcessor extends AbstractAssertionProcessor {

    /** The Constant PRIORITY_QNAME. */
    public static final QName PRIORITY_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Priority", "sopa");

    /** The Constant MIN_ATTRIBUTE. */
    public static final QName MIN_ATTRIBUTE = new QName("min");

    /** The Constant MAX_ATTRIBUTE. */
    public static final QName MAX_ATTRIBUTE = new QName("max");

    /** The Constant VALUE_ATTRIBUTE. */
    public static final QName VALUE_ATTRIBUTE = new QName("value");

    /** The Constant SUPPORT_ATTRIBUTE. */
    public static final QName SUPPORT_ATTRIBUTE = new QName("support");

    /** The Constant PREFERRED_VALUE. */
    public static final int PREFERRED_VALUE = 5;

    /**
     * Instantiates a new priority processor.
     */
    public PriorityProcessor() {

    }

    /**
     * fixed cost for all alternatives with priority - prefer assertions without to ease load on
     * transport layer.
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
        return PRIORITY_QNAME;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#postProcessAlternative(org.apache.ws.policy.All,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean postProcessAlternative(final All alternative, final Object preProcessResultConsumer,
            final Object preProcessResultProvider) throws UnexpectedPolicyProcessingException {
        if (null == preProcessResultConsumer) return true;
        IntRange consumerRange = this.extractRange((ArrayList) preProcessResultConsumer);
        IntRange providerRange = this.extractRange((ArrayList) preProcessResultProvider);
        if (!consumerRange.overlapsRange(providerRange)) return false;
        if ((consumerRange.containsInteger(PREFERRED_VALUE)) && (providerRange.containsInteger(PREFERRED_VALUE))) {
            this.addAssertion(PREFERRED_VALUE, alternative);
        } else {
            int min = Math.max(consumerRange.getMinimumInteger(), providerRange.getMinimumInteger());
            int max = Math.min(consumerRange.getMaximumInteger(), providerRange.getMaximumInteger());
            if (5 < min) {
                this.addAssertion(min, alternative);
            } else {
                this.addAssertion(max, alternative);
            }
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
        this.hideOptional(assertion);
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
        Selector selector = new PrimitiveAssertionSelector(PRIORITY_QNAME);
        TermIterator termIt = new TermIterator(alternative, selector);
        Object ret = super.extractAssertions(alternative, termIt);
        return ret;
    }

    /**
     * Adds the assertion.
     * 
     * @param value
     *        the value
     * @param compositor
     *        the compositor
     */
    private void addAssertion(final int value, final All compositor) {
        PrimitiveAssertion assertion = new PrimitiveAssertion(PRIORITY_QNAME);
        assertion.addAttribute(VALUE_ATTRIBUTE, Integer.toString(value));
        compositor.addTerm(assertion);
    }

    /**
     * Extract range.
     * 
     * @param preProcessResult
     *        the pre process result
     * 
     * @return the int range
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private IntRange extractRange(final ArrayList preProcessResult) throws UnexpectedPolicyProcessingException {
        String sMin = null;
        String sMax = null;
        if ((null != preProcessResult) && (preProcessResult.size() >= 1)) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) preProcessResult.get(0);
            sMin = assertion.getAttribute(VALUE_ATTRIBUTE);
            if (null == sMin) {
                sMin = assertion.getAttribute(MIN_ATTRIBUTE);
                sMax = assertion.getAttribute(MAX_ATTRIBUTE);
            } else {
                sMax = sMin;
            }
            String support = assertion.getAttribute(SUPPORT_ATTRIBUTE);
            if ("false".equals(support)) {
                sMin = "-1";
                sMax = "-1";
            }
        } else
            return new IntRange(0, 9);
        IntRange ret = null;
        if ((null != sMin) && (null != sMax)) {
            try {
                int min = Integer.parseInt(sMin);
                int max = Integer.parseInt(sMax);
                ret = new IntRange(min, max);
            } catch (NumberFormatException e) {
                throw new UnexpectedPolicyProcessingException("Illegal value for number (" + sMin + ") or (" + sMax + ")");
            }
        } else {
            ret = new IntRange(0, 9);
        }
        return ret;
    }

}
