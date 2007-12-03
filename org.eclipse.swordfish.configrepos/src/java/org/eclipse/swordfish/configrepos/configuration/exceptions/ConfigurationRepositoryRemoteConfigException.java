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
package org.eclipse.swordfish.configrepos.configuration.exceptions;

import org.eclipse.swordfish.configrepos.ConfigurationRepositoryRemoteException;

/**
 * Exception for configuration source errors.
 * 
 */
public class ConfigurationRepositoryRemoteConfigException extends ConfigurationRepositoryRemoteException {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = 205375239777044023L;

    /**
     * Constructor.
     */
    public ConfigurationRepositoryRemoteConfigException() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param message
     *        for the error
     */
    public ConfigurationRepositoryRemoteConfigException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message
     *        for the error
     * @param cause
     *        nested exception
     */
    public ConfigurationRepositoryRemoteConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     *        nested exception
     */
    public ConfigurationRepositoryRemoteConfigException(final Throwable cause) {
        super(cause);
    }

}
