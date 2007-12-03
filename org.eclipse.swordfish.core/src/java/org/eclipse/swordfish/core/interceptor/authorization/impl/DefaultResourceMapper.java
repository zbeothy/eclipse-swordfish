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
 * The Class DefaultResourceMapper.
 * 
 * 
 * Default implementation of an authorization resource mapper maps service operations to resource
 * names of the form
 * 
 * sbb://<wsdl-service-namespace/>/<wsdlservice-localname/>#<operation-name/>
 */
public class DefaultResourceMapper implements ResourceMapper {

    /**
     * (non-Javadoc).
     * 
     * @param serviceNamespaceURI
     *        the service namespace URI
     * @param serviceLocalpart
     *        the service localpart
     * @param operation
     *        the operation
     * 
     * @return the resource ID
     * 
     * @see org.eclipse.swordfish.sec.az.mapper.AuthorizationMapper
     *      #getResourceID(javax.xml.namespace.QName, javax.xml.namespace.QName)
     */
    public String getResourceID(final String serviceNamespaceURI, final String serviceLocalpart, final String operation) {
        return "sbb://" + serviceNamespaceURI + "/" + serviceLocalpart + "#" + operation;
    }

}
