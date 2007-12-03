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

import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.instrumentation.impl.MonitorableWrapper;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;

/**
 * The Class ParticipantMonitor.
 */
public class ParticipantMonitor extends BaseMonitor {

    /** Monitorables for this participant Value: MonitorableWrapper. */
    private HashSet monitorables;

    /** InternalSBB participant this monitor is responsible for. */
    private UnifiedParticipantIdentity participantId;

    /**
     * Instantiates a new participant monitor.
     * 
     * @param participant
     *        the participant
     */
    public ParticipantMonitor(final UnifiedParticipantIdentity participant) {
        this.participantId = participant;
        this.monitorables = new HashSet();
    }

    /**
     * Adds the monitorable.
     * 
     * @param monitorable
     *        the monitorable
     */
    public void addMonitorable(final MonitorableWrapper monitorable) {
        this.monitorables.add(monitorable);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.management.monitor.BaseMonitor#destroy()
     */
    @Override
    public void destroy() {
        if (null != this.monitorables) {
            this.monitorables.clear();
            this.monitorables = new HashSet();
        }
        super.destroy();
    }

    /**
     * Gets the participant id.
     * 
     * @return the participant id
     */
    public String getParticipantId() {
        return String.valueOf(this.participantId);
    }

    /**
     * (non-Javadoc).
     * 
     * @return the state
     * 
     * @see org.eclipse.swordfish.core.management.monitor.SbbMonitorMBean#getState()
     */
    public String getState() {
        return this.getStateInternal().toString();
    }

    /**
     * Gets the state internal.
     * 
     * @return the state internal
     */
    public State getStateInternal() {
        State ret = State.RUNNING;
        if (this.monitorables.size() > 0) {
            for (Iterator iter = this.monitorables.iterator(); iter.hasNext();) {
                MonitorableWrapper monitorable = (MonitorableWrapper) iter.next();
                if (ret.toInt() < monitorable.getStateInternal().toInt()) {
                    ret = monitorable.getStateInternal();
                }
            }
        }
        return ret;
    }

    /**
     * Removes the monitorable.
     * 
     * @param monitorable
     *        the monitorable
     */
    public void removeMonitorable(final MonitorableWrapper monitorable) {
        this.monitorables.remove(monitorable);
    }

}
