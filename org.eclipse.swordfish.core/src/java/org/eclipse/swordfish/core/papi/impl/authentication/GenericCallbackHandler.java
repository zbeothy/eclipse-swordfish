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
package org.eclipse.swordfish.core.papi.impl.authentication;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;

/**
 * This class is a generic CallbackHandler handed over to the JAAS login module. It acts as a
 * container to register the callbacks. Wrapper classes associated with each user defined
 * authentication class must be referenced in the META-INF/services directory.
 * 
 */
public class GenericCallbackHandler implements CallbackHandler {

    /** resource place to load <code>RES_PREFIX</code>. */
    private static final String RES_PREFIX = "META-INF/services/";

    /** list of handlers that are invokable. */
    private InternalAuthenticationHandler[] handlers;

    /**
     * creates a GenericCallbackHandler for a set of user defined handlers.
     * 
     * @param handlerArray
     *        the set of handlers to construct this object with
     */
    public GenericCallbackHandler(final InternalAuthenticationHandler[] handlerArray) {
        this.handlers = handlerArray;
    }

    /**
     * Gets the handler id.
     * 
     * @return the handler id
     */
    public String getHandlerId() {
        if (this.handlers.length > 0)
            return this.handlers[0].toString();
        else
            return null;
    }

    /**
     * Handle.
     * 
     * @param callbacks
     *        the callbacks
     * 
     * @throws IOException
     * @throws UnsupportedCallbackException
     * 
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < this.handlers.length; i++) {
            InternalAuthenticationHandler handler = this.handlers[i];
            AuthenticationWrapper wrapper = this.getWrapperForHandler(handler);
            if (wrapper.canHandle(callbacks)) {
                wrapper.handle(callbacks);
            }
        }
    }

    /**
     * find all implemented Interfaces which are derived from authentication handler. PA Code :-)
     * 
     * @param clazz
     *        the class to investigate
     * @param classes
     *        the set of interfaces that are authentication interfaces
     */
    private void findImplementedAuthIFs(final Class clazz, final Set classes) {
        Class referenceIF = InternalAuthenticationHandler.class;

        if (clazz == null) // if called with superclass of Object or an interface
            return;
        if (!referenceIF.isAssignableFrom(clazz)) // clazz doesn't has refernceIF as super type
            return;
        if (clazz.isAssignableFrom(referenceIF)) // clazz and refIF are assignable in both
            // directions
            // so clazz must be equal to refIF
            return;
        if (clazz.isInterface()) {
            // found an interface, remember it
            classes.add(clazz);
        }
        // look for all implemented interfaces
        Class[] ifs = clazz.getInterfaces();
        if (ifs != null) {
            for (int i = 0; i < ifs.length; i++) {
                this.findImplementedAuthIFs(ifs[i], classes);
            }
        }
        // and look for superclass
        this.findImplementedAuthIFs(clazz.getSuperclass(), classes);
    }

    /**
     * Gets the auth IF name.
     * 
     * @param handler
     *        the handler to investigate
     * 
     * @return the name of the authentication handler implemented by this method
     */
    private String getAuthIFName(final InternalAuthenticationHandler handler) {
        Set classes = new HashSet();
        this.findImplementedAuthIFs(handler.getClass(), classes);
        if (classes.size() != 1) throw new IllegalArgumentException("invalid handler class : " + handler.getClass());
        String authIFName = null;
        try {
            authIFName = ((Class) classes.iterator().next()).getName();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("no authentication realted interface implemented by this class: "
                    + handler.getClass().getName(), e);
        }
        return authIFName;
    }

    /**
     * creates a wrappers for a given handler.
     * 
     * @param handler
     *        the handler to create a wrapper for
     * 
     * @return the wrapper for ths handler
     */
    private AuthenticationWrapper getWrapperForHandler(final InternalAuthenticationHandler handler) {
        String resName = RES_PREFIX + AuthenticationWrapper.class.getName() + "$" + this.getAuthIFName(handler);
        Properties props = new Properties();
        try {
            ClassLoader loader = this.getClass().getClassLoader();
            InputStream ios = loader.getResourceAsStream(resName);
            if (ios == null) throw new RuntimeException("ressource " + resName + " does not exist");
            props.load(ios);
        } catch (IOException e) {
            throw new RuntimeException("ressource " + resName + "could not be loaded", e);
        }
        String className = props.getProperty("wrapper.class");
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ressource " + resName + "could not be found", e);
        }
        AuthenticationWrapper wrapper = null;
        try {
            wrapper = (AuthenticationWrapper) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("ressource " + resName + "could not be instantiated", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("access violation to " + resName, e);
        }
        wrapper.setHandler(handler);
        return wrapper;
    }
}
