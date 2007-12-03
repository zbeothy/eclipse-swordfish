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
package org.eclipse.swordfish.core.components.iapi;

import java.util.Collection;
import java.util.Map;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;
import org.apache.commons.collections.MultiMap;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.command.CommandFactory;
import org.eclipse.swordfish.core.components.endpointmanager.EndpointManager;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.handlerregistry.HandlerRegistry;
import org.eclipse.swordfish.core.components.processing.PolicyRouter;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.PolicyResolver;
import org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver;
import org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager;
import org.eclipse.swordfish.core.management.notification.EntityState;
import org.eclipse.swordfish.core.management.notification.ManagementNotificationListener;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;

/**
 * This class provides enough support to implement the PAPI based on this interface.
 */
public interface Kernel {

    /** the role of this interface to look it up. */
    String ROLE = Kernel.class.getName();

    /**
     * activated all endpoints that belong to the service description using the endpoint manager.
     * 
     * @param serviceDesc
     *        the description of the service to be activated
     * 
     * @throws ServiceException
     *         if the service couls not be activated.
     */
    void activateAllEndpoints(CompoundServiceDescription serviceDesc) throws InternalInfrastructureException;

    /*
     * here is support for the messaging framework. Specially this is the case when operations want
     * to initially create something (create Exchange) and send it
     * 
     */
    /**
     * Creates the message exchange factory.
     * 
     * @param description
     *        the description
     * 
     * @return the message exchange factory
     * 
     * @throws InternalMessagingException
     */
    MessageExchangeFactory createMessageExchangeFactory(OperationDescription description) throws MessagingException;

    /**
     * Creates the message exchange factory.
     * 
     * @param epr
     *        the representation of a WSA address
     * 
     * @return -- a message exchange factory
     * 
     * @throws InternalMessagingException
     */
    MessageExchangeFactory createMessageExchangeFactory(WSAEndpointReference epr) throws MessagingException;

    /**
     * deactivated all endpoints that belong to the service description using the endpoint manager.
     * 
     * @param serviceDesc
     *        the description of the service to be deactivated
     * 
     * @throws ServiceException
     *         if the service couls not be deactivated.
     */
    void deactivateAllEndpoints(CompoundServiceDescription serviceDesc) throws InternalInfrastructureException;

    /**
     * Fetch all service description.
     * 
     * @param service
     *        the service
     * @param policyName
     *        the policy name
     * 
     * @return the collection
     * 
     * @throws ServiceAddressingException
     */
    Collection /* <CompoundServiceDescription> */fetchAllServiceDescription(QName service, String policyName)
            throws InternalServiceDiscoveryException;

    /**
     * Fetch service description.
     * 
     * @param service
     *        the service
     * @param providerID
     *        the provider ID
     * 
     * @return the compound service description
     * 
     * @throws ServiceAddressingException
     */
    CompoundServiceDescription fetchServiceDescription(QName service, QName providerID) throws InternalServiceDiscoveryException;

    /*
     * here come the methods to create or access or create ServiceSkeletons or proxies. The final
     * construction of the correct Object will happen in the PAPI instance. However we need to have
     * a facility that stores already created stubs and skeletons to return the same instances back
     * to the PAPI instance.
     */
    /**
     * Fetch service description.
     * 
     * @param service
     *        the service
     * @param policyName
     *        the policy name
     * 
     * @return the compound service description
     * 
     * @throws ServiceAddressingException
     */
    CompoundServiceDescription fetchServiceDescription(QName service, String policyName) throws InternalServiceDiscoveryException;

    /**
     * Fetch service description with policy id.
     * 
     * @param service
     *        the service
     * @param policyId
     *        the policy id
     * 
     * @return the compound service description
     * 
     * @throws ServiceAddressingException
     */
    CompoundServiceDescription fetchServiceDescriptionWithPolicyId(QName service, String policyId)
            throws InternalServiceDiscoveryException;

    /**
     * Generate UUID.
     * 
     * @return the string
     */
    String generateUUID();

    /**
     * Gets the and remove exchange.
     * 
     * @param id
     *        the id
     * 
     * @return the and remove exchange
     */
    MessageExchange getAndRemoveExchange(String id);

    /**
     * Gets the command factory.
     * 
     * @return the command factory
     */
    CommandFactory getCommandFactory();

    /**
     * Gets the component.
     * 
     * @param interfaceName
     *        the interface name
     * @param diversifier
     *        the diversifier
     * 
     * @return the component
     */
    Object getComponent(Class interfaceName, String diversifier);

    /**
     * Gets the endpoint manager.
     * 
     * @return the endpoint manager
     */
    EndpointManager getEndpointManager();

    /*
     * here is also a mechanism for storing arbitrary objects. PAPI will use this for instance for
     * InternalAuthenticationHandler sets, InternalEnvironment Objects and constructed service
     * proxies and skeltons. the key prefix is intended to provide a little help for debugging and
     * sorting the content of such a store.
     */
    /**
     * Gets the environment store.
     * 
     * @return the environment store
     */
    Map getEnvironmentStore();

    /**
     * Gets the handler registry.
     * 
     * @return the handler registry
     */
    HandlerRegistry getHandlerRegistry();

    /*
     * here are methods for persistant storage .. they might need rework because of multi Keys
     */

    /**
     * Gets the instrumentation manager.
     * 
     * @return an <code>InternalInstrumentationManager</code> instance to register and unregister
     *         instrumenation objects
     */
    InstrumentationManager getInstrumentationManager();

    /**
     * Gets the local endpoint repository.
     * 
     * @return the local endpoint repository
     */
    LocalEndpointRepository getLocalEndpointRepository();

    /*
     * here is support for the command processors and delivery channel (down stuff)
     * 
     */

    /*
     * here are environment builing support methods the getCongif value method can be used to access
     * values in the configuration, specially the "Location" information. The assertionInformation
     * might be of use when PAPI needs to interpret some stuff out of the actual policy
     */
    /**
     * Gets the location id.
     * 
     * @return the location id
     */
    String getLocationId();

    /**
     * Gets the management notification listener.
     * 
     * @return the management notification listener
     */
    ManagementNotificationListener getManagementNotificationListener();

    /**
     * Gets the participant.
     * 
     * @return the participant
     */
    UnifiedParticipantIdentity getParticipant();

    /**
     * Gets the policy resolver.
     * 
     * @return the policy resolver
     */
    PolicyResolver getPolicyResolver();

    /**
     * Gets the policy router.
     * 
     * @return the policy router
     */
    PolicyRouter getPolicyRouter();

    /**
     * Gets the proxy store.
     * 
     * @return Returns the proxyStore.
     */
    MultiMap getProxyStore();

    /**
     * Gets the sd resolver.
     * 
     * @return the service description resolver of this Kernel
     */
    ServiceDescriptionResolver getSdResolver();

    /**
     * Gets the skeleton store.
     * 
     * @return Returns the skeletonStore.
     */
    Map getSkeletonStore();

    /**
     * Checks if is active.
     * 
     * @return true, if is active
     */
    boolean isActive();

    /**
     * registers a message handler thus sbb can dispatch inbound messages to it. a message handler
     * is always bound to a serviceName, operation name and a Role (sender, receiver)
     * 
     * @param serviceName
     *        the name of the setrvice the handler behangs to
     * @param operationName
     *        the operation name this handler belongs to
     * @param handler
     *        the handler to be invoked
     * @param role
     *        identify weather this is a sender or a receiver (for local loop back)
     * 
     * @throws MessageHandlerRegistrationException
     */
    void registerMessageHandler(QName serviceName, String operationName, Role role, IncomingMessageHandlerProxy handler)
            throws MessageHandlerRegistrationException;

    /**
     * Removes the call context.
     * 
     * @param key
     *        the key
     */
    void removeCallContext(String key);

    /**
     * Restore call context.
     * 
     * @param key
     *        the key
     * 
     * @return the call context extension
     * 
     * @throws ContextNotRestoreableException
     * @throws ContextNotFoundException
     */
    CallContextExtension restoreCallContext(String key) throws InternalIllegalInputException, InternalInfrastructureException;

    /**
     * Send operation state notification.
     * 
     * @param state
     *        the state
     * @param op
     *        the op
     * @param role
     *        the role
     */
    void sendOperationStateNotification(EntityState state, InternalOperation op, ParticipantRole role);

    /**
     * Sets the participant.
     * 
     * @param participant
     *        the new participant
     */
    void setParticipant(UnifiedParticipantIdentity participant);

    /*
     * here are the core life cycle management
     */
    /**
     * Shutdown.
     */
    void shutdown();

    /*
     * The following should be sufficient support for storing and restoring call contextes. PAPI
     * should do the class casting. This is convenient naming!
     */
    /**
     * Store call context.
     * 
     * @param callContext
     *        the call context
     * 
     * @return the string
     * 
     * @throws ContextNotStoreableException
     */
    String storeCallContext(CallContextExtension callContext) throws InternalIllegalInputException, InternalInfrastructureException;

    /**
     * Store exchange.
     * 
     * @param exchange
     *        the exchange
     */
    void storeExchange(MessageExchange exchange);

    /**
     * unregisters an already registered handler.
     * 
     * @param serviceName
     *        the service name this handler belongs to
     * @param operationName
     *        the operation name this handler belongs to
     * @param role
     *        the role this handler was registered for
     * @param sbbInitiated
     *        if the unregistration is intiated through sbb
     * 
     * @throws MessageHandlerRegistrationException
     */
    void unregisterMessageHandler(QName serviceName, String operationName, Role role, boolean sbbInitiated)
            throws MessageHandlerRegistrationException;

}
