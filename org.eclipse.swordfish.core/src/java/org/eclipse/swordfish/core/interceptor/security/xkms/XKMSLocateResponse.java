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
package org.eclipse.swordfish.core.interceptor.security.xkms;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import org.apache.xml.security.utils.Base64;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class XKMSLocateResponse.
 * 
 */
public class XKMSLocateResponse extends XKMSResponse {

    /** logger for the class. */
    private static final Log LOG = SBBLogFactory.getLog(XKMSLocateResponse.class);

    /** response handler. */
    private XKMSResponseHandler handler;

    /**
     * constructor.
     * 
     * @param response
     *        response
     * 
     * @throws XKMSResponseException
     *         exception
     */
    public XKMSLocateResponse(final String response) throws XKMSResponseException {
        super();
        this.handler = new XKMSResponseHandler(response, this);
        this.handler.parseResponse();
    }

    /**
     * Gets the public key.
     * 
     * @return PublicKey key
     * 
     * @throws XKMSResponseException
     *         exception
     */
    public PublicKey getPublicKey() throws XKMSResponseException {
        String strModulus = this.getRsaKeyValueModulus();
        String strExponent = this.getRsaKeyValueExponent();

        if ((null == strModulus) || (null == strExponent)) return null;

        try {
            BigInteger modulus = new BigInteger(Base64.decode(strModulus.getBytes()));
            BigInteger exponent = new BigInteger(Base64.decode(strExponent.getBytes()));
            RSAPublicKeySpec keyspec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey key = factory.generatePublic(keyspec);
            return key;

        } catch (Exception e) {
            XKMSResponseException exp = new XKMSResponseException(e, "Error while generating public key from response.");
            LOG.error("XKMSLocateResponse getPublicKey", exp);

            throw exp;
        }
    }

    /**
     * Checks if is response for sent request.
     * 
     * @param requestID
     *        requestid
     * 
     * @return boolean result
     */
    public boolean isResponseForSentRequest(final String requestID) {
        return requestID.equalsIgnoreCase(this.getRequestID());
    }

    /**
     * Checks if is response successfull.
     * 
     * @return boolean result
     */
    public boolean isResponseSuccessfull() {
        return this.getResultMajor().equalsIgnoreCase(AbstractXKMSResponse.XKMS_RESPONSE_RESULTMAJOR_SUCCESS);
    }

    /**
     * Usable for encryption.
     * 
     * @return boolean result
     */
    public boolean usableForEncryption() {
        return this.getKeyUsage().contains(XKMSDefinitions.XKMS_KEYUSAGE_ENC);
    }

    /**
     * Usable for signature.
     * 
     * @return boolean result
     */
    public boolean usableForSignature() {
        return this.getKeyUsage().contains(XKMSDefinitions.XKMS_KEYUSAGE_SIG);
    }

}
