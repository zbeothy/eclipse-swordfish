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

import java.util.Collection;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.opensaml.SAMLAssertion;
import com.opensymphony.oscache.base.NeedsRefreshException;

/**
 * The Class ConsumerAuthorizationStratergy.
 */
public class ConsumerAuthorizationStratergy {

    /** logger for this class. */
    private static Log log = SBBLogFactory.getLog(ConsumerAuthorizationStratergy.class);

    /** az bean. */
    private AuthorizationProcessorBean azBean = null;

    /**
     * The Constructor.
     * 
     * @param azBean
     *        azbean
     */
    public ConsumerAuthorizationStratergy(final AuthorizationProcessorBean azBean) {
        this.azBean = azBean;
    }

    /**
     * Handle.
     * 
     * @param message
     *        message exchange
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

        try {
            resource = this.azBean.getResourceName();
            principal = this.azBean.getExchangeHelper().getPrincipalNameFromSubject(message.getSecuritySubject());
            if (principal == null)
                throw new PolicyViolatedException(
                        "Authenticated Principal unavailable. Authentication is a prerequisite for Authorization policy processing. ");
            String keyForCache = principal + resource + AuthorizationProcessorBean.getResourceAction();

            try {
                Object obj = this.azBean.getAssertionCache().getFromCache(keyForCache);
                if (obj != null) {
                    log.info("Found Az assertions in cache.");
                    samlAssertion = (SAMLAssertion) obj;
                    this.azBean.setAuthorizedEnviornment(message, samlAssertion);
                    return;
                }
            } catch (NeedsRefreshException nre) {
                log.info("Could not find Az assertions in cache.");
                this.azBean.getAssertionCache().cancelUpdate(keyForCache);
            }

            samlAssertion = this.azBean.authorize(principal, resource, AuthorizationProcessorBean.getResourceAction());

            if (samlAssertion == null) throw new InternalAuthorizationException("Authorization assertions received are null.");
            if (this.azBean.areAssertionsValid(samlAssertion, resource, this.azBean.isClientVerification(), this.azBean
                .isClientExpiry())) {
                this.azBean.setAuthorizedEnviornmentAndCache(message, samlAssertion, keyForCache);
            }

        } catch (InternalAuthorizationException ae) {
            throw ae;
        } catch (InternalSBBException se) {
            throw se;
        } catch (Exception e) {
            log.error("Exception in Authorization", e);
            throw new InternalInfrastructureException(e);
        }

    }

}
