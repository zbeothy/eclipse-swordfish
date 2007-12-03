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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class WsdlComponent.
 */
public class WsdlComponent {

    /** The Constant WSDL_NS. */
    public static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";

    /** The Constant WSDL_DEFINITIONS. */
    public static final String WSDL_DEFINITIONS = "definitions";

    /** The Constant WSDL_PORT_TYPE. */
    public static final String WSDL_PORT_TYPE = "portType";

    /** The Constant WSDL_OPERATION. */
    public static final String WSDL_OPERATION = "operation";

    /** The Constant WSDL_OPERATION_NAME_ATT. */
    public static final String WSDL_OPERATION_NAME_ATT = "name";

    /** The Constant PARTNER_LINK_NS. */
    public static final String PARTNER_LINK_NS = "http://schemas.xmlsoap.org/ws/2003/05/partner-link/";

    /** The Constant PARTNER_LINK. */
    public static final String PARTNER_LINK = "partnerLinkType";

    /** The Constant PARTNER_LINK_NAME_ATTRIBUTE. */
    public static final String PARTNER_LINK_NAME_ATTRIBUTE = "name";

    /** The Constant PARTNER_LINK_ROLE. */
    public static final String PARTNER_LINK_ROLE = "role";

    /** The Constant PARTNER_LINK_ROLE_NAME. */
    public static final String PARTNER_LINK_ROLE_NAME = "name";

    /** The Constant PARTNER_LINK_PORT_TYPE. */
    public static final String PARTNER_LINK_PORT_TYPE = "portType";

    /** The Constant PARTNER_LINK_PORT_TYPE_NAME. */
    public static final String PARTNER_LINK_PORT_TYPE_NAME = "name";

    /** The Constant PARTNER_LINK_SERVICE_PORT_TYPE. */
    public static final String PARTNER_LINK_SERVICE_PORT_TYPE = "service";

    /**
     * Gets the first child named.
     * 
     * @param nsURI
     *        the ns URI
     * @param nsLocal
     *        the ns local
     * @param parent
     *        the parent
     * 
     * @return the first child named
     */
    protected static Element getFirstChildNamed(final String nsURI, final String nsLocal, final Element parent) {
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ((n.getNodeType() == Node.ELEMENT_NODE) && nsLocal.equals(n.getLocalName()) && nsURI.equals(n.getNamespaceURI()))
                return (Element) n;
        }
        return null;
    }

    /**
     * Gets the next sibling named.
     * 
     * @param nsURI
     *        the ns URI
     * @param nsLocal
     *        the ns local
     * @param element
     *        the element
     * 
     * @return the next sibling named
     */
    protected static Element getNextSiblingNamed(final String nsURI, final String nsLocal, final Element element) {
        for (Node n = element.getNextSibling(); n != null; n = n.getNextSibling()) {
            if ((n.getNodeType() == Node.ELEMENT_NODE) && nsLocal.equals(n.getLocalName()) && nsURI.equals(n.getNamespaceURI()))
                return (Element) n;
        }
        return null;
    }

    /**
     * Nonzero.
     * 
     * @param s
     *        the s
     * 
     * @return true, if successful
     */
    protected static boolean nonzero(final String s) {
        return (s != null) && (s.length() > 0);
    }
}
