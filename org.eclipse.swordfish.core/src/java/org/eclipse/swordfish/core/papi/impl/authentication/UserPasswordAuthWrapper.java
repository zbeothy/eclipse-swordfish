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
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.authentication.InternalUserPasswordAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.authentication.InternalUsernamePasswordCredentials;

/**
 * a wrapper for the username, password authentication.
 * 
 */
public class UserPasswordAuthWrapper implements AuthenticationWrapper {

    /** the wrapped object. */
    private InternalUserPasswordAuthenticationHandler handler;

    /**
     * public constructor. This constructor must be available to create instances genericly
     */
    public UserPasswordAuthWrapper() {
        super();
    }

    /**
     * Can handle.
     * 
     * @param callbacks
     *        the callbacks
     * 
     * @return true, if can handle
     * 
     * @see org.eclipse.swordfish.core.papi.impl.participant.authentication.AuthenticationWrapper#
     *      canHandle(javax.security.auth.callback.Callback[])
     */
    public boolean canHandle(final Callback[] callbacks) {
        boolean ret = true;
        for (int i = 0; i < callbacks.length; i++) {
            // set the user name
            if ((callbacks[i] instanceof NameCallback) || (callbacks[i] instanceof PasswordCallback)) {
                ret = true & ret;
            } else {
                ret = false & ret;
            }
        }
        return ret;

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

        if (!this.canHandle(callbacks)) /*
         * we just return to allow generic callback to process the
         * next registered handler normally the Generic callbacks
         * checks this. But "Sischer ist Sischer" :-)
         */
        return;
        // this is a valid set of callbacks, so propmt back to the application
        InternalUsernamePasswordCredentials credentials = this.handler.getCredentials();
        for (int i = 0; i < callbacks.length; i++) {
            // set the user name
            if (callbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName(credentials.getUsername());
                // set the password
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callbacks[i];
                pc.setPassword(credentials.getPassword());
            } else
                // this code should never be invoked
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback, this was not expected to ever happen");
        }
        // reset the credential object to keep them as short as possible in the
        // memory
        credentials = null;

    }

    /**
     * Sets the handler.
     * 
     * @param pHandler
     *        the handler
     * 
     * @see org.eclipse.swordfish.auth.example.AuthenticationWrapper#
     *      setHandler(org.eclipse.swordfish.papi.participant.authentication.InternalAuthenticationHandler)
     */
    public void setHandler(final InternalAuthenticationHandler pHandler) {
        this.handler = (InternalUserPasswordAuthenticationHandler) pHandler;

    }
}
