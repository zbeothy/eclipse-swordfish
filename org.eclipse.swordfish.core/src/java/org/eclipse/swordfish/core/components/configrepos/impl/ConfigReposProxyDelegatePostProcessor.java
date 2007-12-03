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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * The Class ConfigReposProxyDelegatePostProcessor.
 * 
 */
public class ConfigReposProxyDelegatePostProcessor implements BeanPostProcessor {

    /** The wrapper which will surround the target. */
    private String wrapper = null;

    /** The type which should actually be wrapped. */
    private Class targetClass = null;

    /**
     * Instantiates a new config repos proxy delegate post processor.
     */
    public ConfigReposProxyDelegatePostProcessor() {
        super();
    }

    /**
     * Gets the target class.
     * 
     * @return Returns the targetBean.
     */
    public String getTargetClass() {
        return this.targetClass.getName();
    }

    /**
     * Gets the wrapper.
     * 
     * @return Returns the wrapper.
     */
    public String getWrapper() {
        return this.wrapper;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aBean
     *        the a bean
     * @param aName
     *        the a name
     * 
     * @return the object
     * 
     * @throws BeansException
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
     *      java.lang.String)
     */
    public Object postProcessAfterInitialization(final Object aBean, final String aName) throws BeansException {

        Object anotherBean = aBean;
        if (this.targetClass.isAssignableFrom(anotherBean.getClass())) {
            try {
                Class wrapperClass = Thread.currentThread().getContextClassLoader().loadClass(this.wrapper);
                Constructor builder = wrapperClass.getConstructor(new Class[] {Object.class, Class.class});
                anotherBean = builder.newInstance(new Object[] {anotherBean, this.targetClass});
            } catch (ClassNotFoundException cnfe) {
                // FIXME Insert exception type into new exception, to make it
                // actually visible
                throw new IllegalArgumentException("Appropriate InternalSBB PAPI extension 'configrepos' is missing. ("
                        + cnfe.getMessage() + ")");
            } catch (NoSuchMethodException nsme) {
                throw new IllegalArgumentException("No appropriate constructor for wrapping configuration manager bean. ("
                        + nsme.getMessage() + ")");
            } catch (IllegalAccessException iae) {
                throw new IllegalArgumentException("Permission was denied to create a wrapper object. (" + iae.getMessage() + ")");
            } catch (InstantiationException ie) {
                throw new IllegalArgumentException("Error instantiating configrepos-proxy wrapper. (" + ie.getMessage() + ")");
            } catch (InvocationTargetException ite) {
                throw new IllegalArgumentException("Error while invoking configrepos-proxy wrapper. ("
                        + ite.getCause().getMessage() + ")");
            }
        }
        return anotherBean;
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
     * Sets the target class.
     * 
     * @param aTargetClass
     *        The targetBean to set.
     */
    public void setTargetClass(final String aTargetClass) {
        try {
            this.targetClass = Thread.currentThread().getContextClassLoader().loadClass(aTargetClass);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Unable to load class definition. (" + cnfe + ")");
        }
    }

    /**
     * Sets the wrapper.
     * 
     * @param wrapper
     *        The wrapper to set.
     */
    public void setWrapper(final String wrapper) {
        this.wrapper = wrapper;
    }
}
