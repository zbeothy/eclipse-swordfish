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
package org.eclipse.swordfish.core.components.srproxy.impl;

import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;

/**
 * The Class ParticipantPolicyIdentityStub.
 */
public class ParticipantPolicyIdentityStub implements ParticipantPolicyIdentity {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyIdentity#getKeyName()
     */
    public String getKeyName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyIdentity#getLocation()
     */
    public String getLocation() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.policytrader.PolicyIdentity#setLocation(java.lang.String)
     */
    public void setLocation(final String s) {

    }

}
