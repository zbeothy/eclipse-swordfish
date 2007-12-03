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
 * Interface for classes that offer a localized and parametrized messages through resource bundles
 * and an array of String parameters. Shall be implemented by InternalSBB subclasses of Exception
 * and RuntimeException.
 * 
 */
public interface SBB_Exception {

    /**
     * Gets the default message.
     * 
     * @return String message
     */
    String getDefaultMessage();

    /**
     * Get the java package name of the class which throws this exception. It is used for search in
     * this package class specific resource bundle.
     * 
     * @return the java package name
     */
    String getJavaPackage();

    /**
     * Get parameters to fill gaps in an error message. May return an empty array if there are no
     * parameters, but not null.
     * 
     * @return message parameters
     */
    String[] getMessageParameters();

    /**
     * Get the name of the host where the exception originated. In the implementing classes, the
     * host name shall be set automatically when an exception is thrown by application or service
     * code, but should be taken from the fault message if the exception is recreated on client side
     * after is has been thrown on the server side. This can be achieved by suitable constructors.
     * 
     * @return name of the (local or remote) host where the exception was originally thrown
     */
    String getOriginatingHost();

    /**
     * Get the key to the Exception's parametrizable message in the error message resource bundle.
     * 
     * @return the resource key for the Exception's message
     */
    String getResourceKey();

    /**
     * Get the exception's Universally Unique Identifier (UUID). This identifier can be used to
     * correlate a remote exception on the client side with the original exception on the server
     * side, for example when reading log files. This may be useful for example when an exception is
     * first noticed on the client side, but to investigate the problem, the stacktrace of the
     * server side is needed. In the implementing classes, the UUID shall be generated automatically
     * when an exception is thrown by application or service code, but should be taken from the
     * fault message if the exception is recreated on client side after is has been thrown on the
     * server side. This can be achieved by suitable constructors.
     * 
     * @return UUID for the exception
     */
    String getUuid();

    /**
     * Checks for message parameters.
     * 
     * @return boolean bool
     */
    boolean hasMessageParameters();

    /**
     * Sets the default message.
     * 
     * @param pMessage
     *        message
     */
    void setDefaultMessage(String pMessage);

    /**
     * Set the java package name of the class which throws this exception. It is used for search in
     * this package class specific resource bundle. This method is provided to enable object
     * creation by reflection, for example by the Artix runtime. Application and service programmers
     * should instead set the value through a suitable constructor.
     * 
     * @param javaPackage
     *        the java package name
     */
    void setJavaPackage(final String javaPackage);

    /**
     * Set parameters to fill gaps in an error message. This method is provided to enable object
     * creation by reflection, for example by the Artix runtime. Application and service programmers
     * should set the value through a suitable constructor.
     * 
     * @param messageParameters
     *        array of message parameters
     */
    void setMessageParameters(final String[] messageParameters);

    /**
     * Set the name of the host where the exception originated. This method is provided to enable
     * object creation by reflection, for example by the Artix runtime. Application and service
     * programmers should never use it. Implementing classes shall set the originating host
     * automatically to the local host in all constructors except the no-arg constructor.
     * 
     * @param originatingHost
     *        name of the (local or remote) host where the exception was originally thrown
     */
    void setOriginatingHost(final String originatingHost);

    /**
     * Set the key to the Exception's parametrizable message in the error message resource bundle.
     * This method is provided to enable object creation by reflection, for example by the Artix
     * runtime. Application and service programmers should instead set the value through a suitable
     * constructor.
     * 
     * @param resourceKey
     *        resource bundle key for the message
     */
    void setResourceKey(final String resourceKey);

    /**
     * Set the exception's Universally Unique Identifier (UUID). This method is provided to enable
     * object creation by reflection, for example by the Artix runtime. Application and service
     * programmers should never use it. Implementing classes shall generate a UUID automatically in
     * all constructors except the no-arg constructor.
     * 
     * @param uuid
     *        UUID for the exception
     */
    void setUuid(final String uuid);
}
