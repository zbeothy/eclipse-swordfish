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

import java.util.Collection;
import java.util.Iterator;

/**
 * Utility class to compute averages of a value for different time periods.
 * 
 */
public class AverageBoundaryAggregator extends BaseAggregator {

    /**
     * The Constructor.
     * 
     * @param maxPeriod
     *        max number of periods to consider
     */
    public AverageBoundaryAggregator(final int maxPeriod) {
        super(maxPeriod, AverageBoundaryStatistic.class);
    }

    /**
     * The Constructor.
     * 
     * @param maxPeriod
     *        max number of periods to consider
     * @param period
     *        length of one sample period in milliseconds
     */
    public AverageBoundaryAggregator(final int maxPeriod, final int period) {
        super(maxPeriod, period, AverageBoundaryStatistic.class);
    }

    /**
     * Adds the value.
     * 
     * @param value
     *        to add to sample
     */
    public synchronized void addValue(final long value) {
        AverageBoundaryStatistic current = (AverageBoundaryStatistic) this.getSampler().getCurrentSample();
        current.addValue(value);
    }

    /**
     * Gets the average.
     * 
     * @return average for full sample
     */
    public long getAverage() {
        return this.getAverage(this.getSampler().getMaxPeriod());
    }

    /**
     * Gets the average.
     * 
     * @param periods
     *        to use
     * 
     * @return average for last <code>periods</code> periods
     */
    public synchronized long getAverage(final int periods) {
        Collection sample = this.getSampler().getSample(periods);
        long total = 0;
        int count = 0;
        for (Iterator iter = sample.iterator(); iter.hasNext();) {
            AverageBoundaryStatistic element = (AverageBoundaryStatistic) iter.next();
            total += element.getTotal();
            count += element.getCount();
        }
        long ret = 0;
        if (0 != count) {
            ret = total / count;
        }
        return ret;
    }

    /**
     * Gets the high watermark.
     * 
     * @return high watermark for full sample
     */
    public long getHighWatermark() {
        return this.getHighWatermark(this.getSampler().getMaxPeriod());
    }

    /**
     * Gets the high watermark.
     * 
     * @param periods
     *        to use
     * 
     * @return high watermark for last <code>periods</code> periods
     */
    public synchronized long getHighWatermark(final int periods) {
        Collection sample = this.getSampler().getSample(periods);
        long ret = Long.MIN_VALUE;
        for (Iterator iter = sample.iterator(); iter.hasNext();) {
            AverageBoundaryStatistic element = (AverageBoundaryStatistic) iter.next();
            ret = Math.max(ret, element.getHighWatermark());
        }
        return ret;
    }

    /**
     * Gets the low watermark.
     * 
     * @return low watermark for full sample
     */
    public long getLowWatermark() {
        return this.getLowWatermark(this.getSampler().getMaxPeriod());
    }

    /**
     * Gets the low watermark.
     * 
     * @param periods
     *        to use
     * 
     * @return low watermark for last <code>periods</code> periods
     */
    public synchronized long getLowWatermark(final int periods) {
        Collection sample = this.getSampler().getSample(periods);
        long ret = Long.MAX_VALUE;
        for (Iterator iter = sample.iterator(); iter.hasNext();) {
            AverageBoundaryStatistic element = (AverageBoundaryStatistic) iter.next();
            ret = Math.min(ret, element.getLowWatermark());
        }
        return ret;
    }

}
