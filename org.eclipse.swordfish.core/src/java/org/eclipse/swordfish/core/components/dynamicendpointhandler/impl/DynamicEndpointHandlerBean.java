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
package org.eclipse.swordfish.core.components.dynamicendpointhandler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jbi.management.AdminServiceMBean;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.BCAdapter;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicEndpointHandler;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpoint;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicInboundEndpointDeploymentException;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.EndpointProperties;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.InvalidAddressException;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.locatorproxy.LocatorProxy;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.core.utils.jmx.NamingStrategyFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * The Class DynamicEndpointHandlerBean.
 */
public class DynamicEndpointHandlerBean implements DynamicEndpointHandler, BeanFactoryAware {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(DynamicEndpointHandlerBean.class);

    /** The component context access. */
    private ComponentContextAccess componentContextAccess;

    /** The locator proxy. */
    private LocatorProxy locatorProxy;

    /** The initialized. */
    private boolean initialized = false;

    /** The mbean server. */
    private MBeanServer mbeanServer = null;

    /** The bc adapter mapping. */
    private Map bcAdapterMapping;

    /** The bc adapters. */
    private List bcAdapters = new ArrayList();

    /** The started inbound endpoints. */
    private Map startedInboundEndpoints = new HashMap();

    /** The started notification endpoints. */
    private Map startedNotificationEndpoints = new HashMap();

    /** The bean factory. */
    private BeanFactory beanFactory;

    /** The endpoint properties map. */
    private Map endpointPropertiesMap;

    /**
     * Instantiates a new dynamic endpoint handler bean.
     */
    public DynamicEndpointHandlerBean() {
        this.endpointPropertiesMap = new HashMap();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicEndpointHandler#deployDynamicEndpoint(javax.xml.namespace.QName,
     *      org.eclipse.swordfish.core.components.srproxy.SPDXPort, boolean, java.lang.String,
     *      java.util.Map)
     */
    public void deployDynamicEndpoint(final QName serviceName, final SPDXPort port, final boolean isUsingLocator,
            final String locationId, final Map configMap) {
        if (!this.isInitialized()) {
            this.init();
        }
        // merge all properties for the deployment of this endpoint
        Map propertiesMap = new HashMap();
        propertiesMap.putAll(this.endpointPropertiesMap);
        if (configMap != null) {
            propertiesMap.putAll(configMap);
        }

        DynamicInboundEndpoint dynamicInboundEndpoint = null;
        try {
            dynamicInboundEndpoint = AbstractDynamicEndpoint.newInstance(serviceName, port, propertiesMap);
        } catch (InvalidAddressException e) {
            LOG.error("The dynamic inbound endpoint could not be" + " started because its address is invalid.", e);
        }
        boolean isStarted = false;
        boolean isSupported = false;
        for (Iterator iter = this.bcAdapters.iterator(); iter.hasNext();) {
            BCAdapter bcAdapter = (BCAdapter) iter.next();
            if (bcAdapter.canHandle(dynamicInboundEndpoint)) {
                isSupported = true;
                try {
                    bcAdapter.startDynamicInboundEndpoint(dynamicInboundEndpoint);
                    this.startedInboundEndpoints.put(dynamicInboundEndpoint.getId(), bcAdapter);
                    isStarted = true;
                    break;
                } catch (DynamicInboundEndpointDeploymentException e) {
                    LOG.warn("Dynamic inbound endpoint deployment failed for binding component " + bcAdapter.getBCName(), e);
                }
            }
        }
        if (!isSupported) {

            LOG.error("The dynamic inbound endpoint could not be" + " started because it is not supported by any"
                    + " of the installed binding components.");
            return;
        }
        if (isStarted) {
            if (isUsingLocator && this.getLocatorProxy().isActive()) {
                try {
                    this.getLocatorProxy().register(locationId, dynamicInboundEndpoint.getServiceQName(), port.getName(),
                            dynamicInboundEndpoint.getAddressFragment());
                    LOG.debug("Dynamic inbound endpoint "
                            + TransformerUtil.stringFromDomNode(dynamicInboundEndpoint.getAddressFragment())
                            + " registered to locator at " + locationId);
                } catch (InternalInfrastructureException e) {
                    LOG.warn("Dynamic inbound endpoint "
                            + TransformerUtil.stringFromDomNode(dynamicInboundEndpoint.getAddressFragment())
                            + " was started locally, but could not be registered to " + "the locator in " + locationId
                            + ". The endpoint will not be discoverable!", e);
                }
            }
        } else {
            LOG.error("Starting the dynamic inbound endpoint failed" + ""
                    + " for all binding components. Please check configuration.");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicEndpointHandler#deployDynamicNotificationEndpoint(javax.xml.namespace.QName,
     *      org.eclipse.swordfish.core.components.srproxy.SPDXPort, java.lang.String,
     *      org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository,
     *      org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity, java.util.Map)
     */
    public void deployDynamicNotificationEndpoint(final QName serviceName, final SPDXPort port, final String operationName,
            final LocalEndpointRepository repos, final UnifiedParticipantIdentity participant, final Map configMap) {

        if (!this.isInitialized()) {
            this.init();
        }

        // merge all properties for the deployment of this endpoint
        Map propertiesMap = new HashMap();
        propertiesMap.putAll(this.endpointPropertiesMap);
        if (configMap != null) {
            propertiesMap.putAll(configMap);
        }

        DynamicInboundEndpoint dynamicNotificationEndpoint = null;
        try {
            dynamicNotificationEndpoint =
                    AbstractDynamicEndpoint.newNotificationInstance(serviceName, repos, port, operationName, participant,
                            propertiesMap);
        } catch (InvalidAddressException e) {
            LOG.error("The dynamic notification  endpoint could not be" + " started because its address is invalid.", e);
        }
        boolean isSupported = false;
        for (Iterator iter = this.bcAdapters.iterator(); iter.hasNext();) {
            BCAdapter bcAdapter = (BCAdapter) iter.next();
            if (bcAdapter.canHandle(dynamicNotificationEndpoint)) {
                isSupported = true;
                try {
                    bcAdapter.startDynamicInboundEndpoint(dynamicNotificationEndpoint);
                    this.startedNotificationEndpoints.put(dynamicNotificationEndpoint.getId(), bcAdapter);
                    break;
                } catch (DynamicInboundEndpointDeploymentException e) {
                    LOG.warn("Dynamic notification endpoint deployment failed for binding component " + bcAdapter.getBCName(), e);
                }
            }
        }
        if (!isSupported) {
            LOG.error("The dynamic notification endpoint could not be started "
                    + "because it is not supported by any of the installed binding components.");
            return;
        }
    }

    /**
     * destroy method.
     */
    public void destroy() {
        this.bcAdapters.clear();
        this.componentContextAccess = null;
        this.locatorProxy = null;
    }

    /**
     * Gets the binding processing timeout.
     * 
     * @return -1 if the value is non existant, or the specified int value
     */
    public int getBindingProcessingTimeout() {
        return this.endpointPropertiesMap.get(EndpointProperties.TIMEOUT) == null ? -1 : ((Integer) this.endpointPropertiesMap
            .get(EndpointProperties.TIMEOUT)).intValue();
    }

    /**
     * Gets the component context access.
     * 
     * @return the component context access
     */
    public ComponentContextAccess getComponentContextAccess() {
        return this.componentContextAccess;
    }

    /**
     * Gets the locator proxy.
     * 
     * @return Returns the locatorProxy.
     */
    public LocatorProxy getLocatorProxy() {
        return this.locatorProxy;
    }

    /**
     * Gets the mbean server.
     * 
     * @return the mbean server
     */
    public MBeanServer getMbeanServer() {
        return this.mbeanServer;
    }

    /**
     * Checks if is initialized.
     * 
     * @return true, if is initialized
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Sets the bc adapter mapping.
     * 
     * @param bcAdapterMapping
     *        the new bc adapter mapping
     */
    public void setBcAdapterMapping(final Map bcAdapterMapping) {
        this.bcAdapterMapping = bcAdapterMapping;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;

    }

    /**
     * Sets the binding processing timeout.
     * 
     * @param timeout
     *        the new binding processing timeout
     */
    public void setBindingProcessingTimeout(final int timeout) {
        this.endpointPropertiesMap.put(EndpointProperties.TIMEOUT, new Integer(timeout));
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
     * Sets the initialized.
     * 
     * @param initialized
     *        the new initialized
     */
    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Sets the locator proxy.
     * 
     * @param locatorProxy
     *        The locatorProxy to set.
     */
    public void setLocatorProxy(final LocatorProxy locatorProxy) {
        this.locatorProxy = locatorProxy;
    }

    /**
     * Sets the mbean server.
     * 
     * @param mbeanServer
     *        the new mbean server
     */
    public void setMbeanServer(final MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicEndpointHandler#undeployDynamicEndpoint(javax.jbi.servicedesc.ServiceEndpoint,
     *      org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription,
     *      org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository,
     *      java.lang.String)
     */
    public void undeployDynamicEndpoint(final ServiceEndpoint sep, final CompoundServiceDescription serviceDesc,
            final LocalEndpointRepository repos, final String locationId) {

        if (!this.isInitialized()) {
            this.init();
        }
        String id = AbstractDynamicEndpoint.buildEndpointIdentifier(sep.getServiceName(), sep.getEndpointName(), null);
        if (this.startedInboundEndpoints.containsKey(id)) {
            BCAdapter bcAdapter = (BCAdapter) this.startedInboundEndpoints.get(id);
            try {
                bcAdapter.stopDynamicInboundEndpoint(id);
            } catch (DynamicInboundEndpointDeploymentException e) {
                LOG.warn("Stopping dynamic inbound endpoint failed for binding component " + bcAdapter.getBCName(), e);
            }
            if (this.getLocatorProxy().isActive() && serviceDesc.getPort(sep.getEndpointName()).isUsingLocator()) {
                try {
                    this.getLocatorProxy().unregister(locationId, sep.getServiceName(), sep.getEndpointName());
                    LOG.debug(id + " unregistered from locator at " + locationId);
                } catch (InternalInfrastructureException e) {
                    LOG.warn("Dynamic inbound endpoint " + sep.toString()
                            + " was stopped locally, but could not be unregistered from " + "the locator in " + locationId
                            + ". The endpoint is still discoverable! while the " + "provider is not accessible any more", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicEndpointHandler#undeployDynamicNotificationEndpoint(javax.xml.namespace.QName,
     *      java.lang.String, java.lang.String,
     *      org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository,
     *      org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity)
     */
    public void undeployDynamicNotificationEndpoint(final QName wsdlServiceName, final String wsdlPortName,
            final String operationName, final LocalEndpointRepository repos, final UnifiedParticipantIdentity participant) {

        if (!this.isInitialized()) {
            this.init();
        }
        String id = AbstractDynamicEndpoint.buildEndpointIdentifier(wsdlServiceName, wsdlPortName, participant);
        if (this.startedNotificationEndpoints.containsKey(id)) {
            BCAdapter bcAdapter = (BCAdapter) this.startedNotificationEndpoints.get(id);
            try {
                bcAdapter.stopDynamicInboundEndpoint(id);
            } catch (DynamicInboundEndpointDeploymentException e) {
                LOG.warn("Stopping dynamic notification endpoint failed for binding component " + bcAdapter.getBCName(), e);
            }
        }
    }

    /**
     * Init.
     */
    void init() {
        // get list of installed binding components from JBI admin mbeans
        // TODO TO BE VERIFIED this name is generic for all JBI implementations
        ObjectName adminServiceMBeanName;
        try {
            adminServiceMBeanName =
                    new ObjectName(this.componentContextAccess.getMBeanNames().getJmxDomainName()
                            + ":JBIServiceName=AdminService,ControlType=AdminService");
        } catch (MalformedObjectNameException ex) {
            LOG.error("Error creating JMX ObjectName for JBI AdminService.", ex);
            throw new RuntimeException("Error creating JMX ObjectName for JBI AdminService.", ex);
        }
        final ObjectName adminServiceMBeanNameNS = NamingStrategyFactory.getNamingStrategy().getObjectName(adminServiceMBeanName);
        AdminServiceMBean adminServiceMBean =
                (AdminServiceMBean) MBeanServerInvocationHandler.newProxyInstance(this.mbeanServer, adminServiceMBeanNameNS,
                        javax.jbi.management.AdminServiceMBean.class, true);
        ObjectName bcs[] = adminServiceMBean.getBindingComponents();
        // iterate over the list of installed binding components
        // and create the corresponding BC adapters
        for (int i = 0; i < bcs.length; i++) {
            final ObjectName simpleBCObjectName = NamingStrategyFactory.getNamingStrategy().getSimpleObjectName(bcs[i]);
            if ((simpleBCObjectName != null) && this.bcAdapterMapping.containsKey(simpleBCObjectName.getCanonicalName())) {
                String bcAdapterName = (String) this.bcAdapterMapping.get(simpleBCObjectName.getCanonicalName());
                BCAdapter bcAdapter = (BCAdapter) this.beanFactory.getBean(bcAdapterName);
                if (null != bcAdapter) {
                    ObjectName extensionMBeanNameNS = null;
                    try {
                        // try to find an extension mbean
                        ObjectName extensionMBeanName =
                                (ObjectName) ((MBeanServerConnection) this.mbeanServer).getAttribute(bcs[i], "ExtensionMBeanName");
                        extensionMBeanNameNS = NamingStrategyFactory.getNamingStrategy().getObjectName(extensionMBeanName);
                        if (null != extensionMBeanNameNS) {
                            // if found, add the BC adapter to the list of
                            // active BC adapters
                            bcAdapter.setExtensionMBeanName(extensionMBeanNameNS);
                            bcAdapter.setBCName(bcs[i].getCanonicalName());
                            this.bcAdapters.add(bcAdapter);
                            LOG.debug("BC Adapter for BC " + bcAdapter.getBCName() + " initialized and added to adapter list.");
                        } else {
                            LOG.warn("No Extension MBean found for BC " + bcAdapter.getBCName()
                                    + ", dynamic endpoint deployment is not supported on this BC.");
                        }
                    } catch (Exception e) {
                        LOG.warn("Error accessing the Lifecycle MBean for BC " + bcAdapter.getBCName()
                                + ", dynamic endpoint deployment will be disabled for BC.");
                    }
                }
            }
        }
        if (this.bcAdapters.isEmpty()) {
            LOG.error("No binding component supporting dynamic endpoint " + "deployment is available in the JBI container. "
                    + "The InternalSBB library is not going to work correctly." + " Please check your installation!");
        }
        this.setInitialized(true);
    }
}
