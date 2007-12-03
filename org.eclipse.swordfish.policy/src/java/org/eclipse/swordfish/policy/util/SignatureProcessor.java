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
public class SignatureProcessor extends AbstractAssertionProcessor {

    /** The Constant SIGNATURE_QNAME. */
    public static final QName SIGNATURE_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Signature", "sopa");

    /** The Constant MESSAGE_ATTRIBUTE. */
    public static final QName MESSAGE_ATTRIBUTE = new QName("message");

    /** The Constant VERIFY_ATTRIBUTE. */
    public static final QName VERIFY_ATTRIBUTE = new QName("verify");

    /** indexes into result matrix. */
    private static final int NONE = 0;

    /** The Constant OPTIONAL. */
    private static final int OPTIONAL = 1;

    /** The Constant MANDATORY. */
    private static final int MANDATORY = 2;

    /** values in result matrix. */
    private static final int NO_MATCH = -1;

    /** The Constant NO_SIGNATURE. */
    private static final int NO_SIGNATURE = 0;

    /** The Constant SIGNATURE. */
    private static final int SIGNATURE = 1;

    /** The Constant VERIFY. */
    private static final int VERIFY = 2;

    /** Matching results indexed by [senderIndex][receiverIndex]. */
    private static final int[][] RESULTS =
            { {NO_SIGNATURE, NO_SIGNATURE, NO_MATCH}, {NO_SIGNATURE, NO_SIGNATURE, VERIFY}, {SIGNATURE, VERIFY, VERIFY}};

    /**
     * Instantiates a new signature processor.
     */
    public SignatureProcessor() {

    }

    /**
     * Gets the cost.
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
        String verify = assertion.getAttribute(VERIFY_ATTRIBUTE);
        if ("true".equals(verify)) {
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
        return SIGNATURE_QNAME;
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
        Selector selector = new PrimitiveAssertionSelector(SIGNATURE_QNAME);
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
     * @param verify
     *        the verify
     * @param compositor
     *        the compositor
     */
    private void addAssertion(final String message, final boolean verify, final All compositor) {
        PrimitiveAssertion assertion = new PrimitiveAssertion(SIGNATURE_QNAME);
        assertion.addAttribute(MESSAGE_ATTRIBUTE, message);
        assertion.addAttribute(VERIFY_ATTRIBUTE, Boolean.toString(verify));
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
            case NO_SIGNATURE:
                ret = true;
                break;
            case SIGNATURE:
                this.addAssertion(message, false, compositor);
                ret = true;
                break;
            case VERIFY:
                this.addAssertion(message, true, compositor);
                ret = true;
                break;
            case NO_MATCH:
                ret = false;
                break;
            default:
                throw new UnexpectedPolicyProcessingException("Unexpected state " + result + " for " + message);
        }
        return ret;
    }

    /**
     * Match assertions for verification of agreed policy versus provider policy.
     * 
     * @param message
     *        the message
     * @param agreedAssertion
     *        the agreed assertion
     * @param participantAssertion
     *        the participant assertion
     * @param compositor
     *        the compositor
     * 
     * @return true, if match assertions runtime
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private boolean matchAssertionsRuntime(final String message, final PrimitiveAssertion agreedAssertion,
            final PrimitiveAssertion participantAssertion, final All compositor) throws UnexpectedPolicyProcessingException {
        String verify = null;
        if (null != agreedAssertion) {
            verify = agreedAssertion.getAttribute(VERIFY_ATTRIBUTE);
        }
        if (null == participantAssertion) {
            if ("true".equals(verify)) // agreed policy specifies verification, but receiver does
                // not
                // support it
                // -> possible DoS for the receiver
                return false;
        } else {
            String optional = participantAssertion.getAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE);
            if (!"true".equals(optional)) {
                if (null != agreedAssertion) {
                    if (!"true".equals(verify)) // receiver requires verification, but agreed policy
                        // does not specify it
                        return false;
                } else
                    // receiver requires verification, but agreed policy
                    // contains no signature assertion
                    return false;
            }
        }
        return true;
    }

}
