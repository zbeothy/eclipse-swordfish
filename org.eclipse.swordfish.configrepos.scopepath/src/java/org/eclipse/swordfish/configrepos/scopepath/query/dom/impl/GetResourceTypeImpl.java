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
package org.eclipse.swordfish.configrepos.scopepath.query.dom.impl;

import org.eclipse.swordfish.configrepos.scopepath.query.dom.GetResourceType;
import org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQuery;

/**
 * The Class GetResourceTypeImpl.
 * 
 */
public class GetResourceTypeImpl implements GetResourceType {

    /** The _ resource query. */
    private ResourceQuery resourceQuery = null;

    /**
     * (non-Javadoc).
     * 
     * @return the resource query
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.GetResourceType#getResourceQuery()
     */
    public ResourceQuery getResourceQuery() {
        return this.resourceQuery;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aResQuery
     *        the a res query
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.GetResourceType#setResourceQuery(org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQuery)
     */
    public void setResourceQuery(final ResourceQuery aResQuery) {
        this.resourceQuery = aResQuery;
    }

}
