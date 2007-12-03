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
package org.eclipse.swordfish.core.management.monitor;

/**
 * The Interface IBaseMonitor.
 * 
 */
public interface IBaseMonitor {

    /**
     * Add the response time for a service invocation.
     * 
     * @param time
     *        in milliseconds
     */
    void addResponseTime(long time);

    /**
     * see operation name.
     */
    void handleAppFailEvent();

    /**
     * see operation name.
     */
    void handleNetFailEvent();

    /**
     * see operation name.
     */
    void handleRequestEvent();

}
