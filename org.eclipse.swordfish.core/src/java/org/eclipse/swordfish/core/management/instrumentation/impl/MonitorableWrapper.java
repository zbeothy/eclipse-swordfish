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
package org.eclipse.swordfish.core.management.instrumentation.impl;

import org.eclipse.swordfish.core.management.statistics.jsr77.State;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalMonitorable;

/**
 * A simple wrapper for <code>InternalMonitorable</code> instances that converts the state
 * attribute to a string value for easier monitoring.
 * 
 */
public class MonitorableWrapper {

    /** The wrapped. */
    private InternalMonitorable wrapped;

    /**
     * Instantiates a new monitorable wrapper.
     * 
     * @param obj
     *        the obj
     */
    public MonitorableWrapper(final InternalMonitorable obj) {
        this.wrapped = obj;
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public String getState() {
        return this.wrapped.getState().toString();
    }

    /**
     * Gets the state internal.
     * 
     * @return the state internal
     */
    public State getStateInternal() {
        String name = this.wrapped.getState().toString();
        return State.getByName(name);
    }

}
