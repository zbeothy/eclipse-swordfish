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
package org.eclipse.swordfish.policytrader.exceptions;

/**
 * Base class for policy trading exceptions.
 */
public class PolicyTraderException extends Exception {

    /** Identifier for serialization. */
    private static final long serialVersionUID = 369456410987871434L;

    /**
     * Standard constructor.
     */
    public PolicyTraderException() {
        super();
    }

    /**
     * Constructor with message.
     * 
     * @param message
     *        error message
     */
    public PolicyTraderException(final String message) {
        super(message);
    }

    /**
     * Constructor with message and exception to be wrapped.
     * 
     * @param message
     *        error message
     * @param cause
     *        exception to be wrapped
     */
    public PolicyTraderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor wrapping another exception.
     * 
     * @param cause
     *        exception to be wrapped
     */
    public PolicyTraderException(final Throwable cause) {
        super(cause);
    }
}
