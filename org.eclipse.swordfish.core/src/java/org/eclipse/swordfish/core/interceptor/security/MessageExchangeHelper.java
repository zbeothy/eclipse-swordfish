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
package org.eclipse.swordfish.core.interceptor.security;

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.jbi.messaging.NormalizedMessage;
import javax.security.auth.Subject;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.EncryptionConstants;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAuthenticationStatement;
import org.opensaml.SAMLAuthorizationDecisionStatement;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Class MessageExchangeHelper.
 */
public class MessageExchangeHelper {

    /** saml namespace. */
    public static final String SAML_NS = "urn:oasis:names:tc:SAML:1.0:assertion";

    /** saml assertion element. */
    public static final String ASSERTION_ELEMENT = "Assertion";

    /** username token element. */
    public static final String USERNAMETOKEN_ELEMENT = "UsernameToken";

    /** signature namespace. */
    public static final String SIGNATURE_NS = Constants.SignatureSpecNS;

    /** signature element. */
    public static final String SIGNATURE_ELEMENT = Constants._TAG_SIGNATURE;

    /** encryption key element. */
    public static final String ENCKEY_ELEMENT = EncryptionConstants._TAG_ENCRYPTEDKEY;

    /** encryption namespace. */
    public static final String ENCRYPTION_NS = EncryptionConstants.EncryptionSpecNS;

    /**
     * Gets the auth assertion from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @return String assertions
     * 
     * @throws Exception
     *         exception
     */
    public String getAuthAssertionFromExchange(final NormalizedMessage me) throws Exception {
        String assertions = null;
        Document document = null;
        document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return assertions;

        NodeList list = document.getElementsByTagNameNS(SAML_NS, ASSERTION_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            Node assertionNode = list.item(i);
            SAMLAssertion samlAssertion = new SAMLAssertion((Element) assertionNode);
            Iterator iter = samlAssertion.getStatements();
            while (iter.hasNext()) {
                if (iter.next() instanceof SAMLAuthenticationStatement) {
                    assertions = TransformerUtil.stringFromDomNode(assertionNode);
                    break;
                }
            }
        }

        return assertions;
    }

    /**
     * Gets the az assertion from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @return String assertions
     * 
     * @throws Exception
     *         exception
     */
    public String getAzAssertionFromExchange(final NormalizedMessage me) throws Exception {
        String assertions = null;
        Document document = null;
        document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return assertions;

        NodeList list = document.getElementsByTagNameNS(SAML_NS, ASSERTION_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            Node assertionNode = list.item(i);
            SAMLAssertion samlAssertion = new SAMLAssertion((Element) assertionNode);
            Iterator iter = samlAssertion.getStatements();
            while (iter.hasNext()) {
                if (iter.next() instanceof SAMLAuthorizationDecisionStatement) {
                    assertions = TransformerUtil.stringFromDomNode(assertionNode);
                    break;
                }
            }
        }

        return assertions;
    }

    /**
     * Gets the encryption from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @return String assertions
     * 
     * @throws Exception
     *         exception
     */
    public Node getEncryptionFromExchange(final NormalizedMessage me) throws Exception {
        Node encryption = null;
        Document document = null;
        document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return encryption;

        NodeList list = document.getDocumentElement().getChildNodes();
        // document.getElementsByTagNameNS(SIGNATURE_NS, KEYINFO_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            Node encryptionNode = list.item(i);
            if (encryptionNode.getNamespaceURI().equalsIgnoreCase(ENCRYPTION_NS)
                    && encryptionNode.getLocalName().equalsIgnoreCase(ENCKEY_ELEMENT)) {
                encryption = encryptionNode;
                break;
            }

        }

        return encryption;
    }

    /**
     * Gets the principal name from subject.
     * 
     * @param subject
     *        authenticated subject
     * 
     * @return principal name
     * 
     * @throws Exception
     *         exception
     */
    public String getPrincipalNameFromSubject(final Subject subject) throws Exception {
        String principalName = null;
        String sbbPrincipalClassName = "de.deutschepost.ubbrief.backbone.login.Principal";
        if (subject == null) return null;
        Set principals = subject.getPrincipals();
        if (!principals.isEmpty()) {
            Object[] principalArray = principals.toArray();
            if (principalArray.length > 0) {
                for (int i = 0; i < principalArray.length; i++) {
                    Class[] recInterfaceNames = principalArray[i].getClass().getInterfaces();
                    for (int x = 0; x < recInterfaceNames.length; x++) {
                        String temp = (recInterfaceNames[x]).getName();
                        if (temp.equals(sbbPrincipalClassName)) {
                            principalName = ((Principal) principalArray[i]).getName();
                            break;
                        }
                    }
                }

            }
        }
        return principalName;
    }

    /**
     * Gets the InternalSBB principal object from subject.
     * 
     * @param subject
     *        authenticated subject
     * 
     * @return Object principal
     * 
     * @throws Exception
     *         exception
     */
    public Object getSBBPrincipalObjectFromSubject(final Subject subject) throws Exception {
        Object principal = null;
        String sbbPrincipalClassName = "de.deutschepost.ubbrief.backbone.login.Principal";
        if (subject == null) return null;
        Set principals = subject.getPrincipals();
        if (!principals.isEmpty()) {
            Object[] principalArray = principals.toArray();
            if (principalArray.length > 0) {
                for (int i = 0; i < principalArray.length; i++) {
                    Class[] recInterfaceNames = principalArray[i].getClass().getInterfaces();
                    for (int x = 0; x < recInterfaceNames.length; x++) {
                        String temp = (recInterfaceNames[x]).getName();
                        if (temp.equals(sbbPrincipalClassName)) {
                            principal = principalArray[i];
                            break;
                        }
                    }
                }

            }
        }
        return principal;
    }

    /**
     * Gets the signature from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @return String assertions
     * 
     * @throws Exception
     *         exception
     */
    public Node getSignatureFromExchange(final NormalizedMessage me) throws Exception {
        Node signature = null;
        Document document = null;
        document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return signature;

        NodeList list = document.getDocumentElement().getChildNodes();
        // document.getElementsByTagNameNS(SIGNATURE_NS, SIGNATURE_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            Node signatureNode = list.item(i);
            if (signatureNode.getNamespaceURI().equalsIgnoreCase(SIGNATURE_NS)
                    && signatureNode.getLocalName().equalsIgnoreCase(SIGNATURE_ELEMENT)) {
                signature = signatureNode;
                break;
            }
        }

        return signature;
    }

    /**
     * Gets the username token from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @return String token profile
     * 
     * @throws Exception
     *         exception
     */
    public Node getUsernameTokenFromExchange(final NormalizedMessage me) throws Exception {
        Node tokenProfile = null;
        Document document = null;
        document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return tokenProfile;

        NodeList list = document.getElementsByTagNameNS(HeaderUtil.WSSECURITY_NS, USERNAMETOKEN_ELEMENT);

        if ((list == null) || (list.getLength() == 0)) return tokenProfile;
        tokenProfile = document.getDocumentElement();
        return tokenProfile;

    }

    /**
     * Removes the auth assertion from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @throws Exception
     *         exception
     */
    public void removeAuthAssertionFromExchange(final NormalizedMessage me) throws Exception {

        Document document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return;

        Node assertionNode = null;
        NodeList list = document.getElementsByTagNameNS(SAML_NS, ASSERTION_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            assertionNode = list.item(i);
            SAMLAssertion samlAssertion = new SAMLAssertion((Element) assertionNode);
            Iterator iter = samlAssertion.getStatements();
            while (iter.hasNext()) {
                if (iter.next() instanceof SAMLAuthenticationStatement) {
                    document.getFirstChild().removeChild(assertionNode);
                }
            }
        }

        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());
        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }
    }

    /**
     * Removes the az assertion from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @throws Exception
     *         exception
     */
    public void removeAzAssertionFromExchange(final NormalizedMessage me) throws Exception {

        Document document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return;

        Node assertionNode = null;
        NodeList list = document.getElementsByTagNameNS(SAML_NS, ASSERTION_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            assertionNode = list.item(i);
            SAMLAssertion samlAssertion = new SAMLAssertion((Element) assertionNode);
            Iterator iter = samlAssertion.getStatements();
            while (iter.hasNext()) {
                if (iter.next() instanceof SAMLAuthorizationDecisionStatement) {
                    document.getFirstChild().removeChild(assertionNode);
                }
            }
        }

        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());

        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }
    }

    /**
     * Removes the encryption from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @throws Exception
     *         exception
     */
    public void removeEncryptionFromExchange(final NormalizedMessage me) throws Exception {

        Document document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return;

        Node encryptionNode = null;
        NodeList list = document.getDocumentElement().getChildNodes();
        // document.getElementsByTagNameNS(SIGNATURE_NS, KEYINFO_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            encryptionNode = list.item(i);
            if (encryptionNode.getNamespaceURI().equalsIgnoreCase(ENCRYPTION_NS)
                    && encryptionNode.getLocalName().equalsIgnoreCase(ENCKEY_ELEMENT)) {
                document.getFirstChild().removeChild(encryptionNode);
            }
        }
        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());
        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }
    }

    /**
     * Removes the signature from exchange.
     * 
     * @param me
     *        message exchange
     * 
     * @throws Exception
     *         exception
     */
    public void removeSignatureFromExchange(final NormalizedMessage me) throws Exception {

        Document document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) return;

        NodeList list = document.getDocumentElement().getChildNodes();
        // document.getElementsByTagNameNS(SIGNATURE_NS, SIGNATURE_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            Node tempNode = list.item(i);
            if (tempNode.getNamespaceURI().equalsIgnoreCase(SIGNATURE_NS)
                    && tempNode.getLocalName().equalsIgnoreCase(SIGNATURE_ELEMENT)) {
                document.getFirstChild().removeChild(tempNode);
            }
        }

        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());
        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }
    }

    /**
     * Sets the auth assertion in exchange.
     * 
     * @param me
     *        message exchange
     * @param assertions
     *        assertions
     * 
     * @throws Exception
     *         exception
     */
    public void setAuthAssertionInExchange(final NormalizedMessage me, final String assertions) throws Exception {

        Document document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) {
            document = HeaderUtil.createWSSecurityHeader();
        }

        Node assertionNode = null;
        NodeList list = document.getElementsByTagNameNS(SAML_NS, ASSERTION_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            assertionNode = list.item(i);
            SAMLAssertion samlAssertion = new SAMLAssertion((Element) assertionNode);
            Iterator iter = samlAssertion.getStatements();
            while (iter.hasNext()) {
                if (iter.next() instanceof SAMLAuthenticationStatement) {
                    document.getFirstChild().removeChild(assertionNode);
                }
            }
        }

        Document assertionDoc = TransformerUtil.docFromString(assertions);
        assertionNode = document.importNode(assertionDoc.getDocumentElement(), true);
        document.getFirstChild().appendChild(assertionNode);
        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());
        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }

    }

    /**
     * Sets the az assertion in exchange.
     * 
     * @param me
     *        message exchange
     * @param assertions
     *        assertions
     * 
     * @throws Exception
     *         exception
     */
    public void setAzAssertionInExchange(final NormalizedMessage me, final Node assertions) throws Exception {
        Document document = null;
        document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) {
            document = HeaderUtil.createWSSecurityHeader();
        }

        Node assertionNode = null;
        NodeList list = document.getElementsByTagNameNS(SAML_NS, ASSERTION_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            assertionNode = list.item(i);
            SAMLAssertion samlAssertion = new SAMLAssertion((Element) assertionNode);
            Iterator iter = samlAssertion.getStatements();
            while (iter.hasNext()) {
                if (iter.next() instanceof SAMLAuthorizationDecisionStatement) {
                    document.getFirstChild().removeChild(assertionNode);
                }
            }
        }

        assertionNode = document.importNode(assertions, true);
        document.getFirstChild().appendChild(assertionNode);
        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());
        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }
    }

    /**
     * Sets the encryption in exchange.
     * 
     * @param me
     *        message exchange
     * @param encryption
     *        encryption
     * 
     * @throws Exception
     *         exception
     */
    public void setEncryptionInExchange(final NormalizedMessage me, final Node encryption) throws Exception {
        Document document = null;
        document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) {
            document = HeaderUtil.createWSSecurityHeader();
        }

        Node encryptionNode = null;
        NodeList list = document.getDocumentElement().getChildNodes();
        // document.getElementsByTagNameNS(SIGNATURE_NS, KEYINFO_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            Node tempNode = list.item(i);
            if (tempNode.getNamespaceURI().equalsIgnoreCase(ENCRYPTION_NS)
                    && tempNode.getLocalName().equalsIgnoreCase(ENCKEY_ELEMENT)) {
                document.getFirstChild().removeChild(tempNode);
            }

        }

        encryptionNode = document.importNode(encryption, true);
        document.getFirstChild().appendChild(encryptionNode);
        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());
        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }
    }

    /**
     * Sets the signature in exchange.
     * 
     * @param me
     *        message exchange
     * @param signature
     *        signature
     * 
     * @throws Exception
     *         exception
     */
    public void setSignatureInExchange(final NormalizedMessage me, final Node signature) throws Exception {
        Document document = null;
        document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) {
            document = HeaderUtil.createWSSecurityHeader();
        }

        Node signatureNode = null;
        NodeList list = document.getDocumentElement().getChildNodes();
        // document.getElementsByTagNameNS(SIGNATURE_NS, SIGNATURE_ELEMENT);
        for (int i = 0; i < list.getLength(); i++) {
            Node tempNode = list.item(i);
            if (tempNode.getNamespaceURI().equalsIgnoreCase(SIGNATURE_NS)
                    && tempNode.getLocalName().equalsIgnoreCase(SIGNATURE_ELEMENT)) {
                document.getFirstChild().removeChild(tempNode);
            }
        }

        signatureNode = document.importNode(signature, true);
        document.getFirstChild().appendChild(signatureNode);
        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());
        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }
    }

    /**
     * Sets the usernametoken in exchange.
     * 
     * @param me
     *        message exchange
     * @param token
     *        assertions
     * 
     * @throws Exception
     *         exception
     */
    public void setUsernametokenInExchange(final NormalizedMessage me, final String token) throws Exception {

        Document document = HeaderUtil.getWSSecurityHeaderFromNM(me);
        if (document == null) {
            document = HeaderUtil.createWSSecurityHeader();
        }

        Node tokenNode = null;
        Document tokenDoc = TransformerUtil.docFromString(token);
        tokenNode = document.importNode(tokenDoc.getDocumentElement(), true);
        document.getFirstChild().appendChild(tokenNode);
        DocumentFragment frag = document.createDocumentFragment();
        frag.appendChild(document.getDocumentElement());
        Map map = (Map) me.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (map == null) {
            map = new HashMap();
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
            me.setProperty(HeaderUtil.HEADER_PROPERTY, map);
        } else {
            map.put(HeaderUtil.WSSECURITY_QNAME.toString(), frag);
        }

    }

}
