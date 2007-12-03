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
package org.eclipse.swordfish.core.components.extension.impl;

import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Default implementation of ExtensionFactory that returns a singleton bean specified in the spring
 * configuration as an instance of the papi extension.
 * 
 */
public class DefaultExtensionFactory implements ExtensionFactory, ApplicationContextAware {

    /**
     * The instance of the papi extension. Specified in the Spring config and set by Spring when
     * instantiating the factory
     */
    private Object instance = null;

    /** The name of the bean that provides the actual extension object. */
    private String instanceName;

    /** The application context to use. */
    private ApplicationContext context;

    /**
     * Provides an instance of the papi extension.
     * 
     * @param diversifier
     *        is ignored in this default implementation
     * 
     * @return the instance
     * 
     * @see org.eclipse.swordfish.core.components.extension.ExtensionFactory#getInstance(java.lang.String)
     */
    public Object getInstance(final String diversifier) {
        if (null == this.instance) {
            this.instance = this.context.getBean(this.instanceName);
        }
        return this.instance;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
        this.context = arg0;
    }

    /**
     * Sets the instance name.
     * 
     * @param name
     *        the new instance name
     */
    public void setInstanceName(final String name) {
        this.instanceName = name;
    }

}
