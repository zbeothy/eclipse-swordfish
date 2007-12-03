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
package org.eclipse.swordfish.configrepos.resource.sources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.spring.FileResourceManagerBean;
import org.eclipse.swordfish.configrepos.util.DirectorySourceUtil;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * The Class DirectoryResourceSource.
 * 
 */
public class DirectoryResourceSource extends AbstractResourceSource implements ResourceSource, ResourceLoaderAware,
        WritebackResourceSource {

    /** The Constant TXID_HASHALG. */
    public static final String TXID_HASHALG = "SHA-1";

    /** The Constant TXID_CHARACTERENCODING. */
    public static final String TXID_CHARACTERENCODING = "UTF-8";

    /** Buffer size for writing resources to the filesystem. */
    private static final int UPDATE_WRITEOUT_BUFFER_SIZE = 2048;

    /** Jakarta commons file resource manager bean. */
    private FileResourceManagerBean resourceManager = null;

    /** The resource loader assigned to this Spring bean. */
    private ResourceLoader resourceLoader = null;

    /** Path to the configuration file. Defaults to "./". */
    private String basePath = "." + File.separator;

    /**
     * Specific qualifier which will be preset. Defaults to null, which will require providing a
     * identifier parameter when fetching the configuration.
     */
    private ScopePath fixedScopePath = null;

    /** The fixed tree qualifier. */
    private String fixedTreeQualifier = null;

    /**
     * Instantiates a new directory resource source.
     */
    public DirectoryResourceSource() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#close()
     */
    @Override
    public void close() {
        // NOP
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
     * @return Returns the basePath.
     */
    public String getBasePath() {
        if (null != this.getManager()) return this.getManager().getLocalResourceBase();
        return this.basePath;
    }

    /**
     * Gets the fixed scope path.
     * 
     * @return Returns the fixedScopePath.
     */
    public ScopePath getFixedScopePath() {
        return this.fixedScopePath;
    }

    /**
     * Gets the fixed tree qualifier.
     * 
     * @return Returns the fixedTreeQualifier.
     */
    public String getFixedTreeQualifier() {
        return this.fixedTreeQualifier;
    }

    /**
     * Gets the resource.
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * @param aComponent
     *        the a component
     * @param aIdentifier
     *        the a identifier
     * 
     * @return the resource
     * 
     * @throws ConfigurationRepositoryResourceException
     *         the configuration repository resource exception
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#getConfiguration(java.lang.String,
     *      java.lang.String)
     */
    public InputStream getResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aIdentifier) throws ConfigurationRepositoryResourceException {
        super.pushBeanNameOnNDC();
        InputStream result = null;

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(DirectoryResourceSource.class.getName(), "getResource",
                    new Object[] {aTreeQualifier, aScopePath, aComponent, aIdentifier});
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

        // Try to fetch the resource from the file resource manager, if the
        // latter is available
        if (null != this.getResourceManager()) {
            result = this.lookupResourceInSynchronizationStore(aTreeQualifier, aScopePath, aComponent, aIdentifier);
            if (result != null) return result;
        }

        // Try to use the regular spring resource loader as a fallback
        try {
            result = this.retrieveResourceResiliently(aTreeQualifier, aScopePath, aComponent, aIdentifier);
            return result;
        } catch (IOException ioe) {
            // if (null != getOperationalLogger()) {
            // getOperationalLogger().issueOperationalLog(
            // ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_ERRORREADINGLOCALFILE,
            // new Object[] {ioe.getMessage()});
            // }
            throw new ConfigurationRepositoryResourceException(ioe.getMessage());
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(DirectoryResourceSource.class.getName(), "getResource", result);
            }
            super.popBeanNameFromNDC();
        }
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
     * Flush any transient data for a specific instance id or all instances.
     * 
     * @param aInstance
     *        the a instance
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#resychronize(java.lang.String)
     */
    @Override
    public void resynchronize(final String aInstance) {
        // Auto-generated method stub

    }

    /**
     * Sets the base path.
     * 
     * @param aPath
     *        The basePath to set.
     */
    public void setBasePath(final String aPath) {
        if ((null == aPath) || (aPath.trim().length() == 0)) // TODO i18n
            throw new IllegalArgumentException("null configuration file basePath not allowed.");
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
     * Sets the fixed scope path.
     * 
     * @param fixedScopePath
     *        The fixedScopePath to set.
     */
    public void setFixedScopePath(final ScopePath fixedScopePath) {
        this.fixedScopePath = fixedScopePath;
    }

    /**
     * Sets the fixed tree qualifier.
     * 
     * @param fixedTreeQualifier
     *        The fixedTreeQualifier to set.
     */
    public void setFixedTreeQualifier(final String fixedTreeQualifier) {
        this.fixedTreeQualifier = fixedTreeQualifier;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aResourceLoader
     *        which will be used to fetch the resources using the spring runtime
     * 
     * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    public void setResourceLoader(final ResourceLoader aResourceLoader) {
        this.resourceLoader = aResourceLoader;
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
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * @param aComponentName
     *        the a component name
     * @param aResourceId
     *        the a resource id
     * @param aResourceDataStream
     *        the a resource data stream
     * 
     * @throws ConfigurationRepositoryResourceException
     *         the configuration repository resource exception
     * 
     * @see org.eclipse.swordfish.configrepos.resource.sources.WritebackResourceSource#updateResource(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath, java.lang.String,
     *      java.lang.String, java.io.InputStream)
     */
    public synchronized void updateResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponentName,
            final String aResourceId, final InputStream aResourceDataStream) throws ConfigurationRepositoryResourceException {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(DirectoryResourceSource.class.getName(), "updateResource",
                    new Object[] {aTreeQualifier, aScopePath, aComponentName, aResourceId, aResourceDataStream});
        }

        try {
            // only filebase configuration sources can be synchronized
            if (!this.basePath.startsWith("file:") && (this.basePath.charAt(0) != '.')
                    && (this.basePath.charAt(0) != File.separatorChar)) // TODO
                // oplog
                // TODO tracelog
                return;

            if (null == this.resourceManager) throw new IllegalArgumentException("No resource manager assigned to this component");

            // Identifier of this transaction
            String txid = this.compileResourceTransactionIdentifier(aTreeQualifier, aScopePath, aComponentName, aResourceId);
            // Target filename / resourcename
            String targetfile = this.compileResourceNameForManager(aTreeQualifier, aScopePath, aComponentName, aResourceId);
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
                byte[] buffer = new byte[DirectoryResourceSource.UPDATE_WRITEOUT_BUFFER_SIZE];
                int read = -1;
                while ((read = aResourceDataStream.read(buffer)) > -1) {
                    out.write(buffer, 0, read);
                }
                out.close();
            } catch (ResourceManagerException e) {
                // TODO i18n
                fail = true;
                throw new ConfigurationRepositoryResourceException("Error in transactional resource manager while "
                        + "synchronizing resource data in local filesystem", e);
            } catch (IOException e) {
                fail = true;
                throw new ConfigurationRepositoryResourceException(
                        "IO error while synchronizing resource data in local filesystem", e);
            } catch (Throwable e) {
                // TODO oplog
                fail = true;
                throw new ConfigurationRepositoryResourceException("org.eclipse.swordfish.configrepos.INTERNALEXCEPTION", e);
            } finally {
                if (null != out) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        fail = true;
                        throw new ConfigurationRepositoryResourceException(
                                "IO error while synchronizing resource data in local filesystem", e);
                    }
                }
                try { // commit operation on target file / resource
                    if (!fail) {
                        this.resourceManager.commitTransaction(txid);
                    } else {
                        this.resourceManager.rollbackTransaction(txid);
                    }
                } catch (ResourceManagerException e) {
                    throw new ConfigurationRepositoryResourceException("Error while commiting resource data in local filesystem", e);
                }
            }
        } finally {
            this.getLogger().exiting(DirectoryResourceSource.class.getName(), "updateResource");
        }
    }

    /**
     * Compile resource name for manager.
     * 
     * @param aTreeQualifier
     *        the resource is located in
     * @param aScopePath
     *        to search in resource
     * @param aComponent
     *        which owns the resource
     * @param aIdentifier
     *        as the resource name
     * 
     * @return an InputStream to the resource's data
     */
    private String compileResourceNameForManager(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aIdentifier) {
        return DirectorySourceUtil.canonicalizeFilePath(null, (null != aTreeQualifier ? aTreeQualifier + File.separator : null),
                (null != this.fixedScopePath ? this.fixedScopePath : aScopePath), aComponent + File.separator + aIdentifier);
    }

    /**
     * Compile resource transaction identifier.
     * 
     * @param aTreeQualifier
     *        the resource is located in
     * @param aScopePath
     *        to search in resource
     * @param aComponent
     *        which owns the resource
     * @param aIdentifier
     *        as the resource name
     * 
     * @return an resource id
     */
    private String compileResourceTransactionIdentifier(final String aTreeQualifier, final ScopePath aScopePath,
            final String aComponent, final String aIdentifier) {
        try {
            return new Long(this.resourceManager.fetchNextTXID()).toString();
        } catch (ResourceManagerException e) {
            throw new IllegalArgumentException("Error creating transaction identifier.");
        }
    }

    /**
     * Lookup a resource via the file resource manager reference.
     * 
     * @param aTreeQualifier
     *        the resource is located in
     * @param aScopePath
     *        to search in resource
     * @param aComponent
     *        which owns the resource
     * @param aIdentifier
     *        as the resource name
     * 
     * @return an InputStream to the resource's data
     * 
     * @throws ConfigurationRepositoryResourceException
     *         in case the resource could not be opened
     */
    private InputStream lookupResourceInSynchronizationStore(final String aTreeQualifier, final ScopePath aScopePath,
            final String aComponent, final String aIdentifier) throws ConfigurationRepositoryResourceException {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(DirectoryResourceSource.class.getName(), "lookupResourceInSynchronizationStore",
                    new Object[] {aTreeQualifier, aScopePath, aComponent, aIdentifier});
        }

        InputStream result = null;
        try {
            String txid = this.compileResourceTransactionIdentifier(aTreeQualifier, aScopePath, aComponent, aIdentifier);

            // only filebase configuration sources can be synchronized
            if (!this.basePath.startsWith("file:") && (this.basePath.charAt(0) != '.')
                    && (this.basePath.charAt(0) != File.separatorChar)) // TODO
                // oplog
                // TODO tracelog
                return null;

            try {
                this.resourceManager.startTransaction(txid);

                String resourceName = this.compileResourceNameForManager(aTreeQualifier, aScopePath, aComponent, aIdentifier);

                if (!this.resourceManager.resourceExists(txid, resourceName)) {
                    this.resourceManager.commitTransaction(txid);
                    return null;
                }

                result =
                        new TransactionalInputStream(this.resourceManager, txid, this.resourceManager.readResource(txid,
                                resourceName));
                return result;
            } catch (ResourceManagerException e) {
                throw new ConfigurationRepositoryResourceException(
                        "Error in transactional resource manager while synchronizing resource data in local filesystem", e);
            }
        } finally {
            this.getLogger().exiting(DirectoryResourceSource.class.getName(), "lookupResourceInSynchronizationStore", result);
        }
    }

    /**
     * Retrieve a resource from the local file system.
     * 
     * @param aTreeQualifier
     *        the resource is located in
     * @param aScopePath
     *        to search in resource
     * @param aComponent
     *        which owns the resource
     * @param aIdentifier
     *        as the resource name
     * 
     * @return an InputStream to the resource's data
     * 
     * @throws IOException
     *         in case the resource could not be opened
     */
    private InputStream retrieveResourceResiliently(final String aTreeQualifier, final ScopePath aScopePath,
            final String aComponent, final String aIdentifier) throws IOException {
        InputStream result;
        List pathList = new ArrayList();
        String canonicalFilepath =
                DirectorySourceUtil.searchCanonicalizeFilePath(this.getBasePath(),
                        (null != this.fixedTreeQualifier ? this.fixedTreeQualifier : aTreeQualifier),
                        (null != this.fixedScopePath ? this.fixedScopePath : aScopePath), (null != aComponent ? aComponent
                                + File.separator : "")
                                + aIdentifier, this.resourceLoader, pathList);
        Resource res = this.resourceLoader.getResource(canonicalFilepath);
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.INFO) && res.exists()) {
            this.getLogger().info(
                    this.getApplicationContext().getMessage(
                            "org.eclipse.swordfish.configrepos.configuration.sources.READINGRESOURCESRC",
                            new Object[] {res.getDescription()}, Locale.getDefault()));
        }
        result = res.getInputStream();
        return result;
    }

}
