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

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.management.mock.DummyProcessingNotification;
import org.eclipse.swordfish.core.management.notification.EventType;
import org.eclipse.swordfish.core.management.notification.ExchangePattern;
import org.eclipse.swordfish.core.management.notification.ExchangeState;
import org.eclipse.swordfish.core.management.notification.InteractionStyle;
import org.eclipse.swordfish.core.management.notification.MessageProcessingNotification;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * Convenience generator for management notifications</br> See
 * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points for
 * definition of EventTypes
 * 
 */
public class NotificationProducer {

    /** The Constant BLOCKING_CONSUMER_IN_OUT. */
    public static final DummyProcessingNotification BLOCKING_CONSUMER_IN_OUT = getBaseBlockingConsumerInOut();

    /** The Constant BLOCKING_PROVIDER_IN_OUT. */
    public static final DummyProcessingNotification BLOCKING_PROVIDER_IN_OUT = getBaseBlockingProviderInOut();

    /** The Constant DEFAULT_OP_NAME. */
    private static final String DEFAULT_OP_NAME = "bar";

    /** The Constant DEFAULT_SERVICE_NAME. */
    private static final String DEFAULT_SERVICE_NAME = "foo";

    /**
     * Gets the base blocking consumer in out.
     * 
     * @return the base blocking consumer in out
     */
    private static DummyProcessingNotification getBaseBlockingConsumerInOut() {
        DummyProcessingNotification ret;
        ret = new DummyProcessingNotification();
        ret.setInteractionStyle(InteractionStyle.BLOCKING);
        ret.setExchangePattern(ExchangePattern.IN_OUT);
        ret.setParticipantRole(ParticipantRole.CONSUMER);
        return ret;
    }

    /**
     * Gets the base blocking provider in out.
     * 
     * @return the base blocking provider in out
     */
    private static DummyProcessingNotification getBaseBlockingProviderInOut() {
        DummyProcessingNotification ret;
        ret = new DummyProcessingNotification();
        ret.setInteractionStyle(InteractionStyle.BLOCKING);
        ret.setExchangePattern(ExchangePattern.IN_OUT);
        ret.setParticipantRole(ParticipantRole.PROVIDER);
        return ret;
    }

    /** The base notification. */
    private DummyProcessingNotification baseNotification;

    /**
     * Instantiates a new notification producer.
     * 
     * @param baseNotification
     *        the base notification
     */
    public NotificationProducer(final DummyProcessingNotification baseNotification) {
        this.baseNotification = baseNotification;
        this.baseNotification.setServiceName(new QName(DEFAULT_SERVICE_NAME));
        this.baseNotification.setOperationName(DEFAULT_OP_NAME);
    }

    /**
     * Instantiates a new notification producer.
     * 
     * @param baseNotification
     *        the base notification
     * @param service
     *        the service
     * @param operation
     *        the operation
     */
    public NotificationProducer(final DummyProcessingNotification baseNotification, final QName service, final String operation) {
        this.baseNotification = baseNotification;
        this.baseNotification.setServiceName(service);
        this.baseNotification.setOperationName(operation);
    }

    /**
     * Instantiates a new notification producer.
     * 
     * @param baseNotification
     *        the base notification
     * @param operation
     *        the operation
     */
    public NotificationProducer(final DummyProcessingNotification baseNotification, final String operation) {
        this.baseNotification = baseNotification;
        this.baseNotification.setServiceName(new QName(DEFAULT_SERVICE_NAME));
        this.baseNotification.setOperationName(operation);
    }

    /**
     * Gets the 1.
     * 
     * @return the 1
     */
    public MessageProcessingNotification get1() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.APP_IN_PRE);
        ret.setExchangeState(ExchangeState.ACTIVE);
        return ret;
    }

    /**
     * Gets the 10.
     * 
     * @return the 10
     */
    public MessageProcessingNotification get10() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.INTERNAL_POST);
        ret.setExchangeState(ExchangeState.ACTIVE);
        return ret;
    }

    /**
     * Gets the 2.
     * 
     * @return the 2
     */
    public MessageProcessingNotification get2() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.APP_IN_POST);
        ret.setExchangeState(ExchangeState.ACTIVE);
        return ret;
    }

    /**
     * Gets the 3.
     * 
     * @return the 3
     */
    public MessageProcessingNotification get3() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.APP_OUT_PRE);
        if (ParticipantRole.CONSUMER == ret.getParticipantRole()) {
            ret.setExchangeState(ExchangeState.FINISHED);
        } else {
            ret.setExchangeState(ExchangeState.ACTIVE);
        }
        return ret;
    }

    /**
     * Gets the 4.
     * 
     * @return the 4
     */
    public MessageProcessingNotification get4() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.APP_OUT_POST);
        if (ParticipantRole.CONSUMER == ret.getParticipantRole()) {
            ret.setExchangeState(ExchangeState.FINISHED);
        } else {
            ret.setExchangeState(ExchangeState.ACTIVE);
        }
        return ret;
    }

    /**
     * Gets the 5.
     * 
     * @return the 5
     */
    public MessageProcessingNotification get5() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.NET_IN_PRE);
        ret.setExchangeState(ExchangeState.ACTIVE);
        return ret;
    }

    /**
     * Gets the 6.
     * 
     * @return the 6
     */
    public MessageProcessingNotification get6() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.NET_IN_POST);
        ret.setExchangeState(ExchangeState.ACTIVE);
        return ret;
    }

    /**
     * Gets the 7.
     * 
     * @return the 7
     */
    public MessageProcessingNotification get7() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.NET_OUT_PRE);
        ret.setExchangeState(ExchangeState.ACTIVE);
        return ret;
    }

    /**
     * Gets the 8.
     * 
     * @return the 8
     */
    public MessageProcessingNotification get8() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.NET_OUT_POST);
        ret.setExchangeState(ExchangeState.ACTIVE);
        return ret;
    }

    /**
     * Gets the 9.
     * 
     * @return the 9
     */
    public MessageProcessingNotification get9() {
        DummyProcessingNotification ret;
        ret = (DummyProcessingNotification) this.baseNotification.clone();
        ret.setTimestamp(System.currentTimeMillis());
        ret.setEventType(EventType.INTERNAL_PRE);
        ret.setExchangeState(ExchangeState.ACTIVE);
        return ret;
    }

}
