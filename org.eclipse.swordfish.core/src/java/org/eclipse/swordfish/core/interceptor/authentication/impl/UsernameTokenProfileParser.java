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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class UsernameTokenProfileParser.
 */
public class UsernameTokenProfileParser {

    /** username. */
    private String username = "";

    /** password. */
    private String password = "";

    /**
     * Gets the password.
     * 
     * @return String password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Gets the username.
     * 
     * @return String username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Parse.
     * 
     * @param tokenNode
     *        profile
     * 
     * @throws Exception
     *         exception
     */
    public void parse(final Node tokenNode) throws Exception {
        String profile = TransformerUtil.stringFromDomNode(tokenNode);
        this.username = "";
        this.password = "";
        this.init(profile);
    }

    /**
     * the init method.
     * 
     * @param profile
     *        profile
     * 
     * @throws Exception
     *         exception
     */
    private void init(final String profile) throws Exception {
        UsernameTokenHandler handler = new UsernameTokenHandler();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
            factory.setFeature("http://xml.org/sax/features/namespaces", true);
            factory.newSAXParser().parse(new InputSource(new ByteArrayInputStream(profile.getBytes())), handler);
        } catch (JustToBreakParsingSAXException e) {
            return;
        } catch (IOException e) {
            throw e;
        } catch (SAXException e) {
            throw e;
        }
    }

    /**
     * exception class.
     */
    private static class JustToBreakParsingSAXException extends SAXException {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 5050903598497999330L;

        /**
         * The Constructor.
         * 
         * @param e
         *        exception
         */
        public JustToBreakParsingSAXException(final Exception e) {
            super(e);
        }
    }

    /**
     * handler.
     */
    private class UsernameTokenHandler extends DefaultHandler {

        /** The read username. */
        private boolean readUsername = false;

        /** The read password. */
        private boolean readPassword = false;

        /**
         * Characters.
         * 
         * @param ch
         *        the characters
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
            if (this.readUsername) {
                UsernameTokenProfileParser.this.username =
                        UsernameTokenProfileParser.this.username + String.valueOf(ch, start, length);
            } else if (this.readPassword) {
                UsernameTokenProfileParser.this.password =
                        UsernameTokenProfileParser.this.password + String.valueOf(ch, start, length);
            }
        }

        /**
         * End element.
         * 
         * @param uri
         *        the url
         * @param localName
         *        the name
         * @param qName
         *        the qname
         * 
         * @throws SAXException
         *         exception
         */
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if ("Username".equals(localName)) {
                this.readUsername = false;
            } else if ("Password".equals(localName)) {
                this.readPassword = false;
            }
        }

        /**
         * Error.
         * 
         * @param e
         *        exception
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
         *        exception
         * 
         * @throws SAXException
         *         exception
         */
        @Override
        public void fatalError(final SAXParseException e) throws SAXException {
            throw new SAXException(e);
        }

        public boolean isReadPassword() {
            return this.readPassword;
        }

        public boolean isReadUsername() {
            return this.readUsername;
        }

        public void setReadPassword(final boolean readPassword) {
            this.readPassword = readPassword;
        }

        public void setReadUsername(final boolean readUsername) {
            this.readUsername = readUsername;
        }

        /**
         * Start element.
         * 
         * @param uri
         *        the url
         * @param localName
         *        the name
         * @param qName
         *        the qname
         * @param attributes
         *        attributes
         * 
         * @throws SAXException
         *         exception
         */
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                throws SAXException {
            if ("Username".equals(localName)) {
                this.readUsername = true;
            } else if ("Password".equals(localName)) {
                this.readPassword = true;
            }
        }

        /**
         * Warning.
         * 
         * @param e
         *        exception
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
