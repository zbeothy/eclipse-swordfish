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
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;

/**
 * Process tracking level assertions.
 * 
 */
public class TrackingLevelProcessor extends AbstractAssertionProcessor {

    /** The Constant TRACKING_LEVEL_QNAME. */
    public static final QName TRACKING_LEVEL_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "TrackingLevel", "sopa");

    /** The Constant MIN_ATTRIBUTE. */
    public static final QName MIN_ATTRIBUTE = new QName("min");

    /** The Constant MAX_ATTRIBUTE. */
    public static final QName MAX_ATTRIBUTE = new QName("max");

    /** The Constant VALUE_ATTRIBUTE. */
    public static final QName VALUE_ATTRIBUTE = new QName("value");

    /** The Constant TYPE_ATTRIBUTE. */
    public static final QName TYPE_ATTRIBUTE = new QName("type");

    /** The Constant OPTIONAL_ATTRIBUTE. */
    public static final QName OPTIONAL_ATTRIBUTE = PolicyConstants.SOP_OPTIONAL_ATTRIBUTE;

    /** The Constant levels. */
    private static final HashMap LEVELS = new HashMap();

    /** The Constant reverseLevels. */
    private static final HashMap REVERS_LEVELS = new HashMap();

    /** The Constant MIN_VALUE. */
    private static final Integer MIN_VALUE = new Integer(0);

    /** The Constant MAX_VALUE. */
    private static final Integer MAX_VALUE = new Integer(4);

    static {
        LEVELS.put("detail", new Integer(4));
        LEVELS.put("trace", new Integer(3));
        LEVELS.put("operation", new Integer(2));
        LEVELS.put("summary", new Integer(1));
        LEVELS.put("none", new Integer(0));
        REVERS_LEVELS.put(new Integer(4), "detail");
        REVERS_LEVELS.put(new Integer(3), "trace");
        REVERS_LEVELS.put(new Integer(2), "operation");
        REVERS_LEVELS.put(new Integer(1), "summary");
        REVERS_LEVELS.put(new Integer(0), "none");
    }

    /**
     * Instantiates a new tracking level processor.
     */
    public TrackingLevelProcessor() {
    }

    /**
     * Prefer alternatives where tracking is enabled over those without tracking Prefer alternatives
     * with tracking level "operation" over those with more detailed tracking.
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
        String level = assertion.getAttribute(VALUE_ATTRIBUTE);
        if ("none".equals(level)) {
            ret = 20;
        } else if ("operation".equals(level)) {
            ret = -10;
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
        return TRACKING_LEVEL_QNAME;
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
        boolean ret = true;
        PrimitiveAssertion consumer = null;
        PrimitiveAssertion provider = null;

        consumer = this.getDefinition(preProcessResultConsumer);
        provider = this.getDefinition(preProcessResultProvider);

        if (null != consumer) {
            if (null != provider) {
                // both participants specified tracking -> intersect
                ret = this.intersect(consumer, provider, alternative);
            } else {
                // only consumer specified tracking
                ret = this.handleSingleAssertion(consumer, alternative);
            }
        } else if (null != provider) {
            // only provider specified tracking
            ret = this.handleSingleAssertion(provider, alternative);
        }
        return ret;
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
     * just extract tracking assertions and remember them for later use.
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
        TermIterator it = new TermIterator(alternative, new PrimitiveAssertionSelector(TRACKING_LEVEL_QNAME));
        Object ret = this.extractAssertions(alternative, it);
        return ret;
    }

    /**
     * Gets the definition.
     * 
     * @param preProcessResult
     *        the pre process result
     * 
     * @return the definition
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private PrimitiveAssertion getDefinition(final Object preProcessResult) throws UnexpectedPolicyProcessingException {
        PrimitiveAssertion ret = null;
        try {
            ArrayList list = (ArrayList) preProcessResult;
            if ((null != list) && (list.size() >= 1)) {
                ret = (PrimitiveAssertion) list.get(0);
            }
        } catch (ClassCastException e) {
            throw new UnexpectedPolicyProcessingException("Unexpected type of pre-processing result", e);
        }
        return ret;
    }

    /**
     * Gets the max value.
     * 
     * @param assertion
     *        the assertion
     * 
     * @return the max value
     */
    private Integer getMaxValue(final PrimitiveAssertion assertion) {
        String value = assertion.getAttribute(VALUE_ATTRIBUTE);
        if (null == value) {
            value = assertion.getAttribute(MAX_ATTRIBUTE);
        }
        Integer ret = null;
        if (null == value) {
            ret = MAX_VALUE;
        } else {
            ret = (Integer) LEVELS.get(value.toLowerCase());
        }
        return ret;
    }

    /**
     * Gets the min value.
     * 
     * @param assertion
     *        the assertion
     * 
     * @return the min value
     */
    private Integer getMinValue(final PrimitiveAssertion assertion) {
        String value = assertion.getAttribute(VALUE_ATTRIBUTE);
        if (null == value) {
            value = assertion.getAttribute(MIN_ATTRIBUTE);
        }
        Integer ret = null;
        if (null == value) {
            ret = MIN_VALUE;
        } else {
            ret = (Integer) LEVELS.get(value.toLowerCase());
        }
        return ret;
    }

    /**
     * Handle single assertion.
     * 
     * @param source
     *        the source
     * @param alternative
     *        the alternative
     * 
     * @return true, if successful
     */
    private boolean handleSingleAssertion(final PrimitiveAssertion source, final All alternative) {
        String optional = source.getAttribute(OPTIONAL_ATTRIBUTE);
        if ((null != optional) && ("true".equals(optional.toLowerCase()))) // assertion only
            // describes
            // capabilities of
            // participant,
            // no further action necessary
            return true;
        String value = source.getAttribute(MIN_ATTRIBUTE);
        if (null != value) {
            source.addAttribute(VALUE_ATTRIBUTE, value);
        }
        boolean ret = false;
        if (null != source.getAttribute(VALUE_ATTRIBUTE)) {
            // just to make sure - could be placed in above if clause also if
            // input is certain to be valid
            source.removeAttribute(MIN_ATTRIBUTE);
            source.removeAttribute(MAX_ATTRIBUTE);
            alternative.addTerm(source);
            ret = true;
        }
        return ret;
    }

    /**
     * Intersect.
     * 
     * @param consumer
     *        the consumer
     * @param provider
     *        the provider
     * @param alternative
     *        the alternative
     * 
     * @return true, if successful
     */
    private boolean intersect(final PrimitiveAssertion consumer, final PrimitiveAssertion provider, final All alternative) {
        String consumerOptional = consumer.getAttribute(OPTIONAL_ATTRIBUTE);
        String providerOptional = provider.getAttribute(OPTIONAL_ATTRIBUTE);
        if ((null != consumerOptional) && (null != providerOptional) && ("true".equals(consumerOptional.toLowerCase()))
                && ("true".equals(providerOptional.toLowerCase()))) return true;
        Integer minConsumer = this.getMinValue(consumer);
        Integer maxConsumer = this.getMaxValue(consumer);
        Integer minProvider = this.getMinValue(provider);
        Integer maxProvider = this.getMaxValue(provider);
        PrimitiveAssertion res = null;
        if ((null != maxConsumer) && (null != maxProvider) && (0 <= maxConsumer.compareTo(minProvider))
                && (0 <= maxProvider.compareTo(minConsumer))) {
            Integer iVal = new Integer(Math.max(minConsumer.intValue(), minProvider.intValue()));
            String value = (String) REVERS_LEVELS.get(iVal);
            if (null != value) {
                res = new PrimitiveAssertion(TRACKING_LEVEL_QNAME);
                res.addAttribute(VALUE_ATTRIBUTE, value);
            }
        }
        boolean ret = false;
        if (null != res) {
            ret = this.handleSingleAssertion(res, alternative);
        }
        return ret;
    }

}
