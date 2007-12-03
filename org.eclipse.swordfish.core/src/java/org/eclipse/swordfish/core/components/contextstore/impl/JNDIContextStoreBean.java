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
package org.eclipse.swordfish.core.components.contextstore.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * The Class JNDIContextStoreBean.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class JNDIContextStoreBean extends AbstractContextStore {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(JNDIContextStoreBean.class);

    /** The transient cache. */
    private Map transientCache;

    /** The initial factory. */
    private String initialFactory;

    // private String baseDN;

    /** The provider URL. */
    private String providerURL;

    /** The user DN. */
    private String userDN;

    /** The credentials. */
    private String credentials;

    /** The base context. */
    private DirContext baseContext;

    /**
     * Instantiates a new JNDI context store bean.
     * 
     * @param ldapURL
     *        the ldap URL
     * @param userDN
     *        the user DN
     * @param credentials
     *        the credentials
     * @param initialFactory
     *        the initial factory
     */
    public JNDIContextStoreBean(final String ldapURL, final String userDN, final String credentials, final String initialFactory) {
        this.providerURL = ldapURL;
        this.userDN = userDN;
        this.credentials = credentials;
        this.initialFactory = initialFactory;
        this.transientCache = new HashMap();
    }

    /**
     * Destroy.
     */
    public synchronized void destroy() {
        if (null == this.baseContext) return;
        try {
            this.baseContext.close();
        } catch (NamingException e) {
            LOG.warn("cannot close the JNDIContext", e);
        }
        this.baseContext = null;
    }

    /**
     * Gets the credentials.
     * 
     * @return Returns the credentials.
     */
    public String getCredentials() {
        return this.credentials;
    }

    /**
     * Gets the initial factory.
     * 
     * @return Returns the initialFactory.
     */
    public String getInitialFactory() {
        return this.initialFactory;
    }

    /**
     * Gets the provider URL.
     * 
     * @return Returns the providerURL.
     */
    public String getProviderURL() {
        return this.providerURL;
    }

    /**
     * Gets the user DN.
     * 
     * @return Returns the userDN.
     */
    public String getUserDN() {
        return this.userDN;
    }

    /**
     * Init.
     */
    public void init() {
        this.baseContext = null;
    }

    /**
     * Removes the call context.
     * 
     * @param key
     *        the key
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.impl.AbstractContextStore#removeCallContext(java.lang.String)
     */
    @Override
    public void removeCallContext(final String key) {
        if (this.transientCache.containsKey(key)) {
            this.transientCache.remove(key);
        } else {
            final boolean mayReconnect = this.mayReconnect();
            try {
                this.getBaseContext().unbind("cn=" + key);
            } catch (CommunicationException e) {
                if (mayReconnect) {
                    this.prepareReconnect();
                    this.removeCallContext(key);
                    return;
                }
                LOG.error("cannot remove the callContext from JNDI ", e);
            } catch (NamingException e) {
                LOG.error("cannot remove the callContext from JNDI ", e);
            } catch (InternalInfrastructureException e) {
                LOG.error("cannot remove the callContext from JNDI ", e);
            }
        }
    }

    /**
     * Restore call context.
     * 
     * @param key
     *        the key
     * 
     * @return the call context extension
     * 
     * @throws ContextNotRestoreableException
     * @throws ContextNotFoundException
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.impl.AbstractContextStore#restoreCallContext(java.lang.String)
     */
    @Override
    public CallContextExtension restoreCallContext(final String key) throws InternalIllegalInputException,
            InternalInfrastructureException {
        CallContextExtension ctx = null;
        if (this.transientCache.containsKey(key)) {
            ctx = (CallContextExtension) this.transientCache.get(key);
            this.removeCallContext(key);
            return ctx;
        } else {
            final boolean mayReconnect = this.mayReconnect();
            try {
                byte[] ba = (byte[]) this.getBaseContext().lookup("cn=" + key);
                ctx = this.deserializeContext(ba);
                this.removeCallContext(key);
            } catch (CommunicationException e) {
                if (mayReconnect) {
                    this.prepareReconnect();
                    return this.restoreCallContext(key);
                }
                throw new InternalInfrastructureException("failed to look-up the key " + key + " in jndi", e);
            } catch (NameNotFoundException nfe) {
                // this Exception is mostly caused by a wrong key
                throw new InternalIllegalInputException("failed to look-up the key " + key + " in jndi", nfe);
            } catch (NamingException e) {
                throw new InternalIllegalInputException("failed to look-up the key " + key + " in jndi", e);
            } catch (IOException e) {
                throw new InternalInfrastructureException("could not restore the context for key " + key, e);
            }

            return ctx;
        }
    }

    /**
     * Store call context.
     * 
     * @param ctx
     *        the ctx
     * 
     * @return the string
     * 
     * @throws ContextNotStoreableException
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.impl.AbstractContextStore#storeCallContext(org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension)
     */
    @Override
    public String storeCallContext(final CallContextExtension ctx) throws InternalIllegalInputException,
            InternalInfrastructureException {
        final String key = this.buildKey(ctx);
        final boolean mayReconnect = this.mayReconnect();
        try {
            final byte[] data = this.serializeContext(ctx);
            this.getBaseContext().rebind("cn=" + key, data);
        } catch (IOException e) {
            throw new InternalInfrastructureException("failed to serialize the InternalCallContext", e);
        } catch (CommunicationException e) {
            if (mayReconnect) {
                this.prepareReconnect();
                return this.storeCallContext(ctx);
            }
            throw new InternalInfrastructureException("failed to put the context into jndi", e);
        } catch (NamingException e) {
            throw new InternalConfigurationException("failed to put the context into jndi", e);
        }

        return key;
    }

    /**
     * Gets the base context.
     * 
     * @return the base context
     * 
     * @throws ContextException
     */
    private synchronized DirContext getBaseContext() throws InternalInfrastructureException {
        if (this.baseContext == null) {

            final Hashtable env = new Hashtable(11);
            env.put(Context.INITIAL_CONTEXT_FACTORY, this.initialFactory);
            env.put(Context.PROVIDER_URL, this.providerURL);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");

            env.put(Context.SECURITY_PRINCIPAL, this.userDN);
            env.put(Context.SECURITY_CREDENTIALS, this.credentials);

            try {
                this.baseContext = new InitialDirContext(env);
            } catch (NamingException e) {
                final String message = "Cannot instantiate initial context using <" + this.providerURL + "> as a provider URL.";
                throw new InternalConfigurationException(message, e);
            }

            /*
             * try { baseContext = (DirContext) baseContext.lookup(baseDN); } catch (NamingException
             * e) { final String message = "Cannot lookup <" + baseDN + "> base DN."; throw new
             * ContextException(message, e); }
             */
        }
        return this.baseContext;
    }

    /**
     * The present method needs to be invoked before calls to <code>getBaseContext()</code> in
     * order to clarify if it makes sense to try a reconnect to the JNDI backend, i.e create a new
     * base context.
     * 
     * @return <code>true</code> if it may make sense to replace the current potentially timeouted
     *         base context
     */
    private boolean mayReconnect() {
        return (null != this.baseContext);
    }

    /**
     * Discard the old and probable timeouted base context in order to provoke a reconnect.
     */
    private synchronized void prepareReconnect() {
        if (null != this.baseContext) {
            try {
                this.baseContext.close();
            } catch (NamingException e) {
                e.printStackTrace();
                // ignore it - we know already that the present
                // base context may be timeouted
            }
            this.baseContext = null;
        }
    }
}
