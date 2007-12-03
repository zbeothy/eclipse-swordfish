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

import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.InvalidAddressException;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.iapi.Transport;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * The Class AbstractDynamicEndpoint.
 */
public abstract class AbstractDynamicEndpoint implements DynamicInboundEndpoint {

    /**
     * Builds the endpoint identifier.
     * 
     * @param wsdlServiceName
     *        the wsdl service name
     * @param wsdlPortName
     *        the wsdl port name
     * @param identity
     *        the identity
     * 
     * @return the string
     */
    public static String buildEndpointIdentifier(final QName wsdlServiceName, final String wsdlPortName,
            final UnifiedParticipantIdentity identity) {
        if (identity != null)
            return wsdlServiceName.toString() + "#" + wsdlPortName + "#" + identity.toString();
        else
            return wsdlServiceName.toString() + "#" + wsdlPortName;
    }

    /**
     * New instance.
     * 
     * @param serviceName
     *        the service name
     * @param port
     *        the port
     * @param props
     *        the props
     * 
     * @return the abstract dynamic endpoint
     * 
     * @throws InvalidAddressException
     */
    public static AbstractDynamicEndpoint newInstance(final QName serviceName, final SPDXPort port, final Map props)
            throws InvalidAddressException {

        if (port.getTransport().equals(Transport.HTTP) || port.getTransport().equals(Transport.HTTPS))
            return new HTTPSOAPDynamicInboundEndpoint(serviceName, port, props);
        else if (port.getTransport().equals(Transport.JMS))
            return new JMSDynamicInboundEndpoint(serviceName, port, props);
        else
            return null;
    }

    /**
     * New notification instance.
     * 
     * @param serviceName
     *        the service name
     * @param repos
     *        the repos
     * @param port
     *        the port
     * @param operationName
     *        the operation name
     * @param participant
     *        the participant
     * @param props
     *        the props
     * 
     * @return the abstract dynamic endpoint
     * 
     * @throws InvalidAddressException
     */
    public static AbstractDynamicEndpoint newNotificationInstance(final QName serviceName,

    final LocalEndpointRepository repos, final SPDXPort port, final String operationName,
            final UnifiedParticipantIdentity participant, final Map props) throws InvalidAddressException {
        if (port.getTransport().equals(Transport.JMS))
            return new JMSDynamicNotificationEndpoint(serviceName, port, operationName, participant, repos, props);
        return null;
    }

    /** The port. */
    private SPDXPort port;

    /** The service name. */
    private QName serviceName;

    /** The doc. */
    private Document doc;

    /** The config. */
    private ParticipantConfiguration config;

    /**
     * Instantiates a new abstract dynamic endpoint.
     * 
     * @param serviceName
     *        the service name
     * @param port
     *        the port
     */
    protected AbstractDynamicEndpoint(final QName serviceName, final SPDXPort port) {
        this.port = port;
        this.serviceName = serviceName;
        this.config = new ParticipantConfiguration();
    }

    /**
     * As document.
     * 
     * @return the document
     */
    public Document asDocument() {
        return this.doc;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint#asString()
     */
    public String asString() {
        return this.config.writeAsXML();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint#getAddressFragment()
     */
    public DocumentFragment getAddressFragment() {
        String str =
                "<wsa:EndpointReference " + "xmlns:wsa=\"http://www.w3.org/2005/08/addressing\" "
                        + "xmlns:wsaw=\"http://www.w3.org/2005/03/addressing/wsdl\">" + "<wsa:Address>" + this.getEndpointAddress()
                        + "</wsa:Address>" + "<wsa:Metadata/>" + "</wsa:EndpointReference>";
        Document rDoc = TransformerUtil.docFromString(str);
        DocumentFragment frag = rDoc.createDocumentFragment();
        frag.appendChild(rDoc.getDocumentElement());
        return frag;
    }

    /**
     * Gets the endpoint address.
     * 
     * @return the endpoint address
     */
    public abstract String getEndpointAddress();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint#getId()
     */
    public String getId() {
        return this.config.getName().toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint#getServiceQName()
     */
    public QName getServiceQName() {
        return this.serviceName;
    }

    /**
     * Builds the endpoint identifier.
     * 
     * @return the string
     */
    protected String buildEndpointIdentifier() {
        return buildEndpointIdentifier(this.getServiceQName(), this.getPort().getName(), null);
    }

    /**
     * Creates the endpoint info.
     * 
     * @return the document fragment
     * 
     * @throws InvalidAddressException
     */
    protected abstract DocumentFragment createEndpointInfo() throws InvalidAddressException;

    /**
     * Creates the endpoint name.
     * 
     * @return the string
     */
    protected String createEndpointName() {
        return this.getPort().getName();
    }

    /**
     * Creates the protocol headers.
     * 
     * @return the document fragment
     */
    protected DocumentFragment createProtocolHeaders() {
        return null;
    }

    /**
     * Gets the port.
     * 
     * @return the port
     */
    protected SPDXPort getPort() {
        return this.port;
    }

    /**
     * Gets the port type Q name.
     * 
     * @return the port type Q name
     */
    protected QName getPortTypeQName() {
        return this.port.getBinding().getPortType().getQName();
    }

    /**
     * Init.
     * 
     * @throws InvalidAddressException
     */
    protected void init() throws InvalidAddressException {
        this.config.setName(QName.valueOf(this.buildEndpointIdentifier()));
        this.config.setEndpointName(this.createEndpointName());
        this.config.setImplementation(this.createEndpointInfo());
        this.config.setInvoke(this.getServiceQName());
        this.config.setProtocolHeaders(this.createProtocolHeaders());
    }
}
