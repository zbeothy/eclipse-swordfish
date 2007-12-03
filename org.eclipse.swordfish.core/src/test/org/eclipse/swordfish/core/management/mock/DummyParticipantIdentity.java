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
package org.eclipse.swordfish.core.management.mock;

import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;

/**
 * Minimal implementation to run management tests.
 * 
 */
public class DummyParticipantIdentity implements InternalParticipantIdentity {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.ParticipantIdentity#getApplicationID()
     */
    public String getApplicationID() {
        return "fooAppId";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.ParticipantIdentity#getInstanceID()
     */
    public String getInstanceID() {
        return "barInstId";
    }

}
