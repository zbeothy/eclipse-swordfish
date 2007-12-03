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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * XMLStreamWriter which writes its output into an empty {@link Document}.
 */
public class XMLStreamToDOMWriter implements XMLStreamWriter {

    /**
     * Create a qualified tag or attribute name from namespace prefix and local name.
     * 
     * @param prefix
     *        prefix String, empty or <code>null</code> for default namespace
     * @param localName
     *        local part of tag or attribute name
     * 
     * @return qualified tag or attribute name
     */
    private static String qualifiedName(final String prefix, final String localName) {
        return zero(prefix) ? localName : prefix + ":" + localName;
    }

    /**
     * Check for zero-length String or <code>null</code>.
     * 
     * @param s
     *        any String or <code>null</code>
     * 
     * @return <code>true</code> for <code>null</code> or empty String, else <code>false</code>
     */
    private static boolean zero(final String s) {
        return ((null == s) || (s.length() == 0));
    }

    /** Document which is about to be built from the output. */
    private final Document document;

    /** Root Element of the document. */
    private Element documentRoot = null;

    /** Element of which the details are currently built. */
    private Element currentElement = null;

    /** Namespace mapping valid in the scope of the current element. */
    private NamespaceMapping currentMapping = null;

    /**
     * Constructor which takes an empty Document.
     * 
     * @param document
     *        empty document with namespace support enabled.
     */
    public XMLStreamToDOMWriter(final Document document) {
        super();
        this.document = document;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#close()
     */
    public void close() throws XMLStreamException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#flush()
     */
    public void flush() throws XMLStreamException {
        // do nothing
    }

    /**
     * Get Document built from output.
     * 
     * @return Document which is built from output
     */
    public Document getDocument() {
        return this.document;
    }

    /*
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#getNamespaceContext()
     */
    public NamespaceContext getNamespaceContext() {
        return this.currentMapping;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#getPrefix(java.lang.String)
     */
    public String getPrefix(final String namespaceURI) throws XMLStreamException {
        return null == this.currentMapping ? null : this.currentMapping.getPrefix(namespaceURI);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#getProperty(java.lang.String)
     */
    public Object getProperty(final String name) throws IllegalArgumentException {
        // no further properties
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#setDefaultNamespace(java.lang.String)
     */
    public void setDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        if (zero(namespaceURI)) {
            if (null != this.currentMapping) {
                this.currentMapping.unregisterDefaultNamespace();
            }
            return;
        }
        if ((null == this.currentMapping) || !namespaceURI.equals(this.currentMapping.getDefaultNamespaceURI())) {
            if ((null == this.currentMapping) || (this.currentElement != this.currentMapping.getElement())) {
                this.currentMapping = new NamespaceMapping(this.currentMapping, this.currentElement);
            }
            this.currentMapping.registerDefaultNamespace(namespaceURI);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#setNamespaceContext(javax.xml.namespace.NamespaceContext)
     */
    public void setNamespaceContext(final NamespaceContext nsContext) throws XMLStreamException {
        if (null != this.documentRoot) throw new IllegalStateException("unsupported after start of document root element");
        this.currentMapping = new NamespaceMapping(nsContext);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#setPrefix(java.lang.String, java.lang.String)
     */
    public void setPrefix(final String nsPrefix, final String nsURI) throws XMLStreamException {
        if (zero(nsURI)) {
            if (null != this.currentMapping) {
                this.currentMapping.unregisterNamespace(nsPrefix);
            }
            return;
        }
        if ((null == this.currentMapping) || !nsURI.equals(this.currentMapping.getNamespaceURI(nsPrefix))) {
            if ((null == this.currentMapping) || (this.currentElement != this.currentMapping.getElement())) {
                this.currentMapping = new NamespaceMapping(this.currentMapping, this.currentElement);
            }
            this.currentMapping.registerNamespace(nsPrefix, nsURI);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String)
     */
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        if (null == this.currentElement) throw new IllegalStateException("no element defined for attribute");
        this.currentElement.setAttribute(localName, value);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        final String prefix = this.checkPrefix(namespaceURI);
        this.writeAttribute(prefix, namespaceURI, localName, value);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value)
            throws XMLStreamException {
        if (null == this.currentElement) throw new IllegalStateException("no element defined for attribute");
        this.currentElement.setAttributeNS(namespaceURI, qualifiedName(prefix, localName), value);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeCData(java.lang.String)
     */
    public void writeCData(final String data) throws XMLStreamException {
        this.checkCurrentElement();
        final CDATASection c = this.document.createCDATASection(data);
        this.currentElement.appendChild(c);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeCharacters(char[], int, int)
     */
    public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
        this.writeCharacters(String.valueOf(text, start, len));
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeCharacters(java.lang.String)
     */
    public void writeCharacters(final String text) throws XMLStreamException {
        this.checkCurrentElement();
        final Text t = this.document.createTextNode(text);
        this.currentElement.appendChild(t);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeComment(java.lang.String)
     */
    public void writeComment(final String data) throws XMLStreamException {
        Comment c = this.document.createComment(data);
        if (null == this.currentElement) {
            this.document.appendChild(c);
        } else {
            this.currentElement.appendChild(c);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeDefaultNamespace(java.lang.String)
     */
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
        if (null == this.currentElement) throw new IllegalStateException("no element defined for attribute");
        this.currentElement.setAttribute("xmlns", namespaceURI);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeDTD(java.lang.String)
     */
    public void writeDTD(final String dtd) throws XMLStreamException {
        throw new XMLStreamException("unsupported");
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String)
     */
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.initElement(localName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String, java.lang.String)
     */
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.initElement(namespaceURI, localName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeEmptyElement(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.initElement(prefix, localName, namespaceURI);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeEndDocument()
     */
    public void writeEndDocument() throws XMLStreamException {
        this.currentElement = null;
        this.currentMapping = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeEndElement()
     */
    public void writeEndElement() throws XMLStreamException {
        if ((null != this.currentMapping) && (this.currentMapping.getElement() == this.currentElement)) {
            this.currentMapping = this.currentMapping.getParent();
        }
        final Node parentNode = this.currentElement.getParentNode();
        if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
            this.currentElement = (Element) parentNode;
        } else {
            this.currentElement = null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeEntityRef(java.lang.String)
     */
    public void writeEntityRef(final String name) throws XMLStreamException {
        final EntityReference e = this.document.createEntityReference(name);
        if (null == this.currentElement) {
            this.document.appendChild(e);
        } else {
            this.currentElement.appendChild(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeNamespace(java.lang.String, java.lang.String)
     */
    public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
        if (null == this.currentElement) throw new IllegalStateException("no element defined for attribute");
        final String attName = zero(prefix) ? "xmlns" : "xmlns:" + prefix;
        this.currentElement.setAttribute(attName, namespaceURI);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String)
     */
    public void writeProcessingInstruction(final String target) throws XMLStreamException {
        final ProcessingInstruction p = this.document.createProcessingInstruction(target, null);
        if (null == this.currentElement) {
            this.document.appendChild(p);
        } else {
            this.currentElement.appendChild(p);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang.String,
     *      java.lang.String)
     */
    public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
        final ProcessingInstruction p = this.document.createProcessingInstruction(target, data);
        if (null == this.currentElement) {
            this.document.appendChild(p);
        } else {
            this.currentElement.appendChild(p);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument()
     */
    public void writeStartDocument() throws XMLStreamException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String)
     */
    public void writeStartDocument(final String version) throws XMLStreamException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeStartDocument(java.lang.String, java.lang.String)
     */
    public void writeStartDocument(final String version, final String encoding) throws XMLStreamException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String)
     */
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.currentElement = this.initElement(localName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String, java.lang.String)
     */
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.currentElement = this.initElement(namespaceURI, localName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.currentElement = this.initElement(prefix, localName, namespaceURI);
    }

    /**
     * Return the current element or throw an exception if no element has yet been started or the
     * root element has already been ended.
     * 
     * @return current element
     * 
     * @throws XMLStreamException
     *         if there is no current element
     */
    private Element checkCurrentElement() throws XMLStreamException {
        if (null == this.currentElement) throw new XMLStreamException("not inside an element");
        return this.currentElement;
    }

    /**
     * Return a prefix for a namespace URI from the namespace mappings hierarchy or throw an
     * exception if none has been mapped.
     * 
     * @param namespaceURI
     *        namespace for which a prefix is searched
     * 
     * @return prefix, empty String for default namespace
     * 
     * @throws XMLStreamException
     *         if no prefix has been mapped
     */
    private String checkPrefix(final String namespaceURI) throws XMLStreamException {
        final String prefix = null == this.currentMapping ? null : this.currentMapping.getPrefix(namespaceURI);
        if (null == prefix) throw new XMLStreamException("no prefix for namespaceURI " + namespaceURI);
        return prefix;
    }

    /**
     * Create and initialize a child element.
     * 
     * @param localName
     *        tag name of the element
     * 
     * @return new element which has been inserted into the DOM tree
     * 
     * @throws XMLStreamException
     *         if the present method is called where it is not possible
     */
    private Element initElement(final String localName) throws XMLStreamException {
        final Element e = this.document.createElement(localName);
        if (null == this.currentElement) {
            if (null != this.documentRoot) throw new XMLStreamException("root element already written");
            this.document.appendChild(e);
            this.documentRoot = e;
        } else {
            this.currentElement.appendChild(e);
        }
        return e;
    }

    /**
     * Create and initialize a child element. The present method will neither update the namespace
     * mapping nor write a namespace declaration to the new element.
     * 
     * @param namespaceURI
     *        namespace URI for which the prefix is searched in the namespace mapping hierarchy.
     * @param localName
     *        local part of the tag name of the element
     * 
     * @return new element which has been inserted into the DOM tree
     * 
     * @throws XMLStreamException
     *         if the present method is called where it is not possible
     */
    private Element initElement(final String namespaceURI, final String localName) throws XMLStreamException {
        final String prefix = this.checkPrefix(namespaceURI);
        return this.initElement(prefix, localName, namespaceURI);
    }

    /**
     * Create and initialize a child element. The present method will neither update the namespace
     * mapping nor write a namespace declaration to the new element.
     * 
     * @param prefix
     *        namespace prefix
     * @param localName
     *        local part of the tag name of the element
     * @param namespaceURI
     *        corresponding namespace URI
     * 
     * @return new element which has been inserted into the DOM tree
     * 
     * @throws XMLStreamException
     *         if the present method is called where it is not possible
     */
    private Element initElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        final Element e = this.document.createElementNS(namespaceURI, qualifiedName(prefix, localName));
        if (null == this.currentElement) {
            if (null != this.documentRoot) throw new XMLStreamException("root element already written");
            this.document.appendChild(e);
            this.documentRoot = e;
        } else {
            this.currentElement.appendChild(e);
        }
        return e;
    }

    /**
     * Helper class for mapping of prefixes to namespace URIs.
     */
    private class NamespaceMapping implements NamespaceContext {

        /** Parent mapping at level of a parent element node. */
        private final NamespaceMapping parent;

        /** NamespaceContext with the preset mappings. */
        private final NamespaceContext context;

        /** Element which forms the scope of the present mapping. */
        private final Element element;

        /** Internal map. */
        private final Map namespaceMap = new HashMap();

        /**
         * Standard constructor for empty top-level mapping.
         */
        protected NamespaceMapping() {
            super();
            this.parent = null;
            this.context = null;
            this.element = null;
        }

        /**
         * Constructor for top-level mapping with preset namespace context.
         * 
         * @param context
         *        context with preset mappings
         */
        protected NamespaceMapping(final NamespaceContext context) {
            super();
            this.parent = null;
            this.context = null;
            this.element = null;
        }

        /**
         * Constructor for mapping at child level.
         * 
         * @param parent
         *        parent mapping
         * @param element
         *        element for which the mapping is valid
         */
        protected NamespaceMapping(final NamespaceMapping parent, final Element element) {
            super();
            this.parent = parent;
            this.context = null;
            this.element = element;
        }

        /**
         * Get the URI of the default namespace at the present scope.
         * 
         * @return a namespace URI or <code>null</code> if none has been set
         */
        public String getDefaultNamespaceURI() {
            return this.getNamespaceURI("");
        }

        /**
         * Get the element in the scope of which the present mapping is valid.
         * 
         * @return the element
         */
        public Element getElement() {
            return null == this.element ? XMLStreamToDOMWriter.this.documentRoot : this.element;
        }

        /*
         * {@inheritDoc}
         * 
         * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
         */
        public String getNamespaceURI(final String prefix) {
            final String p = null == prefix ? "" : prefix;
            String result = (String) this.namespaceMap.get(p);
            if (null == result) {
                if (null != this.parent) {
                    result = this.parent.getNamespaceURI(p);
                } else if (null != this.context) {
                    result = this.context.getNamespaceURI(p);
                }
            }
            return result;
        }

        /**
         * Get the parent mapping.
         * 
         * @return parent mapping or <code>null</code> if at top level
         */
        public NamespaceMapping getParent() {
            return this.parent;
        }

        /*
         * {@inheritDoc}
         * 
         * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
         */
        public String getPrefix(final String namespaceURI) {
            final String nsURI = null == namespaceURI ? "" : namespaceURI;
            for (NamespaceMapping nm = this; nm != null; nm = nm.getParent()) {
                for (Iterator i = nm.namespaceMap.entrySet().iterator(); i.hasNext();) {
                    final Map.Entry e = (Map.Entry) i.next();
                    final String p = (String) e.getKey();
                    final String u = (String) e.getValue();
                    // If we are in a parent of the NamespaceMapping,
                    // it must be ensured that the prefix has not been
                    // overridden.
                    if (nsURI.equals(u) && ((this == nm) || nsURI.equals(this.getNamespaceURI(p)))) return p;
                }
                if (null != nm.context) {
                    final String p = nm.context.getPrefix(namespaceURI);
                    if (nsURI.equals(this.getNamespaceURI(p))) return p;
                }
            }
            return null;
        }

        /*
         * {@inheritDoc}
         * 
         * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
         */
        public Iterator getPrefixes(final String namespaceURI) {
            return new Iterator() {

                /**
                 * Namespace URI for which prefixes are returned.
                 */
                private final String nsURI = (null == namespaceURI) ? "" : namespaceURI;

                /**
                 * Mapping within the mapping hierarhie which is currently traversed.
                 */
                private NamespaceMapping currentMapping = NamespaceMapping.this;

                /**
                 * Iterator through entry set of the Map currently traversed.
                 */
                private Iterator mapIterator = NamespaceMapping.this.namespaceMap.entrySet().iterator();

                /**
                 * Iterator through the prefixes preset at top level.
                 */
                private Iterator prefixIterator = null;

                /**
                 * Cached next object.
                 */
                private Object next = null;

                /**
                 * Initializer which finds the first object to be returned.
                 */
                {
                    this.advance();
                }

                /*
                 * {@inheritDoc}
                 * 
                 * @see java.util.Iterator#hasNext()
                 */
                public boolean hasNext() {
                    return null != this.next;
                }

                /*
                 * {@inheritDoc}
                 * 
                 * @see java.util.Iterator#next()
                 */
                public Object next() {
                    if (null == this.next) throw new NoSuchElementException("at end");
                    final Object result = this.next;
                    this.advance();
                    return result;
                }

                /*
                 * {@inheritDoc}
                 * 
                 * @see java.util.Iterator#remove()
                 */
                public void remove() {
                    throw new UnsupportedOperationException("readonly Iterator");

                }

                /**
                 * Find the next object to be returned.
                 */
                private void advance() {
                    this.next = null;
                    if (null != this.prefixIterator) {
                        while (this.prefixIterator.hasNext()) {
                            final String p = (String) this.prefixIterator.next();
                            if (this.nsURI.equals(NamespaceMapping.this.getNamespaceURI(p))) {
                                this.next = p;
                                return;
                            }
                        }
                        return;
                    }
                    while (this.mapIterator.hasNext()) {
                        final Map.Entry e = (Map.Entry) this.mapIterator.next();
                        final String p = (String) e.getKey();
                        final String u = (String) e.getValue();
                        // If we are in a parent of the NamespaceMapping,
                        // it must be ensured that the prefix has not been
                        // overridden.
                        if (this.nsURI.equals(u)
                                && ((NamespaceMapping.this == this.currentMapping) || this.nsURI.equals(NamespaceMapping.this
                                    .getNamespaceURI(p)))) {
                            this.next = p;
                            return;
                        }
                    }
                    if (null != this.currentMapping) {
                        final NamespaceMapping m = this.currentMapping;
                        this.currentMapping = m.getParent();
                        if (null != this.currentMapping) {
                            this.mapIterator = this.currentMapping.namespaceMap.entrySet().iterator();
                            this.advance();
                        } else if (null != m.context) {
                            this.mapIterator = null;
                            this.prefixIterator = m.getPrefixes(namespaceURI);
                            this.advance();
                        }
                    }
                }
            };
        }

        /**
         * Convenience method for registering the default namespace.
         * 
         * @param namespaceURI
         *        a valid namespace URI String
         */
        public void registerDefaultNamespace(final String namespaceURI) {
            this.namespaceMap.put("", namespaceURI);
        }

        /**
         * Register a namespace URI for a prefix.
         * 
         * @param prefix
         *        selected prefix, may be empty or <code>null</code> for registration of the
         *        default namespace
         * @param namespaceURI
         *        a valid namespace URI String
         */
        public void registerNamespace(final String prefix, final String namespaceURI) {
            this.namespaceMap.put(null == prefix ? "" : prefix, namespaceURI);
        }

        /**
         * Remove a default namespace mapping from present scope. This method only tries to remove
         * the namespace entry from the map in the present mapping.
         */
        public void unregisterDefaultNamespace() {
            this.namespaceMap.remove("");
        }

        /**
         * Remove a namespace mapping from present scope. This method only tries to remove the
         * namespace entry from the map in the present mapping.
         * 
         * @param prefix
         *        selected prefix, may be empty or <code>null</code> for registration of the
         *        default namespace
         */
        public void unregisterNamespace(final String prefix) {
            this.namespaceMap.remove(null == prefix ? "" : prefix);
        }
    }
}
