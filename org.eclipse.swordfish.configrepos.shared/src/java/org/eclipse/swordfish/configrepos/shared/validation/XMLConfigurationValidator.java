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
import java.util.logging.Logger;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;

/**
 * .
 * 
 */
public class XMLConfigurationValidator {

    /** The logger. */

    private Logger log;

    /** The validation processor. */

    private ValidationProcessor validationProcessor;

    /** The default catalog. */
    private String defaultCatalog = null;

    /**
     * Instantiates a new XML configuration validator.
     * 
     * @param log
     *        the log
     * @param defaultCatalog
     *        the default catalog
     * 
     * @throws MalformedURLException
     */
    public XMLConfigurationValidator(final Logger log, final String defaultCatalog) throws MalformedURLException {
        this.log = log;
        if (null != defaultCatalog) {
            this.defaultCatalog = defaultCatalog;
        } else {
            this.defaultCatalog = "classpath:catalog.xml";
        }
    }

    /**
     * This method adds an external resolver.
     * 
     * @param externalResolver
     *        external resolver
     * 
     * @throws ValidationException
     */

    public void addExternalResolver(final ExternalResolver externalResolver) throws ValidationException {
        if (this.validationProcessor == null) {
            this.validationProcessor = new ValidationProcessor(this.defaultCatalog);
        }
        this.validationProcessor.addExternalResolver(externalResolver);
        this.log.finest("External resolver added.");
    }

    /**
     * Validate configuration.
     * 
     * @param configuration
     *        the configuration
     * 
     * @throws ConfigurationException
     * @throws ValidationException
     * @throws MalformedURLException
     * @throws IOException
     * @throws ResourceException
     */
    public void validateConfiguration(final XMLConfiguration configuration) throws ConfigurationException, ValidationException,
            MalformedURLException, IOException, ResourceException {

        if (this.validationProcessor == null) {
            this.validationProcessor = new ValidationProcessor(this.defaultCatalog);
        }

        configuration.rebuildDocument();
        String nameSpaceUri = configuration.getDocument().getFirstChild().getNamespaceURI();
        if (nameSpaceUri == null)
            throw new ValidationException("Error during validation - " + "configuration without namespace "
                    + configuration.toString() + " .");
        InputStream schemaInput = this.validationProcessor.resolveNamespace(nameSpaceUri);
        this.validationProcessor.validate(configuration.getDocument(), new StreamSource(schemaInput));
        this.log.finest("Configuration\n" + configuration.toString() + "\nvalidated.");
    }

    /**
     * Validate configuration.
     * 
     * @param configuration
     *        the configuration
     * @param schema
     *        the schema
     * 
     * @throws ConfigurationException
     * @throws ValidationException
     */
    public void validateConfiguration(final XMLConfiguration configuration, final StreamSource schema)
            throws ConfigurationException, ValidationException {

        if (this.validationProcessor == null) {
            this.validationProcessor = new ValidationProcessor(this.defaultCatalog);
        }
        configuration.rebuildDocument();
        this.validationProcessor.validate(configuration.getDocument(), schema);
        this.log.finest("Configuration " + configuration.toString() + " validated.");
    }
}
