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
package org.eclipse.swordfish.core.management.statistics;

/**
 * The Class AverageBoundaryStatistic.
 * 
 */
public class AverageBoundaryStatistic {

    /** sum of values of all samples. */
    private long total;

    /** lowest sample value. */
    private long lowWatermark;

    /** highest sample value. */
    private long highWatermark;

    /** number of samples. */
    private int count;

    /**
     * Constructor.
     */
    public AverageBoundaryStatistic() {
        this.total = 0;
        this.lowWatermark = Long.MAX_VALUE;
        this.highWatermark = Long.MIN_VALUE;
        this.count = 0;
    }

    /**
     * Adds the value.
     * 
     * @param value
     *        the value
     */
    public void addValue(final long value) {
        this.total += value;
        this.count++;
        this.highWatermark = Math.max(this.highWatermark, value);
        this.lowWatermark = Math.min(this.lowWatermark, value);
    }

    /**
     * Gets the average.
     * 
     * @return average value
     */
    public long getAverage() {
        long ret = 0;
        if (0 != this.count) {
            ret = this.total / this.count;
        }
        return ret;
    }

    /**
     * Gets the count.
     * 
     * @return Returns the count.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Gets the high watermark.
     * 
     * @return Returns the highWatermark.
     */
    public long getHighWatermark() {
        return this.highWatermark;
    }

    /**
     * Gets the low watermark.
     * 
     * @return Returns the lowWatermark.
     */
    public long getLowWatermark() {
        return this.lowWatermark;
    }

    /**
     * Gets the total.
     * 
     * @return Returns the total.
     */
    public long getTotal() {
        return this.total;
    }
}
