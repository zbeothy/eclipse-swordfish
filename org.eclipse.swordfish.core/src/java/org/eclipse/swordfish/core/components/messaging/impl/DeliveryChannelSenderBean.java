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
package org.eclipse.swordfish.core.components.messaging.impl;

import java.io.IOException;
import javax.jbi.messaging.DeliveryChannel;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.swordfish.core.components.headerprocessing.impl.HeaderProcessorBean;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.messaging.DeliveryChannelSender;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.utils.ExchangeProperties;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.eclipse.swordfish.papi.internal.exception.InfrastructureRuntimeException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * provides a layer to internal components to send MEPs directly to the JBI delivery channel.
 */
public class DeliveryChannelSenderBean implements DeliveryChannelSender {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(DeliveryChannelListenerBean.class);

    /** JBI's delivery channel. */
    private DeliveryChannel dc;

    /** The hp. */
    private HeaderProcessorBean hp;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelSender#ackExchange(javax.jbi.messaging.MessageExchange)
     */
    public void ackExchange(final MessageExchange exchange) {
        try {
            exchange.setStatus(ExchangeStatus.DONE);
            this.dc.send(exchange);
        } catch (MessagingException e) {
            LOG.info("acknowledging of exchanged failed " + exchange.getOperation().toString());
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelSender#refuseExchangeWithError(javax.jbi.messaging.MessageExchange,
     *      java.lang.Exception)
     */
    public void refuseExchangeWithError(final MessageExchange exchange, final Exception theError) {
        try {
            /**
             * This is a work around! If there is an out message existing in the exchange than
             * remove it. This prevents a BC to try to send the msg instead of the error
             */
            if (exchange instanceof InOut) {
                ((InOut) exchange).setOutMessage(null);
            }
            exchange.setError(theError);
            exchange.setStatus(ExchangeStatus.ERROR);
            this.dc.send(exchange);
        } catch (MessagingException e) {
            LOG.info("refusing of exchanged failed " + exchange.getOperation().toString());
        }
    }

    /**
     * Send.
     * 
     * @param exchange
     *        the exchange
     * 
     * @throws InternalSBBException
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelSender#send(javax.jbi.messaging.MessageExchange)
     */
    public void send(final MessageExchange exchange) throws MessagingException, InternalSBBException {
        this.shapeExchange(exchange);
        this.dc.send(exchange);
    }

    /**
     * Send sync.
     * 
     * @param exchange
     *        the exchange
     * 
     * @return the message exchange
     * 
     * @throws InternalSBBException
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelSender#sendSync(javax.jbi.messaging.MessageExchange)
     */
    public MessageExchange sendSync(final MessageExchange exchange) throws MessagingException, InternalSBBException {
        this.shapeExchange(exchange);
        boolean sendSyncResult = this.dc.sendSync(exchange);
        if (sendSyncResult == true)
            return exchange;
        else
            throw new MessagingException("Send failed at NMR boundry for " + exchange.getService().toString() + ":"
                    + exchange.getOperation().getLocalPart());
    }

    /**
     * Send sync.
     * 
     * @param exchange
     *        the exchange
     * @param timeout
     *        the timeout
     * 
     * @return the message exchange
     * 
     * @throws InternalSBBException
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.core.components.messaging.DeliveryChannelSender#sendSync(javax.jbi.messaging.MessageExchange,
     *      long)
     */
    public MessageExchange sendSync(final MessageExchange exchange, final long timeout) throws MessagingException,
            InternalSBBException {
        this.shapeExchange(exchange);
        boolean sendSyncResult = this.dc.sendSync(exchange, timeout);
        if (sendSyncResult == true)
            return exchange;
        else
            throw new MessagingException("Send failed at NMR boundry for " + exchange.getService().toString() + ":"
                    + exchange.getOperation().getLocalPart());
    }

    /**
     * Sets the context.
     * 
     * @param contextAccess
     *        The contextAccess to set. Spring injection point.
     */
    public void setContext(final ComponentContextAccess contextAccess) {
        try {
            this.dc = contextAccess.getDeliveryChannel();
        } catch (MessagingException e) {
            throw new ComponentRuntimeException("error accessing the delivery channel");
        }
    }

    /**
     * Sets the header processor bean.
     * 
     * @param processor
     *        the new header processor bean
     */
    public void setHeaderProcessorBean(final HeaderProcessorBean processor) {
        this.hp = processor;
    }

    /*
     * FIXME When the superBC is able to deal with more than just DOMSources than change this
     * behaviour
     */
    /**
     * Shape content for binding.
     * 
     * @param msg
     *        the msg
     * 
     * @return the normalized message
     * 
     * @throws InternalMessagingException
     * @throws SAXException
     * @throws IOException
     */
    private NormalizedMessage shapeContentForBinding(final NormalizedMessage msg) throws MessagingException, SAXException,
            IOException {
        Source src = msg.getContent();
        if (src == null) return null;
        if (src instanceof DOMSource) return msg;
        if (src instanceof StreamSource) {
            Document doc = XMLUtil.docFromInputStream(((StreamSource) src).getInputStream());
            msg.setContent(new DOMSource(doc));
            return msg;
        }
        if (src instanceof SAXSource) {
            DOMSource domding = XMLUtil.domSourceFromSAXSource((SAXSource) src);
            msg.setContent(domding);
            return msg;
        }
        throw new MessagingException("unexpected message type while shaping the message for binding");
    }

    /**
     * Shape exchange.
     * 
     * @param exchange
     *        the exchange
     * 
     * @throws InternalSBBException
     */
    private void shapeExchange(final MessageExchange exchange) throws InternalSBBException {
        CallContextExtension ctxe = HeaderUtil.getCallContextExtension(exchange);
        if (ctxe != null) {
            // put the SOAP action into the message exchange. This must be done
            // here as the header
            // processor does not have access to the exchange itself.
            String action = ctxe.getSOAPAction();
            if (action != null) {
                // FIXME remove the quoting once the binding does this
                // Fixes defect #1100
                exchange.setProperty(ExchangeProperties.SOAP_ACTION, "\"" + action + "\"");
            } else {
                LOG.debug("message to " + exchange.getOperation().toString() + " has no SOAP action set");
            }

            Scope scope = ctxe.getScope();
            if (exchange.getStatus() != ExchangeStatus.ERROR) {
                if (scope != null) {
                    if (scope.equals(Scope.REQUEST)) {
                        this.hp.mapOutgoingRequest(ctxe, HeaderUtil.getLatestValidNormalizedMessage(exchange, scope));
                    } else {
                        this.hp.mapOutgoingResponse(ctxe, HeaderUtil.getLatestValidNormalizedMessage(exchange, scope));
                    }

                    try {
                        this.shapeContentForBinding(HeaderUtil.getLatestValidNormalizedMessage(exchange, scope));
                    } catch (MessagingException e) {
                        throw new InternalMessagingException(e);
                    } catch (SAXException e) {
                        throw new InternalIllegalInputException(e);
                    } catch (IOException e) {
                        throw new InternalInfrastructureException(e);
                    }
                } else
                    throw new InfrastructureRuntimeException("missing Scope while sending " + exchange.getOperation().toString());
            }
        } else
            throw new InfrastructureRuntimeException("missing call context while sending " + exchange.getOperation().toString());
    }
}
