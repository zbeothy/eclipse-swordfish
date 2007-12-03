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
package org.eclipse.swordfish.configrepos.mock;

import org.springframework.core.io.Resource;

/**
 * The Class ResourceUsingMock.
 * 
 */
public class ResourceUsingMock {

    /** The target which should be set. */
    private Resource target = null;

    /**
     * Instantiates a new resource using mock.
     */
    public ResourceUsingMock() {
        super();
    }

    /**
     * Gets the target.
     * 
     * @return Returns the target.
     */
    public Resource getTarget() {
        return this.target;
    }

    /**
     * Sets the target.
     * 
     * @param target
     *        The target to set.
     */
    public void setTarget(final Resource target) {
        this.target = target;
    }

}
