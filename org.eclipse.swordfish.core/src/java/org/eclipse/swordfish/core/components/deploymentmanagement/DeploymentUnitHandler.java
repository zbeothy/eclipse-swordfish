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
package org.eclipse.swordfish.core.components.deploymentmanagement;

import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * This is the very minimal deployment interface that is very likely to be expanded once we have a
 * better idea of which functionality is required for the deployment objects.
 */
public interface DeploymentUnitHandler {

    /** The ROLE. */
    String ROLE = DeploymentUnitHandler.class.getName();

    /**
     * performs the real depolyment.
     * 
     * @throws InternalInfrastructureException
     */
    void deploy() throws InternalInfrastructureException;

    /**
     * Gets the handling type.
     * 
     * @return -- a DeploymentUnitType which is handeled by this handler.
     */
    DeploymentUnitType getHandlingType();

    /**
     * performs an undeployment.
     * 
     * @throws InternalInfrastructureException
     */
    void undeploy() throws InternalInfrastructureException;
}
