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
package org.eclipse.swordfish.core.components.messaging;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * this is an interface to the JBI delivery channel.
 */
public interface DeliveryChannelSender {

    /** this components role. */
    String ROLE = DeliveryChannelSender.class.getName();

    /**
     * Ack exchange.
     * 
     * @param exchange
     *        the exchange
     */
    void ackExchange(final MessageExchange exchange);

    /**
     * Refuse exchange with error.
     * 
     * @param exchange
     *        the exchange
     * @param theError
     *        the the error
     */
    void refuseExchangeWithError(final MessageExchange exchange, final Exception theError);

    /**
     * just send an exchange to the delivery Channel.
     * 
     * @param exchange
     *        the exchange to send
     * 
     * @throws InternalMessagingException
     *         if sending to delivery channel fails
     * @throws InternalSBBException
     */
    void send(MessageExchange exchange) throws MessagingException, InternalSBBException;

    /**
     * just send an exchange to the delivery Channel this signature just makes the code "nicer".
     * 
     * @param exchange
     *        exchange the exchange to send
     * 
     * @return -- the message exchange that has been received.
     * 
     * @throws InternalMessagingException
     *         if sending to delivery channel fails
     * @throws InternalSBBException
     */
    MessageExchange sendSync(MessageExchange exchange) throws MessagingException, InternalSBBException;

    /**
     * just send an exchange to the delivery Channel this signature just makes the code "nicer".
     * 
     * @param exchange
     *        exchange the exchange to send
     * @param timeout
     *        timeout value for sending to the delivery channel
     * 
     * @return -- the message exchange that has been received.
     * 
     * @throws InternalMessagingException
     *         if sending to delivery channel fails
     * @throws InternalSBBException
     */
    MessageExchange sendSync(MessageExchange exchange, long timeout) throws MessagingException, InternalSBBException;
}
