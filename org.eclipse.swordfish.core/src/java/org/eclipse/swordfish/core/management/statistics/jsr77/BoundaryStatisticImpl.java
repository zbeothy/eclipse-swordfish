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
 * The Class BoundaryStatisticImpl.
 * 
 */
public class BoundaryStatisticImpl extends StatisticImpl {

    /** the upper bound. */
    private long upperBound = 0;

    /** the lower bound. */
    private long lowerBound = 0;

    /**
     * The constructor for the BoundaryStatistic.
     * 
     * @param name
     *        of the BoundaryStatistic
     * @param unit
     *        of the attribute
     * @param description
     *        of this BoundaryStatistic
     */
    public BoundaryStatisticImpl(final String name, final String unit, final String description) {
        super(name, unit, description);
    }

    /**
     * Gets the lower bound.
     * 
     * @return the lower bound
     * 
     * @see javax.management.j2ee.statistics.BoundaryStatistic#getLowerBound()
     */
    public long getLowerBound() {
        return this.lowerBound;
    }

    /**
     * Gets the upper bound.
     * 
     * @return the upper bound
     * 
     * @see javax.management.j2ee.statistics.BoundaryStatistic#getUpperBound()
     */
    public long getUpperBound() {
        return this.upperBound;
    }

    /**
     * the setter for lower bound.
     * 
     * @param lowerBound
     *        of the attribute
     */
    public void setLowerBound(final long lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * the setter for upper bound.
     * 
     * @param upperBound
     *        of the attribute
     */
    public void setUpperBound(final long upperBound) {
        this.upperBound = upperBound;
    }
}
