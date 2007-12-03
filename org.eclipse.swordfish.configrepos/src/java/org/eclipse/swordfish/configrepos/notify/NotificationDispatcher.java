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
package org.eclipse.swordfish.configrepos.notify;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryEventListenerInternal;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;

/**
 * The Class NotificationDispatcher.
 * 
 */
public class NotificationDispatcher {

    /**
     * Instantiates a new notification dispatcher.
     * 
     * @param aManager
     *        the a manager
     * @param aFactory
     *        the a factory
     */
    // private ConfigurationRepositoryManagerInternal manager = null;
    /**
     * 
     */
    // private ExecutorService executor = null;
    /**
     * 
     */
    // private List internalEventListeners = null;
    /**
     * 
     */
    // private List externalEventListeners = null;
    /**
     * The Constructor.
     * 
     * @param aManager
     *        which this notification dispatcher is assigned to
     */
    public NotificationDispatcher(final ConfigurationRepositoryManagerInternal aManager) {
        this(aManager, Executors.defaultThreadFactory());
    }

    /**
     * NotificationDispatcher.
     * 
     * @param aManager
     *        which this notification dispatcher is assigned to
     * @param aFactory
     *        is the thread factory which should be used to
     */
    public NotificationDispatcher(final ConfigurationRepositoryManagerInternal aManager, final ThreadFactory aFactory) {
        // executor = Executors.newCachedThreadPool(aFactory);
    }

    /**
     * Adds the event listner.
     * 
     * @param aListener
     *        is the external listener which should be included during notification
     */
    public void addEventListner(final ConfigurationRepositoryEventListenerInternal aListener) {

    }

    /**
     * Sets the internal event listeners.
     * 
     * @param aListenerList
     *        list of listeners
     */
    public void setInternalEventListeners(final List aListenerList) {
        Iterator iter = aListenerList.iterator();
        while (iter.hasNext()) {
            if (!(iter.next() instanceof InternalEventNotifier))
                throw new IllegalArgumentException("invalid internal notifier assignment");
        }
        // internalEventListeners = aListenerList;
    }
}
