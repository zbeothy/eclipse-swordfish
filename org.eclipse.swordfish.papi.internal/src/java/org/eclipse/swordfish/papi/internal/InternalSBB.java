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

import java.util.Collection;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalServiceProxy;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalServiceSkeleton;

/**
 * Interface to an actual InternalSBB instance as created by
 * {@link org.eclipse.swordfish.papi.InternalSBBFactory}.
 * <p>
 * InternalSBB's functionality includes:
 * <ul>
 * <li> consumer-side lookup methods for service proxies used when developing service consumers
 * </li>
 * <li> provider-side lookup methods for service skeletons used for developing service providers
 * </li>
 * <li> methods for registering InternalSBB level security callbacks that will be invoked by
 * InternalSBB when authentication information is required </li>
 * <li>methods to manage the InternalSBB instance's lifecycle</li>
 * <li>access to the current InternalSBB environment</li>
 * </ul>
 * 
 * @see org.eclipse.swordfish.papi.InternalSBBFactory for creation of InternalSBB instances.
 */
public interface InternalSBB {

    /**
     * This method adds an authentication handler to the InternalSBB.
     * <p>
     * Authentication in an InternalSBB context is done by an
     * <code>{@link org.eclipse.swordfish.papi.authentication.InternalAuthenticationHandler}</code>.
     * The InternalSBB can handle multiple authentication handlers, with each authentication handler
     * being used to provide a specific means of authentication. This feature is important when a
     * participant needs to authenticate in different ways - for example, with either a username and
     * password, or with an X509 certificate. Depending on the type of authentication required, the
     * InternalSBB calls the associated authentication handler. The participant application has to
     * provide such an authentication handler first by using this method.
     * </p>
     * <p>
     * For each <code>InternalAuthenticationHandler</code> provided by the participant
     * application, the application calls this method and passes the
     * <code>InternalAuthenticationHandler</code>. The functioning of the InternalSBB does not
     * depend on the order in which the different handlers are passed.
     * </p>
     * <p>
     * Each handler registered by this method must implement exactly one concrete sub-interface of
     * <code>{@link org.eclipse.swordfish.papi.authentication.InternalAuthenticationHandler}</code>.
     * This determines the authentication method to be used.
     * </p>
     * <p>
     * Note: You might want to keep the reference for your authentication handler for later removal.
     * </p>
     * <p>
     * The authentication handlers registered for an InternalSBB instance are used for all services,
     * operations and messages unless an authentication handler of the same type is registered
     * directly for services, operations, or an outgoing message. In this case, they overrule the
     * central InternalSBB authentication handler.
     * </p>
     * 
     * @param anAuthenticationHandler
     *        The authentication handler to be added. . handler for the same authentication
     *        mechanism is already registered.
     */
    void addAuthenticationHandler(InternalAuthenticationHandler anAuthenticationHandler) throws InternalSBBException;

    /**
     * This method returns an array of all previously registered authentication handlers for this
     * InternalSBB instance.
     * <p>
     * The array is empty (has a size of zero) if there are no authentication handlers registered.
     * </p>
     * Note:
     * <ul>
     * <li>This method never returns null.</li>
     * <li>The method only returns the registered authentication handlers at the InternalSBB
     * instance level, means for example, not at a service or operation level.</li>
     * </ul>
     * 
     * @return This method returns an array of all authentication handlers registered for this
     *         InternalSBB instance.
     */
    InternalAuthenticationHandler[] getAuthenticationHandlers();

    // constants
    //
    // general functionality
    //
    /**
     * This method returns the InternalSBB instance's environment.
     * 
     * @return Reference to environment
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment
     */
    InternalEnvironment getEnvironment();

    /**
     * This method returns the status of the InternalSBB instance.
     * 
     * @return This method returns <code>true</code> unless this InternalSBB instance has been
     *         shut down.
     * 
     * @see #release()
     */
    boolean isActive();

    /**
     * This method looks up active service proxies.
     * 
     * @return This method returns a collection of all "previously requested but not yet released"
     *         service proxies. In other words, a collection of service proxies, which are "active"
     *         for this particular InternalSBB instance is returned.
     * 
     * @see #lookupServiceProxy(QName, String) .
     */
    Collection/* <InternalServiceSkeleton> */listActiveServiceProxies();

    /**
     * This method looks up all "active" service skeletons.
     * 
     * <p>
     * A service skeleton is "active", if it was requested from the InternalSBB (see:
     * {@link #lookupServiceSkeleton(QName, QName)}) but hasn't been released again yet. <br>
     * While it is in "active" state, for example an
     * {@link org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler} can be registered
     * with this service.
     * </p>
     * 
     * @return This method returns a collection of all "previously requested but not yet released"
     *         service skeletons. In other words, a collection of service skeletons, which are
     *         "active" for this particular InternalSBB instance is returned.
     * 
     * @see #lookupServiceSkeleton(QName, QName) .
     */
    Collection/* <InternalServiceSkeleton> */listActiveServiceSkeletons() throws InternalSBBException;

    /**
     * This method returns a collection of service proxies for all providers that offer the service
     * identified by <code>aServiceName</code> that match the policy identified by
     * <code>aPolicyName</code>.
     * 
     * The resulting collection will contain one service proxy per provider with matching policy.
     * 
     * <p>
     * Note: The returned service proxies differ based on their ProviderID, which can be used to
     * distinguish between the providers offering the service.
     * </p>
     * 
     * @param aServiceName
     *        The qualified name of the service to be used.
     * @param aPolicyName
     *        The symbolic name of the policy to be used. This name is used to identify the policy
     *        used for the service calls. Note: <code>aPolicyName</code> must either reference an
     *        existing policy, or it must be set to <code>null</code> to indicate that the default
     *        policy is to be used.
     * 
     * @return A collection of matching service proxies.
     * 
     * @throws InternalAuthorizationException
     *         the authorization exception
     * @throws InternalAuthenticationException
     *         the authentication exception
     * @throws InternalInfrastructureException
     *         the infrastructure exception
     * @throws ServiceAddressingException .
     *         <ul>
     *         <li>if (<code>null</code>) is passed for the service name or
     *         <li>if an empty string ("") is passed as the policy name
     *         </ul>
     * 
     * @see org.eclipse.swordfish.papi.authentication#AuthenticationHandler
     * @see #lookupServiceProxy(QName, String)
     */
    Collection/* <InternalServiceProxy> */lookupAllServiceProxies(QName aServiceName, String aPolicyName)
            throws InternalSBBException;

    //
    // functionality for consumers
    //
    /**
     * This method returns a service proxy for the service identified by <code>aServiceName</code>
     * that can be used by a consumer to interact with the service. a matching provider is selected,
     * identified by <code>aPolicyName</code> based on the policy.
     * <p>
     * Note: If there is more than one matching provider, the choice is arbitrary. To retrieve a
     * collection of service proxies for all matching providers, use
     * {@link #lookupAllServiceProxies(QName, String) } instead.
     * </p>
     * <p>
     * Note: <code>aPolicyName</code> must either reference an existing policy, or it must be set
     * to <code>null</code> to indicate that the default policy is to be used.
     * </p>
     * 
     * @param aServiceName
     *        The qualified name of the service to be used.
     * @param aPolicyName
     *        The symbolic name of the policy to be used. This name is used to identify the policy
     *        that will be used for the service calls.
     * 
     * @return A proxy for the service.
     * 
     * @throws InternalAuthorizationException
     *         the authorization exception
     * @throws InternalAuthenticationException
     *         the authentication exception
     * @throws InternalInfrastructureException
     *         the infrastructure exception
     * @throws ServiceAddressingException .
     *         <ul>
     *         <li>if (<code>null</code>) is passed for the service name or
     *         <li>if an empty string ("") is passed as the policy name
     *         </ul>
     * 
     * @see #lookupAllServiceProxies(QName, String)
     * @see org.eclipse.swordfish.papi.authentication#AuthenticationHandler
     */
    InternalServiceProxy lookupServiceProxy(QName aServiceName, String aPolicyName) throws InternalSBBException;

    /**
     * Lookup service skeleton.
     * 
     * @param aServiceName
     *        the a service name
     * @param aProviderID
     *        the a provider ID
     * 
     * @return the service skeleton
     * 
     * @throws ServiceAddressingException
     *         the service addressing exception
     * @throws InternalInfrastructureException
     *         the infrastructure exception
     * @throws InternalAuthenticationException
     *         the authentication exception
     * @throws InternalAuthorizationException
     *         the authorization exception
     */
    // functionality for providers
    /**
     * This method returns a skeleton for the service identified by <code>aServiceName</code> to
     * be provided by the provider identified by <code>aProviderID</code>. For a particular
     * InternalSBB instance this message will always return the same (identical) object if it is
     * called with identical parameters (except when this object has been explicitly released
     * already).
     * 
     * <p>
     * The service skeleton can be used to enable processing of incoming messages by registering at
     * least one {@link org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler}. It can
     * also be used to publish notifications.
     * </p>
     * 
     * @param aServiceName
     *        the name of the service for which the skeleton is going to be returned.
     * @param aProviderID
     *        the ID of the provider the skeleton is part of
     * @return a provider service skeleton
     * @throws InternalAuthorizationException
     * @throws InternalAuthenticationException
     * @throws InternalInfrastructureException
     * @throws ServiceAddressingException .
     */
    InternalServiceSkeleton lookupServiceSkeleton(QName aServiceName, QName aProviderID) throws InternalSBBException;

    /**
     * This method releases this instance of the InternalSBB.
     * <p>
     * A participant must call this method to shut down an InternalSBB instance, for example when
     * the application is about to shut down or if it wants to free up resources used by this
     * InternalSBB instance.
     * </p>
     * <p>
     * Note: For an application that retains a reference to the InternalSBB object, calls to
     * {@link #isActive()} return false. All other calls result in an
     * <code>SBBClosedException</code> being thrown.
     * </p> .
     */
    void release() throws InternalSBBException;

    /**
     * This method removes an authentication handler from the InternalSBB.
     * 
     * <p>
     * The authentication handler being removed must be already registered with the InternalSBB,
     * otherwise nothing happens.
     * </p>
     * 
     * @param anAuthenticationHandler
     *        The authentication handler to be removed.
     * 
     * @see #addAuthenticationHandler(InternalAuthenticationHandler)
     */
    void removeAuthenticationHandler(InternalAuthenticationHandler anAuthenticationHandler) throws InternalSBBException;
}
