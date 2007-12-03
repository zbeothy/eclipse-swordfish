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
package org.eclipse.swordfish.core.components.locatorproxy.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorLocation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Class LocatorLocationImpl.
 */
public class LocatorLocationImpl implements LocatorLocation {

    /** The required. */
    private Boolean required;

    /** The elem type. */
    private QName elemType;

    /** The elem. */
    private Element elem;

    /** The locations. */
    private List locations;

    /**
     * Instantiates a new locator location impl.
     */
    public LocatorLocationImpl() {
        super();
        this.locations = new ArrayList();
    }

    /**
     * Gets the element.
     * 
     * @return the element
     */
    public Element getElement() {
        return this.elem;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensibilityElement#getElementType()
     */
    public QName getElementType() {
        return this.elemType;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.locatorproxy.LocatorLocation#getLocationList()
     */
    public List getLocationList() {
        return this.locations;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensibilityElement#getRequired()
     */
    public Boolean getRequired() {
        return this.required;
    }

    /**
     * Sets the element.
     * 
     * @param arg0
     *        the new element
     */
    public void setElement(final Element arg0) {
        this.elem = arg0;
        this.locations = new ArrayList();
        NodeList childs = this.elem.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node node = childs.item(i);
            if ((node.getNodeType() == Node.ELEMENT_NODE) && "location".equals(node.getLocalName())) {

                Node textNode = node.getFirstChild();
                if (textNode.getNodeType() == Node.TEXT_NODE) {
                    String str = textNode.getNodeValue();
                    str = str != null ? str.trim() : "";
                    if (!"".equals(str)) {
                        this.locations.add(str);
                    }
                }
            }

        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensibilityElement#setElementType(javax.xml.namespace.QName)
     */
    public void setElementType(final QName arg0) {
        this.elemType = arg0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensibilityElement#setRequired(java.lang.Boolean)
     */
    public void setRequired(final Boolean arg0) {
        this.required = arg0;
    }

}
