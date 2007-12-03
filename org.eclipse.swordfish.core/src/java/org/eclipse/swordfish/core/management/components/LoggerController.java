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
package org.eclipse.swordfish.core.management.components;

import java.util.Map;

/**
 * Public accessible interface for the component that enables management access to the logging
 * system.
 * 
 */
public interface LoggerController {

    /**
     * Adds a new Logger or changes its level if already existing.
     * 
     * @param loggerName
     *        name of logger to create/set
     * @param levelName
     *        new level for logger (or "null" to set <code>null</code> level)
     * 
     * @return message indicating the result of the operation
     */
    String addLogger(String loggerName, String levelName);

    /**
     * Gets the filter null loggers.
     * 
     * @return whether loggers that have a null Level attribute should be filtered from
     *         <code>getLoggers</code>
     */
    Boolean getFilterNullLoggers();

    /**
     * Gets the loggers.
     * 
     * @return Map containing the names of registered loggers as key and the logger's levels as
     *         values
     */
    Map getLoggers();

    /**
     * decides whether loggers that have a null Level attribute should be filtered from
     * <code>getLoggers</code>.
     * 
     * @param val
     *        <code>true</code> filter <br/> <code>false</code> don't filter
     */
    void setFilterNullLoggers(Boolean val);

    /**
     * Set the level of a particular logger.
     * 
     * @param loggerName
     *        name of logger to set
     * @param levelName
     *        new level for logger (or "null" to set <code>null</code> level)
     * 
     * @return message indicating the result of the operation
     */
    String setLoggerLevel(String loggerName, String levelName);

}
