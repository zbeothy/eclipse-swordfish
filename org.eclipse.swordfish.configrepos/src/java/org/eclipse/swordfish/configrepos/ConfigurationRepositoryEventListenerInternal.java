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
package org.eclipse.swordfish.configrepos;

/**
 * Interface to be implemented by configuration manager event listeners.
 * 
 */
public interface ConfigurationRepositoryEventListenerInternal {

    /**
     * Shall be implemented by a event listener to receive notifications.
     * 
     * @param aEvent
     *        which shall be processed
     */
    void notify(final ConfigurationRepositoryEventInternal aEvent);
}
