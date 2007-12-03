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
package org.eclipse.swordfish.core.management.operations.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.adapter.ManagementAdapter;
import org.eclipse.swordfish.core.management.adapter.SnmpAdapter;

/**
 * Log handler to publish operational logging events via SNMP.
 * 
 */
public class SnmpPublisherWrapper extends Handler {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(SnmpPublisherWrapper.class);

    /* Attributes of wrapped object */

    /** The em host. */
    private String emHost;

    /** The em port. */
    private String emPort;

    /** The mip location. */
    private String mipLocation;

    /** The oid root. */
    private String oidRoot;

    /** The target host. */
    private String targetHost;

    /** The target port. */
    private int targetPort;

    /* own attributes */

    /** The activate. */
    private Boolean activate = new Boolean(false);

    /** The impl class name. */
    private String implClassName;

    /** The wrapped. */
    private SnmpAdapter wrapped;

    /** The adapter. */
    private ManagementAdapter adapter;

    /**
     * Instantiates a new snmp publisher wrapper.
     */
    public SnmpPublisherWrapper() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#close()
     */
    @Override
    public void close() throws SecurityException {
        if (null != this.wrapped) {
            this.wrapped.close();
        }
    }

    /**
     * Destroy.
     */
    public void destroy() {
        if (null != this.wrapped) {
            this.wrapped.flush();
            this.wrapped.close();
            this.wrapped = null;
        }
        this.adapter = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#flush()
     */
    @Override
    public void flush() {
        if (null != this.wrapped) {
            this.wrapped.flush();
        }
    }

    /**
     * Init.
     */
    public void init() {
        if (this.activate.booleanValue()) {
            try {
                Class implClass = Class.forName(this.implClassName);
                this.wrapped = (SnmpAdapter) implClass.newInstance();
                if (null != this.adapter) {
                    this.emHost = this.adapter.getHost();
                    this.emPort = String.valueOf(this.adapter.getPort());
                }
                this.wrapped.setEmHost(this.emHost);
                this.wrapped.setEmPort(this.emPort);
                this.wrapped.setTargetHost(this.targetHost);
                this.wrapped.setTargetPort(this.targetPort);
                this.wrapped.setMipLocation(this.mipLocation);
                this.wrapped.setOidRoot(this.oidRoot);
                this.wrapped.setSbbInstanceId(this.getManagementHost());
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
     * {@inheritDoc}
     * 
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    @Override
    public void publish(final LogRecord record) {
        if (null != this.wrapped) {
            this.wrapped.publish(record);
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
     * Sets the adapter.
     * 
     * @param anAdapter
     *        the new adapter
     */
    public void setAdapter(final ManagementAdapter anAdapter) {
        this.adapter = anAdapter;
    }

    /**
     * Sets the em host.
     * 
     * @param emHost
     *        the new em host
     */
    public void setEmHost(final String emHost) {
        this.emHost = emHost;
    }

    /**
     * Sets the em port.
     * 
     * @param emPort
     *        the new em port
     */
    public void setEmPort(final String emPort) {
        this.emPort = emPort;
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
     * Sets the mip location.
     * 
     * @param mipLocation
     *        the new mip location
     */
    public void setMipLocation(final String mipLocation) {
        this.mipLocation = mipLocation;
    }

    /**
     * Sets the oid root.
     * 
     * @param oidRoot
     *        the new oid root
     */
    public void setOidRoot(final String oidRoot) {
        this.oidRoot = oidRoot;
    }

    /**
     * Sets the target host.
     * 
     * @param targetHost
     *        the new target host
     */
    public void setTargetHost(final String targetHost) {
        this.targetHost = targetHost;
    }

    /**
     * Sets the target port.
     * 
     * @param targetPort
     *        the new target port
     */
    public void setTargetPort(final Integer targetPort) {
        this.targetPort = targetPort.intValue();
    }

    /**
     * Gets the management host.
     * 
     * @return servername/port for EmAdapter
     */
    private String getManagementHost() {
        String ret = null;
        if ((null != this.adapter) && (null != this.adapter.getHost())) {
            ret = this.adapter.getHost();
        } else {
            try {
                InetAddress address = InetAddress.getLocalHost();
                ret = address.getCanonicalHostName();
            } catch (UnknownHostException e) {
                ret = "unknown";
            }
        }
        if (null != this.adapter) {
            ret = ret + ":" + this.adapter.getPort();
        }
        return ret;
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
                new StringBuffer("Unexpected exception when initializing SNMP publisher - "
                        + "InternalSBB library instance will publish operational messages via SNMP.\n" + e.getMessage());
        for (int i = 0; i < stackTrace.length; i++) {
            msg.append("\n").append(stackTrace[i].toString());
        }
        LOG.error(new String(msg));
    }
}
