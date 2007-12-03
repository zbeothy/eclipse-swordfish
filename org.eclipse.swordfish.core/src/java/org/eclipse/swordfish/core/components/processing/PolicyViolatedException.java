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
 * This Exception is thrown if a processing component. cannot fulfil the assigened assertions,
 * because of bad provider input (like the document does not match the schema declared in the
 * assertion). This Exception is not thrown if there is an infrastructure reason of why the
 */
public class PolicyViolatedException extends ComponentException {

    /** This classes VUID. */
    private static final long serialVersionUID = 306901274658552731L;

    /**
     * Instantiates a new policy violated exception.
     */
    public PolicyViolatedException() {
        super();
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     */
    public PolicyViolatedException(final String resourceKey) {
        super(resourceKey);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     */
    public PolicyViolatedException(final String resourceKey, final String parameter1) {
        super(resourceKey, parameter1);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param parameter2
     *        the parameter2
     */
    public PolicyViolatedException(final String resourceKey, final String parameter1, final String parameter2) {
        super(resourceKey, parameter1, parameter2);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param parameter2
     *        the parameter2
     * @param parameter3
     *        the parameter3
     */
    public PolicyViolatedException(final String resourceKey, final String parameter1, final String parameter2,
            final String parameter3) {
        super(resourceKey, parameter1, parameter2, parameter3);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param parameter2
     *        the parameter2
     * @param parameter3
     *        the parameter3
     * @param cause
     *        the cause
     */
    public PolicyViolatedException(final String resourceKey, final String parameter1, final String parameter2,
            final String parameter3, final Throwable cause) {
        super(resourceKey, parameter1, parameter2, parameter3, cause);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param parameter2
     *        the parameter2
     * @param cause
     *        the cause
     */
    public PolicyViolatedException(final String resourceKey, final String parameter1, final String parameter2, final Throwable cause) {
        super(resourceKey, parameter1, parameter2, cause);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param parameter1
     *        the parameter1
     * @param cause
     *        the cause
     */
    public PolicyViolatedException(final String resourceKey, final String parameter1, final Throwable cause) {
        super(resourceKey, parameter1, cause);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param messageParameters
     *        the message parameters
     */
    public PolicyViolatedException(final String resourceKey, final String[] messageParameters) {
        super(resourceKey, messageParameters);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param messageParameters
     *        the message parameters
     * @param cause
     *        the cause
     */
    public PolicyViolatedException(final String resourceKey, final String[] messageParameters, final Throwable cause) {
        super(resourceKey, messageParameters, cause);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param resourceKey
     *        the resource key
     * @param cause
     *        the cause
     */
    public PolicyViolatedException(final String resourceKey, final Throwable cause) {
        super(resourceKey, cause);
    }

    /**
     * Instantiates a new policy violated exception.
     * 
     * @param t
     *        the t
     */
    public PolicyViolatedException(final Throwable t) {
        super(t);
    }

}
