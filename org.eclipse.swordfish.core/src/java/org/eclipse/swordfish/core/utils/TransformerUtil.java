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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class contans some util static function for converting Source from/to DOM, String.
 * 
 */
public final class TransformerUtil {

    /** This constant defines length of the buffer. */
    private static final int BUF_LEN = 1024;

    /**
     * Append new element.
     * 
     * @param node
     *        the node
     * @param ns
     *        the ns
     * @param localName
     *        the local name
     * 
     * @return the element
     */
    public static Element appendNewElement(final Node node, final String ns, final String localName) {
        Document doc = (node instanceof Document) ? (Document) node : node.getOwnerDocument();
        Element el = doc.createElementNS(ns, localName);
        node.appendChild(el);
        return el;
    }

    /**
     * Append new element value.
     * 
     * @param node
     *        the node
     * @param name
     *        the name
     * @param value
     *        the value
     * 
     * @return the element
     */
    public static Element appendNewElementValue(final Node node, final QName name, final String value) {
        Element el = appendNewElement(node, name.getNamespaceURI(), name.getLocalPart());
        setTextData(el, value);
        return el;
    }

    /**
     * Append new element value.
     * 
     * @param node
     *        the node
     * @param ns
     *        the ns
     * @param localName
     *        the local name
     * @param value
     *        the value
     * 
     * @return the element
     */
    public static Element appendNewElementValue(final Node node, final String ns, final String localName, final QName value) {
        Element el = appendNewElement(node, ns, localName);
        setTextData(el, value);
        return el;
    }

    /**
     * Append new element value.
     * 
     * @param node
     *        the node
     * @param ns
     *        the ns
     * @param localName
     *        the local name
     * @param value
     *        the value
     * 
     * @return the element
     */
    public static Element appendNewElementValue(final Node node, final String ns, final String localName, final String value) {
        Document doc = (node instanceof Document) ? (Document) node : node.getOwnerDocument();
        Element el = doc.createElementNS(ns, localName);
        node.appendChild(el);
        setTextData(el, value);
        return el;
    }

    /**
     * Copy contents to.
     * 
     * @param from
     *        the from
     * @param to
     *        the to
     */
    public static void copyContentsTo(final Node from, final Node to) {
        if (from instanceof Element) {
            Element ef = (Element) from;
            Element toe = (Element) to;
            NamedNodeMap nnm = ef.getAttributes();
            if (nnm != null) {
                int l = nnm.getLength();
                for (int i = 0; i < l; i++) {
                    Attr a = (Attr) nnm.item(i);
                    toe.setAttributeNS(a.getNamespaceURI(), a.getNodeName(), a.getValue());
                }

            }
        }
        for (Node c = from.getFirstChild(); c != null; c = c.getNextSibling()) {
            Node cc = cloneNode(c, to.getOwnerDocument());
            to.appendChild(cc);
        }

    }

    /**
     * This method creates fault message from the string message.
     * 
     * @param code
     *        the code
     * @param message
     *        the message
     * @param actor
     *        the actor
     * @param detail
     *        the detail
     * 
     * @return the fault message as DOMSource
     */
    public static Source createFault(final String code, final String message, final String actor, final String detail) {
        StringBuffer sf = new StringBuffer();
        sf.append("<soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><faultcode>");
        sf.append(code);
        sf.append("</faultcode><faultstring>");
        sf.append(message);
        sf.append("</faultstring><faultactor>");
        sf.append(actor);
        sf.append("</faultactor><detail>");
        sf.append(detail);
        sf.append("</detail></soap:Fault>");
        return domFromString(sf.toString());
    }

    /**
     * This method creates fault message from the exception.
     * 
     * @param e
     *        the exception
     * 
     * @return the fault message as DOMSource
     */
    public static Source createFault(final Throwable e) {
        Writer wr = new StringWriter();
        PrintWriter ps = new PrintWriter(wr);
        e.printStackTrace(ps);
        return createFault(e.getClass().getName(), e.getMessage(), "", wr.toString());
    }

    /**
     * This method converts InputSource representation of the xml into the DOMSource.
     * 
     * @param src
     *        the InputSource
     * 
     * @return the DOMSource
     */
    public static Document docFromInputSource(final InputSource src) {
        Document res = null;
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(TransformerUtil.class.getClassLoader());
            DocumentBuilderFactory factory = getDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            res = builder.parse(src);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        return res;
    }

    /**
     * This method converts InputStream representation of the xml into the DOMSource.
     * 
     * @param src
     *        the input xml string
     * 
     * @return the DOMSource
     */
    public static Document docFromInputStream(final InputStream src) {
        Document res = null;
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(TransformerUtil.class.getClassLoader());
            DocumentBuilderFactory factory = getDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            res = builder.parse(src);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        return res;
    }

    /**
     * This method converts Source representation of the xml into the DOMSource.
     * 
     * @param src
     *        the input Source
     * 
     * @return the DOMSource
     */
    public static Document docFromSource(final Source src) {
        if (src instanceof DOMSource)
            // TODO !!! owner document can be more then node
            return docFromString(stringFromDomNode(((DOMSource) src).getNode()));
        else if (src instanceof SAXSource) {
            SAXSource saxSrc = (SAXSource) src;
            return docFromInputSource(saxSrc.getInputSource());
        } else if (src instanceof StreamSource) {
            StreamSource streamSrc = (StreamSource) src;
            Document res = null;
            if (null != streamSrc.getInputStream()) {
                res = docFromInputStream(streamSrc.getInputStream());
            } else if (null != streamSrc.getReader()) {
                // TODO to the PAPI-impl
                // org.eclipse.swordfish.core.papi.impl.untyped.payload.OutgoingStringPayload
                // in method createContentSource if using
                // StringBufferedInputStream instead of StringReader
                // will be one "wrapper" less between string payload and the
                // xerces.
                res = docFromInputSource(new InputSource(streamSrc.getReader()));
            }
            return res;
        } else
            return null;

    }

    /**
     * This method converts string representation of the xml into the DOMSource.
     * 
     * @param src
     *        the input xml string
     * 
     * @return the DOMSource
     */
    public static Document docFromString(final String src) {
        Document res = null;
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(TransformerUtil.class.getClassLoader());
            InputStream is = new ByteArrayInputStream(src.getBytes());
            DocumentBuilderFactory factory = getDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            res = builder.parse(is);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        return res;
    }

    /**
     * This method converts InputSource representation of the xml into the DOMSource.
     * 
     * @param src
     *        the InputSource
     * 
     * @return the DOMSource
     */
    public static DOMSource domFromInputSource(final InputSource src) {
        Document target = docFromInputSource(src);
        return new DOMSource(target.getDocumentElement());
    }

    /**
     * This method converts InputStream representation of the xml into the DOMSource.
     * 
     * @param src
     *        the input xml string
     * 
     * @return the DOMSource
     */
    public static DOMSource domFromInputStream(final InputStream src) {
        Document target = docFromInputStream(src);
        return new DOMSource(target.getDocumentElement());
    }

    /**
     * This method converts Source representation of the xml into the DOMSource.
     * 
     * @param src
     *        the input Source
     * 
     * @return the DOMSource
     */
    public static DOMSource domFromSource(final Source src) {
        if (src instanceof DOMSource)
            return (DOMSource) src;
        else if (src instanceof SAXSource) {
            SAXSource saxSrc = (SAXSource) src;
            DOMSource res = domFromInputSource(saxSrc.getInputSource());
            return res;
        } else if (src instanceof StreamSource) {
            StreamSource streamSrc = (StreamSource) src;
            DOMSource res = null;
            if (null != streamSrc.getInputStream()) {
                res = domFromInputStream(streamSrc.getInputStream());
            } else if (null != streamSrc.getReader()) {
                // TODO to the PAPI-impl
                // org.eclipse.swordfish.core.papi.impl.untyped.payload.OutgoingStringPayload
                // in method createContentSource if using
                // StringBufferedInputStream instead of StringReader
                // will be one "wrapper" less between string payload and the
                // xerces.
                res = domFromInputSource(new InputSource(streamSrc.getReader()));
            }
            return res;
        } else
            return null;
    }

    /**
     * This method converts string representation of the xml into the DOMSource.
     * 
     * @param src
     *        the input xml string
     * 
     * @return the DOMSource
     */
    public static DOMSource domFromString(final String src) {
        Document target = docFromString(src);
        return new DOMSource(target.getDocumentElement());
    }

    /**
     * Gets the document builder.
     * 
     * @return DocumentBuilder document builder
     * 
     * @throws ParserConfigurationException
     *         ParserConfigurationException
     */
    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(TransformerUtil.class.getClassLoader());
            DocumentBuilderFactory myBuilderFactory = getDocumentBuilderFactory();
            return myBuilderFactory.newDocumentBuilder();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Gets the document builder factory.
     * 
     * @return -- an instance of a non-validating but namespace aware DocumentBuilder that ignores
     *         ignoreable white spaces
     */
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(TransformerUtil.class.getClassLoader());
            DocumentBuilderFactory myBuilderFactory = DocumentBuilderFactory.newInstance();
            myBuilderFactory.setValidating(false);
            myBuilderFactory.setNamespaceAware(true);
            myBuilderFactory.setIgnoringElementContentWhitespace(true);
            return myBuilderFactory;
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Gets the or create prefix for namespace.
     * 
     * @param e
     *        the e
     * @param ns
     *        the ns
     * @param seedPrefix
     *        the seed prefix
     * 
     * @return the or create prefix for namespace
     */
    public static String getOrCreatePrefixForNamespace(final Element e, final String ns, final String seedPrefix) {
        String pfx = getPrefixForNamespace(e, ns);
        if (pfx != null)
            return pfx;
        else {
            String newprefix = inventPrefix(e, seedPrefix);
            setNamespaceOnRoot(e, newprefix, ns);
            return newprefix;
        }
    }

    /**
     * Gets the prefix for namespace.
     * 
     * @param e
     *        the e
     * @param ns
     *        the ns
     * 
     * @return the prefix for namespace
     */
    public static String getPrefixForNamespace(final Element e, final String ns) {
        String localPrefix = findLocalPrefixForNamespace(e, ns);
        if (localPrefix != null) return localPrefix;
        Node p = e.getParentNode();
        if (p instanceof Element)
            return getPrefixForNamespace((Element) p, ns);
        else
            return null;
    }

    /**
     * This method converts string representation of the xml into the DOMSource.
     * 
     * @param src
     *        the input xml string
     * 
     * @return the DOMSource
     */
    public static InputSource inputSourceFromSource(final Source src) {
        InputSource res = null;
        if (src instanceof InputSource)
            return (InputSource) src;
        else if (src instanceof SAXSource) {
            SAXSource saxSrc = (SAXSource) src;
            return saxSrc.getInputSource();
        } else if (src instanceof DOMSource) {
            DOMSource domSrc = (DOMSource) src;
            String xml = stringFromDomNode(domSrc.getNode());
            res = new InputSource(new StringReader(xml));
            return res;
        } else if (src instanceof StreamSource) {
            StreamSource streamSrc = (StreamSource) src;
            if (null != streamSrc.getInputStream()) {
                res = new InputSource(streamSrc.getInputStream());
            } else if (null != streamSrc.getReader()) {
                // TODO to the PAPI-impl
                // org.eclipse.swordfish.core.papi.impl.untyped.payload.OutgoingStringPayload
                // in method createContentSource if using
                // StringBufferedInputStream instead of StringReader
                // will be one "wrapper" less between string payload and the
                // xerces.
                res = new InputSource(streamSrc.getReader());
            }
            return res;
        }
        return res;
    }

    // --- some borrowed methods

    /**
     * Insert after.
     * 
     * @param insert
     *        the insert
     * @param node
     *        the node
     */
    public static void insertAfter(final Node insert, final Node node) {
        Node p = node.getParentNode();
        Node next = node.getNextSibling();
        if (next == null) {
            p.appendChild(insert);
        } else {
            p.insertBefore(insert, next);
        }
    }

    /**
     * Insert before.
     * 
     * @param insert
     *        the insert
     * @param node
     *        the node
     */
    public static void insertBefore(final Node insert, final Node node) {
        Node p = node.getParentNode();
        p.insertBefore(insert, node);
    }

    /**
     * Insert text after.
     * 
     * @param text
     *        the text
     * @param node
     *        the node
     */
    public static void insertTextAfter(final String text, final Node node) {
        Document d = node.getOwnerDocument();
        Text tn = d.createTextNode(text);
        insertAfter(tn, node);
    }

    /**
     * Insert text before.
     * 
     * @param text
     *        the text
     * @param node
     *        the node
     */
    public static void insertTextBefore(final String text, final Node node) {
        Document d = node.getOwnerDocument();
        Text tn = d.createTextNode(text);
        insertBefore(tn, node);
    }

    /**
     * Checks if is prefix used.
     * 
     * @param e
     *        the e
     * @param prefix
     *        the prefix
     * 
     * @return true, if is prefix used
     */
    public static boolean isPrefixUsed(final Element e, final String prefix) {
        Element element = e;
        do {
            Attr v = element.getAttributeNodeNS("http://www.w3.org/2000/xmlns/", prefix);
            if (v != null) return true;
            Node p = element.getParentNode();
            if (p.getNodeType() == 1) {
                element = (Element) p;
            } else
                return false;
        } while (true);
    }

    /**
     * This method checks whether the source passed is empty If the source is with empty document,
     * the result will be true.
     * 
     * @param src
     *        Source
     * 
     * @return boolean result
     */
    public static boolean isSourceEmpty(final Source src) {

        if (src == null) return true;

        Node node = ((DOMSource) src).getNode();

        if (node == null) return true;

        String strSource = stringFromDomNode(node);
        if ((strSource == null) || (strSource.trim().length() == 0)) return true;
        return false;
    }

    /**
     * Removes the text nodes after.
     * 
     * @param node
     *        the node
     */
    public static void removeTextNodesAfter(final Node node) {
        for (Node p = node.getNextSibling(); p instanceof Text;) {
            Node t = p;
            p = p.getNextSibling();
            t.getParentNode().removeChild(t);
        }

    }

    /**
     * Removes the text nodes before.
     * 
     * @param node
     *        the node
     */
    public static void removeTextNodesBefore(final Node node) {
        for (Node p = node.getPreviousSibling(); p instanceof Text;) {
            Node t = p;
            p = p.getPreviousSibling();
            t.getParentNode().removeChild(t);
        }

    }

    /**
     * Sets the default namespace.
     * 
     * @param element
     *        the element
     * @param namespace
     *        the namespace
     */
    public static void setDefaultNamespace(final Element element, final String namespace) {
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", namespace);
    }

    /**
     * Sets the namespace.
     * 
     * @param element
     *        the element
     * @param prefix
     *        the prefix
     * @param namespace
     *        the namespace
     */
    public static void setNamespace(final Element element, final String prefix, final String namespace) {
        if ((prefix == null) || (prefix.length() == 0)) {
            setDefaultNamespace(element, namespace);
        } else {
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespace);
        }
    }

    /**
     * Sets the namespace on root.
     * 
     * @param element
     *        the element
     * @param prefix
     *        the prefix
     * @param namespace
     *        the namespace
     */
    public static void setNamespaceOnRoot(final Element element, final String prefix, final String namespace) {
        Element elm = element;
        for (; elm.getParentNode().getNodeType() == 1; elm = (Element) elm.getParentNode()) {
            System.out.print("");
        }
        setNamespace(elm, prefix, namespace);
    }

    /**
     * Sets the text data.
     * 
     * @param element
     *        the element
     * @param data
     *        the data
     */
    public static void setTextData(final Element element, final QName data) {
        String prefix;
        if ((data.getNamespaceURI() == null) || (data.getNamespaceURI().length() == 0)) {
            prefix = null;
        } else {
            prefix = getOrCreatePrefixForNamespace(element, data.getNamespaceURI(), data.getPrefix());
        }
        if ((prefix == null) || (prefix.length() == 0)) {
            setTextData(element, data.getLocalPart());
        } else {
            setTextData(element, prefix + ":" + data.getLocalPart());
        }

    }

    /**
     * Sets the text data.
     * 
     * @param aElement
     *        the a element
     * @param aData
     *        the a data
     */
    public static void setTextData(final Element aElement, final String aData) {
        getOrCreateText(aElement).setData(aData);
    }

    /**
     * This method converts string representation of the xml into the String.
     * 
     * @param node
     *        the input xml string
     * 
     * @return the xml string
     */
    public static String stringFromDomNode(final Node node) {
        return stringFromDomNode(node, true);
    }

    // --- maybe we can clean it from here

    /**
     * This method converts string representation of the xml into the String.
     * 
     * @param node
     *        the input xml string
     * @param xmlDecl
     *        if true then xml declaration will be omitted
     * 
     * @return the xml string
     */
    public static String stringFromDomNode(final Node node, final boolean xmlDecl) {
        String res = null;
        try {
            if (node != null) {
                // // Create a StringWriter object to pass to the FormatterToXML
                // // instance
                // java.io.StringWriter writer = new java.io.StringWriter();
                //
                // // Uses the Xerces Serializer implementation XMLSerializer
                // OutputFormat of = new OutputFormat();
                // of.setOmitXMLDeclaration(!xmlDecl);
                // XMLSerializer serializer = new XMLSerializer(writer, of);
                // if (node instanceof org.w3c.dom.Document) {
                // serializer.serialize((org.w3c.dom.Document) node);
                // } else {
                // serializer.serialize((org.w3c.dom.Element) node);
                // }
                // // Convert the contents of the StringWriter into a String
                res = DOM2Writer.nodeToString(node, xmlDecl);
            }
        } catch (Exception e) {
            res = null;
            throw new RuntimeException(e);
        }
        return res;
    }

    /**
     * This method converts InputSource representation of the xml into the String.
     * 
     * @param src
     *        the InputSource
     * 
     * @return the DOMSource
     */
    public static String stringFromInputSource(final InputSource src) {
        StringBuffer res = new StringBuffer();
        char[] buf = new char[BUF_LEN];
        try {
            Reader rd = src.getCharacterStream();
            int readed = 0;
            do {
                readed = rd.read(buf);
                if (readed > 0) {
                    res.append(buf, 0, readed);
                }
            } while (readed > 0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res.toString();
    }

    /**
     * This method converts InputStream representation of the xml into the DOMSource.
     * 
     * @param src
     *        the InputStream
     * 
     * @return the DOMSource
     */
    public static String stringFromInputStream(final InputStream src) {
        StringBuffer res = new StringBuffer();
        char[] buf = new char[BUF_LEN];
        try {
            int readed = 0;
            Reader rd = new InputStreamReader(src);
            do {
                readed = rd.read(buf);
                if (readed > 0) {
                    res.append(buf, 0, readed);
                }
            } while (readed > 0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res.toString();
    }

    /**
     * This method converts string representation of the xml into the DOMSource.
     * 
     * @param src
     *        the input Source
     * 
     * @return the xml string
     */
    public static String stringFromSource(final Source src) {
        if (src instanceof DOMSource)
            return stringFromDomNode(((DOMSource) src).getNode());
        else if (src instanceof SAXSource)
            return stringFromInputSource(((SAXSource) src).getInputSource());
        else if (src instanceof StreamSource) {
            StreamSource streamSrc = (StreamSource) src;
            String res = null;
            if (null != streamSrc.getInputStream()) {
                res = stringFromInputStream(streamSrc.getInputStream());
            } else if (null != streamSrc.getReader()) {
                // TODO to the PAPI-impl
                // org.eclipse.swordfish.core.papi.impl.untyped.payload.OutgoingStringPayload
                // in method createContentSource if using
                // StringBufferedInputStream instead of StringReader
                // will be one "wrapper" less between string payload and the
                // xerces.
                res = stringFromInputSource(new InputSource(streamSrc.getReader()));
            }
            return res;
        } else
            return null;
    }

    /**
     * Clone node.
     * 
     * @param from
     *        the from
     * @param toDoc
     *        the to doc
     * 
     * @return the node
     */
    private static Node cloneNode(final Node from, final Document toDoc) {
        if (from instanceof Element) {
            Element e = toDoc.createElementNS(from.getNamespaceURI(), from.getNodeName());
            copyContentsTo(from, e);
            return e;
        }
        if (from instanceof Text) {
            Text t = toDoc.createTextNode(((Text) from).getData());
            return t;
        }
        if (from instanceof Comment) {
            Comment t = toDoc.createComment(((Comment) from).getData());
            return t;
        }
        if (from instanceof ProcessingInstruction) {
            ProcessingInstruction pi = (ProcessingInstruction) from;
            ProcessingInstruction t = toDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
            return t;
        } else
            throw new RuntimeException("Unknown node class:" + from.getClass());
    }

    /**
     * Find local prefix for namespace.
     * 
     * @param e
     *        the e
     * @param ns
     *        the ns
     * 
     * @return the string
     */
    private static String findLocalPrefixForNamespace(final Element e, final String ns) {
        NamedNodeMap attrs = e.getAttributes();
        if (attrs == null) return null;
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(0);
            if (attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/") && attr.getValue().equals(ns))
                return attr.getLocalName();
        }

        return null;
    }

    /**
     * Gets the or create text.
     * 
     * @param aElement
     *        the a element
     * 
     * @return the or create text
     */
    private static Text getOrCreateText(final Element aElement) {
        aElement.normalize();
        Node node = aElement.getFirstChild();
        if ((node == null) || !(node instanceof Text)) {
            node = aElement.getOwnerDocument().createTextNode("");
            aElement.appendChild(node);
        }
        return (Text) node;
    }

    /**
     * Invent prefix.
     * 
     * @param e
     *        the e
     * @param seedPrefix
     *        the seed prefix
     * 
     * @return the string
     */
    private static String inventPrefix(final Element e, final String seedPrefix) {
        String base = (seedPrefix != null) && (seedPrefix.length() != 0) ? seedPrefix : "ns";
        int i = 0;
        do {
            String tryPrefix = base + i;
            if (!isPrefixUsed(e, tryPrefix)) return tryPrefix;
            i++;
        } while (true);
    }

    /**
     * This private constructor prevent creation of this class.
     */
    private TransformerUtil() {
    }

}
