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
package org.eclipse.swordfish.papi.internal.extension.operations;

import java.util.HashMap;
import java.util.Map;

/**
 * A InternalSeverity indicates the importance of an OperationsMessage. There are exactly four
 * different pre defined severitys. Each severity has a value and a name. The name may be used to
 * show a severity to a user , while the value indicates importance with higher values for higher
 * importance. The actual values are chosen to allow integration of instances of this class into the
 * java.util.logging.Level hierarchy.
 * 
 */
public final class InternalSeverity {

    /**
     * InternalSeverity for fatal problems. Used to indicate problems which stop any further normal
     * processing. value is '1100' , name is 'FATAL'
     */
    public static final InternalSeverity FATAL = new InternalSeverity(1100, "FATAL");

    /**
     * InternalSeverity for errors. Used to indicate problems which caused cancelationof one
     * specific task, while further processing of other task might be still possible. value is
     * '1050' , name is 'ERROR'
     */
    public static final InternalSeverity ERROR = new InternalSeverity(1050, "ERROR");

    /**
     * InternalSeverity for warnings. Used to indicate unexpected state which doesn't hinder correct
     * functionality but might reduce non functional behaviour. E.g. a missing configuration
     * parameter which is replaced by a default or an fallback to a secondary external ressource
     * would trigger messages of tjis severity. value is '901' , name is 'WARN'
     */
    public static final InternalSeverity WARN = new InternalSeverity(901, "WARN");

    /**
     * InternalSeverity for informations. Used to inform about regular behaviour. E.g. state changes
     * or used configurations might be notified under this severity. value is '801' , name is 'INFO'
     */
    public static final InternalSeverity INFO = new InternalSeverity(801, "INFO");

    /**
     * map of all known levels. levels are registered by name and by value
     */
    private static final Map<Object, InternalSeverity> KNOWN_LEVELS = new HashMap<Object, InternalSeverity>();

    // public static final InternalSeverity LEVEL_OFF = new InternalSeverity(0,
    // "OFF");

    // public static final InternalSeverity LEVEL_ALL = new InternalSeverity(50,
    // "ALL");

    /**
     * returns the named InternalSeverity or null if no InternalSeverity with the given name is
     * defined.
     * 
     * @param aName
     *        name of requested InternalSeverity
     * @return the named InternalSeverity
     */
    public static InternalSeverity getByName(final String aName) {
        return KNOWN_LEVELS.get(aName);
    }

    /**
     * returns the InternalSeverity with the given value or null if no InternalSeverity with the
     * given value is defined.
     * 
     * @param aValue
     *        value of requested InternalSeverity
     * @return the InternalSeverity with given value
     */
    public static InternalSeverity getByValue(final int aValue) {
        return KNOWN_LEVELS.get(new Integer(aValue));
    }

    /**
     * the value
     */
    private int value;

    /**
     * the name
     */
    private String name;

    /**
     * private constructor. instances should never be created beside the known instances available
     * as constants.
     * 
     * @param aValue
     *        the value
     * @param aName
     *        the name
     */
    private InternalSeverity(final int aValue, final String aName) {
        this.value = aValue;
        this.name = aName;
        KNOWN_LEVELS.put(this.name, this);
        KNOWN_LEVELS.put(new Integer(aValue), this);
    }

    /**
     * get the name of this InternalSeverity.
     * 
     * @return the name of this InternalSeverity
     */
    public String getName() {
        return this.name;
    }

    /**
     * get the value of this InternalSeverity.
     * 
     * @return the value of this InternalSeverity
     */
    public int getValue() {
        return this.value;
    }
}
