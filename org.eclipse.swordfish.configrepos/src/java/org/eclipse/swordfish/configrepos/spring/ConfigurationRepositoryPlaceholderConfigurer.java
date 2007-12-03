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
package org.eclipse.swordfish.configrepos.spring;

import java.util.Properties;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryInternalException;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternalImpl;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathUtil;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Use this as an alternative source for bean configuration in conjunction with the SOP
 * Configuration Repository. To fetch a configuration entry, this placeholder configurer must be
 * defined in conjunction with a bean factory.<b/> Example:<b/>
 * 
 * <pre>
 * &lt;bean id=&quot;placeholder&quot; class=&quot;org.eclipse.swordfish.configrepos.spring.ConfigurationRepositoryPlaceholderConfigurer&quot;
 * lazy-init=&quot;false&quot;&gt;
 * &lt;property name=&quot;manager&quot;&gt;&lt;ref bean=&quot;manager&quot;/&gt;&lt;/property&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean name=&quot;aStringWithIdent&quot; singleton=&quot;false&quot; class=&quot;java.lang.String&quot;&gt;
 * &lt;constructor-arg&gt;&lt;value&gt;$sbb{participantID, srcache.disk-store.directory}&lt;/value&gt;&lt;/constructor-arg&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean name=&quot;manager&quot; parent=&quot;cfgmngr&quot; singleton=&quot;false&quot;/&gt;
 * </pre>
 * 
 */
public class ConfigurationRepositoryPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    /** Will separate a tuple defining a path to a configuration entry. */
    public static final char SBBPLACEHOLDERPARTSEPARATOR = ';';

    /** The standard prefix this the references. */
    public static final String SBBPLACEHOLDERPREFIX = "$sbb{";

    /** The bean name of this object. */
    private String beanName = null;

    /**
     * ConfigurationRepositoryManagerInternalImpl which will be used alternativelly to the
     * configuration assigned to this instance.
     */
    private ConfigurationRepositoryManagerInternalImpl manager = null;

    /**
     * Configuration which will be used alternativelly to the configuration manager assigned to this
     * instance.
     */
    private Configuration config = null;

    /** JXPathContextFactory used. */
    private JXPathContextFactory jxPathContextFactory = null;

    /**
     * Instantiates a new configuration repository placeholder configurer.
     */
    public ConfigurationRepositoryPlaceholderConfigurer() {
        super();
        super.setPlaceholderPrefix(SBBPLACEHOLDERPREFIX);
    }

    /**
     * Return the bean name of this object. Required since superclass does not provide access to the
     * related property
     * 
     * @return the name of the bean as set by Spring
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Gets the config.
     * 
     * @return Returns the config.
     */
    public Configuration getConfig() {
        return this.config;
    }

    /**
     * Gets the jx path context factory.
     * 
     * @return the jx path context factory
     */
    public JXPathContextFactory getJxPathContextFactory() {
        return this.jxPathContextFactory;
    }

    /**
     * Gets the manager.
     * 
     * @return Returns the manager.
     */
    public ConfigurationRepositoryManagerInternalImpl getManager() {
        return this.manager;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aBeanName
     *        which will be set by Spring
     * 
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(final String aBeanName) {
        super.setBeanName(aBeanName);
        this.beanName = aBeanName;
    }

    /**
     * Sets the config.
     * 
     * @param config
     *        The config to set.
     */
    public void setConfig(final Configuration config) {
        this.config = config;
    }

    /**
     * Sets the jx path context factory.
     * 
     * @param jxPathContextFactory
     *        the new jx path context factory
     */
    public void setJxPathContextFactory(final JXPathContextFactory jxPathContextFactory) {
        this.jxPathContextFactory = jxPathContextFactory;
    }

    /**
     * Sets the manager.
     * 
     * @param manager
     *        The manager to set.
     */
    public void setManager(final ConfigurationRepositoryManagerInternalImpl manager) {
        this.manager = manager;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aPlaceholder
     *        the a placeholder
     * @param aPropos
     *        the a propos
     * 
     * @return the string
     * 
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#
     *      resolvePlaceholder(java.lang.String, java.util.Properties)
     */
    @Override
    protected String resolvePlaceholder(final String aPlaceholder, final Properties aPropos) {
        // TODO Review whether we have to change this method too!?
        return super.resolvePlaceholder(aPlaceholder, aPropos);
    }

    /**
     * Try to identify were the placeholder should be fetched from.
     * 
     * @param aPlaceholder
     *        the a placeholder
     * @param aPropos
     *        the a propos
     * @param aSystemPropertiesMode
     *        the a system properties mode
     * 
     * @return the string
     * 
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#
     *      resolvePlaceholder(java.lang.String, java.util.Properties, int)
     */
    @Override
    protected String resolvePlaceholder(final String aPlaceholder, final Properties aPropos, final int aSystemPropertiesMode) {
        String result = null;

        if (PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE == aSystemPropertiesMode) {
            result = super.resolvePlaceholder(StringUtils.replaceChars(aPlaceholder, '/', '.'), aPropos, aSystemPropertiesMode);
        }

        // TODO change this to use regular expressions, rather than indexOf();
        if (null == result) {
            if (null != this.manager) {
                if (aPlaceholder.indexOf(SBBPLACEHOLDERPARTSEPARATOR) != -1) {
                    int position;

                    // In case the placeholder is a tuple, use the first element
                    // as the scopepath bean reference,
                    // and the second as the placeholder
                    position = aPlaceholder.indexOf(SBBPLACEHOLDERPARTSEPARATOR);
                    if (position != -1) {
                        String pathdef = aPlaceholder.substring(0, position).trim();
                        ScopePath path =
                                new ScopePathUtil(ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR)
                                    .composeScopePath(pathdef);
                        result =
                                this.resolvePlaceholderFromConfigReposManager(aPlaceholder.substring(position + 1).trim(),
                                        this.manager, path);
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("resolved '" + aPlaceholder + "' with '" + result + "'");
                        }
                    }
                } else {
                    // Assume the scopepath has been assigned to the
                    // configuration repository manager
                    // instance.
                    result = this.resolvePlaceholderFromConfigReposManager(aPlaceholder, this.manager, null);
                }
            } else if (null != this.config) {
                // try to resolve the identifier with a configuration object
                result = this.resolvePlaceholderFromConfiguration(aPlaceholder, this.config);
            }
        }

        if ((null == result) && (PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK == aSystemPropertiesMode)) {
            result = super.resolvePlaceholder(StringUtils.replaceChars(aPlaceholder, '/', '.'), aPropos, aSystemPropertiesMode);
        }

        if (null == result) {
            if (null != this.manager)
                throw new IllegalArgumentException("Placeholder configurator bean '"
                        + this.getBeanName()
                        + "' did not resolve '"
                        + aPlaceholder
                        + "'"
                        + (null != this.manager.getDefaultTreeQualifier() ? " in tree '" + this.manager.getDefaultTreeQualifier()
                                + "'." : "."));
            else
                throw new IllegalArgumentException("Placeholder configurator bean '" + this.getBeanName() + "' did not resolve '"
                        + aPlaceholder + "' from configuration source.");
        }

        // return any result, possibly null
        return result;
    }

    /**
     * Resolve a property from a ConfigurationRepositoryManagerInternalImpl object.
     * 
     * @param aPathInConfiguration
     *        which is being searched for
     * @param aManager
     *        which is being used
     * @param aScopePath
     *        an ID
     * 
     * @return the property or null
     */
    private String resolvePlaceholderFromConfigReposManager(final String aPathInConfiguration,
            final ConfigurationRepositoryManagerInternalImpl aManager, final ScopePath aScopePath) {
        try {
            return this.resolvePlaceholderFromConfiguration(aPathInConfiguration, aManager.getConfiguration(null, aScopePath));
        } catch (ConfigurationRepositoryInternalException ce) {
            return null;
        }
    }

    /**
     * Resolve a property from a configuration object.
     * 
     * @param placeholder
     *        which is being searched for
     * @param aConfig
     *        is the configuration object which shall be consulted
     * 
     * @return the property or null
     */
    private String resolvePlaceholderFromConfiguration(final String placeholder, final Configuration aConfig) {
        if (null == aConfig) return null;
        try {
            JXPathContext context = this.jxPathContextFactory.newContext(null, (aConfig));
            String query = "/configuration/" + StringUtils.stripStart(placeholder, "/");
            String result = (String) context.getValue(query, String.class);
            return result;
        } catch (Throwable e) {
            this.logger.fatal("Error processing placeholder '" + placeholder + "':", e);
            return null;
        }
    }
}
