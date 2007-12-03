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
package org.eclipse.swordfish.core.management.messaging;

import org.eclipse.swordfish.core.management.mock.DummyProcessingNotification;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.ManagementNotificationListener;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * The Class TestProviderInOut.
 */
public class TestProviderInOut {

    /** The listener. */
    ManagementNotificationListener listener;

    /** The producer. */
    private NotificationProducer producer;

    /**
     * Instantiates a new test provider in out.
     * 
     * @param listener
     *        the listener
     */
    public TestProviderInOut(final ManagementNotificationListener listener) {
        this.listener = listener;
    }

    /**
     * Execute in.
     * 
     * @return the string
     */
    public String executeIn() {
        try {
            Thread.sleep(1); // make sure there is a fresh timestamp :-)
        } catch (InterruptedException e) {
            // noop
        }
        String id = new Long(System.currentTimeMillis()).toString();
        this.executeInPart1(id);
        this.executeInPart2(id);
        return id;
    }

    /**
     * Execute in part1.
     * 
     * @param id
     *        the id
     * 
     * @return the string
     */
    public String executeInPart1(final String id) {
        this.producer = new NotificationProducer(NotificationProducer.BLOCKING_PROVIDER_IN_OUT);
        DummyProcessingNotification notification = (DummyProcessingNotification) this.producer.get5();
        notification.setMessageID(id);
        notification.setCorrelationID(id);
        this.listener.sendNotification(notification);
        notification = (DummyProcessingNotification) this.producer.get6();
        notification.setMessageID(id);
        notification.setCorrelationID(id);
        this.listener.sendNotification(notification);
        return id;
    }

    /**
     * Execute in part2.
     * 
     * @param id
     *        the id
     */
    public void executeInPart2(final String id) {
        DummyProcessingNotification notification;
        notification = (DummyProcessingNotification) this.producer.get3();
        notification.setMessageID(id);
        notification.setCorrelationID(id);
        this.listener.sendNotification(notification);
    }

    /**
     * Execute out.
     * 
     * @param id
     *        the id
     * 
     * @return the string
     */
    public String executeOut(final String id) {
        String messageID = this.executeOutPart1(id);
        this.executeOutPart2(id, messageID);
        return id;
    }

    /**
     * Execute out part1.
     * 
     * @param correlationId
     *        the correlation id
     * 
     * @return response message id
     */
    public String executeOutPart1(final String correlationId) {
        try {
            Thread.sleep(1); // make sure there is a fresh timestamp :-)
        } catch (InterruptedException e) {
            // noop
        }
        this.producer = new NotificationProducer(NotificationProducer.BLOCKING_PROVIDER_IN_OUT);
        String messageID = new Long(System.currentTimeMillis()).toString();
        DummyProcessingNotification notification = (DummyProcessingNotification) this.producer.get1();
        notification.setMessageID(messageID);
        notification.setCorrelationID(correlationId);
        this.listener.sendNotification(notification);
        notification = (DummyProcessingNotification) this.producer.get2();
        notification.setMessageID(messageID);
        notification.setCorrelationID(correlationId);
        this.listener.sendNotification(notification);
        notification = (DummyProcessingNotification) this.producer.get7();
        notification.setMessageID(messageID);
        notification.setCorrelationID(correlationId);
        this.listener.sendNotification(notification);
        return messageID;
    }

    /**
     * Execute out part2.
     * 
     * @param correlationId
     *        the correlation id
     * @param messageID
     *        the message ID
     */
    public void executeOutPart2(final String correlationId, final String messageID) {
        DummyProcessingNotification notification;
        notification = (DummyProcessingNotification) this.producer.get8();
        notification.setMessageID(messageID);
        notification.setCorrelationID(correlationId);
        this.listener.sendNotification(notification);
        notification = (DummyProcessingNotification) this.producer.get4();
        notification.setMessageID(messageID);
        notification.setCorrelationID(correlationId);
        this.listener.sendNotification(notification);
        notification = new DummyProcessingNotification();
        notification.setMessageID(messageID);
        notification.setCorrelationID(correlationId);
        notification.setParticipantRole(ParticipantRole.PROVIDER);
        notification.setExchangeState(ExchangeState.FINISHED);
        this.listener.sendNotification(notification);
    }

}
