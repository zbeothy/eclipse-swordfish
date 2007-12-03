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
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * The Class OverrideableConfigurationSource.
 * 
 */
public class OverrideableConfigurationSource extends AbstractConfigurationSource {

    /** Message being exposed in case no configuration sources have been defined. FIXME i18n */
    public static final String SBBCOMMONSCONFIG_NOCONFIGSOURCEAVAILABLE = "no configuration source was available.";

    /**
     * Default participant identifier property which will be used in case no participant identifier
     * is provided when fetching configurations.
     */
    private ScopePath participantIdentifier = null;

    /** The list of sources which are being processed in order. */
    private List sources = null;

    /**
     * Default constructor.
     */
    public OverrideableConfigurationSource() {
        this(null);
    }

    /**
     * Constructor with pre-defined participant identifier which will be used as a default in case
     * this is not being provided on the call.
     * 
     * @param aIdentifier
     *        which shall be used as a default participant identifier
     * 
     * @see org.eclipse.swordfish.configrepos.configuration.sources.OverrideableConfigurationSource#getConfiguration(ScopePath)
     */
    public OverrideableConfigurationSource(final ScopePath aIdentifier) {
        super();

        if (null != aIdentifier) {
            this.participantIdentifier = aIdentifier;
        }
    }

    /**
     * Shall be called to close all sources defined below this configuration source. The referred
     * sources will be de-referred after this call.
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#close()
     */
    @Override
    public void close() {
        super.close();

        if (null != this.sources) {
            Iterator iter = this.sources.iterator();
            while (iter.hasNext()) {
                ((ConfigurationSource) iter.next()).close();
            }
            this.sources.clear();
            this.sources = null;
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

        this.sources = null;

        super.destroy();
    }

    /**
     * Fetch the configuration for a specific identifier. Will browse through the list of sources in
     * their order and try to identify any which successfully provides a configuration. In case non
     * is able to do so, this component will throw a ConfigurationException.
     * 
     * @param aTreeQualifier
     *        used as data source
     * @param aScopePath
     *        which will be used to browse the sources. In case null is being provided, the
     *        participant identifier property of the source is being used.
     * 
     * @return Configuration provided by the first source in the list which was able to compile any.
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case no source was able to provide a configuration.
     * 
     * @see org.eclipse.swordfish.configrepos.sources.OverrideableConfigurationSource#getConfiguration(ScopePath)
     */
    public Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        super.pushBeanNameOnNDC();
        Configuration result = null;

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(OverrideableConfigurationSource.class.getName(), "getConfiguration",
                    new Object[] {aTreeQualifier, aScopePath});
        }
        try {
            Iterator iter = this.sources.iterator();
            ConfigurationSource cfgsrc = null;
            while (iter.hasNext()) {
                try {
                    cfgsrc = (ConfigurationSource) iter.next();
                    result =
                            cfgsrc.getConfiguration(aTreeQualifier,
                                    (null != this.participantIdentifier ? this.participantIdentifier : aScopePath));
                    return result;
                } catch (Exception e) {
                    if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                        this.getLogger().finest(
                                this.getApplicationContext().getMessage(
                                        "org.eclipse.swordfish.configrepos.configuration.sources.CONFIGNOTAVAILABLE",
                                        new Object[] {cfgsrc.toString(), e.getLocalizedMessage(), e.getMessage()},
                                        Locale.getDefault()));
                    }
                }
            }
            String errmsg =
                    this.getApplicationContext().getMessage(
                            "org.eclipse.swordfish.configrepos.configuration.sources.NOSOURCEAVAILABLEEXCEPTION",
                            new Object[] {new Integer(this.sources.size())}, Locale.getDefault());
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.SEVERE)) {
                this.getLogger().severe(errmsg);
            }
            // TODO i18n
            if (null != this.getOperationalLogger()) {
                this.getOperationalLogger().issueOperationalLog(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_NOOVERRIDERESOLVABLE, null);
            }
            throw new ConfigurationRepositoryConfigException(errmsg);
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(OverrideableConfigurationSource.class.getName(), "getConfiguration", result);
            }
            super.popBeanNameFromNDC();
        }
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
            this.getLogger().entering(OverrideableConfigurationSource.class.getName(), "resynchronize", new Object[] {aInstance});
        }

        if ((null != this.sources) && !this.sources.isEmpty()) {
            Iterator iter = this.sources.iterator();
            while (iter.hasNext()) {
                ((ConfigurationSource) iter.next()).resynchronize(aInstance);
            }
        }

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().exiting(OverrideableConfigurationSource.class.getName(), "resynchronize");
        }
    }

    /**
     * Set the list of configuration sources.
     * 
     * @param aListOfSources
     *        should be a list of ConfigurationSource objects.
     */
    public void setSources(final List aListOfSources) {
        this.sources = aListOfSources;
    }
}
