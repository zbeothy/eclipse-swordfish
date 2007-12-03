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
package org.eclipse.swordfish.core.components.iapi;

import java.io.Serializable;

/**
 * The Class Scope.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public final class Scope implements Serializable {

    /** initiator constant. */
    public static final Scope REQUEST = new Scope(Scope.REQUEST_STR);

    /** servicer constant. */
    public static final Scope RESPONSE = new Scope(Scope.RESPONSE_STR);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9058525278509994123L;

    /** initiator string constant. */
    private static final String REQUEST_STR = "request";

    /** servicer string constant. */
    private static final String RESPONSE_STR = "response";

    /**
     * from String.
     * 
     * @param value
     *        the value
     * 
     * @return enum
     */
    public static Scope fromString(final String value) {
        if (REQUEST_STR.equals(value)) return REQUEST;
        if (RESPONSE_STR.equals(value)) return RESPONSE;
        return null;
    }

    /** value. */
    private String value;

    /**
     * private constructor.
     * 
     * @param value
     *        the value
     */
    private Scope(final String value) {
        this.value = value;
    }

    /**
     * (non-Javadoc).
     * 
     * @param obj
     *        the obj
     * 
     * @return true, if equals
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Scope) {
            if (this.value.equals(((Scope) obj).value)) return true;
        }
        return false;
    }

    /**
     * To string.
     * 
     * @return the string
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.value;
    }
}
