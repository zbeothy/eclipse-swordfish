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

import org.eclipse.swordfish.core.exception.report.Util;

/**
 * A RuntimeException with a localized and parametrizable message that is automatically logged when
 * it is instantiated. For demonstration purposes, the logging is currently implemented with a
 * class-specific Log4J Logger. TODO replace Log4J with the SOP logging framework when it is
 * available
 * 
 */
public abstract class Abstract_RuntimeException extends RuntimeException implements SBB_Exception {

    /** errorCode. */
    private String errorCode;

    /** Java package for search the resource bundle for Exception message. */
    private String javaPackage;

    /** Resource key for Exception message. */
    private String resourceKey;

    /** Parameters for gaps in error messages. */
    private String[] messageParameters;

    /** Name of the host where the exception was originally thrown. */
    private String originatingHost;

    /** Universally Unique Identifier for the exception. */
    private String uuid;

    /** default message. */
    private String defaultMessage;

    /**
     * Create a new AbstractLoggingRuntimeException instance without arguments. At least the
     * resource key has to be set with setResourceKey() before throwing the exception. This
     * constructor is provided to enable object creation by reflection, for example by the Artix
     * runtime. Application and service programmes should used one of the other constructors.
     */
    public Abstract_RuntimeException() {
        this.resourceKey = null;
        this.messageParameters = new String[] {};
        this.originatingHost = null;
        this.uuid = null;
        // init();
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     */
    public Abstract_RuntimeException(final String resourceKey) {
        this(null, resourceKey, new String[] {});
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with one message parameter.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     */
    public Abstract_RuntimeException(final String resourceKey, final String parameter1) {
        this(null, resourceKey, new String[] {parameter1});
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with one message parameter.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     * @param parameter2
     *        parameter for gaps in error messages
     */
    public Abstract_RuntimeException(final String resourceKey, final String parameter1, final String parameter2) {
        this(null, resourceKey, new String[] {parameter1, parameter2});
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with one message parameter.
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
    public Abstract_RuntimeException(final String resourceKey, final String parameter1, final String parameter2,
            final String parameter3) {
        this(null, resourceKey, new String[] {parameter1, parameter2, parameter3});
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with a message parameter array.
     * 
     * @param resourceKey
     *        resource bundle key for the error message
     * @param messageParameters
     *        parameters for gaps in error messages
     */
    public Abstract_RuntimeException(final String resourceKey, final String[] messageParameters) {
        this(null, resourceKey, messageParameters);
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with a nested Throwable.
     * 
     * @param cause
     *        the cause of the exception
     * @param resourceKey
     *        resource bundle key for the error message
     */
    public Abstract_RuntimeException(final Throwable cause, final String resourceKey) {
        this(cause, resourceKey, new String[] {});
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with a nested Throwable and one message
     * parameter.
     * 
     * @param cause
     *        the cause of the exception
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     */
    public Abstract_RuntimeException(final Throwable cause, final String resourceKey, final String parameter1) {
        this(cause, resourceKey, new String[] {parameter1});
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with a nested Throwable and two message
     * parameters.
     * 
     * @param cause
     *        the cause of the exception
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     * @param parameter2
     *        parameter for gaps in error messages
     */
    public Abstract_RuntimeException(final Throwable cause, final String resourceKey, final String parameter1,
            final String parameter2) {
        this(cause, resourceKey, new String[] {parameter1, parameter2});
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with a nested Throwable and two message
     * parameters.
     * 
     * @param cause
     *        the cause of the exception
     * @param resourceKey
     *        resource bundle key for the error message
     * @param parameter1
     *        parameter for gaps in error messages
     * @param parameter2
     *        parameter for gaps in error messages
     * @param parameter3
     *        parameter for gaps in error messages
     */
    public Abstract_RuntimeException(final Throwable cause, final String resourceKey, final String parameter1,
            final String parameter2, final String parameter3) {
        this(cause, resourceKey, new String[] {parameter1, parameter2, parameter3});
    }

    /**
     * Create a new AbstractLoggingRuntimeException instance with a nested Throwable and a message
     * parameter array.
     * 
     * @param cause
     *        the cause of the exception
     * @param resourceKey
     *        resource bundle key for the error message
     * @param messageParameters
     *        parameters for gaps in error messages
     */
    public Abstract_RuntimeException(final Throwable cause, final String resourceKey, final String[] messageParameters) {
        super(cause);
        this.resourceKey = resourceKey;
        this.messageParameters = messageParameters;
        this.originatingHost = Util.getLocalHostName();
        this.uuid = Util.generateUuid();
        // init();

    }

    /**
     * Gets the default message.
     * 
     * @return String message
     */
    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    /**
     * Gets the error code.
     * 
     * @return the error code
     * 
     * @see org.eclipse.swordfish.core.exception.SBB_Exception#getErrorCode()
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * Gets the java package.
     * 
     * @return the java package
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#getJavaPackage()
     */
    public String getJavaPackage() {
        return this.javaPackage;
    }

    /**
     * Gets the message.
     * 
     * @return String message
     */
    @Override
    public String getMessage() {
        return this.getDefaultMessage();
    }

    /**
     * Gets the message parameters.
     * 
     * @return the message parameters
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#getMessageParameters()
     */
    public String[] getMessageParameters() {
        return this.messageParameters;
    }

    /**
     * Gets the originating host.
     * 
     * @return the originating host
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#getOriginatingHost()
     */
    public String getOriginatingHost() {
        return this.originatingHost;
    }

    /**
     * Gets the resource key.
     * 
     * @return the resource key
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#getResourceKey()
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * Gets the uuid.
     * 
     * @return the uuid
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#getUuid()
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Checks for message parameters.
     * 
     * @return true, if has message parameters
     * 
     * @see org.eclipse.swordfish.core.exception.SBB_Exception#hasMessageParameters()
     */
    public boolean hasMessageParameters() {
        if ((this.messageParameters == null) || (this.messageParameters.length == 0)) return false;
        return true;
    }

    /**
     * Sets the default message.
     * 
     * @param pDefaultMessage
     *        default message
     */
    public void setDefaultMessage(final String pDefaultMessage) {
        this.defaultMessage = pDefaultMessage;
    }

    /**
     * Sets the error code.
     * 
     * @param errorCode
     *        the error code
     * 
     * @see org.eclipse.swordfish.core.exception.SBB_Exception#setErrorCode(java.lang.String)
     */
    public void setErrorCode(final String errorCode) {
        this.errorCode = errorCode;

    }

    /**
     * Sets the java package.
     * 
     * @param javaPackage
     *        the java package
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#setJavaPackage(String)
     */
    public void setJavaPackage(final String javaPackage) {
        this.javaPackage = javaPackage;
    }

    /**
     * Sets the message parameters.
     * 
     * @param messageParameters
     *        the message parameters
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#setMessageParameters(String[])
     */
    public void setMessageParameters(final String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    /**
     * Sets the originating host.
     * 
     * @param originatingHost
     *        the originating host
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#setOriginatingHost(java.lang.String)
     */
    public void setOriginatingHost(final String originatingHost) {
        this.originatingHost = originatingHost;
    }

    /**
     * Sets the resource key.
     * 
     * @param resourceKey
     *        the resource key
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#setResourceKey(String)
     */
    public void setResourceKey(final String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Sets the uuid.
     * 
     * @param uuid
     *        the uuid
     * 
     * @see org.eclipse.swordfish.core.util.exception.LocalizableException#setUuid(java.lang.String)
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * To string.
     * 
     * @return String string
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append(" :");
        sb.append(" originating Host : ");
        sb.append(this.originatingHost);
        sb.append(" UUID : ");
        sb.append(this.uuid);
        sb.append(" errorCode : ");
        sb.append(this.resourceKey);
        if (this.hasMessageParameters()) {
            sb.append(Util.LINE_SEPARATOR);
            for (int i = 0; i < this.messageParameters.length; i++) {
                sb.append("Paramter ");
                sb.append(i);
                sb.append(" : ");
                sb.append(this.messageParameters[i]);
                sb.append(Util.LINE_SEPARATOR);
            }
        }

        return sb.toString();
    }

}
