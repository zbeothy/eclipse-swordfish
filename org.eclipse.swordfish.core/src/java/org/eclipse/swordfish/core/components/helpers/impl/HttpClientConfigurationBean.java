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

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class HttpClientConfigurationBean.
 */
// TODO this configuration currently does nothing real.
// the behaviour depends on the used http client lib and needs adaption.
public class HttpClientConfigurationBean {

    // Constants
    /** The DEFAUL t_ CONNECTIO n_ TIMEOUT. */
    static private int defaultConnectionTimeout = 10;

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(HttpClientConfigurationBean.class);

    private String m_keyManagerAlgorithm = DynamicSecurityProvider.getKeyManagerAlgorithm();

    private String m_trustManagerAlgorithm = DynamicSecurityProvider.getTrustManagerAlgorithm();

    // Bean properties
    /** The connection timeout. */
    private int connectionTimeout = 0;

    /** The http proxy host. */
    private String httpProxyHost = null;

    /** The http proxy port. */
    private int httpProxyPort = 0;

    /** The http proxy username. */
    private String httpProxyUsername = null;

    /** The http proxy password. */
    private String httpProxyPassword = null;

    /** The http proxy enable. */
    private boolean httpProxyEnable = false;

    /** The ssl keystore. */
    private String sslKeystore = null;

    /** The ssl keystore password. */
    private String sslKeystorePassword = null;

    /** The ssl truststore. */
    private String sslTruststore = null;

    /** The ssl hostname verification. */
    private boolean sslHostnameVerification = false;

    /** The base dir. */
    private String baseDir = null;

    // Component injection points
    /** The component context access. */
    private ComponentContextAccess componentContextAccess = null;

    // State
    /** The configured. */
    private boolean configured = false;

    /**
     * Configure.
     */
    public synchronized void configure() {
        if (this.configured) return;
        // set HTTP client timeout
        if (this.connectionTimeout <= 0) {
            this.connectionTimeout = defaultConnectionTimeout;
        }
        // params.setIntParameter("http.connection.timeout", this.connectionTimeout * 1000);
        // params.setIntParameter("http.socket.timeout", this.connectionTimeout * 1000);
        LOG.config("HTTP client timeout set to " + this.connectionTimeout + " seconds.");

        // set HTTP proxy
        if (this.httpProxyEnable && (null != this.httpProxyHost) && (this.httpProxyPort > 0)) {

            // TODO configure a proxy with the used HTTP client library

            LOG.config("HTTP client proxy enabled, BUT NOT set to " + this.httpProxyHost + ":" + this.httpProxyPort);
        }

        // set SSL parameters
        SSLContext sslCtx = null;
        try {

            if (this.isSslHostnameVerification()) {
                // TODO en/disable SSL HostnameVerification
            }

            sslCtx = SSLContext.getInstance("TLS");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(this.m_keyManagerAlgorithm);
            KeyStore ks = KeyStore.getInstance("JKS");

            KeyManager[] keymgr = null;
            if (!"".equalsIgnoreCase(this.sslKeystore)) {
                File keyStoreFile = new File(this.sslKeystore);
                if (!keyStoreFile.exists()) {
                    String filePath = this.getAbsolutePath(this.sslKeystore);
                    if (filePath != null) {
                        keyStoreFile = new File(filePath);
                    }
                }

                if (keyStoreFile.exists()) {
                    ks.load(new FileInputStream(keyStoreFile), null);
                    LOG.config("Keystore set to " + keyStoreFile.getAbsolutePath());
                    kmf.init(ks, this.sslKeystorePassword.toCharArray());
                    LOG.config("KeystorePassword set");
                    keymgr = kmf.getKeyManagers();
                } else {
                    LOG
                        .warn("Configuration for ssl keystore file incorrect. keystore file not found, using system default KeyManager");
                }
            } else {
                LOG.info("sslKeystore is set to empty, system default KeyManager would be used.");
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(this.m_trustManagerAlgorithm);
            KeyStore ts = KeyStore.getInstance("JKS");
            TrustManager[] trustmgr = null;
            if (!"".equalsIgnoreCase(this.sslTruststore)) {
                File trustStoreFile = new File(this.sslTruststore);
                if (!trustStoreFile.exists()) {
                    String filePath = this.getAbsolutePath(this.sslTruststore);
                    if (filePath != null) {
                        trustStoreFile = new File(filePath);
                    }
                }
                if (trustStoreFile.exists()) {
                    ts.load(new FileInputStream(trustStoreFile), null);
                    LOG.config("Truststore set to " + trustStoreFile.getAbsolutePath());
                    tmf.init(ts);
                    trustmgr = tmf.getTrustManagers();
                } else {
                    LOG
                        .warn("Configuration for ssl truststore file incorrect. Truststore file not found, using system default TrustManager");
                }
            } else {
                LOG.info("sslTruststore is set to empty, system default TrustManager would be used.");
            }

            sslCtx.init(keymgr, trustmgr, null);
        } catch (Exception e) {
            LOG.warn("Error setting SSL parameters, please check section "
                    + "<HTTPClientConfiguration> in bootstrap configuration. " + "Continuing without SSL support.", e);
            this.configured = true;
            return;
        }
        SSLSocketFactory sslSocketFactory = sslCtx.getSocketFactory();

        // TODO set the default SSL Socket Factory for the used http client.

        LOG.debug("SSLSocketFactory set to " + sslSocketFactory);
        this.configured = true;
    }

    /**
     * Checks if is ssl hostname verification.
     * 
     * @return true, if is ssl hostname verification
     */
    public boolean isSslHostnameVerification() {
        return this.sslHostnameVerification;
    }

    /**
     * Sets the base dir.
     * 
     * @param baseDir
     *        the new base dir
     */
    public void setBaseDir(final String baseDir) {
        this.baseDir = baseDir;
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

    /**
     * Sets the connection timeout.
     * 
     * @param connectionTimeout
     *        the new connection timeout
     */
    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Sets the http proxy enable.
     * 
     * @param httpProxyEnable
     *        the new http proxy enable
     */
    public void setHttpProxyEnable(final boolean httpProxyEnable) {
        this.httpProxyEnable = httpProxyEnable;
    }

    /**
     * Sets the http proxy host.
     * 
     * @param httpProxyHost
     *        the new http proxy host
     */
    public void setHttpProxyHost(final String httpProxyHost) {
        this.httpProxyHost = httpProxyHost;
    }

    /**
     * Sets the http proxy password.
     * 
     * @param httpProxyPassword
     *        the new http proxy password
     */
    public void setHttpProxyPassword(final String httpProxyPassword) {
        this.httpProxyPassword = httpProxyPassword;
    }

    /**
     * Sets the http proxy port.
     * 
     * @param httpProxyPort
     *        the new http proxy port
     */
    public void setHttpProxyPort(final int httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
    }

    /**
     * Sets the http proxy username.
     * 
     * @param httpProxyUsername
     *        the new http proxy username
     */
    public void setHttpProxyUsername(final String httpProxyUsername) {
        this.httpProxyUsername = httpProxyUsername;
    }

    /**
     * Sets the ssl hostname verification.
     * 
     * @param sslHostnameVerification
     *        the new ssl hostname verification
     */
    public void setSslHostnameVerification(final boolean sslHostnameVerification) {
        this.sslHostnameVerification = sslHostnameVerification;
    }

    /**
     * Sets the ssl keystore.
     * 
     * @param sslKeystore
     *        the new ssl keystore
     */
    public void setSslKeystore(final String sslKeystore) {
        this.sslKeystore = sslKeystore;
    }

    /**
     * Sets the ssl keystore password.
     * 
     * @param sslKeystorePassword
     *        the new ssl keystore password
     */
    public void setSslKeystorePassword(final String sslKeystorePassword) {
        this.sslKeystorePassword = sslKeystorePassword;
    }

    /**
     * Sets the ssl truststore.
     * 
     * @param sslTruststore
     *        the new ssl truststore
     */
    public void setSslTruststore(final String sslTruststore) {
        this.sslTruststore = sslTruststore;
    }

    /**
     * Gets the absolute path.
     * 
     * @param filename
     *        the filename
     * 
     * @return the absolute path
     */
    private String getAbsolutePath(final String filename) {
        File file =
                new File(this.componentContextAccess.getInstallRoot() + File.separator + this.baseDir + File.separator + filename);
        return (file.exists() && file.canRead()) ? file.getAbsolutePath() : null;
    }

}
