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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class PortType.
 */
public class PortType extends WsdlComponent {

    /** The operation names. */
    private List operationNames;

    /** The name. */
    private String name;

    /**
     * Instantiates a new port type.
     * 
     * @param definition
     *        the definition
     */
    public PortType(final Element definition) {
        this.operationNames = this.extractOperationNames(definition);
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
     * Gets the operation names.
     * 
     * @return the operation names
     */
    public List getOperationNames() {
        // only good for internal use.
        // make return value immutable for anything else
        return this.operationNames;
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
        return definition.getAttribute("name");
    }

    /**
     * Extract operation names.
     * 
     * @param definition
     *        the definition
     * 
     * @return the list
     */
    private List extractOperationNames(final Element definition) {
        List ret = new ArrayList(definition.getChildNodes().getLength());
        for (Node n = definition.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ((n.getNodeType() == Node.ELEMENT_NODE) && WSDL_OPERATION.equals(n.getLocalName())
                    && WSDL_NS.equals(n.getNamespaceURI())) {
                final String opName = ((Element) n).getAttribute(WSDL_OPERATION_NAME_ATT);
                if (nonzero(opName)) {
                    ret.add(opName);
                }
            }
        }
        return ret;
    }

}
