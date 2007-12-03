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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Abstract repository source for configuration type sources.
 * 
 */
public abstract class AbstractRepositorySource implements RepositorySource, BeanNameAware, ApplicationContextAware, DisposableBean,
        InitializingBean {

    /**
     * The logger assigned to this repository source FIXME general guideline for internal logging
     * missing. Remove this workaround!.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName(), "ConfigReposMessageBundle");

    /** Configuration Repository Proxy operational logging endpoint. */
    private ConfigReposOperationalLogger operationalLogger = null;

    /** Logging level assigned to this component. */
    private Level logLevel = null;

    /** The manager. */
    private ConfigurationRepositoryManagerInternal manager = null;

    /** The bean name of this repository source. */
    private String beanName = null;

    /** Assigned bean factory. */
    private BeanFactory factory = null;

    /** The application context. */
    private ApplicationContext applicationContext;

    /**
     * Default constructor.
     */
    public AbstractRepositorySource() {
        super();
    }

    /**
     * Initialize this bean.
     * 
     * @throws Exception
     *         in case initialization went wrong.
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if ((null != this.logLevel) && (null != this.logger)) {
            this.logger.setLevel(this.logLevel);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.papi.internal.configrepos.RepositorySource#close()
     */
    public void close() {
        // NOP
    }

    /**
     * Destroy this spring bean.
     * 
     * @throws Exception
     *         in case destruction of the bean went wrong-
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        this.manager = null;
    }

    /**
     * Compare this bean with another object.
     * 
     * @param aObject
     *        that this bean should be compared to.
     * 
     * @return true if both objects are equal, otherwise false
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object aObject) {
        if (aObject instanceof AbstractRepositorySource)
            return ("" + this.beanName).equals("" + ((AbstractRepositorySource) aObject).beanName);
        else
            return super.equals(aObject);
    }

    /**
     * Return the application context.
     * 
     * @return the application context
     */
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    /**
     * Get the current bean factory which creates this object.
     * 
     * @return the factory reference assigned to this object
     */
    public BeanFactory getBeanFactory() {
        return this.factory;
    }

    /**
     * Return the name of the spring bean.
     * 
     * @return String containing the bean name
     * 
     * @@org.springframework.jmx.export.metadata.ManagedAttribute (description="Bean name assigned
     *                                                            by the runtime framework.") Return
     *                                                            the assigned bean name
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the logger assigned to this source.
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#getLogger() FIXME general guideline
     *      for internal logging missing. Remove this workaround!
     */
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Return the current log level.
     * 
     * @return the log level as int
     */
    public String getLogLevel() {
        return this.logLevel.getName();
    }

    /**
     * Return the manager reference.
     * 
     * @return the configuration repository manager
     */
    public ConfigurationRepositoryManagerInternal getManager() {
        return this.manager;
    }

    /**
     * Return the assigned operational logger adapter.
     * 
     * @return Returns the operationalLogger.
     */
    public ConfigReposOperationalLogger getOperationalLogger() {
        return this.operationalLogger;
    }

    /**
     * Instruct this source to resynchronize its data. This implies forwarding the request to any of
     * its child sources.
     * 
     * @param aScopePath
     *        is the scope path that should be synchronized.
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#resynchronize(java.lang.String)
     */
    public void resynchronize(final String aScopePath) {
        // NOP
    }

    /**
     * Assign a spring framework ApplicationContext.
     * 
     * @param aAppContext
     *        is the spring framework application context assigned to this bean.
     * 
     * @throws BeansException
     *         in case something went wrong.
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(final ApplicationContext aAppContext) throws BeansException {
        this.applicationContext = aAppContext;
    }

    /**
     * Set the bean factory created for this configuration source.
     * 
     * @param aFactory
     *        which should be assigned to this bean
     * 
     * @throws BeansException
     *         in case assignment was not possible
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#
     *      setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(final BeanFactory aFactory) throws BeansException {
        this.factory = aFactory;
    }

    /**
     * Assign spring bean name.
     * 
     * @param aBeanName
     *        is the name this spring bean will have.
     * 
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(final String aBeanName) {
        this.beanName = aBeanName;
    }

    /**
     * Set the logger property of this bean.
     * 
     * @param aLogger
     *        that should be used by this source
     * 
     * @see org.eclipse.swordfish.configrepos.RepositorySource#setLogger(java.util.logging.Logger)
     *      FIXME general guideline for internal logging missing. Remove this workaround!
     */
    public void setLogger(final Logger aLogger) {
        this.logger = aLogger;
    }

    /**
     * Set the loglevel of this component.
     * 
     * @param aLevelName
     *        string representation
     */
    public void setLogLevel(final String aLevelName) {
        this.logLevel = Level.parse(aLevelName);
    }

    /**
     * Set the manager reference.
     * 
     * @param aManager
     *        which should be used by this bean
     */
    public void setManager(final ConfigurationRepositoryManagerInternal aManager) {
        this.manager = aManager;
    }

    /**
     * Set the operational logger adapter.
     * 
     * @param operationalLogger
     *        The operationalLogger to set.
     */
    public void setOperationalLogger(final ConfigReposOperationalLogger operationalLogger) {
        this.operationalLogger = operationalLogger;
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
     * Push the spring bean name on the Log4J NDC stack.
     */
    protected void pushBeanNameOnNDC() {
        /*
         * FIXME if (null != logger && null != beanName) { NDC.push(beanName); }
         */
    }

    /**
     * Issue an operation log message.
     * 
     * @param aMessageID
     *        which should be used
     * @param aParameters
     *        which should configure the message
     */
    void issueOperationalLog(final int aMessageID, final Object[] aParameters) {
        ConfigReposOperationalLogger opLog = this.getOperationalLogger();
        if (null != opLog) {
            opLog.issueOperationalLog(aMessageID, aParameters);
        }
    }
}
