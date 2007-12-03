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
 * <code>SBBClosedException</code> is thrown if a participant tries to access functionality of an
 * InternalSBB that has already been released before.
 * 
 */
public class SBBClosedException extends SBBRuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8052027432329169657L;

    /**
     * Constructor: Instantiates a new <code>SBBClosedException</code>.
     */
    public SBBClosedException() {
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public SBBClosedException(final String message) {
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
    public SBBClosedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public SBBClosedException(final Throwable cause) {
        super(cause);
    }
}
