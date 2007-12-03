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
package org.eclipse.swordfish.core.components.addressing;

import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;

/**
 * The Class WSAEndpointReference.
 */
public class WSAEndpointReference {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: WSAEndpointReference.java,v 1.1.2.3 2007/11/09 17:47:07 kkiehne Exp $";

    // ----------------------------------------------------------------- Static

    /** The counter. */
    private static int counter = 0;

    // ----------------------------------------------------- Instance Variables

    /** The name. */
    private String name;

    /** The address. */
    private String address;

    /** The participant. */
    private InternalParticipantIdentity participant;

    /** The id. */
    private int id;

    // ----------------------------------------------------------- Constructors

    /**
     * Instantiates a new WSA endpoint reference.
     * 
     * @param name
     *        the name
     * @param address
     *        the address
     * @param participant
     *        the participant
     */
    public WSAEndpointReference(final String name, final String address, final InternalParticipantIdentity participant) {
        this.name = name;
        this.address = address;
        this.id = counter++;
        this.participant = participant;
    }

    // ------------------------------------------------------------- Properties

    /**
     * Gets the address.
     * 
     * @return the address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the participant.
     * 
     * @return the participant
     */
    public InternalParticipantIdentity getParticipant() {
        return this.participant;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * To string.
     * 
     * @return the string
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(this.getClass().getName());
        buf.append('[');
        buf.append(this.id);
        buf.append("]: ");
        buf.append("name: ");
        buf.append(this.name);
        buf.append(", address: ");
        buf.append(this.address);
        if (null != this.participant) {
            buf.append(", participant: ");
            buf.append(this.participant.getApplicationID());
            buf.append("/");
            buf.append(this.participant.getInstanceID());
        }
        return buf.toString();
    }

}
