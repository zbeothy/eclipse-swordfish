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

import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * This ConfigurationSource sub-type implements a source with fallback capabilities. It is assigned
 * 1-2 configuration source(s) which will be consulted in order (main, fallback) for a property. In
 * the general case, the main configuration is being consulted for various properties, which either
 * are available or not. In the fallback case, during which the main configuration gets unavailable,
 * the fallback configuration is being addressed.<b/> If this configuration source gets unavailable
 * too, a runtime exception is being thrown.<b/> The FailsafeConfigurationSource must be fed with a
 * identifier which will be used to qualify the query to the configuration sources below this
 * object. It can either be provided during creation of the object, or be set explicitely through a
 * set-method (see below).<b/> TODO We should change the strategy on when to switch between
 * configuration in a way that, switching forward from the main- to the fallback-configuration will
 * be done as soon as an error occures. Switch back from the fallback- to the main-configuration
 * shall only be done on request by the framework. This is due to the possibility that the switch is
 * being done in the middle of a sequence of calls to the configuration source.
 * 
 */
public class FailsafeConfigurationSource extends AbstractConfigurationSource {

    /** The configuration which consulted in the normal case. */
    private ConfigurationSource mainConfigurationSource = null;

    /** The configuration which is being consulted in case the main configuration gets unavailable. */
    private ConfigurationSource fallbackConfigurationSource = null;

    /**
     * Set whether this component shall start with the failsafe configuration right away, rather
     * then trying to connect to the primary configuration.
     */
    private boolean directFailsafe = false;

    /** The participant identifier this configuration source is configured for. */
    private ScopePath fixedScopePath = null;

    /**
     * Create a FailsafeConfigurationSource object, without a specific identifier target Please
     * note, that the object will require setting the identity qualifier later, before retrieving
     * specific configuration properties.
     */
    public FailsafeConfigurationSource() {
        this(null);
    }

    /**
     * Create a FailsafeConfigurationSource object for the specified identifier.
     * 
     * @param aIdentifier
     *        which should be used as a qualifier, can initially be set to null.
     */
    public FailsafeConfigurationSource(final ScopePath aIdentifier) {
        super();

        if (null != aIdentifier) {
            this.fixedScopePath = aIdentifier;
        }
    }

    /**
     * Close all configuration sources and free the references to them.
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#close()
     */
    @Override
    public void close() {
        if (null != this.mainConfigurationSource) {
            this.mainConfigurationSource.close();
            this.mainConfigurationSource = null;
        }

        if (null != this.fallbackConfigurationSource) {
            this.fallbackConfigurationSource.close();
            this.fallbackConfigurationSource = null;
        }
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

        this.fallbackConfigurationSource = null;
        this.mainConfigurationSource = null;

        super.destroy();
    }

    /**
     * Fetch the participant identifier set as a default for this source.
     * 
     * @return String which contains the identifier, or null in case non is set.
     */
    public ScopePath geFixedScopePath() {
        return this.fixedScopePath;
    }

    /**
     * Fetch the configuration for a specific identifier. In case the provided identifier is null,
     * the default participant identifier property of the source will be used instead.
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
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#getConfiguration(ScopePath)
     */
    public Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        super.pushBeanNameOnNDC();
        Configuration result = null;
        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(FailsafeConfigurationSource.class.getName(), "getConfiguration",
                    new Object[] {aTreeQualifier, aScopePath});
        }
        try {
            result =
                    this.fetchAvailableConfiguration(aTreeQualifier, (null != this.fixedScopePath ? this.fixedScopePath
                            : aScopePath));
            return result;
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(FailsafeConfigurationSource.class.getName(), "getConfiguration", result);
            }
            super.popBeanNameFromNDC();
        }
    }

    /**
     * Fetch the fallback configuration source property.
     * 
     * @return the fallback source in case it was set, otherwise null.
     */
    public ConfigurationSource getFallbackConfigurationSource() {
        return this.fallbackConfigurationSource;
    }

    /**
     * Fetch the fallback configuration source property.
     * 
     * @return the main source in case it was set, otherwise null.
     */
    public ConfigurationSource getMainConfigurationSource() {
        return this.mainConfigurationSource;
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
            this.getLogger().entering(FailsafeConfigurationSource.class.getName(), "resynchronize", new Object[] {aInstance});
        }

        if (null != this.mainConfigurationSource) {
            this.mainConfigurationSource.resynchronize(aInstance);
        }

        if (null != this.fallbackConfigurationSource) {
            this.fallbackConfigurationSource.resynchronize(aInstance);
        }

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().exiting(FailsafeConfigurationSource.class.getName(), "resynchronize");
        }
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
     * Set the fallback configuration source property.
     * 
     * @param fallbackConfigurationSource
     *        which should be used.
     */
    public void setFallbackConfigurationSource(final ConfigurationSource fallbackConfigurationSource) {
        this.fallbackConfigurationSource = fallbackConfigurationSource;
    }

    /**
     * Set the default participant identifier property of this source. It will be used when fetching
     * configurations and no identifier is being provided in the call (@see
     * org.eclipse.swordfish.configrepos.configuration.
     * sources.ConfigurationSource#getConfiguration(java.lang.String))
     * 
     * @param aScopePath
     *        the a scope path
     */
    public void setFixedScopePath(final ScopePath aScopePath) {
        if (null == aScopePath) {
            // TODO i18n
            RuntimeException fatal =
                    new IllegalArgumentException(this.getApplicationContext().getMessage(
                            "org.eclipse.swordfish.configrepos.configuration.sources.NULLSCOPEPATHPROPERROR", null,
                            Locale.getDefault()));
            if (null != this.getOperationalLogger()) {
                this.getOperationalLogger().issueOperationalLog(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION, new Object[] {fatal});
            }
            throw fatal;
        }
        this.fixedScopePath = aScopePath;
    }

    /**
     * Set the fallback configuration source property.
     * 
     * @param mainConfigurationSource
     *        which should be used.
     */
    public void setMainConfigurationSource(final ConfigurationSource mainConfigurationSource) {
        this.mainConfigurationSource = mainConfigurationSource;
    }

    /**
     * Returns the configuration for the fixedScopePath, either in the main- or the
     * fallback-configuration-source.
     * 
     * @param aTreeQualifier
     *        used as a data source
     * @param aIdentifier
     *        which will be queried for
     * 
     * @return Configuration which could successfully be found
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case a technical error was encountered.
     */
    private Configuration fetchAvailableConfiguration(final String aTreeQualifier, final ScopePath aIdentifier)
            throws ConfigurationRepositoryConfigException {
        Configuration result = null;
        try {
            // switch directly to using the failsafe configuration, if this has been set in the
            // properties
            if (this.hackSkipForConfigurationRepository(aIdentifier) && (null != this.getFallbackConfigurationSource()))
                return this.getFallbackConfigurationSource().getConfiguration(aTreeQualifier, aIdentifier);

            // try to fetch the configuration from the main source
            result = this.getMainConfigurationSource().getConfiguration(aTreeQualifier, aIdentifier);

            if (WritebackConfigurationSource.class.isAssignableFrom(this.fallbackConfigurationSource.getClass())) {
                try {
                    ((WritebackConfigurationSource) this.fallbackConfigurationSource).updateConfiguration(aTreeQualifier,
                            aIdentifier, result);
                } catch (RuntimeException e) {
                    if (null != this.getOperationalLogger()) {
                        this.getOperationalLogger().issueOperationalLog(ConfigReposOperationalLogger.REPOSITORYDATA_SYNC_ERROR,
                                new Object[] {e.getMessage()});

                    }
                }
            }

            return result;
        } catch (ConfigurationRepositoryConfigException ce) {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.WARNING)) {
                this.getLogger().log(
                        Level.WARNING,
                        this.getApplicationContext().getMessage(
                                "org.eclipse.swordfish.configrepos.configuration.sources.FAILOVERREADINGCONFIGURATION",
                                new Object[] {ce.getMessage()}, Locale.getDefault()), ce);
            }
            if (null != this.getOperationalLogger()) {
                this.getOperationalLogger().issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_FAILOVER,
                        new Object[] {ce});

            }
            // try to fetch the configuration from the fallback source
            return this.getFallbackConfigurationSource().getConfiguration(aTreeQualifier, aIdentifier);
        }
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
        boolean foundapplication = false;
        Iterator iter = path.getPathPart().iterator();
        while (iter.hasNext()) {
            PathPart part = (PathPart) iter.next();
            if (part.getType().equals("Application") && part.getValue().startsWith("Configuration"))
                return true;
            else if (part.getType().equals("Application") && (null != part.getValue())) {
                foundapplication = true;
            }
        }
        if (!foundapplication) return true;
        return this.isDirectFailsafe();
    }
}
