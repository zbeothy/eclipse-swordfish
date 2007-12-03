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
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.statistics.jsr77.CountStatisticImpl;

/**
 * Utility class to provide metrics for the occurence of events over time To ease implementation,
 * the following restrictions apply:
 * <ol>
 * <li>minimum aggregation period is 60s</li>
 * <li>averages will be computed whenever the minimum aggregation period expires</li>
 * <li>rolling averages up to maxPeriod will be provided based on the above restrictions</li>
 * </ol>.
 * 
 */
public class CountAggregator extends BaseAggregator {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(CountAggregator.class);

    /** Comment for <code>SECONDS_PER_MINUTE</code>. */
    private static final float SECONDS_PER_MINUTE = 60;

    /**
     * Constructor for the default sample period of 60 seconds.
     * 
     * @param maxPeriod
     *        the maximum number of one-minute sample periods to use
     */
    public CountAggregator(final int maxPeriod) {
        super(maxPeriod, CountStatisticImpl.class);
    }

    /**
     * Constructor with adjusted sample period.
     * 
     * @param maxPeriod
     *        the maximum number of sample periods to use
     * @param period
     *        length of one sample period in milliseconds
     */
    public CountAggregator(final int maxPeriod, final int period) {
        super(maxPeriod, period, CountStatisticImpl.class);
    }

    /**
     * Gets the average count.
     * 
     * @return the average rate in events/minute during the maximum reporting period
     */
    public int getAverageCount() {
        return this.getAverageCount(this.getSampler().getMaxPeriod());
    }

    /**
     * Gets the average count.
     * 
     * @param period
     *        (in minutes) for which the average is to be provided (0 </> period <= sample size)
     * 
     * @return the average rate in events/minute during the specified period
     */
    public int getAverageCount(final int period) {
        if ((period <= 0) || (period > this.getSampler().getMaxPeriod()))
            throw new IllegalArgumentException("Sample period out of bounds");
        Collection sample = this.getSampler().getSample(period);
        long totalCount = this.computeTotalCount(period, sample);
        int avgCount = this.normalizeCount(totalCount);
        int intervals = Math.min(period, sample.size());
        int ret = 0;
        if (intervals > 0) {
            ret = avgCount / intervals;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Ctr." + this.hashCode() + " Ttl: " + totalCount + " Avg: " + avgCount + " int: " + intervals + " ret: "
                    + ret);
        }
        return ret;
    }

    /**
     * Return the total count.
     * 
     * @return total count
     */
    public int getTotalCount() {
        return this.getTotalCount(this.getSampler().getMaxPeriod());
    }

    /**
     * Return the total count of a specific period.
     * 
     * @param period
     *        the period (0 < period <= sample size)
     * 
     * @return the total count
     */
    public synchronized int getTotalCount(final int period) {
        if ((period <= 0) || (period > this.getSampler().getMaxPeriod()))
            throw new IllegalArgumentException("Sample period out of bounds");
        Collection sample = this.getSampler().getSample(period);
        return this.computeTotalCount(period, sample);
    }

    /**
     * call this method to indicate that one relevant event has occured.
     * 
     * @param source
     *        the source
     * 
     * @see org.eclipse.swordfish.core.management.monitor.EventListener#handleEvent()
     */
    public synchronized void handleEvent(final Object source) {
        CountStatisticImpl current = (CountStatisticImpl) this.getSampler().getCurrentSample();
        current.increaseCount();
    }

    /**
     * This method provides the current values of the <code>CounterAggregator</code> as a String.
     * 
     * @return the current state as String
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("Total values: ");
        Iterator it = this.getSampler().getSample().iterator();
        while (it.hasNext()) {
            buf.append((it.next()).toString()).append(";");
        }
        return buf.toString();
    }

    /**
     * Compute total count.
     * 
     * @param period
     *        the period
     * @param sample
     *        the sample
     * 
     * @return the int
     */
    private int computeTotalCount(final int period, final Collection sample) {
        int sum = 0;
        StringBuffer msg = new StringBuffer();
        for (Iterator iter = sample.iterator(); iter.hasNext();) {
            CountStatisticImpl element = (CountStatisticImpl) iter.next();
            long val = element.getCount();
            if (LOG.isTraceEnabled()) {
                msg.append("[").append(val).append("]");
            }
            sum += val;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Counter: " + this.hashCode() + " Periods: " + period + " Available: " + this.getSampler().getSize()
                    + " Total " + sum);
            if (LOG.isTraceEnabled()) {
                LOG.trace(msg.toString());
            }
        }
        return sum;
    }

    /**
     * Normalize count to number of events per minute.
     * 
     * @param count
     *        total number of events in period
     * 
     * @return average number of events per second
     */
    private int normalizeCount(final long count) {
        final float i = 1000;
        float period = this.getSampler().getPeriodMillis();
        float factor = period / i;
        float avg = (count / factor) * SECONDS_PER_MINUTE;
        Integer rnd = new Integer(Math.round(avg));
        return rnd.intValue();
    }

}
