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
package org.eclipse.swordfish.core.components.processing;

import org.eclipse.swordfish.core.exception.ComponentException;

/**
 * This exception is thrown when duplicate property is added to the ProcessingContext.
 * 
 */
public class AlreadyExistException extends ComponentException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5583727175668752450L;

    /**
     * The default constructor.
     */
    public AlreadyExistException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     */
    public AlreadyExistException(final String message) {
        super(message);
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     * @param cause
     *        the cause
     */
    public AlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the cause
     */
    public AlreadyExistException(final Throwable cause) {
        super(cause);
    }

}
