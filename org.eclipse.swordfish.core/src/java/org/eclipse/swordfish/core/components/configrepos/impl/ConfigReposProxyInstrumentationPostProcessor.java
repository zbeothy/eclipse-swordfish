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
package org.eclipse.swordfish.core.components.configrepos.impl;

import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternalImpl;
import org.eclipse.swordfish.core.components.configrepos.instrumentation.InstrumentedConfigurationManagerInternal;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * The Class ConfigReposProxyInstrumentationPostProcessor.
 * 
 */
public class ConfigReposProxyInstrumentationPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    /** Bean factory in charge of this processor. */
    private BeanFactory beanFactory = null;

    /**
     * Instantiates a new config repos proxy instrumentation post processor.
     */
    public ConfigReposProxyInstrumentationPostProcessor() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param aFactory
     *        in charge of this processor
     */
    public ConfigReposProxyInstrumentationPostProcessor(final BeanFactory aFactory) {
        super();
        this.beanFactory = aFactory;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aBean
     *        the a bean
     * @param arg1
     *        the arg1
     * 
     * @return the object
     * 
     * @throws BeansException
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
     *      java.lang.String)
     */
    public Object postProcessAfterInitialization(final Object aBean, final String arg1) throws BeansException {

        if (aBean instanceof org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternalImpl) {
            this.registerConfigurationManagerWithJMX(aBean);
        }
        return aBean;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aBean
     *        the a bean
     * @param arg1
     *        the arg1
     * 
     * @return the object
     * 
     * @throws BeansException
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
     *      java.lang.String)
     */
    public Object postProcessBeforeInitialization(final Object aBean, final String arg1) throws BeansException {
        return aBean;
    }

    /**
     * Sets the bean factory.
     * 
     * @param aBeanFactory
     *        which should be used
     * 
     * @throws BeansException
     *         in case the factory reference could not be processed
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(final BeanFactory aBeanFactory) throws BeansException {
        this.beanFactory = aBeanFactory;
    }

    /**
     * Register configuration manager with JMX.
     * 
     * @param aBean
     *        which should be instrumented
     */
    private void registerConfigurationManagerWithJMX(final Object aBean) {
        InstrumentedConfigurationManagerInternal mbean =
                (InstrumentedConfigurationManagerInternal) this.beanFactory.getBean(
                        "org.eclipse.swordfish.configrepos.spring.InstrumentedConfigurationManagerInternal_base",
                        InstrumentedConfigurationManagerInternal.class);
        mbean.setDelegate((ConfigurationRepositoryManagerInternalImpl) aBean);
    }

}
