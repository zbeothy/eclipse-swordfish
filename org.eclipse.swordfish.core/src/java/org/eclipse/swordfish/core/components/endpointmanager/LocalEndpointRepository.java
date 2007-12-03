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
package org.eclipse.swordfish.core.components.endpointmanager;

import java.util.Map;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

/**
 * The Interface LocalEndpointRepository.
 */
public interface LocalEndpointRepository {

    /** The ROLE. */
    String ROLE = LocalEndpointRepository.class.getName();

    /**
     * Gets the local callback definition.
     * 
     * @param wsdlPortTypeQName
     *        the wsdl port type Q name
     * 
     * @return a service that is defined to be the callback definition for a porttype or null if it
     *         does not exist.
     */
    Service getLocalCallbackDefinition(QName wsdlPortTypeQName);

    /**
     * Gets the local service definition.
     * 
     * @param wsdlServiceQName
     *        the wsdl service Q name
     * 
     * @return a Service definition for the service with the identified name or null if not such a
     *         service is definied in the local endpoints
     */
    Service getLocalServiceDefinition(QName wsdlServiceQName);

    /**
     * Gets the SPDX ports for service name.
     * 
     * @param wsdlServiceName
     *        the wsdl service name
     * 
     * @return the SPDX ports for service name
     */
    Map getSPDXPortsForServiceName(QName wsdlServiceName);
}
