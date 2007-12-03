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
package org.eclipse.swordfish.configrepos.scopepath.query.dom.impl;

import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.ScopePathImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * The Class ResourceQueryImpl.
 */
public class ResourceQueryImpl extends org.eclipse.swordfish.configrepos.scopepath.query.dom.impl.ResourceQueryTypeImpl implements
        org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQuery {

    /**
     * Instantiates a new resource query impl.
     */
    public ResourceQueryImpl() {

    }

    /**
     * Gets the local name.
     * 
     * @return the local name
     */
    public java.lang.String getLocalName() {
        return "ResourceQuery";
    }

    /**
     * Gets the namespace URI.
     * 
     * @return the namespace URI
     */
    public java.lang.String getNamespaceURI() {
        return "http://types.sopware.org/configuration/ConfigurationQuery/1.0";
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQuery#marshal(org.w3c.dom.Document)
     * @param doc
     */
    public void marshal(final Document doc) {
        this.marshal(doc, null);
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQuery#marshal(org.w3c.dom.Document,
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

        if (this.getScopePath() != null) {
            ScopePath sp = this.getScopePath();
            Element scopePath = doc.createElementNS(((ScopePathImpl) sp).getNamespaceURI(), ((ScopePathImpl) sp).getLocalName());
            scopePath.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", ((ScopePathImpl) sp).getNamespaceURI());
            sp.marshal(doc, scopePath);
            rootElement.appendChild(scopePath);
        }

        if (this.getTree() != null) {
            Element tree = doc.createElementNS(this.getNamespaceURI(), "tree");
            Text text = doc.createTextNode(this.getTree());
            tree.appendChild(text);
            rootElement.appendChild(tree);
        }

        if (this.getComponentId() != null) {
            Element tree = doc.createElementNS(this.getNamespaceURI(), "componentId");
            Text text = doc.createTextNode(this.getComponentId());
            tree.appendChild(text);
            root.appendChild(tree);
        }

        if (this.getResourceId() != null) {
            Element tree = doc.createElementNS(this.getNamespaceURI(), "resourceId");
            Text text = doc.createTextNode(this.getResourceId());
            tree.appendChild(text);
            root.appendChild(tree);
        }
    }
}
