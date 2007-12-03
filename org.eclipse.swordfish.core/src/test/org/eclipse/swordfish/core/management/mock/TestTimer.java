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

import java.util.Timer;
import java.util.logging.Logger;

/**
 * The Class TestTimer.
 */
public class TestTimer extends Timer {

    /** The log. */
    private static Logger log = Logger.getLogger(TestTimer.class.getName());

    /** The count. */
    private static int count = 0;

    /**
     * Instantiates a new test timer.
     */
    public TestTimer() {
        super();
        log.info("Timer " + this + " created, count: " + ++count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Timer#cancel()
     */
    @Override
    public void cancel() {
        log.info("Timer " + this + " canceled, count: " + --count);
    }

}
