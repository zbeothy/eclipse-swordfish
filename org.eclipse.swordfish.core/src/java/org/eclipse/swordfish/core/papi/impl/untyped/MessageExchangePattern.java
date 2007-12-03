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
package org.eclipse.swordfish.core.papi.impl.untyped;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * WSDL 2.0 pattern of interaction.
 */
public final class MessageExchangePattern {

    /**
     * Comment for <code>IN_ONLY_URI</code>.
     */
    public static final URI IN_ONLY_URI;

    /**
     * Comment for <code>IN_OUT_URI</code>.
     */
    public static final URI IN_OUT_URI;

    /**
     * Comment for <code>ROBUST_IN_ONLY_URI</code>.
     */
    public static final URI ROBUST_IN_ONLY_URI;

    /**
     * Comment for <code>IN_OPTIONAL_OUT_URI</code>.
     */
    public static final URI IN_OPTIONAL_OUT_URI;

    /**
     * Comment for <code>OUT_ONLY_URI</code>.
     */
    public static final URI OUT_ONLY_URI;

    /**
     * Comment for <code>OUT_IN_URI</code>.
     */
    public static final URI OUT_IN_URI;

    /**
     * Comment for <code>OUT_OPTIONAL_IN_URI</code>.
     */
    public static final URI OUT_OPTIONAL_IN_URI;

    /**
     * Comment for <code>ROBUST_OUT_ONLY_URI</code>.
     */
    public static final URI ROBUST_OUT_ONLY_URI;

    static {
        try {
            IN_ONLY_URI = new URI("http://www.w3.org/2004/08/wsdl/in-only");
            IN_OUT_URI = new URI("http://www.w3.org/2004/08/wsdl/in-out");
            ROBUST_IN_ONLY_URI = new URI("http://www.w3.org/2004/08/wsdl/robust-in-only");
            IN_OPTIONAL_OUT_URI = new URI("http://www.w3.org/2004/08/wsdl/in-opt-out");
            OUT_ONLY_URI = new URI("http://www.w3.org/2004/08/wsdl/out-only");
            OUT_IN_URI = new URI("http://www.w3.org/2004/08/wsdl/out-in");
            OUT_OPTIONAL_IN_URI = new URI("http://www.w3.org/2004/08/wsdl/out-opt-in");
            ROBUST_OUT_ONLY_URI = new URI("http://www.w3.org/2004/08/wsdl/robust-out-only");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageExchangePattern() {

    }
}
