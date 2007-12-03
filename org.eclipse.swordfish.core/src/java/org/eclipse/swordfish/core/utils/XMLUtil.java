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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The Class XMLUtil.
 */
public final class XMLUtil {

    /** The Constant pattern. */
    static final String PATTERN = "<\\?xml.+encoding\\s*=\\s*[\"'](.*?)[\"'].*\\?>";

    /** The Constant p. */
    static final Pattern P = Pattern.compile(PATTERN);

    /** The Constant DEFAULT_ENCODING. */
    static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Doc from input stream.
     * 
     * @param is
     *        the is
     * 
     * @return the document
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static Document docFromInputStream(final InputStream is) throws SAXException, IOException {
        try {
            DocumentBuilder db = getDocumentBuilder();
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("XML parser configuration problem", e);
        }
    }

    /**
     * Doc from reader.
     * 
     * @param content
     *        the content
     * 
     * @return the document
     * 
     * @throws SAXException
     * @throws IOException
     */
    public static Document docFromReader(final Reader content) throws SAXException, IOException {
        try {
            DocumentBuilder db = getDocumentBuilder();
            return db.parse(new InputSource(content));
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("XML parser configuration problem", e);
        }
    }

    /**
     * Doc from string.
     * 
     * @param xmlString
     *        the xml string
     * 
     * @return the document
     * 
     * @throws UnsupportedEncodingException
     * @throws SAXException
     */
    public static Document docFromString(final String xmlString) throws UnsupportedEncodingException, SAXException {
        String encoding = getEncoding(xmlString);
        InputStream is = new ByteArrayInputStream(xmlString.getBytes(encoding));
        try {
            DocumentBuilder db = getDocumentBuilder();
            return db.parse(is);
        } catch (IOException e) {
            // cannot happen with a ByteArrayInputStream
            throw new RuntimeException("ByteArrayInputStream throws unexpected IOException", e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("XML parser configuration problem", e);
        }
    }

    /**
     * DOM source from SAX source.
     * 
     * @param source
     *        the source
     * 
     * @return the DOM source
     */
    public static DOMSource domSourceFromSAXSource(final SAXSource source) {
        Transformer trafo;
        try {
            trafo = TransformerFactory.newInstance().newTransformer();
            DOMResult result = new DOMResult();
            trafo.transform(source, result);
            return new DOMSource(result.getNode());
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("XML SAX to DOM mapping problem problem", e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new RuntimeException("XML SAX to DOM mapping problem problem", e);
        } catch (TransformerException e) {
            throw new RuntimeException("XML SAX to DOM mapping problem problem", e);
        }
    }

    /**
     * Gets the document builder.
     * 
     * @return the document builder
     * 
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(XMLUtil.class.getClassLoader());
            return getDocumentBuilderFactory().newDocumentBuilder();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Gets the document builder factory.
     * 
     * @return the document builder factory
     */
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(XMLUtil.class.getClassLoader());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            dbf.setIgnoringElementContentWhitespace(true);
            return dbf;
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Gets the name space for prefix.
     * 
     * @param elem
     *        the elem
     * @param prefix
     *        the prefix
     * 
     * @return the name space for prefix
     */
    public static String getNameSpaceForPrefix(final Element elem, final String prefix) {
        if (elem == null)
            return "";
        else {
            String bla = null;
            int length = elem.getAttribute("xmlns:" + prefix).length();
            if (length > 0) {
                bla = elem.getAttribute("xmlns:" + prefix);
            } else {
                bla = getNameSpaceForPrefix((Element) elem.getParentNode(), prefix);
            }
            return bla;
            /*
             * return elem.getAttribute("xmlns:" + prefix).length() > 0 ? elem
             * .getAttribute("xmlns:" + prefix) : getNameSpaceForPrefix((Element) elem
             * .getParentNode(), prefix);
             */
        }
    }

    /**
     * Input stream from dom.
     * 
     * @param doc
     *        the doc
     * 
     * @return the input stream
     */
    public static InputStream inputStreamFromDom(final org.w3c.dom.Document doc) {
        org.jdom.Document jDoc = new DOMBuilder().build(doc);
        XMLOutputter outputter = new XMLOutputter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            outputter.output(jDoc, out);
        } catch (IOException e) {
            // cannot happen with a ByteArrayOutputStream
            throw new RuntimeException("ByteArrayOutputStream throws unexpected IOException", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Checks if is empty.
     * 
     * @param doc
     *        the doc
     * 
     * @return true, if is empty
     */
    public static boolean isEmpty(final org.w3c.dom.Document doc) {
        return (null == doc.getDocumentElement());
    }

    /**
     * New document.
     * 
     * @return the document
     */
    public static Document newDocument() {
        try {
            return getDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("XML parser configuration problem", e);
        }
    }

    /**
     * String from dom.
     * 
     * @param doc
     *        the doc
     * 
     * @return the string
     */
    public static String stringFromDom(final org.w3c.dom.Document doc) {
        org.jdom.Document jDoc = new DOMBuilder().build(doc);
        XMLOutputter outputter = new XMLOutputter();

        StringWriter writer = new StringWriter();
        try {
            outputter.output(jDoc, writer);
        } catch (IOException e) {
            // cannot happen with a StringWriter
            throw new RuntimeException("StringWriter throws unexpected IOException", e);
        }
        return writer.toString();
    }

    /**
     * Gets the encoding.
     * 
     * @param xmlString
     *        the xml string
     * 
     * @return the encoding
     */
    private static String getEncoding(final String xmlString) {
        Matcher m = P.matcher(xmlString);
        if (m.find())
            return m.group(1);
        else
            return DEFAULT_ENCODING;
    }
}
