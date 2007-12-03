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
 * Configuration exception internal to the configuration manager.
 * 
 */
public class ConfigurationRepositoryInternalException extends Exception {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = -6511943239815155073L;

    /**
     * Default constructor.
     */
    public ConfigurationRepositoryInternalException() {
        super();
    }

    /**
     * Constructor which includes a message.
     * 
     * @param message
     *        the message that should be included in the exception
     */
    public ConfigurationRepositoryInternalException(final String message) {
        super(message);
    }

    /**
     * Constructor which includes a message and an exception.
     * 
     * @param message
     *        the message
     * @param cause
     *        the throwable
     */
    public ConfigurationRepositoryInternalException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor incorporating an exception.
     * 
     * @param cause
     *        the throwable
     */
    public ConfigurationRepositoryInternalException(final Throwable cause) {
        super(cause);
    }

}
