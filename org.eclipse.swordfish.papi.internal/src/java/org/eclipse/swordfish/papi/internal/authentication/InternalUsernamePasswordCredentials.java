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
 * This interface provides access to the <code>username</code> and <code>password</code>
 * credentials required for authentication of a participant that uses username/password
 * authentication.
 * 
 * 
 */
public interface InternalUsernamePasswordCredentials {

    /**
     * This method returns the password of the user within
     * <code>InternalUsernamePasswordCredentials</code>.
     * 
     * @return The password of the user.
     */
    char[] getPassword();

    /**
     * This method returns the name of the user within
     * <code>InternalUsernamePasswordCredentials</code>.
     * 
     * @return The name of the user.
     */
    String getUsername();

}
