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

import java.io.InputStream;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * The Interface WritebackResourceSource.
 * 
 */
public interface WritebackResourceSource extends ResourceSource {

    /**
     * Write back the resource data into the local synchronization repository.
     * 
     * @param aTreeQualifier
     *        to be selected
     * @param aScopePath
     *        to be traversed
     * @param aComponentName
     *        for which the configuration will be updated
     * @param aResourceId
     *        is the resource which should be updated
     * @param aResourceDataStream
     *        is the data which will be saved in the resource
     * 
     * @throws ConfigurationRepositoryResourceException
     *         in case the update failed
     */
    void updateResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponentName,
            final String aResourceId, final InputStream aResourceDataStream) throws ConfigurationRepositoryResourceException;
}
