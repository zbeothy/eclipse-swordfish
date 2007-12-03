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
package org.eclipse.swordfish.core.management.adapter.impl;

import javax.management.MBeanServer;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.adapter.ExtensionAdapter;

/**
 * The Class HPManagementAdapterWrapper.
 */
public class HPManagementAdapterWrapper implements ExtensionAdapter {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(HPManagementAdapterWrapper.class);

    /** The impl class name. */
    private String implClassName;

    /** The wrapped. */
    private ExtensionAdapter wrapped;

    /** The mbs. */
    private MBeanServer mbs;

    /** The activate. */
    private Boolean activate = new Boolean(false);

    /** The config location. */
    private String configLocation;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ExtensionAdapter#destroy()
     */
    public void destroy() {
        if (null != this.wrapped) {
            this.wrapped.destroy();
            this.wrapped = null;
        }
        this.mbs = null;
    }

    /**
     * Gets the activate.
     * 
     * @return the activate
     */
    public Boolean getActivate() {
        return this.activate;
    }

    /**
     * Gets the config location.
     * 
     * @return the config location
     */
    public String getConfigLocation() {
        return this.configLocation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getHost()
     */
    public String getHost() {
        return this.wrapped.getHost();
    }

    /**
     * Gets the impl class name.
     * 
     * @return the impl class name
     */
    public String getImplClassName() {
        return this.implClassName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getObjectName()
     */
    public String getObjectName() {
        return this.wrapped.getObjectName();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getPort()
     */
    public Integer getPort() {
        return this.wrapped.getPort();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ExtensionAdapter#init()
     */
    public void init() {
        if (this.activate.booleanValue()) {
            Class implClass;
            try {
                implClass = Class.forName(this.implClassName);
                this.wrapped = (ExtensionAdapter) implClass.newInstance();
                this.wrapped.setProperty("configLocation", this.configLocation);
                this.wrapped.setProperty("MBeanServer", this.mbs);
                this.wrapped.init();
            } catch (ClassNotFoundException e) {
                this.logException(e);
            } catch (InstantiationException e) {
                this.logException(e);
            } catch (IllegalAccessException e) {
                this.logException(e);
            }
        }
    }

    /**
     * Sets the activate.
     * 
     * @param activate
     *        the new activate
     */
    public void setActivate(final Boolean activate) {
        this.activate = activate;
    }

    /**
     * Sets the config location.
     * 
     * @param configLocation
     *        the new config location
     */
    public void setConfigLocation(final String configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Sets the impl class name.
     * 
     * @param implClassName
     *        the new impl class name
     */
    public void setImplClassName(final String implClassName) {
        this.implClassName = implClassName;
    }

    /**
     * Sets the m bean server.
     * 
     * @param mbServer
     *        the new m bean server
     */
    public void setMBeanServer(final MBeanServer mbServer) {
        this.mbs = mbServer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ExtensionAdapter#setProperty(java.lang.String,
     *      java.lang.Object)
     */
    public void setProperty(final String name, final Object value) {
        // no-op for wrapper class
    }

    /**
     * Log exception.
     * 
     * @param e
     *        the e
     */
    private void logException(final Exception e) {
        Object[] stackTrace = e.getStackTrace();
        StringBuffer msg =
                new StringBuffer("Unexpected exception when initializing HPManagementAdapter - "
                        + "InternalSBB library instance will not be observable from HP OpenView.\n" + e.getMessage());
        for (int i = 0; i < stackTrace.length; i++) {
            msg.append("\n").append(stackTrace[i].toString());
        }
        LOG.error(new String(msg));
    }

}
