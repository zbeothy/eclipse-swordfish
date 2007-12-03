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
package org.eclipse.swordfish.core.engine;

import javax.jbi.JBIException;
import javax.jbi.component.InstallationContext;
import javax.management.ObjectName;

/**
 * this class is reposible to perform installation related tasks for the InternalSBB SE. The current
 * version of InternalSBB does not have any component related installation tasks to be performed.
 * This class is provided to be conform with the JBI spec.
 */
public class BootstrapImpl implements javax.jbi.component.Bootstrap {

    /**
     * the constructor called by the JBI container to instantiate the installation of this
     * component.
     */
    public BootstrapImpl() {
        super();
    }

    /**
     * Clean up.
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.DefaultBootstrap#cleanUp()
     */
    public void cleanUp() throws JBIException {
    }

    /**
     * Gets the extension M bean name.
     * 
     * @return the extension M bean name
     * 
     * @see javax.jbi.component.DefaultBootstrap#getExtensionMBeanName()
     */
    public ObjectName getExtensionMBeanName() {
        return null;
    }

    /**
     * Init.
     * 
     * @param ctx
     *        the ctx
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.DefaultBootstrap#init(javax.jbi.component.InstallationContext)
     */
    public void init(final InstallationContext ctx) throws JBIException {
    }

    /**
     * On install.
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.DefaultBootstrap#onInstall()
     */
    public void onInstall() throws JBIException {
    }

    /**
     * On uninstall.
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.DefaultBootstrap#onUninstall()
     */
    public void onUninstall() throws JBIException {
    }

}
