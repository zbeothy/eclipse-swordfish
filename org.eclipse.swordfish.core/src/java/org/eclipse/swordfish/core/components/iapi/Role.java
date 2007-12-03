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
 * The Class Role.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public final class Role implements Serializable {

    /** initiator constant. */
    public static final Role SENDER = new Role(Role.SENDER_STR);

    /** servicer constant. */
    public static final Role RECEIVER = new Role(Role.RECEIVER_STR);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4519638690465319925L;

    /** initiator string constant. */
    private static final String SENDER_STR = "sender";

    /** servicer string constant. */
    private static final String RECEIVER_STR = "receiver";

    /**
     * from String.
     * 
     * @param value
     *        the value
     * 
     * @return enum
     */
    public static Role fromString(final String value) {
        if (SENDER_STR.equals(value)) return SENDER;
        if (RECEIVER_STR.equals(value)) return RECEIVER;
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
    private Role(final String value) {
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
        if (obj instanceof Role) {
            if (this.value.equals(((Role) obj).value)) return true;
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
