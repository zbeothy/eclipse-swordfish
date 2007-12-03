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
 * Process extension assertions
 * 
 * Special handling of optional assertions is necessary to prevent assertion in intersection result
 * if both participants specify optional="true".
 * 
 */
public class ExtensionProcessor extends AbstractAssertionProcessor {

    /** The Constant EXTENSION_QNAME. */
    public static final QName EXTENSION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Extension");

    /** The Constant NAME_ATTRIBUTE. */
    public static final QName NAME_ATTRIBUTE = new QName("name");

    /** The Constant VALUE_ATTRIBUTE. */
    public static final QName VALUE_ATTRIBUTE = new QName("value");

    /** The Constant TYPE_ATTRIBUTE. */
    public static final QName TYPE_ATTRIBUTE = new QName("type");

    /**
     * Instantiates a new extension processor.
     */
    public ExtensionProcessor() {

    }

    /**
     * no preference either way.
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
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#getSupportedAssertion()
     */
    @Override
    public QName getSupportedAssertion() {
        return EXTENSION_QNAME;
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
        ArrayList consumerExtensions = (ArrayList) preProcessResultConsumer;
        ArrayList providerExtensions = (ArrayList) preProcessResultProvider;
        // check that for each assertion in the consumer list there is a
        // matching assertion
        // in the provider list and vice versa
        ret = this.checkRequisites(consumerExtensions, providerExtensions, alternative);
        if (ret) {
            ret = this.checkRequisites(providerExtensions, consumerExtensions, alternative);
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
        Selector selector = new PrimitiveAssertionSelector(EXTENSION_QNAME);
        TermIterator termIt = new TermIterator(alternative, selector);
        Object ret = super.extractAssertions(alternative, termIt);
        return ret;
    }

    /**
     * verifies that all required extensions from <code>source</code> are satisfied by
     * <code>target</code> <br/>Side effect: removes each matched extension from
     * <code>target</code> and adds it to <code>alternative</code>.
     * 
     * @param source
     *        the source
     * @param target
     *        the target
     * @param alternative
     *        the alternative
     * 
     * @return <code>true</code> if all non-optional extensions in <code>source</code> are
     *         satisfied by <code>target</code> <br/> <code>false</code> otherwise
     */
    private boolean checkRequisites(final ArrayList source, final ArrayList target, final All alternative) {
        boolean ret = true;
        if (null != source) {
            for (Iterator iter = source.iterator(); iter.hasNext();) {
                PrimitiveAssertion sourceAssertion = (PrimitiveAssertion) iter.next();
                String optional = sourceAssertion.getAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE);
                if ("true".equals(optional)) {
                    continue;
                }
                boolean found = false;
                if (null != target) {
                    for (Iterator iterator = target.iterator(); iterator.hasNext();) {
                        PrimitiveAssertion targetAssertion = (PrimitiveAssertion) iterator.next();
                        if (sourceAssertion.getAttribute(NAME_ATTRIBUTE).equals(targetAssertion.getAttribute(NAME_ATTRIBUTE))) {
                            String consumerValue = sourceAssertion.getAttribute(VALUE_ATTRIBUTE);
                            String providerValue = targetAssertion.getAttribute(VALUE_ATTRIBUTE);
                            if (((null != consumerValue) && (consumerValue.equals(providerValue)))
                                    || ((null == consumerValue) && (null == providerValue))) {
                                found = true;
                                alternative.addTerm(sourceAssertion);
                                target.remove(targetAssertion);
                                break;
                            }
                        }
                    }
                }
                if (!found) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }

}
