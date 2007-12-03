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
import javax.xml.namespace.QName;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddress;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.EndpointProperties;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.InvalidAddressException;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * The Class JMSDynamicNotificationEndpoint.
 */
public class JMSDynamicNotificationEndpoint extends AbstractJMSDynamicEndpoint {

    /** The identity. */
    private UnifiedParticipantIdentity identity;

    /** The selector. */
    private String selector;

    /** The timeout. */
    private Integer timeout;

    /** The durable. */
    private Boolean durable;

    /**
     * Instantiates a new JMS dynamic notification endpoint.
     * 
     * @param serviceName
     *        the service name
     * @param port
     *        the port
     * @param operationName
     *        the operation name
     * @param participant
     *        the participant
     * @param repos
     *        the repos
     * @param props
     *        the props
     * 
     * @throws InvalidAddressException
     */
    public JMSDynamicNotificationEndpoint(final QName serviceName, final SPDXPort port, final String operationName,
            final UnifiedParticipantIdentity participant, final LocalEndpointRepository repos, final Map props)
            throws InvalidAddressException {
        super(serviceName, port);
        this.identity = participant;
        this.selector = (String) props.get(EndpointProperties.SELECTOR);
        this.timeout = (Integer) props.get(EndpointProperties.TIMEOUT);
        this.durable =
                (null != (Boolean) props.get(EndpointProperties.DURABLE) ? (Boolean) props.get(EndpointProperties.DURABLE)
                        : new Boolean(false));
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
     * Builds the endpoint identifier.
     * 
     * @return the string
     */
    @Override
    protected String buildEndpointIdentifier() {
        return buildEndpointIdentifier(this.getServiceQName(), this.getPort().getName(), this.identity);
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
        Element el = DOMUtil.appendNewElement(frag, soapNs, "jms-event-source");
        DOMUtil.setDefaultNamespace(el, soapNs);
        String jmsUrl = this.getJMSQueueURL(this.getPort());
        DOMUtil.appendNewElementValue(el, soapNs, "listener-url", jmsUrl);

        if (this.durable.booleanValue()) {
            String subscriberName = this.identity.getParticipantIdentity().getApplicationID();
            if (this.identity.getParticipantIdentity().getInstanceID() != null) {
                subscriberName += ":" + this.identity.getParticipantIdentity().getInstanceID();
            }
            DOMUtil.appendNewElementValue(el, soapNs, "durable-subscriber-name", subscriberName);
        }
        if (this.timeout != null) {
            DOMUtil.appendNewElementValue(el, soapNs, "timeout", this.timeout.toString());
        }

        if (this.selector != null) {
            DOMUtil.appendNewElementValue(el, soapNs, "message-selector", "<![CDATA[" + this.selector + "]]>");
        }

        return frag;
    }

    /**
     * Creates the endpoint name.
     * 
     * @return the string
     */
    @Override
    protected String createEndpointName() {
        return this.getPort().getName() + "-" + this.identity.getReproducibleHash();
    }

    /**
     * Creates the protocol headers.
     * 
     * @return the document fragment
     */
    @Override
    protected DocumentFragment createProtocolHeaders() {
        String applicationId = this.identity.getParticipantIdentity().getApplicationID();
        String instanceId = this.identity.getParticipantIdentity().getInstanceID();
        Document doc = DOMUtil.newDocument();
        DocumentFragment frag = doc.createDocumentFragment();
        Element el = DOMUtil.appendNewElementValue(frag, HeaderUtil.SBB_NS, "ApplicationId", applicationId);
        DOMUtil.setDefaultNamespace(el, HeaderUtil.SBB_NS);
        if (null != instanceId) {
            el = DOMUtil.appendNewElementValue(frag, HeaderUtil.SBB_NS, "InstanceId", instanceId);
            DOMUtil.setDefaultNamespace(el, HeaderUtil.SBB_NS);
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
                url =
                        "jms:/" + jmsAddress.getJndiDestinationName() + "?" + "vendor=JNDI" + "&java.naming.factory.initial="
                                + jmsAddress.getInitialContextFactory() + "&java.naming.provider.url="
                                + jmsAddress.getJndiProviderURL();
                if (jmsAddress.getJavaNamingSecurityPrincipal() != null) {
                    url = url + "&java.naming.provider.user=" + jmsAddress.getJavaNamingSecurityPrincipal();
                }
                if (jmsAddress.getJavaNamingSecurityPrincipal() != null) {
                    url = url + "&java.naming.provider.password=" + jmsAddress.getJavaNamingSecurityCredentials();
                }

                if (jmsAddress.getDestinationUser() != null) {
                    url = url + "&;jms-user=" + jmsAddress.getDestinationUser();
                }
                if (jmsAddress.getDestinationPassword() != null) {
                    url = url + "&;jms-password=" + jmsAddress.getDestinationPassword();
                }

                url = url + "&ConnectionFactoryJNDIName=" + jmsAddress.getJndiConnectionFactoryName();
            }
        }
        return url;
    }
}
