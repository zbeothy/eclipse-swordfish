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
package org.eclipse.swordfish.core.components.processing;

/**
 * The Class ContentAction.
 */
public class ContentAction {

    /** The Constant READ. */
    public static final ContentAction READ = new ContentAction(ContentAction.READ_STR);

    /** The Constant WRITE. */
    public static final ContentAction WRITE = new ContentAction(ContentAction.WRITE_STR);

    /** The Constant READWRITE. */
    public static final ContentAction READWRITE = new ContentAction(ContentAction.READ_WRITE_STR);

    /** The Constant NONE. */
    public static final ContentAction NONE = new ContentAction(ContentAction.NONE_STR);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7984176992891010054L;

    /** string constants. */
    private static final String READ_STR = "READ";

    /** The Constant WRITE_STR. */
    private static final String WRITE_STR = "WRITE";

    /** The Constant READ_WRITE_STR. */
    private static final String READ_WRITE_STR = "READ-WRITE";

    /** The Constant NONE_STR. */
    private static final String NONE_STR = "NONE";

    /**
     * from String.
     * 
     * @param value
     *        the value
     * 
     * @return enum
     */
    public static ContentAction fromString(final String value) {
        if (READ_STR.equals(value)) return READ;
        if (WRITE_STR.equals(value)) return WRITE;
        if (READ_WRITE_STR.equals(value)) return READWRITE;
        if (NONE_STR.equals(value)) return NONE;
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
    private ContentAction(final String value) {
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
        if (obj instanceof ContentAction) {
            if (this.value.equals(((ContentAction) obj).value)) return true;
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
