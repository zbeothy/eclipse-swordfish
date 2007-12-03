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
 * Process compression assertions
 * 
 * Special handling of optional assertions is necessary to prevent assertion in intersection result
 * if both participants specify optional="true".
 * 
 */
public class CompressionProcessor extends AbstractAssertionProcessor {

    /** The Constant COMPRESSION_QNAME. */
    public static final QName COMPRESSION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Compression", "sopa");

    /** The Constant MESSAGE_ATTRIBUTE. */
    public static final QName MESSAGE_ATTRIBUTE = new QName("message");

    /** The Constant SIZE_ATTRIBUTE. */
    public static final QName SIZE_ATTRIBUTE = new QName("size");

    /*
     * Definition matrix
     * 
     * request response consumer provider max_size
     * 
     */
    /** The Constant DEFINITION_MAX_X. */
    private static final int DEFINITION_MAX_X = 2;

    /** The Constant DEFINITION_MAX_Y. */
    private static final int DEFINITION_MAX_Y = 3;

    /*
     * x-axis
     */
    /** The Constant REQUEST. */
    private static final int REQUEST = 0;

    /** The Constant RESPONSE. */
    private static final int RESPONSE = 1;

    /*
     * y-axis
     */
    /** The Constant CONSUMER. */
    private static final int CONSUMER = 0;

    /** The Constant PROVIDER. */
    private static final int PROVIDER = 1;

    /** The Constant SIZE. */
    private static final int SIZE = 2;

    /*
     * array to map message index back to string
     */
    /** The Constant MESSAGE. */
    private static final String[] MESSAGE = {"request", "response"};

    /*
     * Level of compression support a participant has for a message (content of customer and
     * provider column in definition matrix
     */
    /** Compression state not yet specified. */
    private static final int UNSPECIFIED = 0;

    /** Compression forbidden for this message. */
    private static final int FORBIDDEN = 1;

    /** Compression supported if other participant requires it. */
    private static final int SUPPORTED = 2;

    /** Compression required for this message. */
    private static final int REQUIRED = 3;

    /*
     * Indicator of the matching result for two assertion
     */
    /** Policies do not match. */
    private static final int NO_MATCH = 0;

    /** Policies match, no compression in result. */
    private static final int EMPTY = 1;

    /** Policies match, result contains compression. */
    private static final int INCLUDE = 2;

    /** Matrix determining matching result indexed by compression support for each participant. */
    private static final int[][] RESULT =
            { {EMPTY, EMPTY, EMPTY, INCLUDE}, {EMPTY, EMPTY, EMPTY, NO_MATCH}, {EMPTY, EMPTY, EMPTY, INCLUDE},
                    {INCLUDE, NO_MATCH, INCLUDE, INCLUDE}};

    /**
     * Instantiates a new compression processor.
     */
    public CompressionProcessor() {

    }

    /**
     * no preference either way - weighing of processor cycles versus bandwidth can't be done with
     * one size fits all.
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
        return COMPRESSION_QNAME;
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
        ArrayList consumerAssertions = (ArrayList) preProcessResultConsumer;
        ArrayList providerAssertions = (ArrayList) preProcessResultProvider;
        int[][] specificationMatrix = new int[DEFINITION_MAX_X][DEFINITION_MAX_Y];
        this.parseParticipant(specificationMatrix, CONSUMER, consumerAssertions);
        this.parseParticipant(specificationMatrix, PROVIDER, providerAssertions);
        ret = this.handleResult(specificationMatrix, REQUEST, alternative);
        if (ret) {
            ret = this.handleResult(specificationMatrix, RESPONSE, alternative);
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
        Selector selector = new PrimitiveAssertionSelector(COMPRESSION_QNAME);
        TermIterator termIt = new TermIterator(alternative, selector);
        Object ret = super.extractAssertions(alternative, termIt);
        return ret;
    }

    /**
     * Handle result.
     * 
     * @param specificationMatrix
     *        the specification matrix
     * @param message
     *        the message
     * @param alternative
     *        the alternative
     * 
     * @return true, if successful
     */
    private boolean handleResult(final int[][] specificationMatrix, final int message, final All alternative) {
        boolean ret = true;
        int result = RESULT[specificationMatrix[message][CONSUMER]][specificationMatrix[message][PROVIDER]];
        if (INCLUDE == result) {
            PrimitiveAssertion assertion = new PrimitiveAssertion(COMPRESSION_QNAME);
            assertion.addAttribute(MESSAGE_ATTRIBUTE, MESSAGE[message]);
            if (0 != specificationMatrix[message][SIZE]) {
                String size = Integer.toString(specificationMatrix[message][SIZE]);
                assertion.addAttribute(SIZE_ATTRIBUTE, size);
            }
            alternative.addTerm(assertion);
        } else if (NO_MATCH == result) {
            ret = false;
        }
        return ret;
    }

    /**
     * Parses the assertion.
     * 
     * @param specificationMatrix
     *        the specification matrix
     * @param roleIndex
     *        the role index
     * @param assertion
     *        the assertion
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private void parseAssertion(final int[][] specificationMatrix, final int roleIndex, final PrimitiveAssertion assertion)
            throws UnexpectedPolicyProcessingException {
        String message = assertion.getAttribute(MESSAGE_ATTRIBUTE);
        if (null == message) {
            message = "all";
        }
        String optional = assertion.getAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE);
        if ("true".equals(optional)) {
            // any messages that are not mentioned in the assertion will be
            // forbidden unless there is another specification
            // for them
            specificationMatrix[REQUEST][roleIndex] = Math.max(FORBIDDEN, specificationMatrix[REQUEST][roleIndex]);
            specificationMatrix[RESPONSE][roleIndex] = Math.max(FORBIDDEN, specificationMatrix[RESPONSE][roleIndex]);
            if (("request".equals(message)) || ("all".equals(message))) {
                specificationMatrix[REQUEST][roleIndex] = Math.max(SUPPORTED, specificationMatrix[REQUEST][roleIndex]);
            }
            if (("response".equals(message)) || ("all".equals(message))) {
                specificationMatrix[RESPONSE][roleIndex] = Math.max(SUPPORTED, specificationMatrix[RESPONSE][roleIndex]);
            }
        } else {
            // compression is required for the messages mentioned in the
            // assertion
            if (("request".equals(message)) || ("all".equals(message))) {
                specificationMatrix[REQUEST][roleIndex] = Math.max(REQUIRED, specificationMatrix[REQUEST][roleIndex]);
            }
            if (("response".equals(message)) || ("all".equals(message))) {
                specificationMatrix[RESPONSE][roleIndex] = Math.max(REQUIRED, specificationMatrix[RESPONSE][roleIndex]);
            }
        }
        String sizeString = assertion.getAttribute(SIZE_ATTRIBUTE);
        if (null != sizeString) {
            try {
                int size = Integer.parseInt(sizeString);
                if (("request".equals(message)) || ("all".equals(message))) {
                    specificationMatrix[REQUEST][SIZE] = Math.max(size, specificationMatrix[REQUEST][SIZE]);
                }
                if (("response".equals(message)) || ("all".equals(message))) {
                    specificationMatrix[RESPONSE][SIZE] = Math.max(size, specificationMatrix[RESPONSE][SIZE]);
                }
            } catch (NumberFormatException e) {
                throw new UnexpectedPolicyProcessingException("Illegal value " + sizeString
                        + " for size attribute in compression assertion.", e);
            }
        }
    }

    /**
     * Parses the participant.
     * 
     * @param specificationMatrix
     *        the specification matrix
     * @param roleIndex
     *        the role index
     * @param assertions
     *        the assertions
     * 
     * @throws UnexpectedPolicyProcessingException
     */
    private void parseParticipant(final int[][] specificationMatrix, final int roleIndex, final ArrayList assertions)
            throws UnexpectedPolicyProcessingException {
        specificationMatrix[REQUEST][roleIndex] = UNSPECIFIED;
        specificationMatrix[RESPONSE][roleIndex] = UNSPECIFIED;
        if (null == assertions) {
            // enter default values
            specificationMatrix[REQUEST][roleIndex] = SUPPORTED;
            specificationMatrix[RESPONSE][roleIndex] = SUPPORTED;
        } else {
            Iterator it = assertions.iterator();
            while (it.hasNext()) {
                PrimitiveAssertion assertion = (PrimitiveAssertion) it.next();
                this.parseAssertion(specificationMatrix, roleIndex, assertion);
            }
        }
    }

}
