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
 * The Class CountStatisticImpl.
 * 
 */
public class CountStatisticImpl extends StatisticImpl {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(CountStatisticImpl.class);

    /** The current count. */
    private long count = 0;

    /**
     * Instantiates a new count statistic impl.
     */
    public CountStatisticImpl() {
        super();
    }

    /**
     * The constructor for CountStatisticImpl.
     * 
     * @param name
     *        of this CountStatistic
     * @param unit
     *        of the Statistic
     * @param description
     *        of this CountStatistic
     */
    public CountStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);

    }

    /**
     * Gets the count.
     * 
     * @return the count
     * 
     * @see javax.management.j2ee.statistics.CountStatistic#getCount()
     */
    public long getCount() {
        return this.count;
    }

    /**
     * Call this method to increase the count by one.
     */
    public void increaseCount() {
        this.count = this.count + 1;
        this.setLastSampleTime(System.currentTimeMillis());
        if (LOG.isTraceEnabled()) {
            LOG.trace("Counter " + this.hashCode() + " : " + this.count);
        }
    }

    /**
     * The setter for count.
     * 
     * @param count
     *        of the CounterStatistic
     */
    public void setCount(final long count) {
        this.count = count;
    }
}
