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

import java.math.BigInteger;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.opensaml.SAMLAction;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAuthorizationDecisionStatement;
import org.opensaml.SAMLException;
import org.opensaml.SAMLNameIdentifier;
import org.opensaml.SAMLSubject;

/**
 * The Class LocalAuthorizationProcessorBean.
 */
public class LocalAuthorizationProcessorBean extends AuthorizationProcessorBean {

    /**
     * Authorize.
     * 
     * @param principal
     *        principal
     * @param resource
     *        resource
     * @param action
     *        action
     * 
     * @return SAMLAssertion assertion
     * 
     * @throws InternalSBBException
     */
    @Override
    protected SAMLAssertion authorize(final String principal, final String resource, final String action)
            throws InternalSBBException {

        try {
            SAMLAssertion samlAssertion = new SAMLAssertion();
            samlAssertion.setIssuer("Dummy");
            GregorianCalendar now = new GregorianCalendar();
            samlAssertion.setNotBefore(now.getTime());
            now.add(Calendar.MINUTE, 120);
            samlAssertion.setNotOnOrAfter(now.getTime());

            SAMLAuthorizationDecisionStatement decisionSt = new SAMLAuthorizationDecisionStatement();
            decisionSt.addAction(new SAMLAction(null, action));
            decisionSt.setResource(resource);
            decisionSt.setDecision("Permit");
            SAMLSubject samlSubject = new SAMLSubject();
            samlSubject.setName(new SAMLNameIdentifier(principal, null, null));
            decisionSt.setSubject(samlSubject);
            decisionSt.checkValidity();
            samlAssertion.addStatement(decisionSt);

            samlAssertion.sign("http://www.w3.org/2000/09/xmldsig#rsa-sha1", this.getKeyStore().getKey("aznservice",
                    "password".toCharArray()), Arrays.asList(this.getKeyStore().getCertificateChain("aznservice")));
            return samlAssertion;
        } catch (Exception e) {
            throw new InternalConfigurationException(e);
        }
    }

    /**
     * Check signature.
     * 
     * @param samlAssertion
     *        assertion
     * 
     * @throws InternalAuthorizationException
     * @throws InternalSBBException
     */
    @Override
    void checkSignature(final SAMLAssertion samlAssertion) throws InternalAuthorizationException, InternalSBBException {
        BigInteger certSerialNum = new BigInteger("1116407146");

        Certificate cert = null;
        try {
            cert = this.getCertificateFromKeyStore(certSerialNum);
        } catch (Exception e) {
            throw new InternalInfrastructureException(e);
        }
        if (cert == null)
            throw new InternalConfigurationException("Cannot get Certificate with serial number " + certSerialNum
                    + " from the keystore.");

        try {
            samlAssertion.verify(cert);
        } catch (SAMLException se) {
            throw new InternalAuthorizationException(se);
        }
    }

}
