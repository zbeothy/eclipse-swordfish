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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.swordfish.configrepos.shared.validation.protocolhandler.classpath.Handler;
import org.eclipse.swordfish.configrepos.shared.validation.util.TransformerUtil;
import org.w3c.dom.Document;

/**
 * The Class ValidationProcessor.
 * 
 */

public class ValidationProcessor {

    private static boolean classpathURLHandlerRegistered = false;

    private static final String URL_HANDLER_PACKAGE = "org.eclipse.swordfish.configrepos.shared.validation.protocolhandler";

    private static final String URL_HANDLER_PROP = "java.protocol.handler.pkgs";

    private static void ensureClasspathURLHandlerRegistration() {
        // TODO FIND ALTERNATIVE SOLUTION, classloader will not find handler :-(
        if (classpathURLHandlerRegistered) return;
        String val = System.getProperty(URL_HANDLER_PROP, "");
        if (val.length() > 0) val += "|";
        val += URL_HANDLER_PACKAGE;
        System.setProperty(URL_HANDLER_PROP, val);
        classpathURLHandlerRegistered = true;
    }

    /** logger for this class. */
    private Logger log = Logger.getLogger(ValidationProcessor.class.getName());

    /** schema validator. */
    private CatalogSchemaValidator schemaValidator = null;

    /**
     * Constructor.
     * 
     * @param defaultCatalog
     *        the default catalog
     * 
     * @throws ValidationException
     */
    public ValidationProcessor(final String defaultCatalog) throws ValidationException {
        ensureClasspathURLHandlerRegistration();
        try {
            this.init(defaultCatalog);
        } catch (Exception e) {
            throw new ValidationException(e);
        }
    }

    /**
     * This method adds an external resolver.
     * 
     * @param externalResolver
     *        external resolver
     */

    public void addExternalResolver(final ExternalResolver externalResolver) {
        this.schemaValidator.addExternalResolver(externalResolver);
    }

    /**
     * initializing method.
     * 
     * @param defaultCatalog
     *        the default catalog
     * 
     * @throws Exception
     *         exception
     */
    public void init(final String defaultCatalog) throws Exception {
        String[] parts = defaultCatalog.split(":");
        if (parts.length != 2) throw new MalformedURLException();
        URL catalogUrl = null;
        if ("classpath".equalsIgnoreCase(parts[0])) {
            catalogUrl = new URL(parts[0], "", -1, parts[1], new Handler());
        } else {
            catalogUrl = new URL(defaultCatalog);
        }
        this.schemaValidator = new CatalogSchemaValidator(defaultCatalog);
    }

    /**
     * This method resolves a namespace uri using the resolvess of the validator.
     * 
     * @param nameSpaceUri
     *        namespace uri
     * 
     * @return InputStream
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws ResourceException
     */

    public InputStream resolveNamespace(final String nameSpaceUri) throws MalformedURLException, IOException, ResourceException {
        return this.schemaValidator.resolveNamespace(nameSpaceUri);
    }

    /**
     * This method is identical to
     * 
     * @see addExternalResolver. It exists solely for the purpose of making Spring happy
     * 
     * @param externalResolvers
     *        external resolver.
     */

    public void setExternalResolvers(final List/* <ExternalResolver> */externalResolvers) {
        for (Iterator iter = externalResolvers.iterator(); iter.hasNext();) {
            ExternalResolver externalResolver = (ExternalResolver) iter.next();
            this.schemaValidator.addExternalResolver(externalResolver);
        }
    }

    /**
     * This method validates a given xml document against a xml schema If we have the Schema, we are
     * able to create a validator and then we validate the document.
     * 
     * @param document
     *        source document
     * @param schema
     *        represented by a StreamSource object
     * 
     * @throws ValidationException
     */
    public void validate(final Document document, final StreamSource schema) throws ValidationException {
        Source source = new DOMSource(document.getDocumentElement());
        this.validate(source, schema);
    }

    /**
     * This method validates a given xml document against a xml schema represented by a StreamSource
     * object. If we have the Schema, we are able to create a validator and then we validate the
     * document
     * 
     * @param schema
     *        represented by a StreamSource object
     * @param aSource
     *        the a source
     * 
     * @throws ValidationException
     */

    public void validate(final Source aSource, final StreamSource schema) throws ValidationException {
        if (aSource == null) throw new ValidationException("Source to validate against schema is null.");

        if (TransformerUtil.isSourceEmpty(aSource)) {
            this.log.finest("No data to process for validation.");
            return;
        }

        long beforeTime = System.currentTimeMillis();
        try {
            if (schema == null) throw new ValidationException("No schema found for validation");
            this.schemaValidator.validate(schema, aSource);
        } catch (Exception e) {
            throw new ValidationException(e);
        } finally {
            this.log.info("Validation took " + (System.currentTimeMillis() - beforeTime) + " ms.");
        }
    }

}
