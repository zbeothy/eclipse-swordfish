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
package org.eclipse.swordfish.core.management.components.impl;

import java.util.Timer;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * Convenience implementation to make spring calls to base implementation loggable.
 * 
 */
public class NotificationTimer extends Timer {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(NotificationTimer.class);

    /** The count. */
    private static int count = 0;

    /**
     * Instantiates a new notification timer.
     */
    public NotificationTimer() {
        super(true);
        LOG.info("Timer " + this + " created, count: " + ++count);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Timer#cancel()
     */
    @Override
    public void cancel() {
        super.cancel();
        LOG.info("Timer " + this + " canceled, count: " + --count);
    }

}
