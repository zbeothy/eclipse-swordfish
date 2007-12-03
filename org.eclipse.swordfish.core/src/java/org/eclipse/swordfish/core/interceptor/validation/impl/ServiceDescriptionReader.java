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
package org.eclipse.swordfish.core.interceptor.validation.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class ServiceDescriptionReader.
 * 
 */
public final class ServiceDescriptionReader {

    /** logger. */
    private static final Log LOG = SBBLogFactory.getLog(ServiceDescriptionReader.class);

    /**
     * New instance.
     * 
     * @param pServiceDescription
     *        document based
     * 
     * @return aServiceDescription a ServiceDescriptionReader
     * 
     * @throws IOException
     *         never thrown
     * @throws SAXException
     *         never thrown
     */
    public static ServiceDescriptionReader newInstance(final Document pServiceDescription) throws IOException, SAXException {
        ServiceDescriptionReader aServiceDescriptionReader = new ServiceDescriptionReader();
        aServiceDescriptionReader.setServiceDescription(pServiceDescription);
        return aServiceDescriptionReader;
    }

    /**
     * New instance.
     * 
     * @param pServiceDescription
     *        description
     * 
     * @return ServiceDescriptionReader reader
     * 
     * @throws IOException
     *         exception
     * @throws SAXException
     *         exception
     */
    public static ServiceDescriptionReader newInstance(final Element pServiceDescription) throws IOException, SAXException {
        ServiceDescriptionReader aServiceDescriptionReader = new ServiceDescriptionReader();
        aServiceDescriptionReader.setServiceDescription(pServiceDescription);
        return aServiceDescriptionReader;
    }

    /**
     * New instance.
     * 
     * @param pServiceDescription
     *        the ServiceDescription (file based)
     * 
     * @return aServiceDescription a ServiceDescriptionReader
     * 
     * @throws IOException
     *         if the file isn't avaible
     * @throws SAXException
     *         creating the reader
     */
    public static ServiceDescriptionReader newInstance(final File pServiceDescription) throws IOException, SAXException {
        if (!pServiceDescription.exists()) throw new FileNotFoundException("File: " + pServiceDescription + " does not exist");
        ServiceDescriptionReader aServiceDescriptionReader = new ServiceDescriptionReader();
        aServiceDescriptionReader.setServiceDescription(pServiceDescription);
        return aServiceDescriptionReader;
    }

    /**
     * New instance.
     * 
     * @param pServiceDescription
     *        string based
     * 
     * @return aServiceDescription a ServiceDescriptionReader
     * 
     * @throws IOException
     *         v
     * @throws SAXException
     *         v
     */
    public static ServiceDescriptionReader newInstance(final String pServiceDescription) throws IOException, SAXException {
        ServiceDescriptionReader aServiceDescriptionReader = new ServiceDescriptionReader();
        aServiceDescriptionReader.setServiceDescription(pServiceDescription);
        return aServiceDescriptionReader;
    }

    /** the servicedescription from the serice registry. */
    private Element serviceDescription;

    /**
     * ctor of the ServiceDescriptionReader.
     */
    private ServiceDescriptionReader() {
    }

    /**
     * Gets the schemas.
     * 
     * @return aSchema
     * 
     * @throws SAXException
     *         parse exception
     */
    public NodeList getSchemas() throws SAXException {
        LOG.debug("...start getSchemas()");
        if (this.getServiceDescription() == null) throw new SAXException("ServiceDescription is not set");

        NodeList xsdList =
                this.serviceDescription.getElementsByTagNameNS(ValidationProcessorBean.getXsdSchema(),
                        ValidationProcessorBean.XSD_SCHEMA_ELEMENT);

        LOG.debug("...found : " + xsdList.getLength() + " schemas in the serviceDescription");
        LOG.debug("...end getSchema()");
        return xsdList;
    }

    /**
     * Gets the service description.
     * 
     * @return servicedescription
     */
    public Element getServiceDescription() {
        return this.serviceDescription;
    }

    /**
     * Sets the service description.
     * 
     * @param pServiceDescription
     *        servicedescription from sr
     * 
     * @throws SAXException
     *         parseexcetpion never thrown
     * @throws IOException
     *         ioexception never thrown
     */
    private void setServiceDescription(final Document pServiceDescription) throws SAXException, IOException {
        this.serviceDescription = pServiceDescription.getDocumentElement();
    }

    /**
     * Sets the service description.
     * 
     * @param pServiceDescription
     *        description
     * 
     * @throws SAXException
     *         exception
     * @throws IOException
     *         exception
     */
    private void setServiceDescription(final Element pServiceDescription) throws SAXException, IOException {
        this.serviceDescription = pServiceDescription;
    }

    /**
     * Sets the service description.
     * 
     * @param pServiceDescription
     *        servicedescription from sr
     * 
     * @throws IOException
     *         IOException
     * @throws SAXException
     *         SAXException
     */
    private void setServiceDescription(final File pServiceDescription) throws IOException, SAXException {

        if (pServiceDescription == null) return;

        try {

            DocumentBuilder db = TransformerUtil.getDocumentBuilder();
            db.setErrorHandler(new ValidationErrorHandler(LOG));
            this.serviceDescription = db.parse(pServiceDescription).getDocumentElement();
        } catch (ParserConfigurationException e) {
            throw new SAXException(e.getMessage(), e);
        }
    }

    /**
     * Sets the service description.
     * 
     * @param pServiceDescription
     *        servicedescription from sr
     * 
     * @throws SAXException
     *         SAXException
     * @throws IOException
     *         IOException
     */
    private void setServiceDescription(final String pServiceDescription) throws SAXException, IOException {

        if (pServiceDescription == null) return;

        try {
            DocumentBuilder db = TransformerUtil.getDocumentBuilder();
            db.setErrorHandler(new ValidationErrorHandler(LOG));
            this.serviceDescription = db.parse(new ByteArrayInputStream(pServiceDescription.getBytes())).getDocumentElement();
        } catch (ParserConfigurationException e) {
            throw new SAXException(e.getMessage(), e);
        }
    }

}
