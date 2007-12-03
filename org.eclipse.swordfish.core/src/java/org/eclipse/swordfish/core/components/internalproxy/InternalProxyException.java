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
package org.eclipse.swordfish.core.components.internalproxy;

/**
 * The Class InternalProxyException.
 * 
 */
public class InternalProxyException extends Exception {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = 4613410543495223413L;

    /**
     * Instantiates a new internal proxy exception.
     */
    public InternalProxyException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     */
    public InternalProxyException(final String message) {
        super(message);
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     * @param t
     *        the t
     */
    public InternalProxyException(final String message, final Throwable t) {
        super(message, t);
    }

    /**
     * The Constructor.
     * 
     * @param t
     *        for the throwable
     */
    public InternalProxyException(final Throwable t) {
        super(t);
    }

}
