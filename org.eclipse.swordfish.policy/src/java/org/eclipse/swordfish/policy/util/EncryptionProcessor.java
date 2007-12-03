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
public class EncryptionProcessor extends AbstractAssertionProcessor {

    /** The Constant ENCRYPTION_QNAME. */
    public static final QName ENCRYPTION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Encryption", "sopa");

    /** The Constant MESSAGE_ATTRIBUTE. */
    public static final QName MESSAGE_ATTRIBUTE = new QName("message");

    /** indexes into result matrix. */
    private static final int NONE = 0;

    /** The Constant OPTIONAL. */
    private static final int OPTIONAL = 1;

    /** The Constant MANDATORY. */
    private static final int MANDATORY = 2;

    /** values in result matrix. */
    private static final int NO_MATCH = -1;

    /** The Constant NO_ENCRYPTION. */
    private static final int NO_ENCRYPTION = 0;

    /** The Constant ENCRYPTION. */
    private static final int ENCRYPTION = 1;

    /** Matching results indexed by [senderIndex][receiverIndex]. */
    private static final int[][] RESULTS =
            { {NO_ENCRYPTION, NO_ENCRYPTION, NO_MATCH}, {NO_ENCRYPTION, NO_ENCRYPTION, ENCRYPTION},
                    {NO_MATCH, ENCRYPTION, ENCRYPTION}};

    /**
     * Instantiates a new encryption processor.
     */
    public EncryptionProcessor() {

    }

    /**
     * prefer alternative without encryption (adds system load).
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
        return ENCRYPTION_QNAME;
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
        PrimitiveAssertion consumerAssertion = this.getMessageSpecification("request", (ArrayList) preProcessResultConsumer);
        PrimitiveAssertion providerAssertion = this.getMessageSpecification("request", (ArrayList) preProcessResultProvider);
        boolean ret = this.matchAssertions("request", consumerAssertion, providerAssertion, alternative);
        if (ret) {
            consumerAssertion = this.getMessageSpecification("response", (ArrayList) preProcessResultConsumer);
            providerAssertion = this.getMessageSpecification("response", (ArrayList) preProcessResultProvider);
            ret = this.matchAssertions("response", providerAssertion, consumerAssertion, alternative);
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
        Selector selector = new PrimitiveAssertionSelector(ENCRYPTION_QNAME);
        TermIterator termIt = new TermIterator(alternative, selector);
        Object ret = super.extractAssertions(alternative, termIt);
        return ret;
    }

    /**
     * Gets the message specification.
     * 
     * @param message
     *        the message
     * @param list
     *        the list
     * 
     * @return the message specification
     */
    PrimitiveAssertion getMessageSpecification(final String message, final ArrayList list) {
        PrimitiveAssertion ret = null;
        if (null != list) {
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                PrimitiveAssertion assertion = (PrimitiveAssertion) iter.next();
                String assertionMessage = assertion.getAttribute(MESSAGE_ATTRIBUTE);
                if (("all".equals(assertionMessage)) || (null == assertionMessage) || (message.equals(assertionMessage))) {
                    ret = assertion;
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * Adds the assertion.
     * 
     * @param message
     *        the message
     * @param compositor
     *        the compositor
     */
    private void addAssertion(final String message, final All compositor) {
        PrimitiveAssertion assertion = new PrimitiveAssertion(ENCRYPTION_QNAME);
        assertion.addAttribute(MESSAGE_ATTRIBUTE, message);
        compositor.addTerm(assertion);
    }

    /**
     * Gets the index.
     * 
     * @param assertion
     *        the assertion
     * 
     * @return the index
     */
    private int getIndex(final PrimitiveAssertion assertion) {
        int ret = -1;
        if (null == assertion) {
            ret = NONE;
        } else {
            String optional = assertion.getAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE);
            if ("true".equals(optional)) {
                ret = OPTIONAL;
            } else {
                ret = MANDATORY;
            }
        }
        return ret;
    }

    /**
     * Match assertions.
     * 
     * @param message
     *        the message
     * @param senderAssertion
     *        the sender assertion
     * @param receiverAssertion
     *        the receiver assertion
     * @param compositor
     *        the compositor
     * 
     * @return true, if successful
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private boolean matchAssertions(final String message, final PrimitiveAssertion senderAssertion,
            final PrimitiveAssertion receiverAssertion, final All compositor) throws UnexpectedPolicyProcessingException {
        String mode = null;
        if (null != senderAssertion) {
            mode = senderAssertion.getAttribute(PolicyConstants.SOP_MODE_ATTRIBUTE);
        } else if (null != receiverAssertion) {
            mode = receiverAssertion.getAttribute(PolicyConstants.SOP_MODE_ATTRIBUTE);
        }
        if ("runtime".equals(mode))
            return this.matchAssertionsRuntime(message, senderAssertion, receiverAssertion, compositor);
        else
            return this.matchAssertionsLookup(message, senderAssertion, receiverAssertion, compositor);
    }

    /**
     * Match assertions during normal lookup.
     * 
     * @param message
     *        the message
     * @param senderAssertion
     *        the sender assertion
     * @param receiverAssertion
     *        the receiver assertion
     * @param compositor
     *        the compositor
     * 
     * @return true, if match assertions lookup
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private boolean matchAssertionsLookup(final String message, final PrimitiveAssertion senderAssertion,
            final PrimitiveAssertion receiverAssertion, final All compositor) throws UnexpectedPolicyProcessingException {
        int senderIndex = this.getIndex(senderAssertion);
        int receiverIndex = this.getIndex(receiverAssertion);
        int result = RESULTS[senderIndex][receiverIndex];
        boolean ret = false;
        switch (result) {
            case NO_ENCRYPTION:
                ret = true;
                break;
            case ENCRYPTION:
                this.addAssertion(message, compositor);
                ret = true;
                break;
            case NO_MATCH:
                ret = false;
                break;
            default:
                throw new UnexpectedPolicyProcessingException("Encountered unexpected state " + result + " for " + message);
        }
        return ret;
    }

    /**
     * Match assertions for verification of agreed policy versus provider policy.
     * 
     * @param message
     *        the message
     * @param senderAssertion
     *        the sender assertion
     * @param receiverAssertion
     *        the receiver assertion
     * @param compositor
     *        the compositor
     * 
     * @return true, if match assertions runtime
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private boolean matchAssertionsRuntime(final String message, final PrimitiveAssertion senderAssertion,
            final PrimitiveAssertion receiverAssertion, final All compositor) throws UnexpectedPolicyProcessingException {
        // the only attack that is possible is that the consumer uses a forged
        // agreed policy to force the provider to
        // encrypt the response message, which would result in a runtime
        // exception if the provider does not support encryption
        if ("response".equals(message)) {
            if ((null == senderAssertion) && (null != receiverAssertion)) // in this context:
                // senderAssertion == provider
                // receiverAssertion == agreed
                return false;
        }
        return true;
    }

}
