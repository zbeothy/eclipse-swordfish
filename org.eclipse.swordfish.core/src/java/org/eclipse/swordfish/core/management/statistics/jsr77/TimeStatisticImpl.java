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

/**
 * The Class TimeStatisticImpl.
 * 
 */
public class TimeStatisticImpl extends StatisticImpl {

    /** The current count. */
    private long count = 0;

    /** The max time for a single process. */
    private long maxTime = 0;

    /** The min time for a single process. */
    private long minTime = 0;

    /** The total time for all processes. */
    private long totalTime = 0;

    /** indicates if the TimeStatistic is called the first time. */
    private boolean isFirstCall = true;

    /**
     * The constructor for the TimeStatisticImpl.
     * 
     * @param name
     *        the name for this statistic
     * @param unit
     *        the unit for this statistic
     * @param description
     *        the description for this statistic
     */
    public TimeStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
    }

    /**
     * This method provides a component the possiblity to add processtime for a workstep.
     * 
     * @param processTime
     *        the processtime for one workstep
     */
    public void addProcessTime(final long processTime) {
        if (this.getStartTime() == 0) {
            this.setStartTime(System.currentTimeMillis());
        }
        if (processTime > this.maxTime) {
            this.maxTime = processTime;
        }

        if (processTime < this.minTime) {
            this.minTime = processTime;
        } else if (this.isFirstCall) {
            this.minTime = processTime;
            this.isFirstCall = false;
        }

        this.count++;
        this.totalTime = this.totalTime + processTime;
        this.setLastSampleTime(System.currentTimeMillis());
    }

    /**
     * Return the current count.
     * 
     * @return the count
     * 
     * @see javax.management.j2ee.statistics.TimeStatistic#getCount()
     */
    public long getCount() {
        return this.count;
    }

    /**
     * Return the current max time.
     * 
     * @return the max time
     * 
     * @see javax.management.j2ee.statistics.TimeStatistic#getMaxTime()
     */
    public long getMaxTime() {
        return this.maxTime;
    }

    /**
     * Return the current min time.
     * 
     * @return the min time
     * 
     * @see javax.management.j2ee.statistics.TimeStatistic#getMinTime()
     */
    public long getMinTime() {
        return this.minTime;
    }

    /**
     * Return the total process time.
     * 
     * @return the total time
     * 
     * @see javax.management.j2ee.statistics.TimeStatistic#getTotalTime()
     */
    public long getTotalTime() {
        return this.totalTime;
    }

    /**
     * The setter for the count.
     * 
     * @param count
     *        the count
     */
    public void setCount(final long count) {
        this.count = count;
    }

    /**
     * The setter for the max time.
     * 
     * @param maxTime
     *        the max time
     */
    public void setMaxTime(final long maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * The setter for the min time.
     * 
     * @param minTime
     *        the min time
     */
    public void setMinTime(final long minTime) {
        this.minTime = minTime;
    }

    /**
     * The setter for the totaltime.
     * 
     * @param totalTime
     *        the total time
     */
    public void setTotalTime(final long totalTime) {
        this.totalTime = totalTime;
    }
}
