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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.eclipse.swordfish.configrepos.spring.FileResourceManagerBean;
import org.eclipse.swordfish.configrepos.util.DirectorySourceUtil;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

/**
 * The Class DirectoryConfigurationSource.
 * 
 * @@org.springframework.jmx.export.metadata.ManagedResource (description="Directory configuration
 *                                                           source")
 */
public class DirectoryConfigurationSource extends AbstractConfigurationSource implements ResourceLoaderAware,
        WritebackConfigurationSource {

    /** The Constant TXID_HASHALG. */
    public static final String TXID_HASHALG = "SHA-1";

    /** The Constant TXID_CHARACTERENCODING. */
    public static final String TXID_CHARACTERENCODING = "UTF-8";

    /** Jakarta commons file resource manager bean. */
    private FileResourceManagerBean resourceManager = null;

    /** Is the resource loader which will be used to fetch the files. */
    private ResourceLoader resourceLoader = null;

    /** Path to the configuration file. Defaults to "./". */
    private String basePath = "./";

    /**
     * Prefix for the configuration files which will be searched for. Defaults to null.
     */
    private String filename = null;

    /**
     * Specific qualifier which will be preset. Defaults to null, which will require providing a
     * identifier parameter when fetching the configuration.
     */
    private ScopePath fixedScopePath = null;

    /** Suffix for the requested configuration files. Defaults to '.xml'. */
    private String suffix = ".xml";

    /**
     * Set whether an empty configuration object should be return, rather than create an error in
     * case the configuration file is not available.
     */
    private boolean returnEmptyForNonAvailable = false;

    /**
     * Instantiates a new directory configuration source.
     */
    // private final Pattern protocolmatcher =
    // Pattern.compile("(classpath|file|url|http):.*", 1);
    /**
     * Create a default configuration source.
     */
    public DirectoryConfigurationSource() {
        super();
    }

    /**
     * Will do nothing, since no resources are being held open.
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#close()
     */
    @Override
    public void close() {
        super.close();
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     *         the exception
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().log(Level.FINEST, "destroy " + this.getBeanName());
        }

        this.resourceLoader = null;
        this.resourceManager = null;
        super.destroy();
    }

    /**
     * Gets the base path.
     * 
     * @return String of the basePath property value.
     * 
     * @@org.springframework.jmx.export.metadata.ManagedAttribute (description="Base path to the
     *                                                            directory.") Return the basePath
     *                                                            property set for this
     *                                                            DirectoryConfigurationSource.
     */
    public String getBasePath() {
        if (null != this.getManager()) return this.getManager().getLocalResourceBase();
        return this.basePath;
    }

    /**
     * Fetch the configuration for a specific identifier. The file and basePath which will be
     * searched for will be compiled from the initialization parameters of the configuration source
     * and the aIdentifier parameter of the call.<b/> That is:
     * &lt;basePath&gt;/&lt;filename&gt;&lt;fixedScopePath | aIdentifier&gt;&lt;suffix/&gt;<b/>
     * Please note, that setting the fixedScopePath property will instruct this configuration source
     * to fetch exactly one file and to not use any provided aIdentifier parameter.
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * 
     * @return the configuration
     * 
     * @throws ConfigurationRepositoryConfigException
     *         the configuration repository config exception
     * 
     * @see org.eclipse.swordfish.configrepos.sources.ConfigurationSource#getConfiguration(ScopePath)
     */
    public Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        XMLConfiguration result = this.fetchConfiguration(aTreeQualifier, aScopePath);

        if ((null != this.getSchemasource()) && (null != this.getValidator())) {
            this.validateConfiguration(aTreeQualifier, aScopePath, result, this.filename);
        }

        return result;
    }

    /**
     * Gets the filename.
     * 
     * @return String containing the filename property value.
     * 
     * @@org.springframework.jmx.export.metadata.ManagedAttribute (description="Filename which is
     *                                                            being fetched.") Return the
     *                                                            filename property value.
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * Gets the fixed scope path.
     * 
     * @return String containing the fixedScopePath property value.
     * 
     * @@org.springframework.jmx.export.metadata.ManagedAttribute (description="Fixed scope path.")
     *                                                            Return the fixedScopePath property
     *                                                            value.
     */
    public ScopePath getFixedScopePath() {
        return this.fixedScopePath;
    }

    /**
     * Gets the resource manager.
     * 
     * @return Returns the resourceManager.
     */
    public FileResourceManagerBean getResourceManager() {
        return this.resourceManager;
    }

    /**
     * Gets the suffix.
     * 
     * @return String containing the suffix property value.
     * 
     * @@org.springframework.jmx.export.metadata.ManagedAttribute (description="Filename suffix
     *                                                            being used.") Return the suffix
     *                                                            property value.
     */
    public String getSuffix() {
        return this.suffix;
    }

    /**
     * Checks if is return empty for non available.
     * 
     * @return Returns the returnEmptyForNonAvailable.
     */
    public boolean isReturnEmptyForNonAvailable() {
        return this.returnEmptyForNonAvailable;
    }

    /**
     * Flush any transient data for a specific instance id or all instances.
     * 
     * @param aInstance
     *        the a instance
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#resychronize(java.lang.String)
     */
    @Override
    public void resynchronize(final String aInstance) {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().finest("NOP on resynchronization request.");
        }
    }

    /**
     * Set the basePath property of this DirectoryConfigurationSource. Whitespaces will be trimmed
     * away from both ends, basePath separator will be added to the end, if not already there.<b/>
     * Will create a runtime exception (IllegalArgumentException), in case a null value is being
     * passed in the call.
     * 
     * @param aPath
     *        which shall be set. Value 'null' is not allowed. Use "" to create a empty property.
     */
    public void setBasePath(final String aPath) {
        if ((null == aPath) || (aPath.trim().length() == 0))
            throw new IllegalArgumentException(this.getApplicationContext().getMessage(
                    "org.eclipse.swordfish.configrepos.configuration.sources.NULLBASEPATHPROPERROR", null, Locale.getDefault()));
        this.basePath = aPath.trim();

        // FIXME this should be more general
        if (!this.basePath.startsWith("classpath:") && !this.basePath.startsWith("file:")) {
            this.basePath = "file:" + this.basePath;
        }

        if (this.basePath.charAt(aPath.length() - 1) != File.separatorChar) {
            this.basePath = this.basePath + File.separator;
        }
    }

    /**
     * Set the filename property of this DirectoryConfigurationSource. Whitespaces will be trimmed
     * away from both ends.<b/> Will create a runtime exception (IllegalArgumentException), in case
     * a null value is being passed in the call.
     * 
     * @param filename
     *        which shall be set. Value 'null' is not allowed. Use "" to create a empty property.
     */
    public void setFilename(final String filename) {
        if (null == filename) // FIXME OPLOG: Internal software error
            throw new IllegalArgumentException(this.getApplicationContext().getMessage(
                    "org.eclipse.swordfish.configrepos.configuration.sources.NULLFILENAMEPROPERROR", null, Locale.getDefault()));
        this.filename = filename.trim();
    }

    /**
     * Set a fixed qualifier property of this DirectoryConfigurationSource. Whitespaces will be
     * trimmed away from both ends.<b/> Will create a runtime exception (IllegalArgumentException),
     * in case a null value is being passed in the call.
     * 
     * @param aScopePath
     *        which shall be set. Value 'null' is not allowed. Use "" to create a empty property.
     */
    public void setFixedScopePath(final ScopePath aScopePath) {
        this.fixedScopePath = aScopePath;
    }

    /**
     * Allows setting the resource loader by which this directory configuration source later fetches
     * the files.
     * 
     * @param resourceLoader
     *        is the loader which should be set
     * 
     * @see org.springframework.context.ResourceLoaderAware#
     *      setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Sets the resource manager.
     * 
     * @param resourceManager
     *        The resourceManager to set.
     */
    public void setResourceManager(final FileResourceManagerBean resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * Sets the return empty for non available.
     * 
     * @param returnEmptyForNonAvailable
     *        The returnEmptyForNonAvailable to set.
     */
    public void setReturnEmptyForNonAvailable(final boolean returnEmptyForNonAvailable) {
        this.returnEmptyForNonAvailable = returnEmptyForNonAvailable;
    }

    /**
     * Set the suffix property of this DirectoryConfigurationSource. Whitespaces will be trimmed
     * away from both ends.<b/> Will create a runtime exception (IllegalArgumentException), in case
     * a null value is being passed in the call.
     * 
     * @param suffix
     *        which shall be set. Value 'null' is not allowed. Use "" to create a empty property.
     */
    public void setSuffix(final String suffix) {
        if (null == suffix) // FIXME OPLOG: Internal software error
            throw new IllegalArgumentException(this.getApplicationContext().getMessage(
                    "org.eclipse.swordfish.configrepos.configuration.sources.NULLSUFFIXPROPERROR", null, Locale.getDefault()));
        this.suffix = suffix.trim();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * @param aConfiguration
     *        the a configuration
     * 
     * @throws ConfigurationRepositoryConfigException
     *         the configuration repository config exception
     * 
     * @see org.eclipse.swordfish.configrepos.configuration.sources.WritebackConfigurationSource#updateConfiguration(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath,
     *      org.apache.commons.configuration.Configuration)
     */
    public synchronized void updateConfiguration(final String aTreeQualifier, final ScopePath aScopePath,
            final Configuration aConfiguration) throws ConfigurationRepositoryConfigException {
        if (!(aConfiguration instanceof FileConfiguration))
            throw new IllegalArgumentException("Requiring file based configuration container object");

        // only filebase configuration sources can be synchronized
        if (!this.basePath.startsWith("file:") && (this.basePath.charAt(0) != '.')
                && (this.basePath.charAt(0) != File.separatorChar)) // TODO
            // oplog
            // TODO tracelog
            return;

        if (null == this.resourceManager) throw new IllegalArgumentException("No resource manager assigned to this component");

        // Identifier of this transaction
        String txid = this.compileConfigurationTransactionIdentifier(aTreeQualifier, aScopePath);
        // Target filename / resourcename
        String targetfile = this.compileResourceNameForManager(aTreeQualifier, aScopePath);
        boolean fail = false;
        OutputStream out = null;
        try {
            // open a transaction
            this.resourceManager.startTransaction(txid);

            // create the target file if it does not exist
            if (!this.resourceManager.resourceExists(targetfile)) {
                this.resourceManager.createResource(txid, targetfile);
            }

            // write the file's contents
            out = this.resourceManager.writeResource(txid, targetfile);
            ((FileConfiguration) aConfiguration).save(out);
        } catch (ResourceManagerException e) {
            // TODO i18n
            // TODO oplog
            fail = true;
            throw new ConfigurationRepositoryConfigException(
                    "Error in transactional resource manager while synchronizing configuration data in local filesystem", e);
        } catch (ConfigurationException e) {
            // TODO oplog
            fail = true;
            throw new ConfigurationRepositoryConfigException(
                    "Error during configuration serialization while synchronizing configuration data in local filesystem", e);
        } catch (Throwable e) {
            // TODO oplog
            fail = true;
            throw new ConfigurationRepositoryConfigException("org.eclipse.swordfish.configrepos.INTERNALEXCEPTION", e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e1) {
                    fail = true;
                    throw new ConfigurationRepositoryConfigException(
                            "IO error while synchronizing resource data in local filesystem", e1);
                }
            }

            try { // commit operation on target file / resource
                if (!fail) {
                    this.resourceManager.commitTransaction(txid);
                } else {
                    this.resourceManager.rollbackTransaction(txid);
                }
            } catch (ResourceManagerException e) {
                throw new ConfigurationRepositoryConfigException("Error while commiting configuration data in local filesystem", e);
            }
        }

    }

    /**
     * Fetch the configuration and return it as XMLConfiguration object.
     * 
     * @param aTreeQualifier
     *        to be selected
     * @param aScopePath
     *        to be traversed
     * 
     * @return an XMLConfiguration object filled with the fetched configuration and with the source
     *         name set into its filename attribute
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case the configuration could not be fetched
     */
    protected XMLConfiguration fetchConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        XMLConfiguration result = null;
        super.pushBeanNameOnNDC();
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(DirectoryConfigurationSource.class.getName(), "getConfiguration",
                    new Object[] {aTreeQualifier, aScopePath});
        }

        if (null == this.resourceLoader) {
            // TODO i18n
            RuntimeException fatal =
                    new IllegalArgumentException("Requiring a resource loader or resource manager assigned to this bean.");
            if (null != this.getOperationalLogger()) {
                this.getOperationalLogger().issueOperationalLog(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION, new Object[] {fatal});
            }
            throw fatal;
        }

        if (null != this.getResourceManager()) {
            result = this.lookupConfigurationInSynchronizationStore(aTreeQualifier, aScopePath);
            if (result != null) return result;
        }

        try {
            if (this.returnEmptyForNonAvailable) {
                result = this.retrieveConfigurationResilientlyOrEmpty(aTreeQualifier, aScopePath);
                return result;
            } else {
                result = this.retrieveConfigurationFromAbsolutPath(aTreeQualifier, aScopePath);
                return result;
            }
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(DirectoryConfigurationSource.class.getName(), "getConfiguration", result);
            }
            super.popBeanNameFromNDC();
        }
    }

    /**
     * Compile configuration transaction identifier.
     * 
     * @param aTreeQualifier
     *        to apply
     * @param aScopePath
     *        to apply
     * 
     * @return returns a transaction id based on the provided parameters
     */
    private String compileConfigurationTransactionIdentifier(final String aTreeQualifier, final ScopePath aScopePath) {
        try {
            return new Long(this.resourceManager.fetchNextTXID()).toString();
        } catch (ResourceManagerException e) {
            throw new IllegalArgumentException("Error creating transaction identifier.");
        }
    }

    /**
     * Compile resource name for manager.
     * 
     * @param aTreeQualifier
     *        to apply
     * @param aScopePath
     *        to apply
     * 
     * @return returns a resource name, as the related manager would use it
     */
    private String compileResourceNameForManager(final String aTreeQualifier, final ScopePath aScopePath) {
        return DirectorySourceUtil.canonicalizeFilePath(null, (null != aTreeQualifier ? aTreeQualifier + File.separator : null),
                (null != this.fixedScopePath ? this.fixedScopePath : aScopePath), this.filename + this.suffix);
    }

    /**
     * Lookup configuration in synchronization store.
     * 
     * @param aTreeQualifier
     *        to be used
     * @param aScopePath
     *        to be traversed
     * 
     * @return returns a configuration, which will possibly be empty in case no source was found
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case the configuration proxy exception occured
     */
    private XMLConfiguration lookupConfigurationInSynchronizationStore(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        XMLConfiguration result;
        String txid = this.compileConfigurationTransactionIdentifier(aTreeQualifier, aScopePath);
        boolean fail = false;

        // only filebase configuration sources can be synchronized
        if (!this.basePath.startsWith("file:") && (this.basePath.charAt(0) != '.')
                && (this.basePath.charAt(0) != File.separatorChar)) // TODO
            // oplog
            // TODO tracelog
            return null;

        String resourceName = null;
        InputStream in = null;
        try {
            this.resourceManager.startTransaction(txid);

            resourceName = this.compileResourceNameForManager(aTreeQualifier, aScopePath);
            if (!this.resourceManager.resourceExists(txid, resourceName)) // resourceManager.commitTransaction(txid);
                return null;

            in = this.resourceManager.readResource(txid, resourceName);
            result = new XMLConfiguration();
            result.load(in);
            result.setFileName(resourceName);

        } catch (ResourceManagerException e) {
            throw new ConfigurationRepositoryConfigException(
                    "Error in transactional resource manager while synchronizing configuration data in sychronization store", e);
        } catch (ConfigurationException e) {
            throw new ConfigurationRepositoryConfigException("Error in import of configuration data in local filesystem", e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // FIXME operational logging
                }
            }
            try {
                if (!fail) {
                    this.resourceManager.commitTransaction(txid);
                } else {
                    this.resourceManager.rollbackTransaction(txid);
                }
            } catch (ResourceManagerException e) {
                e.printStackTrace();
                // FIXME operational logging
            }
        }
        return result;
    }

    /**
     * Retrieve configuration from absolut path.
     * 
     * @param aTreeQualifier
     *        to be used
     * @param aScopePath
     *        to be traversed
     * 
     * @return returns a configuration, which will possibly be empty in case no source was found
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case the configuration proxy exception occured
     */
    private XMLConfiguration retrieveConfigurationFromAbsolutPath(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        String canonicalFilepath =
                DirectorySourceUtil.canonicalizeFilePath(this.getBasePath(), (null != aTreeQualifier ? aTreeQualifier
                        + File.separator : null), (null != this.fixedScopePath ? this.fixedScopePath : aScopePath), this.filename
                        + this.suffix);

        if (this.resourceLoader.getResource(canonicalFilepath).exists()) {
            try {
                if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.INFO)) {
                    this.getLogger().info(
                            this.getApplicationContext().getMessage(
                                    "org.eclipse.swordfish.configrepos.configuration.sources.READINGCONFIGRESOURCE",
                                    new Object[] {this.resourceLoader.getResource(canonicalFilepath).getFile().getAbsolutePath()},
                                    Locale.getDefault()));
                }

                XMLConfiguration result = new XMLConfiguration(this.resourceLoader.getResource(canonicalFilepath).getFile());

                return result;
            } catch (ConfigurationException e) {
                throw new ConfigurationRepositoryConfigException("Configuration in '" + canonicalFilepath + "'", e);
            } catch (IOException e) {
                throw new ConfigurationRepositoryConfigException("IO error reading '" + canonicalFilepath + "'", e);
            }
        } else {
            String msg =
                    this.getApplicationContext().getMessage(
                            "org.eclipse.swordfish.configrepos.configuration.sources.NOCONFIGSOURCEAVAILABLE",
                            new Object[] {canonicalFilepath}, Locale.getDefault());
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.WARNING)) {
                this.getLogger().warning(msg);
            }
            if (null != this.getOperationalLogger()) {
                this.getOperationalLogger().issueOperationalLog(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION, new Object[] {msg});
            }
            throw new ConfigurationRepositoryConfigException(msg);
        }
    }

    /**
     * Retrieve configuration resiliently or empty.
     * 
     * @param aTreeQualifier
     *        to be used
     * @param aScopePath
     *        to be traversed
     * 
     * @return returns a configuration, which will possibly be empty in case no source was found
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case any exception occured while reading the configuration
     */
    private XMLConfiguration retrieveConfigurationResilientlyOrEmpty(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        List pathList = new ArrayList();
        String canonicalFilepath =
                DirectorySourceUtil.searchCanonicalizeFilePath(this.getBasePath(), (null != aTreeQualifier ? aTreeQualifier
                        + File.separator : null), (null != this.fixedScopePath ? this.fixedScopePath : aScopePath), this.filename
                        + this.suffix, this.resourceLoader, pathList);

        // search for configuration along the path
        if (this.resourceLoader.getResource(canonicalFilepath).exists()) {
            try {
                if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.INFO)) {
                    this.getLogger().info(
                            this.getApplicationContext().getMessage(
                                    "org.eclipse.swordfish.configrepos.configuration.sources.READINGCONFIGRESOURCE",
                                    new Object[] {this.resourceLoader.getResource(canonicalFilepath).getFile().getAbsolutePath()},
                                    Locale.getDefault()));
                }

                XMLConfiguration result = new XMLConfiguration(this.resourceLoader.getResource(canonicalFilepath).getFile());

                return result;
            } catch (ConfigurationException e) {
                throw new ConfigurationRepositoryConfigException("Configuration in '" + canonicalFilepath + "'", e);
            } catch (IOException e) {
                throw new ConfigurationRepositoryConfigException("IO error reading '" + canonicalFilepath + "'", e);
            }
        } else {
            // return empty config in case the file is must only be
            // available on an optional basis
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.INFO)) {
                this.getLogger().info(
                        this.getApplicationContext().getMessage(
                                "org.eclipse.swordfish.configrepos.configuration.sources.NOCONFIGSOURCEAVAILASSUMEEMPTY",
                                new Object[] {canonicalFilepath}, Locale.getDefault()));
            }
            return new XMLConfiguration();
        }
    }
}
