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
package org.eclipse.swordfish.core.utils;

import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;

/**
 * The Class DumpeableParticipantIdentity.
 */
public class DumpeableParticipantIdentity implements InternalParticipantIdentity {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: DumpeableParticipantIdentity.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    // ----------------------------------------------------- Instance Variables

    /**
     * Decorate.
     * 
     * @param pid
     *        the pid
     * @param dumper
     *        the dumper
     * 
     * @return the internal participant identity
     */
    public static DumpeableParticipantIdentity decorate(final InternalParticipantIdentity pid, final Dumper dumper) {
        return new DumpeableParticipantIdentity(pid, dumper);
    }

    /** The dumper. */
    protected final Dumper dumper;

    // ----------------------------------------------------------- Constructors

    /** The pid. */
    protected final InternalParticipantIdentity pid;

    // ------------------------------------------------------------- Properties

    /**
     * Instantiates a new dumpeable participant identity.
     * 
     * @param pid
     *        the pid
     * @param dumper
     *        the dumper
     */
    protected DumpeableParticipantIdentity(final InternalParticipantIdentity pid, final Dumper dumper) {
        super();
        if (null == pid) throw new IllegalArgumentException("ParticipantIdentity must not be null!");
        if (null == dumper) throw new IllegalArgumentException("Dumper must not be null!");
        this.pid = pid;
        this.dumper = dumper;
    }

    /**
     * Dump.
     * 
     * @return the string
     */
    public String dump() {
        return this.dumper.dump(this.pid);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    public String getApplicationID() {
        return this.pid.getApplicationID();
    }

    /**
     * {@inheritDoc}
     */
    public String getInstanceID() {
        return this.pid.getInstanceID();
    }

    // ---------------------------------------------------------- Inner Classes
}
