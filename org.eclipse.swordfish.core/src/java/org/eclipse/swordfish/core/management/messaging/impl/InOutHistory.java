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
package org.eclipse.swordfish.core.management.messaging.impl;

import org.eclipse.swordfish.core.management.statistics.AverageBoundaryStatistic;

/**
 * The Class InOutHistory.
 */
class InOutHistory extends History {

    /** Statistics on response times during aggregation period. */
    private AverageBoundaryStatistic statistic = new AverageBoundaryStatistic();

    public AverageBoundaryStatistic getStatistic() {
        return this.statistic;
    }

    public void setStatistic(final AverageBoundaryStatistic statistic) {
        this.statistic = statistic;
    }

}
