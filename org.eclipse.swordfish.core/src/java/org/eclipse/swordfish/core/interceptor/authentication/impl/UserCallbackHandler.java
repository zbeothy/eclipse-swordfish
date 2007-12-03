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
package org.eclipse.swordfish.core.interceptor.authentication.impl;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * The Class UserCallbackHandler.
 * 
 */
public class UserCallbackHandler implements CallbackHandler {

    /** the user prompt. */
    private static final String USER_PROMPT = "Username:";

    /** the pwd prompt. */
    private static final String PWD_PROMPT = "Password:";

    /** the new pwd prompt. */
    private static final String NEW_PWD_PROMPT = "New Password:";

    /** the userid and password. */
    private String userId;

    private String pwd = null;

    /**
     * The Constructor.
     * 
     * @param userId
     *        the userid
     * @param pwd
     *        the password
     */
    public UserCallbackHandler(final String userId, final String pwd) {
        this.userId = userId;
        this.pwd = pwd;
    }

    /**
     * Handle.
     * 
     * @param callbacks
     *        the jaas callbacks
     * 
     * @throws UnsupportedCallbackException
     *         exception
     */
    public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback cb = (NameCallback) callbacks[i];
                String prompt = cb.getPrompt();
                if (!(prompt.equals(USER_PROMPT)))
                    throw new UnsupportedCallbackException(callbacks[i], "Unexpected name prompt found: " + prompt);
                if (this.userId != null) {
                    cb.setName(this.userId);
                }
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback pb = (PasswordCallback) callbacks[i];
                String prompt = pb.getPrompt();
                if (prompt.equals(PWD_PROMPT)) {
                    if (this.pwd != null) {
                        pb.setPassword(this.pwd.toCharArray());
                    }
                } else if (prompt.equals(NEW_PWD_PROMPT)) {
                    System.out.print("");
                    // code to be added
                } else
                    throw new UnsupportedCallbackException(callbacks[i], "Unexpected password prompt found: " + prompt);
            } else
                throw new UnsupportedCallbackException(callbacks[i], "Unexpected call back found");
        }
    }
}
