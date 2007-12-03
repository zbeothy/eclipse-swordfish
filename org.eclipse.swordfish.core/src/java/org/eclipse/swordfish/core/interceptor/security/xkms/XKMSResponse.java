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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * The Class XKMSResponse.
 * 
 */
public class XKMSResponse extends AbstractXKMSRequest {

    /** response id. */
    private String responseID = null;

    /** request id. */
    private String requestID = null;

    /** binding id. */
    private String keyBindingID = null;

    /** certificate. */
    private Vector x509Certificate = null;

    /** key usage. */
    private Vector keyUsage = null;

    /** key with. */
    private Hashtable useKeyWith = null;

    /** status value. */
    private String statusValue = null;

    /** valida reason. */
    private Vector validReason = null;

    /** invalid reason. */
    private Vector invalidReason = null;

    /** inter reason. */
    private Vector indeterminateReason = null;

    /** key value. */
    private String rsaKeyValueModulus = null;

    /** exponent. */
    private String rsaKeyValueExponent = null;

    /** type. */
    private String responseType = null;

    /** result. */
    private String resultMajor = null;

    /** type binding. */
    private String keyBindingType = null;

    /**
     * constructor.
     */
    public XKMSResponse() {
        this.x509Certificate = new Vector();
        this.keyUsage = new Vector();
        this.useKeyWith = new Hashtable();
        this.validReason = new Vector();
        this.invalidReason = new Vector();
        this.indeterminateReason = new Vector();
    }

    /**
     * Adds the indeterminate reason.
     * 
     * @param indeterminateReasonValue
     *        value
     * 
     * @return boolean result
     */
    public boolean addIndeterminateReason(final String indeterminateReasonValue) {

        return this.indeterminateReason.add(indeterminateReasonValue);
    }

    /**
     * Adds the in valid reason.
     * 
     * @param invalidReasonValue
     *        value
     * 
     * @return boolean result
     */
    public boolean addInValidReason(final String invalidReasonValue) {

        return this.invalidReason.add(invalidReasonValue);
    }

    /**
     * Adds the key usage.
     * 
     * @param keyUsageValue
     *        value
     * 
     * @return boolean result
     */
    public boolean addKeyUsage(final String keyUsageValue) {
        return this.keyUsage.add(keyUsageValue);
    }

    /**
     * Adds the use key with.
     * 
     * @param useKeyWithKey
     *        keywith
     * @param useKeyWithValue
     *        keyvalue
     * 
     * @throws Exception
     *         exception
     */
    public void addUseKeyWith(final String useKeyWithKey, final String useKeyWithValue) throws Exception {
        this.useKeyWith.put(useKeyWithKey, useKeyWithValue);
    }

    /**
     * Adds the valid reason.
     * 
     * @param validReasonValue
     *        value
     * 
     * @return boolean result
     */
    public boolean addValidReason(final String validReasonValue) {
        return this.validReason.add(validReasonValue);
    }

    /**
     * Adds the x509 certificate.
     * 
     * @param certificateValue
     *        value
     */
    public void addX509Certificate(final String certificateValue) {
        this.x509Certificate.add(certificateValue);
    }

    /**
     * Gets the indeterminate reason.
     * 
     * @return Vector vec
     */
    public Vector getIndeterminateReason() {
        return this.indeterminateReason;
    }

    /**
     * Gets the in valid reason.
     * 
     * @return Vector vector
     */
    public Vector getInValidReason() {
        return this.invalidReason;
    }

    /**
     * Gets the key binding ID.
     * 
     * @return String id
     */
    public String getKeyBindingID() {
        return this.keyBindingID;
    }

    /**
     * Gets the key binding type.
     * 
     * @return String type
     */
    public String getKeyBindingType() {
        return this.keyBindingType;
    }

    /**
     * Gets the key usage.
     * 
     * @return Vector vector
     */
    public Vector getKeyUsage() {
        return this.keyUsage;
    }

    /**
     * Gets the request ID.
     * 
     * @return String id
     */
    public String getRequestID() {
        return this.requestID;
    }

    /**
     * Gets the response ID.
     * 
     * @return String id
     */
    public String getResponseID() {
        return this.responseID;
    }

    /**
     * Getresponse type.
     * 
     * @return String type
     */
    public String getresponseType() {
        return this.responseType;
    }

    /**
     * Gets the result major.
     * 
     * @return String result
     */
    public String getResultMajor() {
        return this.resultMajor;
    }

    /**
     * Gets the status value.
     * 
     * @return String value
     */
    public String getStatusValue() {
        return this.statusValue;
    }

    /**
     * Gets the use key with.
     * 
     * @return Hashtable hash
     */
    public Hashtable getUseKeyWith() {
        return this.useKeyWith;
    }

    /**
     * Gets the valid reason.
     * 
     * @return Vector vector
     */
    public Vector getValidReason() {
        return this.validReason;
    }

    /**
     * Gets the x509 certificate.
     * 
     * @return Vector vec
     */
    public Vector getX509Certificate() {
        return this.x509Certificate;
    }

    /**
     * Sets the key binding ID.
     * 
     * @param keyBindingID
     *        id
     */
    public void setKeyBindingID(final String keyBindingID) {
        this.keyBindingID = keyBindingID;
    }

    /**
     * Sets the key binding type.
     * 
     * @param keyBindingType
     *        type
     */
    public void setKeyBindingType(final String keyBindingType) {
        this.keyBindingType = keyBindingType;
    }

    /**
     * Sets the request ID.
     * 
     * @param requestID
     *        id
     */
    public void setRequestID(final String requestID) {
        this.requestID = requestID;
    }

    /**
     * Sets the response ID.
     * 
     * @param responseID
     *        id
     */
    public void setResponseID(final String responseID) {
        this.responseID = responseID;
    }

    /**
     * Sets the response type.
     * 
     * @param responseTypeValue
     *        type
     */
    public void setResponseType(final String responseTypeValue) {
        this.responseType = responseTypeValue;
    }

    /**
     * Sets the result major.
     * 
     * @param resultMajor
     *        result
     */
    public void setResultMajor(final String resultMajor) {
        this.resultMajor = resultMajor;
    }

    /**
     * Sets the rsa key value exponent.
     * 
     * @param rsaKeyValueExponent
     *        value
     */
    public void setRsaKeyValueExponent(final String rsaKeyValueExponent) {
        this.rsaKeyValueExponent = rsaKeyValueExponent;
    }

    /**
     * Sets the rsa key value modulus.
     * 
     * @param rsaKeyValueModulus
     *        value
     */
    public void setRsaKeyValueModulus(final String rsaKeyValueModulus) {
        this.rsaKeyValueModulus = rsaKeyValueModulus;
    }

    /**
     * Sets the status value.
     * 
     * @param statusValue
     *        value
     */
    public void setStatusValue(final String statusValue) {
        this.statusValue = statusValue;
    }

    /**
     * To XML string.
     * 
     * @return String xmlstring
     * 
     * @throws XKMSResponseException
     *         exception
     */
    public String toXMLString() throws XKMSResponseException {

        try {

            DocumentBuilder docbuilder = TransformerUtil.getDocumentBuilder();
            Document document = docbuilder.newDocument();

            Element result = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, this.responseType);

            result.setAttribute("xmlns", XKMSDefinitions.XMLNS_XKMS);
            result.setAttribute("xmlns:" + XKMSDefinitions.XMLNS_XMLDSPREFIX, XKMSDefinitions.XMLNS_XMLDS);
            result.setAttribute("xmlns:" + XKMSDefinitions.XMLNS_XMLENCPREFIX, XKMSDefinitions.XMLNS_XMLENC);
            result.setAttribute("RequestId", this.requestID);
            result.setAttribute("Id", this.responseID);
            result.setAttribute("Service", XKMSDefinitions.XKMS_SERVICE);
            result.setAttribute("ResultMajor", this.resultMajor);

            Element elementKeybinding = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, this.keyBindingType);
            elementKeybinding.setAttribute("Id", this.keyBindingID);

            Element elementKeyinfo = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "KeyInfo");

            elementKeyinfo.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);

            if (!this.x509Certificate.isEmpty()) {
                Element elementX509data = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "X509Data");
                elementX509data.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);

                int size = this.x509Certificate.size();
                for (int i = 0; i < size; i++) {
                    Element elementX509certificate = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "X509Certificate");
                    elementX509certificate.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);
                    Text text = document.createTextNode("");
                    text.setData((String) this.x509Certificate.get(i));
                    elementX509certificate.appendChild(text);
                    elementX509data.appendChild(elementX509certificate);

                }

                elementKeyinfo.appendChild(elementX509data);

            }

            if (this.rsaKeyValueModulus != null) {
                Element elementKeyvalue = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "KeyValue");
                elementKeyvalue.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);

                Element elementKsakeyvalue = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "RSAKeyValue");
                elementKsakeyvalue.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);

                Element elementModulus = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "Modulus");
                elementModulus.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);

                Text text = document.createTextNode("");
                text.setData(this.rsaKeyValueModulus);
                elementModulus.appendChild(text);

                Element elementExponent = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "Exponent");
                elementExponent.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);

                Text text1 = document.createTextNode("");
                text1.setData(this.rsaKeyValueExponent);
                elementExponent.appendChild(text1);

                elementKsakeyvalue.appendChild(elementModulus);
                elementKsakeyvalue.appendChild(elementExponent);

                elementKeyvalue.appendChild(elementKsakeyvalue);

                elementKeyinfo.appendChild(elementKeyvalue);
            }

            elementKeybinding.appendChild(elementKeyinfo);

            if (!this.keyUsage.isEmpty()) {
                int size = this.keyUsage.size();
                for (int i = 0; i < size; i++) {
                    Element elementKeyusage = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "KeyUsage");
                    elementKeyusage.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);
                    Text text = document.createTextNode("");
                    text.setData((String) this.keyUsage.get(i));
                    elementKeyusage.appendChild(text);
                    // element_keyusage.setNodeValue((String)keyUsage.get(i));
                    elementKeybinding.appendChild(elementKeyusage);
                }
            }

            if (!this.useKeyWith.isEmpty()) {
                Enumeration keys = this.useKeyWith.keys();

                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    String val = (String) this.useKeyWith.get(key);

                    if (val != null) {
                        Element elementUsekeywith = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, "UseKeyWith");
                        elementUsekeywith.setAttribute("Application", key);
                        elementUsekeywith.setAttribute("Identifier", val);
                        elementKeybinding.appendChild(elementUsekeywith);
                    }
                }

            }

            if (this.statusValue != null) {
                Element elementStatus = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, "Status");
                elementStatus.setAttribute("StatusValue", this.statusValue);
                if (!this.validReason.isEmpty()) {
                    int size = this.validReason.size();
                    for (int i = 0; i < size; i++) {
                        Element elementValidreason = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, "ValidReason");
                        Text text = document.createTextNode("");
                        text.setData((String) this.validReason.get(i));
                        elementValidreason.appendChild(text);
                        elementStatus.appendChild(elementValidreason);
                    }
                }
                if (!this.invalidReason.isEmpty()) {
                    int size = this.invalidReason.size();
                    for (int i = 0; i < size; i++) {
                        Element elementInvalidreason = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, "InvalidReason");
                        Text text = document.createTextNode("");
                        text.setData((String) this.invalidReason.get(i));
                        elementInvalidreason.appendChild(text);
                        elementStatus.appendChild(elementInvalidreason);
                    }
                }
                if (!this.indeterminateReason.isEmpty()) {
                    int size = this.indeterminateReason.size();
                    for (int i = 0; i < size; i++) {
                        Element elementIndeterminatereason =
                                document.createElementNS(XKMSDefinitions.XMLNS_XKMS, "IndeterminateReason");
                        Text text = document.createTextNode("");
                        text.setData((String) this.indeterminateReason.get(i));
                        elementIndeterminatereason.appendChild(text);
                        elementStatus.appendChild(elementIndeterminatereason);
                    }
                }

                elementKeybinding.appendChild(elementStatus);
            }

            result.appendChild(elementKeybinding);
            document.appendChild(result);
            return TransformerUtil.stringFromDomNode(document);
        } catch (Exception e) {
            XKMSResponseException respexp = new XKMSResponseException(e, "Error while generating the XKMS Response.");
            throw respexp;
        }

    }

    /**
     * Gets the rsa key value exponent.
     * 
     * @return String value
     */
    String getRsaKeyValueExponent() {
        return this.rsaKeyValueExponent;
    }

    /**
     * Gets the rsa key value modulus.
     * 
     * @return String value
     */
    String getRsaKeyValueModulus() {
        return this.rsaKeyValueModulus;
    }

}
