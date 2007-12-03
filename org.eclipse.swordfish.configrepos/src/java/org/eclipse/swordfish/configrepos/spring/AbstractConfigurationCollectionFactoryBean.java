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

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * The Class AbstractConfigurationCollectionFactoryBean.
 * 
 */
public abstract class AbstractConfigurationCollectionFactoryBean extends AbstractFactoryBean implements BeanFactoryAware,
        BeanNameAware {

    /** The bean name. */
    private String beanName = null;

    /** The base path being used to identify the element which hold the data. */
    private String basePath = null;

    /** The manager which will be consulted when compiling the list. */
    private Configuration configuration = null;

    /** The bean factory. */
    private BeanFactory beanFactory;

    /**
     * Instantiates a new abstract configuration collection factory bean.
     */
    public AbstractConfigurationCollectionFactoryBean() {
        super();
    }

    /**
     * Gets the base path.
     * 
     * @return Returns the basePath.
     */
    public String getBasePath() {
        return this.basePath;
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Gets the configuration.
     * 
     * @return Returns the manager.
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Sets the base path.
     * 
     * @param basePath
     *        The basePath to set.
     */
    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    /**
     * (non-Javadoc).
     * 
     * @param beanFactory
     *        the bean factory
     * 
     * @throws BeansException
     *         the beans exception
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * (non-Javadoc).
     * 
     * @param name
     *        the name
     * 
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(final String name) {
        this.beanName = name;
    }

    /**
     * Sets the configuration.
     * 
     * @param aConfiguration
     *        which will be consulted
     */
    public void setConfiguration(final Configuration aConfiguration) {
        this.configuration = aConfiguration;
    }
}
