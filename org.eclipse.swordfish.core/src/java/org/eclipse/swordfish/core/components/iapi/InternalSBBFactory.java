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

import java.util.Observer;
import org.eclipse.swordfish.papi.internal.InternalSBB;

/**
 * The Interface InternalSBBFactory.
 */
public interface InternalSBBFactory {

    /** The ROLE. */
    String ROLE = InternalSBBFactory.class.getName();

    /** The identifier. */
    String IDENTIFIER = "sbb/internal:type=InternalSBBFactory";

    /**
     * Adds the observer.
     * 
     * @param observer
     *        the observer
     */
    void addObserver(Observer observer);

    /**
     * Creates a new InternalSBB object.
     * 
     * @param identitiy
     *        the identitiy
     * 
     * @return the InternalSBB
     */
    InternalSBB createSBB(org.eclipse.swordfish.papi.internal.InternalParticipantIdentity identitiy);

}
