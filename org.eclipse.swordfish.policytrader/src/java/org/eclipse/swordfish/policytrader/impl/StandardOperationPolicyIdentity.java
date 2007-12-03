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

import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;

/**
 * Standard implementation of {@link OperationPolicyIdentity}.
 */
public class StandardOperationPolicyIdentity implements OperationPolicyIdentity {

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
    public StandardOperationPolicyIdentity(final OperationPolicyIdentity id) {
        super();
        this.keyName = id.getKeyName();
        this.location = id.getLocation();
    }

    /**
     * Constructor used by {@link PolicyFactory}.
     * 
     * @param keyName
     *        key name (usually the policy reference URI)
     */
    public StandardOperationPolicyIdentity(final String keyName) {
        super();
        this.keyName = keyName;
    }

    /**
     * Constructor used by {@link PolicyFactory}.
     * 
     * @param keyName
     *        key name (usually the policy reference URI)
     * @param location
     *        location
     */
    public StandardOperationPolicyIdentity(final String keyName, final String location) {
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
        if ((null == other) || !(other instanceof StandardOperationPolicyIdentity)) return false;
        if (null == this.keyName) return (null == ((StandardOperationPolicyIdentity) other).getKeyName());
        return this.keyName.equals(((StandardOperationPolicyIdentity) other).getKeyName());
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.OperationPolicyIdentity#getKeyName()
     */
    public String getKeyName() {
        return this.keyName;
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.OperationPolicyIdentity#getLocation()
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
     * @see org.eclipse.swordfish.policytrader.OperationPolicyIdentity#setLocation(java.lang.String)
     */
    public void setLocation(final String location) {
        this.location = location;
    }
}
