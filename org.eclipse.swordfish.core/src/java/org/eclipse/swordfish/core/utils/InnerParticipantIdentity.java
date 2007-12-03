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
/*
 * Created on 24.08.2005
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package org.eclipse.swordfish.core.utils;

import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;

/**
 * The Class InnerParticipantIdentity.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */

public class InnerParticipantIdentity implements InternalParticipantIdentity {

    /** The application. */
    private String application;

    /** The instance. */
    private String instance;

    /**
     * The Constructor.
     * 
     * @param application
     *        the application
     * @param instance
     *        the instance
     */
    public InnerParticipantIdentity(final String application, final String instance) {
        super();
        this.application = application;
        this.instance = instance;
    }

    /**
     * Gets the application ID.
     * 
     * @return the application ID
     * 
     * @see org.eclipse.swordfish.papi.InternalParticipantIdentity#getApplicationID()
     */
    public String getApplicationID() {
        return this.application;
    }

    /**
     * Gets the instance ID.
     * 
     * @return the instance ID
     * 
     * @see org.eclipse.swordfish.papi.InternalParticipantIdentity#getInstanceID()
     */
    public String getInstanceID() {
        return this.instance;
    }
}
