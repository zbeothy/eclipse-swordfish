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

/**
 * The Class XKMSValidateResponse.
 * 
 */
public class XKMSValidateResponse extends XKMSResponse {

    /** handler. */
    private XKMSResponseHandler handler;

    /**
     * The Constructor.
     * 
     * @param response
     *        response
     * 
     * @throws XKMSResponseException
     *         exception
     */
    public XKMSValidateResponse(final String response) throws XKMSResponseException {
        super();
        this.handler = new XKMSResponseHandler(response, this);
        this.handler.parseResponse();
    }

    /**
     * Gets the status for certificate issuer trust.
     * 
     * @return String status
     */
    public String getStatusForCertificateIssuerTrust() {
        if (this.getValidReason().contains(XKMSDefinitions.XKMS_VALIDREASON_IT))
            return XKMSDefinitions.XKMS_STATUS_VALID;
        else if (this.getInValidReason().contains(XKMSDefinitions.XKMS_VALIDREASON_IT))
            return XKMSDefinitions.XKMS_STATUS_INVALID;
        else
            return XKMSDefinitions.XKMS_STATUS_INDETERMINATE;
    }

    /**
     * Gets the status for certificate revocation.
     * 
     * @return String status
     */
    public String getStatusForCertificateRevocation() {
        if (this.getValidReason().contains(XKMSDefinitions.XKMS_VALIDREASON_RS))
            return XKMSDefinitions.XKMS_STATUS_VALID;
        else if (this.getInValidReason().contains(XKMSDefinitions.XKMS_VALIDREASON_RS))
            return XKMSDefinitions.XKMS_STATUS_INVALID;
        else
            return XKMSDefinitions.XKMS_STATUS_INDETERMINATE;
    }

    /**
     * Gets the status for certificate signature.
     * 
     * @return String sig
     */
    public String getStatusForCertificateSignature() {
        if (this.getValidReason().contains(XKMSDefinitions.XKMS_VALIDREASON_SIG))
            return XKMSDefinitions.XKMS_STATUS_VALID;
        else if (this.getInValidReason().contains(XKMSDefinitions.XKMS_VALIDREASON_SIG))
            return XKMSDefinitions.XKMS_STATUS_INVALID;
        else
            return XKMSDefinitions.XKMS_STATUS_INDETERMINATE;
    }

    /**
     * Gets the status for certificate validity.
     * 
     * @return String status
     */
    public String getStatusForCertificateValidity() {
        if (this.getValidReason().contains(XKMSDefinitions.XKMS_VALIDREASON_VI))
            return XKMSDefinitions.XKMS_STATUS_VALID;
        else if (this.getInValidReason().contains(XKMSDefinitions.XKMS_VALIDREASON_VI))
            return XKMSDefinitions.XKMS_STATUS_INVALID;
        else
            return XKMSDefinitions.XKMS_STATUS_INDETERMINATE;

    }

    /**
     * Checks if is response for sent request.
     * 
     * @param requestID
     *        id
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

}
