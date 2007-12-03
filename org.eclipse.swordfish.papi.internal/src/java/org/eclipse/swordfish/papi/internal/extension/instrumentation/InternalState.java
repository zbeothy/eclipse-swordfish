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
package org.eclipse.swordfish.papi.internal.extension.instrumentation;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration describing the possible states for <code>InternalMonitorable</code> components.
 * 
 */
public class InternalState {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: InternalState.java,v 1.1.2.3 2007/11/09 17:47:16 kkiehne Exp $";

    /** A <code>int</code> as <code>STARTING_INDEX</code> of this class. */
    public static final int STARTING_INDEX = 0;

    /** A <code>int</code> as <code>RUNNING_INDEX</code> of this class. */
    public static final int RUNNING_INDEX = 1;

    /** A <code>int</code> as <code>STOPPING_INDEX</code> of this class. */
    public static final int STOPPING_INDEX = 2;

    /** A <code>int</code> as <code>STOPPED_INDEX</code> of this class. */
    public static final int STOPPED_INDEX = 3;

    /** A <code>int</code> as <code>FAILED_INDEX</code> of this class. */
    public static final int FAILED_INDEX = 4;

    /** A <code>InternalState</code> as <code>STARTING</code> of this class. */
    public static final InternalState STARTING = new InternalState("STARTING", STARTING_INDEX);

    /** A <code>InternalState</code> as <code>RUNNING</code> of this class. */
    public static final InternalState RUNNING = new InternalState("RUNNING", RUNNING_INDEX);

    /** A <code>InternalState</code> as <code>STOPPING</code> of this class. */
    public static final InternalState STOPPING = new InternalState("STOPPING", STOPPING_INDEX);

    /** A <code>InternalState</code> as <code>STOPPED</code> of this class. */
    public static final InternalState STOPPED = new InternalState("STOPPED", STOPPED_INDEX);

    /** A <code>InternalState</code> as <code>FAILED</code> of this class. */
    public static final InternalState FAILED = new InternalState("FAILED", FAILED_INDEX);

    /** map of all known levels. levels are registered by name and by value */
    private static final Map<Object, InternalState> KNOWN_LEVELS = new HashMap<Object, InternalState>();

    // ----------------------------------------------------- Instance Variables

    /**
     * Gets the by name.
     * 
     * @param aName
     *        the a name
     * 
     * @return the by name
     */
    public static InternalState getByName(final String aName) {
        return KNOWN_LEVELS.get(aName);
    }

    /**
     * Gets the by value.
     * 
     * @param aValue
     *        the a value
     * 
     * @return the by value
     */
    public static InternalState getByValue(final int aValue) {
        return KNOWN_LEVELS.get(new Integer(aValue));
    }

    /** Numeric representation of a state. */
    private final int code;

    // ----------------------------------------------------------- Constructors

    /** Textual representation of a state. */
    private final String text;

    // ------------------------------------------------------------- Properties

    /** Cached hash code. */
    private int cachedHashCode;

    /**
     * Instantiates a new internal state.
     * 
     * @param text
     *        the text
     * @param code
     *        the code
     */
    private InternalState(final String text, final int code) {
        this.text = text;
        this.code = code;
        this.cachedHashCode = 0;
        KNOWN_LEVELS.put(text, this);
        KNOWN_LEVELS.put(new Integer(code), this);
    }

    /**
     * equals.
     * 
     * @param other
     * 
     * @return boolean the boolean.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof InternalState)) return false;
        InternalState castOther = (InternalState) other;
        return ((this.code == castOther.code) && (((null != this.text) && this.text.equals(castOther.text)) || ((null == this.text) && (null == castOther.text))));
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Gets the code.
     * 
     * @return the code
     */
    public int getCode() {
        return this.code;
    }

    /**
     * hashCode().
     * 
     * @return int the int.
     * 
     * @see java.lang.Object#hashCode().
     */
    @Override
    public int hashCode() {
        if (this.cachedHashCode == 0) {
            this.cachedHashCode = 17;
            this.cachedHashCode = this.cachedHashCode * 37 + this.code;
            this.cachedHashCode = this.cachedHashCode * 37 + this.text.hashCode();
        }
        return this.cachedHashCode;
    }

    /**
     * toString.
     * 
     * @return String the String.
     */
    @Override
    public String toString() {
        return this.text;
    }

}
