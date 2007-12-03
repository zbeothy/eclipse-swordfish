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
 * <code>OperationException</code> is a RuntimeException that is thrown when InternalSBB cannot
 * create an operation internally (when the provider has indicated that it does not support a
 * particular operation from the service description, but an attempt is made to invoke it anyway).
 * 
 */
public class OperationException extends SBBRuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4939604681102589566L;

    /**
     * Constructor: Instantiates a new <code>OperationException</code>.
     */
    public OperationException() {
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public OperationException(final String message) {
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
    public OperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public OperationException(final Throwable cause) {
        super(cause);
    }
}
