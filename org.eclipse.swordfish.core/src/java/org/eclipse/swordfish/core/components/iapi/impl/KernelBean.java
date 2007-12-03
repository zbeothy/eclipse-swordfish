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
package org.eclipse.swordfish.core.components.iapi.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.jbi.JBIException;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.command.CommandFactory;
import org.eclipse.swordfish.core.components.contextstore.ContextStore;
import org.eclipse.swordfish.core.components.endpointmanager.EndpointManager;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.endpointmanager.impl.EndpointManagerBean;
import org.eclipse.swordfish.core.components.endpointreferenceresolver.EndpointReferenceResolver;
import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.core.components.handlerregistry.HandlerRegistry;
import org.eclipse.swordfish.core.components.helpers.UUIDGenerator;
import org.eclipse.swordfish.core.components.helpers.impl.HttpClientConfigurationBean;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.processing.PolicyRouter;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.PolicyResolver;
import org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver;
import org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager;
import org.eclipse.swordfish.core.management.notification.EntityState;
import org.eclipse.swordfish.core.management.notification.ManagementNotificationListener;
import org.eclipse.swordfish.core.management.notification.OperationStateNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.notification.ParticipantStateNotification;
import org.eclipse.swordfish.core.papi.impl.untyped.consumer.ServiceProxyImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;
import org.eclipse.swordfish.core.papi.impl.untyped.provider.ServiceSkeletonImpl;
import org.eclipse.swordfish.papi.internal.exception.InfrastructureRuntimeException;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.w3c.dom.DocumentFragment;

/**
 * The Class KernelBean.
 */
public class KernelBean implements Kernel, ApplicationContextAware {

    /** The instance ctx. */
    private ApplicationContext instanceCtx;

    /** The participant. */
    private UnifiedParticipantIdentity participant;

    /** The sd resolver. */
    private ServiceDescriptionResolver sdResolver;

    /** The handler registry. */
    private HandlerRegistry handlerRegistry;

    /** The endpoint manager. */
    private EndpointManager endpointManager;

    /** The endpoint reference resolver. */
    private EndpointReferenceResolver endpointReferenceResolver;

    /** The management notification listener. */
    private ManagementNotificationListener managementNotificationListener;

    /** The component context access. */
    private ComponentContextAccess componentContextAccess;

    /** The policy resolver. */
    private PolicyResolver policyResolver;

    /** The policy router. */
    private PolicyRouter policyRouter;

    /** The command factory. */
    private CommandFactory commandFactory;

    /** The uuid generator. */
    private UUIDGenerator uuidGenerator;

    /** The http client configuration. */
    private HttpClientConfigurationBean httpClientConfiguration;

    /** The context store. */
    private ContextStore contextStore;

    /** The location id. */
    private String locationId;

    /** The exchange store. */
    private Map exchangeStore;

    /**
     * the proxyStore is a multimap as we can lookup a set of proxies for a single service. See also
     * lookupAllSrviceProxies
     */
    private MultiMap proxyStore;

    /** The skeleton store. */
    private HashMap skeletonStore;

    /** The environment store. */
    private ConcurrentHashMap environmentStore;

    /** The active. */
    private boolean active;

    /** The instrumentation manager. */
    private InstrumentationManager instrumentationManager;

    /** The instrumentation. */
    private Instrumentation instrumentation;

    /** The local endpoint repository. */
    private LocalEndpointRepository localEndpointRepository;

    /**
     * Utility object to inform interested parties about property changes Currently supported
     * property changes: - participant.
     */
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Instantiates a new kernel bean.
     */
    public KernelBean() {
        this.exchangeStore = new HashMap();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    /**
     * Activate all endpoints.
     * 
     * @param serviceDesc
     *        the service desc
     * 
     * @throws ServiceException
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#activateAllEndpoints(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription)
     */
    public void activateAllEndpoints(final CompoundServiceDescription serviceDesc) throws InternalInfrastructureException {
        try {
            this.endpointManager.activateAllEndpoints(this.getParticipant(), serviceDesc, this.getLocalEndpointRepository(), this
                .getLocationId());
        } catch (JBIException e) {
            throw new InternalInfrastructureException("could not activate endpoint for the service "
                    + serviceDesc.getServiceQName().toString(), e);
        }
    }

    /**
     * Adds the property change listener.
     * 
     * @param listener
     *        the listener
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Adds the property change listener.
     * 
     * @param propertyName
     *        the property name
     * @param listener
     *        the listener
     */
    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Creates the message exchange factory.
     * 
     * @param description
     *        the description
     * 
     * @return the message exchange factory
     * 
     * @throws InternalMessagingException
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#createMessageExchangeFactory(org.eclipse.swordfish.core.components.iapi.OperationDescription)
     */
    public MessageExchangeFactory createMessageExchangeFactory(final OperationDescription description) throws MessagingException {
        DeliveryChannel dc = this.componentContextAccess.getDeliveryChannel();
        DocumentFragment epr = this.endpointReferenceResolver.createEndpointReference(description);
        ServiceEndpoint endpoint = null;
        if (epr != null) {
            endpoint = this.componentContextAccess.resolveEndpointReference(epr);
        } else {
            String epName = this.endpointReferenceResolver.getEndpointNameForOperation(description);
            endpoint = this.componentContextAccess.getEndpoint(description.getServiceDescription().getServiceQName(), epName);
        }
        if (endpoint != null)
            return dc.createExchangeFactory(endpoint);
        else
            throw new MessagingException("cannot resolve an endpoint for " + description.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#createMessageExchangeFactory(org.eclipse.swordfish.core.components.addressing.WSAEndpointReference)
     */
    public MessageExchangeFactory createMessageExchangeFactory(final WSAEndpointReference address) throws MessagingException {
        DocumentFragment epr = this.endpointReferenceResolver.createEndpointReference(address);
        DeliveryChannel dc = this.componentContextAccess.getDeliveryChannel();
        ServiceEndpoint endpoint = this.componentContextAccess.resolveEndpointReference(epr);
        if (endpoint != null)
            return dc.createExchangeFactory(endpoint);
        else
            throw new MessagingException("cannot resolve an endpoint for " + address);
    }

    /**
     * Deactivate all endpoints.
     * 
     * @param serviceDesc
     *        the service desc
     * 
     * @throws ServiceException
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#deactivateAllEndpoints(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription)
     */
    public void deactivateAllEndpoints(final CompoundServiceDescription serviceDesc) throws InternalInfrastructureException {
        try {
            this.endpointManager.deactivateAllEndpoints(this.getParticipant(), serviceDesc, this.getLocalEndpointRepository(), this
                .getLocationId());
        } catch (JBIException e) {
            throw new InternalInfrastructureException("could not deactivate endpoint for the service "
                    + serviceDesc.getServiceQName().toString(), e);
        }
    }

    /**
     * Destroy.
     */
    public void destroy() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#fetchAllServiceDescription(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public Collection fetchAllServiceDescription(final QName service, final String policyName)
            throws InternalServiceDiscoveryException {
        String policyId = "";
        if (policyName != "") {
            policyId = this.policyResolver.resolvePolicyID(policyName);
        } else {
            policyId = this.policyResolver.getDefaultPolicyID();
        }
        return this.sdResolver.fetchAllServiceDescription(service, policyId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#fetchServiceDescription(javax.xml.namespace.QName,
     *      javax.xml.namespace.QName)
     */
    public CompoundServiceDescription fetchServiceDescription(final QName service, final QName providerID)
            throws InternalServiceDiscoveryException {
        String policyId = this.policyResolver.getDefaultConsumerPolicyID();
        return this.sdResolver.fetchServiceDescription(service, providerID, policyId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#fetchServiceDescription(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public CompoundServiceDescription fetchServiceDescription(final QName service, final String policyName)
            throws InternalServiceDiscoveryException {
        String policyId = "";
        if (policyName != "") {
            policyId = this.policyResolver.resolvePolicyID(policyName);
        } else {
            policyId = this.policyResolver.getDefaultPolicyID();
        }
        return this.sdResolver.fetchServiceDescription(service, policyId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#fetchServiceDescriptionWithPolicyId(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public CompoundServiceDescription fetchServiceDescriptionWithPolicyId(final QName service, final String policyId)
            throws InternalServiceDiscoveryException {
        return this.sdResolver.fetchServiceDescription(service, policyId);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#generateUUID()
     */
    public String generateUUID() {
        return this.getUuidGenerator().getUUID("uuid:");
    }

    /**
     * retrives locally stores exchanges.
     * 
     * @param id
     *        the id
     * 
     * @return the and remove exchange
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getAndRemoveExchange(java.lang.String)
     */
    public MessageExchange getAndRemoveExchange(final String id) {
        return (MessageExchange) this.exchangeStore.remove(id);
    }

    /**
     * Gets the command factory.
     * 
     * @return Returns the commandFactory.
     */
    public CommandFactory getCommandFactory() {
        return this.commandFactory;
    }

    /**
     * This method is used by PAPI to return components that are defined for this particular
     * instance to the participant application.<br/> Note that components in order to be queriable
     * from participant application must be Spring beans.<br/> If the bean returned by Spring is an
     * instance of <code>ExtensionFactory</code>, this is used to construct the actual component,
     * taking the diversifier into account. If the bean returned is not an ExtensionFactory, it is
     * returned as is.<br/>
     * 
     * @param interfaceName
     *        the interface name
     * @param diversifier
     *        the diversifier
     * 
     * @return the component
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getComponent(java.lang.Class,
     *      java.lang.String)
     */
    public Object getComponent(final Class interfaceName, final String diversifier) {
        Object bean = this.instanceCtx.getBean(interfaceName.getName());
        if (bean instanceof ExtensionFactory) {
            ExtensionFactory factory = (ExtensionFactory) bean;
            bean = factory.getInstance(diversifier);
        }
        return bean;
    }

    /**
     * Gets the context store.
     * 
     * @return Returns the contextStore.
     */
    public ContextStore getContextStore() {
        return this.contextStore;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getEndpointManager()
     */
    public EndpointManager getEndpointManager() {
        return this.endpointManager;
    }

    /**
     * Gets the endpoint reference resolver.
     * 
     * @return the endpoint reference resolver
     */
    public EndpointReferenceResolver getEndpointReferenceResolver() {
        return this.endpointReferenceResolver;
    }

    /**
     * Gets the environment store.
     * 
     * @return Returns the environmentStore.
     */
    public Map getEnvironmentStore() {
        return this.environmentStore;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getHandlerRegistry()
     */
    public HandlerRegistry getHandlerRegistry() {
        return this.handlerRegistry;
    }

    /**
     * Gets the http client configuration.
     * 
     * @return the http client configuration
     */
    public HttpClientConfigurationBean getHttpClientConfiguration() {
        return this.httpClientConfiguration;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getInstrumentationManager()
     */
    public InstrumentationManager getInstrumentationManager() {
        return this.instrumentationManager;
    }

    /**
     * Gets the local endpoint repository.
     * 
     * @return Returns the localEndpointRepository.
     */
    public LocalEndpointRepository getLocalEndpointRepository() {
        return this.localEndpointRepository;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getLocationId()
     */
    public String getLocationId() {
        return this.locationId;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getManagementNotificationListener()
     */
    public ManagementNotificationListener getManagementNotificationListener() {
        return this.managementNotificationListener;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getParticipant()
     */
    public UnifiedParticipantIdentity getParticipant() {
        return this.participant;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getPolicyResolver()
     */
    public PolicyResolver getPolicyResolver() {
        return this.policyResolver;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getPolicyRouter()
     */
    public PolicyRouter getPolicyRouter() {
        return this.policyRouter;
    }

    /**
     * Gets the proxy store.
     * 
     * @return Returns the proxyStore.
     */
    public MultiMap getProxyStore() {
        return this.proxyStore;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#getSdResolver()
     */
    public ServiceDescriptionResolver getSdResolver() {
        return this.sdResolver;
    }

    /**
     * Gets the skeleton store.
     * 
     * @return Returns the skeletonStore.
     */
    public Map getSkeletonStore() {
        return this.skeletonStore;
    }

    /**
     * Gets the uuid generator.
     * 
     * @return the uuid generator
     */
    public UUIDGenerator getUuidGenerator() {
        return this.uuidGenerator;
    }

    /**
     * Init.
     */
    public void init() {
        this.active = true;
        this.proxyStore = new MultiHashMap();
        this.skeletonStore = new HashMap();
        this.environmentStore = new ConcurrentHashMap();
        this.httpClientConfiguration.configure();
        if (null != this.instrumentationManager) {
            InputStream desc =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "org/eclipse/swordfish/core/components/iapi/impl/KernelBeanInstrumentationDesc.xml");
            this.instrumentation = new Instrumentation();
            try {
                this.instrumentationManager.registerInstrumentation(this.instrumentation, desc,
                        "org.eclipse.swordfish.core.components.iapi.impl.KernelBeanInstrumentation");
            } catch (InternalInfrastructureException e) {
                e.printStackTrace();
                // no logging in Kernel implementation????
                // !TODO: log exception, state instrumenation will not be
                // available
            } catch (InternalAlreadyRegisteredException e) {
                // TODO do some log warning and ignore it.
                ;
            }
        }
    }

    /**
     * Checks if is active.
     * 
     * @return true, if is active
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#isActive()
     */
    public boolean isActive() {
        return this.active;
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
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#registerMessageHandler(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.papi.untyped.IncomingMessageHandler)
     */
    public void registerMessageHandler(final QName serviceName, final String operationName, final Role role,
            final IncomingMessageHandlerProxy handler) throws MessageHandlerRegistrationException {
        this.handlerRegistry.associate(role, serviceName, operationName, handler);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#removeCallContext(java.lang.String)
     */
    public void removeCallContext(final String key) {
        ContextStore ctxStore = this.getContextStore();
        if (ctxStore == null)
            throw new InfrastructureRuntimeException("could not get any context store");
        else {
            ctxStore.removeCallContext(key);
        }
    }

    /**
     * Removes the property change listener.
     * 
     * @param listener
     *        the listener
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Removes the property change listener.
     * 
     * @param propertyName
     *        the property name
     * @param listener
     *        the listener
     */
    public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#restoreCallContext(java.lang.String)
     */
    public CallContextExtension restoreCallContext(final String key) throws InternalIllegalInputException,
            InternalInfrastructureException {
        ContextStore ctxStore = this.getContextStore();
        if (ctxStore == null)
            throw new InternalConfigurationException("could not get any context store");
        else
            return ctxStore.restoreCallContext(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#sendOperationStateNotification(org.eclipse.swordfish.core.management.notification.EntityState,
     *      org.eclipse.swordfish.papi.untyped.InternalOperation,
     *      org.eclipse.swordfish.core.management.notification.ParticipantRole)
     */
    public void sendOperationStateNotification(final EntityState state, final InternalOperation op, final ParticipantRole role) {
        this.getManagementNotificationListener().sendNotification(
                new OperationStateNotification(state, op, this.getParticipant(), role));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(final ApplicationContext instCtx) {
        this.instanceCtx = instCtx;
    }

    /**
     * Sets the command factory.
     * 
     * @param commandFactory
     *        The commandFactory to set.
     */
    public void setCommandFactory(final CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * Sets the component context access.
     * 
     * @param componentContextAccess
     *        the new component context access
     */
    public void setComponentContextAccess(final ComponentContextAccess componentContextAccess) {
        this.componentContextAccess = componentContextAccess;
    }

    /**
     * Sets the context store.
     * 
     * @param contextStore
     *        The contextStore to set. Spring injection point
     */
    public void setContextStore(final ContextStore contextStore) {
        this.contextStore = contextStore;
    }

    /**
     * Sets the endpoint manager.
     * 
     * @param endpointManager
     *        the new endpoint manager
     */
    public void setEndpointManager(final EndpointManager endpointManager) {
        this.endpointManager = endpointManager;
    }

    /**
     * Sets the endpoint reference resolver.
     * 
     * @param endpointReferenceResolver
     *        the new endpoint reference resolver
     */
    public void setEndpointReferenceResolver(final EndpointReferenceResolver endpointReferenceResolver) {
        this.endpointReferenceResolver = endpointReferenceResolver;
    }

    /**
     * Sets the handler registry.
     * 
     * @param handlerRegistry
     *        the new handler registry
     */
    public void setHandlerRegistry(final HandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    /**
     * Sets the http client configuration.
     * 
     * @param httpClientConfiguration
     *        the new http client configuration
     */
    public void setHttpClientConfiguration(final HttpClientConfigurationBean httpClientConfiguration) {
        this.httpClientConfiguration = httpClientConfiguration;
    }

    /**
     * Sets the instrumentation manager.
     * 
     * @param instrumentationManager
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManager instrumentationManager) {
        this.instrumentationManager = instrumentationManager;
    }

    /**
     * Sets the local endpoint repository.
     * 
     * @param localEndpointRepository
     *        The localEndpointRepository to set.
     */
    public void setLocalEndpointRepository(final LocalEndpointRepository localEndpointRepository) {
        this.localEndpointRepository = localEndpointRepository;
    }

    /**
     * Sets the location id.
     * 
     * @param locationId
     *        The locationId to set.
     */
    public void setLocationId(final String locationId) {
        this.locationId = locationId;
    }

    /**
     * Sets the management notification listener.
     * 
     * @param managementNotificationListener
     *        the new management notification listener
     */
    public void setManagementNotificationListener(final ManagementNotificationListener managementNotificationListener) {
        this.managementNotificationListener = managementNotificationListener;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#setParticipant(org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity)
     */
    public void setParticipant(final UnifiedParticipantIdentity participant) {
        UnifiedParticipantIdentity oldParticipant = this.participant;
        this.participant = participant;
        if (null != this.propertyChangeSupport) {
            this.propertyChangeSupport.firePropertyChange("participant", oldParticipant, this.participant);
        }
    }

    /**
     * Sets the policy resolver.
     * 
     * @param policyResolver
     *        the new policy resolver
     */
    public void setPolicyResolver(final PolicyResolver policyResolver) {
        this.policyResolver = policyResolver;
    }

    /**
     * Sets the policy router.
     * 
     * @param policyRouter
     *        the new policy router
     */
    public void setPolicyRouter(final PolicyRouter policyRouter) {
        this.policyRouter = policyRouter;
    }

    /**
     * Sets the sd resolver.
     * 
     * @param sdResolver
     *        the new sd resolver
     */
    public void setSdResolver(final ServiceDescriptionResolver sdResolver) {
        this.sdResolver = sdResolver;
    }

    /**
     * Sets the uuid generator.
     * 
     * @param uuidGenerator
     *        the new uuid generator
     */
    public void setUuidGenerator(final UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#shutdown()
     */
    public void shutdown() {
        this.active = false;
        /*
         * clear the stores
         */
        this.getEnvironmentStore().clear();

        while (this.proxyStore.size() != 0) {
            Iterator proxyStoreIterator = this.proxyStore.keySet().iterator();
            if (proxyStoreIterator.hasNext()) {
                Collection col = (Collection) this.proxyStore.get(proxyStoreIterator.next());
                Iterator iter = col.iterator();
                while (iter.hasNext()) {
                    try {
                        ((ServiceProxyImpl) iter.next()).cleanup(true);
                    } catch (InternalSBBException e) {
                        // no logging in Kernel implementation????
                        // !TODO: log exception, state instrumenation will not be
                        // available
                    }
                }
            }
        }

        /*
         * Iterator proxyStoreIterator = proxyStore.keySet().iterator(); while
         * (proxyStoreIterator.hasNext()) { Collection col = (Collection)
         * proxyStore.get(proxyStoreIterator.next()); Iterator iter = col.iterator(); while
         * (iter.hasNext()) { ((ServiceProxyImpl) iter.next()).cleanup(true); } }
         * proxyStore.clear();
         */

        while (this.skeletonStore.size() != 0) {
            Iterator skeletonStoreIterator = this.skeletonStore.values().iterator();
            if (skeletonStoreIterator.hasNext()) {
                Object o = skeletonStoreIterator.next();
                try {
                    ((ServiceSkeletonImpl) o).cleanup(true);
                } catch (InternalSBBException e) {
                    // no logging in Kernel implementation????
                    // !TODO: log exception, state instrumenation will not be
                    // available
                }
            }
        }

        /*
         * Iterator skeletonStoreIterator = skeletonStore.values().iterator(); while
         * (skeletonStoreIterator.hasNext()) { Object o = skeletonStoreIterator.next();
         * ((ServiceSkeletonImpl) o).cleanup(true); } skeletonStore.clear();
         */

        this.getManagementNotificationListener().sendNotification(
                new ParticipantStateNotification(this.getParticipant(), EntityState.REMOVED));
        // must happen before destroying Spring contexts, otherwise danger of deadlock.
        this.getManagementNotificationListener().deactivate();

        if ((null != this.instrumentationManager) && (null != this.instrumentation)) {
            try {
                this.instrumentationManager.unregisterInstrumentation(this.instrumentation);
            } catch (InternalInfrastructureException e) {
                e.printStackTrace();
                // no logging in Kernel implementation????
                // !TODO: log exception, state instrumenation will not be
                // available
            }
        }

        this.proxyStore = null;
        this.skeletonStore = null;
        this.environmentStore = null;

        // close the context and than close the parent context.
        // Fixes:#outOfMemoryError
        ((AbstractApplicationContext) this.instanceCtx).destroy();
        ((AbstractApplicationContext) this.instanceCtx).close();
        ((AbstractApplicationContext) this.instanceCtx.getParent()).destroy();
        ((AbstractApplicationContext) this.instanceCtx.getParent()).close();
        this.instanceCtx = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#storeCallContext(org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension)
     */
    public String storeCallContext(final CallContextExtension callContext) throws InternalIllegalInputException,
            InternalInfrastructureException {
        ContextStore ctxStore = this.getContextStore();
        if (ctxStore == null)
            throw new InternalConfigurationException("could not get any context store");
        else
            return ctxStore.storeCallContext(callContext);
    }

    /**
     * stores local IOExchanges.
     * 
     * @param exchange
     *        the exchange
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#storeExchange(javax.jbi.messaging.MessageExchange)
     */
    public void storeExchange(final MessageExchange exchange) {
        this.exchangeStore.put(exchange.getExchangeId(), exchange);
    }

    /**
     * Unregister message handler.
     * 
     * @param serviceName
     *        the service name
     * @param operationName
     *        the operation name
     * @param role
     *        the role
     * @param sbbInitiated
     *        the sbb initiated
     * 
     * @throws MessageHandlerRegistrationException
     * 
     * @see org.eclipse.swordfish.core.components.iapi.Kernel#unregisterMessageHandler(javax.xml.namespace.QName,
     *      java.lang.String, org.eclipse.swordfish.core.components.iapi.Role, boolean)
     */
    public void unregisterMessageHandler(final QName serviceName, final String operationName, final Role role,
            final boolean sbbInitiated) throws MessageHandlerRegistrationException {
        this.handlerRegistry.remove(role, serviceName, operationName, sbbInitiated);
    }

    /**
     * The Class Instrumentation.
     */
    public class Instrumentation {

        /**
         * Gets the active.
         * 
         * @return the active
         */
        public Boolean getActive() {
            return new Boolean(KernelBean.this.active);
        }

        /**
         * Gets the endpoint manager.
         * 
         * @return the endpoint manager
         */
        public ObjectName getEndpointManager() {
            ObjectName ret = null;
            if (KernelBean.this.endpointManager instanceof EndpointManagerBean) {
                EndpointManagerBean epm = (EndpointManagerBean) KernelBean.this.endpointManager;
                Object epmInstrumentation = epm.getInstrumentation();
                ret = KernelBean.this.instrumentationManager.getObjectName(epmInstrumentation);
            }
            return ret;
        }

        /**
         * Gets the location id.
         * 
         * @return the location id
         */
        public String getLocationId() {
            return KernelBean.this.locationId;
        }

        /**
         * Gets the participant id.
         * 
         * @return the participant id
         */
        public String getParticipantId() {
            return KernelBean.this.getParticipant().toString();
        }

    }

}
