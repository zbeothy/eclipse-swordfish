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
package org.eclipse.swordfish.core.components.headerprocessing.impl;

import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.SAXException;

/**
 * The Class JDOMUtil.
 */
public class JDOMUtil {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(JDOMUtil.class);

    /** The my builder factory. */
    private DocumentBuilderContainer builderContainer;

    /**
     * Instantiates a new JDOM util.
     */
    public JDOMUtil() {
        this.builderContainer = new DocumentBuilderContainer();
    }

    /**
     * Adds the element if not null.
     * 
     * @param target
     *        the target
     * @param tag
     *        the tag
     * @param namespace
     *        the namespace
     * @param value
     *        the value
     */
    public void addElementIfNotNull(final Element target, final String tag, final Namespace namespace, final String value) {
        if ((target != null) && (tag != null) && (namespace != null) && (value != null)) {
            Element elem = new Element(tag, namespace);
            elem.setText(value);
            target.addContent(elem);
        }
    }

    /**
     * Builds the document fragment.
     * 
     * @param doc
     *        the doc
     * 
     * @return the document fragment
     * 
     * @throws SAXException
     * @throws IOException
     */
    public DocumentFragment buildDocumentFragment(final Document doc) throws SAXException, IOException {
        org.w3c.dom.Document w3cdoc = this.getBuilder().parse((new JDOMSource(doc)).getInputSource());
        DocumentFragment frag = w3cdoc.createDocumentFragment();
        frag.appendChild(w3cdoc.getDocumentElement());
        return frag;
    }

    /**
     * Builds the document fragment.
     * 
     * @param elem
     *        the elem
     * 
     * @return the document fragment
     * 
     * @throws SAXException
     * @throws IOException
     */
    public DocumentFragment buildDocumentFragment(final Element elem) throws SAXException, IOException {
        org.w3c.dom.Document w3cdoc = this.getBuilder().parse((new JDOMSource(elem)).getInputSource());
        DocumentFragment frag = w3cdoc.createDocumentFragment();
        frag.appendChild(w3cdoc.getDocumentElement());
        return frag;
    }

    /**
     * Builds the document from element.
     * 
     * @param elem
     *        the elem
     * 
     * @return the document
     * 
     * @throws IOException
     * @throws SAXException
     */
    public org.w3c.dom.Document buildDocumentFromElement(final Element elem) throws SAXException, IOException {

        return this.getBuilder().parse((new JDOMSource(elem)).getInputSource());
    }

    /**
     * Builds the document from fragment.
     * 
     * @param frag
     *        the frag
     * 
     * @return the org.w3c.dom. document
     * 
     * @throws SAXException
     * @throws IOException
     */
    public org.w3c.dom.Document buildDocumentFromFragment(final DocumentFragment frag) throws SAXException, IOException {
        return this.getBuilder().parse((new JDOMSource(this.fragmentToElement(frag))).getInputSource());
    }

    /**
     * Builds the string fragment.
     * 
     * @param tag
     *        the tag
     * @param prefix
     *        the prefix
     * @param namespace
     *        the namespace
     * @param value
     *        the value
     * 
     * @return the document fragment
     * 
     * @throws SAXException
     * @throws IOException
     */
    public DocumentFragment buildStringFragment(final String tag, final String prefix, final String namespace, final String value)
            throws SAXException, IOException {
        Element elem =
                new Element(tag, prefix == null ? Namespace.getNamespace(namespace) : Namespace.getNamespace(prefix, namespace));
        elem.setText(value);
        return this.buildDocumentFragment(elem);
    }

    /**
     * Dom document to document.
     * 
     * @param doc
     *        the doc
     * 
     * @return the document
     */
    public Document domDocumentToDocument(final org.w3c.dom.Document doc) {
        return new DOMBuilder().build(doc);
    }

    /**
     * Fragment to element.
     * 
     * @param fragment
     *        the fragment
     * 
     * @return the element
     */
    public Element fragmentToElement(final DocumentFragment fragment) {
        if (fragment != null)
            return new DOMBuilder().build((org.w3c.dom.Element) fragment.getFirstChild());
        else
            return null;
    }

    /**
     * Gets the name NS.
     * 
     * @param elem
     *        the elem
     * 
     * @return the name NS
     */
    public String getNameNS(final Element elem) {
        StringBuffer sb = new StringBuffer("{");
        sb.append(elem.getNamespaceURI());
        sb.append("}");
        sb.append(elem.getName());
        return sb.toString();
    }

    /**
     * Gets the string value.
     * 
     * @param idFrag
     *        the id frag
     * @param tag
     *        the tag
     * @param prefix
     *        the prefix
     * @param namespace
     *        the namespace
     * 
     * @return the string value
     */
    public String getStringValue(final DocumentFragment idFrag, final String tag, final String prefix, final String namespace) {
        Element elem = this.fragmentToElement(idFrag);
        if (elem != null)
            return elem.getText();
        else {
            LOG.debug("Unable to retrieve string value for tag " + prefix + ":" + tag + " from fragment " + idFrag);
            return null;
        }
    }

    /**
     * Pretty printed.
     * 
     * @param doc
     *        the doc
     * 
     * @return the string
     */
    public String prettyPrinted(final Document doc) {
        try {
            StringWriter sw = new StringWriter();
            new XMLOutputter(Format.getPrettyFormat()).output(doc, sw);
            return sw.toString();
        } catch (IOException e) {

            e.printStackTrace();
            return "<nil/>";
        }
    }

    /**
     * Pretty printed.
     * 
     * @param fragment
     *        the fragment
     * 
     * @return the string
     */
    public String prettyPrinted(final DocumentFragment fragment) {
        return this.prettyPrinted(this.fragmentToElement(fragment));
    }

    /**
     * Pretty printed.
     * 
     * @param element
     *        the element
     * 
     * @return the string
     */
    public String prettyPrinted(final Element element) {
        if (element != null) {
            try {
                StringWriter sw = new StringWriter();
                new XMLOutputter(Format.getPrettyFormat()).output(element, sw);
                return sw.toString();
            } catch (IOException e) {
                LOG.info("Exception during pretty-printing", e);
            }
        }
        return "<nil/>";
    }

    private DocumentBuilder getBuilder() {
        return (DocumentBuilder) this.builderContainer.get();
    }

    /**
     * The Class DocumentBuilderContainer.
     */
    private class DocumentBuilderContainer extends ThreadLocal {

        @Override
        protected Object initialValue() {
            try {
                return XMLUtil.getDocumentBuilder();
            } catch (ParserConfigurationException e) {
                LOG.error("Unexpected exception when creating JAXP document builder", e);
                return null;
            }
        }
    }

}
