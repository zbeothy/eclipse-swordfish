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
package org.eclipse.swordfish.papi.internal.untyped;

import java.util.Collection;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.InternalEnvironment;
import org.eclipse.swordfish.papi.internal.InternalSBB;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.SBBRuntimeException;

/**
 * This interface describes the behavior of a service.
 * 
 */
public interface InternalService {

    /**
     * This method adds an authentication handler to this operation. It works like
     * <code>{@link org.eclipse.swordfish.papi.InternalSBB#addAuthenticationHandler(InternalAuthenticationHandler)}</code>.
     * 
     * <p>
     * The authentication handlers registered for a service instance are used for calls to this
     * service and all operations of this service unless an authentication handler of the same type
     * is registered directly for an operation or a message. InternalService authentication handlers
     * overrule central InternalSBB authentication handlers and are themselves overruled by
     * operation or message authentication handlers.
     * </p>
     * 
     * @param anAuthenticationHandler
     *        The authentication handler to be added. The handler must not be null.
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#addAuthenticationHandler(InternalAuthenticationHandler)
     *      Only throws runtime exceptions of type - {@link IllegalArgumentException} if the handed
     *      over handler is null or {@link DuplicateAuthenticationHandlerException} if the same
     *      handler should be registered twice.
     */
    void addAuthenticationHandler(InternalAuthenticationHandler anAuthenticationHandler) throws InternalSBBException;

    /**
     * This method returns an array of all previously registered authentication handlers for this
     * service.
     * <p>
     * The array is empty (has a size of zero) if there are no authentication handlers registered.
     * </p>
     * Notes:
     * <ul>
     * <li>This method never returns null.</li>
     * <li>The method only returns the registered authentication handlers at the service level,
     * means for example, not at the InternalSBB or operation level.</li>
     * </ul>
     * 
     * @return This method returns an array of all authentication handlers registered for this
     *         service.
     */
    InternalAuthenticationHandler[] getAuthenticationHandlers();

    /**
     * This method returns the environment, which is in use for this InternalSBB instance (and thus
     * for this service).
     * 
     * @return Reference to environment.
     * @see org.eclipse.swordfish.papi.InternalEnvironment
     */
    InternalEnvironment getEnvironment();

    /**
     * This method returns the name, which identifies this service.
     * 
     * @return Name, which identifies this service.
     */
    QName getName();

    /**
     * This method returns the communication style of the named operation.
     * 
     * The method is meant to be used by generic participants, which use reflection to determine the
     * abilities of services they deal with.
     * 
     * @param anOperationName
     *        Name of the operation.
     * @return Communication style of the named operation. Only throws
     *         {@link IllegalArgumentException} if the handed over argument is null or
     *         {@link BadOperationNameException} if the operation is not part of the participant
     *         protocol.
     */
    InternalCommunicationStyle getOperationCommunicationStyle(String anOperationName) throws InternalSBBException;

    /*
     * suspends all handlers of this skeleton until resume is called.
     * 
     * REMARK This functionality should be introduced in later versions
     */
    // void suspend();
    /*
     * activates all handlers of this skeleton, that have MessageHandlers. Note that notifications
     * are always active as they are trigered through the provider code.
     * 
     * REMARK This functionality should be introduced in later versions
     */
    // void resume();
    /**
     * This method returns a Collection of Strings holding the names of all available operations for
     * the present service.
     * 
     * This collection will only contain the operations really available and does not neccessarily
     * contain all operations as defined in the service description. Provider and consumer "see" the
     * operations that are present due to restrictions that apply from the service description,
     * participant and agreed policy.
     * <p>
     * Note: It is perfectly valid for a provider to split the implementation of a service in two or
     * more distinct objects, each implementing only parts of the service.
     * </p>
     * <p>
     * 
     * @return Collection of Strings holding the names of all available operations.
     */
    Collection/* <String> */getOperationNames();

    /**
     * This method returns the identification of this service provider.
     * 
     * The identification is used inside the SOP InternalService Registry to define the available
     * policies of a service provider.
     * 
     * @return aProviderId The identification of this service provider.
     */
    QName getProviderID();

    /**
     * This method returns the InternalSBB instance, which was used to create this service.
     * 
     * @return InternalSBB instance this service was created by.
     */
    InternalSBB getSBB();

    /**
     * This method checks, whether there is a callback service for the current service (in opposite
     * direction) defined.
     * <p>
     * A callback service holds operations used for correlated messages, which are send as reaction
     * to incoming messages. Currently callback services are only supported for "Oneway" operations.
     * Thus usually two "Oneway" operations, which are linked via their callback service, form the
     * base for a reliable communictaion.
     * </p>
     * 
     * @return True if a callback service is defined, false otherwise.
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalServiceProxy#getCallbackServiceSkeleton()
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalServiceSkeleton#getCallbackServiceProxy()
     */
    boolean hasCallbackService();

    /**
     * This method indicates whether this service is "active".
     * 
     * <p>
     * A service is active until it is released.
     * </p>
     * 
     * @return True if this service is active (not released), false otherwise.
     */
    boolean isActive();

    /**
     * This method releases this service instance.
     * 
     * Once a service is no longer to be used by a consumer or provider, this consumer or provider
     * should call the <code>release</code> method to free up resources.
     * <p>
     * A particular service was usually obtained by either
     * {@link org.eclipse.swordfish.papi.InternalSBB#lookupServiceProxy(QName, String)} or by
     * {@link org.eclipse.swordfish.papi.InternalSBB#lookupServiceSkeleton(QName, QName)}.
     * </p>
     * Note: In case of a call to {@link org.eclipse.swordfish.papi.InternalSBB#release()} the
     * InternalSBB instance will de-register all active service instances implicitly by a call to
     * the service's <code>release</code> method during the InternalSBB shutdown process.
     * <p>
     * Only throws runtiem exceptions of type {@link SBBRuntimeException}
     */
    void release() throws InternalSBBException;

    /**
     * This method removes an authentication handler from this service. It works like
     * <code>{@link org.eclipse.swordfish.papi.InternalSBB#removeAuthenticationHandler(InternalAuthenticationHandler)}</code>.
     * 
     * <p>
     * Note: This method will not remove authentication handlers on operation, message or
     * InternalSBB level. After removal of an authentication handler from this service the
     * authentication handler of the InternalSBB (if one exists) will be used for further calls.
     * </p>
     * 
     * @param anAuthenticationHandler
     *        The authentication handler to be removed.
     */
    void removeAuthenticationHandler(InternalAuthenticationHandler anAuthenticationHandler) throws InternalSBBException;

}
