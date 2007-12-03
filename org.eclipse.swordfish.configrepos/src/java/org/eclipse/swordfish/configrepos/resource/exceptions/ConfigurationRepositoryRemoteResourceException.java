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
package org.eclipse.swordfish.configrepos.resource.exceptions;

import org.eclipse.swordfish.configrepos.ConfigurationRepositoryRemoteException;

/**
 * The Class ConfigurationRepositoryRemoteResourceException.
 * 
 */
public class ConfigurationRepositoryRemoteResourceException extends ConfigurationRepositoryRemoteException {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = -6392834210002832752L;

    /**
     * Instantiates a new configuration repository remote resource exception.
     */
    public ConfigurationRepositoryRemoteResourceException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     */
    public ConfigurationRepositoryRemoteResourceException(final String message) {
        super(message);
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     * @param cause
     *        the cause
     */
    public ConfigurationRepositoryRemoteResourceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the cause
     */
    public ConfigurationRepositoryRemoteResourceException(final Throwable cause) {
        super(cause);
    }

}
