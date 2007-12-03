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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class XKMSResponseHandler.
 * 
 */
public class XKMSResponseHandler extends AbstractResponseHandler {

    /** log. */
    private static final Log LOG = SBBLogFactory.getLog(XKMSResponseHandler.class);

    /** xkms response. */
    private XKMSResponse xkmsResponse = null;

    /**
     * The Constructor.
     * 
     * @param xkmsResponse
     *        string response
     * @param response
     *        xmlsresponse
     */
    public XKMSResponseHandler(final String xkmsResponse, final XKMSResponse response) {
        super(xkmsResponse);
        this.xkmsResponse = response;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.interceptor.security.xkms.AbstractResponseHandler#parseResponse()
     */
    @Override
    public void parseResponse() throws XKMSResponseException {

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            XKMSContentHandler handler = new XKMSContentHandler();
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
                factory.setFeature("http://xml.org/sax/features/namespaces", true);
                factory.newSAXParser().parse(new InputSource(new ByteArrayInputStream(this.getStrResponse().getBytes())), handler);
            } catch (IOException ioe) {
                XKMSResponseException parseexp = new XKMSResponseException(ioe, "Error while parsing XKMS Response.");
                LOG.error("Error while parsing XKMS Response.", parseexp);
                throw parseexp;
            } catch (SAXException saxe) {
                XKMSResponseException parseexp = new XKMSResponseException(saxe, "Error while parsing XKMS Response.");
                LOG.error("Error while parsing XKMS Response.", parseexp);
                throw parseexp;
            } catch (Exception e) {
                XKMSResponseException parseexp = new XKMSResponseException(e, "Error while parsing XKMS Response.");
                LOG.error("Error while parsing XKMS Response.", parseexp);
                throw parseexp;
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }

    }

    /**
     * The Class XKMSContentHandler.
     */
    private class XKMSContentHandler extends DefaultHandler {

        /** reqid. */
        private String reqid = null;

        /** resid. */
        private String resid = null;

        /** binding id. */
        private String keybindingid = null;

        /** cur element. */
        private String currentElement = null;

        /** app name. */
        private String appName = null;

        /** identifier. */
        private String identifier = null;

        /** cert value. */
        private String certificateValue = "";

        /** result major. */
        private String resultMajor = null;

        /** rsa mod. */
        private String rsamod = "";

        /** The rsaexponent. */
        private String rsaexponent = "";

        /** status. */
        private String statusval = null;

        /**
         * const.
         */
        public XKMSContentHandler() {

        }

        /**
         * Characters.
         * 
         * @param ch
         *        char[]
         * @param start
         *        start
         * @param length
         *        length
         * 
         * @throws SAXException
         *         exception
         */
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {

            if ("X509Certificate".equals(this.currentElement)) {
                this.certificateValue = this.certificateValue + String.valueOf(ch, start, length);
            } else if ("KeyUsage".equals(this.currentElement)) {
                XKMSResponseHandler.this.xkmsResponse.addKeyUsage(String.valueOf(ch, start, length));
            } else if ("Modulus".equals(this.currentElement)) {
                this.rsamod = this.rsamod + String.valueOf(ch, start, length);
            } else if ("Exponent".equals(this.currentElement)) {
                this.rsaexponent = this.rsaexponent + String.valueOf(ch, start, length);
            } else if ("ValidReason".equals(this.currentElement)) {
                XKMSResponseHandler.this.xkmsResponse.addValidReason(String.valueOf(ch, start, length));
            } else if ("InvalidReason".equals(this.currentElement)) {
                XKMSResponseHandler.this.xkmsResponse.addInValidReason(String.valueOf(ch, start, length));
            } else if ("IndeterminateReason".equals(this.currentElement)) {
                XKMSResponseHandler.this.xkmsResponse.addIndeterminateReason(String.valueOf(ch, start, length));
            }
        }

        /**
         * End element.
         * 
         * @param uri
         *        uri
         * @param localName
         *        localname
         * @param qName
         *        qName
         * 
         * @throws SAXException
         *         exception
         */
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {

            this.currentElement = null;

            if ("X509Certificate".equals(localName)) {
                XKMSResponseHandler.this.xkmsResponse.addX509Certificate(this.certificateValue);
                this.certificateValue = "";
            } else if ("Modulus".equals(localName)) {
                XKMSResponseHandler.this.xkmsResponse.setRsaKeyValueModulus(this.rsamod);
                this.rsamod = "";
            } else if ("Exponent".equals(localName)) {
                XKMSResponseHandler.this.xkmsResponse.setRsaKeyValueExponent(this.rsaexponent);
                this.rsaexponent = "";
            }

        }

        /**
         * Error.
         * 
         * @param e
         *        SAXParseException
         * 
         * @throws SAXException
         *         exception
         */
        @Override
        public void error(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }

        /**
         * Fatal error.
         * 
         * @param e
         *        SAXParseException
         * 
         * @throws SAXException
         *         exception
         */
        @Override
        public void fatalError(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }

        /**
         * Start element.
         * 
         * @param uri
         *        uri
         * @param localName
         *        localname
         * @param qName
         *        qname
         * @param attributes
         *        attributes
         * 
         * @throws SAXException
         *         saxexception
         */
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                throws SAXException {

            this.currentElement = localName;

            if ("ValidateResult".equals(localName)) {
                XKMSResponseHandler.this.xkmsResponse.setResponseType(AbstractXKMSResponse.XKMS_VALIDATE_RESPONSE);
                this.reqid = attributes.getValue("RequestId");
                XKMSResponseHandler.this.xkmsResponse.setRequestID(this.reqid);
                this.resid = attributes.getValue("Id");
                XKMSResponseHandler.this.xkmsResponse.setResponseID(this.resid);
                this.resultMajor = attributes.getValue("ResultMajor");
                XKMSResponseHandler.this.xkmsResponse.setResultMajor(this.resultMajor);
            } else if ("LocateResult".equals(localName)) {
                XKMSResponseHandler.this.xkmsResponse.setResponseType(AbstractXKMSResponse.XKMS_LOCATE_RESPONSE);
                this.reqid = attributes.getValue("RequestId");
                XKMSResponseHandler.this.xkmsResponse.setRequestID(this.reqid);
                this.resid = attributes.getValue("Id");
                XKMSResponseHandler.this.xkmsResponse.setResponseID(this.resid);
                this.resultMajor = attributes.getValue("ResultMajor");
                XKMSResponseHandler.this.xkmsResponse.setResultMajor(this.resultMajor);
            } else if (AbstractXKMSResponse.XKMS_RESPONSE_KEYBINDING_UNVERIFIED.equals(localName)) {
                XKMSResponseHandler.this.xkmsResponse.setKeyBindingType(AbstractXKMSResponse.XKMS_RESPONSE_KEYBINDING_UNVERIFIED);
            } else if (AbstractXKMSResponse.XKMS_RESPONSE_KEYBINDING_VERIFIED.equals(localName)) {
                XKMSResponseHandler.this.xkmsResponse.setKeyBindingType(AbstractXKMSResponse.XKMS_RESPONSE_KEYBINDING_VERIFIED);
            } else if (AbstractXKMSResponse.XKMS_RESPONSE_KEYBINDING_UNVERIFIED.equals(localName)
                    || AbstractXKMSResponse.XKMS_RESPONSE_KEYBINDING_VERIFIED.equals(localName)) {
                this.keybindingid = attributes.getValue("Id");
                XKMSResponseHandler.this.xkmsResponse.setKeyBindingID(this.keybindingid);
            } else if ("UseKeyWith".equals(localName)) {
                this.identifier = attributes.getValue("Identifier");
                this.appName = attributes.getValue("Application");
                try {
                    XKMSResponseHandler.this.xkmsResponse.addUseKeyWith(this.appName, this.identifier);
                } catch (Exception e) {
                    throw new SAXException(e);
                }

            } else if ("Status".equals(localName)) {
                this.statusval = attributes.getValue("StatusValue");
                XKMSResponseHandler.this.xkmsResponse.setStatusValue(this.statusval);
            }
        }

        /**
         * Warning.
         * 
         * @param e
         *        SAXParseException
         * 
         * @throws SAXException
         *         exception
         */
        @Override
        public void warning(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }

    }

}
