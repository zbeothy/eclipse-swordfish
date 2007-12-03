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

/**
 * The Interface AssertionProcessor.
 */
public interface AssertionProcessor {

    /**
     * computes the cost of a given alternative wrt the assertion(s) processed by the processor The
     * cost is an arbitrary measure for each assertion choice that is used to select the alternative
     * to use in multi-alternative results. Custom value for different assertions choices are
     * multiples of 10 This is called after matching for each assertion that is included in the
     * alternative -> cost of assertion not being present is 0
     * 
     * @param alternative
     *        to compute cost for
     * @param assertion
     *        the assertion
     * 
     * @return positive or zero int specifying the cost of processing the alternative
     * 
     * @assertion the assertion choice in <code>alternative</code><br/> provided here to prevent
     *            each processor through the terms of <code>alternative</code>
     */
    int getCost(All alternative, PrimitiveAssertion assertion);

    /**
     * Allows the handler to declare interest of handling a particular PrimitiveAssertion before
     * normalization.
     * 
     * @return the QName for the primitive assertion the processor handles
     */
    QName getSupportedAssertion();

    /**
     * checks if one alternative in an intersection result is valid in the context of one concrete
     * assertion type.
     * 
     * @param alternative
     *        the alternative
     * @param preProcessResultConsumer
     *        the pre process result consumer
     * @param preProcessResultProvider
     *        the pre process result provider
     * 
     * @return true if the alternative is valid in the context of the assertion false if the
     *         alternative should be removed from the matching result
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    boolean postProcessAlternative(All alternative, Object preProcessResultConsumer, Object preProcessResultProvider)
            throws UnexpectedPolicyProcessingException;

    /**
     * called when the processor has registered itself as a handler for certain type of primitive
     * assertions (identified via QName).
     * 
     * @param assertion
     *        the assertion
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    void preNormalization(PrimitiveAssertion assertion) throws UnexpectedPolicyProcessingException;

    /**
     * Pre process alternative.
     * 
     * @param alternative
     *        the alternative
     * 
     * @return state information about <code>alternative</code> that can be used in
     *         post-processing
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    Object preProcessAlternative(All alternative) throws UnexpectedPolicyProcessingException;

}
