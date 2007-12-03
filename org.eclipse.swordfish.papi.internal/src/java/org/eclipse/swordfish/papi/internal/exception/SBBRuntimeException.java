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
 * Root class for all RuntimeExceptions thrown by InternalSBB.<br/>
 * 
 */
public class SBBRuntimeException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5526565972499106771L;

    /**
     * Constructor: Instantiates a new <code>SBBRuntimeException</code>.
     */
    public SBBRuntimeException() {
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public SBBRuntimeException(final String message) {
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
    public SBBRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public SBBRuntimeException(final Throwable cause) {
        super(cause);
    }
}
