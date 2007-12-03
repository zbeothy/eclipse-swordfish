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
package org.eclipse.swordfish.configrepos.configuration.sources;

import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.swordfish.configrepos.AbstractRepositorySource;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.resource.sources.ResourceSource;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.eclipse.swordfish.configrepos.shared.validation.ValidationException;
import org.eclipse.swordfish.configrepos.shared.validation.ValidationProcessor;

/**
 * This abstract class is used to base for all types of configuration sources, used in conjunction
 * with the SOP Configuration Repository Proxy.
 * 
 */
public abstract class AbstractConfigurationSource extends AbstractRepositorySource implements ConfigurationSource {

    /** Reference to a resource source for configuration schemas (XSD). */
    private ResourceSource schemasource = null;

    /** Reference to a XMLCompositeConfiguration object which can serve as a validator. */
    private ValidationProcessor validator = null;

    /**
     * Default constructor.
     */
    public AbstractConfigurationSource() {
        super();
    }

    /**
     * Return the assigned schema source.
     * 
     * @return Returns the schemasource.
     */
    public ResourceSource getSchemasource() {
        return this.schemasource;
    }

    /**
     * Return the assigned validator.
     * 
     * @return Returns the validator.
     */
    public ValidationProcessor getValidator() {
        return this.validator;
    }

    /**
     * Set a new schema source.
     * 
     * @param schemasource
     *        The schemasource to set.
     */
    public void setSchemasource(final ResourceSource schemasource) {
        this.schemasource = schemasource;
    }

    /**
     * Set the new validator.
     * 
     * @param validator
     *        The validator to set.
     */
    public void setValidator(final ValidationProcessor validator) {
        this.validator = validator;
    }

    /**
     * Find the appropriate schema.
     * 
     * @param treeQualifier
     *        to be applied
     * @param scopePath
     *        to be applied
     * @param filename
     *        of the schema
     * 
     * @return a list of input streams to schemas which will be used for configuration validation
     */
    protected InputStream lookupAppropriateSchema(final String treeQualifier, final ScopePath scopePath, final String filename) {

        try {
            InputStream in =
                    this.getSchemasource().getResource(treeQualifier, scopePath, "ConfigurationManager", filename + ".xsd");
            return in;
        } catch (ConfigurationRepositoryResourceException e) {
            return null;
        }
    }

    /**
     * Validate configuration.
     * 
     * @param aTreeQualifier
     *        to be used to find the schema
     * @param aScopePath
     *        to be applied to find the schema
     * @param config
     *        to validate
     * @param aSchemaname
     *        to use
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case there was any error while validating the data
     */
    protected void validateConfiguration(final String aTreeQualifier, final ScopePath aScopePath, final XMLConfiguration config,
            final String aSchemaname) throws ConfigurationRepositoryConfigException {
        InputStream schema = this.lookupAppropriateSchema(aTreeQualifier, aScopePath, aSchemaname);

        if (null != schema) {
            try {
                this.getValidator().validate(config.getDocument(), new StreamSource(schema));
            } catch (ValidationException e) {
                if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                    this.getLogger().log(
                            Level.FINEST,
                            this.getApplicationContext().getMessage(
                                    "org.eclipse.swordfish.configrepos.configuration.sources.DUMP_CONFIGURATION",
                                    new Object[] {config.toString()}, Locale.getDefault()));
                }
                throw new ConfigurationRepositoryConfigException("Error validating configuration from source '"
                        + config.getFileName() + "'", e);
            }
        }
    }
}
