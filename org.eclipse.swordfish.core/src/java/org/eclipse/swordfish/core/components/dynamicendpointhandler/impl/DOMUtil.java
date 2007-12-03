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
package org.eclipse.swordfish.core.components.dynamicendpointhandler.impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * some DOM utility methods
 */
public class DOMUtil {

    private static DocumentBuilder builder;

    /**
     * @param frag
     * @param soapNs
     * @param string
     * @return
     */
    public static Element appendNewElement(final DocumentFragment frag, final String namespace, final String elName) {
        Element el = frag.getOwnerDocument().createElementNS(namespace, elName);
        frag.appendChild(el);
        return el;
    }

    /**
     * @param e
     * @param soapNs
     * @param string
     * @return
     */
    public static Element appendNewElement(final Element e, final String namespace, final String elName) {
        Element el = e.getOwnerDocument().createElementNS(namespace, elName);
        e.appendChild(el);
        return el;
    }

    /**
     * @param frag
     * @param sbbNs
     * @param string
     * @param applicationId
     * @return
     */
    public static Element appendNewElementValue(final DocumentFragment frag, final String namespace, final String elName,
            final String value) {
        Element el = appendNewElement(frag, namespace, elName);
        el.setNodeValue(value);
        return el;
    }

    /**
     * @param e
     * @param soapNs
     * @param string
     * @param contextPath
     */
    public static Element appendNewElementValue(final Element e, final String namespace, final String elName, final String value) {
        Element el = appendNewElement(e, namespace, elName);
        el.setNodeValue(value);
        return el;
    }

    /**
     * @return
     */
    public static Document newDocument() {
        return getBuilder().newDocument();
    }

    /**
     * @param e
     * @param soapNs
     */
    public static void setDefaultNamespace(final Element e, final String soapNs) {
        e.setAttribute("xmlns", soapNs);
    }

    private static DocumentBuilder getBuilder() {
        if (builder == null) {
            try {
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                throw new RuntimeException("Error creating document builder.", ex);
            }
        }
        return builder;
    }

}
