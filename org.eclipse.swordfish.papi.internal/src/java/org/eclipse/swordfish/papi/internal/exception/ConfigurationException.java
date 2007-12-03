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
package org.eclipse.swordfish.papi.internal.exception;

/**
 * <code>InternalConfigurationException</code> is thrown if there are issues with the
 * <code>InternalEnvironment</code> of the current InternalSBB, for example, if it is
 * misconfigured.
 * 
 */
public class ConfigurationException extends EnvironmentRuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7721034610743340891L;

    /**
     * Constructor: Instantiates a new <code>InternalConfigurationException</code>.
     */
    public ConfigurationException() {
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public ConfigurationException(final String message) {
        super(message);
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public ConfigurationException(final Throwable cause) {
        super(cause);
    }
}
