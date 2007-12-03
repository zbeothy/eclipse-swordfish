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
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddress;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.EndpointProperties;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.InvalidAddressException;
import org.eclipse.swordfish.core.components.endpointreferenceresolver.impl.EPRHelper;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * The Class JMSDynamicInboundEndpoint.
 */
public class JMSDynamicInboundEndpoint extends AbstractJMSDynamicEndpoint {

    /** The selector. */
    private String selector;

    /** The timeout. */
    private Integer timeout;

    /**
     * Instantiates a new JMS dynamic inbound endpoint.
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
    public JMSDynamicInboundEndpoint(final QName serviceName, final SPDXPort port, final Map props) throws InvalidAddressException {
        super(serviceName, port);
        this.selector = (String) props.get(EndpointProperties.SELECTOR);
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
        return this.getJMSQueueURL(this.getPort());
    }

    /**
     * Creates the endpoint info.
     * 
     * @return the document fragment
     */
    @Override
    protected DocumentFragment createEndpointInfo() {
        Document doc = DOMUtil.newDocument();
        DocumentFragment frag = doc.createDocumentFragment();
        String soapNs = "http://xmlns.oracle.com/jbi/component/soap";
        Element e = DOMUtil.appendNewElement(frag, soapNs, "jms-event-source");
        DOMUtil.setDefaultNamespace(e, soapNs);
        String jmsUrl = this.getJMSQueueURL(this.getPort());
        DOMUtil.appendNewElementValue(e, soapNs, "listener-url", jmsUrl);
        if (this.timeout != null) {
            DOMUtil.appendNewElementValue(e, soapNs, "timeout", this.timeout.toString());
        }
        if (this.selector != null) {
            Element el = DOMUtil.appendNewElement(e, soapNs, "message-selector");
            CDATASection cdata = doc.createCDATASection(this.selector);
            el.appendChild(cdata);
        }
        return frag;
    }

    /**
     * Gets the JMS queue URL.
     * 
     * @param port
     *        the port
     * 
     * @return the JMS queue URL
     */
    private String getJMSQueueURL(final SPDXPort port) {
        String url = null;
        for (Iterator iter = port.getExtensibilityElements().iterator(); iter.hasNext();) {
            ExtensibilityElement element = (ExtensibilityElement) iter.next();
            if (element instanceof JMSAddress) {
                JMSAddress jmsAddress = (JMSAddress) element;
                url = EPRHelper.createJMSAddressURI(jmsAddress);
            }
            if (element instanceof SOAPAddress) {
                SOAPAddress soapAddress = (SOAPAddress) element;
                url = soapAddress.getLocationURI();
            }
        }
        return url;
    }

}
