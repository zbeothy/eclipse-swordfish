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
 * <code>EnvironmentRuntimeException</code> indicates a misuse or bad programming issue either
 * related to the <code>InternalEnvironment</code> object or related to objects that are retrieved
 * using the <code>InternalEnvironment</code> object.
 * 
 */
public class EnvironmentRuntimeException extends SBBRuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1960258662584530919L;

    /**
     * Constructor: Instantiates a new <code>EnvironmentRuntimeException</code>.
     */
    public EnvironmentRuntimeException() {
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public EnvironmentRuntimeException(final String message) {
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
    public EnvironmentRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public EnvironmentRuntimeException(final Throwable cause) {
        super(cause);
    }

}
