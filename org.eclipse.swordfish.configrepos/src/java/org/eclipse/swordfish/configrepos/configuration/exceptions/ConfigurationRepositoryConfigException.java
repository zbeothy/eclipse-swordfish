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

import org.eclipse.swordfish.configrepos.ConfigurationRepositoryInternalException;

/**
 * Root type for all exceptions that are being thrown by the configuration related components of the
 * library.
 * 
 */
public class ConfigurationRepositoryConfigException extends ConfigurationRepositoryInternalException {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = -6250726263003280772L;

    /**
     * Initialize blank exception.
     */
    public ConfigurationRepositoryConfigException() {
        super();
    }

    /**
     * Initialize exception with a message and a root cause.
     * 
     * @param aMessage
     *        which shall be passed with this exception
     */
    public ConfigurationRepositoryConfigException(final String aMessage) {
        super(aMessage);
    }

    /**
     * Initialize exception with a message and a root cause.
     * 
     * @param aMessage
     *        which shall be passed with this exception
     * @param aException
     *        which this exception was based upon
     */
    public ConfigurationRepositoryConfigException(final String aMessage, final Throwable aException) {
        super(aMessage, aException);
    }

    /**
     * Initialize exception with a root cause.
     * 
     * @param aException
     *        which shall be passed with this exception
     */
    public ConfigurationRepositoryConfigException(final Throwable aException) {
        super(aException);
    }

}
