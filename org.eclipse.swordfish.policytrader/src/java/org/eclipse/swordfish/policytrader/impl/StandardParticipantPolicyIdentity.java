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
package org.eclipse.swordfish.policytrader.impl;

import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;

/**
 * Standard implementation of {@link ParticipantPolicyIdentity}.
 */
public class StandardParticipantPolicyIdentity implements ParticipantPolicyIdentity {

    /** Key attribute of the identity. */
    private final String keyName;

    /** Optional attribute for use by clients. */
    private String location = null;

    /**
     * Copy constructor.
     * 
     * @param id
     *        source identity
     */
    public StandardParticipantPolicyIdentity(final ParticipantPolicyIdentity id) {
        super();
        this.keyName = id.getKeyName();
        this.location = id.getLocation();
    }

    /**
     * Constructor used by {@link PolicyFactory}.
     * 
     * @param keyName
     *        key name
     */
    public StandardParticipantPolicyIdentity(final String keyName) {
        super();
        this.keyName = keyName;
    }

    /**
     * Constructor used by {@link PolicyFactory}.
     * 
     * @param keyName
     *        key name
     * @param location
     *        location
     */
    public StandardParticipantPolicyIdentity(final String keyName, final String location) {
        super();
        this.keyName = keyName;
        this.location = location;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if ((null == other) || !(other instanceof StandardParticipantPolicyIdentity)) return false;
        if (null == this.keyName) return (null == ((StandardParticipantPolicyIdentity) other).getKeyName());
        return this.keyName.equals(((StandardParticipantPolicyIdentity) other).getKeyName());
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity#getKeyName()
     */
    public String getKeyName() {
        return this.keyName;
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity#getLocation()
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (null == this.keyName) ? 0 : this.keyName.hashCode();
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity#setLocation(java.lang.String)
     */
    public void setLocation(final String location) {
        this.location = location;
    }
}
