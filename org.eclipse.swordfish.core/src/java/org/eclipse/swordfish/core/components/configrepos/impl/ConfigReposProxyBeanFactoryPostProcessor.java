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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * The Class ConfigReposProxyBeanFactoryPostProcessor.
 * 
 */
public class ConfigReposProxyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    /**
     * Instantiates a new config repos proxy bean factory post processor.
     */
    public ConfigReposProxyBeanFactoryPostProcessor() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aFactory
     *        the a factory
     * 
     * @throws BeansException
     * 
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory aFactory) throws BeansException {
        aFactory.addBeanPostProcessor(new ConfigReposProxyInstrumentationPostProcessor(aFactory));
    }

}
