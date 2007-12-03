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

/**
 * The Class History.
 */
public class History {

    /** Number of calls initiated during aggregation period. */
    private long calls;

    /** Number of calls completed during aggregation period. */
    private long completed;

    /** Number of aborts due to network failures during aggregation period. */
    private long netFail;

    /** Number of aborts due to application failures during aggregation period. */
    private long appFail;

    /** timestamp (as returned from System.currentTimeMillis()) this history was started. */
    private long tspStart;

    /** timestamp (as returned from System.currentTimeMillis()) this history was stopped. */
    private long tspStop;

    public long getAppFail() {
        return this.appFail;
    }

    public long getCalls() {
        return this.calls;
    }

    public long getCompleted() {
        return this.completed;
    }

    public long getNetFail() {
        return this.netFail;
    }

    public long getTspStart() {
        return this.tspStart;
    }

    public long getTspStop() {
        return this.tspStop;
    }

    public void setAppFail(final long appFail) {
        this.appFail = appFail;
    }

    public void setCalls(final long calls) {
        this.calls = calls;
    }

    public void setCompleted(final long completed) {
        this.completed = completed;
    }

    public void setNetFail(final long netFail) {
        this.netFail = netFail;
    }

    public void setTspStart(final long tspStart) {
        this.tspStart = tspStart;
    }

    public void setTspStop(final long tspStop) {
        this.tspStop = tspStop;
    }

}
