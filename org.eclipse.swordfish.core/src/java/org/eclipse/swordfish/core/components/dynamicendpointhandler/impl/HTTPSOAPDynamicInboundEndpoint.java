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
package org.eclipse.swordfish.core.components.dynamicendpointhandler.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.EndpointProperties;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.InvalidAddressException;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * The Class HTTPSOAPDynamicInboundEndpoint.
 */
public class HTTPSOAPDynamicInboundEndpoint extends AbstractDynamicEndpoint {

    /** The Constant urlPattern. */
    static final String URL_PATTERN = "https?://.*/soap/(.*)";

    /** The Constant p. */
    static final Pattern P = Pattern.compile(URL_PATTERN);

    /** The location URI. */
    private String locationURI;

    /** The timeout. */
    private Integer timeout;

    /**
     * Instantiates a new HTTPSOAP dynamic inbound endpoint.
     * 
     * @param serviceName
     *        the service name
     * @param port
     *        the port
     * @param props
     *        the props
     * 
     * @throws InvalidAddressException
     */
    protected HTTPSOAPDynamicInboundEndpoint(final QName serviceName, final SPDXPort port, final Map props)
            throws InvalidAddressException {
        super(serviceName, port);
        this.locationURI = null;
        this.timeout = (Integer) props.get(EndpointProperties.TIMEOUT);
        this.init();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.impl.AbstractDynamicEndpoint#getEndpointAddress()
     */
    @Override
    public String getEndpointAddress() {
        return this.locationURI;
    }

    /**
     * Creates the endpoint info.
     * 
     * @return the document fragment
     * 
     * @throws InvalidAddressException
     */
    @Override
    protected DocumentFragment createEndpointInfo() throws InvalidAddressException {
        Document doc = DOMUtil.newDocument();
        DocumentFragment frag = doc.createDocumentFragment();
        String soapNs = "http://xmlns.oracle.com/jbi/component/soap";
        Element e = DOMUtil.appendNewElement(frag, soapNs, "event-source");
        DOMUtil.setDefaultNamespace(e, soapNs);
        for (Iterator iter = this.getPort().getExtensibilityElements().iterator(); iter.hasNext();) {
            ExtensibilityElement element = (ExtensibilityElement) iter.next();
            if (element instanceof SOAPAddress) {
                this.locationURI = ((SOAPAddress) element).getLocationURI();
                String contextPath = this.extractContextPath(this.locationURI);
                if ((null != contextPath) && (contextPath.length() > 0)) {
                    DOMUtil.appendNewElementValue(e, soapNs, "external-name", contextPath);
                } else
                    throw new InvalidAddressException("Endpoint address for port " + this.getPort().getName() + " is invalid."
                            + " Expected format is http[s]://<hostname>[:<port>]/soap/<servicename>");
            }
        }
        DOMUtil.appendNewElementValue(e, soapNs, "wsdl-location", "internal");
        DOMUtil.appendNewElementValue(e, soapNs, "timeout", this.timeout.toString());
        DOMUtil.appendNewElementValue(e, soapNs, "port-type", this.getPortTypeQName().getLocalPart());
        return frag;
    }

    /**
     * Extract context path.
     * 
     * @param sLocationURI
     *        the location URI
     * 
     * @return the string
     */
    private String extractContextPath(final String sLocationURI) {
        if (null != sLocationURI) {
            Matcher m = P.matcher(sLocationURI);
            if (m.matches()) return m.group(1);
        }
        return null;
    }

}
