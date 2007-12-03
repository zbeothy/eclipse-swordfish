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
package org.eclipse.swordfish.core.components.iapi.impl;

import java.util.Iterator;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class WSDLHelper.
 */
public final class WSDLHelper {

    /** The Constant WSDL_NS. */
    private static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";

    /** The Constant XMLNS_NAMESPACE. */
    private static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";

    /**
     * Creates an internal JBI style internal WSDL for the port type.
     * 
     * @param portType
     *        the port type
     * @param serviceName
     *        the service name
     * 
     * @return The DOM of the JBI style internal WSDL.
     */
    public static Document createWSDL(final PortType portType, final QName serviceName) {
        try {
            Document d = TransformerUtil.getDocumentBuilder().newDocument();
            String namespace = serviceName.getNamespaceURI();
            Element e = d.createElementNS(WSDL_NS, "definitions");
            d.appendChild(e);
            e.setAttributeNS(XMLNS_NAMESPACE, "xmlns", WSDL_NS);
            e.setAttributeNS(XMLNS_NAMESPACE, "xmlns:tns", namespace);
            e.setAttribute("targetNamespace", namespace);
            Element pt = d.createElementNS(WSDL_NS, "portType");
            pt.setAttribute("name", portType.getQName().getLocalPart());
            e.appendChild(pt);

            // Add the PortType
            Iterator ops = portType.getOperations().iterator();
            while (ops.hasNext()) {
                Operation op = (Operation) ops.next();
                Element ope = d.createElementNS(WSDL_NS, "operation");
                ope.setAttribute("name", op.getName());
                pt.appendChild(ope);
                // TODO currently doesn't actually build messages, needs to.
                if ((op.getInput() != null) && (op.getOutput() != null)) {
                    // in-out
                    ope.appendChild(d.createElementNS(WSDL_NS, "input"));
                    ope.appendChild(d.createElementNS(WSDL_NS, "output"));
                } else if ((op.getInput() != null) && (op.getOutput() == null)) {
                    // in-only
                    ope.appendChild(d.createElementNS(WSDL_NS, "input"));
                } else if ((op.getInput() == null) && (op.getOutput() != null)) {
                    // out-only
                    // currently needs to be faked to in-only because of a
                    // defect in the JBI container
                    // TODO remove workaround once container defect conc.
                    // out-only is fixed
                    ope.appendChild(d.createElementNS(WSDL_NS, "input"));
                }
            }
            // Add the binding:
            Element bindinge = d.createElementNS(WSDL_NS, "binding");
            e.appendChild(bindinge);
            bindinge.setAttribute("name", "JBIBinding");
            bindinge.setAttribute("type", "tns:" + portType.getQName().getLocalPart());

            // Add the service:
            Element servicee = d.createElementNS(WSDL_NS, "service");
            e.appendChild(servicee);
            servicee.setAttribute("name", serviceName.getLocalPart());
            Element porte = d.createElementNS(WSDL_NS, "port");
            servicee.appendChild(porte);
            porte.setAttribute("name", "JBI");
            porte.setAttribute("binding", "tns:JBIBinding");

            return d;
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }
    }
}
