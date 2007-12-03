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
 * The Class RangeStatisticImpl.
 * 
 */
public class RangeStatisticImpl extends StatisticImpl {

    /** the high watermark of an attribute. */
    private long highWaterMark = 0;

    /** the low watermark of an attribute. */
    private long lowWaterMark = 0;

    /** the current value of an attribute. */
    private long current = 0;

    /**
     * The constructor for RangeStatisticImpl.
     * 
     * @param name
     *        the statistic name of the RangeStatistic
     * @param unit
     *        the unit of the attribute
     * @param description
     *        the statistic description of the RangeStatistic
     */
    public RangeStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
    }

    /**
     * To add a new Attribute value to the <code>RangeStatistic</code>.
     * 
     * @param value
     *        the attribute value
     */
    public void addAttributeValue(final long value) {
        if (value > this.highWaterMark) {
            this.highWaterMark = value;
        }

        if (value < this.lowWaterMark) {
            this.lowWaterMark = value;
        }

        this.current = value;
        this.setLastSampleTime(System.currentTimeMillis());
    }

    /**
     * Gets the current.
     * 
     * @return the current
     * 
     * @see javax.management.j2ee.statistics.RangeStatistic#getCurrent()
     */
    public long getCurrent() {
        return this.current;
    }

    /**
     * Gets the high water mark.
     * 
     * @return the high water mark
     * 
     * @see javax.management.j2ee.statistics.RangeStatistic#getHighWaterMark()
     */
    public long getHighWaterMark() {
        return this.highWaterMark;
    }

    /**
     * Gets the low water mark.
     * 
     * @return the low water mark
     * 
     * @see javax.management.j2ee.statistics.RangeStatistic#getLowWaterMark()
     */
    public long getLowWaterMark() {
        return this.lowWaterMark;
    }
}
