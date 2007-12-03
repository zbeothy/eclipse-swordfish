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
package org.eclipse.swordfish.configrepos.shared.validation.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
                // org.eclipse.swordfish.papi.impl.untyped.payload.OutgoingStringPayload
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
                // org.eclipse.swordfish.papi.impl.untyped.payload.OutgoingStringPayload
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
                // org.eclipse.swordfish.papi.impl.untyped.payload.OutgoingStringPayload
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
                // org.eclipse.swordfish.papi.impl.untyped.payload.OutgoingStringPayload
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
     * This private constructor prevent creation of this class.
     */
    private TransformerUtil() {
    }
}
