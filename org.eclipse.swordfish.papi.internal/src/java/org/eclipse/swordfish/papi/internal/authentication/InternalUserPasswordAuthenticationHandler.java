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
package org.eclipse.swordfish.papi.internal.authentication;

/**
 * This interface implements an authentication handler based on the <code>username</code> and
 * <code>password</code> of the user.
 * 
 * Participant applications register implementations of this interface with the InternalSBB using
 * the
 * {@link org.eclipse.swordfish.papi.InternalSBB#addAuthenticationHandler(InternalAuthenticationHandler)}
 * method.
 * 
 */
public interface InternalUserPasswordAuthenticationHandler extends InternalAuthenticationHandler {

    /**
     * This methods returns the <code>InternalUsernamePasswordCredentials</code> that identify the
     * user.
     * 
     * @return The credentials that identify the user.
     */
    InternalUsernamePasswordCredentials getCredentials();

}
