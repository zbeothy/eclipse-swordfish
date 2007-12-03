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
package org.eclipse.swordfish.policytrader.impl;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedEnvironmentException;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.eclipse.swordfish.policytrader.exceptions.UnreadableSourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * XML processing toolbox (must be instantiated).
 */
public class Toolbox {

    /** Factory for DOM building. */
    private DocumentBuilderFactory builderFactory = null;

    /**
     * Standard constructor performing the initialization.
     */
    public Toolbox() {
        super();
        this.initDocumentBuilderFactory();
    }

    /**
     * This method converts string representation of the xml into the DOM.
     * 
     * @param xml
     *        the xml
     * 
     * @return the DOMSource
     * 
     * @throws UnreadableSourceException
     *         on IO problems
     * @throws CorruptedSourceException
     *         on parsing problems
     */
    public Document streamToDocument(final InputStream xml) throws UnreadableSourceException, CorruptedSourceException {
        Document res = null;
        DocumentBuilder builder = this.createDocumentBuilder();
        try {
            res = builder.parse(xml);
        } catch (SAXException e) {
            throw new CorruptedSourceException(e);
        } catch (IOException e) {
            throw new UnreadableSourceException(e);
        }
        return res;
    }

    /**
     * Create a document builder for DOM creation from Strings or streams.
     * 
     * @return -- an instance of a non-validating but namespace aware DocumentBuilder that ignores
     *         ignoreable white spaces
     */
    private DocumentBuilder createDocumentBuilder() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            return this.builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new CorruptedEnvironmentException("Document Builder system corrupted", e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Create a suitable DocumentBuilderFactory.
     * 
     * @return -- an instance of a non-validating but namespace aware DocumentBuilder that ignores
     *         ignoreable white spaces
     */
    private void initDocumentBuilderFactory() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
            bf.setValidating(false);
            bf.setNamespaceAware(true);
            bf.setIgnoringElementContentWhitespace(true);
            this.builderFactory = bf;
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}
