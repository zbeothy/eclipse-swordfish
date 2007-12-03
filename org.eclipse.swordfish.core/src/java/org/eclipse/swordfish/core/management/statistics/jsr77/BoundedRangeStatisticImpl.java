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
 * The <code>BoundedRangeStatisticImpl</code> is the implementation of the
 * <code>BoundedRangeStatistic</code> Interface. This implementation provides standard
 * measurements of a range that has fixed limits.
 * 
 */
public class BoundedRangeStatisticImpl extends StatisticImpl {

    /** The BoundaryStatistic. */
    private BoundaryStatisticImpl boundary = null;

    /** The RangeStatistic. */
    private RangeStatisticImpl range = null;

    /**
     * The constructor for the BoundedRangeStatisticImpl.
     * 
     * @param name
     *        of the BoundedRangeStatistic
     * @param unit
     *        of the attribute
     * @param description
     *        the description of the BoundedRangeStatistic
     */
    public BoundedRangeStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
        this.boundary = new BoundaryStatisticImpl("boundary/" + name, unit, description);
        this.range = new RangeStatisticImpl("range/" + name, unit, description);

    }

    /**
     * To add a new Attribute value to the <code>RangeStatistic</code>.
     * 
     * @param value
     *        the attribute value
     */
    public void addAttributeValue(final long value) {
        this.range.addAttributeValue(value);
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
        return this.range.getCurrent();
    }

    /**
     * Gets the high water mark.
     * 
     * @return the high water mark
     * 
     * @see javax.management.j2ee.statistics.RangeStatistic#getHighWaterMark()
     */
    public long getHighWaterMark() {
        return this.range.getHighWaterMark();
    }

    /**
     * Gets the lower bound.
     * 
     * @return the lower bound
     * 
     * @see javax.management.j2ee.statistics.BoundaryStatistic#getLowerBound()
     */
    public long getLowerBound() {
        return this.boundary.getLowerBound();
    }

    /**
     * Gets the low water mark.
     * 
     * @return the low water mark
     * 
     * @see javax.management.j2ee.statistics.RangeStatistic#getLowWaterMark()
     */
    public long getLowWaterMark() {
        return this.range.getLowWaterMark();
    }

    /**
     * Gets the upper bound.
     * 
     * @return the upper bound
     * 
     * @see javax.management.j2ee.statistics.BoundaryStatistic#getUpperBound()
     */
    public long getUpperBound() {
        return this.boundary.getUpperBound();
    }

    /**
     * the setter for lower bound.
     * 
     * @param lowerBound
     *        of an attriubte
     */
    public void setLowerBound(final long lowerBound) {
        this.boundary.setLowerBound(lowerBound);
    }

    /**
     * the setter for upper bound.
     * 
     * @param upperBound
     *        of an attribute
     */
    public void setUpperBound(final long upperBound) {
        this.boundary.setUpperBound(upperBound);
    }
}
