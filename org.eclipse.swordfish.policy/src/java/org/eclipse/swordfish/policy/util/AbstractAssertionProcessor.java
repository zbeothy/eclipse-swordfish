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
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;

/**
 * Abstract implementation of <code>AssertionProcessor</code> to provide convenience methods to
 * concrete implemenations.
 * 
 */
public abstract class AbstractAssertionProcessor implements AssertionProcessor {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessor#getSupportedAssertion()
     */
    public QName getSupportedAssertion() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessor#postProcessAlternative(org.apache.ws.policy.All,
     *      java.lang.Object, java.lang.Object)
     */
    public abstract boolean postProcessAlternative(All alternative, Object preProcessResultConsumer, Object preProcessResultProvider)
            throws UnexpectedPolicyProcessingException;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessor#preNormalization(org.apache.ws.policy.PrimitiveAssertion)
     */
    public void preNormalization(final PrimitiveAssertion assertion) throws UnexpectedPolicyProcessingException {
        // no-op
    }

    // no implementation required for standard wsp:Optional handling

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AssertionProcessor#preProcessAlternative(org.apache.ws.policy.All)
     */
    public abstract Object preProcessAlternative(All alternative) throws UnexpectedPolicyProcessingException;

    /**
     * Extract assertions.
     * 
     * @param alternative
     *        the alternative
     * @param sourceIterator
     *        the source iterator
     * 
     * @return the object
     * 
     * !TODO: refactor to return ArrayList
     */
    protected Object extractAssertions(final All alternative, final TermIterator sourceIterator) {
        Object ret = null;
        while (sourceIterator.hasNext()) {
            if (null == ret) {
                ret = new ArrayList();
            }
            ((ArrayList) ret).add(sourceIterator.next());
        }
        // now remove them from the alternative
        if (null != ret) {
            Iterator listIter = ((ArrayList) ret).iterator();
            while (listIter.hasNext()) {
                Object assertion = listIter.next();
                alternative.remove((Assertion) assertion);
            }
        }
        return ret;
    }

    /**
     * Hide optional.
     * 
     * @param assertion
     *        the assertion
     */
    protected void hideOptional(final PrimitiveAssertion assertion) {
        // replace @wsp:Optional with custom attribute to prevent
        // normalization (handled during post-process)
        if (assertion.isOptional()) {
            assertion.addAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE, "true");
            assertion.setOptional(false);
            assertion.removeAttribute(PolicyConstants.WSP_OPTIONAL_ATTRIBUTE);
        }
    }

}
