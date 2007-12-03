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

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;

/**
 * The Class UnifiedParticipantIdentity.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class UnifiedParticipantIdentity {

    /** The Constant SINGLE_INSTANCE. */
    static final String SINGLE_INSTANCE = "SINGLE_INSTANCE";

    /** The Constant MTH_GET_PARTICIPANT_IDENTITY. */
    private static final String MTH_GET_PARTICIPANT_IDENTITY = "getParticipantIdentity";

    /** The ident. */
    private InternalParticipantIdentity ident;

    /**
     * Instantiates a new unified participant identity.
     * 
     * @param identity
     *        the identity
     */
    public UnifiedParticipantIdentity(final InternalParticipantIdentity identity) {
        this.ident = identity;
    }

    /**
     * Equals.
     * 
     * @param o
     *        the o
     * 
     * @return true, if equals
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o != null) {
            if (o.getClass().getName().equals(this.getClass().getName())) {
                if (this.ident instanceof org.eclipse.swordfish.core.components.iapi.InternalParticipantIdentity) return true;
                try {
                    Method getParticipantIdentityMethod = o.getClass().getMethod(MTH_GET_PARTICIPANT_IDENTITY, (Class[]) null);
                    Object result = getParticipantIdentityMethod.invoke(o, (Object[]) null);
                    if (result != null) {
                        InternalParticipantIdentity objectIdentity = (InternalParticipantIdentity) result;
                        String applicationId = objectIdentity.getApplicationID();
                        String instanceID = objectIdentity.getInstanceID();
                        if ((instanceID != null) && (this.ident.getInstanceID() != null))
                            return this.ident.getApplicationID().equals(applicationId)
                                    && this.ident.getInstanceID().equals(instanceID);
                        else
                            return this.ident.getApplicationID().equals(applicationId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;

    }

    /**
     * Gets the participant identity.
     * 
     * @return -- the original participant id this unifier is unifying
     */
    public InternalParticipantIdentity getParticipantIdentity() {
        return this.ident;
    }

    /**
     * Gets the reproducible hash.
     * 
     * @return the reproducible hash
     */
    public String getReproducibleHash() {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (this.ident.getApplicationID() != null) {
            md5.update(this.ident.getApplicationID().getBytes());
        }
        if (this.ident.getInstanceID() != null) {
            md5.update(this.ident.getInstanceID().getBytes());
        }
        return new String(Hex.encodeHex(md5.digest()));
    }

    /**
     * Hash code.
     * 
     * @return the int
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (this.ident instanceof org.eclipse.swordfish.core.components.iapi.InternalParticipantIdentity)
            return org.eclipse.swordfish.core.components.iapi.InternalParticipantIdentity.IDENTITY.hashCode();
        if (this.ident.getInstanceID() != null)
            return 5 * this.ident.getApplicationID().hashCode() + 3 * this.ident.getInstanceID().hashCode();
        else
            return 5 * this.ident.getApplicationID().hashCode() + 3 * SINGLE_INSTANCE.hashCode();

    }

    /**
     * a readable from of this object.
     * 
     * @return the string
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + String.valueOf(this.ident.getApplicationID()) + "#" + String.valueOf(this.ident.getInstanceID()) + "]";
    }
}
