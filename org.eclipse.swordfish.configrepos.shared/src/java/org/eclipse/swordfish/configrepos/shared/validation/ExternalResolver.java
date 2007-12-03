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
package org.eclipse.swordfish.configrepos.shared.validation;

import org.w3c.dom.ls.LSInput;

/**
 * <p>
 * Instances of this interface resolve SOP resources from local URIs (LDAP, SOP-Service, Spring)
 * </p>.
 */

public interface ExternalResolver {

    /**
     * Checks if is external resource.
     * 
     * @param uri
     *        the uri
     * 
     * @return true, if is external resource
     * 
     * @throws ResourceException
     */
    boolean isExternalResource(String uri) throws ResourceException;

    /**
     * Resolve external resource.
     * 
     * @param uri
     *        the uri
     * 
     * @return the LS input
     * 
     * @throws ResourceException
     */
    LSInput resolveExternalResource(String uri) throws ResourceException;

}
