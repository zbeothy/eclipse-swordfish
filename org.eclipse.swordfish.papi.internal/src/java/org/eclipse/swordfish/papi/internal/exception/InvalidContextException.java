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
 * <code>InvalidContextException</code> reports an issue while dealing with a context (indicates
 * an internal issue).
 * 
 */
public class InvalidContextException extends EnvironmentRuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3776964140652321655L;

    /**
     * Constructor: Instantiates a new <code>InvalidContextException</code>.
     */
    public InvalidContextException() {
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public InvalidContextException(final String message) {
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
    public InvalidContextException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public InvalidContextException(final Throwable cause) {
        super(cause);
    }
}
