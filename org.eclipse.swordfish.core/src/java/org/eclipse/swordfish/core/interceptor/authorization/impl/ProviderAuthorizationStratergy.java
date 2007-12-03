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
package org.eclipse.swordfish.core.interceptor.authorization.impl;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Iterator;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAuthorizationDecisionStatement;
import com.opensymphony.oscache.base.NeedsRefreshException;

/**
 * The Class ProviderAuthorizationStratergy.
 */
public class ProviderAuthorizationStratergy {

    /** logger for this class. */
    private static Log log = SBBLogFactory.getLog(ProviderAuthorizationStratergy.class);

    /** azBean. */
    private AuthorizationProcessorBean azBean = null;

    /**
     * The Constructor.
     * 
     * @param azBean
     *        azBean
     */
    public ProviderAuthorizationStratergy(final AuthorizationProcessorBean azBean) {
        this.azBean = azBean;
    }

    /**
     * Handle.
     * 
     * @param message
     *        message
     * @param role
     *        role
     * @param assertions
     *        the assertions
     * 
     * @throws Exception
     *         exception
     */
    public void handle(final NormalizedMessage message, final Role role, final Collection/* <Assertion> */assertions)
            throws Exception {
        String resource = null;
        String principal = null;
        SAMLAssertion samlAssertion = null;
        SAMLAssertion cacheSamlAssertion = null;

        try {
            resource = this.azBean.getResourceName();
            principal = this.azBean.getExchangeHelper().getPrincipalNameFromSubject(message.getSecuritySubject());
            if (principal == null)
                throw new PolicyViolatedException(
                        "Authenticated Principal unavailable. Authentication is a prerequisite for Authorization policy processing. ");
            String keyForCache = principal + resource + AuthorizationProcessorBean.getResourceAction();
            String strAssertions = this.azBean.getExchangeHelper().getAzAssertionFromExchange(message);

            if (strAssertions != null) {
                ClassLoader bootClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(ProviderAuthorizationStratergy.class.getClassLoader());
                    samlAssertion = new SAMLAssertion(new ByteArrayInputStream(strAssertions.getBytes()));
                } finally {
                    Thread.currentThread().setContextClassLoader(bootClassLoader);
                }
                // check whether the authorized and authenticated users are same
                String assertionPrincipal = null;
                Iterator iterator = samlAssertion.getStatements();

                while (iterator.hasNext()) {
                    Object obj = iterator.next();
                    if (obj instanceof SAMLAuthorizationDecisionStatement) {
                        assertionPrincipal = ((SAMLAuthorizationDecisionStatement) obj).getSubject().getName().getName();
                    }
                }

                if (assertionPrincipal.startsWith("uid=")) {
                    assertionPrincipal = assertionPrincipal.substring(assertionPrincipal.indexOf("=") + 1);
                }
                if (!principal.equals(assertionPrincipal))
                    throw new InternalConfigurationException("The authenticated and authorized principals are different.");

                // update the cache key with assertionid

                String assertionID = samlAssertion.getId();
                keyForCache = keyForCache + assertionID;
            }

            try {
                cacheSamlAssertion = (SAMLAssertion) this.azBean.getAssertionCache().getFromCache(keyForCache);
            } catch (NeedsRefreshException nre) {
                this.azBean.getAssertionCache().cancelUpdate(keyForCache);
            }

            if (cacheSamlAssertion != null) {
                log.info("Found Az Assertions in Cache.");
                this.azBean.setAuthorizedEnviornment(message, cacheSamlAssertion);
                return;
            }

            if (strAssertions == null) {
                // if assertions are not received from caller, authorize now
                log.info("Didnot receive Az assertions from caller.");

                samlAssertion = this.azBean.authorize(principal, resource, AuthorizationProcessorBean.getResourceAction());

                if (samlAssertion == null)
                    throw new InternalInfrastructureException("Authorization assertions received are null.");

                if (this.azBean.areAssertionsValid(samlAssertion, resource, this.azBean.isClientVerification(), this.azBean
                    .isClientExpiry())) {
                    this.azBean.setAuthorizedEnviornmentAndCache(message, samlAssertion, keyForCache);
                    return;
                }
            }

            this.azBean.checkDecision(samlAssertion, resource);

            this.azBean.checkSignature(samlAssertion);

            if (this.azBean.isExpired(samlAssertion)) {
                // if the assertions are expired, authorize again and get new
                // assertions
                samlAssertion = this.azBean.authorize(principal, resource, AuthorizationProcessorBean.getResourceAction());

                if (samlAssertion == null)
                    throw new InternalInfrastructureException("Authorization assertions received are null.");

                if (this.azBean.areAssertionsValid(samlAssertion, resource, this.azBean.isClientVerification(), this.azBean
                    .isClientExpiry())) {
                    this.azBean.setAuthorizedEnviornmentAndCache(message, samlAssertion, keyForCache);
                    return;
                }
            } else {
                this.azBean.setAuthorizedEnviornmentAndCache(message, samlAssertion, keyForCache);
                return;
            }

        } catch (InternalAuthorizationException ae) {
            throw ae;
        } catch (InternalSBBException se) {
            throw se;
        } catch (Exception e) {
            log.error("Exception in authorization", e);
            throw new InternalInfrastructureException(e);
        }
    }

}
