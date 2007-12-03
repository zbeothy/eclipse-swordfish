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

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddressImpl;
import org.eclipse.swordfish.configrepos.wsdl.extensions.jms.JMSAddressSerializer;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorConstants;
import org.eclipse.swordfish.core.components.locatorproxy.impl.LocatorAddressDeserializer;
import org.eclipse.swordfish.core.components.locatorproxy.impl.LocatorAddressSerializer;
import org.eclipse.swordfish.core.components.locatorproxy.impl.LocatorLocationImpl;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The Class DefinitionHelper.
 */
public class DefinitionHelper {

    /** logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(DefinitionHelper.class);

    /** The instance. */
    private static DefinitionHelper instance = null;

    /**
     * Gets the single instance of DefinitionHelper.
     * 
     * @return single instance of DefinitionHelper
     */
    public static DefinitionHelper getInstance() {
        if (instance == null) {
            try {
                instance = new DefinitionHelper();
            } catch (Exception e) {
                throw new RuntimeException("Cannot instantiate WSDL utilities");
            }
        }
        return instance;
    }

    /** WSDL4J reader. */
    private WSDLReader reader;

    /** WSDL4J writer. */
    private WSDLWriter writer;

    /** SAX2 parser. */
    private DocumentBuilder builder;

    /**
     * constructor.
     * 
     * @throws Exception
     *         exception
     */
    private DefinitionHelper() throws Exception {

        WSDLFactory factory = WSDLFactory.newInstance();
        ExtensionRegistry reg = factory.newPopulatedExtensionRegistry();

        // register extension serializer and the extension types for JMS
        reg.registerDeserializer(Port.class, new QName("http://schemas.xmlsoap.org/wsdl/jms/", "address"),
                new JMSAddressSerializer());
        reg
            .registerSerializer(Port.class, new QName("http://schemas.xmlsoap.org/wsdl/jms/", "address"),
                    new JMSAddressSerializer());
        reg.mapExtensionTypes(Port.class, new QName("http://schemas.xmlsoap.org/wsdl/jms/", "address"), JMSAddressImpl.class);

        // register extension serializer and the extension types for Locator
        // Adresses
        reg.registerDeserializer(Port.class, LocatorConstants.SPDX_SOP_LOCATOR, new LocatorAddressDeserializer());
        reg.registerSerializer(Port.class, LocatorConstants.SPDX_SOP_LOCATOR, new LocatorAddressSerializer());
        reg.mapExtensionTypes(Port.class, LocatorConstants.SPDX_SOP_LOCATOR, LocatorLocationImpl.class);

        // register extension serializer and the extension types for
        // partnerlinks
        reg.registerDeserializer(Definition.class, new QName("http://schemas.xmlsoap.org/ws/2003/05/partner-link/",
                "partnerLinkType"), new PartnerLinkTypeDeserializer());
        reg.registerSerializer(Definition.class,
                new QName("http://schemas.xmlsoap.org/ws/2003/05/partner-link/", "partnerLinkType"),
                new PartnerLinkTypeSerializer());
        reg.mapExtensionTypes(Definition.class,
                new QName("http://schemas.xmlsoap.org/ws/2003/05/partner-link/", "partnerLinkType"), PartnerLinkTypeImpl.class);

        this.reader = factory.newWSDLReader();
        this.reader.setExtensionRegistry(reg);

        this.reader.setFeature("javax.wsdl.importDocuments", false);
        this.reader.setFeature("javax.wsdl.verbose", false);

        this.writer = factory.newWSDLWriter();

        this.builder = TransformerUtil.getDocumentBuilder();

    }

    /**
     * Clone definition.
     * 
     * @param def
     *        the def
     * 
     * @return the definition
     * 
     * @throws WSDLException
     */
    public Definition cloneDefinition(final Definition def) throws WSDLException {
        Definition result = null;
        try {
            Document doc = null;
            doc = this.writer.getDocument(def);
            result = this.elementToDefinition(doc.getDocumentElement());
        } catch (WSDLException e) {
            LOG.error("WSDL Exception", e);
        } catch (Exception e) {
            LOG.error("WSDL cloning Exception", e);
        }
        return result;
    }

    /**
     * Converts a W3C Element to a WSDL4J Definition.
     * 
     * @param elem
     *        the Element
     * 
     * @return the Definition
     * 
     * @throws Exception
     *         exception
     */
    public Definition elementToDefinition(final Element elem) throws Exception {
        Definition result = null;

        try {
            synchronized (this.reader) {
                result = this.reader.readWSDL((String) null, elem);
            }
        } catch (WSDLException e) {
            LOG.error("WSDL Exception", e);
        }

        return result;
    }

    /**
     * File todefinition.
     * 
     * @param filename
     *        filename
     * 
     * @return Definition definition
     * 
     * @throws Exception
     *         exception
     */
    public Definition fileTodefinition(final String filename) throws Exception {
        Document document = this.builder.parse(new File(filename));
        return this.elementToDefinition(document.getDocumentElement());
    }

    /**
     * Input stream todefinition.
     * 
     * @param in
     *        the in
     * 
     * @return the definition
     * 
     * @throws Exception
     */
    public Definition inputStreamTodefinition(final InputStream in) throws Exception {
        Document document = this.builder.parse(in);
        return this.elementToDefinition(document.getDocumentElement());
    }

    /**
     * Converts a String with WSDL infoset to a WSDL4J Definition.
     * 
     * @param wsdlStr
     *        the WSDL string
     * 
     * @return the Definition
     * 
     * @throws WSDLException
     */
    public Definition stringToDefinition(final String wsdlStr) throws WSDLException {
        Definition result = null;

        try {
            Document doc = XMLUtil.docFromString(wsdlStr);
            synchronized (this.reader) {
                result = this.reader.readWSDL((String) null, doc.getDocumentElement());
            }
        } catch (WSDLException e) {
            LOG.error("WSDL Exception", e);
        } catch (UnsupportedEncodingException e) {
            LOG.error("WSDL parsing Exception", e);
        } catch (SAXException e) {
            LOG.error("WSDL parsing Exception", e);
        }

        return result;
    }
}
