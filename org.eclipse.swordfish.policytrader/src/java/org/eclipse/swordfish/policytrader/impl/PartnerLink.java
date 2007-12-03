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
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class PartnerLink.
 */
public class PartnerLink extends WsdlComponent {

    /** The name. */
    private String name;

    /** The port type names. */
    private Map portTypeNames;

    /**
     * Instantiates a new partner link.
     * 
     * @param definition
     *        the definition
     */
    public PartnerLink(final Element definition) {
        this.portTypeNames = this.extractPortTypeNames(definition);
        this.name = this.extractName(definition);
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * returns the name of the port type associated with the given rolename.
     * 
     * @param rolename
     *        the rolename
     * 
     * @return name of the porttype
     */
    public String getPortTypeName(final String rolename) {
        return (String) this.portTypeNames.get(rolename);
    }

    /**
     * Extract name.
     * 
     * @param definition
     *        the definition
     * 
     * @return the string
     */
    private String extractName(final Element definition) {
        return definition.getAttribute(PARTNER_LINK_NAME_ATTRIBUTE);
    }

    /**
     * Extracts the porttype name for each role in the partnerlink.
     * 
     * @param definition
     *        the definition
     * 
     * @return (String roleName) -> (String portTypeName - without namespace prefix)
     */
    private Map extractPortTypeNames(final Element definition) {
        Map ret = new HashMap();
        for (Node n = definition.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ((n.getNodeType() == Node.ELEMENT_NODE) && PARTNER_LINK_ROLE.equals(n.getLocalName())
                    && PARTNER_LINK_NS.equals(n.getNamespaceURI())) {
                String roleName = ((Element) n).getAttribute(PARTNER_LINK_ROLE_NAME);
                if (nonzero(roleName)) {
                    Element portType = getFirstChildNamed(PARTNER_LINK_NS, PARTNER_LINK_PORT_TYPE, (Element) n);
                    if (null != portType) {
                        String portTypeName = portType.getAttribute(PARTNER_LINK_PORT_TYPE_NAME);
                        if (nonzero(portTypeName)) {
                            // remove namespace prefix from porttype name
                            String[] parts = portTypeName.split(":");
                            if (1 < parts.length) {
                                portTypeName = parts[parts.length - 1];
                            }
                            ret.put(roleName, portTypeName);
                        }
                    }
                }
            }
        }
        return ret;
    }

}
