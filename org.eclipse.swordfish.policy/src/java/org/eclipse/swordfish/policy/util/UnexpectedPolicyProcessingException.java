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
package org.eclipse.swordfish.policy.util;

/**
 * Indicates an unexpected problem during policy processing within an AssertionProcessor.
 * 
 */
public class UnexpectedPolicyProcessingException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 3967754739663431405L;

    /**
     * Instantiates a new unexpected policy processing exception.
     * 
     * @param msg
     *        the msg
     */
    public UnexpectedPolicyProcessingException(final String msg) {
        super(msg);
    }

    /**
     * Instantiates a new unexpected policy processing exception.
     * 
     * @param msg
     *        the msg
     * @param t
     *        the t
     */
    public UnexpectedPolicyProcessingException(final String msg, final Throwable t) {
        super(msg, t);
    }

}
