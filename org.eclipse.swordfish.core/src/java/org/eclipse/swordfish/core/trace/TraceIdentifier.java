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
package org.eclipse.swordfish.core.trace;

/**
 * The Class TraceIdentifier.
 */
public class TraceIdentifier {

    /** The tls. */
    private static ThreadLocal tls = new ThreadLocal();

    /**
     * Get.
     * 
     * @return the string
     */
    public static String get() {
        return tls.get() == null ? "" : (String) tls.get();
    }

    /**
     * Set.
     * 
     * @param str
     *        the str
     */
    public static void set(final String str) {
        tls.set(str);
    }

    /**
     * Instantiates a new trace identifier.
     */
    private TraceIdentifier() {
    }

}
