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
package org.eclipse.swordfish.configrepos;

/**
 * The Class ConfigurationRepositoryRemoteException.
 * 
 */
public class ConfigurationRepositoryRemoteException extends Exception {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = -8553260845908714673L;

    /**
     * Instantiates a new configuration repository remote exception.
     */
    public ConfigurationRepositoryRemoteException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     */
    public ConfigurationRepositoryRemoteException(final String message) {
        super(message);
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     * @param cause
     *        the throwable
     */
    public ConfigurationRepositoryRemoteException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the throwable
     */
    public ConfigurationRepositoryRemoteException(final Throwable cause) {
        super(cause);
    }

}
