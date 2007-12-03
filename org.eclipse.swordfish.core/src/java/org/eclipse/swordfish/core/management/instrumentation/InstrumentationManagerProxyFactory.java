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
package org.eclipse.swordfish.core.management.instrumentation;

import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A factory for creating InstrumentationManagerProxy objects.
 */
public class InstrumentationManagerProxyFactory implements ExtensionFactory, ApplicationContextAware {

    /** The Constant INTERNAL_IMPL. */
    private static final String INTERNAL_IMPL =
            "org.eclipse.swordfish.core.management.instrumentation.InstrumentationManagerExternal";

    /** The instance. */
    private Object instance = null;

    /** The context. */
    private ApplicationContext context;

    /**
     * Destroy.
     */
    public void destroy() {
        if (null != this.instance) {
            ((InstrumentationManagerProxy) this.instance).destroy();
            this.instance = null;
        }
        this.context = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.extension.ExtensionFactory#getInstance(java.lang.String)
     */
    public Object getInstance(final String diversifier) {
        if (null == this.instance) {
            this.instance = this.createInstance();
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
     * Creates a new InstrumentationManagerProxy object.
     * 
     * @return the object
     */
    private Object createInstance() {
        InstrumentationManagerBean wrapped = (InstrumentationManagerBean) this.context.getBean(INTERNAL_IMPL);
        InstrumentationManagerProxy ret = new InstrumentationManagerProxy();
        ret.setWrapped(wrapped);
        return ret;
    }

}
