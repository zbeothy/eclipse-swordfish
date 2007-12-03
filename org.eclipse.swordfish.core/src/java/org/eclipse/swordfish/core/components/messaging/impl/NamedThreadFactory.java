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
package org.eclipse.swordfish.core.components.messaging.impl;

import java.util.concurrent.ThreadFactory;

/**
 * this class is used for java concurrent package to produce named threads.
 */
public class NamedThreadFactory implements ThreadFactory {

    /** thread's name prefix. */
    private String name;

    /** a sequential number for thread naming. */
    private long seq;

    /**
     * constructor for this class.
     * 
     * @param name
     *        configures how the name of the threads are going to be
     */
    public NamedThreadFactory(final String name) {
        this.name = name;
        this.seq = 0;
    }

    /**
     * New thread.
     * 
     * @param runnable
     *        the runnable
     * 
     * @return the thread
     * 
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    public Thread newThread(final Runnable runnable) {
        return new Thread(runnable, this.name + ":" + this.seq++);
    }
}
