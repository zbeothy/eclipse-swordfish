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
 * RuntimeException thrown when policy trading is attempted without having set a PolicyResolver.
 */
public class MissingPolicyResolverException extends RuntimeException {

    /** Identifier for serialization. */
    private static final long serialVersionUID = 5125318556581191357L;

    /**
     * Standard constructor.
     */
    public MissingPolicyResolverException() {
        super();
    }

    /**
     * Constructor with message.
     * 
     * @param message
     *        error message
     */
    public MissingPolicyResolverException(final String message) {
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
    public MissingPolicyResolverException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor wrapping another exception.
     * 
     * @param cause
     *        exception to be wrapped
     */
    public MissingPolicyResolverException(final Throwable cause) {
        super(cause);
    }

}
