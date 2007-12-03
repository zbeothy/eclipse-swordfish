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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class PathPartImpl.
 */
public class PathPartImpl extends org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.PathPartTypeImpl implements
        org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart {

    /**
     * Instantiates a new path part impl.
     */
    public PathPartImpl() {

    }

    /**
     * Gets the local name.
     * 
     * @return the local name
     */
    public java.lang.String getLocalName() {
        return "PathPart";
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
     * Gets the primary interface.
     * 
     * @return the primary interface
     */
    public java.lang.Class getPrimaryInterface() {
        return (org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart.class);
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart#marshal(org.w3c.dom.Document)
     * @param doc
     */
    public void marshal(final Document doc) {
        this.marshal(doc, null);
    }

    /**
     * (non-Javadoc)..
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart#marshal(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     * @param doc
     * @param root
     */
    public void marshal(final Document doc, final Element root) {
        final Element rootElement = (root == null) ? doc.createElementNS(this.getNamespaceURI(), this.getLocalName()) : root;
        if (root == null) {
            doc.appendChild(rootElement);
        }

        if (this.getType() != null) {
            rootElement.setAttribute("type", this.getType());
        }

        if (this.getValue() != null) {
            rootElement.setAttribute("value", this.getValue());
        }

    }
}
