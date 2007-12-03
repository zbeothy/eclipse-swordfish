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

import org.eclipse.swordfish.configrepos.ConfigurationRepositoryInternalException;

/**
 * The Class ConfigurationRepositoryResourceException.
 * 
 */
public class ConfigurationRepositoryResourceException extends ConfigurationRepositoryInternalException {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = 2254824012462683474L;

    /**
     * Create a new exception.
     */
    public ConfigurationRepositoryResourceException() {
        super();
    }

    /**
     * Create a new exception, based on a message.
     * 
     * @param message
     *        which should be set in the exception.
     */
    public ConfigurationRepositoryResourceException(final String message) {
        super(message);
    }

    /**
     * Create a new exception, based on a message and root cause exception.
     * 
     * @param message
     *        which should be set in the exception.
     * @param cause
     *        the root cause exception provided with this new exception.
     */
    public ConfigurationRepositoryResourceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new exception, based on a root cause exception.
     * 
     * @param cause
     *        the root cause exception provided with this new exception.
     */
    public ConfigurationRepositoryResourceException(final Throwable cause) {
        super(cause);
    }

}
