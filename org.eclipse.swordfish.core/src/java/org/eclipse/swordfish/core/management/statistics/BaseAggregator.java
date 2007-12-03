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

import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * Common base class for time-based aggregators.
 * 
 */
public class BaseAggregator {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(BaseAggregator.class);

    /** Holds the collected samples. */
    private TimedSampler sampler;

    /** The max period. */
    private int maxPeriod;

    /** The period. */
    private int period;

    /** The sampler class. */
    private Class samplerClass;

    /**
     * Constructor for the default sample period of 60 seconds.
     * 
     * @param maxPeriod
     *        the maximum number of one-minute sample periods to use
     * @param clazz
     *        the Class of sample objects (must have nullary constructor)
     */
    public BaseAggregator(final int maxPeriod, final Class clazz) {
        try {
            this.sampler = new TimedSampler(maxPeriod, clazz);
            this.maxPeriod = maxPeriod;
            this.period = (int) TimedSampler.PERIOD_MILLIS;
            this.samplerClass = clazz;
        } catch (InstantiationException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Exception in constructor - no statistics will be collected", e);
            }
        } catch (IllegalAccessException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Exception in constructor - no statistics will be collected", e);
            }
        }
    }

    /**
     * Constructor with adjusted sample period.
     * 
     * @param maxPeriod
     *        the maximum number of sample periods to use
     * @param period
     *        length of one sample period in milliseconds
     * @param clazz
     *        the Class of sample objects (must have nullary constructor)
     */
    public BaseAggregator(final int maxPeriod, final int period, final Class clazz) {
        try {
            this.sampler = new TimedSampler(maxPeriod, period, clazz);
            this.maxPeriod = maxPeriod;
            this.period = period;
            this.samplerClass = clazz;
        } catch (InstantiationException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Exception in constructor - no statistics will be collected", e);
            }
        } catch (IllegalAccessException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Exception in constructor - no statistics will be collected", e);
            }
        }
    }

    /**
     * Gets the max period.
     * 
     * @return Returns the maximum reporting period.
     */
    public int getMaxPeriod() {
        return this.sampler.getMaxPeriod();
    }

    public TimedSampler getSampler() {
        return this.sampler;
    }

    /**
     * Reset.
     */
    public void reset() {
        try {
            TimedSampler newSampler = new TimedSampler(this.maxPeriod, this.period, this.samplerClass);
            this.sampler = newSampler;
        } catch (InstantiationException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Exception during reset - continue using old sampler", e);
            }
        } catch (IllegalAccessException e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Exception during reset - continue using old sampler", e);
            }
        }
    }

    /**
     * Sets the max period.
     * 
     * @param maxPeriod
     *        The maximum reporting period to set.
     */
    public void setMaxPeriod(final int maxPeriod) {
        this.sampler.setMaxPeriod(maxPeriod);
    }

    public void setSampler(final TimedSampler sampler) {
        this.sampler = sampler;
    }

}
