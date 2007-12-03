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
package org.eclipse.swordfish.core.management.statistics.jsr77;

import java.util.HashMap;

/**
 * Enumeration object, the represents the possible states of the InternalSBB Library.
 * 
 */
public final class State {

    /** The number code of the corresponding state. */
    public static final int IDX_STARTING = 0;

    // -------------------------------------------------------------- Constants

    /** The number code of the corresponding state. */
    public static final int IDX_RUNNING = 1;

    /** The number code of the corresponding state. */
    public static final int IDX_STOPPING = 2;

    /** The number code of the corresponding state. */
    public static final int IDX_STOPPED = 3;

    /** The number code of the corresponding state. */
    public static final int IDX_FAILED = 4;

    /** The number code of the corresponding state. */
    public static final String NAME_STARTING = "STARTING";

    /** The number code of the corresponding state. */
    public static final String NAME_RUNNING = "RUNNING";

    /** The number code of the corresponding state. */
    public static final String NAME_STOPPING = "STOPPING";

    /** The number code of the corresponding state. */
    public static final String NAME_STOPPED = "STOPPED";

    /** The number code of the corresponding state. */
    public static final String NAME_FAILED = "FAILED";

    /** The one and only instance of the corresponding state. */
    public static final State STARTING = new State(NAME_STARTING, IDX_STARTING);

    /** The one and only instance of the corresponding state. */
    public static final State RUNNING = new State(NAME_RUNNING, IDX_RUNNING);

    /** The one and only instance of the corresponding state. */
    public static final State STOPPING = new State(NAME_STOPPING, IDX_STOPPING);

    /** The one and only instance of the corresponding state. */
    public static final State STOPPED = new State(NAME_STOPPED, IDX_STOPPED);

    /** The one and only instance of the corresponding state. */
    public static final State FAILED = new State(NAME_FAILED, IDX_FAILED);

    /** The known states. */
    private static HashMap knownStates = new HashMap();

    /**
     * Gets the by name.
     * 
     * @param name
     *        the name
     * 
     * @return the by name
     */
    public static State getByName(final String name) {
        return (State) knownStates.get(name);
    }

    /** readable name of state. */
    private String name;

    /** nummerical index of state as defined by JSR77. */
    private int index;

    /** The hash code. */
    private int hashCode;

    /**
     * Instantiates a new state.
     * 
     * @param name
     *        the name
     * @param index
     *        the index
     */
    private State(final String name, final int index) {
        this.index = index;
        this.name = name;
        knownStates.put(name, this);
    }

    /**
     * Equals.
     * 
     * @param other
     *        the other
     * 
     * @return true, if equals
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof State)) return false;
        State castOther = (State) other;

        return ((this.index == castOther.index) && (((null != this.name) && this.name.equals(castOther.name)) || ((null == this.name) && (null == castOther.name))));

    }

    /**
     * Hash code.
     * 
     * @return the int
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int nHashCode = 17;
            nHashCode = nHashCode * 37 + this.index;
            nHashCode = nHashCode * 37 + this.name.hashCode();
        }
        return this.hashCode;
    }

    /**
     * To int.
     * 
     * @return the int
     */
    public int toInt() {
        return this.index;
    }

    /**
     * To string.
     * 
     * @return the string
     */
    @Override
    public String toString() {
        return this.name;
    }

}
