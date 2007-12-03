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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.adapter.ManagementAdapter;
import org.eclipse.swordfish.core.management.messages.ManagementMessage;
import org.eclipse.swordfish.core.management.operations.Operations;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;

/**
 * The Class JMXConnectorAdapter.
 */
public class JMXConnectorAdapter implements ManagementAdapter {

    /** Using static registry to avoid multiple instantiation. */
    private static Registry registry;

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(JMXConnectorAdapter.class);

    /** The Constant uriRoot. */
    private static final String URI_ROOT = "service:jmx:rmi:///jndi/rmi://localhost:";

    /** RMI Registry port to use. */
    private int port;

    /** The active. */
    private boolean active = false;

    /** The local rmi. */
    private boolean localRmi = false;

    /** The jndi local path. */
    private String jndiLocalPath;

    /** The state. */
    private State state = State.STOPPED;

    /** The operations. */
    private Operations operations;

    /** The mbs. */
    private MBeanServer mbs;

    /** The server. */
    private JMXConnectorServer server;

    /**
     * Destroy.
     */
    public void destroy() {
        LOG.info("Trying to stop connector");
        if (null != this.server) {
            try {
                this.server.stop();
                LOG.info("Connector successfully stopped");
            } catch (IOException e) {
                LOG.error("While trying to stop JMXConnectorAdapter: " + e.toString());
                this.operations.notify(ManagementMessage.SERVER_SHUTDOWN_FAILED, "JMXConnectorAdapter", e.toString());
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getHost()
     */
    public String getHost() {
        String ret = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            ret = address.getCanonicalHostName();
        } catch (UnknownHostException e) {
            ret = "unknown";
        }
        return ret;
    }

    /**
     * Gets the jndi local path.
     * 
     * @return the jndi local path
     */
    public String getJndiLocalPath() {
        return this.jndiLocalPath;
    }

    /**
     * Gets the local rmi.
     * 
     * @return the local rmi
     */
    public Boolean getLocalRmi() {
        return new Boolean(this.localRmi);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getObjectName()
     */
    public String getObjectName() {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getPort()
     */
    public Integer getPort() {
        return new Integer(this.port);
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public State getState() {
        return this.state;
    }

    /**
     * Init.
     */
    public void init() {
        LOG.info("Starting init");
        if (!State.STOPPED.equals(this.state)) {
            LOG.warn("Trying to start component in state " + this.state.toString());
            return;
        }
        try {
            if ((this.active) && (this.localRmi) && (-1 != this.port)) {
                if (null == registry) {
                    registry = LocateRegistry.createRegistry(this.port);
                    LOG.info("Created local RMI registry at " + this.port);
                } else {
                    LOG.info("Re-using existing RMI registry");
                }
            }
            String urlString = URI_ROOT + this.port + this.jndiLocalPath;
            try {
                JMXServiceURL url = new JMXServiceURL(urlString);
                this.server = JMXConnectorServerFactory.newJMXConnectorServer(url, null, this.mbs);
                this.server.start();
                this.state = State.RUNNING;
                LOG.info("Init complete. InternalState: " + this.state.toString());
            } catch (IOException e) {
                LOG.error("Could not initialize JMXConnectorAdapter. Reason: " + e.toString());
                this.operations.notify(ManagementMessage.SERVER_CONNECTION_FAILED, "JMXConnectorAdapter", urlString, e.toString());
                this.state = State.FAILED;
            }

        } catch (RemoteException e) {
            // workaround due to a bug in JDK 1.4.2
            // (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4267864)
            // If an RMI registry already exists, trying to create a second
            // one on a different port
            // results in an exception
            // To work around this bug, we don't use the NamingService that
            // comes with MX4J but call
            // createRegistry instead and ignore the exception.
            LOG.warn("When trying to create rmi registry: ", e);
        }

    }

    /**
     * Checks if is active.
     * 
     * @return the boolean
     */
    public Boolean isActive() {
        return new Boolean(this.active);
    }

    /**
     * Sets the active.
     * 
     * @param newActive
     *        the new active
     */
    public void setActive(final Boolean newActive) {
        if (null != newActive) {
            this.active = newActive.booleanValue();
        } else {
            this.active = false;
        }
        if (!this.active) {
            this.destroy();
        }
    }

    /**
     * Sets the jndi local path.
     * 
     * @param jndiLocalPath
     *        the new jndi local path
     */
    public void setJndiLocalPath(final String jndiLocalPath) {
        this.jndiLocalPath = jndiLocalPath;
    }

    /**
     * Sets the local rmi.
     * 
     * @param arg0
     *        the new local rmi
     */
    public void setLocalRmi(final Boolean arg0) {
        this.localRmi = arg0.booleanValue();
    }

    /**
     * Sets the mbean server.
     * 
     * @param mbServer
     *        the new mbean server
     */
    public void setMbeanServer(final MBeanServer mbServer) {
        this.mbs = mbServer;
    }

    /**
     * Sets the operations.
     * 
     * @param operations
     *        the new operations
     */
    public void setOperations(final Operations operations) {
        this.operations = operations;
    }

    /**
     * Sets the port.
     * 
     * @param newPort
     *        the new port
     */
    public void setPort(final Integer newPort) {
        if (null != newPort) {
            this.port = newPort.intValue();
        } else {
            this.port = -1;
        }
    }

}
