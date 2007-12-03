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
import java.util.List;
import java.util.Map;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class ServiceDescriptorImpl.
 */
public class ServiceDescriptorImpl extends WsdlComponent implements ServiceDescriptor {

    /** The operation names. */
    private List operationNames;

    /** PartnerLink contained in ServiceDescriptor - may be null. */
    private PartnerLink partnerLink;

    /**
     * Map (String name -> PortType) contained in ServiceDescriptor - may be empty if null ==
     * partnerLink.
     */
    private Map portTypeMap = new HashMap();

    /**
     * Instantiates a new service descriptor impl.
     * 
     * @param document
     *        the document
     * 
     * @throws CorruptedSourceException
     */
    public ServiceDescriptorImpl(final Document document) throws CorruptedSourceException {
        super();
        this.operationNames = this.extractOperations(document);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.ServiceDescriptor#getOperationNames()
     */
    public List getOperationNames() {
        return this.operationNames;
    }

    /**
     * Extract components.
     * 
     * @param definition
     *        the definition
     */
    private void extractComponents(final Element definition) {
        for (Node n = definition.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if ((WSDL_PORT_TYPE.equals(n.getLocalName())) && (WSDL_NS.equals(n.getNamespaceURI()))) {
                    PortType pt = new PortType((Element) n);
                    this.portTypeMap.put(pt.getName(), pt);
                } else if ((PARTNER_LINK.equals(n.getLocalName())) && (PARTNER_LINK_NS.equals(n.getNamespaceURI()))) {
                    this.partnerLink = new PartnerLink((Element) n);
                }
            }
        }
    }

    /**
     * Extract operations.
     * 
     * @param document
     *        the document
     * 
     * @return the list
     * 
     * @throws CorruptedSourceException
     */
    private List extractOperations(final Document document) throws CorruptedSourceException {
        final Element definitions = document.getDocumentElement();
        if (!(WSDL_NS.equals(definitions.getNamespaceURI()) && WSDL_DEFINITIONS.equals(definitions.getLocalName())))
            throw new CorruptedSourceException("Unexpected document element name");
        this.extractComponents(definitions);
        if (null == this.partnerLink) {
            Object[] portTypes = this.portTypeMap.values().toArray();
            if (1 == portTypes.length)
                return ((PortType) portTypes[0]).getOperationNames();
            else {
                if (0 == portTypes.length)
                    throw new CorruptedSourceException("No porttype definition in source");
                else
                    throw new CorruptedSourceException(
                            "Multiple porttype definitions in source but no partnerlink to declare service porttype");
            }
        } else { // null != partnerlink
            String portTypeName = this.partnerLink.getPortTypeName(PARTNER_LINK_SERVICE_PORT_TYPE);
            if (nonzero(portTypeName)) {
                PortType pt = (PortType) this.portTypeMap.get(portTypeName);
                if (null != pt)
                    return pt.getOperationNames();
                else
                    throw new CorruptedSourceException("No porttype definition found for service porttype " + portTypeName
                            + " declared in PartnerLink");
            } else
                throw new CorruptedSourceException("No service porttype declared in PartnerLink");
        }
    }

}
