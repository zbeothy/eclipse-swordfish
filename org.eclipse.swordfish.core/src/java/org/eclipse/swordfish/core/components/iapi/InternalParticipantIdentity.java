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

/**
 * This is a default participantIdentidy used for internal core usage.
 */
public final class InternalParticipantIdentity implements org.eclipse.swordfish.papi.internal.InternalParticipantIdentity {

    /** The Constant identity. */
    public static final InternalParticipantIdentity IDENTITY = new InternalParticipantIdentity();

    /**
     * Instantiates a new internal participant identity.
     */
    private InternalParticipantIdentity() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.InternalParticipantIdentity#getApplicationID()
     */
    public String getApplicationID() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.InternalParticipantIdentity#getInstanceID()
     */
    public String getInstanceID() {
        return null;
    }

}
