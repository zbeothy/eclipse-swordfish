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

package org.eclipse.swordfish.core.management.statistics.jsr77;

import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class StatisticImpl.
 * 
 */
public abstract class StatisticImpl {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(StatisticImpl.class);

    /** The name of the statistic. */
    private String name = null;

    /** The unit of measurement for this statistic. */
    private String unit = null;

    /** The description of this statistic. */
    private String description = null;

    /** The time the first measurement was taken. */
    private long startTime = 0;

    /** The time the most recent measurement was taken. */
    private long lastSampleTime = 0;

    /**
     * Instantiates a new statistic impl.
     */
    public StatisticImpl() {
        this.name = "undefined";
        this.unit = "undefined";
        this.description = "undefined";
        this.startTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Created " + this.getClass().getName() + "/" + this.hashCode() + " - default");
        }
    }

    /**
     * The constructor for the StatisticImpl.
     * 
     * @param name
     *        the name for this statistic
     * @param unit
     *        the unit for this statistic
     * @param description
     *        the description for this statistic
     */
    public StatisticImpl(final String name, final String unit, final String description) {
        this.name = name;
        this.unit = unit;
        this.description = description;
        this.startTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Created " + this.getClass().getName() + "/" + this.hashCode() + " " + name + ":" + unit + " (" + description
                    + ")");
        }
    }

    /**
     * Gets the description.
     * 
     * @return the description
     * 
     * @see javax.management.j2ee.statistics.Statistic#getDescription()
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the last sample time.
     * 
     * @return the last sample time
     * 
     * @see javax.management.j2ee.statistics.Statistic#getLastSampleTime()
     */
    public long getLastSampleTime() {
        return this.lastSampleTime;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     * 
     * @see javax.management.j2ee.statistics.Statistic#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the start time.
     * 
     * @return the start time
     * 
     * @see javax.management.j2ee.statistics.Statistic#getStartTime()
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Gets the unit.
     * 
     * @return the unit
     * 
     * @see javax.management.j2ee.statistics.Statistic#getUnit()
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *        the description
     * 
     * @see javax.management.j2ee.statistics.Statistic#getDescription()
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * The setter for the LastSampleTime.
     * 
     * @param lastSampleTime
     *        the last sample time
     */
    public void setLastSampleTime(final long lastSampleTime) {
        this.lastSampleTime = lastSampleTime;
    }

    /**
     * The setter for the name of the statistic.
     * 
     * @param name
     *        of the statistic
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * The setter for the Starttime of the measurement.
     * 
     * @param startTime
     *        of the measurement
     */
    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    /**
     * The setter for the unit.
     * 
     * @param unit
     *        for the measurement
     */
    public void setUnit(final String unit) {
        this.unit = unit;
    }
}
