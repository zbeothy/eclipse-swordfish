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

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;

/**
 * the super interface for the authetication wrappers. Each authentication handler comes in a
 * separate jar file with the implementation and its wrapper.
 * 
 */
public interface AuthenticationWrapper extends CallbackHandler {

    /**
     * indicates weather this handler is able to handle the handover callbacks.
     * 
     * @param callbacks
     *        the callbacks
     * 
     * @return true if this wrapper can handle the callback, false otherwise
     */
    boolean canHandle(Callback[] callbacks);

    /**
     * Sets the handler.
     * 
     * @param handler
     *        sets the handler for this particular wrapper
     */
    void setHandler(InternalAuthenticationHandler handler);

}
