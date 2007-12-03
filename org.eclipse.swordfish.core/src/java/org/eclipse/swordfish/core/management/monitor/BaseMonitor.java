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
package org.eclipse.swordfish.core.management.monitor;

import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.statistics.AverageBoundaryAggregator;
import org.eclipse.swordfish.core.management.statistics.BaseAggregator;
import org.eclipse.swordfish.core.management.statistics.CountAggregator;

/**
 * Base class for all monitors that report message processing.
 * 
 */
public class BaseMonitor implements IBaseMonitor {

    /** Comment for <code>DEFAULT_AGGREGATION_PERIOD</code>. */
    protected static final Integer DEFAULT_LONG_AGGREGATION_PERIOD = new Integer(60);

    /** Comment for <code>DEFAULT_MEDIUM_AGGREGATION_PERIOD</code>. */
    protected static final Integer DEFAULT_MEDIUM_AGGREGATION_PERIOD = new Integer(15);

    /** Comment for <code>DEFAULT_SHORT_AGGREGATION_PERIOD</code>. */
    protected static final Integer DEFAULT_SHORT_AGGREGATION_PERIOD = new Integer(5);

    /** The log. */
    private static Log log = SBBLogFactory.getLog(BaseMonitor.class);

    /** Total number of requests processed. */
    private CountAggregator reqCounter;

    /** statistics on response times. */
    private AverageBoundaryAggregator responseTimeAggregator;

    /** Selected reporting period. */
    private Integer reqRatePeriod1;

    /** Selected reporting period. */
    private Integer reqRatePeriod2;

    /** Selected reporting period. */
    private Integer reqRatePeriod3;

    /** Number of requests failed due to application problems. */
    private CountAggregator appFailCounter;

    /** Number of requests failed due to network problems. */
    private CountAggregator netFailCounter;

    /** Selected reporting period. */
    private Integer appFailPeriod1;

    /** Selected reporting period. */
    private Integer appFailPeriod2;

    /** Selected reporting period. */
    private Integer appFailPeriod3;

    /** Selected reporting period. */
    private Integer netFailPeriod;

    /** Selected reporting period. */
    private Integer avgResponseTimePeriod;

    /** Selected reporting period. */
    private Integer minResponseTimePeriod;

    /** Selected reporting period. */
    private Integer maxResponseTimePeriod;

    /** Selected reporting period. */
    private Integer totalRequestsPeriod;

    /**
     * (non-Javadoc).
     * 
     * @param time
     *        the time
     * 
     * @see org.eclipse.swordfish.core.management.monitor.IBaseMonitor#addResponseTime(long)
     */
    public void addResponseTime(final long time) {
        this.responseTimeAggregator.addValue(time);
    }

    /**
     * Destroy.
     */
    public void destroy() {
        // don't null the counters, could cause npe's
        this.netFailCounter = new CountAggregator(1);
        this.appFailCounter = this.netFailCounter;
        this.reqCounter = this.appFailCounter;
        this.responseTimeAggregator = new AverageBoundaryAggregator(1);
        if (log.isDebugEnabled()) {
            log.debug("Destroyed " + this.getClass().getName() + ":" + this.hashCode());
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @return the avg response time
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getAvgResponseTime()
     */
    public Integer getAvgResponseTime() {
        Long time = new Long(this.responseTimeAggregator.getAverage(this.avgResponseTimePeriod.intValue()));
        int ret = time.intValue();
        return new Integer(ret);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the avg response time period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getAvgResponseTimePeriod()
     */
    public Integer getAvgResponseTimePeriod() {
        return this.avgResponseTimePeriod;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the max response time
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getMaxResponseTime()
     */
    public Integer getMaxResponseTime() {
        Long time = new Long(this.responseTimeAggregator.getHighWatermark(this.maxResponseTimePeriod.intValue()));
        int ret = time.intValue();
        return new Integer(ret);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the max response time period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getMaxResponseTimePeriod()
     */
    public Integer getMaxResponseTimePeriod() {
        return this.maxResponseTimePeriod;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the min response time
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getMinResponseTime()
     */
    public Integer getMinResponseTime() {
        Long time = new Long(this.responseTimeAggregator.getLowWatermark(this.minResponseTimePeriod.intValue()));
        int ret = time.intValue();
        return new Integer(ret);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the min response time period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getMinResponseTimePeriod()
     */
    public Integer getMinResponseTimePeriod() {
        return this.minResponseTimePeriod;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct app fail1
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getPctAppFail1()
     */
    public Integer getPctAppFail1() {
        return this.computePctFail(this.reqCounter, this.appFailCounter, this.appFailPeriod1);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct app fail1 period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getPctAppFail1Period()
     */
    public Integer getPctAppFail1Period() {
        return this.appFailPeriod1;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct app fail2
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getPctAppFail2()
     */
    public Integer getPctAppFail2() {
        return this.computePctFail(this.reqCounter, this.appFailCounter, this.appFailPeriod2);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct app fail2 period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getPctAppFail2Period()
     */
    public Integer getPctAppFail2Period() {
        return this.appFailPeriod2;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct app fail3
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getPctAppFail3()
     */
    public Integer getPctAppFail3() {
        return this.computePctFail(this.reqCounter, this.appFailCounter, this.appFailPeriod3);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct app fail3 period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getPctAppFail3Period()
     */
    public Integer getPctAppFail3Period() {
        return this.appFailPeriod3;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct net fail
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getPctNetFail()
     */
    public Integer getPctNetFail() {
        return this.computePctFail(this.reqCounter, this.netFailCounter, this.netFailPeriod);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the pct net fail period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getPctNetFailPeriod()
     */
    public Integer getPctNetFailPeriod() {
        return this.netFailPeriod;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the req rate1
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getReqRate1()
     */
    public Integer getReqRate1() {
        return new Integer(this.reqCounter.getAverageCount(this.reqRatePeriod1.intValue()));
    }

    /**
     * (non-Javadoc).
     * 
     * @return the req rate1 period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getReqRate1Period()
     */
    public Integer getReqRate1Period() {
        return this.reqRatePeriod1;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the req rate2
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getReqRate2()
     */
    public Integer getReqRate2() {
        return new Integer(this.reqCounter.getAverageCount(this.reqRatePeriod2.intValue()));
    }

    /**
     * (non-Javadoc).
     * 
     * @return the req rate2 period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getReqRate2Period()
     */
    public Integer getReqRate2Period() {
        return this.reqRatePeriod2;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the req rate3
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getReqRate3()
     */
    public Integer getReqRate3() {
        return new Integer(this.reqCounter.getAverageCount(this.reqRatePeriod3.intValue()));
    }

    /**
     * (non-Javadoc).
     * 
     * @return the req rate3 period
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getReqRate3Period()
     */
    public Integer getReqRate3Period() {
        return this.reqRatePeriod3;
    }

    /**
     * Gets the total requests.
     * 
     * @return the total requests
     */
    public Integer getTotalRequests() {
        return new Integer(this.reqCounter.getTotalCount(this.totalRequestsPeriod.intValue()));
    }

    /**
     * Gets the total requests period.
     * 
     * @return the total requests period
     */
    public Integer getTotalRequestsPeriod() {
        return this.totalRequestsPeriod;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.monitor.IBaseMonitor#handleAppFailEvent()
     */
    public void handleAppFailEvent() {
        this.appFailCounter.handleEvent(null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.monitor.IBaseMonitor#handleNetFailEvent()
     */
    public void handleNetFailEvent() {
        this.netFailCounter.handleEvent(null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.monitor.IBaseMonitor#handleRequestEvent()
     */
    public void handleRequestEvent() {
        this.reqCounter.handleEvent(null);
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.core.framework.activity.Initializable#initialize()
     */
    public void initialize() {
        this.reqCounter = new CountAggregator(DEFAULT_LONG_AGGREGATION_PERIOD.intValue());
        this.appFailCounter = new CountAggregator(DEFAULT_LONG_AGGREGATION_PERIOD.intValue());
        this.netFailCounter = new CountAggregator(DEFAULT_LONG_AGGREGATION_PERIOD.intValue());
        this.responseTimeAggregator = new AverageBoundaryAggregator(DEFAULT_LONG_AGGREGATION_PERIOD.intValue());
        this.setReqRate1Period(DEFAULT_SHORT_AGGREGATION_PERIOD);
        this.setReqRate2Period(DEFAULT_MEDIUM_AGGREGATION_PERIOD);
        this.setReqRate3Period(DEFAULT_LONG_AGGREGATION_PERIOD);
        this.setPctAppFail1Period(DEFAULT_SHORT_AGGREGATION_PERIOD);
        this.setPctAppFail2Period(DEFAULT_MEDIUM_AGGREGATION_PERIOD);
        this.setPctAppFail3Period(DEFAULT_MEDIUM_AGGREGATION_PERIOD);
        this.setPctNetFailPeriod(DEFAULT_LONG_AGGREGATION_PERIOD);
        this.setAvgResponseTimePeriod(DEFAULT_LONG_AGGREGATION_PERIOD);
        this.setMinResponseTimePeriod(DEFAULT_LONG_AGGREGATION_PERIOD);
        this.setMaxResponseTimePeriod(DEFAULT_LONG_AGGREGATION_PERIOD);
        this.setTotalRequestsPeriod(DEFAULT_SHORT_AGGREGATION_PERIOD);
        if (log.isDebugEnabled()) {
            log.debug("Created " + this.getClass().getName() + ":" + this.hashCode() + "\n reqCounter: "
                    + this.reqCounter.hashCode() + "\n appFailCounter: " + this.appFailCounter.hashCode() + "\n netFailCounter: "
                    + this.netFailCounter.hashCode() + "\n timeAggregator: " + this.responseTimeAggregator.hashCode());
        }
    }

    /**
     * Reset statistics.
     */
    public void resetStatistics() {
        this.reqCounter.reset();
        this.appFailCounter.reset();
        this.netFailCounter.reset();
        this.responseTimeAggregator.reset();
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setAvgResponseTimePeriod(java.lang.Integer)
     */
    public void setAvgResponseTimePeriod(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.responseTimeAggregator, min);
        this.avgResponseTimePeriod = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setMaxResponseTimePeriod(java.lang.Integer)
     */
    public void setMaxResponseTimePeriod(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.responseTimeAggregator, min);
        this.maxResponseTimePeriod = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setMinResponseTimePeriod(java.lang.Integer)
     */
    public void setMinResponseTimePeriod(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.responseTimeAggregator, min);
        this.minResponseTimePeriod = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setPctAppFail1Period(java.lang.Integer)
     */
    public void setPctAppFail1Period(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.appFailCounter, min);
        this.checkMaxPeriod(this.reqCounter, min);
        this.appFailPeriod1 = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setPctAppFail2Period(java.lang.Integer)
     */
    public void setPctAppFail2Period(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.appFailCounter, min);
        this.checkMaxPeriod(this.reqCounter, min);
        this.appFailPeriod2 = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setPctAppFail3Period(java.lang.Integer)
     */
    public void setPctAppFail3Period(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.appFailCounter, min);
        this.checkMaxPeriod(this.reqCounter, min);
        this.appFailPeriod3 = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setPctNetFailPeriod(java.lang.Integer)
     */
    public void setPctNetFailPeriod(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.netFailCounter, min);
        this.checkMaxPeriod(this.reqCounter, min);
        this.netFailPeriod = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setReqRate1Period(java.lang.Integer)
     */
    public void setReqRate1Period(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;

        this.checkMaxPeriod(this.reqCounter, min);
        this.reqRatePeriod1 = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setReqRate2Period(java.lang.Integer)
     */
    public void setReqRate2Period(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.reqCounter, min);
        this.reqRatePeriod2 = min;
    }

    /**
     * (non-Javadoc).
     * 
     * @param minutes
     *        the minutes
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#setReqRate3Period(java.lang.Integer)
     */
    public void setReqRate3Period(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.reqCounter, min);
        this.reqRatePeriod3 = min;
    }

    /**
     * Sets the total requests period.
     * 
     * @param minutes
     *        the new total requests period
     */
    public void setTotalRequestsPeriod(final Integer minutes) {
        final Integer min = (minutes.intValue() <= 0) ? new Integer(1) : minutes;
        this.checkMaxPeriod(this.reqCounter, min);
        this.totalRequestsPeriod = min;
    }

    /**
     * Ensure the maximum reporting period of aggregator is sufficient.
     * 
     * @param counter
     *        to check
     * @param minutes
     *        required max reporting period
     */
    private void checkMaxPeriod(final BaseAggregator counter, final Integer minutes) {
        int currentMax = counter.getMaxPeriod();
        if (minutes.intValue() > currentMax) {
            counter.setMaxPeriod(minutes.intValue());
        }
    }

    /**
     * Computes the percentage of failures during a given time.
     * 
     * @param allCount
     *        counter for overall events
     * @param failCount
     *        counter for failed events
     * @param period
     *        time in minutes for which to compute percentage
     * 
     * @return percentage of failed events during last <code>period</code> minutes
     */
    private Integer computePctFail(final CountAggregator allCount, final CountAggregator failCount, final Integer period) {
        int ret = 0;
        int all = allCount.getTotalCount(period.intValue());
        if (0 != all) {
            int fail = failCount.getTotalCount(period.intValue());
            ret = fail * 100 / all;
        }
        return new Integer(ret);
    }

}
