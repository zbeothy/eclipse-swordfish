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
package org.eclipse.swordfish.configrepos.dao;

import javax.wsdl.Service;
import javax.xml.namespace.QName;
import com.ibm.wsdl.ServiceImpl;

/**
 * Local endpoint.
 * 
 */
public class LocalEndpoint {

    /** The service. */
    private ServiceImpl service = null;

    /** The is callback endpoint. */
    private boolean isCallbackEndpoint = false;

    /**
     * LocalEndpoint.
     * 
     * @param namespace
     * @param portTypeName
     * @param isCallbackEndpoint
     */
    public LocalEndpoint(final String namespace, final String portTypeName, final boolean isCallbackEndpoint) {
        this.service = new ServiceImpl();
        this.service.setQName(new QName(namespace, portTypeName));
        this.isCallbackEndpoint = isCallbackEndpoint;
    }

    /**
     * Gets the service.
     * 
     * @return the service
     */
    public Service getService() {
        return this.service;
    }

    /**
     * Checks if is callback endpoint.
     * 
     * @return true, if is callback endpoint
     */
    public boolean isCallbackEndpoint() {
        return this.isCallbackEndpoint;
    }

}
