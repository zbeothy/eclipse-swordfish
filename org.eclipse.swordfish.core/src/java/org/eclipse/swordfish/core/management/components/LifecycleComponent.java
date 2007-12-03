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
package org.eclipse.swordfish.core.management.components;

import java.util.List;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Some beans need other beans still working when they shut down. Since it does not seem to be
 * possible to control this behaviour via Spring, this class can be used to declare shutdown
 * dependents. The <code>ManagementController</code> is responsible for the actual shutdown.
 * 
 * Note: this mechanism is currently unused
 * 
 */
public abstract class LifecycleComponent implements BeanFactoryAware {

    /** The Constant CONTROLLER_ID. */
    private final static String CONTROLLER_ID = "org.eclipse.swordfish.core.management.components.ManagementController";

    /** The controller. */
    private ManagementController controller;

    /** The bean factory. */
    private BeanFactory beanFactory;

    /** The dependents. */
    private List dependents;

    /**
     * Destroy.
     */
    public abstract void destroy();

    /**
     * Implementors can overwrite this method to return a list with <code>LifecycleComponents</code>
     * that are instantiated before this component and need to be shut down before it is shut down.
     * 
     * @return the dependents
     */
    public List getDependents() {
        return this.dependents;
    }

    /**
     * This method <em>must</em> be called by implementors that overwrite it.
     */
    public void init() {
        this.controller.addLifecycleComponent(this);
    }

    /**
     * This method <em>must</em> be called by implementors that overwrite it.
     * 
     * @param arg0
     *        the arg0
     * 
     * @throws BeansException
     */
    public void setBeanFactory(final BeanFactory arg0) throws BeansException {
        this.beanFactory = arg0;
        this.controller = (ManagementController) this.beanFactory.getBean(CONTROLLER_ID);
    }

    /**
     * Sets the dependents.
     * 
     * @param dependents
     *        the new dependents
     */
    public void setDependents(final List dependents) {
        this.dependents = dependents;
    }

}
