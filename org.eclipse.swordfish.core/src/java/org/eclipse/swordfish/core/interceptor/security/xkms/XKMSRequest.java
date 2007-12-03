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

import java.rmi.server.UID;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * The Class XKMSRequest.
 * 
 */
public class XKMSRequest extends AbstractXKMSRequest {

    /** query binding. */
    private static final String QUERY_BINARY_BING_TYPE = "QueryKeyBinding";

    /** logger. */
    private static Log log = SBBLogFactory.getLog(XKMSRequest.class);

    /** request id. */
    private String requestID = null;

    /** respond with. */
    private Vector respondWith = null;

    /** X509Certificate. */
    private Vector x509Certificate = null;

    /** key usage. */
    private Vector keyUsage = null;

    /** use key with. */
    private Hashtable useKeyWith = null;

    /** request type. */
    private String requestType = null;

    /**
     * constructor.
     */
    public XKMSRequest() {
        this.respondWith = new Vector();
        this.x509Certificate = new Vector();
        this.keyUsage = new Vector();
        this.useKeyWith = new Hashtable();

        this.setRequestID(new UID().toString());

    }

    /**
     * Adds the key usage.
     * 
     * @param keyUsageValue
     *        usage
     * 
     * @return boolean result
     */
    public boolean addKeyUsage(final String keyUsageValue) {
        if (log.isDebugEnabled()) {
            log.debug("Adding keyusage with " + keyUsageValue);
        }
        return this.keyUsage.add(keyUsageValue);
    }

    /**
     * Adds the respond with.
     * 
     * @param respondWithValue
     *        The respondWithValue to set.
     * 
     * @return boolean result
     */
    public boolean addRespondWith(final String respondWithValue) {
        if (log.isDebugEnabled()) {
            log.debug("Adding respond with " + respondWithValue);
        }
        return this.respondWith.add(respondWithValue);
    }

    /**
     * Adds the use key with.
     * 
     * @param useKeyWithKey
     *        use key with
     * @param useKeyWithValue
     *        key value
     * 
     * @throws Exception
     *         exception
     */
    public void addUseKeyWith(final String useKeyWithKey, final String useKeyWithValue) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Adding use key with " + useKeyWithKey + ":::" + useKeyWithValue);
        }
        this.useKeyWith.put(useKeyWithKey, useKeyWithValue);
    }

    /**
     * Adds the x509 certificate.
     * 
     * @param certificate
     *        The x509Certificate to set.
     * 
     * @return boolean result
     */
    public boolean addX509Certificate(final String certificate) {
        if (log.isDebugEnabled()) {
            log.debug("Adding certificate " + certificate);
        }
        return this.x509Certificate.add(certificate);
    }

    /**
     * Getkey usage.
     * 
     * @return Vector vector
     */
    public Vector getkeyUsage() {
        return this.keyUsage;
    }

    /**
     * Gets the request ID.
     * 
     * @return Returns the requestID.
     */
    public String getRequestID() {
        return this.requestID;
    }

    /**
     * Gets the request type.
     * 
     * @return Returns the requestType.
     */
    public String getRequestType() {
        return this.requestType;
    }

    /**
     * Gets the respond with.
     * 
     * @return Returns the respondWith.
     */
    public Vector getRespondWith() {
        return this.respondWith;
    }

    /**
     * Gets the use key with.
     * 
     * @return Returns the useKeyWith.
     */
    public Hashtable getUseKeyWith() {
        return this.useKeyWith;
    }

    /**
     * Gets the x509 certificate.
     * 
     * @return Returns the x509Certificate.
     */
    public Vector getX509Certificate() {
        return this.x509Certificate;
    }

    /**
     * To XML string.
     * 
     * @return String sml string
     * 
     * @throws XKMSRequestException
     *         exception
     */
    public String toXMLString() throws XKMSRequestException {

        try {

            DocumentBuilder docbuilder = TransformerUtil.getDocumentBuilder();
            Document document = docbuilder.newDocument();

            Element result = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, this.requestType);

            result.setAttribute("xmlns", XKMSDefinitions.XMLNS_XKMS);
            result.setAttribute("xmlns:" + XKMSDefinitions.XMLNS_XMLDSPREFIX, XKMSDefinitions.XMLNS_XMLDS);
            result.setAttribute("xmlns:" + XKMSDefinitions.XMLNS_XMLENCPREFIX, XKMSDefinitions.XMLNS_XMLENC);
            result.setAttribute("Id", this.requestID);
            result.setAttribute("Service", XKMSDefinitions.XKMS_SERVICE);

            if (!this.respondWith.isEmpty()) {
                int size = this.respondWith.size();
                for (int i = 0; i < size; i++) {
                    Element elementResponseWith = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, "RespondWith");
                    Text text = document.createTextNode("");
                    text.setData((String) this.respondWith.get(i));
                    elementResponseWith.appendChild(text);
                    result.appendChild(elementResponseWith);
                }
            }

            Element elementKeybinding = document.createElementNS(XKMSDefinitions.XMLNS_XKMS, QUERY_BINARY_BING_TYPE);

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

            elementKeybinding.appendChild(elementKeyinfo);

            if (!this.keyUsage.isEmpty()) {
                int size = this.keyUsage.size();
                for (int i = 0; i < size; i++) {
                    Element elementKeyusage = document.createElementNS(XKMSDefinitions.XMLNS_XMLDS, "KeyUsage");
                    elementKeyusage.setPrefix(XKMSDefinitions.XMLNS_XMLDSPREFIX);
                    Text text = document.createTextNode("");
                    text.setData((String) this.keyUsage.get(i));
                    elementKeyusage.appendChild(text);
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

            result.appendChild(elementKeybinding);
            document.appendChild(result);
            return TransformerUtil.stringFromDomNode(document);
        } catch (Exception e) {
            XKMSRequestException reqexp = new XKMSRequestException(e, "Error while generating the XKMS Request.");
            log.error("Error while generating the XKMS Request.", reqexp);
            throw reqexp;
        }

    }

    /**
     * Sets the request ID.
     * 
     * @param requestID
     *        The requestID to set.
     */
    void setRequestID(final String requestID) {
        if (log.isDebugEnabled()) {
            log.debug("Adding request id " + requestID);
        }
        this.requestID = requestID;
    }

    /**
     * Sets the request type.
     * 
     * @param requestType
     *        The requestType to set.
     */
    void setRequestType(final String requestType) {
        if (log.isDebugEnabled()) {
            log.debug("Adding request type " + requestType);
        }
        this.requestType = requestType;
    }

}
