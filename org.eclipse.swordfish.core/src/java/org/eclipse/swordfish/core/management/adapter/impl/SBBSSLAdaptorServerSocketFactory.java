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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.tools.adaptor.ssl.SSLAdaptorServerSocketFactoryMBean;
import org.eclipse.swordfish.core.components.helpers.impl.DynamicSecurityProvider;

/**
 * Note this class is taken from mx4j implementation. Modified it for making it work under both Sun
 * and IBM java.
 * 
 * TODO: Fix this class to avoid hardcoding Sun's provider, since it will not work with IBM's JDK.
 * This MBean creates SSLServerSocket instances.
 * <p>
 * It can be configured to use a specific keystore and SSL protocol version to create
 * SSLServerSockets that will use the keystore information to encrypt data. <br>
 * <p/> A keystore can be created with this command:
 * 
 * <pre>
 * keytool -genkey -v -keystore store.key -storepass storepwd -keypass keypwd -dname &quot;CN=Simone Bordet, OU=Project Administrator, O=MX4J, L=Torino, S=TO, C=IT&quot; -validity 365
 * </pre>
 * 
 * or with this minimal command (that will prompt you for further information):
 * 
 * <pre>
 * keytool -genkey -keystore store.key
 * </pre>
 * 
 * <p/> A keystore may contains more than one entry, but only the first entry will be used for
 * encryption, no matter which is the alias for that entry. <p/> Following the first example of
 * generation of the keystore, this MBean must be instantiated and then setup by invoking the
 * following methods:
 * <ul>
 * <li> {@link #setKeyStoreName}("store.key");
 * <li> {@link #setKeyStorePassword}("storepwd");
 * <li> {@link #setKeyManagerPassword}("keypwd");
 * </ul>
 * before {@link #createServerSocket} is called.
 * 
 */
public class SBBSSLAdaptorServerSocketFactory implements SSLAdaptorServerSocketFactoryMBean {

    public static void addProvider(final Provider provider) {
        Security.addProvider(provider);
    }

    private String m_keyManagerAlgorithm = DynamicSecurityProvider.getKeyManagerAlgorithm();

    private String m_trustManagerAlgorithm = DynamicSecurityProvider.getTrustManagerAlgorithm();

    private String m_keyStoreType = "JKS";

    private String m_trustStoreType = "JKS";

    private String m_keyStoreName;

    private String m_trustStoreName;

    private String m_keyStorePassword;

    private String m_trustStorePassword;

    private String m_keyManagerPassword;

    private String m_sslProtocol = "TLS";

    /**
     * Returns a SSLServerSocket on the given port.
     */
    public ServerSocket createServerSocket(final int port, final int backlog, final String host) throws IOException {
        if (this.m_keyStoreName == null) throw new IOException("KeyStore file name cannot be null");
        if (this.m_keyStorePassword == null) throw new IOException("KeyStore password cannot be null");

        Logger logger = this.getLogger();
        if (logger.isEnabledFor(Logger.TRACE)) {
            logger.trace("Creating SSLServerSocket");
            logger.trace("\tKeyStore " + this.m_keyStoreName + ", type " + this.m_keyStoreType);
            logger.trace("\tKeyManager algorithm is " + this.m_keyManagerAlgorithm);
            logger.trace("\tTrustStore " + this.m_trustStoreName + ", type " + this.m_trustStoreType);
            logger.trace("\tTrustManager algorithm is " + this.m_trustManagerAlgorithm);
            logger.trace("\tSSL protocol version is " + this.m_sslProtocol);
        }

        try {
            KeyStore keystore = KeyStore.getInstance(this.m_keyStoreType);
            InputStream keyStoreStream = this.getClass().getClassLoader().getResourceAsStream(this.m_keyStoreName);
            // Must check for nullity, otherwise a new empty keystore is created by KeyStore.load
            if (keyStoreStream == null) {
                // Let's look at the file system, maybe that the name provided is in fact a file path
                File fle = new java.io.File(this.m_keyStoreName);
                if (fle.exists()) keyStoreStream = new FileInputStream(fle);
            }
            if (keyStoreStream == null) throw new IOException("Cannot find KeyStore " + this.m_keyStoreName);
            keystore.load(keyStoreStream, this.m_keyStorePassword.toCharArray());
            try {
                keyStoreStream.close();
            } catch (IOException x) {
            }

            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(this.m_keyManagerAlgorithm);
            // Use the keystore password as default if not given
            keyFactory.init(keystore, this.m_keyManagerPassword == null ? this.m_keyStorePassword.toCharArray()
                    : this.m_keyManagerPassword.toCharArray());

            TrustManagerFactory trustFactory = null;
            if (this.m_trustStoreName != null) {
                // User specified a trust store, retrieve it

                if (this.m_trustStorePassword == null) throw new IOException("TrustStore password cannot be null");

                KeyStore trustStore = KeyStore.getInstance(this.m_trustStoreType);
                InputStream trustStoreStream = this.getClass().getClassLoader().getResourceAsStream(this.m_trustStoreName);
                // Check for nullity
                if (trustStoreStream == null) throw new IOException("Cannot find TrustStore " + this.m_trustStoreName);
                trustStore.load(trustStoreStream, this.m_trustStorePassword.toCharArray());

                trustFactory = TrustManagerFactory.getInstance(this.m_trustManagerAlgorithm);
                trustFactory.init(trustStore);
            }

            SSLContext context = SSLContext.getInstance(this.m_sslProtocol);
            // Below call does not handle TrustManagers, needed when server must authenticate
            // clients.
            context.init(keyFactory.getKeyManagers(), trustFactory == null ? null : trustFactory.getTrustManagers(), null);

            SSLServerSocketFactory ssf = context.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(port, backlog, InetAddress.getByName(host));

            return serverSocket;
        } catch (IOException x) {
            logger.error("", x);
            throw x;
        } catch (UnrecoverableKeyException x) {
            // Wrong password for the key
            logger.error("Probably a bad key password", x);
            throw new IOException("Probably a bad key password: " + x.toString());
        } catch (Exception x) {
            logger.error("Unexpected exception", x);
            throw new IOException(x.toString());
        }
    }

    public void setKeyManagerAlgorithm(final String algorithm) {
        if ((algorithm == null) || (algorithm.trim().length() == 0))
            throw new IllegalArgumentException("Invalid KeyManager algorithm");
        this.m_keyManagerAlgorithm = algorithm;
    }

    public void setKeyManagerPassword(final String password) {
        if ((password == null) || (password.trim().length() == 0))
            throw new IllegalArgumentException("Invalid KeyManager password");
        this.m_keyManagerPassword = password;
    }

    public void setKeyStoreName(final String name) {
        if ((name == null) || (name.trim().length() == 0)) throw new IllegalArgumentException("Invalid KeyStore name");
        this.m_keyStoreName = name;
    }

    public void setKeyStorePassword(final String password) {
        if ((password == null) || (password.trim().length() == 0)) throw new IllegalArgumentException("Invalid KeyStore password");
        this.m_keyStorePassword = password;
    }

    public void setKeyStoreType(final String keyStoreType) {
        if ((keyStoreType == null) || (keyStoreType.trim().length() == 0))
            throw new IllegalArgumentException("Invalid KeyStore type");
        this.m_keyStoreType = keyStoreType;
    }

    public void setSSLProtocol(final String protocol) {
        if ((protocol == null) || (protocol.trim().length() == 0)) throw new IllegalArgumentException("Invalid SSL protocol");
        this.m_sslProtocol = protocol;
    }

    public void setTrustManagerAlgorithm(final String algorithm) {
        if ((algorithm == null) || (algorithm.trim().length() == 0))
            throw new IllegalArgumentException("Invalid TrustManager algorithm");
        this.m_trustManagerAlgorithm = algorithm;
    }

    public void setTrustStoreName(final String name) {
        if ((name == null) || (name.trim().length() == 0)) throw new IllegalArgumentException("Invalid TrustStore name");
        this.m_trustStoreName = name;
    }

    public void setTrustStorePassword(final String password) {
        if ((password == null) || (password.trim().length() == 0))
            throw new IllegalArgumentException("Invalid TrustStore password");
        this.m_trustStorePassword = password;
    }

    public void setTrustStoreType(final String trustStoreType) {
        if ((trustStoreType == null) || (trustStoreType.trim().length() == 0))
            throw new IllegalArgumentException("Invalid TrustStore type");
        this.m_trustStoreType = trustStoreType;
    }

    private Logger getLogger() {
        return Log.getLogger(this.getClass().getName());
    }
}
