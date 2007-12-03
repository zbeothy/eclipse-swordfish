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
package org.eclipse.swordfish.core.components.endpointmanager.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.jbi.JBIException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.ws.policy.All;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.DynamicEndpointHandler;
import org.eclipse.swordfish.core.components.dynamicendpointhandler.EndpointProperties;
import org.eclipse.swordfish.core.components.endpointmanager.EndpointManager;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.iapi.Transport;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.components.srproxy.impl.SPDXPortImpl;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;
import org.eclipse.swordfish.policy.selector.ClassSelector;
import org.eclipse.swordfish.policy.selector.NameSelector;
import org.eclipse.swordfish.policy.util.TermIterator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Responsible for registering endpoint mappings and activation and deactivation of endpoints on the
 * JBI component context.
 */
public class EndpointManagerBean implements EndpointManager, BeanFactoryAware {

    private static final String ENDPOINT_MANAGER_BEAN =
            "org.eclipse.swordfish.core.components.endpointmanager.impl.EndpointManagerBean";

    private static final String ENDPOINT_MANAGER_BEAN_INSTRUMENTATION_DESC =
            "org/eclipse/swordfish/core/components/endpointmanager/impl/EndpointManagerBeanInstrumentationDesc.xml";

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(EndpointManagerBean.class);

    /**
     * access to the JBI component context for endpoint activation. TODO provide information about
     * the WSDL data of the regitered endpoints through component context
     */
    private ComponentContextAccess componentContextAccess;

    /**
     * the DynamicEndpointHandler handles activation of dynamic endpoints in the binding component.
     */
    private DynamicEndpointHandler dynamicEndpointHandler;

    /**
     * Mappings from (WSDLServiceQName, operationName, WSDLportName) -> ServiceEndpoint.
     */
    private ConcurrentMap endpointMapping = new ConcurrentHashMap();

    /**
     * Mappings from (WSDLserviceQName, operationName, WSDLPortName) -> InternalParticipantIdentity.
     */
    private ConcurrentMap participantMapping = new ConcurrentHashMap();

    /** Mappings from (WSDLserviceQName, WSDLPortName) -> ServiceEndpoint. */
    private ConcurrentMap activatedEndpoints = new ConcurrentHashMap();

    /**
     * Mappings from (WSDLserviceQName, WSDLPortName) -> AggregateServiceDescription.
     */
    private ConcurrentMap serviceDescriptions = new ConcurrentHashMap();

    /**
     * Mappings from (se) -> #participants opened that port this is only usefull for notification
     * ports.
     */
    private ConcurrentMap endPointCount = new ConcurrentHashMap();

    /** to register and unregister this object's management instrumentation. */
    private InstrumentationManager instrumentationManager;

    /** The instrumentation. */
    private Object instrumentation;

    /** The bean factory. */
    private BeanFactory beanFactory;

    /**
     * Activate all endpoints.
     * 
     * @param participant
     *        the participant
     * @param serviceDesc
     *        the service desc
     * @param repos
     *        the repos
     * @param locationId
     *        the location id
     * 
     * @throws JBIException
     * 
     * @see org.eclipse.swordfish.core.components.endpointmanager.EndpointManager#
     *      activateAllEndpoints(org.eclipse.swordfish.core.components.iapi.CompoundServiceDescription,
     *      java.lang.String, org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity)
     */
    public void activateAllEndpoints(final UnifiedParticipantIdentity participant, final CompoundServiceDescription serviceDesc,
            final LocalEndpointRepository repos, final String locationId) throws JBIException {

        QName wsdlServiceName = serviceDesc.getServiceQName();
        Map localPorts = repos.getSPDXPortsForServiceName(wsdlServiceName);
        SPDXPort[] ports = this.preSelectPortsForActivation(serviceDesc, localPorts);
        // FIXES the NPE for JMS Endpoints when there are messages inside the
        // queue
        this.serviceDescriptions.put(wsdlServiceName, serviceDesc);

        for (int i = 0; i < ports.length; i++) {
            // build the magic key
            MultiKey magicKey = this.buildKey(wsdlServiceName, ports[i].getName());
            if (!this.participantMapping.containsKey(magicKey)) {
                ServiceEndpoint se = this.getActiveEndpoint(wsdlServiceName, ports[i].getName(), ports[i].getTransport());
                if (se == null) {
                    se = this.activateEndpoint(wsdlServiceName, ports[i].getName(), ports[i].getTransport());
                    // open external endpoint only for non-jbi transports
                    if (!Transport.JBI.equals(ports[i].getTransport())) {
                        /*
                         * for the service description the inbound endpoints (Provider side and
                         * consumer callback side) are defined in the consfuguration and therefore
                         * accesible through the LocalEndpointRepository. So the information in the
                         * spdx is worthless in this case.
                         */
                        SPDXPort thePort = this.getSPDXPortWithLocallyConfiguredTransport(ports[i], localPorts);
                        boolean usingLocator = thePort.isUsingLocator();
                        Map props = new HashMap();
                        props.put(EndpointProperties.CSD, serviceDesc);
                        if (thePort.isShared()) {
                            String selector = this.buildSelectorExpression(participant);
                            props.put(EndpointProperties.SELECTOR, selector);
                        }
                        this.dynamicEndpointHandler
                            .deployDynamicEndpoint(wsdlServiceName, thePort, usingLocator, locationId, props);
                    }
                }
                this.participantMapping.put(magicKey, participant);
                this.endpointMapping.put(magicKey, se);
                LOG.debug("registered " + wsdlServiceName.toString() + ":" + " for participant " + participant.toString()
                        + " on port " + ports[i].getName());
            } else {
                UnifiedParticipantIdentity aParticipant = (UnifiedParticipantIdentity) this.participantMapping.get(magicKey);
                if (aParticipant.equals(participant))
                    throw new ComponentRuntimeException("Operation endpoints have already been activated.");
                else {
                    LOG.warn("participant " + aParticipant + " already preoccupied port" + ports[i] + " for " + wsdlServiceName
                            + ":" + " . Port usage clashes for participant " + participant);
                }
            }
        }
    }

    /**
     * Activate notification endpoint.
     * 
     * @param participant
     *        the participant
     * @param serviceDesc
     *        the service desc
     * @param operationName
     *        the operation name
     * @param repos
     *        the repos
     * 
     * @throws JBIException
     */
    public void activateNotificationEndpoint(final UnifiedParticipantIdentity participant,
            final CompoundServiceDescription serviceDesc, final String operationName, final LocalEndpointRepository repos)
            throws JBIException {

        QName wsdlServiceName = serviceDesc.getServiceQName();
        // find the port to activate an endpoint for
        // currently, notifications are supported over JMS exclusively.
        // if the notification operation is bound to more than one JMS port, we
        // currently use the
        // first one found and log the condition .
        // if the notification operation is not bound to any JMS port, we throw
        // an exception
        SPDXPort ports[] = serviceDesc.getSupportedPorts(operationName);
        SPDXPort port = null;
        Transport wsdlPortTransport = null;
        for (int i = 0; i < ports.length; i++) {
            SPDXPort tempport = ports[i];
            Transport tp = tempport.getTransport();
            if (Transport.JMS.equals(tp)) {
                if (null == port) {
                    port = tempport;
                    wsdlPortTransport = tp;
                } else {
                    LOG.warn("Notification operation " + operationName + " bound to more than one JMS port. Using "
                            + port.getName());
                }
            }
        }
        if (null == port) {
            LOG.error("Notification operation " + operationName + " is not bound to an JMS port.");
            throw new JBIException("No port available to register notification " + operationName + " for service "
                    + wsdlServiceName.toString() + ":" + " for participant " + participant.toString());
        }
        String endpointName = port.getName() + "-" + participant.getReproducibleHash();
        ServiceEndpoint se = this.getActiveEndpoint(wsdlServiceName, endpointName, wsdlPortTransport);
        if (se == null) {
            se = this.activateEndpoint(wsdlServiceName, endpointName, wsdlPortTransport);
            this.endPointCount.put(se, new Integer(0));
        }
        // FIXES the NPE for JMS Endpoints when there are messages inside the
        // queue
        this.serviceDescriptions.put(wsdlServiceName, serviceDesc);
        Map props = new HashMap();
        props.put(EndpointProperties.DURABLE, new Boolean(this.isDurable(serviceDesc, operationName)));
        this.dynamicEndpointHandler.deployDynamicNotificationEndpoint(wsdlServiceName, port, operationName, repos, participant,
                props);
        MultiKey magicKey = this.buildKey(wsdlServiceName, endpointName);
        this.participantMapping.put(magicKey, participant);
        // increment the count
        this.endPointCount.put(se, new Integer(((Integer) this.endPointCount.get(se)).intValue() + 1));

        LOG.debug("registered notification " + operationName + " for service " + wsdlServiceName.toString() + ":"
                + " for participant " + participant.toString() + " on port " + endpointName);
    }

    /**
     * Deactivate all endpoints.
     * 
     * @param participant
     *        the participant
     * @param serviceDesc
     *        the service desc
     * @param repos
     *        the repos
     * @param locationId
     *        the location id
     * 
     * @throws JBIException
     * 
     * @see org.eclipse.swordfish.core.components.endpointmanager.EndpointManager#
     *      deactivateAllEndpoints(org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity,
     *      org.eclipse.swordfish.core.components.iapi.AggregateServiceDescription,
     *      java.lang.String)
     */
    // NOTE We doe not remove the service descriptions from the service desc map
    // when unregistering the endpoints. Is it going to cause trouble?
    public void deactivateAllEndpoints(final UnifiedParticipantIdentity participant, final CompoundServiceDescription serviceDesc,
            final LocalEndpointRepository repos, final String locationId) throws JBIException {

        QName wsdlServiceName = serviceDesc.getServiceQName();
        /*
         * for the service description the inbound endpoints (Provider side and consumer callback
         * side) are defined in the consfuguration and therefore accesible through the
         * LocalEndpointRepository. So the information in the spdx is worthless in this case.
         */
        SPDXPort[] ports = serviceDesc.getPorts();

        Map endpointDeactivationCheckList = new HashMap();
        for (int i = 0; i < ports.length; i++) {
            if (serviceDesc.isNotificationOnlyPort(ports[i].getName())) {
                continue;
            }
            /*
             * if (!localPorts.containsKey(ports[i].getName())) { continue; }
             */

            // build the magic key
            MultiKey magicKey = this.buildKey(wsdlServiceName, ports[i].getName());
            this.participantMapping.remove(magicKey);
            ServiceEndpoint sep = (ServiceEndpoint) this.endpointMapping.remove(magicKey);
            // remember the deleted value
            if (sep != null) {
                endpointDeactivationCheckList.put(sep, this.buildKey(wsdlServiceName, ports[i].getName(), ports[i].getTransport()));
            }
        }
        /*
         * now check which endpoints can be deactivated
         */
        Iterator iter = endpointDeactivationCheckList.keySet().iterator();
        while (iter.hasNext()) {
            ServiceEndpoint sep = (ServiceEndpoint) iter.next();
            if (!this.endpointMapping.containsValue(sep)) {
                MultiKey aKey = (MultiKey) endpointDeactivationCheckList.get(sep);
                this.activatedEndpoints.remove(aKey);
                Transport trapo = (Transport) aKey.getKey(2);
                if (!Transport.JBI.equals(trapo)) {
                    this.dynamicEndpointHandler.undeployDynamicEndpoint(sep, serviceDesc, repos, locationId);
                }
                this.componentContextAccess.deactivateEndpoint(sep);
                LOG.debug("service endpoint " + sep + " deactivated.");
            }
        }
    }

    /**
     * Deactivate notification endpoint.
     * 
     * @param participant
     *        the participant
     * @param serviceDesc
     *        the service desc
     * @param operationName
     *        the operation name
     * @param repos
     *        the repos
     * 
     * @throws JBIException
     */
    public void deactivateNotificationEndpoint(final UnifiedParticipantIdentity participant,
            final CompoundServiceDescription serviceDesc, final String operationName, final LocalEndpointRepository repos)
            throws JBIException {

        QName wsdlServiceName = serviceDesc.getServiceQName();
        // TODO make sure that this is the only port !!!
        String wsdlPortName = serviceDesc.getSupportedPorts(operationName)[0].getName();
        String wsdlPortNameForThisClass =
                serviceDesc.getSupportedPorts(operationName)[0].getName() + "-" + participant.getReproducibleHash();
        Transport portTransport = serviceDesc.getSupportedPorts(operationName)[0].getTransport();

        // build the magic key
        MultiKey magicKey = this.buildKey(wsdlServiceName, wsdlPortNameForThisClass, portTransport);

        ServiceEndpoint se = this.getActiveEndpoint(wsdlServiceName, wsdlPortNameForThisClass, portTransport);
        this.dynamicEndpointHandler.undeployDynamicNotificationEndpoint(se.getServiceName(), wsdlPortName, operationName, repos,
                participant);
        // calculate the actual count
        int count = ((Integer) this.endPointCount.get(se)).intValue() - 1;
        if (count == 0) {
            this.componentContextAccess.deactivateEndpoint(se);
            this.endPointCount.remove(se);
            this.activatedEndpoints.remove(magicKey);
        } else {
            this.endPointCount.put(se, new Integer(count));
        }

        LOG.debug("unregistered notification " + operationName + " for service " + wsdlServiceName.toString() + ":"
                + " for participant " + participant.toString() + " on port " + wsdlPortNameForThisClass);
    }

    /**
     * Destroy.
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        if ((null != this.instrumentationManager) && (null != this.instrumentation)) {
            try {
                this.instrumentationManager.unregisterInstrumentation(this.instrumentation);
            } catch (InternalInfrastructureException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Could not unregister management instrumentation - reason:\n" + e.getMessage());
                }
            }
            this.instrumentation = null;
            this.componentContextAccess = null;
            this.dynamicEndpointHandler = null;
            if (this.endpointMapping != null) {
                this.endpointMapping.clear();
                this.endpointMapping = null;
            }
            if (this.participantMapping != null) {
                this.participantMapping.clear();
                this.participantMapping = null;
            }
            if (this.activatedEndpoints != null) {
                this.activatedEndpoints.clear();
                this.activatedEndpoints = null;
            }
            if (this.serviceDescriptions != null) {
                this.serviceDescriptions.clear();
                this.serviceDescriptions = null;
            }
            if (this.endPointCount != null) {
                this.endPointCount.clear();
                this.endPointCount = null;
            }
            this.instrumentationManager = null;
            this.beanFactory = null;

        }
    }

    /**
     * Gets the dynamic endpoint handler.
     * 
     * @return the dynamic endpoint handler
     */
    public DynamicEndpointHandler getDynamicEndpointHandler() {
        return this.dynamicEndpointHandler;
    }

    /**
     * Gets the instrumentation.
     * 
     * @return the instrumentation
     */
    public Object getInstrumentation() {
        return this.instrumentation;
    }

    /**
     * Gets the instrumentation manager.
     * 
     * @return the instrumentation manager
     */
    public InstrumentationManager getInstrumentationManager() {
        return this.instrumentationManager;
    }

    /**
     * returns the Participant identity out of the participant mapping.We need the operation name in
     * addition for correct dispatching.
     * 
     * @param se
     *        the se
     * 
     * @return the participant identity unifier
     * 
     * @see org.eclipse.swordfish.core.components.endpointmanager.EndpointManager#
     *      getParticipantIdentityUnifier(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public UnifiedParticipantIdentity getParticipantIdentityUnifier(final ServiceEndpoint se) {
        MultiKey key = this.buildKey(se.getServiceName(), se.getEndpointName());
        return (UnifiedParticipantIdentity) this.participantMapping.get(key);
    }

    /**
     * returns the service description.
     * 
     * @param se
     *        the se
     * 
     * @return the service description
     */
    public CompoundServiceDescription getServiceDescription(final ServiceEndpoint se) {
        QName serviceName = se.getServiceName();
        if (serviceName != null)
            return (CompoundServiceDescription) this.serviceDescriptions.get(serviceName);
        else
            return null;
    }

    /**
     * retrieves a service endpoint for the supplied service name, port name and operation name.
     * 
     * @param wsdlPortName
     *        the port name indicated in the WSDL
     * @param wsdlServiceQName
     *        the wsdl service Q name
     * 
     * @return - an already active endpoint associated with the parameters or null no endpoint has
     *         been activated
     */
    public ServiceEndpoint getServiceEndpoint(final QName wsdlServiceQName, final String wsdlPortName) {
        MultiKey endpointMappingKey = this.buildKey(wsdlServiceQName, wsdlPortName);
        ServiceEndpoint se = (ServiceEndpoint) this.endpointMapping.get(endpointMappingKey);
        return se;
    }

    /**
     * Init.
     */
    public void init() {
        if ((null != this.beanFactory) && (null == this.instrumentationManager)) {
            this.instrumentationManager =
                    (InstrumentationManager) this.beanFactory
                        .getBean("org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager");
        }
        if (null != this.instrumentationManager) {
            InputStream desc = this.getClass().getClassLoader().getResourceAsStream(ENDPOINT_MANAGER_BEAN_INSTRUMENTATION_DESC);

            this.instrumentation = new Instrumentation();
            try {
                this.instrumentationManager.registerInstrumentation(this.instrumentation, desc, ENDPOINT_MANAGER_BEAN);
            } catch (InternalInfrastructureException e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to initiate management instrumentation - "
                            + "EndpointManager will not be visible to Operator. Reason:\n" + e.getMessage());
                }

            } catch (InternalAlreadyRegisteredException e) {
                LOG.warn(ENDPOINT_MANAGER_BEAN + " already registered at MVenaServer!");
            }
        }
    }

    /**
     * Sets the bean factory.
     * 
     * @param arg0
     *        the new bean factoryy
     * 
     * @throws BeansException
     */
    public void setBeanFactory(final BeanFactory arg0) throws BeansException {
        this.beanFactory = arg0;
    }

    /**
     * Sets the component context access.
     * 
     * @param componentContextAccess
     *        Spring injection point
     */
    public void setComponentContextAccess(final ComponentContextAccess componentContextAccess) {
        this.componentContextAccess = componentContextAccess;
    }

    // TODO we need this method for notification endpoint unregistration ONLY

    /**
     * Sets the dynamic endpoint handler.
     * 
     * @param dynamicEndpointHandler
     *        the new dynamic endpoint handler
     */
    public void setDynamicEndpointHandler(final DynamicEndpointHandler dynamicEndpointHandler) {
        this.dynamicEndpointHandler = dynamicEndpointHandler;
    }

    // NOTE We doe not remove the service descriptions from the service desc map
    // when unregistering the endpoints. Is it going to cause trouble?

    /**
     * Sets the instrumenation manager.
     * 
     * @param instrumenationManager
     *        the new instrumenation manager
     */
    public void setInstrumenationManager(final InstrumentationManager instrumenationManager) {
        this.instrumentationManager = instrumenationManager;
    }

    /**
     * activates an endpoint for the parametes and puts is into the activeEndpoints map.
     * 
     * @param wsdlServiceName
     *        the service name indicated in the WSDL
     * @param wsdlPortName
     *        the port name indicated in the WSDL
     * @param portTransport
     *        the port transport
     * 
     * @return -- the newly creates endpoint TODO better exception handling
     * 
     * @throws JBIException
     *         if an endpoint could not be activated
     */
    private ServiceEndpoint activateEndpoint(final QName wsdlServiceName, final String wsdlPortName, final Transport portTransport)
            throws JBIException {

        ServiceEndpoint endpoint = this.componentContextAccess.activateEndpoint(wsdlServiceName, wsdlPortName);
        this.activatedEndpoints.put(this.buildKey(wsdlServiceName, wsdlPortName, portTransport), endpoint);
        LOG.debug("activated endpoint for service" + wsdlServiceName + " on port " + wsdlPortName);
        return endpoint;
    }

    /**
     * creates a key based on the given parameters.
     * 
     * @param wsdlServiceQName
     *        the service name as indicated in the WSDL
     * @param wsdlPortName
     *        the port name as indicated in the WSDL
     * 
     * @return -- a multikey to identify participants and endpoints
     */
    private MultiKey buildKey(final QName wsdlServiceQName, final String wsdlPortName) {
        return new MultiKey(wsdlServiceQName, wsdlPortName);
    }

    /**
     * Builds the key.
     * 
     * @param wsdlServiceQName
     *        the wsdl service Q name
     * @param wsdlPortName
     *        the wsdl port name
     * @param trapo
     *        the trapo
     * 
     * @return the multi key
     */
    private MultiKey buildKey(final QName wsdlServiceQName, final String wsdlPortName, final Transport trapo) {
        return new MultiKey(wsdlServiceQName, wsdlPortName, trapo);
    }

    /**
     * Builds the selector expression.
     * 
     * @param participant
     *        the participant
     * 
     * @return the string
     */
    private String buildSelectorExpression(final UnifiedParticipantIdentity participant) {
        String applicationId = participant.getParticipantIdentity().getApplicationID();
        String instanceId = participant.getParticipantIdentity().getInstanceID();
        StringBuffer selector = new StringBuffer();
        selector.append("SBBApplicationId = '");
        selector.append(applicationId);
        selector.append("'");
        if (null != instanceId) {
            selector.append(" AND SBBInstanceId = '");
            selector.append(instanceId);
            selector.append("'");
        }
        return selector.toString();
    }

    /**
     * Gets the active endpoint.
     * 
     * @param wsdlServiceName
     *        the service name indicated in the WSDL
     * @param wsdlPortName
     *        the port name indicated in the WSDL
     * @param portTransport
     *        the port transport
     * 
     * @return - an already active endpoint associated with the parameters or null if this endpoint
     *         has not been activated
     */
    private ServiceEndpoint getActiveEndpoint(final QName wsdlServiceName, final String wsdlPortName, final Transport portTransport) {
        MultiKey key = this.buildKey(wsdlServiceName, wsdlPortName, portTransport);
        return (ServiceEndpoint) this.activatedEndpoints.get(key);
    }

    /**
     * Gets the SPDX port with locally configured transport.
     * 
     * @param port
     *        the port
     * @param localPorts
     *        the local ports
     * 
     * @return the SPDX port with locally configured transport
     */
    private SPDXPort getSPDXPortWithLocallyConfiguredTransport(final SPDXPort port, final Map localPorts) {
        // if we need to consider local config than
        if (localPorts.containsKey(port.getName())) {
            Transport trapo = Transport.UNKNOWN;
            if (port.getTransport() != Transport.UNKNOWN) {
                trapo = port.getTransport();
            } else {
                SPDXPort prt = (SPDXPort) (localPorts.get(port.getName()));
                String uri = ((SOAPAddress) prt.getExtensibilityElements().get(0)).getLocationURI();
                if (uri.startsWith("http://")) {
                    trapo = Transport.HTTP;
                } else if (uri.startsWith("https://")) {
                    trapo = Transport.HTTP;
                } else if (uri.startsWith("jms://")) {
                    trapo = Transport.JMS;
                }
            }
            return new SPDXPortImpl((SPDXPort) (localPorts.get(port.getName())), trapo);
        } else
            // otherwise we will take the configuration out of the SPDX ...
            // maybe refineable with
            // JBI transport beachten.
            // log.warn("Port " + port.getName()
            // + " is declared in SPDX, but no corresponding local "
            // + "endpoint is defined in configuration. Using configuration from
            // SPDX.");
            return port;
    }

    /**
     * Checks if is durable.
     * 
     * @param csd
     *        the csd
     * @param operationName
     *        the operation name
     * 
     * @return true, if is durable
     */
    private boolean isDurable(final CompoundServiceDescription csd, final String operationName) {
        Policy operationPolicy = csd.getAgreedPolicy().getOperationPolicy(operationName);
        ExactlyOne eo = (ExactlyOne) operationPolicy.getTerms().get(0);
        TermIterator iterAll = new TermIterator(eo, new ClassSelector(All.class));
        All all = (All) iterAll.next();
        TermIterator iterPrimitive = new TermIterator(all, new NameSelector("Subscription"));
        if (iterPrimitive.hasNext()) {
            PrimitiveAssertion assertion = (PrimitiveAssertion) iterPrimitive.next();
            String type = assertion.getAttribute(new QName("type"));
            return "durable".equalsIgnoreCase(type);
        }
        return false;
    }

    /**
     * Pre select ports for activation.
     * 
     * @param serviceDesc
     *        the service desc
     * @param localConfigPorts
     *        the local config ports
     * 
     * @return the SPDX port[]
     * 
     * @throws JBIException
     */
    private SPDXPort[] preSelectPortsForActivation(final CompoundServiceDescription serviceDesc, final Map localConfigPorts)
            throws JBIException {
        List portsToActivate = new ArrayList();

        SPDXPort[] ports = serviceDesc.getPorts();
        for (int i = 0; i < ports.length; i++) {
            SPDXPort currentPort = ports[i];
            /*
             * EXCLUDE PORTS
             */
            // do not care about notification ports
            if (serviceDesc.isNotificationOnlyPort(currentPort.getName())) {
                continue;
            }

            boolean hasLocalConfiguration = localConfigPorts.containsKey(currentPort.getName());

            // if there is a locator port with a bad config than log it
            if (currentPort.isUsingLocator() && !hasLocalConfiguration) {
                LOG.error("Port " + ports[i].getName() + " is declared in SPDX as a locator-enabled port, "
                        + "but no corresponding local " + "endpoint is defined in configuration. Port is NOT activated.");
                continue;
            }

            // TODO please refractor me later :)
            if (currentPort.getName().equals("DummyCallbackPort") && !hasLocalConfiguration) {
                LOG.error("Callback Port " + " is declared in SDX , but no corresponding local "
                        + "endpoint is defined in configuration. Port is NOT activated.");
                continue;
            }

            /*
             * INCLUDE PORTS
             */
            portsToActivate.add(currentPort);
        }
        return (SPDXPort[]) portsToActivate.toArray(new SPDXPort[portsToActivate.size()]);
    }

    /**
     * The Class Instrumentation.
     */
    public class Instrumentation {

        /**
         * Gets the endpoint count.
         * 
         * @return the endpoint count
         */
        public Integer getEndpointCount() {
            return new Integer(EndpointManagerBean.this.activatedEndpoints.size());
        }

        /**
         * Gets the participant mapping.
         * 
         * @return the participant mapping
         */
        public String[] getParticipantMapping() {
            ArrayList result = new ArrayList();
            Iterator iter = EndpointManagerBean.this.participantMapping.keySet().iterator();
            while (iter.hasNext()) {
                MultiKey key = (MultiKey) iter.next();
                UnifiedParticipantIdentity participant =
                        (UnifiedParticipantIdentity) EndpointManagerBean.this.participantMapping.get(key);
                String mapping = key.toString() + " => " + (null != participant ? participant.toString() : "null");
                result.add(mapping);
            }
            return (String[]) result.toArray(new String[0]);
        }

    }

}
