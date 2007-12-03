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
package org.eclipse.swordfish.configrepos;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactory;
import org.eclipse.swordfish.configrepos.configuration.exceptions.ConfigurationRepositoryConfigException;
import org.eclipse.swordfish.configrepos.configuration.sources.ConfigurationSource;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.resource.sources.ResourceSource;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePathUtil;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;
import org.eclipse.swordfish.configrepos.shared.XMLConfiguration;
import org.eclipse.swordfish.configrepos.util.DirectorySourceUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <b>The ConfigurationManager is the facade inside the SOP SBB core into the configuration services
 * of the SOP infrastructure. It can be queried for configurations dedicated to the bootstrap of the
 * SBB core, as well as to the SBB core components. It additionally can be used to fetch resource
 * data stored in the central SOP Configuration Repository, if any such is available in the SOP
 * infrastructure.</b>
 * <h2>Spring Framework Usage</h2>
 * <p>
 * The ConfigurationManager is being instantiated as any other Spring bean inside a BeanFactory, and
 * will be assigned the name 'cfgmngr'. It will be instantiated as a singleton for that specific
 * BeanFactory, and thus will be the coordinator for configuration related issues between the
 * configuration infrastructure and any participant using a specific SBB core. A
 * ConfigurationManager is usually being create as follows:
 * </p>
 * 
 * <pre>
 * ConfigurationManager manager = (ConfigurationManager) factory.getBean(&quot;manager&quot;);
 * </pre>
 * 
 * The ConfigurationManager can provide configurations for specific participants:
 * 
 * <pre>
 * try {
 * Configuration cfg = manager.getConfiguration(&quot;participant&quot;);
 * } catch (ConfigurationException cme) {
 * ...
 * }
 * </pre>
 * 
 * <b>which will be compile from a set of different sources as defined inside the SBB core. The
 * sources can encompass the participants working directory, the SBB core installation's
 * configuration directory and the SBB Configuration Repository as a central configuration source.</b>
 * The ConfigurationManager can additionally be consulted for resource data for specific
 * participants:
 * 
 * <pre>
 * try {
 * byte[] res = manager.getResource(&quot;participant&quot;);
 * } catch (ConfigurationException cme) {
 * ...
 * }
 * </pre>
 */
public class ConfigurationRepositoryManagerInternalImpl implements ConfigurationRepositoryManagerInternal, DisposableBean,
        BeanFactoryAware, BeanNameAware, InitializingBean {

    /** Qualifier for the Log4J MDC. */
    public static final String LOGGING_MDCKEY = "sbb-configrepos-proxy.qualifier";

    /** Base path to local configurations/resources. */
    private String localResourceBase = null;

    /** Parameter for turning remote calls on and off. */
    private boolean skipRemoteRepositoryCalls = true;

    /**
     * List of components which should be informed about changes in the skipRemoteRepositoryCalls
     * property.
     */
    private List reverseLinkList = null;

    /**
     * The logger for this log manager. FIXME general guideline for internal logging missing. Remove
     * this workaround!
     */
    private Logger logger = null;

    /** The configuration source referenced by this manager instance. */
    private ConfigurationSource configSource = null;

    /** The resource source referenced by this manager instance. */
    private ResourceSource resourceSource = null;

    /** The boot configuration assigned to this manager instance. */
    private Configuration bootConfiguration = null;

    /** The bean name as assigned by the Spring framework. */
    private String beanName = null;

    /**
     * Default tree qualifier which should be used in case none is provided in a call.
     */
    private String defaultTreeQualifier = null;

    /** The bean factory which created this manager instance. */
    private BeanFactory beanFactory = null;

    /**
     * A participant ID preset for this instance, as a fallback if configuration requests do not
     * provide a dedicated instance id themselves.
     */
    private ScopePath fixedScopePath = null;

    /** Operational logger which should be used by this manager. */
    private ConfigReposOperationalLogger operationalLogger;

    /** JXPathContzextFactory. */
    private JXPathContextFactory jxPathContextFactory;

    /**
     * Default constructor.
     */
    public ConfigurationRepositoryManagerInternalImpl() {
        super();
        this.logger = Logger.getLogger(ConfigurationRepositoryManagerInternal.class.getName(), "ConfigReposMessageBundle");
        this.logger.finest("Instantiating " + ConfigurationRepositoryManagerInternalImpl.class.getName()
                + " [$Revision: 1.1.2.3 $]");
    }

    /**
     * (non-Javadoc).
     * 
     * @param aEventListener
     *        to be added.
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#addConfigurationRepositoryEventListner(org.eclipse.swordfish.configrepos.ConfigurationRepositoryEventListenerInternal)
     */
    public void addConfigurationRepositoryEventListner(final ConfigurationRepositoryEventListenerInternal aEventListener) {
        throw new IllegalArgumentException("not implemented yet.");
    }

    /**
     * (non-Javadoc).
     * 
     * @param aTreeQualifier
     *        that the event listener is interested in.
     * @param aEventListener
     *        to be added.
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#addConfigurationRepositoryEventListner(java.lang.String,
     *      org.eclipse.swordfish.configrepos.ConfigurationRepositoryEventListenerInternal)
     */
    public void addConfigurationRepositoryEventListner(final String aTreeQualifier,
            final ConfigurationRepositoryEventListenerInternal aEventListener) {
        throw new IllegalArgumentException("not implemented yet.");
    }

    /**
     * Execute initialization after properties have been set.
     * 
     * @throws Exception
     *         in case initialization was not successful.
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if ((null != this.reverseLinkList) && (this.reverseLinkList.size() > 0)) {
            Iterator iter = this.reverseLinkList.iterator();
            while (iter.hasNext()) {
                Object ref = iter.next();
                if (AbstractRepositorySource.class.isAssignableFrom(ref.getClass())) {
                    ((AbstractRepositorySource) ref).setManager(this);
                }
            }
        }

        if (null != this.operationalLogger) {
            this.operationalLogger.issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSTARTUP,
                    new Object[] {this.getBeanName()});
        }
    }

    /**
     * Will close down this configuration manager instance. All sources will be closed too, and the
     * manager will not be able to serve any subsequent requests for configuration or resource data.
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#close()
     */
    public void close() {
        if (null != this.configSource) {
            this.configSource.close();
        }

        if (null != this.resourceSource) {
            this.resourceSource.close();
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     *         in case of any error encountered during distruction.
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        this.close();

        this.configSource = null;
        this.resourceSource = null;

        if (null != this.reverseLinkList) {
            Iterator iter = this.reverseLinkList.iterator();
            while (iter.hasNext()) {
                AbstractRepositorySource resource = (AbstractRepositorySource) iter.next();
                resource.setManager(null);
            }
            this.reverseLinkList.clear();
            this.reverseLinkList = null;
        }

        this.beanFactory = null;
        this.beanName = null;

        if (null != this.bootConfiguration) {
            // TODO commented for now, we need to fix this, if we do this, we
            // get an error for classloading
            // while running inside oc4j.
            // bootConfiguration.clear();
            this.bootConfiguration = null;
        }
        this.logger = null;
        this.operationalLogger = null;

        if (null != this.operationalLogger) {
            this.operationalLogger.issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_COMPSHUTDOWN,
                    new Object[] {this.getBeanName()});
        }
    }

    /**
     * Return the bean factory assigned to this instance.
     * 
     * @return BeanFactory reference assigned to this instance.
     */
    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    /**
     * Return the bean name assigned to this instance.
     * 
     * @return String which contains the bean name, or possibly null, in case none was assigned.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Fetches the boot configuration assigned to this instance, if any.
     * 
     * @return Returns the bootConfiguration.
     */
    public Configuration getBootConfiguration() {
        return this.bootConfiguration;
    }

    /**
     * Returns the configuration source assigned to this manager instance.
     * 
     * @return the config source assigned to the manager, null in case non has been assigned so far
     */
    public ConfigurationSource getConfigSource() {
        return this.configSource;
    }

    /**
     * Fetch the actual configuration for a specific identifier.
     * 
     * @param aTreeQualifier
     *        is the tree which should be traversed.
     * @param aScopePath
     *        is the path taken in the configuration tree.
     * 
     * @return returns the configuration found.
     * 
     * @throws ConfigurationRepositoryConfigException
     *         in case any sub-component encountered an error.
     * 
     * @see org.eclipse.swordfish.configrepos.sources.ConfigurationSource#getConfiguration(ScopePath)
     */
    public Configuration getConfiguration(final String aTreeQualifier, final ScopePath aScopePath)
            throws ConfigurationRepositoryConfigException {
        Configuration result = null;
        // FIXME pushBeanNameOnNDC();

        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(ConfigurationRepositoryManagerInternalImpl.class.getName(), "getConfiguration", new Object[] {
                    (null != aTreeQualifier ? aTreeQualifier : this.defaultTreeQualifier),
                    (null != aScopePath ? aScopePath : this.fixedScopePath)});
        }

        try {
            // FIXME pushInstanceIdOnMDC(aIdentifier);

            if (null == this.configSource) {
                // TODO i18n
                RuntimeException fatal =
                        new IllegalArgumentException(
                                "no configuration source available. Either it was not set, or the manager has previously closed it on request.");
                if (null != this.operationalLogger) {
                    this.operationalLogger.issueOperationalLog(
                            ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION, new Object[] {fatal});
                }
                throw fatal;
            }
            result =
                    this.configSource.getConfiguration((null != aTreeQualifier ? aTreeQualifier : this.defaultTreeQualifier),
                            (null != aScopePath ? aScopePath : this.fixedScopePath));
            return result;
        } catch (RuntimeException re) {
            if (null != this.operationalLogger) {
                this.operationalLogger.issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION,
                        new Object[] {re});
            }
            // TODO i18n
            throw new ConfigurationRepositoryConfigException("Internal runtime exception.", re);
        } catch (ConfigurationRepositoryConfigException e) {
            if ((null != this.logger) && this.logger.isLoggable(Level.WARNING)) {
                this.logger.log(Level.WARNING, "Configuration exception", e);
            }
            throw e;
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(ConfigurationRepositoryManagerInternalImpl.class.getName(), "getConfiguration", result);
            }
            // FIXME popBeanNameFromNDC();
        }
    }

    /**
     * Fetch the tree qualifier which has been set for this manager.
     * 
     * @return Returns the defaultTreeQualifier.
     */
    public String getDefaultTreeQualifier() {
        return this.defaultTreeQualifier;
    }

    /**
     * Return the instance ID assigned to this instance, if any is available.
     * 
     * @return Returns the fixedScopePath.
     */
    public ScopePath getFixedScopePath() {
        return this.fixedScopePath;
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
     * Get the local resource base, i.e. the URL which contains configurations and resource data.
     * 
     * @return Returns the localResourceBase.
     */
    public String getLocalResourceBase() {
        return this.localResourceBase;
    }

    /**
     * Return the logger assigned to this instance, if any is available.
     * 
     * @return Logger assigned to the manager.
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#getLogger() FIXME general guideline
     *      for internal logging missing. Remove this workaround!
     */
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Return the operational logging adapter assigned to this manager.
     * 
     * @return Returns the operationalLogger.
     */
    public ConfigReposOperationalLogger getOperationalLogger() {
        return this.operationalLogger;
    }

    /**
     * Fetch a resource for a specific identifier.
     * 
     * @param aTreeQualifier
     *        is the tree that should be traversed.
     * @param aScopePath
     *        is the path that shoule be taken in the tree
     * @param aComponent
     *        is the component owning the resource
     * @param aResourceIdentifier
     *        is the name of the resource in the component
     * 
     * @return the resource found
     * 
     * @throws ConfigurationRepositoryResourceException
     *         in case the resource could not be fetched
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.ResourceSource#getResource(java.lang.String)
     */
    public InputStream getResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResourceIdentifier) throws ConfigurationRepositoryResourceException {
        InputStream result = null;
        // FIXME this.pushBeanNameOnNDC();

        if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
            this.logger.entering(ConfigurationRepositoryManagerInternalImpl.class.getName(), "getResource", new Object[] {
                    (null != aTreeQualifier ? aTreeQualifier : this.defaultTreeQualifier),
                    (null != aScopePath ? aScopePath : this.fixedScopePath), aComponent, aResourceIdentifier});
        }

        try {
            // FIXME pushInstanceIdOnMDC(aIdentifier);

            if (null == this.resourceSource) {
                // TODO i18n
                RuntimeException fatal =
                        new IllegalArgumentException("no resource source available. Either it was not set, "
                                + "or the manager has previously closed it on request.");
                if (null != this.operationalLogger) {
                    this.operationalLogger.issueOperationalLog(
                            ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION, new Object[] {fatal});
                }
                throw fatal;
            }
            result =
                    this.resourceSource.getResource((null != aTreeQualifier ? aTreeQualifier : this.defaultTreeQualifier),
                            (null != aScopePath ? aScopePath : this.fixedScopePath), aComponent, aResourceIdentifier);
            return result;
        } catch (RuntimeException re) {
            if (null != this.operationalLogger) {
                this.operationalLogger.issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION,
                        new Object[] {re});
            }
            // TODO i18n
            throw new ConfigurationRepositoryResourceException("internal runtime exception.", re);
        } finally {
            if ((null != this.logger) && this.logger.isLoggable(Level.FINEST)) {
                this.logger.exiting(ConfigurationRepositoryManagerInternalImpl.class.getName(), "getResource", result);
            }
            // FIXME popBeanNameFromNDC();
        }
    }

    /**
     * Fetch the resource source from this manager instance.
     * 
     * @return ResourceSource assigned to this manager instance. Return null in case non has been
     *         assigned so far.
     */
    public ResourceSource getResourceSource() {
        return this.resourceSource;
    }

    /**
     * Get the list of components linked to this manager.
     * 
     * @return Returns the reverseLinkList.
     */
    public List getReverseLinkList() {
        return this.reverseLinkList;
    }

    /**
     * (non-Javadoc).
     * 
     * @return whether remote configuration calls are skipped or not.
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#isSkipRemoteRepositoryCalls()
     */
    public boolean isSkipRemoteRepositoryCalls() {
        return this.skipRemoteRepositoryCalls;
    }

    /**
     * Remove a configuration event listener from this managers list.
     * 
     * @param aEventListener
     *        that should be removed from the internal list.
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#removeConfigurationRepositoryEventListner(org.eclipse.swordfish.configrepos.ConfigurationRepositoryEventListenerInternal)
     */
    public void removeConfigurationRepositoryEventListner(final ConfigurationRepositoryEventListenerInternal aEventListener) {
        throw new IllegalArgumentException("not implemented yet.");
    }

    /**
     * Flush all transient data for a specific instance or all instances in the configuration and
     * resource source tree.
     * 
     * @param aScopePath
     *        which should be resynchronized
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#resychronize(java.lang.String)
     */
    public void resynchronize(final String aScopePath) {
        if (null != this.configSource) {
            this.configSource.resynchronize(aScopePath);
        }

        if (null != this.resourceSource) {
            this.resourceSource.resynchronize(aScopePath);
        }
    }

    /**
     * Assign a Spring BeanFactory which created this instance.
     * 
     * @param beanFactory
     *        of the spring framework
     * 
     * @throws BeansException
     *         in case the beanFactory parameter was not of the expected state
     */
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * Assign a bean name to this manager instance.
     * 
     * @param name
     *        is the identifier assigned to this bean.
     * 
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(final String name) {
        this.beanName = name;
    }

    /**
     * Set the boot configuration for this manager instance.
     * 
     * @param bootConfiguration
     *        The bootConfiguration to set. In case this is null, a runtime exception will be
     *        thrown.
     */
    public void setBootConfiguration(final Configuration bootConfiguration) {
        if (null == bootConfiguration) {
            // TODO i18n
            RuntimeException fatal = new IllegalArgumentException("No empty argument allowed when assigning a boot configuration.");
            if (null != this.operationalLogger) {
                this.operationalLogger.issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION,
                        new Object[] {fatal});
            }
            throw fatal;
        }
        this.bootConfiguration = bootConfiguration;
    }

    /**
     * Set the resource source for this manager instance.
     * 
     * @param aSource
     *        is the source which shall be assigned. In case this is null, a runtime exception will
     *        be thrown.
     */
    public void setConfigSource(final ConfigurationSource aSource) {
        if (null == aSource) {
            // TODO i18n
            RuntimeException fatal =
                    new IllegalArgumentException("Setting null for the configuration source property is not allowed.");
            if (null != this.operationalLogger) {
                this.operationalLogger.issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION,
                        new Object[] {fatal});
            }
            throw fatal;
        }
        this.configSource = aSource;
    }

    /**
     * Set the default tree qualifier for this manager.
     * 
     * @param defaultTreeQualifier
     *        The defaultTreeQualifier to set.
     */
    public void setDefaultTreeQualifier(final String defaultTreeQualifier) {
        this.defaultTreeQualifier = defaultTreeQualifier;
    }

    /**
     * Set the default participant identifier which should be used by this configuration manager.
     * 
     * @param aFixedScopePath
     *        The fixedScopePath to set.
     */
    public void setFixedScopePath(final ScopePath aFixedScopePath) {
        this.fixedScopePath = aFixedScopePath;
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
     * Set the local resource base, i.e. URL which will include the configuration and resource data.
     * 
     * @param localResourceBase
     *        The localResourceBase to set.
     */
    public void setLocalResourceBase(final String localResourceBase) {
        this.localResourceBase = DirectorySourceUtil.compileSpringResourcePath(localResourceBase);
    }

    /**
     * (non-Javadoc).
     * 
     * @param aLogger
     *        that should be assigned to this manager.
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#setLogger(java.util.logging.Logger)
     *      FIXME general guideline for internal logging missing. Remove this workaround!
     */
    public void setLogger(final Logger aLogger) {
        this.logger = aLogger;
    }

    /**
     * Set the operational logging adapter.
     * 
     * @param operationalLogger
     *        The operationalLogger to set.
     */
    public void setOperationalLogger(final ConfigReposOperationalLogger operationalLogger) {
        this.operationalLogger = operationalLogger;
    }

    /**
     * Set a fixed ParticipantIdentity for this object. This will prepend the former with a location
     * being fetched from the boot configuration. This will have to be set prior to using this
     * method.
     * 
     * @param appId
     *        which should be set
     * @param instId
     *        which should be set
     */
    public void setParticipantId(final String appId, final String instID) {
        // FIXME Check this! Maybe remove method
        if (null == this.bootConfiguration)
            throw new IllegalArgumentException("can't set fixed participant id without having a boot configuration");

        String location = null;
        try {
            JXPathContext ctx =
                    this.jxPathContextFactory.newContext(null, ((XMLConfiguration) this.bootConfiguration).getDocument());
            location =
                    (String) ctx.getValue("/configuration/Participant[@sopcs:name='/']"
                            + "/ConfigurationRepositoryManager/Location", String.class);

            this.setFixedScopePath(new ScopePathUtil(ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR)
                .composeScopePath(location, appId, instID));
        } catch (ScopePathException spe) {
            RuntimeException fatal =
                    new IllegalArgumentException("can not set participant id '" + appId + "/" + instID + "' for location '"
                            + location + "'");
            if (null != this.operationalLogger) {
                this.operationalLogger.issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION,
                        new Object[] {fatal});
            }
            throw fatal;
        }
    }

    /**
     * Set the configuration source for this manager instance.
     * 
     * @param aSource
     *        is the source which shall be assigned. In case this is null, a runtime exception will
     *        be thrown.
     */
    public void setResourceSource(final ResourceSource aSource) {
        if (null == aSource) {
            // TODO i18n
            RuntimeException fatal = new IllegalArgumentException("Setting null for the resource source property is not allowed.");
            if (null != this.operationalLogger) {
                this.operationalLogger.issueOperationalLog(ConfigReposOperationalLogger.CONFIGREPOSPROXY_MSGID_INTERNALEXCEPTION,
                        new Object[] {fatal});
            }
            throw fatal;
        }
        this.resourceSource = aSource;
    }

    /**
     * Set the list of components linked into this manager.
     * 
     * @param reverseLinkList
     *        The reverseLinkList to set.
     */
    public void setReverseLinkList(final List reverseLinkList) {
        this.reverseLinkList = reverseLinkList;
    }

    /**
     * Enable/Disable skipping remote configuration calls.
     * 
     * @param aSetSkip =
     *        true, to skip remote calls. If false: include remote calls.
     * 
     * @see org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal#setSkipRemoteRepositoryCalls(boolean)
     */
    public void setSkipRemoteRepositoryCalls(final boolean aSetSkip) {
        this.skipRemoteRepositoryCalls = aSetSkip;
    }

    /**
     * Remove the least entry of the Log4J NDC.
     */
    protected void popBeanNameFromNDC() {
        /*
         * FIXME if (null != logger && null != beanName) { NDC.pop(); }
         */
    }

    /**
     * Remove the instance id from the mapped diagnostic context of this thread.
     */
    protected void popInstanceIdFromMDC() {
        /*
         * FIXME if (null != logger) { MDC.remove(LOGGING_MDCKEY); }
         */
    }

    /**
     * Push the spring bean name on the Log4J NDC stack.
     */
    protected void pushBeanNameOnNDC() {
        /*
         * FIXME if (null != logger && null != beanName) { NDC.push(beanName); }
         */
    }

    /**
     * Push the instance id into the mapped diagnostic context of this thread.
     * 
     * @param aInstance
     *        that shall be stored
     */
    protected void pushInstanceIdOnMDC(final String aInstance) {
        /*
         * FIXME if (null != logger) { MDC.put(LOGGING_MDCKEY, aInstance); }
         */
    }
}
