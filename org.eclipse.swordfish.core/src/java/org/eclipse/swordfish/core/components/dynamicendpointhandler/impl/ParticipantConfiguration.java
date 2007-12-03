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

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.SBBRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * The Class ParticipantConfiguration.
 */
public class ParticipantConfiguration {

    /** The name. */
    private QName name;

    /** The implementation config. */
    private DocumentFragment implementationConfig;

    /** The invoke. */
    private QName invoke;

    /** The protocol headers. */
    private DocumentFragment protocolHeaders;

    /** The deployment URL. */
    private URL deploymentURL;

    /** The endpoint name. */
    private String endpointName;

    /** The fail on startup error. */
    private boolean failOnStartupError;

    /**
     * Instantiates a new participant configuration.
     */
    public ParticipantConfiguration() {
    }

    /**
     * Instantiates a new participant configuration.
     * 
     * @param deploymentURL
     *        the deployment URL
     * @param name
     *        the name
     * @param epn
     *        the epn
     * @param implementationConfig
     *        the implementation config
     * @param invoke
     *        the invoke
     * @param protocolHeaders
     *        the protocol headers
     * @param failOnStartupError
     *        the fail on startup error
     */
    public ParticipantConfiguration(final URL deploymentURL, final QName name, final String epn,
            final DocumentFragment implementationConfig, final QName invoke, final DocumentFragment protocolHeaders,
            final boolean failOnStartupError) {
        this.deploymentURL = deploymentURL;
        this.name = name;
        this.implementationConfig = implementationConfig;
        this.invoke = invoke;
        this.protocolHeaders = protocolHeaders;
        this.failOnStartupError = failOnStartupError;
        this.endpointName = epn;
    }

    /**
     * The URL of the deployed artifact for this components config; allows fetching further config
     * files.
     * 
     * @return The URL
     */
    public URL getDeploymentURL() {
        return this.deploymentURL;
    }

    /**
     * Gets the endpoint name.
     * 
     * @return the endpoint name
     */
    public String getEndpointName() {
        return this.endpointName;
    }

    /**
     * Gets the fail on startup error.
     * 
     * @return the fail on startup error
     */
    public boolean getFailOnStartupError() {
        return this.failOnStartupError;
    }

    /**
     * Gets the specific implementation specific XML configuration.
     * 
     * @return The configuration, never null.
     */
    public DocumentFragment getImplementation() {
        return this.implementationConfig;
    }

    /**
     * Needs to be generalized for more than 1 invoke & more data under invoke.
     * 
     * @return The next-service to invoke, may be null if no next service.
     */
    public QName getInvoke() {
        return this.invoke;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public QName getName() {
        return this.name;
    }

    /**
     * DPWN feature; fixed header values (cookies) to append to inbound messages.
     * 
     * @return The headers, or null if none.
     */
    public DocumentFragment getProtocolHeaders() {
        return this.protocolHeaders;
    }

    /**
     * Sets the endpoint name.
     * 
     * @param endpointName
     *        the new endpoint name
     */
    public void setEndpointName(final String endpointName) {
        this.endpointName = endpointName;
    }

    /**
     * Sets the implementation.
     * 
     * @param documentFragment
     *        the new implementation
     */
    public void setImplementation(final DocumentFragment documentFragment) {
        this.implementationConfig = documentFragment;
    }

    /**
     * Sets the invoke.
     * 
     * @param invoke
     *        the new invoke
     */
    public void setInvoke(final QName invoke) {
        this.invoke = invoke;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *        the new name
     */
    public void setName(final QName name) {
        this.name = name;
    }

    /**
     * Sets the protocol headers.
     * 
     * @param protocolHeaders
     *        the new protocol headers
     */
    public void setProtocolHeaders(final DocumentFragment protocolHeaders) {
        this.protocolHeaders = protocolHeaders;
    }

    /**
     * Write as XML.
     * 
     * @return the string
     */
    public String writeAsXML() {
        String ns = "http://xmlns.oracle.com/jbi/endpoints";
        Document doc;
        try {
            doc = TransformerUtil.getDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new SBBRuntimeException("Error creating Endpoint Document, cannot get Documentbuilder");
        }
        Element endpoint = doc.createElementNS(ns, "endpoint");
        TransformerUtil.setDefaultNamespace(endpoint, ns);
        doc.appendChild(endpoint);
        if (this.name != null) {
            TransformerUtil.appendNewElementValue(endpoint, ns, "name", this.name);
        }
        if (this.invoke != null) {
            Element invokeElement = TransformerUtil.appendNewElementValue(endpoint, ns, "invoke", this.invoke);
            if (this.endpointName != null) {
                invokeElement.setAttribute("name", this.endpointName);
            }
        }
        if (this.protocolHeaders != null) {
            Element header = TransformerUtil.appendNewElement(endpoint, ns, "headers");
            TransformerUtil.copyContentsTo(this.protocolHeaders, header);
        }
        if (this.implementationConfig != null) {
            Element impl = TransformerUtil.appendNewElement(endpoint, ns, "implementation");
            TransformerUtil.copyContentsTo(this.implementationConfig, impl);
        }

        return TransformerUtil.stringFromDomNode(doc, true);
    }
}
