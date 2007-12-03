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

import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.jxpath.CompiledExpression;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.eclipse.swordfish.policytrader.exceptions.UnreadableSourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class DocumentReaderBase.
 * 
 */
public abstract class DocumentReaderBase {

    /** The document. */
    private final Document document;

    /** The path context. */
    private final JXPathContext pathContext;

    /**
     * Instantiates a new document reader base.
     * 
     * @param source
     *        the source
     * @param toolbox
     *        the toolbox
     * 
     * @throws UnreadableSourceException
     * @throws CorruptedSourceException
     */
    protected DocumentReaderBase(final InputStream source, final Toolbox toolbox) throws UnreadableSourceException,
            CorruptedSourceException {
        super();
        this.document = toolbox.streamToDocument(source);
        this.pathContext = JXPathContext.newContext(this.document);
    }

    /**
     * Gets the document.
     * 
     * @return the document
     */
    protected final Document getDocument() {
        return this.document;
    }

    /**
     * Gets the document element.
     * 
     * @return the document element
     */
    protected final Element getDocumentElement() {
        return this.document.getDocumentElement();
    }

    /**
     * Gets the pointer.
     * 
     * @param path
     *        the path
     * 
     * @return the pointer
     */
    protected final Pointer getPointer(final CompiledExpression path) {
        return (Pointer) path.iteratePointers(this.pathContext).next();
    }

    /**
     * Gets the pointer.
     * 
     * @param path
     *        the path
     * 
     * @return the pointer
     */
    protected final Pointer getPointer(final String path) {
        return this.pathContext.getPointer(path);
    }

    /**
     * Gets the value.
     * 
     * @param path
     *        the path
     * 
     * @return the value
     */
    protected final String getValue(final CompiledExpression path) {
        return (String) path.getValue(this.pathContext);
    }

    /**
     * Gets the value.
     * 
     * @param path
     *        the path
     * 
     * @return the value
     */
    protected final String getValue(final String path) {
        return (String) this.pathContext.getValue(path);
    }

    /**
     * Checks if is lenient.
     * 
     * @return true, if is lenient
     */
    protected final boolean isLenient() {
        return this.pathContext.isLenient();
    }

    /**
     * Iterate pointers.
     * 
     * @param path
     *        the path
     * 
     * @return the iterator
     */
    protected final Iterator iteratePointers(final CompiledExpression path) {
        return path.iteratePointers(this.pathContext);
    }

    /**
     * Register namespace.
     * 
     * @param prefix
     *        the prefix
     * @param namespace
     *        the namespace
     */
    protected final void registerNamespace(final String prefix, final String namespace) {
        this.pathContext.registerNamespace(prefix, namespace);
    }

    /**
     * Sets the lenient.
     * 
     * @param lenient
     *        the new lenient
     */
    protected final void setLenient(final boolean lenient) {
        this.pathContext.setLenient(lenient);
    }

    /**
     * Sets the value.
     * 
     * @param path
     *        the path
     * @param value
     *        the value
     */
    protected final void setValue(final CompiledExpression path, final String value) {
        path.setValue(this.pathContext, value);
    }

    /**
     * Sets the value.
     * 
     * @param path
     *        the path
     * @param value
     *        the value
     */
    protected final void setValue(final String path, final String value) {
        this.pathContext.setValue(path, value);
    }
}
