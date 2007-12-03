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
package org.eclipse.swordfish.configrepos.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swordfish.configrepos.configuration.sources.SingleConfigurationSource;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * The ConfigurationSourceLister will allow creating a list of Spring Resources based on
 * configuration files that:
 * <ul>
 * <li>Are defined within a specific tree.</li>
 * <li>Are located relative to a base path.</li>
 * <li>Have a filename which matches a pattern.</li>
 * </ul>
 * 
 */
public class ConfigurationSourceLister implements ResourceLoaderAware {

    /** Logger used by this component. */
    private Logger logger = Logger.getLogger(ConfigurationSourceLister.class.getName(), "ConfigReposMessageBundle");

    /** Spring resource loader used to find configurations. */
    private ResourceLoader loader = null;

    /** Base path to look for configurations. */
    private String basePath;

    /** Patter for configuration files. */
    private String pattern;

    /** Tree which holds the configuration. */
    private String treeQualifier;

    /**
     * Instantiates a new configuration source lister.
     */
    public ConfigurationSourceLister() {
        super();
    }

    /**
     * Gets the base path.
     * 
     * @return Returns the basePath.
     */
    public String getBasePath() {
        return this.basePath;
    }

    /**
     * Gets the pattern.
     * 
     * @return Returns the pattern.
     */
    public String getPattern() {
        return this.pattern;
    }

    /**
     * Gets the tree qualifier.
     * 
     * @return Returns the treeQualifier.
     */
    public String getTreeQualifier() {
        return this.treeQualifier;
    }

    /**
     * Load the configurations according to the properties set for the bean.
     * 
     * @return a list of configurations
     * 
     * @throws IOException
     *         in case the configurations could not be read
     * @throws ConfigurationException
     *         in case any jakarta commons configuration exception occured
     */
    public List load() throws ConfigurationException, IOException {
        if (this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(ConfigurationSourceLister.class.getName(), "load");
        }
        List result = null;

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.loader);
            String path =
                    StringUtils.stripEnd(this.basePath, File.separator) + File.separatorChar
                            + StringUtils.stripStart(this.treeQualifier, File.separator) + File.separatorChar
                            + StringUtils.stripStart(this.pattern, File.separator);
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.log(Level.FINEST,
                        "org.eclipse.swordfish.configrepos.configuration.sources.CONFIGLISTER_SEARCHINGWITHPATTERN",
                        new Object[] {path});
            }
            Resource[] foundresources = resolver.getResources(path);

            result = new ArrayList(foundresources.length);
            for (int pos = 0; pos < foundresources.length; pos++) {
                if (this.logger.isLoggable(Level.INFO)) {
                    this.logger.log(Level.INFO,
                            "org.eclipse.swordfish.configrepos.configuration.sources.CONFIGLISTER_LOADINGFROMRESOURCE",
                            new Object[] {foundresources[pos].getDescription()});
                }
                XMLConfiguration config = new XMLConfiguration();
                config.load(foundresources[pos].getInputStream());
                result.add(new SingleConfigurationSource(config));
            }

            return result;
        } finally {
            if (this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(ConfigurationSourceLister.class.getName(), "load", result);
            }
        }
    }

    /**
     * Sets the base path.
     * 
     * @param basePath
     *        The basePath to set.
     */
    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    /**
     * Sets the pattern.
     * 
     * @param pattern
     *        The pattern to set.
     */
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aLoader
     *        the a loader
     * 
     * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    public void setResourceLoader(final ResourceLoader aLoader) {
        this.loader = aLoader;
    }

    /**
     * Sets the tree qualifier.
     * 
     * @param treeQualifier
     *        The treeQualifier to set.
     */
    public void setTreeQualifier(final String treeQualifier) {
        this.treeQualifier = treeQualifier;
    }
}
