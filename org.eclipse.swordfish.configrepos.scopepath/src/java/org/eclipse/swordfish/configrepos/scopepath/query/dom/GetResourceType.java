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
package org.eclipse.swordfish.configrepos.scopepath.query.dom;

/**
 * The Interface GetResourceType.
 */
public interface GetResourceType {

    /**
     * Gets the resource query.
     * 
     * @return the resource query
     */
    ResourceQuery getResourceQuery();

    /**
     * Sets the resource query.
     * 
     * @param aResQuery
     *        the a res query
     */
    void setResourceQuery(ResourceQuery aResQuery);

}
