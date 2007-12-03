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

import java.util.Iterator;
import java.util.List;
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
public class SdxValidationProcessor extends AbstractAssertionProcessor {

    /** The Constant SDX_VALIDATION_QNAME. */
    public static final QName SDX_VALIDATION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "SDXValidation", "sopa");

    /** The Constant MESSAGE_ATTRIBUTE. */
    public static final QName MESSAGE_ATTRIBUTE = new QName("message");

    /** The Constant LOCATION_ATTRIBUTE. */
    public static final QName LOCATION_ATTRIBUTE = new QName("location");

    /** indexes into result matrix. */
    private static final int NONE = 0;

    /** The Constant OPTIONAL. */
    private static final int OPTIONAL = 1;

    /** The Constant OPTIONAL_SENDER. */
    private static final int OPTIONAL_SENDER = 2;

    /** The Constant OPTIONAL_RECEIVER. */
    private static final int OPTIONAL_RECEIVER = 3;

    /** The Constant MANDATORY. */
    private static final int MANDATORY = 4;

    /** The Constant MANDATORY_SENDER. */
    private static final int MANDATORY_SENDER = 5;

    /** The Constant MANDATORY_RECEIVER. */
    private static final int MANDATORY_RECEIVER = 6;

    /** values in result matrix. */
    private static final int NO_MATCH = -1;

    /** The Constant NO_VLDTN. */
    private static final int NO_VLDTN = 0; // no validation, looks ugly, but

    // improves formatting of result
    // matrix

    /** The Constant VAL_SEND. */
    private static final int VAL_SEND = 1; // validation on sender

    /** The Constant VAL_RECV. */
    private static final int VAL_RECV = 2; // validation on receiver

    /** The Constant VAL_BOTH. */
    private static final int VAL_BOTH = 3; // validation on both sides

    /** Matching results indexed by [senderIndex][receiverIndex]. */
    private static final int[][] RESULTS =
            { {NO_VLDTN, NO_VLDTN, NO_VLDTN, NO_VLDTN, VAL_SEND, VAL_SEND, VAL_RECV},
                    {NO_VLDTN, NO_VLDTN, NO_VLDTN, NO_VLDTN, VAL_SEND, VAL_SEND, VAL_RECV},
                    {NO_VLDTN, NO_VLDTN, NO_VLDTN, NO_VLDTN, VAL_SEND, VAL_SEND, VAL_RECV},
                    {NO_VLDTN, NO_VLDTN, NO_VLDTN, NO_VLDTN, VAL_RECV, NO_MATCH, VAL_RECV},
                    {VAL_SEND, VAL_SEND, VAL_SEND, VAL_SEND, VAL_SEND, VAL_SEND, VAL_RECV},
                    {VAL_SEND, VAL_SEND, VAL_SEND, VAL_SEND, VAL_SEND, VAL_SEND, VAL_BOTH},
                    {VAL_RECV, VAL_RECV, NO_MATCH, VAL_RECV, VAL_RECV, VAL_BOTH, VAL_RECV}};

    /**
     * Instantiates a new sdx validation processor.
     */
    public SdxValidationProcessor() {

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
        int ret = 10;
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.util.AbstractAssertionProcessor#getSupportedAssertion()
     */
    @Override
    public QName getSupportedAssertion() {
        return SDX_VALIDATION_QNAME;
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
        String mode = this.getMode((List) preProcessResultConsumer, (List) preProcessResultProvider);
        if ("runtime".equals(mode))
            return this
                .postProcessAlternativeRuntime(alternative, (List) preProcessResultConsumer, (List) preProcessResultProvider);
        else
            return this.postProcessAlternativeLookup(alternative, (List) preProcessResultConsumer, (List) preProcessResultProvider);
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
        Selector selector = new PrimitiveAssertionSelector(SDX_VALIDATION_QNAME);
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
    PrimitiveAssertion getMessageSpecification(final String message, final List list) {
        PrimitiveAssertion ret = null;
        if (null != list) {
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                PrimitiveAssertion assertion = (PrimitiveAssertion) iter.next();
                String assertionMessage = assertion.getAttribute(MESSAGE_ATTRIBUTE);
                if (null == assertionMessage) {
                    assertionMessage = "all";
                }
                if (("all".equals(assertionMessage)) || (message.equals(assertionMessage))) {
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
     * @param location
     *        the location
     * @param compositor
     *        the compositor
     */
    private void addAssertion(final String message, final String location, final All compositor) {
        PrimitiveAssertion assertion = new PrimitiveAssertion(SDX_VALIDATION_QNAME);
        assertion.addAttribute(MESSAGE_ATTRIBUTE, message);
        assertion.addAttribute(LOCATION_ATTRIBUTE, location);
        compositor.addTerm(assertion);
    }

    /**
     * Check assertion runtime.
     * 
     * @param assertion
     *        the assertion
     * @param agreed
     *        the agreed
     * 
     * @return true, if successful
     */
    private boolean checkAssertionRuntime(final PrimitiveAssertion assertion, final List agreed) {
        boolean ret = true;
        String optional = assertion.getAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE);
        if (!"true".equals(optional)) {
            boolean found = false;
            if (null != agreed) {
                String participantLocation = assertion.getAttribute(LOCATION_ATTRIBUTE);
                if (null == participantLocation) {
                    // this only works when called for requests on the provider
                    // side
                    // TODO: rework if use of validation is exended to the
                    // consumer side
                    participantLocation = "consumer";
                }
                for (Iterator iter = agreed.iterator(); iter.hasNext();) {
                    PrimitiveAssertion agreedAssertion = (PrimitiveAssertion) iter.next();
                    String agreedLocation = agreedAssertion.getAttribute(LOCATION_ATTRIBUTE);
                    if (participantLocation.equals(agreedLocation)) {
                        found = true;
                        break;
                    }
                }
            }
            ret = found;
        }
        return ret;
    }

    /**
     * Compute location.
     * 
     * @param message
     *        the message
     * @param participant
     *        the participant
     * 
     * @return the string
     */
    private String computeLocation(final String message, final String participant) {
        if ("request".equals(message)) {
            if ("sender".equals(participant))
                return "consumer";
            else
                return "provider";
        } else { // response
            if ("receiver".equals(participant))
                return "consumer";
            else
                return "provider";

        }
    }

    /**
     * Gets the index.
     * 
     * @param assertion
     *        the assertion
     * @param message
     *        the message
     * 
     * @return the index
     */
    private int getIndex(final PrimitiveAssertion assertion, final String message) {
        int ret = -1;
        if (null == assertion) {
            ret = NONE;
        } else {
            String optional = assertion.getAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE);
            String location = assertion.getAttribute(LOCATION_ATTRIBUTE);

            if ("request".equals(message)) {
                if ("true".equals(optional)) {
                    if ("consumer".equals(location)) {
                        ret = OPTIONAL_SENDER;
                    } else if ("provider".equals(location)) {
                        ret = OPTIONAL_RECEIVER;
                    } else {
                        ret = OPTIONAL;
                    }
                } else if (("false".equals(optional)) || (null == optional)) {
                    if ("consumer".equals(location)) {
                        ret = MANDATORY_SENDER;
                    } else if ("provider".equals(location)) {
                        ret = MANDATORY_RECEIVER;
                    } else {
                        ret = MANDATORY;
                    }
                }
            } else if ("response".equals(message)) {
                if ("true".equals(optional)) {
                    if ("provider".equals(location)) {
                        ret = OPTIONAL_SENDER;
                    } else if ("consumer".equals(location)) {
                        ret = OPTIONAL_RECEIVER;
                    } else {
                        ret = OPTIONAL;
                    }
                } else if (("false".equals(optional)) || (null == optional)) {
                    if ("provider".equals(location)) {
                        ret = MANDATORY_SENDER;
                    } else if ("consumer".equals(location)) {
                        ret = MANDATORY_RECEIVER;
                    } else {
                        ret = MANDATORY;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Gets the mode.
     * 
     * @param preProcessResult
     *        the pre process result
     * 
     * @return the mode
     */
    private String getMode(final List preProcessResult) {
        String mode = null;
        if (null != preProcessResult) {
            for (Iterator iter = preProcessResult.iterator(); iter.hasNext();) {
                PrimitiveAssertion assertion = (PrimitiveAssertion) iter.next();
                mode = assertion.getAttribute(PolicyConstants.SOP_MODE_ATTRIBUTE);
                if (null != mode) {
                    break;
                }
            }
        }
        return mode;
    }

    /**
     * Gets the mode.
     * 
     * @param preProcessResultConsumer
     *        the pre process result consumer
     * @param preProcessResultProvider
     *        the pre process result provider
     * 
     * @return the mode
     */
    private String getMode(final List preProcessResultConsumer, final List preProcessResultProvider) {
        String mode = this.getMode(preProcessResultProvider);
        if (null == mode) {
            mode = this.getMode(preProcessResultConsumer);
        }
        return mode;
    }

    /**
     * Match assertions lookup.
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
    private boolean matchAssertionsLookup(final String message, final PrimitiveAssertion senderAssertion,
            final PrimitiveAssertion receiverAssertion, final All compositor) throws UnexpectedPolicyProcessingException {
        int senderIndex = this.getIndex(senderAssertion, message);
        int receiverIndex = this.getIndex(receiverAssertion, message);
        int result = RESULTS[senderIndex][receiverIndex];
        boolean ret = false;
        String location;
        switch (result) {
            case NO_VLDTN:
                ret = true;
                break;
            case VAL_SEND:
                location = this.computeLocation(message, "sender");
                this.addAssertion(message, location, compositor);
                ret = true;
                break;
            case VAL_RECV:
                location = this.computeLocation(message, "receiver");
                this.addAssertion(message, location, compositor);
                ret = true;
                break;
            case VAL_BOTH:
                this.addAssertion(message, "consumer", compositor);
                this.addAssertion(message, "provider", compositor);
                ret = true;
                break;
            case NO_MATCH:
                ret = false;
                break;
            default:
                throw new UnexpectedPolicyProcessingException("Unexpected state " + result + " for message");
        }
        return ret;
    }

    /**
     * Do matching for SdxValidation at lookup time -> as described in Policy Guide.
     * 
     * @param alternative
     *        the alternative
     * @param preProcessResultConsumer
     *        the pre process result consumer
     * @param preProcessResultProvider
     *        the pre process result provider
     * 
     * @return true, if post process alternative lookup
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private boolean postProcessAlternativeLookup(final All alternative, final List preProcessResultConsumer,
            final List preProcessResultProvider) throws UnexpectedPolicyProcessingException {
        PrimitiveAssertion consumerAssertion = this.getMessageSpecification("request", preProcessResultConsumer);
        PrimitiveAssertion providerAssertion = this.getMessageSpecification("request", preProcessResultProvider);
        boolean ret = this.matchAssertionsLookup("request", consumerAssertion, providerAssertion, alternative);
        if (ret) {
            consumerAssertion = this.getMessageSpecification("response", preProcessResultConsumer);
            providerAssertion = this.getMessageSpecification("response", preProcessResultProvider);
            ret = this.matchAssertionsLookup("response", providerAssertion, consumerAssertion, alternative);
        }
        return ret;
    }

    /**
     * Do matching to verify that the content of the agreed policy satisfies the participant policy.
     * 
     * @param alternative
     *        the alternative
     * @param preProcessResultAgreed
     *        the pre process result agreed
     * @param preProcessResultParticipant
     *        the pre process result participant
     * 
     * @return true, if post process alternative runtime
     */
    private boolean postProcessAlternativeRuntime(final All alternative, final List preProcessResultAgreed,
            final List preProcessResultParticipant) {
        boolean ret = true;
        if (null != preProcessResultParticipant) {
            for (Iterator iter = preProcessResultParticipant.iterator(); iter.hasNext();) {
                PrimitiveAssertion assertion = (PrimitiveAssertion) iter.next();
                ret = this.checkAssertionRuntime(assertion, preProcessResultAgreed);
                if (!ret) {
                    break;
                }
            }
        }
        return ret;
    }

}
