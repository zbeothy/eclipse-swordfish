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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.springmodules.cache.EntryRetrievalException;
import org.springmodules.cache.provider.CacheProviderFacade;

/**
 * The Class CachingResourceSource.
 * 
 */
public class CachingResourceSource extends AbstractResourceSource {

    /** Cache Provider used by this bean. */
    private CacheProviderFacade cacheProvider = null;

    /** Cache profile to use. */
    private String cacheProfile = null;

    /** Cached target source. */
    private ResourceSource cacheTarget = null;

    /**
     * Instantiates a new caching resource source.
     */
    public CachingResourceSource() {
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

        this.cacheProvider = null;
        this.cacheTarget = null;
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
    public ResourceSource getCacheTarget() {
        return this.cacheTarget;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        the a tree qualifier
     * @param aScopePath
     *        the a scope path
     * @param aComponent
     *        the a component
     * @param aResource
     *        the a resource
     * 
     * @return the resource
     * 
     * @throws ConfigurationRepositoryResourceException
     *         the configuration repository resource exception
     * 
     * @see org.eclipse.swordfish.configrepos.configuration.sources.ConfigurationSource#getConfiguration(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath)
     */
    public InputStream getResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResource) throws ConfigurationRepositoryResourceException {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(CachingResourceSource.class.getName(), "getResource",
                    new Object[] {aTreeQualifier, aScopePath, aComponent, aResource});
        }

        InputStream stream = null;
        try {
            Serializable cacheKey = this.compileKey(aTreeQualifier, aScopePath, aComponent, aResource);
            boolean updated = false;
            try {
                byte[] data = (byte[]) this.cacheProvider.getFromCache(cacheKey, this.cacheProfile);
                if (null != data) {
                    stream = new ByteArrayInputStream(data);
                }
            } catch (EntryRetrievalException e) {
                String msg =
                        this.getApplicationContext().getMessage(
                                "org.eclipse.swordfish.configrepos.resource.sources.CACHINGRESOURCESOURCE_ERROR",
                                new Object[] {aTreeQualifier, aScopePath, aComponent, aResource}, Locale.getDefault());
                if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                    this.getLogger().log(Level.FINEST, msg);
                }
                throw new ConfigurationRepositoryResourceException(msg, e);
            }

            if (null == stream) {
                if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                    this.getLogger().log(Level.FINEST,
                            "org.eclipse.swordfish.configrepos.resource.sources.CACHINGRESOURCESOURCE_MISS",
                            new Object[] {aTreeQualifier, aScopePath, aComponent, aResource});
                }

                ByteArrayOutputStream ostream = null;
                try {
                    try {
                        stream = this.cacheTarget.getResource(aTreeQualifier, aScopePath, aComponent, aResource);

                        ostream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[BUFFEREDREAD_SIZE];
                        int read = -1;
                        while (0 <= (read = stream.read(buffer))) {
                            ostream.write(buffer, 0, read);
                        }
                    } catch (IOException e) {
                        throw new ConfigurationRepositoryResourceException(
                                "org.eclipse.swordfish.configrepos.resource.sources.CACHINGRESOURCESOURCE_WRITEERR", e);
                    } finally {
                        if (null != ostream) {
                            try {
                                ostream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                // FIXME operational log
                            }
                        }
                        if (null != stream) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                // FIXME operational log
                            }
                        }
                    }

                    this.cacheProvider.putInCache(cacheKey, this.cacheProfile, ostream.toByteArray());
                    stream = new ByteArrayInputStream(ostream.toByteArray());
                    updated = true;
                } finally {
                    if (!updated) {
                        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                            this.getLogger().log(Level.FINEST,
                                    "org.eclipse.swordfish.configrepos.resource.sources.CACHERESOURCESOURCE_UPDATEABORT",
                                    new Object[] {aTreeQualifier, aScopePath, aComponent, aResource});
                        }
                        this.cacheProvider.cancelCacheUpdate(cacheKey);
                    }
                }
            } else if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().log(Level.FINEST, "org.eclipse.swordfish.configrepos.resource.sources.CACHINGRESOURCESOURCE_HIT",
                        new Object[] {aTreeQualifier, aScopePath, aComponent, aResource});
            }

            return stream;
        } finally {
            if (this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(CachingResourceSource.class.getName(), "getResource", stream);
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
    public void setCacheTarget(final ResourceSource cacheTarget) {
        this.cacheTarget = cacheTarget;
    }

    /**
     * Compile a cache key.
     * 
     * @param aTreeQualifier
     *        for the tree the object goes into
     * @param aScopePath
     *        for the scopepath the object is located in
     * @param aComponent
     *        which owns the resource
     * @param aResource
     *        which should be fetched
     * 
     * @return for the actual key
     */
    private Serializable compileKey(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResource) {
        StringBuffer buffer =
                new StringBuffer("sop-configrepos-proxy:resource:").append(aTreeQualifier).append(":").append(aScopePath).append(
                        ":").append(aComponent).append(":").append(aResource);
        return buffer.toString();
    }
}
