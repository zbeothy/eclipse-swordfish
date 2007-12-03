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

import org.eclipse.swordfish.core.components.contextstore.ContextStore;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * A factory for creating ContextStore objects.
 */
public class ContextStoreFactory implements ContextStore {

    /** The store impl. */
    private ContextStore storeImpl = null;

    /** The provider URL. */
    private String providerURL;

    /** The user DN. */
    private String userDN;

    /** The credentials. */
    private String credentials;

    /** The store type. */
    private String storeType;

    /** The initial factory. */
    private String initialFactory;

    /**
     * Instantiates a new context store factory.
     */
    public ContextStoreFactory() {

    }

    /**
     * Destroy.
     */
    public void destroy() {
        this.storeImpl.destroy();
    }

    /**
     * Gets the credentials.
     * 
     * @return the credentials
     */
    public String getCredentials() {
        return this.credentials;
    }

    /**
     * Gets the initial factory.
     * 
     * @return the initial factory
     */
    public String getInitialFactory() {
        return this.initialFactory;
    }

    /**
     * Gets the provider URL.
     * 
     * @return the provider URL
     */
    public String getProviderURL() {
        return this.providerURL;
    }

    /**
     * Gets the store impl.
     * 
     * @return the store impl
     */
    public ContextStore getStoreImpl() {
        return this.storeImpl;
    }

    /**
     * Gets the store type.
     * 
     * @return the store type
     */
    public String getStoreType() {
        return this.storeType;
    }

    /**
     * Gets the user DN.
     * 
     * @return the user DN
     */
    public String getUserDN() {
        return this.userDN;
    }

    /**
     * Init.
     * 
     * @throws ContextException
     */
    public void init() throws InternalInfrastructureException {
        if (this.storeType.equalsIgnoreCase("ldap")) {
            this.storeImpl = new JNDIContextStoreBean(this.providerURL, this.userDN, this.credentials, this.initialFactory);
        } else if (this.storeType.equalsIgnoreCase("memory")) {
            this.storeImpl = new InMemoryContextStoreBean();
        } else
            throw new InternalInfrastructureException("Cannot create " + this.storeType + " type of ContextStore, type unknown.");
        this.storeImpl.init();
    }

    /**
     * Removes the call context.
     * 
     * @param key
     *        the key
     */
    public void removeCallContext(final String key) {
        this.storeImpl.removeCallContext(key);
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
     */
    public CallContextExtension restoreCallContext(final String key) throws InternalIllegalInputException,
            InternalInfrastructureException {
        return this.storeImpl.restoreCallContext(key);
    }

    /**
     * Sets the credentials.
     * 
     * @param credentials
     *        the new credentials
     */
    public void setCredentials(final String credentials) {
        this.credentials = credentials;
    }

    /**
     * Sets the initial factory.
     * 
     * @param initialFactory
     *        the new initial factory
     */
    public void setInitialFactory(final String initialFactory) {
        this.initialFactory = initialFactory;
    }

    /**
     * Sets the provider URL.
     * 
     * @param providerURL
     *        the new provider URL
     */
    public void setProviderURL(final String providerURL) {
        this.providerURL = providerURL;
    }

    /**
     * Sets the store impl.
     * 
     * @param storeImpl
     *        the new store impl
     */
    public void setStoreImpl(final ContextStore storeImpl) {
        this.storeImpl = storeImpl;
    }

    /**
     * Sets the store type.
     * 
     * @param storeType
     *        the new store type
     */
    public void setStoreType(final String storeType) {
        this.storeType = storeType;
    }

    /**
     * Sets the user DN.
     * 
     * @param userDN
     *        the new user DN
     */
    public void setUserDN(final String userDN) {
        this.userDN = userDN;
    }

    /**
     * Store call context.
     * 
     * @param callContext
     *        the call context
     * 
     * @return the string
     * 
     * @throws ContextNotStoreableException
     */
    public String storeCallContext(final CallContextExtension callContext) throws InternalIllegalInputException,
            InternalInfrastructureException {
        return this.storeImpl.storeCallContext(callContext);
    }

}
