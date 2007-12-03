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

import java.security.AccessController;
import java.security.Principal;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.naming.AuthenticationException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.authentication.GenericCallbackHandler;
import org.eclipse.swordfish.core.utils.ExchangeProperties;

/**
 * The Class ConsumerAuthenticationStrategy.
 * 
 */
public class ConsumerAuthenticationStrategy {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(ConsumerAuthenticationStrategy.class);

    /** auth bean. */
    private AuthenticationProcessorBean authBean = null;

    /**
     * constructor.
     * 
     * @param authBean
     *        authBean
     */
    public ConsumerAuthenticationStrategy(final AuthenticationProcessorBean authBean) {
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
     *         on error.
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent
     *      #handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handle(final NormalizedMessage nmMessage, final MessageExchange context, final Role role,
            final String agreedAuthType) throws Exception {

        CallbackHandler receivedCallbackHandler = null;
        Subject authenticatedSubject = null;
        String authAssertions = null;

        try {

            // first check for the authentication callback handler
            // get the authentication callback.
            receivedCallbackHandler = this.searchCallback(context);

            // 1. if SAMLToken is one of the agreed authentication types, try
            // this one first
            if (this.authBean.getSamlToken().equals(agreedAuthType)) {

                // 1a. if callback handlers are available in the message
                // exchange, use them
                if (null != receivedCallbackHandler) {
                    LOG.info("Attempt to authenticate using callback handlers.");
                    // if we already have a cache entry for this handler, use
                    // the Subject from the cache
                    if (receivedCallbackHandler instanceof GenericCallbackHandler) {
                        authenticatedSubject = this.authBean.lookupInCache((GenericCallbackHandler) receivedCallbackHandler);
                    }

                    if (authenticatedSubject != null) {
                        // check whether the cache assertions are expired or
                        // still valid.
                        if (this.authBean.isAssertionInSubjectExpired(authenticatedSubject)) {
                            // if the assertions are expired, do login with saml
                            // assertions
                            String cacheAssertions = this.authBean.getAssertionFromSubject(authenticatedSubject);
                            LOG.info("Attempt to authenticate using expired assertions present in the cache.");
                            LOG.debug("Expired assertions received from the cache are " + cacheAssertions);
                            authenticatedSubject =
                                    this.authBean.authenticate(this.authBean.getAssertionLoginType(), new AssertionCallbackHandler(
                                            cacheAssertions));
                        }
                    } else {
                        // otherwise do an actual login
                        authenticatedSubject =
                                this.authBean.authenticate(this.authBean.getCredentialLoginType(), receivedCallbackHandler);
                    }

                    this.authBean.setAuthenticatedEnviornment(nmMessage, authenticatedSubject);
                    return;
                }
                // 1b. check whether assertions are already present in the
                // request
                // if yes perform authentication using the assertions
                authAssertions = this.authBean.getAuthAssertionFromExchange(nmMessage);
                if (authAssertions != null) {
                    LOG.info("Attempt to authenticate using assertions present in the message exchange.");
                    LOG.debug("Assertions received from the message exchange is " + authAssertions);
                    authenticatedSubject =
                            this.authBean.authenticate(this.authBean.getAssertionLoginType(), new AssertionCallbackHandler(
                                    authAssertions));
                    this.authBean.setAuthenticatedEnviornment(nmMessage, authenticatedSubject);
                    return;
                }
                // 1c. check whether a subject is already present in the current
                // thread, if yes authenticate using that subject.
                Subject curThreadSubject = Subject.getSubject(AccessController.getContext());
                if (curThreadSubject != null) {
                    LOG.info("Attempt to authenticate using subject in the current thread.");
                    authAssertions = this.authBean.getAssertionFromSubject(curThreadSubject);
                    if (authAssertions != null) {
                        LOG.debug("Assertions received from the current thread subject is " + authAssertions);
                        authenticatedSubject =
                                this.authBean.authenticate(this.authBean.getAssertionLoginType(), new AssertionCallbackHandler(
                                        authAssertions));
                        this.authBean.setAuthenticatedEnviornment(nmMessage, authenticatedSubject);
                        return;
                    }
                }

                // (special case). There can be a case where we want to use SSO.
                // In SSO we try to login without any authentication handlers.
                if (null == receivedCallbackHandler) {
                    LOG.warn("No Authentication handlers found, trying to authenticate without the handlers.");
                    authenticatedSubject = this.authBean.authenticate(this.authBean.getCredentialLoginType(), null);
                    this.authBean.setAuthenticatedEnviornment(nmMessage, authenticatedSubject);
                    return;
                }

            }

            // 2. now try UsernameToken, if this is among the agreed
            // authentication types
            if (this.authBean.getUserNameToken().equals(agreedAuthType)) {
                LOG.info("Using UsernameTokenProfile authentication.");
                if (null == receivedCallbackHandler) throw new AuthenticationException("No callback handler registered.");
                Subject tokenSubject = new Subject();
                NameCallback nameCallback = new NameCallback("Username:");
                PasswordCallback passwordCallback = new PasswordCallback("Password:", false);
                Callback[] callbacks = new Callback[] {nameCallback, passwordCallback};
                receivedCallbackHandler.handle(callbacks);
                String tokenUser = nameCallback.getName();
                char[] chartokenPassword = passwordCallback.getPassword();
                if ((tokenUser == null) || (tokenUser.trim().length() == 0))
                    throw new AuthenticationException("Cannot retrieve username from the callback handlers.");
                if (chartokenPassword == null)
                    throw new AuthenticationException("Cannot retrieve password from the callback handlers.");
                tokenSubject.getPrincipals().add(new TokenPrincipal(tokenUser));
                String token = this.authBean.generateUsernameToken(tokenUser, new String(chartokenPassword));
                this.authBean.setUsernameTokenInExchange(nmMessage, token, tokenSubject);
                return;
            }
        } catch (Exception ace) {
            LOG.error("Exception in authentication", ace);
            throw ace;
        }

    }

    /**
     * Method for getting credential type from the message exchange.
     * 
     * @param messageExchange
     *        the message exchange
     * 
     * @return String the credential type
     */
    private CallbackHandler searchCallback(final MessageExchange messageExchange) {
        CallbackHandler handler = null;

        handler = (CallbackHandler) messageExchange.getProperty(ExchangeProperties.MESSAGE_AUTH_CALLBACKS);
        if (handler != null) {
            LOG.info("Authentication callback handler for message found.");
            return handler;
        }
        handler = (CallbackHandler) messageExchange.getProperty(ExchangeProperties.OPERATION_AUTH_CALLBACKS);
        if (handler != null) {
            LOG.info("Authentication callback handler for operation found.");
            return handler;
        }
        handler = (CallbackHandler) messageExchange.getProperty(ExchangeProperties.SERVICE_AUTH_CALLBACKS);
        if (handler != null) {
            LOG.info("Authentication callback handler for service found.");
            return handler;
        }
        LOG.info("Searching for authentication callback handler set for InternalSBB.");
        handler = (CallbackHandler) messageExchange.getProperty(ExchangeProperties.SBB_AUTH_CALLBACKS);

        return handler;
    }

    /**
     * The Class tokenPrincipal.
     * 
     */
    private class TokenPrincipal implements Principal {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -4070566189071297193L;

        /** the username. */
        private String username = null;

        /**
         * The Constructor.
         * 
         * @param username
         *        the username
         */
        public TokenPrincipal(final String username) {
            this.username = username;
        }

        /**
         * @return the name
         * 
         * @see java.security.Principal#getName()
         */
        public String getName() {
            return this.username;
        }

    }

}
