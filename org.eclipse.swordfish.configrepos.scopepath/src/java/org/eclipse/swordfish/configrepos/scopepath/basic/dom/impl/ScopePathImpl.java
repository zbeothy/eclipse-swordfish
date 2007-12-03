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
package org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl;

import java.util.Iterator;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class ScopePathImpl.
 */
public class ScopePathImpl extends org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.PathTypeImpl implements
        org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath {

    /**
     * Instantiates a new scope path impl.
     */
    public ScopePathImpl() {
        this.setSeparator(ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR);
    }

    /**
     * Gets the local name.
     * 
     * @return the local name
     */
    public java.lang.String getLocalName() {
        return "ScopePath";
    }

    /**
     * Gets the namespace URI.
     * 
     * @return the namespace URI
     */
    public java.lang.String getNamespaceURI() {
        return "http://types.sopware.org/configuration/BasicScopePath/1.0";
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath#marshal(org.w3c.dom.Document)
     * @param doc
     */
    public void marshal(final Document doc) {
        this.marshal(doc, null);
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath#marshal(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     * @param doc
     * @param root
     */
    public void marshal(final Document doc, final Element root) {
        final Element rootElement = (root == null) ? doc.createElementNS(this.getNamespaceURI(), this.getLocalName()) : root;
        if (root == null) {
            rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", this.getNamespaceURI());
            doc.appendChild(rootElement);
        }

        if (this.getSeparator() != null) {
            rootElement.setAttribute("separator", this.getSeparator());
        }

        if (this.getPathPart() != null) {
            Iterator pathPart = this.getPathPart().iterator();
            while (pathPart.hasNext()) {
                PathPart part = (PathPart) pathPart.next();
                Element partElem =
                        doc.createElementNS(((PathPartImpl) part).getNamespaceURI(), ((PathPartImpl) part).getLocalName());
                part.marshal(doc, partElem);
                rootElement.appendChild(partElem);
            }
        }

    }
}
