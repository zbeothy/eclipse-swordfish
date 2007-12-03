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
import org.eclipse.swordfish.configrepos.RepositorySource;
import org.eclipse.swordfish.configrepos.resource.exceptions.ConfigurationRepositoryResourceException;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;

/**
 * The Interface ResourceSource.
 * 
 */
public interface ResourceSource extends RepositorySource {

    /**
     * Fetch a resource byte array for a specific identifier.
     * 
     * @param aTreeQualifier
     *        which should be used as a qualifier
     * @param aScopePath
     *        which should be traversed
     * @param aResourceIdentifier
     *        which is the name of actual to be fetched resource
     * @param aComponent
     *        which is the owner of the resource
     * 
     * @return the resource byte array which was found for the identifier. Null in case nothing was
     *         found.
     * 
     * @throws ConfigurationRepositoryResourceException
     *         in case a technical error occured.
     */
    InputStream getResource(final String aTreeQualifier, final ScopePath aScopePath, final String aComponent,
            final String aResourceIdentifier) throws ConfigurationRepositoryResourceException;
}
