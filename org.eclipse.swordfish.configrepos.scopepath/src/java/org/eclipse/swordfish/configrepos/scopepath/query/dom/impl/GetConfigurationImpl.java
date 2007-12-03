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

import org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class GetConfigurationImpl.
 * 
 */
public class GetConfigurationImpl extends GetConfigurationTypeImpl implements
        org.eclipse.swordfish.configrepos.scopepath.query.dom.GetConfiguration {

    /**
     * Instantiates a new get configuration impl.
     */
    public GetConfigurationImpl() {
        super();
    }

    /**
     * Gets the local name.
     * 
     * @return the local name
     */
    public java.lang.String getLocalName() {
        return "getConfiguration";
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
     * @param doc
     *        the doc
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.GetConfiguration#marshal(org.w3c.dom.Document)
     */
    public void marshal(final Document doc) {
        this.marshal(doc, null);
    }

    /**
     * (non-Javadoc).
     * 
     * @param doc
     *        the doc
     * @param root
     *        the root
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.GetConfiguration#marshal(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    public void marshal(final Document doc, final Element root) {
        final Element rootElement = (root == null) ? doc.createElementNS(this.getNamespaceURI(), this.getLocalName()) : root;
        if (root == null) {
            rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", this.getNamespaceURI());
            doc.appendChild(rootElement);
        }

        if (this.getConfigurationQuery() != null) {
            ConfigurationQuery query = this.getConfigurationQuery();
            Element scopePath =
                    doc.createElementNS(((ConfigurationQueryImpl) query).getNamespaceURI(), ((ConfigurationQueryImpl) query)
                        .getLocalName());
            scopePath.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", ((ConfigurationQueryImpl) query).getNamespaceURI());
            query.marshal(doc, scopePath);
            rootElement.appendChild(scopePath);
        }
    }

}
