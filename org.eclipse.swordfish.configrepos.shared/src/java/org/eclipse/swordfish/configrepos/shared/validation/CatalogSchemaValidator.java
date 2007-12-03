/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.configrepos.shared.validation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.SAXException;

/**
 * The Class CatalogSchemaValidator.
 */
public class CatalogSchemaValidator implements SchemaValidator {

    /** logger for this class. */
    private Logger log = Logger.getLogger(CatalogSchemaValidator.class.getName());

    /** validation error handler. */
    private ValidationErrorHandler errorHandler = null;

    /** validation error handler. */
    private SchemaFactory factory = null;

    /** The resolver. */
    private ResourceResolver resolver;

    /**
     * The Constructor.
     * 
     * @param defaultCatalog
     *        the default catalog
     * 
     * @throws Exception
     *         exception
     */
    public CatalogSchemaValidator(final String defaultCatalog) throws Exception {
        this(new String[] {defaultCatalog});
    }

    /**
     * The Constructor.
     * 
     * @param urls
     *        the urls
     * 
     * @throws Exception
     *         exception
     */
    public CatalogSchemaValidator(final String[] urls) throws Exception {
        String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        this.factory = SchemaFactory.newInstance(language);
        this.errorHandler = new ValidationErrorHandler(this.log);
        this.factory.setErrorHandler(this.errorHandler);
        this.resolver = new ResourceResolver(this.log, urls);
        this.factory.setResourceResolver(this.resolver);
    }

    /**
     * Adds the external resolver.
     * 
     * @param externalResolver
     *        the external resolver
     */
    public void addExternalResolver(final ExternalResolver externalResolver) {
        this.resolver.addExternalResolver(externalResolver);
    }

    /**
     * Resolve namespace.
     * 
     * @param nameSpaceUri
     *        the name space uri
     * 
     * @return the input stream
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws ResourceException
     */
    public InputStream resolveNamespace(final String nameSpaceUri) throws MalformedURLException, IOException, ResourceException {
        LSInput input = this.resolver.resolveResource(null, nameSpaceUri, null, null, null);
        if (input == null) throw new ResourceException("Cannot resolve resource " + nameSpaceUri + " .");
        if (input.getByteStream() != null)
            return input.getByteStream();
        else if (input.getSystemId() != null)
            return new URL(input.getSystemId()).openStream();
        else
            throw new ResourceException("Cannot resolve namespace " + nameSpaceUri + ".");
    }

    /**
     * (non-Javadoc).
     * 
     * @param schemaSource
     *        the schema source
     * @param message
     *        the message
     * 
     * @throws SAXException
     * @throws IOException
     * 
     * @see org.eclipse.swordfish.processing.components.validation.SchemaValidator#validate(org.w3c.dom.NodeList,
     *      java.lang.String)
     */
    public void validate(final StreamSource schemaSource, final Source message) throws SAXException, IOException {

        ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
        this.resolver.setContextClassLoader(currThreadLoader);
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {

            Schema schema;
            schema = this.factory.newSchema(schemaSource);

            // create a Validator instance, which can be used to validate an
            // instance document
            Validator validator = schema.newValidator();

            // validate the DOM tree
            validator.validate(message);
        } finally {
            this.resolver.unsetContextClassLoader();
            Thread.currentThread().setContextClassLoader(currThreadLoader);
        }
    }

}
