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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.parsers.DocumentBuilder;
import org.eclipse.swordfish.core.components.srproxy.ServiceInfo;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class ServiceInfoImpl.
 */
public class ServiceInfoImpl implements ServiceInfo {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(ServiceInfoImpl.class);

    /** service description. */
    private Definition serviceDescription = null;

    /** The writer. */
    private WSDLWriter writer;

    /** The builder. */
    private DocumentBuilder builder;

    /**
     * The Constructor.
     * 
     * @param serviceDesc
     *        service description
     */
    public ServiceInfoImpl(final Definition serviceDesc) {
        this.serviceDescription = serviceDesc;
        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            this.writer = factory.newWSDLWriter();
            this.builder = TransformerUtil.getDocumentBuilder();
        } catch (Exception e) {
            LOG.error("ServiceInfoImpl failed to instantiate properly", e);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @return the service description
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.ServiceInfo#getServiceDescription()
     */
    public Definition getServiceDescription() {
        return this.serviceDescription;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.ServiceInfo#getServiceDescriptionAsElement()
     */
    public Element getServiceDescriptionAsElement() {
        return this.definitionToElement(this.serviceDescription);
    }

    /**
     * Definition to element.
     * 
     * @param def
     *        the def
     * 
     * @return the element
     */
    private Element definitionToElement(final Definition def) {
        Element result = null;

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            synchronized (this.writer) {
                this.writer.writeWSDL(def, outStream);
            }
            ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
            synchronized (this.builder) {
                Document document = this.builder.parse(inStream);
                result = document.getDocumentElement();
            }
        } catch (Exception e) {
            LOG.error("cannot map WSDL Definition to DOM ELement", e);
        }
        return result;
    }

}
