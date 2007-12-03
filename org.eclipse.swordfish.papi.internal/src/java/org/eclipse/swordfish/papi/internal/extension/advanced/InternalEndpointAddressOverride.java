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
package org.eclipse.swordfish.papi.internal.extension.advanced;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalServiceProxy;

/**
 * The Interface InternalEndpointAddressOverride.
 */
public interface InternalEndpointAddressOverride {

    /**
     * Override transport.
     * 
     * @param props
     *        the props
     * @param proxy
     *        the proxy
     * @param transport
     *        the transport
     * 
     * @throws internalSBBException
     *         the SBB exception
     * @throws InternalSBBException
     *         the internal sbb exception
     */
    void overrideTransport(InternalServiceProxy proxy, TransportType transport, Properties props) throws InternalSBBException;

    /**
     * Reset transport.
     * 
     * @param proxy
     *        the proxy
     * @param transport
     *        the transport
     * 
     * @throws internalSBBException
     *         the SBB exception
     * @throws InternalSBBException
     *         the internal sbb exception
     */
    void resetTransport(InternalServiceProxy proxy, TransportType transport) throws InternalSBBException;

    /**
     * The Class TransportType.
     */
    public static class TransportType {

        /** The Constant HTTP. */
        public static final TransportType HTTP = new TransportType("HTTP");

        /** The Constant HTTPS. */
        public static final TransportType HTTPS = new TransportType("HTTPS");

        /** The Constant JMS. */
        public static final TransportType JMS = new TransportType("JMS");

        /** The INTERN. */
        private static final Map<String, TransportType> CONTENT = new HashMap<String, TransportType>();

        /**
         * Gets the.
         * 
         * @param name
         *        the name
         * 
         * @return the transport type
         */
        public static TransportType get(final String name) {
            return CONTENT.get(name);
        }

        /** The name. */
        private final String name;

        /**
         * The Constructor.
         * 
         * @param aName
         *        the a name
         */
        private TransportType(final String aName) {
            this.name = aName;
            CONTENT.put(aName, this);
        }

        /**
         * Gets the name.
         * 
         * @return the name
         */
        public String getName() {
            return name;
        }

    }
}
