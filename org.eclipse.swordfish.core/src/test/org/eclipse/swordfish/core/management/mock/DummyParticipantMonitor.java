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
package org.eclipse.swordfish.core.management.mock;

import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.monitor.ParticipantMonitor;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;

/**
 * The Class DummyParticipantMonitor.
 */
public class DummyParticipantMonitor extends ParticipantMonitor {

    /** The state. */
    private State state;

    /**
     * Instantiates a new dummy participant monitor.
     * 
     * @param participant
     *        the participant
     */
    public DummyParticipantMonitor(final UnifiedParticipantIdentity participant) {
        super(participant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.monitor.ParticipantMonitor#getState()
     */
    @Override
    public String getState() {
        return this.state.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.management.monitor.ParticipantMonitor#getStateInternal()
     */
    @Override
    public State getStateInternal() {
        return this.state;
    }

    /**
     * Sets the state.
     * 
     * @param newState
     *        the new state
     */
    public void setState(final State newState) {
        this.state = newState;
    }

}
