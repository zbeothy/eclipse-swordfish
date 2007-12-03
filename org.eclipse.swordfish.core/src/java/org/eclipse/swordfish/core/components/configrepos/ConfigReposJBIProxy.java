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
package org.eclipse.swordfish.core.components.configrepos;

import org.eclipse.swordfish.configrepos.configuration.sources.RemoteConfigSourceProxy;
import org.eclipse.swordfish.configrepos.resource.sources.RemoteResourceSourceProxy;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.w3c.dom.Document;

/**
 * The Interface ConfigReposJBIProxy.
 * 
 */
public interface ConfigReposJBIProxy extends RemoteConfigSourceProxy, RemoteResourceSourceProxy {

    /**
     * Gets the configuration data.
     * 
     * @param aIdentity
     *        which is makine the call
     * @param aRequestPayload
     *        holds the request document
     * 
     * @return the source which containes the reply document
     * 
     * @throws Exception
     *         in case of any error which occures
     * 
     */
    public Document getConfigurationData(final InternalParticipantIdentity aIdentity, final Document aRequestPayload)
            throws Exception;

    /**
     * Gets the configuration data.
     * 
     * @param aIdentity
     *        which is makine the call
     * @param aRequestPayload
     *        holds the request document
     * 
     * @return Document with the resource data inside a resourceResponse tag
     * 
     * @throws Exception
     *         in case of any problem during the exchange
     * 
     */
    public Document getResourceData(final InternalParticipantIdentity aIdentity, final Document aRequestPayload) throws Exception;

}
