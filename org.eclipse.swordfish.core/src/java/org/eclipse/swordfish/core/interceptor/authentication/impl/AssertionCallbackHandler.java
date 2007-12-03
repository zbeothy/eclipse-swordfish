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
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * The Class AssertionCallbackHandler.
 * 
 */
public class AssertionCallbackHandler implements CallbackHandler {

    /** the xml assertion prompt type. */
    private static final String ASSERTION_PROMPT = "XML-Assertion:";

    /** class level variable for assertions. */
    private String assertion = null;

    /**
     * The Constructor.
     * 
     * @param assertions
     *        the assertions
     */
    public AssertionCallbackHandler(final String assertions) {
        this.assertion = assertions;
    }

    /**
     * Handles the callback from the login module. This method does respond to TextinputCallback
     * only.
     * 
     * @param callbacks
     *        the callbacks given by the login module.
     * 
     * @throws UnsupportedCallbackException
     *         in case of unexpected callback parameters or unsupported prompts.
     */
    public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof TextInputCallback) {
                TextInputCallback tb = (TextInputCallback) callbacks[i];
                String prompt = tb.getPrompt();
                if (prompt.equals(ASSERTION_PROMPT)) {
                    tb.setText(this.assertion.trim());
                }
            } else
                throw new UnsupportedCallbackException(callbacks[i], "Unexpected call back found");
        }
    }
}
