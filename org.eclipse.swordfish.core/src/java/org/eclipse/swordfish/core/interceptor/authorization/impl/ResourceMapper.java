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
package org.eclipse.swordfish.core.interceptor.authorization.impl;

/**
 * The Interface ResourceMapper.
 * 
 */
public interface ResourceMapper {

    /**
     * following method must be implemented to use a custom az mapper.
     * 
     * @param serviceNamespaceURI
     *        namespace
     * @param serviceLocalPart
     *        localpart
     * @param operation
     *        operation
     * 
     * @return String resourceid
     */
    String getResourceID(String serviceNamespaceURI, String serviceLocalPart, String operation);

}
