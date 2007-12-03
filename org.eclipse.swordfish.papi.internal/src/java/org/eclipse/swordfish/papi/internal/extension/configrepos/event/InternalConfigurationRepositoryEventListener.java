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
package org.eclipse.swordfish.papi.internal.extension.configrepos.event;

/**
 * Any participant which wants to get notified of configuration repository events shall implement
 * this interface. The InternalConfigurationRepositoryManagerImpl will callback the
 * <code>notify(...)</code> method, in order to propagate the received events. TODO What about
 * exception handling and signaling by the participant application
 * 
 */
public interface InternalConfigurationRepositoryEventListener {

    /**
     * Shall be implemented by a event listener to receive notifications.
     * 
     * @param aEvent
     *        which shall be processed
     */
    void notify(final InternalConfigurationRepositoryEvent aEvent); // throws
    // ConfigurationRepositoryException;
    // ???
}
