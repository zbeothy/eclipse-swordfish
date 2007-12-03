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
package org.eclipse.swordfish.core.logging;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * A factory for creating SBBLog objects.
 */
public final class SBBLogFactory {

    /** The Constant loggers. */
    private static final HashMap LOGGERS = new HashMap();

    /**
     * Gets the log.
     * 
     * @param clazz
     *        class
     * 
     * @return Log logger
     */
    public static Log getLog(final Class clazz) {
        if (LOGGERS.containsKey(clazz.getName())) return (Log) LOGGERS.get(clazz.getName());
        JavaUtilLog log = null;
        Logger logger = Logger.getLogger(clazz.getName());
        ClassLoader currClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(SBBLogFactory.class.getClassLoader());
            log = new JavaUtilLog(logger);
            LOGGERS.put(clazz.getName(), log);
        } finally {
            Thread.currentThread().setContextClassLoader(currClassLoader);
        }
        return log;
    }

    /**
     * Gets the log.
     * 
     * @param clazz
     *        class
     * @param bundleBaseName
     *        bundle name
     * 
     * @return Log logger
     */
    public static Log getLog(final Class clazz, final String bundleBaseName) {
        if (LOGGERS.containsKey(clazz.getName())) return (Log) LOGGERS.get(clazz.getName());
        JavaUtilLog log = null;
        Logger logger = Logger.getLogger(clazz.getName(), bundleBaseName);
        ClassLoader currClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(SBBLogFactory.class.getClassLoader());
            log = new JavaUtilLog(logger);
            LOGGERS.put(clazz.getName(), log);
        } finally {
            Thread.currentThread().setContextClassLoader(currClassLoader);
        }
        return log;
    }

    /**
     * Gets the log.
     * 
     * @param logger
     *        the logger
     * 
     * @return the log
     */
    public static Log getLog(final Logger logger) {
        JavaUtilLog log = null;
        if (LOGGERS.containsKey(logger.getName())) return (Log) LOGGERS.get(logger.getName());
        ClassLoader currClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(SBBLogFactory.class.getClassLoader());
            log = new JavaUtilLog(logger);
            LOGGERS.put(logger.getName(), log);
        } finally {
            Thread.currentThread().setContextClassLoader(currClassLoader);
        }
        return log;
    }

    /**
     * Release all.
     */
    public static void releaseAll() {
        LOGGERS.clear();
    }

    /**
     * private constructor.
     */
    private SBBLogFactory() {
    }
}
