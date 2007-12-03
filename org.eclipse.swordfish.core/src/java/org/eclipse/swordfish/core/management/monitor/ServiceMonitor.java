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
package org.eclipse.swordfish.core.management.monitor;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;

/**
 * The Class ServiceMonitor.
 */
public class ServiceMonitor extends BaseMonitor {

    /** InternalSBB participant that registered the service. */
    private UnifiedParticipantIdentity participant;

    /** InternalSBB service this monitor is responsible for. */
    private QName service;

    /** The role. */
    private ParticipantRole role;

    /**
     * Instantiates a new service monitor.
     * 
     * @param participant
     *        the participant
     * @param service
     *        the service
     * @param role
     *        the role
     */
    public ServiceMonitor(final UnifiedParticipantIdentity participant, final QName service, final ParticipantRole role) {
        this.participant = participant;
        this.service = service;
        this.role = role;
    }

    /**
     * Gets the participant id.
     * 
     * @return the participant id
     */
    public String getParticipantId() {
        return String.valueOf(this.participant);
    }

    /**
     * Gets the role.
     * 
     * @return the role
     */
    public String getRole() {
        return String.valueOf(this.role);
    }

    /**
     * Gets the service.
     * 
     * @return the service
     */
    public String getService() {
        return String.valueOf(this.service);
    }

}
