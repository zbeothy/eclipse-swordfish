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

import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.Role;

/**
 * The Class MaxResponseTimeProcessor.
 */
public class MaxResponseTimeProcessor extends AbstractAssertionProcessor implements AssertionProcessor {

    /** The Constant MAXRESPONSETIME_ASSERTION. */
    public static final QName MAXRESPONSETIME_ASSERTION = new QName(PolicyConstants.SOP_ASSERTION_URI, "MaxResponseTime");

    /** The Constant MAXRESPONSETIME_VALUE_ATTRIBUTE. */
    public static final QName MAXRESPONSETIME_VALUE_ATTRIBUTE = new QName("value");

    /** The Constant MAXRESPONSETIME_LOCATION_ATTRIBUTE. */
    public static final QName MAXRESPONSETIME_LOCATION_ATTRIBUTE = new QName("location");

    /** The Constant MAXRESPONSETIME_CHECK_ATTRIBUTE. */
    public static final QName MAXRESPONSETIME_CHECK_ATTRIBUTE = new QName("check");

    /**
     * The overhead in milliseconds for message transport and SOPware processing when comparing
     * assertions specified for different locations TODO: make configurable, dynamic.
     */
    private static long transportOverhead = 2000;

    /**
     * Gets the transport overhead.
     * 
     * @return Returns the transportOverhead.
     */
    public static long getTransportOverhead() {
        return transportOverhead;
    }

    /**
     * Sets the transport overhead.
     * 
     * @param transportOverhead
     *        The transportOverhead to set.
     */
    public static void setTransportOverhead(final long transportOverhead) {
        MaxResponseTimeProcessor.transportOverhead = transportOverhead;
    }

    /**
     * prefer alternatives that monitor max response time over those who don't.
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
        return -10;
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
        boolean ret = false;
        TermIterator it = new TermIterator(alternative, new PrimitiveAssertionSelector(MAXRESPONSETIME_ASSERTION));
        PrimitiveAssertion requires = null;
        PrimitiveAssertion assures = null;
        while (it.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
            String role = assertion.getAttribute(PolicyConstants.SOP_ROLE_ATTRIBUTE);
            if (Role.CONSUMER.equals(role)) {
                requires = assertion;
            } else if (Role.PROVIDER.equals(role)) {
                assures = assertion;
            } else
                throw new UnexpectedPolicyProcessingException("Encountered unexpected role " + role);
        }
        if (null != requires) {
            if (null != assures) {
                ret = this.compare(requires, assures);
            }
            if (ret) {
                // we only check for the time specified by the consumer
                // TODO: idea - include check for provider specification also,
                // but with different error level
                // - requires changes to interceptor
                alternative.remove(assures);
            }
        } else {
            // nothing required, so no further checks
            ret = true;
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#preProcessAlternative(org.apache.ws.policy.All)
     */
    @Override
    public Object preProcessAlternative(final All alternative) {
        // no action necessary
        return null;
    }

    /**
     * Compares required response time from consumer with assured response time of provider, taking
     * into acount network overhead if times are specified for different locations.
     * 
     * @param requires
     *        the requires
     * @param assures
     *        the assures
     * 
     * @return true, if compare
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private boolean compare(final PrimitiveAssertion requires, final PrimitiveAssertion assures)
            throws UnexpectedPolicyProcessingException {
        long required = 0;
        long assured = 0;
        try {
            required = this.computeNormalizedResponsetime(requires);
            assured = this.computeNormalizedResponsetime(assures);
        } catch (NumberFormatException e) {
            throw new UnexpectedPolicyProcessingException("Illegal value for response time", e);
        }
        return assured <= required;
    }

    /**
     * Computes the response time from a given assertion normalized for the consumer location.
     * 
     * @param assertion
     *        the assertion
     * 
     * @return the long
     */
    private long computeNormalizedResponsetime(final PrimitiveAssertion assertion) {
        String value = assertion.getAttribute(MAXRESPONSETIME_VALUE_ATTRIBUTE);
        long time = new Long(value).longValue();
        if (!"consumer".equals(assertion.getAttribute(MAXRESPONSETIME_LOCATION_ATTRIBUTE))) {
            time += transportOverhead;
        }
        return time;
    }

    // no implementation of getSupportedAssertion and preNormalization since
    // wsp:Optional is
    // handled according to the standard for this assertion

}
