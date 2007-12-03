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
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.PolicyConstants;
import org.eclipse.swordfish.policy.Role;
import org.eclipse.swordfish.policy.selector.Selector;

/**
 * Process transport assertions
 * 
 * Include assertion if specified by provider. Set location attribute to "both" if consumer
 * specifies matching assertion, "provider" otherwise
 * 
 */
public class AuthenticationProcessor extends AbstractAssertionProcessor {

    /** The Constant MESSAGE_VALUE_REQUEST. */
    public static final String MESSAGE_VALUE_REQUEST = "request";

    /** The Constant MESSAGE_VALUE_RESPONSE. */
    public static final String MESSAGE_VALUE_RESPONSE = "response";

    /** The Constant AUTHENTICATION_QNAME. */
    public static final QName AUTHENTICATION_QNAME = new QName(PolicyConstants.SOP_ASSERTION_URI, "Authentication");

    /** The Constant TYPE_ATTRIBUTE. */
    public static final QName TYPE_ATTRIBUTE = new QName("type");

    /** The Constant MESSAGE_ATTRIBUTE. */
    public static final QName MESSAGE_ATTRIBUTE = new QName("message");

    /**
     * Instantiates a new authentication processor.
     */
    public AuthenticationProcessor() {

    }

    /**
     * prefer alternatives without authentication - saves load prefer SAML assertions over Username
     * assertions.
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
        String type = assertion.getAttribute(TYPE_ATTRIBUTE);
        if ("SAMLToken".equals(type)) {
            ret = 10;
        } else {
            // only use UsernameToken if no way around it
            ret = 1000;
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
        return AUTHENTICATION_QNAME;
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
        List consumerList = this.expandList((List) preProcessResultConsumer, Role.CONSUMER);
        List providerList = this.expandList((List) preProcessResultProvider, Role.PROVIDER);
        boolean ret =
                this.check(alternative, providerList, consumerList, MESSAGE_VALUE_RESPONSE)
                        && this.check(alternative, consumerList, providerList, MESSAGE_VALUE_REQUEST);
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
        Selector selector = new PrimitiveAssertionSelector(AUTHENTICATION_QNAME);
        TermIterator termIt = new TermIterator(alternative, selector);
        Object ret = this.extractAssertions(alternative, termIt);
        return ret;
    }

    /**
     * Expand list.
     * 
     * @param source
     *        the source
     * @param role
     *        the role
     * 
     * @return the list
     */
    protected List expandList(final List source, final Role role) {
        List ret = null;
        if (null != source) {
            ret = new ArrayList();
            for (Iterator iter = source.iterator(); iter.hasNext();) {
                PrimitiveAssertion old = (PrimitiveAssertion) iter.next();
                String optional = old.getAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE);
                String message = old.getAttribute(MESSAGE_ATTRIBUTE);
                String type = old.getAttribute(TYPE_ATTRIBUTE);
                if (MESSAGE_VALUE_REQUEST.equals(message) || MESSAGE_VALUE_RESPONSE.equals(message)) {
                    ret.add(old);
                } else {
                    // split assertion in mandatory and optional part according
                    // to role
                    String mandatoryMessage = null;
                    String optionalMessage = null;
                    if (Role.CONSUMER.equals(role)) {
                        mandatoryMessage = MESSAGE_VALUE_RESPONSE;
                        optionalMessage = MESSAGE_VALUE_REQUEST;
                    } else {
                        mandatoryMessage = MESSAGE_VALUE_REQUEST;
                        optionalMessage = MESSAGE_VALUE_RESPONSE;
                    }
                    PrimitiveAssertion auth = new PrimitiveAssertion(AUTHENTICATION_QNAME);
                    auth.addAttribute(TYPE_ATTRIBUTE, type);
                    auth.addAttribute(MESSAGE_ATTRIBUTE, optionalMessage);
                    auth.addAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE, "true");
                    ret.add(auth);
                    if (!"true".equals(optional)) {
                        auth = new PrimitiveAssertion(AUTHENTICATION_QNAME);
                        auth.addAttribute(TYPE_ATTRIBUTE, type);
                        auth.addAttribute(MESSAGE_ATTRIBUTE, mandatoryMessage);
                        ret.add(auth);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Check.
     * 
     * @param alternative
     *        the alternative
     * @param senderList
     *        the sender list
     * @param receiverList
     *        the receiver list
     * @param message
     *        the message
     * 
     * @return true, if successful
     */
    private boolean check(final All alternative, final List senderList, final List receiverList, final String message) {
        if (null == receiverList) return true;
        for (Iterator iter = receiverList.iterator(); iter.hasNext();) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iter.next();
            String optional = assertion.getAttribute(PolicyConstants.SOP_OPTIONAL_ATTRIBUTE);
            String assertionMessage = assertion.getAttribute(MESSAGE_ATTRIBUTE);
            if (!message.equals(assertionMessage)) {
                continue;
            }
            if (!"true".equals(optional)) {
                String type = assertion.getAttribute(TYPE_ATTRIBUTE);
                if (this.isSupported(senderList, type, assertionMessage)) {
                    PrimitiveAssertion auth = new PrimitiveAssertion(AUTHENTICATION_QNAME);
                    auth.addAttribute(MESSAGE_ATTRIBUTE, message);
                    auth.addAttribute(TYPE_ATTRIBUTE, type);
                    alternative.addTerm(auth);
                } else
                    return false;
            }
        }
        return true;
    }

    /**
     * Checks if is supported.
     * 
     * @param assertions
     *        the assertions
     * @param type
     *        the type
     * @param message
     *        the message
     * 
     * @return true, if is supported
     */
    private boolean isSupported(final List assertions, final String type, final String message) {
        if (null == assertions) return false;
        for (Iterator iter = assertions.iterator(); iter.hasNext();) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iter.next();
            String supportedType = assertion.getAttribute(TYPE_ATTRIBUTE);
            String targetMessage = assertion.getAttribute(MESSAGE_ATTRIBUTE);
            if (type.equals(supportedType) && message.equals(targetMessage)) return true;
        }
        return false;
    }

}
