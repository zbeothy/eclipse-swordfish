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
package org.eclipse.swordfish.core.management.instrumentation;

import java.util.List;
import java.util.Properties;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalMonitorable;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalParticipantMonitor;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalState;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalUnknownComponentException;

/**
 * The Class TestMonitorable.
 */
public class TestMonitorable implements InternalMonitorable {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.Monitorable#getChildren()
     */
    public List getChildren() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.Monitorable#getState()
     */
    public InternalState getState() {
        return InternalState.RUNNING;
    }

    /**
     * Register.
     * 
     * @param arg0
     *        the arg0
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     */
    public void register(final InternalParticipantMonitor arg0) throws InternalInfrastructureException, AlreadyRegisteredException {

    }

    /**
     * Register.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     */
    public void register(final InternalParticipantMonitor arg0, final Properties arg1) throws InternalInfrastructureException,
            AlreadyRegisteredException {

    }

    /**
     * Unregister.
     * 
     * @param arg0
     *        the arg0
     * 
     * @throws ParticipantHandlingException
     * @throws InternalUnknownComponentException
     */
    public void unregister(final InternalParticipantMonitor arg0) throws InternalInfrastructureException,
            InternalUnknownComponentException {

    }

}
