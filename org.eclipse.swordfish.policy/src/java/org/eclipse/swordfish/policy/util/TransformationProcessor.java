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

/**
 * Process transformation assertions
 * 
 * Processing for this assertion means to extract the assertion during pre-processing and re-adding
 * them during post-processing with the correct location attribute.
 * 
 */
public class TransformationProcessor extends AbstractAssertionProcessor {

    /** The Constant TRANSFORMATION_QNAME. */
    public static final QName TRANSFORMATION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Transformation");

    /** The Constant LOCATION_ATTRIBUTE. */
    public static final QName LOCATION_ATTRIBUTE = new QName("location");

    /**
     * Instantiates a new transformation processor.
     */
    public TransformationProcessor() {

    }

    /**
     * Transforming adds load -> prefer alternatives with fewer transformations.
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
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#postProcessAlternative(org.apache.ws.policy.All,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean postProcessAlternative(final All alternative, final Object preProcessResultConsumer,
            final Object preProcessResultProvider) throws UnexpectedPolicyProcessingException {
        this.postProcessRole(alternative, preProcessResultConsumer, "consumer");
        this.postProcessRole(alternative, preProcessResultProvider, "provider");
        // this assertion can't prevent policies from matching
        return true;
    }

    /**
     * Pre-processing for the Transformation assertion just extracts all transformation assertions
     * from the alternative and adds them to the pre-processing result.
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
        TermIterator sourceIter = new TermIterator(alternative, new PrimitiveAssertionSelector(TRANSFORMATION_QNAME));
        // first collect all transformations in the pre-processing result
        Object ret = this.extractAssertions(alternative, sourceIter);
        return ret;
    }

    /**
     * Post process role.
     * 
     * @param alternative
     *        the alternative
     * @param preProcessResult
     *        the pre process result
     * @param role
     *        the role
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private void postProcessRole(final All alternative, final Object preProcessResult, final String role)
            throws UnexpectedPolicyProcessingException {
        if (null != preProcessResult) {
            if (preProcessResult instanceof PrimitiveAssertion) {
                this.postProcessSingle(alternative, (PrimitiveAssertion) preProcessResult, role);
            } else if (preProcessResult instanceof ArrayList) {
                Iterator it = ((ArrayList) preProcessResult).iterator();
                while (it.hasNext()) {
                    PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
                    this.postProcessSingle(alternative, assertion, role);
                }
            } else
                throw new UnexpectedPolicyProcessingException("Unexpected type of pre-processing result for " + role + " : "
                        + preProcessResult);
        }
    }

    /**
     * Post process single.
     * 
     * @param alternative
     *        the alternative
     * @param assertion
     *        the assertion
     * @param role
     *        the role
     */
    private void postProcessSingle(final All alternative, final PrimitiveAssertion assertion, final String role) {
        assertion.addAttribute(LOCATION_ATTRIBUTE, role);
        alternative.addTerm(assertion);
    }

    // no implementation of getSupportedAssertion and preNormalization since
    // wsp:Optional is not
    // needed / supported by this assertion

}
