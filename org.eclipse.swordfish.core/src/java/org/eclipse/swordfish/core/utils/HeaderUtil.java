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
package org.eclipse.swordfish.core.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * this class is used to produce Document fragments that can be used as SOAP headers <br>
 * TODO rethink the fact of this class becoming a Bean so some properties are configurable. This
 * means refactoring to helpers package also
 */
public final class HeaderUtil {

    // SOAP Namespace URI
    /** The Constant SOAP_NS. */
    public static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";

    // TODO is this a correct NS?
    /** The Constant SBB_NS. */
    public static final String SBB_NS = "http://types.sopware.org/messaging/headers/1.0";

    // namespace for ws security
    /** The Constant WSSECURITY_NS. */
    public static final String WSSECURITY_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

    // name for ws security element
    /** The Constant WSSECURITY_ELEMENT. */
    public static final String WSSECURITY_ELEMENT = "Security";

    // qname for ws security element
    /** The Constant WSSECURITY_QNAME. */
    public static final QName WSSECURITY_QNAME = new QName(WSSECURITY_NS, WSSECURITY_ELEMENT);

    /** The Constant HEADER_PROPERTY. */
    static public final String HEADER_PROPERTY = "javax.jbi.messaging.protocol.headers";

    /** a static document builder for this instance. */
    private static DocumentBuilder docBuilder;

    // TODO is it okay to have a document builder here like this?
    static {
        try {
            docBuilder = TransformerUtil.getDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException("Shouldn't happen");
        }
    }

    /**
     * returns a participant Identity out of a normalized message as far as the header is present.
     * If the header is not present or does not have the required information this method will
     * return null.
     * 
     * @return -- an object that is a participant identity
     * 
     * public static InternalParticipantIdentity getParticipantIdentityFromNM( final
     * NormalizedMessage nm) { if (nm != null) { Map map = (Map) nm.getProperty(HEADER_PROPERTY);
     * DocumentFragment frag; if (map != null) { frag = (DocumentFragment) map.get("{" + SBB_NS +
     * "}" + "InternalParticipantIdentity"); if (frag != null) { InnerParticipantIdentity
     * innerParticipantIdentity = new InnerParticipantIdentity( frag); if
     * (innerParticipantIdentity.getApplicationID() == null &&
     * innerParticipantIdentity.getInstanceID() == null) { return
     * InternalParticipantIdentity.identity; } else { return innerParticipantIdentity; } } } }
     * return null; }
     */
    /**
     * Creates header for WS Security.
     * 
     * @return DocumentFragement the created document
     */
    public static Document createWSSecurityHeader() {
        Document doc = docBuilder.newDocument();
        Element root = doc.createElementNS(WSSECURITY_NS, WSSECURITY_ELEMENT);
        // root.setAttribute("xmlns:wsse", WSSECURITY_NS);
        doc.appendChild(root);
        return doc;
    }

    /**
     * Gets the call context extension.
     * 
     * @param exchange
     *        the exchange
     * 
     * @return the call context extension
     */
    public static CallContextExtension getCallContextExtension(final MessageExchange exchange) {
        return (CallContextExtension) exchange.getProperty(ExchangeProperties.CALL_CONTEXT);
    }

    /**
     * Gets the latest valid normalized message.
     * 
     * @param exchange
     *        the exchange
     * @param scope
     *        the scope
     * 
     * @return -- the latest valid normalized message in the current scope or null if the mep is
     *         null (this happend for instance after deserialization)
     */
    public static NormalizedMessage getLatestValidNormalizedMessage(final MessageExchange exchange, final Scope scope) {
        if (exchange != null) {
            if (exchange.getError() != null) return exchange.getMessage("in");
            if (exchange.getFault() != null) return exchange.getFault();
            if (exchange instanceof InOut) {
                if (Scope.REQUEST.equals(scope))
                    return exchange.getMessage("in");
                else
                    return exchange.getMessage("out");
            }
            if (exchange instanceof InOnly) return exchange.getMessage("in");
            return null;
        } else
            return null;
    }

    /**
     * Gets the WS security header from NM.
     * 
     * @param nm
     *        normalized message
     * 
     * @return DocumentFragment wssecurity header
     */
    public static Document getWSSecurityHeaderFromNM(final NormalizedMessage nm) {
        if (nm != null) {
            Map map = (Map) nm.getProperty(HEADER_PROPERTY);
            DocumentFragment frag;
            if (map != null) {
                frag = (DocumentFragment) map.get("{" + WSSECURITY_NS + "}" + WSSECURITY_ELEMENT);
                if (frag == null) {
                    frag = (DocumentFragment) map.get(HeaderUtil.WSSECURITY_ELEMENT);
                }
                if (frag != null) {
                    Node firstChild = frag.getFirstChild();
                    Document wsDocument = docBuilder.newDocument();
                    Node imported = wsDocument.importNode(firstChild, true);
                    wsDocument.appendChild(imported);
                    return wsDocument;
                }
            }
        }
        return null;
    }

    /**
     * Prints the DF.
     * 
     * @param df
     *        the df
     */
    public static void printDF(final DocumentFragment df) {
        OutputFormat format = new OutputFormat();
        format.setOmitXMLDeclaration(true);
        format.setIndenting(true);
        format.setStandalone(false);
        format.setIndent(3);
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        try {
            serial.serialize(df);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(stringOut.toString());
    }

    /**
     * Prints the doc.
     * 
     * @param df
     *        the df
     */
    public static void printDoc(final Document df) {
        OutputFormat format = new OutputFormat();
        format.setOmitXMLDeclaration(true);
        format.setIndenting(true);
        format.setStandalone(false);
        format.setIndent(3);
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        try {
            serial.serialize(df);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(stringOut.toString());
    }

    /**
     * Prints the element.
     * 
     * @param df
     *        the df
     */
    public static void printElement(final Element df) {
        OutputFormat format = new OutputFormat();
        format.setOmitXMLDeclaration(true);
        format.setIndenting(true);
        format.setStandalone(false);
        format.setIndent(3);
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        try {
            serial.serialize(df);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(stringOut.toString());
    }

    /**
     * Sets the call context extension.
     * 
     * @param exchange
     *        the exchange
     * @param ctxe
     *        the ctxe
     */
    public static void setCallContextExtension(final MessageExchange exchange, final CallContextExtension ctxe) {
        exchange.setProperty(ExchangeProperties.CALL_CONTEXT, ctxe);
    }

    /**
     * No instances of this class please.
     */
    private HeaderUtil() {
    }
}
