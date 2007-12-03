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
 * The Class CommunicationException.
 * 
 */
public class CommunicationException extends InternalProxyException {

    /** Comment for <code>serialVersionUID</code>. */
    private static final long serialVersionUID = 9092596880151585334L;

    /**
     * Instantiates a new communication exception.
     */
    public CommunicationException() {
        super();
    }

    /**
     * The Constructor.
     * 
     * @param message
     *        the message
     */
    public CommunicationException(final String message) {
        super(message);
    }

    /**
     * The Constructor.
     * 
     * @param cause
     *        for the cause
     * @param message
     *        the message
     */
    public CommunicationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The Constructor.
     * 
     * @param t
     *        for the throwable
     */
    public CommunicationException(final Throwable t) {
        super(t);
    }

}
