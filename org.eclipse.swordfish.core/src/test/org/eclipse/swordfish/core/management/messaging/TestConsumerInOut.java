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
import org.eclipse.swordfish.core.management.notification.ExchangePattern;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.InteractionStyle;
import org.eclipse.swordfish.core.management.notification.ManagementNotificationListener;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * The Class TestConsumerInOut.
 */
public class TestConsumerInOut {

    /** The listener. */
    ManagementNotificationListener listener;

    /** The producer. */
    private NotificationProducer producer;

    /**
     * Instantiates a new test consumer in out.
     * 
     * @param listener
     *        the listener
     */
    public TestConsumerInOut(final ManagementNotificationListener listener) {
        this.listener = listener;
    }

    /**
     * Execute blocking in.
     * 
     * @param id
     *        the id
     */
    public void executeBlockingIn(final String id) {
        String messageID = this.executeBlockingInPart1(id);
        this.executeBlockingInPart2(id, messageID);
    }

    /**
     * Execute blocking in part1.
     * 
     * @param id
     *        the id
     * 
     * @return the string
     */
    public String executeBlockingInPart1(final String id) {
        try {
            Thread.sleep(1); // make sure there is a fresh timestamp :-)
        } catch (InterruptedException e) {
            // noop
        }
        this.producer = new NotificationProducer(NotificationProducer.BLOCKING_CONSUMER_IN_OUT);
        String messageID = new Long(System.currentTimeMillis()).toString();
        DummyProcessingNotification notification = (DummyProcessingNotification) this.producer.get5();
        notification.setParticipantRole(ParticipantRole.CONSUMER);
        notification.setExchangePattern(ExchangePattern.IN_OUT);
        notification.setInteractionStyle(InteractionStyle.BLOCKING);
        notification.setMessageID(messageID);
        notification.setCorrelationID(id);
        this.listener.sendNotification(notification);
        notification = (DummyProcessingNotification) this.producer.get6();
        notification.setParticipantRole(ParticipantRole.CONSUMER);
        notification.setExchangePattern(ExchangePattern.IN_OUT);
        notification.setInteractionStyle(InteractionStyle.BLOCKING);
        notification.setMessageID(messageID);
        notification.setCorrelationID(id);
        this.listener.sendNotification(notification);
        return messageID;
    }

    /**
     * Execute blocking in part2.
     * 
     * @param id
     *        the id
     * @param messageID
     *        the message ID
     */
    public void executeBlockingInPart2(final String id, final String messageID) {
        DummyProcessingNotification notification;
        notification = (DummyProcessingNotification) this.producer.get3();
        notification.setParticipantRole(ParticipantRole.CONSUMER);
        notification.setExchangePattern(ExchangePattern.IN_OUT);
        notification.setInteractionStyle(InteractionStyle.BLOCKING);
        notification.setExchangeState(ExchangeState.ACTIVE);
        notification.setMessageID(messageID);
        notification.setCorrelationID(id);
        this.listener.sendNotification(notification);
        notification = (DummyProcessingNotification) this.producer.get3();
        notification.setParticipantRole(ParticipantRole.CONSUMER);
        notification.setCorrelationID(id);
        notification.setExchangeState(ExchangeState.FINISHED);
        this.listener.sendNotification(notification);
    }

    /**
     * Execute blocking out.
     * 
     * @return the string
     */
    public String executeBlockingOut() {
        String id = this.executeBlockingOutPart1();
        this.executeBlockingOutPart2(id);
        return id;
    }

    /**
     * Execute blocking out part1.
     * 
     * @return the string
     */
    public String executeBlockingOutPart1() {
        try {
            Thread.sleep(1); // make sure there is a fresh timestamp :-)
        } catch (InterruptedException e) {
            // noop
        }
        String id = new Long(System.currentTimeMillis()).toString();
        this.producer = new NotificationProducer(NotificationProducer.BLOCKING_CONSUMER_IN_OUT);
        DummyProcessingNotification notification = (DummyProcessingNotification) this.producer.get1();
        notification.setParticipantRole(ParticipantRole.CONSUMER);
        notification.setExchangePattern(ExchangePattern.IN_OUT);
        notification.setInteractionStyle(InteractionStyle.BLOCKING);
        notification.setMessageID(id);
        notification.setCorrelationID(id);
        System.out.println("> " + System.currentTimeMillis());
        this.listener.sendNotification(notification);
        System.out.println("> " + System.currentTimeMillis());
        notification = (DummyProcessingNotification) this.producer.get2();
        notification.setParticipantRole(ParticipantRole.CONSUMER);
        notification.setExchangePattern(ExchangePattern.IN_OUT);
        notification.setInteractionStyle(InteractionStyle.BLOCKING);
        notification.setMessageID(id);
        notification.setCorrelationID(id);
        System.out.println("> " + System.currentTimeMillis());
        this.listener.sendNotification(notification);
        System.out.println("> " + System.currentTimeMillis());
        return id;
    }

    /**
     * Execute blocking out part2.
     * 
     * @param id
     *        the id
     */
    public void executeBlockingOutPart2(final String id) {
        DummyProcessingNotification notification;
        notification = (DummyProcessingNotification) this.producer.get7();
        notification.setParticipantRole(ParticipantRole.CONSUMER);
        notification.setExchangePattern(ExchangePattern.IN_OUT);
        notification.setInteractionStyle(InteractionStyle.BLOCKING);
        notification.setMessageID(id);
        notification.setCorrelationID(id);
        this.listener.sendNotification(notification);
    }

}
