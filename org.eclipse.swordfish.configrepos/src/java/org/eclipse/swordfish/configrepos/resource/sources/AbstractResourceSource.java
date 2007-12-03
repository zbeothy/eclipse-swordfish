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
package org.eclipse.swordfish.configrepos.resource.sources;

import org.eclipse.swordfish.configrepos.AbstractRepositorySource;

/**
 * This abstract class is used to base for all types of resource sources, used in conjunction with
 * the SOP Configuration Repository Proxy.
 * 
 */
public abstract class AbstractResourceSource extends AbstractRepositorySource implements ResourceSource {

    /** Size of read buffer while writing configurations into the cache. */
    public static final int BUFFEREDREAD_SIZE = 4096;

    /**
     * Instantiates a new abstract resource source.
     */
    public AbstractResourceSource() {
        super();
    }
}
