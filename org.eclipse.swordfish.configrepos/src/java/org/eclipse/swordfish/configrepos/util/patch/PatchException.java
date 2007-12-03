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
package org.eclipse.swordfish.configrepos.util.patch;

/**
 * The Class PatchException.
 * 
 */
public class PatchException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2740341547219375202L;

    /**
     * Instantiates a new patch exception.
     */
    public PatchException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     */
    public PatchException(final String message) {
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
    public PatchException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        the cause
     */
    public PatchException(final Throwable cause) {
        super(cause);
    }

}
