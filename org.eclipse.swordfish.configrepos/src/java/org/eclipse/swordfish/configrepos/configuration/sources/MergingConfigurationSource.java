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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.configuration.Configuration;
import org.eclipse.swordfish.configrepos.ConfigReposOperationalLogger;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.shared.XMLCompositeConfiguration;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;

/**
 * The Class MergingConfigurationSource.
 * 
 */
public class MergingConfigurationSource extends AbstractConfigurationSource {

    /** Define whether strictMerging merges should be applied. */
    private boolean strictMerging = true;

    /** List of configuration sources. */
    private List configSources = null;

    /**
     * Instantiates a new merging configuration source.
     */
    public MergingConfigurationSource() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#close()
     */
    @Override
    public void close() {
        super.close();
        // Auto-generated method stub
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

        this.configSources = null;
        super.destroy();
    }

    /**
     * Get a specific configuration source at the specified position from the internal list.
     * 
     * @param aPosition
     *        at which the source is set
     * 
     * @return ConfigurationSource fetched at the specified position TODO What about exception
     *         handling?
     */
    public ConfigurationSource getConfigSource(final int aPosition) {
        return (ConfigurationSource) this.configSources.get(aPosition);
    }

    /**
     * Return an array of all configuration sources stored in this merger.
     * 
     * @return ConfigurationSource array defined in this merger object TODO Implementation is empty!
     */
    public ConfigurationSource[] getConfigSources() {
        return null;
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
     * @see org.eclipse.swordfish.papi.internal.configrepos.ConfigurationSource#getConfiguration(ScopePath)
     *      FIXME replace this with a SOPware conforming merging algorithm
     */
    public Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        super.pushBeanNameOnNDC();
        Configuration result = null;

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().entering(MergingConfigurationSource.class.getName(), "getConfiguration",
                    new Object[] {aTreeQualifier, aScopePath});
        }
        try {
            List xmlConfigs = new ArrayList();
            Iterator iter = this.configSources.iterator();
            while (iter.hasNext()) {
                XMLConfiguration config =
                        (XMLConfiguration) ((ConfigurationSource) iter.next()).getConfiguration(aTreeQualifier, aScopePath);
                xmlConfigs.add(config);
            }
            if (xmlConfigs.isEmpty()) {
                result = new XMLConfiguration();
            } else {
                result = XMLCompositeConfiguration.mergeConfigurations(xmlConfigs, this.strictMerging);
            }
            return result;
        } catch (Exception ex) {
            if (null != this.getOperationalLogger()) {
                this.getOperationalLogger().issueOperationalLog(
                        ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_ERRORREADINGLOCALFILE, new Object[] {ex});
            }
            throw new ConfigurationRepositoryConfigException("Exception while merging configurations", ex);
            // CompositeConfiguration compconf = new CompositeConfiguration();
            // Iterator iter = configSources.iterator();
            // while (iter.hasNext()) {
            // compconf.addConfiguration(((ConfigurationSource) iter.next())
            // .getConfiguration(aTreeQualifier, aScopePath));
            // }
            // return compconf;
        } finally {
            if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
                this.getLogger().exiting(MergingConfigurationSource.class.getName(), "getConfiguration", result);
            }
            super.popBeanNameFromNDC();
        }
    }

    /**
     * Checks if is strict merging.
     * 
     * @return Returns the strictMerging.
     */
    public boolean isStrictMerging() {
        return this.strictMerging;
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
            this.getLogger().entering(MergingConfigurationSource.class.getName(), "resynchronize", new Object[] {aInstance});
        }

        if ((null != this.configSources) && !this.configSources.isEmpty()) {
            Iterator iter = this.configSources.iterator();
            while (iter.hasNext()) {
                ((ConfigurationSource) iter.next()).resynchronize(aInstance);
            }
        }

        if ((null != this.getLogger()) && this.getLogger().isLoggable(Level.FINEST)) {
            this.getLogger().exiting(MergingConfigurationSource.class.getName(), "resynchronize");
        }
    }

    /**
     * Set a specific configuration source at the specified position into the internal list.
     * 
     * @param aPosition
     *        number of the configuration source
     * @param aSource
     *        to be inserted TODO What about exception handling?
     */
    public void setConfigSource(final int aPosition, final ConfigurationSource aSource) {
        this.configSources.add(aPosition, aSource);
    }

    /**
     * Set the list of configuration sources for this merger object. Please note that assigning the
     * sources with method will make the list itself inmutable. If source should frequently be added
     * later, please use the setConfigSource method.
     * 
     * @param aSourceArray
     *        which should be inserted
     */
    public void setConfigSources(final ConfigurationSource[] aSourceArray) {
        this.configSources = Arrays.asList(aSourceArray);
    }

    /**
     * Sets the strict merging.
     * 
     * @param strictMerging
     *        The strictMerging to set.
     */
    public void setStrictMerging(final boolean strictMerging) {
        this.strictMerging = strictMerging;
    }
}
