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
import java.util.LinkedList;
import java.util.Vector;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class TimedSampler.
 * 
 */
public class TimedSampler {

    /** Default sample period in milliseconds. */
    public static final long PERIOD_MILLIS = 60000;

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(TimedSampler.class);

    /** Sample period in milliseconds. */
    private long periodMillis;

    /** time when current sample period ends. */
    private long currentCutoff;

    /** maximum number of periods to aggregate data for. */
    private int maxPeriod;

    /** aggregated averages for past sample periods up to maxPeriod. */
    private LinkedList sample;

    /** Class of sample objects. */
    private Class sampleClass;

    /** The current sample object. */
    private Object currentSample;

    /**
     * Constructor for the default sample period of 60 seconds.
     * 
     * @param maxPeriod
     *        the maximum number of one-minute sample periods to use
     * @param sampleClass
     *        the Class of sample objects (must have nullary constructor)
     * 
     * @throws IllegalAccessException
     *         see java.lang.Class.newInstance()
     * @throws InstantiationException
     *         see java.lang.Class.newInstance()
     */
    public TimedSampler(final int maxPeriod, final Class sampleClass) throws InstantiationException, IllegalAccessException {
        this.maxPeriod = maxPeriod;
        this.periodMillis = PERIOD_MILLIS;
        this.init(sampleClass);
    }

    /**
     * Constructor for arbitrary sample periods.
     * 
     * @param maxPeriod
     *        the maximum number of sample periods to use
     * @param periodMillis
     *        the length of one sample period in millisecondds
     * @param sampleClass
     *        the Class of sample objects (must have nullary constructor)
     * 
     * @throws InstantiationException
     *         see java.lang.Class.newInstance()
     * @throws IllegalAccessException
     *         see java.lang.Class.newInstance()
     */
    public TimedSampler(final int maxPeriod, final long periodMillis, final Class sampleClass) throws InstantiationException,
            IllegalAccessException {
        this.maxPeriod = maxPeriod;
        this.periodMillis = periodMillis;
        this.init(sampleClass);
    }

    /**
     * Computes the sample object for the current sample period, creating sample periods for the
     * meantime if necessary.
     * 
     * @return the sample object for the current sample period
     */
    public synchronized Object getCurrentSample() {
        this.syncPeriod();
        return this.currentSample;
    }

    /**
     * Gets the max period.
     * 
     * @return Returns the maximum reporting period.
     */
    public int getMaxPeriod() {
        return this.maxPeriod;
    }

    /**
     * Gets the period millis.
     * 
     * @return the period millis
     */
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    /**
     * Gets the sample.
     * 
     * @return the full sample
     */
    public synchronized Collection getSample() {
        this.syncPeriod();
        return (Collection) this.sample.clone();
    }

    /**
     * Gets the sample.
     * 
     * @param periods
     *        number of periods to get sample for
     * 
     * @return sample for <code>periods</code> periods
     */
    public synchronized Collection getSample(final int periods) {
        this.syncPeriod();
        int start = this.sample.size() - periods;
        if (start < 0) {
            start = 0;
        }
        return new Vector(this.sample.subList(start, this.sample.size()));
    }

    /**
     * Gets the size.
     * 
     * @return the size
     */
    public int getSize() {
        return this.sample.size();
    }

    /**
     * Sets the max period.
     * 
     * @param maxPeriod
     *        The maximum reporting period to set.
     */
    public synchronized void setMaxPeriod(final int maxPeriod) {
        this.maxPeriod = maxPeriod;
    }

    /**
     * add a new sample object and make it the <code>currentSample</code>.
     */
    private void addSample() {
        try {
            this.currentSample = this.sampleClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            // can't happen if constructor completes
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            // can't happen if constructor completes
        }
        this.sample.add(this.currentSample);
        while (this.sample.size() > this.maxPeriod) {
            this.sample.removeFirst();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Add sample to " + this.hashCode() + " (" + this.sample.size() + ")");
            if (LOG.isTraceEnabled()) {
                StringBuffer buf = new StringBuffer();
                for (Iterator iter = this.sample.iterator(); iter.hasNext();) {
                    Object element = iter.next();
                    buf.append("[").append(element.hashCode()).append("]");
                }
                LOG.trace(buf.toString());
            }
        }
    }

    /**
     * Init.
     * 
     * @param cSampleClass
     *        the sample class
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private void init(final Class cSampleClass) throws InstantiationException, IllegalAccessException {
        this.currentCutoff = System.currentTimeMillis() + this.periodMillis;
        this.sample = new LinkedList();
        this.sampleClass = cSampleClass;
        this.currentSample = cSampleClass.newInstance();
        this.sample.add(this.currentSample);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Created " + this.hashCode() + "/" + cSampleClass.getName() + " " + this.maxPeriod + "/" + this.periodMillis);
        }
    }

    /**
     * start new sample period if current period has expired.
     */
    private void syncPeriod() {
        long tick = System.currentTimeMillis();
        while (tick > this.currentCutoff) {
            this.addSample();
            this.currentCutoff = this.currentCutoff + this.periodMillis;
        }
    }

}
