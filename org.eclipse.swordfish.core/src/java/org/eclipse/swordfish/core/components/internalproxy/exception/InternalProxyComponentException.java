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
package org.eclipse.swordfish.core.components.internalproxy.exception;

/**
 * The Class InternalProxyComponentException.
 * 
 */
public class InternalProxyComponentException extends Exception {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = 2938054238227600974L;

    /**
     * Instantiates a new internal proxy component exception.
     */
    public InternalProxyComponentException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     */
    public InternalProxyComponentException(final String message) {
        super(message);
    }

    /**
     * The Constructor.
     * 
     * @param resourceKey
     *        to be used to fill in this exception with a specific message
     * @param cause
     *        which will reference the cause for this exception
     */
    public InternalProxyComponentException(final String resourceKey, final Throwable cause) {
        super(resourceKey, cause);
    }

    /**
     * The Constructor.
     * 
     * @param t
     *        which will reference the cause for this exception
     */
    public InternalProxyComponentException(final Throwable t) {
        super(t);
    }

}
