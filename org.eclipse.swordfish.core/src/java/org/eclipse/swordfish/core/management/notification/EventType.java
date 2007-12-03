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
 * Definition of event types for reporting messaging events see
 * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
 * 
 */
public final class EventType extends AbstractEnum {

    /**
     * to be emitted when message is handed in from application, before processing by InternalSBB
     * see http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType APP_IN_PRE = new EventType("APP_IN_PRE");

    /**
     * to be emitted after message handed in from application was processed by InternalSBB see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType APP_IN_POST = new EventType("APP_IN_POST");

    /**
     * to be emitted before message is handed over to application see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType APP_OUT_PRE = new EventType("APP_OUT_PRE");

    /**
     * to be emitted after message handed over by InternalSBB was processed by application see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType APP_OUT_POST = new EventType("APP_OUT_POST");

    /**
     * to be emitted before message is processed by nternal component (e.g., interceptor) see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType INTERNAL_PRE = new EventType("INTERNAL_PRE");

    /**
     * to be emitted after message was processed by internal component (e.g., interceptor) see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType INTERNAL_POST = new EventType("INTERNAL_POST");

    /**
     * to be emitted before message that was received from binding component is processed by
     * InternalSBB see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType NET_IN_PRE = new EventType("NET_IN_PRE");

    /**
     * to be emitted after message that was received from binding component was processed by
     * InternalSBB see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType NET_IN_POST = new EventType("NET_IN_POST");

    /**
     * to be emitted before message is handed over to binding component see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType NET_OUT_PRE = new EventType("NET_OUT_PRE");

    /**
     * to be emitted after message was handed over to binding component see
     * http://sam.servicebackbone.org/wiki/index.php/M1_Messaging_Overview#Notification_points.
     */
    public static final EventType NET_OUT_POST = new EventType("NET_OUT_POST");

    /** to be emitted after an operation has been added. */
    public static final EventType OPERATION_ADDED = new EventType("OPERATION_ADDED");

    /** to be emitted after an operation has been removed. */
    public static final EventType OPERATION_REMOVED = new EventType("OPERATION_REMOVED");

    /** to be used if event type can not be determined. */
    public static final EventType UNKNOWN = new EventType("UNKNOWN");

    /**
     * Gets the instance by name.
     * 
     * @param name
     *        the name
     * 
     * @return the instance by name
     */
    public static EventType getInstanceByName(final String name) {
        return (EventType) getInstanceByNameInternal(EventType.class, name);
    }

    /**
     * The Constructor.
     * 
     * @param name
     *        to use for event type
     */
    private EventType(final String name) {
        super(name);
    }

}
