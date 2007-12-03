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
package org.eclipse.swordfish.core.exception;

/**
 * Abstract superclass for all checked Exceptions thrown by InternalSBB components.
 * 
 */
public class ComponentRuntimeException extends Abstract_RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3460270038213362030L;

    /**
     * Create a new ComponentException instance without arguments. At least the resource key has to
     * be set with setResourceKey() before throwing the exception. This constructor is provided to
     * enable object creation by reflection, for example by the Artix runtime. Application and
     * service programmes should used one of the other constructors.
     */
    public ComponentRuntimeException() {
        super();
    }

    /**
     * Create a new ComponentException instance.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     */
    public ComponentRuntimeException(final String resourceKey) {
        super(resourceKey);
    }

    /**
     * Create a new ComponentException instance with one message parameter.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     */
    public ComponentRuntimeException(final String resourceKey, final String parameter1) {
        super(resourceKey, parameter1);
    }

    /**
     * Create a new ComponentException instance with one message parameter.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     * @param parameter2
     *        parameter for gaps in error messages
     */
    public ComponentRuntimeException(final String resourceKey, final String parameter1, final String parameter2) {
        super(resourceKey, parameter1, parameter2);
    }

    /**
     * Create a new ComponentException instance with one message parameter.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     * @param parameter2
     *        parameter for gaps in error messages
     * @param parameter3
     *        parameter for gaps in error messages
     */
    public ComponentRuntimeException(final String resourceKey, final String parameter1, final String parameter2,
            final String parameter3) {
        super(resourceKey, parameter1, parameter2, parameter3);
    }

    /**
     * Create a new ComponentException instance with a nested Throwable and two message parameters.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     * @param parameter2
     *        parameter for gaps in error messages
     * @param parameter3
     *        parameter for gaps in error messages
     * @param cause
     *        the cause of the exception
     */
    public ComponentRuntimeException(final String resourceKey, final String parameter1, final String parameter2,
            final String parameter3, final Throwable cause) {
        super(cause, resourceKey, parameter1, parameter2, parameter3);
    }

    /**
     * Create a new ComponentException instance with a nested Throwable and two message parameters.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     * @param parameter2
     *        parameter for gaps in error messages
     * @param cause
     *        the cause of the exception
     */
    public ComponentRuntimeException(final String resourceKey, final String parameter1, final String parameter2,
            final Throwable cause) {
        super(cause, resourceKey, parameter1, parameter2);
    }

    /**
     * Create a new ComponentException instance with a nested Throwable and one message parameter.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     * @param cause
     *        the cause of the exception
     */
    public ComponentRuntimeException(final String resourceKey, final String parameter1, final Throwable cause) {
        super(cause, resourceKey, parameter1);
    }

    /**
     * Create a new ComponentException instance with a message parameter array.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param messageParameters
     *        parameters for gaps in error messages
     */
    public ComponentRuntimeException(final String resourceKey, final String[] messageParameters) {
        super(resourceKey, messageParameters);
    }

    /**
     * Create a new ComponentException instance with a nested Throwable and a message parameter
     * array.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param messageParameters
     *        parameters for gaps in error messages
     * @param cause
     *        the cause of the exception
     */
    public ComponentRuntimeException(final String resourceKey, final String[] messageParameters, final Throwable cause) {
        super(cause, resourceKey, messageParameters);
    }

    /**
     * Create a new ComponentException instance with a nested Throwable.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param cause
     *        the cause of the exception
     */
    public ComponentRuntimeException(final String resourceKey, final Throwable cause) {
        super(cause, resourceKey);
    }

    /**
     * To string.
     * 
     * @return String string
     */
    @Override
    public String toString() {
        return super.toString();
    }

}
