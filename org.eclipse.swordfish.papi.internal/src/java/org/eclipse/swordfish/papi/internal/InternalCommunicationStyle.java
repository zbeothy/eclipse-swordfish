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
package org.eclipse.swordfish.papi.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class provides a type safe enumeration of possible InternalSBB communication styles (also
 * known as message exchange patterns or MEPs).
 * 
 * <p>
 * <b>Communication styles</b> - The InternalSBB distinguishes the following communication styles:
 * <ul>
 * <li>A service operation invocation that causes the service provider to return a response message
 * has the <code>request-response</code> communication style.</li>
 * <li>A service operation invocation that does not result in a response message has the
 * <code>one-way</code> communication style.</li>
 * <li>A service operation invocation where a service provider sends ("publishes") a message that
 * is received by any consumer which has previously "subscribed" to it has the
 * <code>notification</code> communication style.</li>
 * <li><code>request-callback</code> is a communication style where the consumer application
 * sends a request and receives a callback from the provider application at an undetermined point of
 * time in the future. The service designer can choose <code>request-callback</code> instead of
 * <code>request-response</code> in cases where the provider does not have to react immediately
 * and SOP must guarantee the reliable delivery of the callback to the consumer. (See also 'Message
 * delivery reliability semantics' described later). </li>
 * </ul>
 * </p>
 * <p>
 * <b>Invocation style (consumer's perspective)</b> - The actual invocation of a service operation
 * can be either blocking (the consumer waits for a response) or non-blocking, (the consumer
 * continues to work and receives a potentials response using an
 * {@link org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler} in a separate thread).
 * </p>
 * <p>
 * <b>Execution style (provider's perspective)</b> - For a provider, "blocking" is one of the
 * execution styles supported by InternalSBB. On an execution of a service, the InternalSBB waits
 * for the provider to return from the processing. If "non-blocking" execution of a service is used,
 * the provider delivers a response to the InternalSBB in an operation running in parallel.
 * </p>
 * <p>
 * <b>Message delivery reliability semantics</b> - With one exception all combinations of
 * communication, invocation and execution styles have "at most one" message delivery semantics,
 * because they are optimized for communication speed and memory utilization.<br/> The exception is
 * <code>non-blocking Oneway calls with callbacks</code>. Such calls support "exactly one"
 * semantics - in other words: guaranteed message delivery. They are a combination of two
 * <code>non-blocking Oneway</code> calls, where a callback relationship is specified between a
 * service consumer and a service provider in their respective service consumer description and
 * service provider description. Within a {@link org.eclipse.swordfish.papi.untyped.InternalService}
 * and its sub-interfaces as well as within an
 * {@link org.eclipse.swordfish.papi.untyped.InternalOperation} and its sub-interfaces methods are
 * available to obtain the callback for a reliable communication. In the case of a callback
 * relationship, InternalSBB offers facilities to persist message contexts. The persisted message
 * context allows the consumer to recover the communication after failures.
 * 
 */
public final class InternalCommunicationStyle extends AbstractEnum {

    /**
     * A string symbol associated with a <code>request-response</code> communication style
     * (blocking or non-blocking).
     */
    public static final String REQUEST_RESPONSE_NAME = "Request-Response";

    /**
     * A string symbol associated with a <code>one-way</code> communication style.
     */
    public static final String ONEWAY_NAME = "Oneway";

    /**
     * A string symbol associated with a <code>notification</code> communication style.
     */
    public static final String NOTIFICATION_NAME = "Notification";

    /**
     * A numeric symbol associated with a <code>request-response</code> communication style
     * (blocking or non-blocking).
     */
    public static final int REQUEST_RESPONSE_VALUE = 1;

    /**
     * A numeric symbol associated with a <code>one-way</code> communication style.
     */
    public static final int ONEWAY_VALUE = 2;

    /**
     * A numeric symbol associated with a <code>notification</code> communication style.
     */
    public static final int NOTIFICATION_VALUE = 3;

    /**
     * A communication style for service invocations where a response message is provided following
     * a request message (blocking or non-blocking) <code>request-response</code>.
     */
    public static final InternalCommunicationStyle REQUEST_RESPONSE =
            new InternalCommunicationStyle(REQUEST_RESPONSE_NAME, REQUEST_RESPONSE_VALUE);

    /**
     * A communication style for service invocations that do not provoke a response (<code>one-way</code>).
     */
    public static final InternalCommunicationStyle ONEWAY = new InternalCommunicationStyle(ONEWAY_NAME, ONEWAY_VALUE);

    /**
     * A communication style for "publish-subscribe" topic messages (<code>Notification</code>).
     */
    public static final InternalCommunicationStyle NOTIFICATION =
            new InternalCommunicationStyle(NOTIFICATION_NAME, NOTIFICATION_VALUE);

    /**
     * serial version UID for serialization
     */
    private static final long serialVersionUID = -6612138167612681693L;

    /**
     * Converts an Integer into a <code>InternalCommunicationStyle</code>.
     * 
     * @param commStyleValue
     *        The symbolic integer value of the communication style.
     * @return The corresponding communication style.
     */
    public static InternalCommunicationStyle getEnum(final int commStyleValue) {
        return (InternalCommunicationStyle) getEnum(InternalCommunicationStyle.class, commStyleValue);
    }

    /**
     * Converts a String into a <code>InternalCommunicationStyle</code>.
     * 
     * @param commStyleName
     *        The symbolic name of the communication style.
     * @return The corresponding communication style.
     */
    public static InternalCommunicationStyle getEnum(final String commStyleName) {
        return (InternalCommunicationStyle) getEnum(InternalCommunicationStyle.class, commStyleName);
    }

    /**
     * The is a list of all communication styles, in an arbitrary order.
     * 
     * @return The enumeration list.
     */
    public static List getEnumList() {
        return getEnumList(InternalCommunicationStyle.class);
    }

    /**
     * This enumeration map holds all communication styles, using InternalCommunicationStyle names
     * as keys and using InternalCommunicationStyle instances as values.
     * 
     * @return The enumeration map.
     */
    public static Map getEnumMap() {
        return getEnumMap(InternalCommunicationStyle.class);
    }

    /**
     * The iterator of the enumeration returns all communication styles, in sequence.
     * 
     * @return The communication style iterator.
     */
    public static Iterator iterator() {
        return iterator(InternalCommunicationStyle.class);
    }

    /**
     * A part of the "typesafe enumeration" protocol - hidden constructor.
     * 
     * @param name
     *        The name of the communication style.
     * @param value
     *        The numeric value of the communication style.
     */
    private InternalCommunicationStyle(final String name, final int value) {
        super(name, value);
    }
}
