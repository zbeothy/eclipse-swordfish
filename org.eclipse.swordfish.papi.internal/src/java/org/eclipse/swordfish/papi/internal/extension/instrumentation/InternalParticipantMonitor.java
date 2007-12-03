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
package org.eclipse.swordfish.papi.internal.extension.instrumentation;

import java.util.List;
import java.util.Properties;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * Monitor that watches an instance of <code>InternalMonitorable</code> and provide the
 * information to SBB's management component. The difference to using
 * <code>InstrumenationManager</code> is that<br> - the information monitored is standardized to
 * the isAlive attribute<br> - the information is provided to the management component even after
 * the <code>InternalMonitorable</code> is unregistered</br>
 * 
 * An instance implementing this interface can be obtained via the environment of an existing SBB by
 * 
 * <pre>
 *    InternalParticipantMonitor myManager =  mySbb.getEnvironment().getComponent(
 *        org.eclipse.swordfish.papi.extension.instrumentation.ParticipantMonitor.class, null)
 * </pre>
 * 
 * Multiple calls to this method will always return the same instance of this interface. It can be
 * used to register multiple <code>InternalMonitorable</code> objects. The value of all
 * <code>InternalParticipantMonitor</code>s isAlive attribute will be considered when determining
 * the overall state of a participant instance. If at least one of them returns <code>false</code>,
 * the overall state of the participant will indicate that it is not able to serve requests. <p/>
 */
public interface InternalParticipantMonitor {

    /**
     * getComponents.
     * 
     * @return a <code>List</code> containing the <code>InternalMonitorable</code> instances
     *         currently registered with this <code>InternalParticipantMonitor</code>.
     */
    List getComponents();

    /**
     * Register a <code>InternalMonitorable</code>.
     * 
     * @param component
     *        <code>InternalMonitorable</code> to be monitored.
     * @throws InternalSBBException
     *         if registration had to be aborted due to an internal error
     */
    void register(InternalMonitorable component) throws InternalSBBException;

    /**
     * register.
     * 
     * @param component
     * @param nameProperties
     * @throws InternalSBBException
     *         if registration had to be aborted due to an internal error
     */
    void register(InternalMonitorable component, Properties nameProperties) throws InternalSBBException;

    /**
     * unregister.
     * 
     * @param component
     * @throws InternalSBBException
     *         if registration had to be aborted due to an internal error
     */
    void unregister(InternalMonitorable component) throws InternalSBBException;

}
