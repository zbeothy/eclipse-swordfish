/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.Transport;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorLocation;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.w3c.dom.Element;

/**
 * This class implements SPDXPort.
 */
public class SPDXPortImpl implements SPDXPort {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2250071086424670955L;

    /** JMS transport. */
    private static final String JMS_TRANSPORT_URI = "http://schemas.xmlsoap.org/soap/jms";

    /** SOAP via HTTP transport. */
    private static final String HTTP_TRANSPORT_URI = "http://schemas.xmlsoap.org/soap/http";

    /** JBI transport. */
    private static final String JBI_TRANSPORT_URI = "http://binding.sopware.org/soap/jbi";

    /** The base WSDL4J port. */
    private Port port;

    /** The transport type. */
    private Transport transport;

    /**
     * Wheather this SPDXPort has locator definitions (or concrete endpoint information).
     */
    private boolean usingLocators;

    /** Locator clusters. */
    private List locatorClustersNames;

    /** Weather or not this data type has been interpreted yet. */
    private boolean interpreted;

    /** The shared. */
    private boolean shared;

    /**
     * Creates a new SPDX port.
     * 
     * @param newPort
     *        a javax.wsdl.Port
     */
    public SPDXPortImpl(final Port newPort) {
        this.port = newPort;
        this.usingLocators = false;
        this.shared = false;
        this.locatorClustersNames = new ArrayList();
    }

    /**
     * Instantiates a new SPDX port impl.
     * 
     * @param port
     *        the port
     * @param transport
     *        the transport
     */
    public SPDXPortImpl(final SPDXPort port, final Transport transport) {
        this(port);
        this.transport = transport;
    }

    /**
     * Adds the extensibility element.
     * 
     * @param arg0
     *        the arg0
     * 
     * @see javax.wsdl.Port#addExtensibilityElement(javax.wsdl.extensions.ExtensibilityElement)
     */
    public void addExtensibilityElement(final ExtensibilityElement arg0) {
        this.port.addExtensibilityElement(arg0);
    }

    /**
     * Gets the binding.
     * 
     * @return the binding
     * 
     * @see javax.wsdl.Port#getBinding()
     */
    public Binding getBinding() {
        return this.port.getBinding();
    }

    /**
     * Gets the documentation element.
     * 
     * @return the documentation element
     * 
     * @see javax.wsdl.Port#getDocumentationElement()
     */
    public Element getDocumentationElement() {
        return this.port.getDocumentationElement();
    }

    /**
     * Gets the extensibility elements.
     * 
     * @return the extensibility elements
     * 
     * @see javax.wsdl.Port#getExtensibilityElements()
     */
    public List getExtensibilityElements() {
        return this.port.getExtensibilityElements();
    }

    /**
     * Gets the extension attribute.
     * 
     * @param arg0
     *        the arg0
     * 
     * @return the extension attribute
     * 
     * @see javax.wsdl.extensions.AttributeExtensible#getExtensionAttribute(javax.xml.namespace.QName)
     */
    public Object getExtensionAttribute(final QName arg0) {
        return this.port.getExtensionAttribute(arg0);
    }

    /**
     * Gets the extension attributes.
     * 
     * @return the extension attributes
     * 
     * @see javax.wsdl.extensions.AttributeExtensible#getExtensionAttributes()
     */
    public Map getExtensionAttributes() {
        return this.port.getExtensionAttributes();
    }

    /**
     * Gets the locator cluster locations.
     * 
     * @return the locator cluster locations
     * 
     * @see org.eclipse.swordfish.servicedesc.SPDXPort#getLocatorClusters()
     */
    public List getLocatorClusterLocations() {

        if (!this.interpreted) {
            this.interpret();
        }

        return this.locatorClustersNames;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     * 
     * @see javax.wsdl.Port#getName()
     */
    public String getName() {
        return this.port.getName();
    }

    /**
     * Gets the native attribute names.
     * 
     * @return the native attribute names
     * 
     * @see javax.wsdl.extensions.AttributeExtensible#getNativeAttributeNames()
     */
    public List getNativeAttributeNames() {
        return this.port.getNativeAttributeNames();
    }

    /**
     * Gets the transport.
     * 
     * @return the transport
     * 
     * @see org.eclipse.swordfish.servicedesc.SPDXPort#getTransportType()
     */
    public Transport getTransport() {

        if (!this.interpreted) {
            this.interpret();
        }

        return this.transport;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.SPDXPort#isShared()
     */
    public boolean isShared() {
        if (!this.interpreted) {
            this.interpret();
        }
        return this.shared;
    }

    /**
     * Checks if is using locator.
     * 
     * @return true, if is using locator
     * 
     * @see org.eclipse.swordfish.servicedesc.SPDXPort#isUsingLocators()
     */
    public boolean isUsingLocator() {

        if (!this.interpreted) {
            this.interpret();
        }

        return this.usingLocators;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ElementExtensible#removeExtensibilityElement(javax.wsdl.extensions.ExtensibilityElement)
     */
    public ExtensibilityElement removeExtensibilityElement(final ExtensibilityElement arg0) {
        // Auto-generated method stub
        return arg0;
    }

    /**
     * Sets the binding.
     * 
     * @param arg0
     *        the arg0
     * 
     * @see javax.wsdl.Port#setBinding(javax.wsdl.Binding)
     */
    public void setBinding(final Binding arg0) {
        this.port.setBinding(arg0);
    }

    /**
     * Sets the documentation element.
     * 
     * @param arg0
     *        the arg0
     * 
     * @see javax.wsdl.Port#setDocumentationElement(org.w3c.dom.Element)
     */
    public void setDocumentationElement(final Element arg0) {
        this.port.setDocumentationElement(arg0);
    }

    /**
     * Sets the extension attribute.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @see javax.wsdl.extensions.AttributeExtensible#setExtensionAttribute(javax.xml.namespace.QName,
     *      java.lang.Object)
     */
    public void setExtensionAttribute(final QName arg0, final Object arg1) {
        this.port.setExtensionAttribute(arg0, arg1);
    }

    /**
     * Sets the name.
     * 
     * @param arg0
     *        the arg0
     * 
     * @see javax.wsdl.Port#setName(java.lang.String)
     */
    public void setName(final String arg0) {
        this.port.setName(arg0);
    }

    /**
     * Interprets the port to fill member variables.
     */
    private void interpret() {

        if (null == this.transport) {
            // Transport type
            SOAPBinding binding = null;
            List ee = this.port.getBinding().getExtensibilityElements();
            for (Iterator iter = ee.iterator(); iter.hasNext();) {
                ExtensibilityElement element = (ExtensibilityElement) iter.next();
                if (element instanceof SOAPBinding) {
                    binding = (SOAPBinding) element;
                }
            }
            if (binding != null) {
                // REMINDER We might deduce the locator endpoint in the same
                // way. Currently it is done "not nicely"
                if (binding.getTransportURI().equals(JBI_TRANSPORT_URI)) {
                    this.transport = Transport.JBI;
                } else if (binding.getTransportURI().equals(JMS_TRANSPORT_URI)) {
                    this.transport = Transport.JMS;
                } else if (binding.getTransportURI().equals(HTTP_TRANSPORT_URI)) {
                    List extElems = this.port.getExtensibilityElements();
                    for (Iterator iter = extElems.iterator(); iter.hasNext();) {
                        ExtensibilityElement element = (ExtensibilityElement) iter.next();
                        if ((element instanceof SOAPAddress)) {
                            String uri = ((SOAPAddress) element).getLocationURI();
                            if (uri.toLowerCase().startsWith("https")) {
                                this.transport = Transport.HTTPS;
                            } else {
                                this.transport = Transport.HTTP;
                            }
                        }
                    }
                }
            }
            // we could not find out any transport
            if (this.transport == null) {
                this.transport = Transport.UNKNOWN;
            }
        }
        // locator information
        List extElems = this.port.getExtensibilityElements();
        for (Iterator iter = extElems.iterator(); iter.hasNext();) {
            ExtensibilityElement elem = (ExtensibilityElement) iter.next();
            if (elem instanceof LocatorLocation) {
                this.usingLocators = true;
                this.locatorClustersNames = ((LocatorLocation) elem).getLocationList();
            }
            // TODO ADD JMS SUPPORT
            // if (elem instanceof JMSAddress) {
            // JMSAddress jmsAddress = (JMSAddress) elem;
            // String str = jmsAddress.getIsSharedDestination();
            // if ((null != str) && Boolean.valueOf(str).booleanValue()) {
            // this.shared = true;
            // }
            // }
        }

        this.interpreted = true;
    }

}
