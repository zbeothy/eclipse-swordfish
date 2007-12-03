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
package org.eclipse.swordfish.core.management.mock;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.xml.namespace.QName;
import org.apache.commons.collections.MultiMap;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.command.CommandFactory;
import org.eclipse.swordfish.core.components.endpointmanager.EndpointManager;
import org.eclipse.swordfish.core.components.handlerregistry.HandlerRegistry;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.iapi.impl.KernelBean;
import org.eclipse.swordfish.core.components.processing.PolicyRouter;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.PolicyResolver;
import org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver;
import org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager;
import org.eclipse.swordfish.core.management.notification.EntityState;
import org.eclipse.swordfish.core.management.notification.ManagementNotificationListener;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;

/**
 * Minimal dummy implementation to run management unit tests.
 * 
 */
public class DummyKernel extends KernelBean {

    /** The participant. */
    private UnifiedParticipantIdentity participant;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#activateAllEndpoints(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription)
     */
    @Override
    public void activateAllEndpoints(final CompoundServiceDescription serviceDesc) throws InternalInfrastructureException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#createMessageExchangeFactory(org.eclipse.swordfish.core.components.iapi.OperationDescription)
     */
    @Override
    public MessageExchangeFactory createMessageExchangeFactory(final OperationDescription description) throws MessagingException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#createMessageExchangeFactory(org.eclipse.swordfish.core.components.addressing.WSAEndpointReference)
     */
    @Override
    public MessageExchangeFactory createMessageExchangeFactory(final WSAEndpointReference epr) throws MessagingException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#deactivateAllEndpoints(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription)
     */
    @Override
    public void deactivateAllEndpoints(final CompoundServiceDescription serviceDesc) throws InternalInfrastructureException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#fetchAllServiceDescription(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    @Override
    public Collection fetchAllServiceDescription(final QName service, final String policyName)
            throws InternalServiceDiscoveryException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#fetchServiceDescription(javax.xml.namespace.QName,
     *      javax.xml.namespace.QName)
     */
    @Override
    public CompoundServiceDescription fetchServiceDescription(final QName service, final QName providerID)
            throws InternalServiceDiscoveryException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#fetchServiceDescription(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    @Override
    public CompoundServiceDescription fetchServiceDescription(final QName service, final String policyName)
            throws InternalServiceDiscoveryException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#generateUUID()
     */
    @Override
    public String generateUUID() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getAndRemoveExchange(java.lang.String)
     */
    @Override
    public MessageExchange getAndRemoveExchange(final String id) {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getCommandFactory()
     */
    @Override
    public CommandFactory getCommandFactory() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getComponent(java.lang.Class,
     *      java.lang.String)
     */
    @Override
    public Object getComponent(final Class interfaceName, final String diversifier) {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getEndpointManager()
     */
    @Override
    public EndpointManager getEndpointManager() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getEnvironmentStore()
     */
    @Override
    public Map getEnvironmentStore() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getHandlerRegistry()
     */
    @Override
    public HandlerRegistry getHandlerRegistry() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getInstrumentationManager()
     */
    @Override
    public InstrumentationManager getInstrumentationManager() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getLocationId()
     */
    @Override
    public String getLocationId() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getManagementNotificationListener()
     */
    @Override
    public ManagementNotificationListener getManagementNotificationListener() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getParticipant()
     */
    @Override
    public UnifiedParticipantIdentity getParticipant() {
        return this.participant;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getPolicyResolver()
     */
    @Override
    public PolicyResolver getPolicyResolver() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getPolicyRouter()
     */
    @Override
    public PolicyRouter getPolicyRouter() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getProxyStore()
     */
    @Override
    public MultiMap getProxyStore() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getSdResolver()
     */
    @Override
    public ServiceDescriptionResolver getSdResolver() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#getSkeletonStore()
     */
    @Override
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
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#isActive()
     */
    @Override
    public boolean isActive() {

        return false;
    }

    /**
     * Register message handler.
     * 
     * @param serviceName
     *        the service name
     * @param operationName
     *        the operation name
     * @param role
     *        the role
     * @param handler
     *        the handler
     * 
     * @throws MessageHandlerRegistrationException
     */
    public void registerMessageHandler(final QName serviceName, final String operationName, final Role role,
            final InternalIncomingMessageHandler handler) throws MessageHandlerRegistrationException {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#removeCallContext(java.lang.String)
     */
    @Override
    public void removeCallContext(final String key) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#restoreCallContext(java.lang.String)
     */
    @Override
    public CallContextExtension restoreCallContext(final String key) throws InternalIllegalInputException,
            InternalInfrastructureException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#sendOperationStateNotification(org.eclipse.swordfish.core.management.notification.EntityState,
     *      org.eclipse.swordfish.papi.untyped.Operation,
     *      org.eclipse.swordfish.core.management.notification.ParticipantRole)
     */
    @Override
    public void sendOperationStateNotification(final EntityState state, final InternalOperation op, final ParticipantRole role) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#setParticipant(org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity)
     */
    @Override
    public void setParticipant(final UnifiedParticipantIdentity participant) {
        this.participant = participant;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#shutdown()
     */
    @Override
    public void shutdown() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#storeCallContext(org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension)
     */
    @Override
    public String storeCallContext(final CallContextExtension callContext) throws InternalIllegalInputException,
            InternalInfrastructureException {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#storeExchange(javax.jbi.messaging.MessageExchange)
     */
    @Override
    public void storeExchange(final MessageExchange exchange) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.iapi.impl.KernelBean#unregisterMessageHandler(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.components.iapi.Role, boolean)
     */
    @Override
    public void unregisterMessageHandler(final QName serviceName, final String operationName, final Role role,
            final boolean sbbInitiated) throws MessageHandlerRegistrationException {

    }

}
