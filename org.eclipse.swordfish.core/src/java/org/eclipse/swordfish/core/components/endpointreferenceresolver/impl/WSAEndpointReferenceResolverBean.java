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
package org.eclipse.swordfish.core.components.endpointreferenceresolver.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddress;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.endpointmanager.EndpointManager;
import org.eclipse.swordfish.core.components.endpointreferenceresolver.EndpointReferenceResolver;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Transport;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.w3c.dom.DocumentFragment;

/**
 * The Class WSAEndpointReferenceResolverBean.
 */
public class WSAEndpointReferenceResolverBean implements EndpointReferenceResolver {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(WSAEndpointReferenceResolverBean.class);

    /** The enable local loopback. */
    private boolean enableLocalLoopback = false;

    /** The default transport. */
    private String defaultTransport;

    /** The endpoint manager. */
    private EndpointManager endpointManager;

    // contamination with the locator proxy
    /** The locator proxy. */
    private LocatorProxy locatorProxy;

    /**
     * Creates the endpoint reference.
     * 
     * @param asd
     *        the asd
     * @param operationName
     *        the operation name
     * 
     * @return the document fragment
     */
    public DocumentFragment createEndpointReference(final CompoundServiceDescription asd, final String operationName) {
        String addressURI = null;
        DocumentFragment epr = null;
        SPDXPort port = asd.choosePort(operationName, this.defaultTransport);
        if (!port.isUsingLocator()) {
            if (port.getTransport().equals(Transport.JBI))
                // this null is returned by purpose. This is not because we do
                // cannot create an EPR, but because we want to use no external
                // endpoint
                return null;
            else if (port.getTransport().equals(Transport.JMS)) {
                addressURI = this.createJMSEndpointReference(asd, operationName, port);
            } else if (port.getTransport().equals(Transport.HTTP) || port.getTransport().equals(Transport.HTTPS)) {
                addressURI = this.createSOAPHTTPEndpointReference(asd, operationName, port);
            }
            if (addressURI != null) {
                epr = EPRHelper.createBcEPR(addressURI);
                LOG.debug("Created EPR: " + this.prettyPrint(epr));
            } else {
                LOG.warn("EPR could not be created: please check address metadata for port " + port.getName());
            }
        } else {
            if (this.getLocatorProxy().isActive()) {
                epr = EPRHelper.createLocatorEPR(asd, port);
            } else {
                LOG.warn("EPR could not be created: port " + port.getName()
                        + " indicates to use locator, but the configuration indicates not to use the locator.");
            }
        }
        return epr;
    }

    /**
     * Creates the endpoint reference.
     * 
     * @param opdesc
     *        the opdesc
     * 
     * @return the document fragment
     */
    public DocumentFragment createEndpointReference(final OperationDescription opdesc) {
        return this.createEndpointReference(opdesc.getServiceDescription(), opdesc.getName());
    }

    /**
     * Creates the endpoint reference.
     * 
     * @param address
     *        the address
     * 
     * @return the document fragment
     */
    public DocumentFragment createEndpointReference(final WSAEndpointReference address) {
        String url = address.getAddress();
        return EPRHelper.createBcEPR(address.getAddress());
    }

    /**
     * Creates the JMS endpoint reference.
     * 
     * @param asd
     *        the asd
     * @param operationName
     *        the operation name
     * @param port
     *        the port
     * 
     * @return the string
     */
    public String createJMSEndpointReference(final CompoundServiceDescription asd, final String operationName, final SPDXPort port) {
        String addressURI = null;
        for (Iterator iter = port.getExtensibilityElements().iterator(); iter.hasNext();) {
            ExtensibilityElement element = (ExtensibilityElement) iter.next();
            if (element instanceof JMSAddress) {
                JMSAddress jmsAddress = (JMSAddress) element;
                addressURI = EPRHelper.createJMSAddressURI(jmsAddress);
            }
        }
        return addressURI;
    }

    /**
     * Gets the default transport.
     * 
     * @return the default transport
     */
    public String getDefaultTransport() {
        return this.defaultTransport;
    }

    /**
     * Gets the endpoint manager.
     * 
     * @return the endpoint manager
     */
    public EndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    /**
     * Gets the endpoint name for operation.
     * 
     * @param opdesc
     *        the opdesc
     * 
     * @return the endpoint name for operationn
     */
    public String getEndpointNameForOperation(final OperationDescription opdesc) {
        SPDXPort port = opdesc.getServiceDescription().choosePort(opdesc.getName(), this.defaultTransport);
        return port.getName();
    }

    /**
     * Gets the locator proxy.
     * 
     * @return Returns the locatorProxy.
     */
    public LocatorProxy getLocatorProxy() {
        return this.locatorProxy;
    }

    /**
     * Checks if is enable local loopback.
     * 
     * @return true, if is enable local loopback
     */
    public boolean isEnableLocalLoopback() {
        return this.enableLocalLoopback;
    }

    /**
     * Resolve endpoint reference.
     * 
     * @param epr
     *        the epr
     * 
     * @return the service endpoint
     */
    public ServiceEndpoint resolveEndpointReference(final DocumentFragment epr) {
        LOG.debug("Container asks to resolve EPR: " + this.prettyPrint(epr));
        if (this.isEnableLocalLoopback()) {
            QName service = EPRHelper.getService(epr);
            String port = EPRHelper.getPort(epr);
            ServiceEndpoint se = this.endpointManager.getServiceEndpoint(service, port);
            return se;
        } else
            return null;
    }

    /**
     * Sets the default transport.
     * 
     * @param defaultTransport
     *        the new default transport
     */
    public void setDefaultTransport(final String defaultTransport) {
        this.defaultTransport = defaultTransport;
    }

    /**
     * Sets the enable local loopback.
     * 
     * @param enableLocalLoopback
     *        the new enable local loopback
     */
    public void setEnableLocalLoopback(final boolean enableLocalLoopback) {
        this.enableLocalLoopback = enableLocalLoopback;
    }

    /**
     * Sets the endpoint manager.
     * 
     * @param endpointManager
     *        the new endpoint manager
     */
    public void setEndpointManager(final EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    /**
     * Sets the locator proxy.
     * 
     * @param locatorProxy
     *        The locatorProxy to set.
     */
    public void setLocatorProxy(final LocatorProxy locatorProxy) {
        this.locatorProxy = locatorProxy;
    }

    /**
     * Creates the SOAPHTTP endpoint reference.
     * 
     * @param asd
     *        the asd
     * @param operationName
     *        the operation name
     * @param port
     *        the port
     * 
     * @return the string
     */
    private String createSOAPHTTPEndpointReference(final CompoundServiceDescription asd, final String operationName,
            final SPDXPort port) {
        String addressURI = null;
        for (Iterator iter = port.getExtensibilityElements().iterator(); iter.hasNext();) {
            ExtensibilityElement element = (ExtensibilityElement) iter.next();
            if (element instanceof SOAPAddress) {
                addressURI = ((SOAPAddress) element).getLocationURI();
            }
        }
        return addressURI;
    }

    /**
     * Pretty print.
     * 
     * @param df
     *        the df
     * 
     * @return the string
     */
    private String prettyPrint(final DocumentFragment df) {
        OutputFormat format = new OutputFormat();
        format.setOmitXMLDeclaration(true);
        format.setIndenting(true);
        format.setStandalone(false);
        format.setIndent(3);
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        try {
            serial.serialize(df);
        } catch (IOException e) {
            e.printStackTrace();
            // swallow intentionally
        }
        return stringOut.toString();
    }

}
