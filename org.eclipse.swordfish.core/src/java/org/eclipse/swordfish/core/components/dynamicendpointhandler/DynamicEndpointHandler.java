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
package org.eclipse.swordfish.core.components.dynamicendpointhandler;

import java.util.Map;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;

/**
 * The Interface DynamicEndpointHandler.
 */
public interface DynamicEndpointHandler {

    /**
     * Deploy dynamic endpoint.
     * 
     * @param serviceName
     *        the service name
     * @param port
     *        the port
     * @param isUsingLocator
     *        the is using locator
     * @param locationId
     *        the location id
     * @param properties
     *        the properties
     */
    void deployDynamicEndpoint(final QName serviceName, SPDXPort port, boolean isUsingLocator, String locationId, Map properties);

    /**
     * Deploy dynamic notification endpoint.
     * 
     * @param serviceName
     *        the service name
     * @param port
     *        the port
     * @param operationName
     *        the operation name
     * @param repos
     *        the repos
     * @param participant
     *        the participant
     * @param properties
     *        the properties
     */
    void deployDynamicNotificationEndpoint(final QName serviceName, SPDXPort port, String operationName,
            LocalEndpointRepository repos, UnifiedParticipantIdentity participant, Map properties);

    /**
     * Undeploy dynamic endpoint.
     * 
     * @param sep
     *        the sep
     * @param serviceDesc
     *        the service desc
     * @param repos
     *        the repos
     * @param locationId
     *        the location id
     */
    void undeployDynamicEndpoint(ServiceEndpoint sep, CompoundServiceDescription serviceDesc, LocalEndpointRepository repos,
            String locationId);

    /**
     * Undeploy dynamic notification endpoint.
     * 
     * @param serviceName
     *        the service name
     * @param endpointName
     *        the endpoint name
     * @param operationName
     *        the operation name
     * @param repos
     *        the repos
     * @param participant
     *        the participant
     */
    void undeployDynamicNotificationEndpoint(QName serviceName, String endpointName, String operationName,
            LocalEndpointRepository repos, UnifiedParticipantIdentity participant);
}
