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
 * <code>MessageHandlerRegistrationException</code> is thrown if an incoming message handler
 * cannot be registered (for example, there is already a provider that registered a handler for a
 * certain operation and we cannot overwrite it).
 * 
 */
public class MessageHandlerRegistrationException extends OperationException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5805858549676196409L;

    /**
     * Constructor: Instantiates a new <code>MessageHandlerRegistrationException</code>.
     */
    public MessageHandlerRegistrationException() {
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public MessageHandlerRegistrationException(final String message) {
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
    public MessageHandlerRegistrationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that caused this exception.
     */
    public MessageHandlerRegistrationException(final Throwable cause) {
        super(cause);
    }

}
