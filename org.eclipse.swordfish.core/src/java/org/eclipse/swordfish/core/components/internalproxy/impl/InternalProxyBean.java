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
package org.eclipse.swordfish.core.components.internalproxy.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.xml.namespace.QName;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.eclipse.swordfish.core.components.headerprocessing.HeaderProcessor;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.internalproxy.CommunicationException;
import org.eclipse.swordfish.core.components.internalproxy.InternalProxy;
import org.eclipse.swordfish.core.components.internalproxy.InternalProxyException;
import org.eclipse.swordfish.core.components.internalproxy.ResilienceController;
import org.eclipse.swordfish.core.components.internalproxy.ResilienceControllerFactory;
import org.eclipse.swordfish.core.components.internalproxy.ServiceOperationProxy;
import org.eclipse.swordfish.core.components.internalproxy.exception.InternalProxyComponentException;
import org.eclipse.swordfish.core.components.processing.PolicyRouter;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.interceptor.authentication.impl.UserCallbackHandler;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageExchangePattern;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtensionFactory;
import org.eclipse.swordfish.core.utils.ExchangeProperties;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * The Class InternalProxyBean.
 * 
 */
public class InternalProxyBean implements InternalProxy {

    /** Logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(InternalProxyBean.class);

    /**
     * Factories creating resilience controller. If non is being set, the internal default
     * implementation will be used
     */
    private ResilienceControllerFactory resilienceControllerFactory = null;

    /** InternalService proxies used to use the calls. */
    private ServiceOperationProxy requestResponseServiceOperationProxy = null;

    /** The oneway service operation proxy. */
    private ServiceOperationProxy onewayServiceOperationProxy = null;

    /** kernal. */
    private Kernel kernel = null;

    /** router. */
    private PolicyRouter policyRouter;

    /** header processor. */
    private HeaderProcessor headerProcessor;

    /** multi value map. */
    private MultiMap operationMap = null;

    /** infrastructure username. */
    private String infrastructureUsername = null;

    /** infrastructure password. */
    private String infrastructurePassword = null;

    /** infrastructure policy id. */
    private String infrastructurePolicyId = null;

    /**
     * constructor.
     */
    public InternalProxyBean() {
        this.operationMap = new MultiHashMap();
    }

    /**
     * destroy method.
     */
    public void destroy() {
        this.resilienceControllerFactory = null;
        this.requestResponseServiceOperationProxy = null;
        this.onewayServiceOperationProxy = null;
        this.kernel = null;
        this.policyRouter = null;
        this.headerProcessor = null;
        if (this.operationMap != null) {
            this.operationMap.clear();
            this.operationMap = null;
        }
    }

    /**
     * Get the header processor assigned to this proxy.
     * 
     * @return the header processor, if any
     */
    public HeaderProcessor getHeaderProcessor() {
        return this.headerProcessor;
    }

    /**
     * Gets the infrastructure policy id.
     * 
     * @return the infrastructure policy id
     */
    public String getInfrastructurePolicyId() {
        return this.infrastructurePolicyId;
    }

    /**
     * Gets the oneway service operation proxy.
     * 
     * @return Returns the requestResponseServiceOperationProxy.
     */
    public ServiceOperationProxy getOnewayServiceOperationProxy() {
        return this.onewayServiceOperationProxy;
    }

    /**
     * Gets the request response service operation proxy.
     * 
     * @return Returns the requestResponseServiceOperationProxy.
     */
    public ServiceOperationProxy getRequestResponseServiceOperationProxy() {
        return this.requestResponseServiceOperationProxy;
    }

    /**
     * Gets the resilience controller factory.
     * 
     * @return Returns the resilienceControllerFactory.
     */
    public ResilienceControllerFactory getResilienceControllerFactory() {
        return this.resilienceControllerFactory;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aggServiceDesc
     *        the agg service desc
     * @param operationName
     *        the operation name
     * @param inMessage
     *        the in message
     * 
     * @return the string
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.InternalProxy#invokeService(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription,
     *      java.lang.String, java.lang.String)
     * 
     * This is the invocation Version where you just need the internal resilenceControler
     */
    public String invokeService(final CompoundServiceDescription aggServiceDesc, final String operationName, final String inMessage)
            throws Exception {
        ResilienceController controller = null;
        if (null != this.resilienceControllerFactory) {
            controller = this.resilienceControllerFactory.createInstance();
        } else {
            controller = new InternalProxyBean.InternalProxyResilienceController();
        }

        return (String) this.invokeService(aggServiceDesc, operationName, inMessage, controller);
    }

    /**
     * (non-Javadoc).
     * 
     * @param aggServiceDesc
     *        the agg service desc
     * @param operationName
     *        the operation name
     * @param inMessage
     *        the in message
     * @param controller
     *        the controller
     * 
     * @return the object
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.InternalProxy
     *      #invokeService(org.eclipse.swordfish.core.components.iapi.AggregateServiceDescription,
     *      java.lang.String, java.lang.String, ResilienceController)
     */
    public Object invokeService(final CompoundServiceDescription aggServiceDesc, final String operationName,
            final String inMessage, final ResilienceController controller) throws Exception {

        List targets = new ArrayList();
        targets.add(0, aggServiceDesc);

        LOG.info("Received a request for internal consumer call for service=" + aggServiceDesc.getServiceQName().toString()
                + " operation name=" + operationName + " pattern="
                + aggServiceDesc.getOperation(operationName).getExchangePattern().toString());

        try {

            if (aggServiceDesc.getOperation(operationName).getExchangePattern().equals(MessageExchangePattern.IN_OUT_URI))
                return this.requestResponseServiceOperationProxy.invokeServiceOperation(targets, new QName(aggServiceDesc
                    .getServiceQName().getNamespaceURI(), aggServiceDesc.getOperation(operationName).getName()), controller,
                        TransformerUtil.docFromString(inMessage));
            else
                return this.onewayServiceOperationProxy.invokeServiceOperation(targets, new QName(aggServiceDesc.getServiceQName()
                    .getNamespaceURI(), aggServiceDesc.getOperation(operationName).getName()), controller, TransformerUtil
                    .docFromString(inMessage));
        } catch (Exception e) {
            // ExceptionReporter.logErrorReport(log, e);
            throw e;
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @param serviceName
     *        the service name
     * @param operationName
     *        the operation name
     * @param inMessage
     *        the in message
     * 
     * @return the string
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.InternalProxy#
     *      callService(javax.xml.namespace.QName, java.lang.String, java.lang.String)
     */
    public String invokeService(final QName serviceName, final String operationName, final String inMessage) throws Exception {

        LOG.info("Received a request for internal consumer call for service=" + serviceName.toString() + " operation name="
                + operationName);

        try {
            return this.invokeService(this.kernel
                .fetchServiceDescriptionWithPolicyId(serviceName, this.getInfrastructurePolicyId()), operationName, inMessage);
        } catch (InternalProxyException e) {
            // ExceptionReporter.logErrorReport(log, e);
            throw e;
        }
    }

    /**
     * ************************************************************************* Set the header
     * processor for this proxy.
     * 
     * @param headerProcessor
     *        for this internal proxy instance
     */
    public void setHeaderProcessor(final HeaderProcessor headerProcessor) {
        this.headerProcessor = headerProcessor;
    }

    /**
     * Sets the infrastructure password.
     * 
     * @param infrastructurePassword
     *        password
     */
    public void setInfrastructurePassword(final String infrastructurePassword) {
        this.infrastructurePassword = infrastructurePassword;
    }

    /**
     * Sets the infrastructure policy id.
     * 
     * @param infrastructurePolicyId
     *        the new infrastructure policy id
     */
    public void setInfrastructurePolicyId(final String infrastructurePolicyId) {
        this.infrastructurePolicyId = infrastructurePolicyId;
    }

    /**
     * Sets the infrastructure username.
     * 
     * @param infrastructureUsername
     *        username
     */
    public void setInfrastructureUsername(final String infrastructureUsername) {
        this.infrastructureUsername = infrastructureUsername;
    }

    /**
     * Sets the kernel.
     * 
     * @param kernel
     *        kernel
     */
    public void setKernel(final Kernel kernel) {
        this.kernel = kernel;
    }

    /**
     * Sets the oneway service operation proxy.
     * 
     * @param onewayServiceOperationProxy
     *        the oneway service operation proxy
     */
    public void setOnewayServiceOperationProxy(final ServiceOperationProxy onewayServiceOperationProxy) {
        this.onewayServiceOperationProxy = onewayServiceOperationProxy;
    }

    /**
     * Sets the policy router.
     * 
     * @param router
     *        router
     */
    public void setPolicyRouter(final PolicyRouter router) {
        this.policyRouter = router;
    }

    /**
     * Sets the request response service operation proxy.
     * 
     * @param requestResponseServiceOperationProxy
     *        The serviceOperationProxy to set.
     */
    public void setRequestResponseServiceOperationProxy(final ServiceOperationProxy requestResponseServiceOperationProxy) {
        this.requestResponseServiceOperationProxy = requestResponseServiceOperationProxy;
    }

    /**
     * Sets the resilience controller factory.
     * 
     * @param resilienceControllerFactory
     *        The resilienceControllerFactory to set.
     */
    public void setResilienceControllerFactory(final ResilienceControllerFactory resilienceControllerFactory) {
        this.resilienceControllerFactory = resilienceControllerFactory;
    }

    /**
     * The Class InternalProxyResilienceController.
     * 
     */
    private class InternalProxyResilienceController extends AbstractSkeletonResilienceController {

        /** Memorized the compound service description. */
        private CompoundServiceDescription serviceDescription = null;

        /**
         * Instantiates a new internal proxy resilience controller.
         */
        public InternalProxyResilienceController() {
            super(InternalProxyBean.this.headerProcessor);
        }

        /**
         * (non-Javadoc).
         * 
         * @param aServiceDescription
         *        the a service description
         * @param aServiceOperation
         *        the a service operation
         * 
         * @return the message exchange
         * 
         * @throws Exception
         * 
         * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#createInOnlyExchange(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription,
         *      javax.xml.namespace.QName)
         */
        @Override
        public MessageExchange createInOnlyExchange(final Collection aServiceDescription, final QName aServiceOperation)
                throws Exception {

            return this.createExchange(aServiceDescription, aServiceOperation, MessageExchangePattern.IN_ONLY_URI);
        }

        /**
         * (non-Javadoc).
         * 
         * @param aServiceDescription
         *        the a service description
         * @param aServiceOperation
         *        the a service operation
         * 
         * @return the message exchange
         * 
         * @throws Exception
         * 
         * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#createInOutExchange(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription,
         *      javax.xml.namespace.QName)
         */
        @Override
        public MessageExchange createInOutExchange(final Collection aServiceDescription, final QName aServiceOperation)
                throws Exception {

            return this.createExchange(aServiceDescription, aServiceOperation, MessageExchangePattern.IN_OUT_URI);
        }

        /**
         * (non-Javadoc).
         * 
         * @param e
         *        the e
         * 
         * @return the exception
         * 
         * @see org.eclipse.swordfish.core.components.internalproxy.impl.AbstractSkeletonResilienceController#handleException(java.lang.Exception)
         */
        @Override
        public Exception handleException(final Exception e) {
            return new InternalProxyComponentException(e);
        }

        /**
         * (non-Javadoc).
         * 
         * @param aExchange
         *        the a exchange
         * 
         * @throws Exception
         * 
         * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#preprocessExchange(javax.jbi.messaging.MessageExchange)
         */
        @Override
        public void preprocessExchange(final MessageExchange aExchange) throws Exception {
            super.preprocessExchange(aExchange);
            // TODO maybe set the MESSAGE_AUTH_CALLBACKS would be better
            aExchange.setProperty(ExchangeProperties.OPERATION_AUTH_CALLBACKS, new UserCallbackHandler(
                    InternalProxyBean.this.infrastructureUsername, InternalProxyBean.this.infrastructurePassword));
            InternalProxyBean.this.policyRouter.handleRequest(aExchange, Role.SENDER, this.serviceDescription.getAgreedPolicy());
        }

        /**
         * Creates the exchange.
         * 
         * @param aServiceDescription
         *        the a service description
         * @param aServiceOperation
         *        the a service operation
         * @param mep
         *        the mep
         * 
         * @return the message exchange
         * 
         * @throws Exception
         */
        private MessageExchange createExchange(final Collection aServiceDescription, final QName aServiceOperation, final URI mep)
                throws Exception {

            // This is just a 'single-try' call controller
            if (null != super.getInCtx()) return null;

            this.serviceDescription = (CompoundServiceDescription) ((List) aServiceDescription).get(0);
            OperationDescription desc = null;
            MessageExchange msgExchange = null;

            // try to re-use message exchange factory and operation descriptions
            // to a specific operation qname
            synchronized (InternalProxyBean.this.operationMap) {
                if (!InternalProxyBean.this.operationMap.containsKey(aServiceOperation.toString())) {
                    desc = this.serviceDescription.getOperation(aServiceOperation.getLocalPart());
                    if (desc == null)
                        throw new CommunicationException("Requested InternalOperation not found in the service description.");
                    MessageExchangeFactory msgExFactory = InternalProxyBean.this.kernel.createMessageExchangeFactory(desc);
                    if (mep.equals(MessageExchangePattern.IN_OUT_URI)) {
                        msgExchange = msgExFactory.createInOutExchange();
                    } else {
                        msgExchange = msgExFactory.createInOnlyExchange();
                    }
                    InternalProxyBean.this.operationMap.put(aServiceOperation.toString(), msgExFactory);
                    InternalProxyBean.this.operationMap.put(aServiceOperation.toString(), desc);
                } else {
                    Iterator iter = ((Collection) InternalProxyBean.this.operationMap.get(aServiceOperation.toString())).iterator();
                    while (iter.hasNext()) {
                        Object obj = iter.next();
                        if (obj instanceof MessageExchangeFactory) {
                            MessageExchangeFactory msgExFactory = (MessageExchangeFactory) obj;
                            if (mep.equals(MessageExchangePattern.IN_OUT_URI)) {
                                msgExchange = msgExFactory.createInOutExchange();
                            } else {
                                msgExchange = msgExFactory.createInOnlyExchange();
                            }
                        } else if (obj instanceof OperationDescription) {
                            desc = (OperationDescription) obj;
                        }
                    }
                }
            }
            // service and operation on exchange
            msgExchange.setService(this.serviceDescription.getServiceQName());
            msgExchange.setOperation(aServiceOperation);

            // start creating a bare minimum InternalCallContext
            CallContextExtension inCtx = CallContextExtensionFactory.createCallContextExtension();
            HeaderUtil.setCallContextExtension(msgExchange, inCtx);
            inCtx.setOperationName(desc.getName());
            inCtx.setServiceName(desc.getServiceDescription().getServiceQName());
            inCtx.setSOAPAction(desc.getSoapAction());
            inCtx.setProviderID(this.serviceDescription.getServiceQName());
            inCtx.setUnifiedParticipantIdentity(InternalProxyBean.this.kernel.getParticipant());
            inCtx.setScope(Scope.REQUEST);
            super.setInCtx(inCtx);

            AgreedPolicy policy = desc.getServiceDescription().getAgreedPolicy();
            if (policy == null)
                throw new ComponentRuntimeException("missing the agreed policy for service "
                        + desc.getServiceDescription().getServiceQName());
            AgreedPolicy reduced = policy.getReducedAgreedPolicy(aServiceOperation.getLocalPart());
            inCtx.setPolicy(reduced);
            return msgExchange;
        }
    }

}
