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
package org.eclipse.swordfish.core.interceptor.authentication.impl;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.security.auth.Subject;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.w3c.dom.Node;

/**
 * The Class ProviderAuthenticationStrategy.
 */
public class ProviderAuthenticationStrategy {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(ProviderAuthenticationStrategy.class);

    /** The auth bean. */
    private AuthenticationProcessorBean authBean = null;

    /**
     * constructor.
     * 
     * @param authBean
     *        the auth bean
     * 
     * @throws Exception
     */
    ProviderAuthenticationStrategy(final AuthenticationProcessorBean authBean) throws Exception {
        this.authBean = authBean;
    }

    /**
     * (non-Javadoc).
     * 
     * @param nmMessage
     *        the nm message
     * @param context
     *        the context
     * @param role
     *        the role
     * @param agreedAuthType
     *        the agreed auth type
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handle(final NormalizedMessage nmMessage, final MessageExchange context, final Role role,
            final String agreedAuthType) throws Exception {

        Subject authenticatedSubject = null;

        // 1. try to get a SAML assertion from the request
        // if one is found and SAML is one of the agreed authentication types,
        // use
        // it to authenticate the subject and finish
        String authAssertions = this.authBean.getAuthAssertionFromExchange(nmMessage);
        if ((null != authAssertions) && this.authBean.getSamlToken().equals(agreedAuthType)) {
            LOG.info("Using SAMLToken to authenticate subject.");
            LOG.debug("Received assertions are: " + authAssertions);
            authenticatedSubject =
                    this.authBean.authenticate(this.authBean.getAssertionLoginType(), new AssertionCallbackHandler(authAssertions));
            this.authBean.setAuthenticatedEnviornment(nmMessage, authenticatedSubject);
            return;
        }
        // 2. try to get a UsernameToken from the request
        // if one is found and UsernameToken is one of the agreed authentication
        // types, use
        // it to authenticate the subject and finish
        Node tokenNode = this.getUsernameTokenFromExchange(nmMessage);
        if ((null != tokenNode) && this.authBean.getUserNameToken().equals(agreedAuthType)) {
            LOG.info("usernametoken found in the message exchange.");
            UsernameTokenProfileParser tokenParser = new UsernameTokenProfileParser();
            tokenParser.parse(tokenNode);
            String username = tokenParser.getUsername();
            String password = tokenParser.getPassword();
            if ((username == null) || username.trim().equals("")) {
                LOG.warn("Username not available in the UsernameTokenProfile.");
            }
            if ((password == null) || password.trim().equals("")) {
                LOG.warn("Password not available in the UsernameTokenProfile.");
            }
            authenticatedSubject =
                    this.authBean.authenticate(this.authBean.getCredentialLoginType(), new UserCallbackHandler(username, password));
            this.authBean.setAuthenticatedEnviornment(nmMessage, authenticatedSubject);
            return;
        }
        // if we get here, no authentication token matching the agreed
        // authentication types
        // was found
        throw new InternalAuthenticationException(
                "No authentication token matching one of the agreed authentication types was found.");
    }

    /**
     * Gets the username token from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @return Node tokenProfile
     * 
     * @throws Exception
     *         exception
     */
    private Node getUsernameTokenFromExchange(final NormalizedMessage me) throws Exception {
        return this.authBean.getExchangeHelper().getUsernameTokenFromExchange(me);

    }

}
