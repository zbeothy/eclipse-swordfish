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
package org.eclipse.swordfish.core.management.operations.impl;

import java.util.List;
import javax.management.ObjectName;
import org.eclipse.swordfish.core.management.statistics.jsr77.State;

/**
 * Backend to publish notification batches to consumers of operational log messages.
 * 
 */
public interface PublisherBackend {

    /**
     * Gets the instrumentation on.
     * 
     * @return the <code>ObjectName</code> for the backend's instrumentation object
     */
    ObjectName getInstrumentationOn();

    /**
     * Gets the state.
     * 
     * @return the current state of the backend
     */
    State getState();

    /**
     * send out notifications to the consumer.
     * 
     * @param notifications
     *        the notifications
     * 
     * @return <code>true</code> if notifications were successfully send <code>false</code>
     *         otherwise
     */
    boolean sendNotifications(List notifications);

}
