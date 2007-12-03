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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * The Class FailsafeResourceSource.
 * 
 */
public class FailsafeResourceSource extends AbstractResourceSource {

    /** The configuration which consulted in the normal case. */
    private ResourceSource mainResourceSource = null;

    /** The configuration which is being consulted in case the main configuration gets unavailable. */
    private ResourceSource fallbackResourceSource = null;

    /**
     * Set whether this component shall start with the failsafe configuration right away, rather
     * then trying to connect to the primary configuration.
     */
    private boolean directFailsafe = false;

    /** The participant identifier this configuration source is configured for. */
    private ScopePath fixedScopePath = null;

    /**
     * Instantiates a new failsafe resource source.
     */
    public FailsafeResourceSource() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.AbstractRepositorySource#close()
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
     * @see org.eclipse.swordfish.configrepos.AbstractRepositorySource#destroy()
     */
    @Override
    public void destroy() throws Exception {
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().log(Level.FINEST, "destroy " + this.getBeanName());
        }

        this.fallbackResourceSource = null;
        this.mainResourceSource = null;

        super.destroy();
    }

    /**
     * Gets the fallback resource source.
     * 
     * @return Returns the fallbackResourceSource.
     */
    public ResourceSource getFallbackResourceSource() {
        return this.fallbackResourceSource;
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
     * Gets the main resource source.
     * 
     * @return Returns the mainResourceSource.
     */
    public ResourceSource getMainResourceSource() {
        return this.mainResourceSource;
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
     * @param aResourceIdentifier
     *        the a resource identifier
     * 
     * @return the resource
     * 
     * @throws ConfigurationRepositoryResourceException
     *         the configuration repository resource exception
     * 
     * @see org.eclipse.swordfish.configrepos.resource.sources.ResourceSource#getResource(java.lang.String,
     *      org.eclipse.swordfish.configrepos.scopepath.basic.ScopePath, java.lang.String,
     *      java.lang.String)
     */
    public InputStream getResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResourceIdentifier) throws ConfigurationRepositoryResourceException {
        InputStream result = null;

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(FailsafeResourceSource.class.getName(), "getResource",
                    new Object[] {aTreeQualifier, aScopePath, aComponent, aResourceIdentifier});
        }

        try {
            // directly switch to using the failsafe configuration if this has been set in the
            // properties
            if (this.hackSkipForConfigurationRepository(aScopePath) && (null != this.getFallbackResourceSource())) {
                result = this.getFallbackResourceSource().getResource(aTreeQualifier, aScopePath, aComponent, aResourceIdentifier);
                return result;
            }

            // try to fetch the configuration from the main source
            result = this.getMainResourceSource().getResource(aTreeQualifier, aScopePath, aComponent, aResourceIdentifier);

            if (WritebackResourceSource.class.isAssignableFrom(this.fallbackResourceSource.getClass())) {
                try {
                    result.mark(0);
                    ((WritebackResourceSource) this.fallbackResourceSource).updateResource(aTreeQualifier, aScopePath, aComponent,
                            aResourceIdentifier, result);
                    result.reset();
                } catch (RuntimeException e) {
                    if (null != this.getOperationalLogger()) {
                        this.getOperationalLogger().issueOperationalLog(ConfigReposOperationalLogger.REPOSITORYDATA_SYNC_ERROR,
                                new Object[] {e.getMessage()});

                    }
                } catch (IOException e) {
                    if (null != this.getOperationalLogger()) {
                        this.getOperationalLogger().issueOperationalLog(ConfigReposOperationalLogger.REPOSITORYDATA_SYNC_ERROR,
                                new Object[] {e.getMessage()});

                    }
                }
            }

            return result;
        } catch (ConfigurationRepositoryResourceException ce) {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.WARNING)) {
                // FIXME i18n
                this.getLogger().log(Level.WARNING, "Failover while fetching resource from main source.", ce);
            }
            if (null != this.getOperationalLogger()) {
                this.getOperationalLogger().issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_FAILOVER,
                        new Object[] {ce.getMessage()});
            }
            // try to fetch the configuration from the fallback source
            return this.getFallbackResourceSource().getResource(aTreeQualifier, aScopePath, aComponent, aResourceIdentifier);
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(FailsafeResourceSource.class.getName(), "getResource", result);
            }
        }
    }

    /**
     * Checks if is direct failsafe.
     * 
     * @return Returns the directFailsafe.
     */
    public boolean isDirectFailsafe() {
        ConfigurationRepositoryManagerInternal mngr = this.getManager();
        if (null != mngr)
            return mngr.isSkipRemoteRepositoryCalls();
        else
            return this.directFailsafe;
    }

    /**
     * Resynchronize.
     * 
     * @param aScopePath
     *        the a scope path
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#resynchronize(java.lang.String)
     */
    @Override
    public void resynchronize(final String aScopePath) {
    }

    /**
     * Sets the direct failsafe.
     * 
     * @param directFailsafe
     *        The directFailsafe to set.
     */
    public void setDirectFailsafe(final boolean directFailsafe) {
        this.directFailsafe = directFailsafe;
    }

    /**
     * Sets the fallback resource source.
     * 
     * @param fallbackSource
     *        The fallbackResourceSource to set.
     */
    public void setFallbackResourceSource(final ResourceSource fallbackSource) {
        this.fallbackResourceSource = fallbackSource;
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
     * Sets the main resource source.
     * 
     * @param mainSource
     *        The mainResourceSource to set.
     */
    public void setMainResourceSource(final ResourceSource mainSource) {
        this.mainResourceSource = mainSource;
    }

    /**
     * A hack to circumvent the missing possiblity to turn on and off remote call behaviour for
     * specific participants.
     * 
     * @param path
     *        of the current participant
     * 
     * @return whether a remote call should be skipped
     */
    // FIXME: Remove this hack! Replace it with a decent concept for remote config on/off
    private boolean hackSkipForConfigurationRepository(final ScopePath path) {
        if (null == path) return this.isDirectFailsafe();
        boolean foundapplication = true;
        Iterator iter = path.getPathPart().iterator();
        while (iter.hasNext()) {
            PathPart part = (PathPart) iter.next();
            if (part.getType().equals("Application") && part.getValue().startsWith("Configuration"))
                return true;
            else if (part.getType().equals("Application") && (null == part.getValue())) {
                foundapplication = true;
            }
        }
        if (!foundapplication) return true;
        return this.isDirectFailsafe();
    }

}
