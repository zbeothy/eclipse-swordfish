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

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.springmodules.cache.EntryRetrievalException;
import org.springmodules.cache.provider.CacheProviderFacade;

/**
 * The Class CachingConfigurationSource.
 * 
 */
public class CachingConfigurationSource extends AbstractConfigurationSource {

    /** Cache Provider used by this bean. */
    private CacheProviderFacade cacheProvider = null;

    /** Cache profile to use. */
    private String cacheProfile = null;

    /** Cached target source. */
    private ConfigurationSource cacheTarget = null;

    /**
     * Instantiates a new caching configuration source.
     */
    public CachingConfigurationSource() {
        super();
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

        this.cacheTarget = null;
        this.cacheProvider = null;
        this.cacheProfile = null;
        super.destroy();
    }

    /**
     * Gets the cache profile.
     * 
     * @return Returns the cacheProfile.
     */
    public String getCacheProfile() {
        return this.cacheProfile;
    }

    /**
     * Gets the cache provider.
     * 
     * @return Returns the cacheProvider.
     */
    public CacheProviderFacade getCacheProvider() {
        return this.cacheProvider;
    }

    /**
     * Gets the cache target.
     * 
     * @return Returns the cacheTarget.
     */
    public ConfigurationSource getCacheTarget() {
        return this.cacheTarget;
    }

    /**
     * (non-Javadoc).
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
     * @see org.eclipse.swordfish.configrepos.configuration.sources.ConfigurationSource#getConfiguration(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath)
     */
    public Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        Configuration config = null;
        if (this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(CachingConfigurationSource.class.getName(), "getConfiguration",
                    new Object[] {aTreeQualifier, aScopePath});
        }

        try {
            Serializable cacheKey = this.compileKey(aTreeQualifier, aScopePath);
            boolean updated = false;
            try {
                config = (Configuration) this.cacheProvider.getFromCache(cacheKey, this.cacheProfile);
            } catch (EntryRetrievalException e) {
                String msg =
                        this.getApplicationContext().getMessage(
                                "org.eclipse.swordfish.configrepos.configuration.sources.CACHINGCONFIGSOURCE_MISS",
                                new Object[] {aTreeQualifier, aScopePath}, Locale.getDefault());
                throw new ConfigurationRepositoryConfigException(msg, e);
            }
            if (null == config) {
                if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                    this.getLogger().log(Level.FINEST,
                            "org.eclipse.swordfish.configrepos.configuration.sources.CACHINGCONFIGSOURCE_MISS",
                            new Object[] {aTreeQualifier, aScopePath});
                }
                try {
                    config = this.cacheTarget.getConfiguration(aTreeQualifier, aScopePath);
                    this.cacheProvider.putInCache(cacheKey, this.cacheProfile, config);
                    updated = true; // Ugly, but obviously required
                } finally {
                    if (!updated) {
                        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                            this.getLogger().log(Level.FINEST,
                                    "org.eclipse.swordfish.configrepos.configuration.sources.CACHEUPDATE_ABORT",
                                    new Object[] {aTreeQualifier, aScopePath});
                        }
                        this.cacheProvider.cancelCacheUpdate(cacheKey);
                    }
                }
            } else if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().log(Level.FINEST,
                        "org.eclipse.swordfish.configrepos.configuration.sources.CACHINGCONFIGSOURCE_HIT",
                        new Object[] {aTreeQualifier, aScopePath});
            }

            return config;
        } finally {
            if (this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(CachingConfigurationSource.class.getName(), "getConfiguration", config);
            }
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param aScopePath
     *        the a scope path
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#resynchronize(java.lang.String)
     */
    @Override
    public void resynchronize(final String aScopePath) {
        this.cacheProvider.flushCache(new String[] {this.cacheProfile});
        if (null != this.cacheTarget) {
            this.cacheTarget.resynchronize(aScopePath);
        }
    }

    /**
     * Sets the cache profile.
     * 
     * @param cacheProfile
     *        The cacheProfile to set.
     */
    public void setCacheProfile(final String cacheProfile) {
        this.cacheProfile = cacheProfile;
    }

    /**
     * Sets the cache provider.
     * 
     * @param cacheProvider
     *        The cacheProvider to set.
     */
    public void setCacheProvider(final CacheProviderFacade cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    /**
     * Sets the cache target.
     * 
     * @param cacheTarget
     *        The cacheTarget to set.
     */
    public void setCacheTarget(final ConfigurationSource cacheTarget) {
        this.cacheTarget = cacheTarget;
    }

    /**
     * Compile a cache key.
     * 
     * @param treeQualifier
     *        for the tree the object goes into
     * @param scopePath
     *        for the scopepath the object is located in
     * 
     * @return for the actual key
     */
    private Serializable compileKey(final String treeQualifier, final ScopePath scopePath) {
        StringBuffer buffer = new StringBuffer("sop-configrepos-proxy:config:").append(treeQualifier).append(":").append(scopePath);
        return buffer.toString();
    }

}
