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
package org.eclipse.swordfish.core.components.configrepos.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.wsdl.Definition;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryRemoteException;
import org.eclipse.swordfish.core.components.configrepos.ConfigReposJBIProxy;
import org.eclipse.swordfish.core.components.endpointreferenceresolver.EndpointReferenceResolver;
import org.eclipse.swordfish.core.components.headerprocessing.HeaderProcessor;
import org.eclipse.swordfish.core.components.helpers.UUIDGenerator;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.internalproxy.ProviderException;
import org.eclipse.swordfish.core.components.internalproxy.ResilienceController;
import org.eclipse.swordfish.core.components.internalproxy.ResilienceControllerFactory;
import org.eclipse.swordfish.core.components.internalproxy.ServiceOperationProxy;
import org.eclipse.swordfish.core.components.internalproxy.exception.InternalProxyComponentException;
import org.eclipse.swordfish.core.components.internalproxy.impl.AbstractSkeletonResilienceController;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.impl.CompoundServiceDescriptionImpl;
import org.eclipse.swordfish.core.components.srproxy.impl.DefinitionHelper;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtensionFactory;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * The Class ConfigReposJBIProxyBean.
 * 
 */
public class ConfigReposJBIProxyBean implements ConfigReposJBIProxy, InitializingBean, DisposableBean {

    /** Default timeout is set to 15 seconds. */
    public static final long CONFIGREPOSJBIPROXYBEAN_DEFAULTTIMEOUT = 15000;

    /** Namespace of the port type of the configuration repository service. */
    public static final String CONFIGREPOSJBIPROXYBEAN_SDX_NAMESPACE =
            "http://services.sopware.org/configuration/ConfigurationRuntime/1.0";

    /** Namespace of the port type of the configuration repository service. */
    public static final String CONFIGREPOSJBIPROXYBEAN_SPDX_NAMESPACE =
            "http://services.sopware.org/configuration/ConfigurationRuntimeProvider/1.0";

    /** Namespace of the port type of the configuration repository service. */
    public static final String CONFIGREPOSJBIPROXYBEAN_PORTNAME = "ConfigurationRuntime_Http_Port";

    /** Namespace of the port type of the configuration repository service. */
    public static final String CONFIGREPOSJBIPROXYBEAN_SERVICENAME = "ConfigurationRuntimeProvider";

    /** The Constant CONFIGREPOSJBIPROXYBEAN_CONFIGOPERNAME. */
    public static final String CONFIGREPOSJBIPROXYBEAN_CONFIGOPERNAME = "getConfiguration";

    /** The Constant CONFIGREPOSJBIPROXYBEAN_RESOURCEOPERNAME. */
    public static final String CONFIGREPOSJBIPROXYBEAN_RESOURCEOPERNAME = "getResource";

    /** Reference to the header processor. */
    private HeaderProcessor headerProcessor = null;

    /** Timeout for the proxy. */
    private long timeout = CONFIGREPOSJBIPROXYBEAN_DEFAULTTIMEOUT;

    /** Factory to be used when parsing the service descriptions. */
    private DocumentBuilderFactory factory = null;

    /**
     * Property referencing the resource for the service description of the configuration
     * repository.
     */
    private Resource configReposSDX = null;

    /**
     * Property referencing the resource for the service provider description of the configuration
     * repository.
     */
    private Resource configReposSPDX = null;

    /** Agreed policy resource file of the configuration runtime. */
    private Resource configAgreedPolicy = null;

    /** Compiled agreed policy. */
    private transient AgreedPolicy internalizedAgreedPolicy = null;

    /** reference to the InternalSBB endpoint reference resolver. */
    private EndpointReferenceResolver resolver = null;

    /** Reference to the JBI component context. */
    private ComponentContext context = null;

    /** Proxy doing the calls. */
    private ServiceOperationProxy serviceOperationProxy = null;

    /** List of soap addresses to try out. */
    private transient List soapAddressesConfig = null;

    /** Internalized list of message exchange factories for different provider instances. */
    private List configMEXList = null;

    /** Internalized list of message exchange factories for different provider instances. */
    private List resourceMEXList = null;

    /** Internalized compound service description. */
    private CompoundServiceDescription serviceDescription = null;

    /** Resilience controller factory. */
    private ResilienceControllerFactory resilienceCntlrFact = null;

    /** The uuid generator which is being used for creating message ids. */
    private UUIDGenerator uuidGenerator = null;

    /**
     * Instantiates a new config repos JBI proxy bean.
     */
    public ConfigReposJBIProxyBean() {
        this.factory = TransformerUtil.getDocumentBuilderFactory();
    }

    /**
     * (non-Javadoc).
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.impl.BarebonesServiceOperationProxyBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        this.internalizedAgreedPolicy = this.compileAgreedPolicy();

        this.configMEXList = new ArrayList(this.soapAddressesConfig.size());
        this.resourceMEXList = new ArrayList(this.soapAddressesConfig.size());

        DefinitionHelper definitionHelper = DefinitionHelper.getInstance();
        Definition internalizedSDX = definitionHelper.elementToDefinition(this.createElementFromResource(this.configReposSDX));

        for (int pos = 0; pos < this.soapAddressesConfig.size(); pos++) {
            Definition internalizedSPDX =
                    definitionHelper.elementToDefinition(this.createElementFromResource(this.configReposSPDX));

            this.serviceDescription =
                    new CompoundServiceDescriptionImpl(internalizedSDX, this.reconfigureSOAPAddressElement(internalizedSPDX,
                            new URL((String) this.soapAddressesConfig.get(pos))), this.internalizedAgreedPolicy, null);

            // create factory for configuration
            ServiceEndpoint endpoint =
                    this.context.resolveEndpointReference(this.resolver.createEndpointReference(this.serviceDescription,
                            CONFIGREPOSJBIPROXYBEAN_CONFIGOPERNAME));
            // initialize the message exchange factory
            this.configMEXList.add(this.context.getDeliveryChannel().createExchangeFactory(endpoint));

            // create factory for configuration
            endpoint =
                    this.context.resolveEndpointReference(this.resolver.createEndpointReference(this.serviceDescription,
                            CONFIGREPOSJBIPROXYBEAN_RESOURCEOPERNAME));
            // initialize the message exchange factory
            this.resourceMEXList.add(this.context.getDeliveryChannel().createExchangeFactory(endpoint));
        }

    }

    /**
     * Close this proxy instance. Subsequent calls to this proxy will create exceptions
     * 
     * @see org.eclipse.swordfish.configrepos.configuration.sources.RemoteConfigSourceProxy#close()
     */
    public void close() {
        throw new RuntimeException("Not implemented yet!");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    public void destroy() throws Exception {
        this.configAgreedPolicy = null;
        this.configMEXList = null;
        this.configReposSDX = null;
        this.configReposSPDX = null;
        this.context = null;
        this.soapAddressesConfig = null;
        this.serviceDescription = null;
        this.resilienceCntlrFact = null;
        this.resolver = null;
        this.serviceOperationProxy.destroy();
    }

    /**
     * Gets the config agreed policy.
     * 
     * @return Returns the configAgreedPolicy.
     */
    public Resource getConfigAgreedPolicy() {
        return this.configAgreedPolicy;
    }

    /**
     * Gets the config repos SDX.
     * 
     * @return Returns the configReposSDX.
     */
    public Resource getConfigReposSDX() {
        return this.configReposSDX;
    }

    /**
     * Gets the config repos SPDX.
     * 
     * @return Returns the configReposSPDX.
     */
    public Resource getConfigReposSPDX() {
        return this.configReposSPDX;
    }

    /**
     * Gets the configuration data.
     * 
     * @param aIdentity
     *        which is makine the call
     * @param aRequestPayload
     *        holds the request document
     * 
     * @return the source which containes the reply document
     * 
     * @throws Exception
     *         in case of any error which occures
     * 
     */
    public Document getConfigurationData(final InternalParticipantIdentity aIdentity, final Document aRequestPayload)
            throws Exception {
        if (null == this.serviceOperationProxy)
            throw new RuntimeException(
                    "Configuration proxy is in illegal state. No JBI endpoint assigned to 'serviceOperationProxy' property.");

        try {
            ResilienceController controller = null;
            if (null != this.resilienceCntlrFact) {
                controller = this.resilienceCntlrFact.createInstance();
            } else {
                controller =
                        new ConfigReposJBIProxyBean.ConfigReposDefaultResilienceController(
                                new UnifiedParticipantIdentity(aIdentity));
            }
            return (Document) this.serviceOperationProxy.invokeServiceOperation(this.configMEXList, new QName(
                    CONFIGREPOSJBIPROXYBEAN_SDX_NAMESPACE, CONFIGREPOSJBIPROXYBEAN_CONFIGOPERNAME), controller, aRequestPayload);
        } catch (Exception e) {
            // FIXME ExceptionReporter.logErrorReport(logger, e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.configuration.sources.RemoteConfigSourceProxy#getConfigurationData(java.lang.String,
     *      java.lang.String, org.w3c.dom.Document)
     */
    public Document getConfigurationData(final String appId, final String instId, final Document request) throws Exception {
        return this.getConfigurationData(new PartIDImpl(appId, instId), request);
    }

    /**
     * Gets the context.
     * 
     * @return Returns the context.
     */
    public ComponentContext getContext() {
        return this.context;
    }

    /**
     * Gets the header processor.
     * 
     * @return Returns the headerProcessor.
     */
    public HeaderProcessor getHeaderProcessor() {
        return this.headerProcessor;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the repository identifier
     * 
     * @see org.eclipse.swordfish.configrepos.configuration.sources.RemoteConfigSourceProxy#getRepositoryIdentifier()
     */
    public String getRepositoryIdentifier() {
        StringBuffer result = new StringBuffer();
        result.append("[");
        Iterator iter = this.soapAddressesConfig.iterator();
        while (iter.hasNext()) {
            result.append((String) iter.next());
            if (iter.hasNext()) {
                result.append("; ");
            }
        }
        result.append("]");
        return result.toString();
    }

    /**
     * Gets the resolver.
     * 
     * @return Returns the resolver.
     */
    public EndpointReferenceResolver getResolver() {
        return this.resolver;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aIdentity
     *        which is makine the call
     * @param aRequestPayload
     *        holds the request document
     * 
     * @return Document with the resource data inside a resourceResponse tag
     * 
     * @throws Exception
     *         in case of any problem during the exchange
     * 
     * 
     * FIXME This is just copy-and-paste from getConfiguration. Needs to be adapted to resource
     * fetching
     */
    public Document getResourceData(final InternalParticipantIdentity aIdentity, final Document aRequestPayload) throws Exception {
        if (null == this.serviceOperationProxy)
            throw new RuntimeException(
                    "Configuration proxy is in illegal state. No JBI endpoint assigned to 'serviceOperationProxy' property.");

        try {
            ResilienceController controller = null;
            if (null != this.resilienceCntlrFact) {
                controller = this.resilienceCntlrFact.createInstance();
            } else {
                controller =
                        new ConfigReposJBIProxyBean.ConfigReposDefaultResilienceController(
                                new UnifiedParticipantIdentity(aIdentity));
            }
            return (Document) this.serviceOperationProxy.invokeServiceOperation(this.resourceMEXList, new QName(
                    CONFIGREPOSJBIPROXYBEAN_SDX_NAMESPACE, CONFIGREPOSJBIPROXYBEAN_RESOURCEOPERNAME), controller, aRequestPayload);
        } catch (Exception e) {
            // FIXME ExceptionReporter.logErrorReport(logger, e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.resource.sources.RemoteResourceSourceProxy#getResourceData(java.lang.String,
     *      java.lang.String, org.w3c.dom.Document)
     */
    public Document getResourceData(final String appId, final String instId, final Document request) throws Exception {
        return this.getResourceData(new PartIDImpl(appId, instId), request);
    }

    /**
     * Gets the service operation proxy.
     * 
     * @return Returns the serviceOperationProxy.
     */
    public ServiceOperationProxy getServiceOperationProxy() {
        return this.serviceOperationProxy;
    }

    /**
     * Gets the soap addresses config.
     * 
     * @return Returns the soapAddressesConfig.
     */
    public List getSoapAddressesConfig() {
        return this.soapAddressesConfig;
    }

    /**
     * Gets the timeout.
     * 
     * @return Returns the timeout.
     */
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * Gets the uuid generator.
     * 
     * @return Returns the uuidGenerator.
     */
    public UUIDGenerator getUuidGenerator() {
        return this.uuidGenerator;
    }

    /**
     * Sets the config agreed policy.
     * 
     * @param configAgreedPolicy
     *        The configAgreedPolicy to set.
     */
    public void setConfigAgreedPolicy(final Resource configAgreedPolicy) {
        this.configAgreedPolicy = configAgreedPolicy;
    }

    /**
     * Sets the config repos SDX.
     * 
     * @param configReposSDX
     *        The configReposSDX to set.
     */
    public void setConfigReposSDX(final Resource configReposSDX) {
        this.configReposSDX = configReposSDX;
    }

    /**
     * Sets the config repos SPDX.
     * 
     * @param configReposSPDX
     *        The configReposSPDX to set.
     */
    public void setConfigReposSPDX(final Resource configReposSPDX) {
        this.configReposSPDX = configReposSPDX;
    }

    /**
     * Sets the context.
     * 
     * @param context
     *        The context to set.
     */
    public void setContext(final ComponentContext context) {
        this.context = context;
    }

    /**
     * Sets the header processor.
     * 
     * @param headerProcessor
     *        The headerProcessor to set.
     */
    public void setHeaderProcessor(final HeaderProcessor headerProcessor) {
        this.headerProcessor = headerProcessor;
    }

    /**
     * Sets the resolver.
     * 
     * @param resolver
     *        The resolver to set.
     */
    public void setResolver(final EndpointReferenceResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Sets the service operation proxy.
     * 
     * @param serviceOperationProxy
     *        The serviceOperationProxy to set.
     */
    public void setServiceOperationProxy(final ServiceOperationProxy serviceOperationProxy) {
        this.serviceOperationProxy = serviceOperationProxy;
    }

    /**
     * Sets the soap addresses config.
     * 
     * @param soapAddressesConfig
     *        The soapAddressesConfig to set.
     */
    public void setSoapAddressesConfig(final List soapAddressesConfig) {
        this.soapAddressesConfig = soapAddressesConfig;
    }

    /**
     * Sets the timeout.
     * 
     * @param timeout
     *        The timeout to set.
     */
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    /**
     * Sets the uuid generator.
     * 
     * @param uuidGenerator
     *        The uuidGenerator to set.
     */
    public void setUuidGenerator(final UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    /**
     * Compile agreed policy.
     * 
     * @return an agreed policy object from the configAgreedPolicy resource
     * 
     * @throws IOException
     *         in case the input stream to the agreed policy could not be opened
     * @throws ParserConfigurationException
     *         in case the parser was not configured correctly
     * @throws SAXException
     *         in case the agreed policy could not be parsed
     */
    private AgreedPolicy compileAgreedPolicy() throws IOException, ParserConfigurationException, SAXException {
        return AgreedPolicyFactory.getInstance().createFrom(this.configAgreedPolicy.getInputStream());
    }

    /**
     * Create an Element from a Spring Resource defining the configuration repository service
     * interface.
     * 
     * @param aResource
     *        which should be internalized
     * 
     * @return the element which containes the specified resource
     * 
     * @throws SAXException
     *         in case the SAX parser encountered an error
     * @throws ParserConfigurationException
     *         in case the parser was not set up correctly
     * @throws IOException
     *         in case the resource could not be read
     */
    private Element createElementFromResource(final Resource aResource) throws IOException, SAXException,
            ParserConfigurationException {
        this.factory.setNamespaceAware(true);
        return TransformerUtil.getDocumentBuilder().parse(aResource.getInputStream()).getDocumentElement();
    }

    /**
     * Reconfigure SOAP address element.
     * 
     * @param internalizedSPDX
     *        which should be adapted
     * @param aURL
     *        which should be used to address the endpoint
     * 
     * @return a list of compound service descriptions which can be used to call the remote endpoint
     *         extension is malformed
     * 
     * @throws MalformedURLException
     *         in case the url in the soap:address
     */
    private Definition reconfigureSOAPAddressElement(final Definition internalizedSPDX, final URL aURL)
            throws MalformedURLException {
        // Change address to point to details from the configuration
        List elements =
                internalizedSPDX.getService(new QName(CONFIGREPOSJBIPROXYBEAN_SPDX_NAMESPACE, CONFIGREPOSJBIPROXYBEAN_SERVICENAME))
                    .getPort(CONFIGREPOSJBIPROXYBEAN_PORTNAME).getExtensibilityElements();
        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
            ExtensibilityElement elem = (ExtensibilityElement) iter.next();
            if (SOAPAddress.class.isAssignableFrom(elem.getClass())) {
                SOAPAddress address = (SOAPAddress) elem;
                address.setLocationURI(aURL.toExternalForm());
            }
        }
        return internalizedSPDX;
    }

    /**
     * The Class ConfigReposDefaultResilienceController.
     * 
     */
    public class ConfigReposDefaultResilienceController extends AbstractSkeletonResilienceController {

        /** Last index used when processing the service description list. */
        private int lastIndex = -1;

        /** The identity. */
        private UnifiedParticipantIdentity identity = null;

        /**
         * The Constructor.
         * 
         * @param aIdentity
         *        The identity to be used during all subsequent calls
         */
        public ConfigReposDefaultResilienceController(final UnifiedParticipantIdentity aIdentity) {
            super(ConfigReposJBIProxyBean.this.headerProcessor);
            this.identity = aIdentity;
        }

        /**
         * (non-Javadoc).
         * 
         * @param aMessageExchangeFactoryList
         *        the a message exchange factory list
         * @param serviceOperation
         *        the service operation
         * 
         * @return the message exchange
         * 
         * @throws Exception
         * 
         * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#createInOutExchange(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription,
         *      javax.xml.namespace.QName)
         */
        @Override
        public MessageExchange createInOutExchange(final Collection aMessageExchangeFactoryList, final QName serviceOperation)
                throws Exception {

            if (null == aMessageExchangeFactoryList) throw new IllegalArgumentException("Missing service description list");

            OperationDescription desc =
                    ConfigReposJBIProxyBean.this.serviceDescription.getOperation(serviceOperation.getLocalPart());
            MessageExchange msgExchange = null;
            if (++this.lastIndex >= aMessageExchangeFactoryList.size())
                // return null to signal end of approach
                return null;
            else {
                msgExchange =
                        ((MessageExchangeFactory) ((List) aMessageExchangeFactoryList).get(this.lastIndex)).createInOutExchange();
            }
            // service and operation on exchange
            msgExchange.setService(ConfigReposJBIProxyBean.this.serviceDescription.getServiceQName());
            msgExchange.setOperation(serviceOperation);

            // start creating a bare minimum InternalCallContext
            CallContextExtension inCtx = CallContextExtensionFactory.createCallContextExtension();
            HeaderUtil.setCallContextExtension(msgExchange, inCtx);
            inCtx.setOperationName(desc.getName());
            inCtx.setServiceName(desc.getServiceDescription().getServiceQName());
            inCtx.setSOAPAction(desc.getSoapAction());
            inCtx.setProviderID(ConfigReposJBIProxyBean.this.serviceDescription.getServiceQName());
            if (null != this.identity) {
                inCtx.setUnifiedParticipantIdentity(this.identity);
            }
            inCtx.setScope(Scope.REQUEST);
            if (null != ConfigReposJBIProxyBean.this.uuidGenerator) {
                inCtx.setMessageID(ConfigReposJBIProxyBean.this.uuidGenerator.getUUID("uuid:"));
            }
            super.setInCtx(inCtx);

            AgreedPolicy policy = desc.getServiceDescription().getAgreedPolicy();
            if (policy == null)
                throw new ComponentRuntimeException("Missing the agreed policy for service "
                        + desc.getServiceDescription().getServiceQName());
            AgreedPolicy reduced = policy.getReducedAgreedPolicy(serviceOperation.getLocalPart());
            inCtx.setPolicy(reduced);
            return msgExchange;
        }

        /**
         * (non-Javadoc).
         * 
         * @return the call timeout
         * 
         * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#getCallTimeout()
         */
        @Override
        public long getCallTimeout() {
            return ConfigReposJBIProxyBean.this.timeout;
        }

        /**
         * (non-Javadoc).
         * 
         * @param aExceptionList
         *        the a exception list
         * 
         * @return the object
         * 
         * @throws Exception
         * 
         * @see org.eclipse.swordfish.core.components.internalproxy.impl.AbstractSkeletonResilienceController#handleFailure(java.util.List)
         */
        @Override
        public Object handleFailure(final List aExceptionList) throws Exception {
            try {
                return super.handleFailure(aExceptionList);
            } catch (InternalProxyComponentException e) {
                throw new ConfigurationRepositoryRemoteException("EXCEPTION_00010", e);
            }
        }

        /**
         * (non-Javadoc).
         * 
         * @param aOutMessage
         *        the a out message
         * @param aFault
         *        the a fault
         * 
         * @return the object
         * 
         * @throws Exception
         * 
         * @see org.eclipse.swordfish.core.components.internalproxy.impl.AbstractSkeletonResilienceController#postprocessMessage(javax.jbi.messaging.NormalizedMessage,
         *      javax.jbi.messaging.Fault)
         */
        @Override
        public Object postprocessMessage(final NormalizedMessage aOutMessage, final Fault aFault) throws Exception {
            if (null == aFault) {
                ConfigReposJBIProxyBean.this.headerProcessor.mapIncomingResponse(aOutMessage, super.getOutCtx());
                return (TransformerUtil.docFromSource(aOutMessage.getContent()));
            }
            throw new ProviderException("Provider returned fault " + TransformerUtil.stringFromSource(aFault.getContent()));
        }

    }

    private static class PartIDImpl implements InternalParticipantIdentity {

        private String applicationID;

        private String instanceID;

        /**
         * @param appId
         * @param instId
         */
        public PartIDImpl(final String appId, final String instId) {
            this.applicationID = appId;
            this.instanceID = instId;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swordfish.papi.internal.InternalParticipantIdentity#getApplicationID()
         */
        public String getApplicationID() {
            return this.applicationID;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swordfish.papi.internal.InternalParticipantIdentity#getInstanceID()
         */
        public String getInstanceID() {
            return this.instanceID;
        }

    }

}
