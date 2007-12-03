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

import java.io.Serializable;

/**
 * The Class DeploymentUnitState.
 */
public final class DeploymentUnitState implements Serializable {

    /** The Constant UNKNOWN. */
    public static final DeploymentUnitState UNKNOWN = new DeploymentUnitState(DeploymentUnitState.UNKNOWN_STR);

    /** The Constant DEPLOYED. */
    public static final DeploymentUnitState DEPLOYED = new DeploymentUnitState(DeploymentUnitState.DEPLOYED_STR);

    /** The Constant SHUTDOWN. */
    public static final DeploymentUnitState SHUTDOWN = new DeploymentUnitState(DeploymentUnitState.SHUTDOWN_STR);

    /** The Constant STOPPED. */
    public static final DeploymentUnitState STOPPED = new DeploymentUnitState(DeploymentUnitState.STOPPED_STR);

    /** The Constant STARTED. */
    public static final DeploymentUnitState STARTED = new DeploymentUnitState(DeploymentUnitState.STARTED_STR);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7984176992891010054L;

    /** string constants. */
    private static final String UNKNOWN_STR = "UNKNOWN";

    /** The Constant DEPLOYED_STR. */
    private static final String DEPLOYED_STR = "DEPLOYED";

    /** The Constant SHUTDOWN_STR. */
    private static final String SHUTDOWN_STR = "SHUTDOWN";

    /** The Constant STOPPED_STR. */
    private static final String STOPPED_STR = "STOPPED";

    /** The Constant STARTED_STR. */
    private static final String STARTED_STR = "STARTED";

    /**
     * from String.
     * 
     * @param value
     *        the value
     * 
     * @return enum
     */
    public static DeploymentUnitState fromString(final String value) {
        if (UNKNOWN_STR.equals(value)) return UNKNOWN;
        if (DEPLOYED_STR.equals(value)) return DEPLOYED;
        if (SHUTDOWN_STR.equals(value)) return SHUTDOWN;
        if (STOPPED_STR.equals(value)) return STOPPED;
        if (STARTED_STR.equals(value)) return STARTED;
        return null;
    }

    /** value. */
    private String value;

    /**
     * private constructor.
     * 
     * @param value
     *        the value
     */
    private DeploymentUnitState(final String value) {
        this.value = value;
    }

    /**
     * (non-Javadoc).
     * 
     * @param obj
     *        the obj
     * 
     * @return true, if equals
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DeploymentUnitState) {
            if (this.value.equals(((DeploymentUnitState) obj).value)) return true;
        }
        return false;
    }

    /**
     * To string.
     * 
     * @return the string
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.value;
    }
}
