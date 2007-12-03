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
package org.eclipse.swordfish.core.management.notification;

/**
 * Interface to define the information to be provided for each message processing event.
 * 
 */
public interface MessageProcessingNotification extends ExchangeNotification {

    /**
     * Gets the consumer policy ID.
     * 
     * @return the consumer policy ID
     */
    String getConsumerPolicyID();

    /**
     * Gets the exchange pattern.
     * 
     * @return communication style applicable to operation
     */
    ExchangePattern getExchangePattern();

    /**
     * Gets the exchange state.
     * 
     * @return state the messsage exchange had when notification was created
     */
    ExchangeState getExchangeState();

    /**
     * Gets the interaction style.
     * 
     * @return invocation or execution style (interaction pattern between application and PAPI)
     */
    InteractionStyle getInteractionStyle();

    /**
     * Gets the message ID.
     * 
     * @return ID of single message
     */
    String getMessageID();

    /**
     * Gets the provider policy ID.
     * 
     * @return the provider policy ID
     */
    String getProviderPolicyID();

    /**
     * Gets the reporter.
     * 
     * @return component that created the notification
     */
    Object getReporter();

    /**
     * Gets the timestamp.
     * 
     * @return timestamp of creation of notification
     */
    long getTimestamp();

}
