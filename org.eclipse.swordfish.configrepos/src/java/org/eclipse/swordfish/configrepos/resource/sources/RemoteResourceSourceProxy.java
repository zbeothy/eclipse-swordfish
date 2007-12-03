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

import org.w3c.dom.Document;

/**
 * The Interface RemoteResourceSourceProxy.
 * 
 */
public interface RemoteResourceSourceProxy {

    /**
     * Gets the resource data.
     * 
     * @param appId
     *        the configuration should be fetched for
     * @param instId
     *        the configuration should be fetched for
     * @param aRequest
     *        which should be sent
     * 
     * @return a Source object for reading the results
     * 
     * @throws Exception
     *         in case any exception arrises
     */
    Document getResourceData(final String appId, final String instId, final Document aRequest) throws Exception;
}
