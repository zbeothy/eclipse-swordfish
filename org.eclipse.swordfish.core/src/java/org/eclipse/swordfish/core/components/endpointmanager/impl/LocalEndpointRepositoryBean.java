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
package org.eclipse.swordfish.core.components.endpointmanager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.configrepos.dao.LocalEndpoint;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.srproxy.impl.SPDXPortImpl;

/**
 * The Class LocalEndpointRepositoryBean.
 */
public class LocalEndpointRepositoryBean implements LocalEndpointRepository {

    /** The service list. */
    private List serviceList;

    /** The spdx port map map. */
    private Map spdxPortMapMap;

    /**
     * Instantiates a new local endpoint repository bean.
     */
    public LocalEndpointRepositoryBean() {
        super();
        this.spdxPortMapMap = new HashMap();
        this.serviceList = new ArrayList();
    }

    /**
     * destroy method.
     */
    public void destroy() {
        if (this.serviceList != null) {
            this.serviceList.clear();
            this.serviceList = null;
        }
        if (this.spdxPortMapMap != null) {
            this.spdxPortMapMap.clear();
            this.spdxPortMapMap = null;
        }
    }

    /**
     * Gets the local callback definition.
     * 
     * @param wsdlPortTypeQName
     *        the wsdl port type Q name
     * 
     * @return the local callback definitionn
     */
    public Service getLocalCallbackDefinition(final QName wsdlPortTypeQName) {
        QName toSearch = new QName(wsdlPortTypeQName.getNamespaceURI(), wsdlPortTypeQName.getLocalPart() + "__service");
        for (int i = 0; i < this.serviceList.size(); i++) {
            LocalEndpoint le = (LocalEndpoint) this.serviceList.get(i);
            if (le.getService().getQName().equals(toSearch) && le.isCallbackEndpoint()) return le.getService();
        }
        return null;
    }

    /**
     * Gets the local service definition.
     * 
     * @param wsdlServiceQName
     *        the wsdl service Q name
     * 
     * @return the local service definitionn
     */
    public Service getLocalServiceDefinition(final QName wsdlServiceQName) {
        for (int i = 0; i < this.serviceList.size(); i++) {
            LocalEndpoint le = (LocalEndpoint) this.serviceList.get(i);
            if (le.getService().getQName().equals(wsdlServiceQName) && !le.isCallbackEndpoint()) return le.getService();
        }
        return null;
    }

    /**
     * Gets the service list.
     * 
     * @return Returns the serviceList.
     */
    public List getServiceList() {
        return this.serviceList;
    }

    /**
     * Gets the SPDX ports for service name.
     * 
     * @param wsdlServiceName
     *        the wsdl service name
     * 
     * @return the SPDX ports for service namee
     */
    public Map getSPDXPortsForServiceName(final QName wsdlServiceName) {
        if (this.spdxPortMapMap.containsKey(wsdlServiceName))
            return (Map) this.spdxPortMapMap.get(wsdlServiceName);
        else {
            Map value = new HashMap();
            Service srv = this.getLocalServiceDefinition(wsdlServiceName);
            if (null != srv) {
                Map ports = srv.getPorts();
                for (Iterator iter = ports.keySet().iterator(); iter.hasNext();) {
                    Port prt = (Port) ports.get(iter.next());
                    value.put(prt.getName(), new SPDXPortImpl(prt));
                }
                this.spdxPortMapMap.put(wsdlServiceName, value);
            }
            return value;
        }
    }

    /**
     * Init.
     */
    public void init() {
    }

    /**
     * Sets the service list.
     * 
     * @param serviceList
     *        The serviceList to set.
     */
    public void setServiceList(final List serviceList) {
        this.serviceList = serviceList;
    }

}
