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
 * <code>InfrastructureRuntimeException</code> indicates an issue in the local InternalSBB
 * infrastructure, caused because of internal errors (internal issue).
 * 
 */
public class InfrastructureRuntimeException extends SBBRuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8082548871187762571L;

    /**
     * Constructor: Instantiates a new <code>InfrastructureRuntimeException</code>.
     */
    public InfrastructureRuntimeException() {
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public InfrastructureRuntimeException(final String message) {
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
    public InfrastructureRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public InfrastructureRuntimeException(final Throwable cause) {
        super(cause);
    }

}
