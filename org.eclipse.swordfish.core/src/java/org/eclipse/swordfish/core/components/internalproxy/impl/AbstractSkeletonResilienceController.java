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

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import org.eclipse.swordfish.core.components.headerprocessing.HeaderProcessor;
import org.eclipse.swordfish.core.components.internalproxy.CommunicationException;
import org.eclipse.swordfish.core.components.internalproxy.ProviderException;
import org.eclipse.swordfish.core.components.internalproxy.ResilienceController;
import org.eclipse.swordfish.core.components.internalproxy.exception.InternalProxyComponentException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.TransformerUtil;

/**
 * The Class AbstractSkeletonResilienceController.
 * 
 */
public abstract class AbstractSkeletonResilienceController implements ResilienceController {

    /** Logger for this class. */
    private static Log log = SBBLogFactory.getLog(AbstractSkeletonResilienceController.class);

    /** Outbound call context. */
    private CallContextExtension inCtx = null;

    /** Inbound call context. */
    private CallContextExtension outCtx = null;

    /** Header processor used to pre-/post-process messages and call context instances. */
    private HeaderProcessor headerProcessor = null;

    /**
     * The Constructor.
     * 
     * @param aHeaderProcessor
     *        which should be used while pre-/post-processing messages and context
     */
    public AbstractSkeletonResilienceController(final HeaderProcessor aHeaderProcessor) {
        super();
        this.headerProcessor = aHeaderProcessor;
    }

    /**
     * (non-Javadoc).
     * 
     * @param messageExchangeBag
     *        the message exchange bag
     * @param serviceOperation
     *        the service operation
     * 
     * @return the message exchange
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#createInOnlyExchange(java.util.Collection,
     *      javax.xml.namespace.QName)
     */
    public MessageExchange createInOnlyExchange(final Collection messageExchangeBag, final QName serviceOperation) throws Exception {
        return null;
    }

    /**
     * (non-Javadoc).
     * 
     * @param serviceDescriptionList
     *        the service description list
     * @param serviceOperation
     *        the service operation
     * 
     * @return the message exchange
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#createInOutExchange(java.util.Collection,
     *      javax.xml.namespace.QName)
     */
    public MessageExchange createInOutExchange(final Collection serviceDescriptionList, final QName serviceOperation)
            throws Exception {
        return null;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aExchange
     *        the a exchange
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#finalizeExchange(javax.jbi.messaging.MessageExchange)
     */
    public void finalizeExchange(final MessageExchange aExchange) throws Exception {
        if (null != aExchange) {
            aExchange.setStatus(ExchangeStatus.DONE);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @return the call timeout
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#getCallTimeout()
     */
    public long getCallTimeout() {
        return 0;
    }

    /**
     * Gets the in ctx.
     * 
     * @return Returns the inCtx.
     */
    public CallContextExtension getInCtx() {
        return this.inCtx;
    }

    /**
     * Gets the logger.
     * 
     * @return Returns the log.
     */
    public Logger getLogger() {
        return (Logger) log.getLogger();
    }

    /**
     * Gets the out ctx.
     * 
     * @return Returns the outCtx.
     */
    public CallContextExtension getOutCtx() {
        return this.outCtx;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aExchange
     *        the a exchange
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#handleError(javax.jbi.messaging.MessageExchange)
     */
    public void handleError(final MessageExchange aExchange) throws Exception {
        throw new CommunicationException("Communication with SOPware Infrastructure TSP failed for service "
                + aExchange.getService() + " and operation " + aExchange.getOperation().getLocalPart() + " because of "
                + aExchange.getError().getMessage(), aExchange.getError());
    }

    /**
     * (non-Javadoc).
     * 
     * @param e
     *        the e
     * 
     * @return the exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#handleException(java.lang.Exception)
     */
    public Exception handleException(final Exception e) {
        return e;
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
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#handleFailure()
     */
    public Object handleFailure(final List aExceptionList) throws Exception {
        if ((null == aExceptionList) || (aExceptionList.size() < 1))
            throw new InternalProxyComponentException("Unknown Exception");
        throw new InternalProxyComponentException((Exception) aExceptionList.get(0));
    }

    /**
     * (non-Javadoc).
     * 
     * @param aExchange
     *        the a exchange
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#postprocessExchange(javax.jbi.messaging.MessageExchange)
     */
    public void postprocessExchange(final MessageExchange aExchange) {
        this.outCtx = HeaderUtil.getCallContextExtension(aExchange);
        this.outCtx.setOperationName(aExchange.getOperation().getLocalPart());
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
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#postprocessMessage(javax.jbi.messaging.NormalizedMessage)
     */
    public Object postprocessMessage(final NormalizedMessage aOutMessage, final Fault aFault) throws Exception {
        if (null == aFault) {
            this.headerProcessor.mapIncomingResponse(aOutMessage, this.outCtx);

            if ((null != log) && log.isDebugEnabled()) {
                log.debug("Inbound message: [{0}]", aOutMessage);
            }

            Source source = aOutMessage.getContent();
            String outMessage = null;

            if (TransformerUtil.isSourceEmpty(source)) {
                outMessage = "";
                log.debug("The internal consumer call received an empty response...");
            } else {
                outMessage = TransformerUtil.stringFromSource(source);
                log.debug("The internal consumer call received the following response...");
                log.debug(outMessage);
            }
            return outMessage;
        }
        throw new ProviderException("Provider returned fault " + TransformerUtil.stringFromSource(aFault.getContent()));
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
    public void preprocessExchange(final MessageExchange aExchange) throws Exception {
        HeaderUtil.setCallContextExtension(aExchange, this.inCtx);
    }

    /**
     * (non-Javadoc).
     * 
     * @param aInMessage
     *        the a in message
     * 
     * @throws Exception
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.ResilienceController#preprocessMessage(javax.jbi.messaging.NormalizedMessage)
     */
    public void preprocessMessage(final NormalizedMessage aInMessage) throws Exception {
        this.headerProcessor.mapOutgoingRequest(this.inCtx, aInMessage);
        if ((null != log) && log.isDebugEnabled()) {
            log.debug("Outbound message: [{0}]", aInMessage);
        }
    }

    /**
     * Set the outbound call context for the next call.
     * 
     * @param aInCtx
     *        to be set
     */
    public void setInCtx(final CallContextExtension aInCtx) {
        this.inCtx = aInCtx;
    }

}
