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
 * The Class Transport.
 */
public final class Transport implements Serializable {

    /** HTTP constant. */
    public static final Transport HTTP = new Transport(Transport.HTTP_STR);

    /** HTTPS constant. */
    public static final Transport HTTPS = new Transport(Transport.HTTPS_STR);

    /** JMS constant. */
    public static final Transport JMS = new Transport(Transport.JMS_STR);

    /** JBI local constant. */
    public static final Transport JBI = new Transport(Transport.JBI_STR);

    /** unknown constant. */
    public static final Transport UNKNOWN = new Transport(Transport.UNKNOWN_STR);

    /** HTTP string constant. */
    private static final String HTTP_STR = "HttpTransport";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9058525248509994123L;

    /** HTTPS string constant. */
    private static final String HTTPS_STR = "HttpsTransport";

    /** JMS string constant. */
    private static final String JMS_STR = "JmsTransport";

    /** JBI string constant. */
    private static final String JBI_STR = "JbiTransport";

    /** unknown transport for instance by using locator. */
    private static final String UNKNOWN_STR = "UNKNOWN";

    /**
     * from String.
     * 
     * @param value
     *        the value
     * 
     * @return enum
     */
    public static Transport fromString(final String value) {
        if (HTTP_STR.equals(value)) return HTTP;
        if (HTTPS_STR.equals(value)) return HTTPS;
        if (JMS_STR.equals(value)) return JMS;
        if (JBI_STR.equals(value)) return JBI;
        if (UNKNOWN_STR.equals(value)) return UNKNOWN;

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
    private Transport(final String value) {
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
        if (obj instanceof Transport) {
            if (this.value.equals(((Transport) obj).value)) return true;
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
