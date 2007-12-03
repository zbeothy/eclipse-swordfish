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
package org.eclipse.swordfish.core.components.helpers.impl;

import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.springframework.beans.factory.FactoryBean;

/**
 * The Class MBeanServerReuseFactoryBean.
 */
public class MBeanServerReuseFactoryBean implements FactoryBean {

    /** The component context access. */
    private ComponentContextAccess componentContextAccess;

    /**
     * Gets the component context access.
     * 
     * @return the component context access
     */
    public ComponentContextAccess getComponentContextAccess() {
        return this.componentContextAccess;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() {
        return this.componentContextAccess.getMBeanServer();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        return this.componentContextAccess.getMBeanServer().getClass();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    /**
     * Sets the component context access.
     * 
     * @param componentContextAccess
     *        the new component context access
     */
    public void setComponentContextAccess(final ComponentContextAccess componentContextAccess) {
        this.componentContextAccess = componentContextAccess;
    }

}
