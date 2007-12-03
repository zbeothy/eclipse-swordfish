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
package org.eclipse.swordfish.papi.internal;

import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalMessageFactory;

/**
 * <b>Access to valid environment and convenience methods for an InternalSBB instance</b><br>
 * This interface provides access to the environment, which surrounds an actual InternalSBB instance
 * once it has been created via the {@link org.eclipse.swordfish.papi.InternalSBBFactory}.
 * <p>
 * The <code>InternalEnvironment</code> provides different kinds of general helper functionality
 * including:
 * <ul>
 * <li>Access to business releated IDs such as application, location, and instance ID for an
 * InternalSBB instance.</li>
 * <li>A factory to create messages to be used by service consumers and providers.</li>
 * <li>The ability to store and retrieve context information for service calls (<code>InternalCallContext</code>).</li>
 * <li>Access to InternalSBB extensions using "InternalSBB extension components"</li>
 * </ul>
 * 
 */
public interface InternalEnvironment {

    /**
     * To facilitate logging in a distributed, service-oriented environment, InternalSBB supports
     * integration of its own logging usage with application-level logging.
     * 
     * This method can be used to ensure that the application level log message is written to the
     * application's log output and also to the InternalSBB log. This enables an application to mix
     * business-level log output (which can only be provided by the consumer or provider
     * application) with InternalSBB's log output.
     * 
     * Note: If the InternalSBB handler is already added or if the provided object is not of an
     * appropriate type, invocation of this method will be ignored.
     * 
     * <p>
     * 
     * @param aLogger
     *        the logger to be modified. Any message written to this logger will be written to the
     *        InternalSBB trace log (in addition to whatever other log destinations the application
     *        has configured for this logger). Currently supported types are
     *        java.util.logging.Logger org.apache.log4j.Logger org.apache.log4j.AppenderAttachable
     * @return A boolean indicating whether an appender/handler has been set to the logger.
     * 
     * @throws IllegalAccessException,
     *         ClassNotFoundException if a logger can not be constructed.
     */
    boolean addSBBTraceDestination(Object aLogger) throws InternalSBBException;

    /**
     * This method gives access to extensions to the PAPI. Concrete usage is described within the
     * extensions. Each extension defines an interface for one entry object. By calling this method
     * with the interface as first parameter, this method retuns an object of a class that
     * implements this interface. The second parameter can be used for some extensions if there are
     * different implementation options for the entry interface.
     * <p>
     * An example for a possible future extension could be an API for some of the management
     * extensions of the SOPware Management InternalService Specification (MSS).
     * </p>
     * 
     * @param anInterface
     *        interface of required object
     * @param anOptionalDiversification
     *        optional additional indicator for a specific required implementation. May be null.
     *        Concrete usage depends on required interface and will be defined with the extension
     * @return An object implementing the requested interface or null if this interface is not
     *         supported.
     */
    Object getComponent(Class anInterface, String anOptionalDiversification) throws InternalSBBException;

    /**
     * This method returns an ID that defines the current location of the participant instance.
     * <p>
     * The location ID parameter is set by a SOP administrator as part of the InternalSBB library
     * installation.
     * </p>
     * 
     * @return The ID of the location where the application is running. This method might throw a
     *         {@link org.eclipse.swordfish.papi.exception.ConfigurationException} in case of
     *         misconfiguration.
     */
    String getLocationID();

    /**
     * This method returns a InternalMessageFactory that can be used to create messages to be sent.
     * 
     * @return A message factory to create outgoing messages. Note: This method does not create a
     *         new InternalMessageFactory but returns an existing one.
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory
     */
    InternalMessageFactory getMessageFactory();

    /**
     * This method returns a {@link org.eclipse.swordfish.papi.InternalParticipantIdentity}
     * 
     * The object returned is exactly the same object that was passed to InternalSBB to create the
     * current instance.<br>
     * IMPORTANT: The application must treat its InternalParticipantIdentity for a certain
     * InternalSBB instance as immutable ("read-only"). This means that while it is technically
     * possible for the application to manipulate for example the ApplicationID within a
     * InternalParticipantIdentity that has been used to create an InternalSBB instance, this may
     * lead to unpredictable results. In other words, the application is NOT ALLOWED to perform such
     * manipulations after the InternalParticipantIdentity has been used.
     * 
     * @return The read-only InternalSBB identification data.
     */
    InternalParticipantIdentity getParticipantIdentity();

    /**
     * This method removes the InternalSBB appender/handler from a logger that has previously been
     * set using {@link #addSBBTraceDestination(Object)}
     * <p>
     * If no InternalSBB specific handler has been added to the logger before, this method has no
     * effect. If the provided object is not of an appropriate type no operation will be performed.
     * </p>
     * 
     * @param aLogger
     *        the logger to be modified Currently supported types are java.util.logging.Logger
     *        org.apache.log4j.Logger org.apache.log4j.AppenderAttachable
     * @return A boolean indicating whether an handler/appender has been removed.
     * 
     */
    boolean removeSBBTraceDestination(Object aLogger) throws InternalSBBException;

    /**
     * This method restores a previously persisted InternalCallContext.
     * <p>
     * The InternalCallContext is read (and deleted) from storage. One can use this to create
     * responses for callback operations. The mechanism to store and restore a context is explained
     * in detail in the method {@link #storeCallContext(InternalCallContext)}.
     * </p>
     * <p>
     * 
     * <b>Note:</b> the transactional behavior described above is the targeted behavior. The full
     * functional coverage is not implemented in the present InternalSBB version. The current
     * implementation removes the InternalCallContext after restoring it. You need to put the
     * context into the storage again, if the processing does not succeed.<br>
     * </p>
     * 
     * @param aCallId
     *        The ID of the stored context, as previously returned by
     *        {@link #storeCallContext(InternalCallContext)}.
     * @return The context that can be used immediately.
     * @throws ContextNotFoundException
     * @throws ContextNotRestoreableException
     * 
     * @see #storeCallContext(InternalCallContext)
     * @see org.eclipse.swordfish.papi.untyped.InternalCallContext
     */
    InternalCallContext restoreCallContext(String aCallId) throws InternalSBBException;

    /**
     * This method stores a <code>InternalCallContext</code> persistently.
     * <p>
     * For example, it can be used to enable an InternalSBB participant application to persist a
     * context, shut down, restart, retrieve the context and then respond. This is the usecase of
     * being able to reply to a request later (in an callback operation for instance).
     * </p>
     * <p>
     * When a participant application wants to persist a context it passes the context to this
     * method. The method returns a unique identifier for the (stored) context. It is up to the
     * participant application to decide how to handle the identifier (it may decide to store it in
     * a database, for example).<br>
     * 
     * At a later point in time the participant application retrieves the context and passes it to
     * the method {@link #restoreCallContext(String)} that returns the context.<br>
     * 
     * @param aCall
     *        The context that should be persisted.
     * @return The unique identification of the stored context. This ID enables the participant
     *         application to request the stored context from the InternalSBB at a later time (using
     *         {@link #restoreCallContext(String)}
     * 
     * @throws ContextNotStoreableException (
     *         forwarded from delegate ).
     * @throws IllegalArgumentException
     *         if handed over context is invalid ( null || bad type ).
     * 
     * @see #restoreCallContext(String)
     * @see org.eclipse.swordfish.papi.untyped.InternalCallContext
     */
    String storeCallContext(InternalCallContext aCall) throws InternalSBBException;
}
