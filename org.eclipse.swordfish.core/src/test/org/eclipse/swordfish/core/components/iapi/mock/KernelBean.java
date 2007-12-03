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
package org.eclipse.swordfish.core.components.iapi.mock;

import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;
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
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
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
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.w3c.dom.Element;

/**
 * The Class KernelBean.
 */
public class KernelBean implements Kernel {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#activateAllEndpoints(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription)
     */
    public void activateAllEndpoints(final CompoundServiceDescription serviceDesc) throws InternalInfrastructureException {

    }

    /**
     * Creates the logger.
     * 
     * @param bundle
     *        the bundle
     * @param name
     *        the name
     * 
     * @return the logger
     */
    public Logger createLogger(final ResourceBundle bundle, final String name) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#createMessageExchangeFactory(org.eclipse.swordfish.core.components.iapi.OperationDescription)
     */
    public MessageExchangeFactory createMessageExchangeFactory(final OperationDescription description) throws MessagingException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#createMessageExchangeFactory(org.eclipse.swordfish.core.components.addressing.WSAEndpointReference)
     */
    public MessageExchangeFactory createMessageExchangeFactory(final WSAEndpointReference epr) throws MessagingException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#deactivateAllEndpoints(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription)
     */
    public void deactivateAllEndpoints(final CompoundServiceDescription serviceDesc) throws InternalInfrastructureException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#fetchAllServiceDescription(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public Collection fetchAllServiceDescription(final QName service, final String policyName)
            throws InternalServiceDiscoveryException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#fetchServiceDescription(javax.xml.namespace.QName,
     *      javax.xml.namespace.QName)
     */
    public CompoundServiceDescription fetchServiceDescription(final QName service, final QName providerID)
            throws InternalServiceDiscoveryException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#fetchServiceDescription(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public CompoundServiceDescription fetchServiceDescription(final QName service, final String policyName)
            throws InternalServiceDiscoveryException {
        return null;
    }

    /**
     * Fetch service description.
     * 
     * @param service
     *        the service
     * @param policyName
     *        the policy name
     * @param providerID
     *        the provider ID
     * 
     * @return the compound service description
     * 
     * @throws ServiceAddressingException
     */
    public CompoundServiceDescription fetchServiceDescription(final QName service, final String policyName, final QName providerID)
            throws InternalServiceDiscoveryException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#fetchServiceDescriptionWithPolicyId(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public CompoundServiceDescription fetchServiceDescriptionWithPolicyId(final QName service, final String policyId)
            throws InternalServiceDiscoveryException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#generateUUID()
     */
    public String generateUUID() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getAndRemoveExchange(java.lang.String)
     */
    public MessageExchange getAndRemoveExchange(final String id) {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getCommandFactory()
     */
    public CommandFactory getCommandFactory() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getComponent(java.lang.Class,
     *      java.lang.String)
     */
    public Object getComponent(final Class interfaceName, final String diversifier) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getEndpointManager()
     */
    public EndpointManager getEndpointManager() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getEnvironmentStore()
     */
    public Map getEnvironmentStore() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getHandlerRegistry()
     */
    public HandlerRegistry getHandlerRegistry() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getInstrumentationManager()
     */
    public InstrumentationManager getInstrumentationManager() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getLocalEndpointRepository()
     */
    public LocalEndpointRepository getLocalEndpointRepository() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getLocationId()
     */
    public String getLocationId() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getManagementNotificationListener()
     */
    public ManagementNotificationListener getManagementNotificationListener() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getParticipant()
     */
    public UnifiedParticipantIdentity getParticipant() {
        return new UnifiedParticipantIdentity(new InternalParticipantIdentity() {

            public String getApplicationID() {
                return "Library";
            }

            public String getInstanceID() {
                return null;
            }
        });

    }

    /**
     * Gets the policy assertion.
     * 
     * @param name
     *        the name
     * 
     * @return the policy assertion
     */
    public Element getPolicyAssertion(final String name) {
        return null;
    }

    /**
     * Gets the policy assertion names.
     * 
     * @return the policy assertion names
     */
    public Collection getPolicyAssertionNames() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getPolicyResolver()
     */
    public PolicyResolver getPolicyResolver() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getPolicyRouter()
     */
    public PolicyRouter getPolicyRouter() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getProxyStore()
     */
    public MultiMap getProxyStore() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getSdResolver()
     */
    public ServiceDescriptionResolver getSdResolver() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getSkeletonStore()
     */
    public Map getSkeletonStore() {
        return null;
    }

    /**
     * Gets the thread pool executor.
     * 
     * @return the thread pool executor
     */
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#isActive()
     */
    public boolean isActive() {
        return false;
    }

    /**
     * Checks if is service description fetched.
     * 
     * @param serviceName
     *        the service name
     * @param policyName
     *        the policy name
     * 
     * @return true, if is service description fetched
     */
    public boolean isServiceDescriptionFetched(final QName serviceName, final String policyName) {
        return false;
    }

    /**
     * Checks if is service description fetched.
     * 
     * @param serviceName
     *        the service name
     * @param policyName
     *        the policy name
     * @param providerID
     *        the provider ID
     * 
     * @return true, if is service description fetched
     */
    public boolean isServiceDescriptionFetched(final QName serviceName, final String policyName, final QName providerID) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#registerMessageHandler(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy)
     */
    public void registerMessageHandler(final QName serviceName, final String operationName, final Role role,
            final IncomingMessageHandlerProxy handler) throws MessageHandlerRegistrationException {

    }

    /**
     * Register provider message handler.
     * 
     * @param serviceDesc
     *        the service desc
     * @param name
     *        the name
     * @param handler
     *        the handler
     * 
     * @throws MessageHandlerRegistrationException
     */
    public void registerProviderMessageHandler(final CompoundServiceDescription serviceDesc, final String name,
            final InternalIncomingMessageHandler handler) throws MessageHandlerRegistrationException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#removeCallContext(java.lang.String)
     */
    public void removeCallContext(final String key) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#restoreCallContext(java.lang.String)
     */
    public CallContextExtension restoreCallContext(final String key) throws InternalInfrastructureException,
            InternalIllegalInputException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#sendOperationStateNotification(org.eclipse.swordfish.core.management.notification.EntityState,
     *      org.eclipse.swordfish.papi.untyped.Operation,
     *      org.eclipse.swordfish.core.management.notification.ParticipantRole)
     */
    public void sendOperationStateNotification(final EntityState state, final InternalOperation op, final ParticipantRole role) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#setParticipant(org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity)
     */
    public void setParticipant(final UnifiedParticipantIdentity participant) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#shutdown()
     */
    public void shutdown() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#storeCallContext(org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension)
     */
    public String storeCallContext(final CallContextExtension callContext) throws InternalInfrastructureException,
            InternalIllegalInputException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#storeExchange(javax.jbi.messaging.MessageExchange)
     */
    public void storeExchange(final MessageExchange exchange) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#unregisterMessageHandler(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.components.iapi.Role, boolean)
     */
    public void unregisterMessageHandler(final QName serviceName, final String operationName, final Role role,
            final boolean sbbInitiated) throws MessageHandlerRegistrationException {

    }

    /**
     * Unregister provider message handler.
     * 
     * @param serviceDesc
     *        the service desc
     * @param operationName
     *        the operation name
     * @param sbbInitiated
     *        the sbb initiated
     * 
     * @throws MessageHandlerRegistrationException
     */
    public void unregisterProviderMessageHandler(final CompoundServiceDescription serviceDesc, final String operationName,
            final boolean sbbInitiated) throws MessageHandlerRegistrationException {

    }

}
