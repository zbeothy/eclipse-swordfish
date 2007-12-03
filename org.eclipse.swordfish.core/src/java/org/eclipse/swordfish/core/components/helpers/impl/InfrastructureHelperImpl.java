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
package org.eclipse.swordfish.core.components.helpers.impl;

import org.eclipse.swordfish.papi.internal.extension.infrastructure.InternalInfrastructureHelper;
import org.eclipse.swordfish.papi.internal.extension.infrastructure.InternalInfrastructureInstance;

/**
 * The Class InfrastructureHelperImpl.
 */
public class InfrastructureHelperImpl implements InternalInfrastructureHelper {

    /** The infra instance. */
    private InternalInfrastructureInstance infraInstance = null;

    /**
     * Instantiates a new infrastructure helper impl.
     * 
     * @param confURL
     *        the conf URL
     * @param srURL
     *        the sr URL
     */
    public InfrastructureHelperImpl(final String confURL, final String srURL) {
        this.infraInstance = new InfrastructureInstanceImpl(confURL, srURL);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.infrastructure.InternalInfrastructureHelper#getCurrentInfrastructureInstance()
     */
    public InternalInfrastructureInstance getCurrentInfrastructureInstance() {
        return this.infraInstance;
    }

}
