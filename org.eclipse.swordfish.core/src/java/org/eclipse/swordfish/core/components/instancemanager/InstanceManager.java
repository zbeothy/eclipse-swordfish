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
package org.eclipse.swordfish.core.components.instancemanager;

import java.util.Observer;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.InternalSBB;

/**
 * The duty of this component is to associate an InternalSBB instance with different objects, thus
 * all of them can be used as a key to retrieve the same InternalSBB instance. This manager is
 * intended to be a singelton inside the whole InternalSBB-Engine
 */
public interface InstanceManager {

    /** the role of this component. */
    String ROLE = InstanceManager.class.getName();

    /**
     * Adds the observer.
     * 
     * @param observer
     *        the observer
     */
    void addObserver(Observer observer);

    /**
     * associats an InternalSBB with a InternalParticipantIdentity for the use case of creating an
     * InternalSBB.
     * 
     * @param sbb
     *        the InternalSBB to be associated.
     * @param identity
     *        the participant Identity to associate this InternalSBB with
     * 
     * @throws AssociationException
     *         if the assiciation cannot be made
     */
    void associate(InternalSBB sbb, InternalParticipantIdentity identity) throws AssociationException;

    /**
     * find the InternalSBB associated with the Participant Identity.
     * 
     * @param ident
     *        the participantIdentity
     * 
     * @return -- the InternalSBB associated with the Participant or null if this association does
     *         not exist
     */
    SBBExtension query(InternalParticipantIdentity ident);

    /**
     * removes all associations that are identifying an InternalSBB instance.
     * 
     * @param sbb
     *        the instance which associations are going to be removed
     */
    void removeAllAssociations(InternalSBB sbb);

    /**
     * removes the participant association of an InternalSBB.
     * 
     * @param identity
     *        the participant identity previously associated with the InternalSBB
     */
    void removeAssociation(InternalParticipantIdentity identity);

}
