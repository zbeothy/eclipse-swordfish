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
package org.eclipse.swordfish.configrepos.shared.validation;

/**
 * The Class ResourceException.
 */
public class ResourceException extends Exception {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = 5435169302787986219L;

    /**
     * Constructor: Instantiates a new <code>SBBException</code>.
     */
    public ResourceException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     */
    public ResourceException(final String message) {
        super(message);
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        Textual message that describes the exception.
     * @param cause
     *        The underlying exception that was causing this exception.
     */
    public ResourceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        The underlying exception that was causing this exception.
     */
    public ResourceException(final Throwable cause) {
        super(cause);
    }
}
