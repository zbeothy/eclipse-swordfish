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
 * Level for message tracking as defined in.
 * 
 * @link https://svn.servicebackbone.org/repos/SOP/ProductsAndSupport/ServiceEnablement/QoS_Policy/SystemDevelopment/Architecture/DesignSpecification/Assertion_TrackingLevel.pdf
 */
public class TrackingLevel extends AbstractEnum {

    /** No tracking (default) Might be overruled by participant settings. */
    public final static TrackingLevel NONE = new TrackingLevel("none", Integer.MAX_VALUE);

    /** Aggregated info about service calls Might be overruled by participant settings. */
    public final static TrackingLevel SUMMARY = new TrackingLevel("summary", 1000);

    /** One notification for each service call. */
    public final static TrackingLevel OPERATION = new TrackingLevel("operation", 800);

    /** One notification when message is processed by InternalSBB library. */
    public final static TrackingLevel TRACE = new TrackingLevel("trace", 400);

    /** Maximum number of notifications. */
    public final static TrackingLevel DETAIL = new TrackingLevel("detail", 300);

    /**
     * Gets the instance by name.
     * 
     * @param name
     *        the name
     * 
     * @return the instance by name
     */
    public static TrackingLevel getInstanceByName(final String name) {
        return (TrackingLevel) getInstanceByNameInternal(TrackingLevel.class, name);
    }

    /** Level of detail of a given TrackingLevel. */
    private int intval;

    /**
     * Instantiates a new tracking level.
     * 
     * @param name
     *        the name
     * @param intval
     *        the intval
     */
    private TrackingLevel(final String name, final int intval) {
        super(name);
        this.intval = intval;
    }

    /**
     * Gets the int value.
     * 
     * @return the int value
     */
    public int getIntValue() {
        return this.intval;
    }

    /**
     * Compares tracking level for filtering purposes. This method returns true if the supplied
     * <code>TrackingLevel</code> has the same or higher aggregation of details than
     * <code>this</code>. Example: <code>TrackingLevel.SUMMARY</code> has a higher aggregation
     * than <code>TrackingLevel.TRACE</code>, so
     * <code>TrackingLevel.TRACE.isIncluded(TrackingLevel.SUMMARY) == true</code>
     * 
     * @param lvl
     *        the lvl
     * 
     * @return <code>true</code> if <code>TrackingLevel</code> lvl provides the same or higher
     *         aggregation than <code>this</code>
     * <code>false</code> otherwise
     */
    public boolean isIncluded(final TrackingLevel lvl) {
        return (null != lvl) ? this.intval <= lvl.intval : false;
    }

}
