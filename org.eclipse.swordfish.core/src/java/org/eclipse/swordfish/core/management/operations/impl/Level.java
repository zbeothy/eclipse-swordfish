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

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.operations.Severity;

/**
 * Extends java.util.logging.Level to include levels as defined by
 * org.eclipse.swordfish.core.management.operations.Severity.
 * 
 */
public class Level extends java.util.logging.Level {

    /** <code>serialVersionUID</code> for Serializable. */
    private static final long serialVersionUID = 4379985479756458439L;

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(Level.class);

    /** map of all known levels. levels are registered by name and by value */
    private static final Map KNOWN_LEVELS = new HashMap();

    static {
        KNOWN_LEVELS.put("ALL", java.util.logging.Level.ALL);
        KNOWN_LEVELS.put("SEVERE", java.util.logging.Level.SEVERE);
        KNOWN_LEVELS.put("WARNING", java.util.logging.Level.WARNING);
        KNOWN_LEVELS.put("INFO", java.util.logging.Level.INFO);
        KNOWN_LEVELS.put("FINE", java.util.logging.Level.FINE);
        KNOWN_LEVELS.put("FINER", java.util.logging.Level.FINER);
        KNOWN_LEVELS.put("FINEST", java.util.logging.Level.FINEST);
        KNOWN_LEVELS.put("OFF", java.util.logging.Level.OFF);
    }

    /**
     * Gets the level.
     * 
     * @param value
     *        the value
     * 
     * @return the level
     */
    public static Level getLevel(final int value) {
        Integer key = new Integer(value);
        Level ret = (Level) KNOWN_LEVELS.get(key);
        return ret;
    }

    /**
     * get the Level instance corresponding to a
     * org.eclipse.swordfish.core.management.operations.Severity instance.
     * 
     * @param severity
     *        the severity
     * 
     * @return the level
     */
    public static java.util.logging.Level getLevel(final Severity severity) {
        java.util.logging.Level ret = getLevel(severity.getName());
        if (null == ret) {
            int max = Integer.MIN_VALUE;
            for (Iterator iter = KNOWN_LEVELS.values().iterator(); iter.hasNext();) {
                java.util.logging.Level level = (java.util.logging.Level) iter.next();
                if ((max < level.intValue()) && (severity.getValue() > level.intValue())) {
                    ret = level;
                    max = level.intValue();
                }
            }
            if (null != ret) {
                KNOWN_LEVELS.put(severity.getName(), ret);
                if (LOG.isInfoEnabled()) {
                    LOG.info("Mapped InternalSeverity " + severity.getName() + " to Level " + ret.getName());
                }
            } else {
                ret = java.util.logging.Level.ALL;
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Could not map InternalSeverity " + severity.getName() + " to any Level - default to Level.ALL");
                }
            }
        }
        return ret;
    }

    /**
     * Gets the level.
     * 
     * @param name
     *        the name
     * 
     * @return the level
     */
    public static java.util.logging.Level getLevel(final String name) {
        String key = name.toUpperCase();
        Object obj = KNOWN_LEVELS.get(key);
        java.util.logging.Level ret = (java.util.logging.Level) obj;
        return ret;
    }

    /**
     * Instantiates a new level.
     * 
     * @param name
     *        the name
     * @param value
     *        the value
     */
    protected Level(final String name, final int value) {
        super(name.toUpperCase(), value);
        KNOWN_LEVELS.put(name.toUpperCase(), this);
        KNOWN_LEVELS.put(new Integer(value), this);
    }

    /**
     * make sure that the right instance is returned when de-serialized.
     * 
     * @return the object
     * 
     * @throws java.io.ObjectStreamException
     * @throws ObjectStreamException
     */
    private Object readResolve() throws java.io.ObjectStreamException {
        // TODO: verify
        return getLevel(this.intValue());
    }

}
