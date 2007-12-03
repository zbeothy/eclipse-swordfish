/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.core.management.adapter.impl;

import java.io.InputStream;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.adapter.ManagementAdapter;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * Management Adapter to support connections from the Element Manager
 * 
 * 
 * !TODO: add operational logging.
 * 
 */
public class EmAdapter implements ManagementAdapter {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(EmAdapter.class);

    /** The Constant INSTRUMENTATION_DESCRIPTION. */
    private final static String INSTRUMENTATION_DESCRIPTION = "EmAdapterDesc.xml";

    /** The Constant INSTRUMENTATION_ID. */
    private final static String INSTRUMENTATION_ID = "org.eclipse.swordfish.core.sbb.EmAdapter";

    /** The activate. */
    private boolean activate = true;

    /** The object name root. */
    private String objectNameRoot = "sbb/adapter:name=mx4jadaptor";

    /** The port. */
    private Integer port;

    /** The host. */
    private String host;

    /** The processor. */
    private String processor;

    /** The mbs. */
    private MBeanServer mbs;

    /** The adaptor on. */
    private ObjectName adaptorOn;

    /** The username. */
    private String username;

    /** The password. */
    private String password;

    /** The instrumentation manager. */
    private InstrumentationManagerBean instrumentationManager;

    /** The instrumentation. */
    private org.eclipse.swordfish.core.management.adapter.impl.EmAdapter.Instrumentation instrumentation;

    /**
     * Destroy.
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        if (!this.activate) return;
        // TODO STOP AND FREE MANAGEMENT ADAPTOR
        this.unregisterInstrumentation();
        this.instrumentationManager = null;
        this.mbs = null;
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * Gets the activate.
     * 
     * @return the activate
     */
    public Boolean getActivate() {
        return new Boolean(this.activate);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getHost()
     */
    public String getHost() {
        return this.host;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getObjectName()
     */
    public String getObjectName() {
        return this.adaptorOn.toString();
    }

    /**
     * Gets the object name root.
     * 
     * @return the object name root
     */
    public String getObjectNameRoot() {
        return this.objectNameRoot;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.adapter.ManagementAdapter#getPort()
     */
    public Integer getPort() {
        return this.port;
    }

    /**
     * Init.
     */
    public void init() {
        if (!this.activate) {
            LOG.info("EmAdapter not activated as per configuration. This instance will not be manageable through ElementManager");
            return;
        }
        if ((null == this.username) || (null == this.password)) {
            String msg =
                    "Username and/or password not set for management connection.\n"
                            + "Management adapter not initialized, Element Manager will be unable to connect to this InternalSBB instance";
            LOG.error(msg);
        }

        // TODO ADD AND START A MANAGEMENT ADAPTOR
    }

    /**
     * Sets the activate.
     * 
     * @param activate
     *        the new activate
     */
    public void setActivate(final Boolean activate) {
        this.activate = activate.booleanValue();
    }

    /**
     * Sets the host.
     * 
     * @param host
     *        the new host
     */
    public void setHost(final String host) {
        if (!"$DEFAULT".equals(host)) {
            this.host = host;
        } else {
            this.host = null;
        }
    }

    /**
     * Sets the instrumentation manager.
     * 
     * @param mgr
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManagerBean mgr) {
        this.unregisterInstrumentation();
        this.instrumentationManager = mgr;
        this.instrumentation = new Instrumentation(this);
        InputStream is = this.getClass().getResourceAsStream(INSTRUMENTATION_DESCRIPTION);
        try {
            mgr.registerInstrumentation(this.instrumentation, is, INSTRUMENTATION_ID);
        } catch (Exception e) {
            LOG.error("Could not register EmAdapter - operator access via Element Manager will not be possible. Reason:\n" + e);
        }
    }

    /**
     * Sets the mbean server.
     * 
     * @param server
     *        the new mbean server
     */
    public void setMbeanServer(final MBeanServer server) {
        this.mbs = server;
    }

    /**
     * Sets the object name root.
     * 
     * @param objectNameRoot
     *        the new object name root
     */
    public void setObjectNameRoot(final String objectNameRoot) {
        this.objectNameRoot = objectNameRoot;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *        the new password
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Sets the port.
     * 
     * @param port
     *        the new port
     */
    public void setPort(final Integer port) {
        this.port = port;
    }

    /**
     * Sets the processor.
     * 
     * @param processor
     *        the new processor
     */
    public void setProcessor(final String processor) {
        this.processor = processor;
    }

    /**
     * Sets the username.
     * 
     * @param username
     *        the new username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * initialize the keystore for ssl connection authentication.
     */
    private void initKeyStore() {
        // create MX4J SSLServerSocketFactory
        SBBSSLAdaptorServerSocketFactory sslFactory = new SBBSSLAdaptorServerSocketFactory();
        sslFactory.setKeyStoreName("org/eclipse/swordfish/core/management/adapter/impl/em.jks");
        sslFactory.setKeyStorePassword("anfang");
        sslFactory.setKeyManagerPassword("anfang");
        sslFactory.setTrustStoreName("org/eclipse/swordfish/core/management/adapter/impl/em.jks");
        sslFactory.setTrustStorePassword("anfang");
        // sslFactory.setSSLProtocol("TLS");
        // sslFactory.setClientAuthentication(true);
        // TODO CONFIGURE SSL ON MANAGAMENT ADAPTOR
        // this.adaptor.setSocketFactory(sslFactory);
    }

    /**
     * Unregister instrumentation.
     */
    private void unregisterInstrumentation() {
        if ((null != this.instrumentation)) {
            if (null != this.instrumentationManager) {
                try {
                    this.instrumentationManager.unregisterInstrumentation(this.instrumentation);
                } catch (InternalInfrastructureException e) {
                    LOG.error("Could not unregister EmAdapter - component might still be visible via Element Manager,"
                            + " but is in undefined state. Reason:\n" + e);
                }
            }
        }
    }

    /**
     * The Class Instrumentation.
     */
    public class Instrumentation {

        /** The owner. */
        private EmAdapter owner;

        /**
         * Instantiates a new instrumentation.
         * 
         * @param owner
         *        the owner
         */
        public Instrumentation(final EmAdapter owner) {
            this.owner = owner;
        }

        /**
         * Gets the active.
         * 
         * @return the active
         */
        public Boolean getActive() {
            Boolean ret = Boolean.FALSE;
            // TODO CHECK OWNERS ADAPTOR
            // if (null != this.owner.adaptor) {
            // ret = new Boolean(this.owner.adaptor.isActive());
            // }
            return ret;
        }

        /**
         * Gets the host.
         * 
         * @return the host
         */
        public String getHost() {
            StringBuffer ret = new StringBuffer(this.owner.getHost()).append(":").append(this.owner.getPort());
            return ret.toString();
        }

    }

}
