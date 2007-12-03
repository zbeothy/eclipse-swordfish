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
package org.eclipse.swordfish.core.management.messages;

import java.util.ResourceBundle;
import org.eclipse.swordfish.core.management.operations.AbstractOperationalMessage;
import org.eclipse.swordfish.papi.internal.extension.operations.InternalAbstractOperationalMessage;

/**
 * Operational log messages concerning operations of MSS.
 * 
 */
public class TrackingMessage extends AbstractOperationalMessage {

    /** Response time was execeeded for indicated MessageExchange. */
    public static final TrackingMessage RESPONSETIME_EXCEEDED = new TrackingMessage(12);

    /** The Constant TRACKING_ACTIVE. */
    public static final TrackingMessage TRACKING_ACTIVE = new TrackingMessage(2);

    /** The Constant TRACKING_FINISHED. */
    public static final TrackingMessage TRACKING_FINISHED = new TrackingMessage(9);

    /** The Constant TRACKING_ABORTED. */
    public static final TrackingMessage TRACKING_ABORTED = new TrackingMessage(10);

    /** The Constant DONE_LOCAL. */
    public static final TrackingMessage DONE_LOCAL = new TrackingMessage(7);

    /** The Constant TRACKING_SUMMARY_CONSUMER_ONEWAY. */
    public static final TrackingMessage TRACKING_SUMMARY_CONSUMER_ONEWAY = new TrackingMessage(5);

    /** The Constant TRACKING_SUMMARY_CONSUMER_INOUT. */
    public static final TrackingMessage TRACKING_SUMMARY_CONSUMER_INOUT = new TrackingMessage(6);

    /** The Constant TRACKING_SUMMARY_PROVIDER_ONEWAY. */
    public static final TrackingMessage TRACKING_SUMMARY_PROVIDER_ONEWAY = new TrackingMessage(5);

    /** The Constant TRACKING_SUMMARY_PROVIDER_INOUT. */
    public static final TrackingMessage TRACKING_SUMMARY_PROVIDER_INOUT = new TrackingMessage(6);

    /** The Constant TRACKING_METADATA. */
    public static final TrackingMessage TRACKING_METADATA = new TrackingMessage(11);

    /**
     * The Constructor.
     * 
     * @param val
     *        the val
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int)
     */
    protected TrackingMessage(final int val) {
        super(val);
    }

    /**
     * The Constructor.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int, ResourceBundle)
     */
    protected TrackingMessage(final int arg0, final ResourceBundle arg1) {
        super(arg0, arg1);
    }

    /**
     * The Constructor.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @see InternalAbstractOperationalMessage#AbstractOperationalMessage(int, String)
     */
    protected TrackingMessage(final int arg0, final String arg1) {
        super(arg0, arg1);
    }

}
