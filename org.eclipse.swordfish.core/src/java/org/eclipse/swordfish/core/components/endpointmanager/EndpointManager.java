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

import javax.jbi.JBIException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;

/**
 * this class manages the activation and deactivation of endpoints related to an operation of a
 * particular service provided by a participant. It also alows queries about the participant given
 * an JBI endpoint.
 */
public interface EndpointManager {

    /** This classes role. */
    String ROLE = EndpointManager.class.getName();

    /**
     * activates all endpoints (regardless of the binding) for a particiant and all operation using
     * the service description.
     * 
     * @param participant
     *        the participant requesting the activation
     * @param serviceDesc
     *        the service description that contains the ports to be activated
     * @param repos
     *        the repos
     * @param locationId
     *        the location id
     * 
     * @throws JBIException
     *         if it is not possible to activate an endpoint
     */
    void activateAllEndpoints(UnifiedParticipantIdentity participant, CompoundServiceDescription serviceDesc,
            LocalEndpointRepository repos, String locationId) throws JBIException;

    /**
     * Activate notification endpoint.
     * 
     * @param participant
     *        the participant
     * @param serviceDesc
     *        the service desc
     * @param operationName
     *        the operation name
     * @param repos
     *        the repos
     * 
     * @throws JBIException
     */
    void activateNotificationEndpoint(final UnifiedParticipantIdentity participant, final CompoundServiceDescription serviceDesc,
            final String operationName, LocalEndpointRepository repos) throws JBIException;

    /**
     * deactivates all endpoints (regardless of the binding) for a particiant and all operation
     * using the service description.
     * 
     * @param participant
     *        the participant requesting the deactivation
     * @param serviceDesc
     *        the service description that contains the ports to be deactivated
     * @param repos
     *        the repos
     * @param locationId
     *        the location id
     * 
     * @throws JBIException
     *         if it is not possible to deactivate an endpoint
     */
    void deactivateAllEndpoints(UnifiedParticipantIdentity participant, CompoundServiceDescription serviceDesc,
            LocalEndpointRepository repos, String locationId) throws JBIException;

    /**
     * Deactivate notification endpoint.
     * 
     * @param participant
     *        the participant
     * @param serviceDesc
     *        the service desc
     * @param operationName
     *        the operation name
     * @param repos
     *        the repos
     * 
     * @throws JBIException
     */
    void deactivateNotificationEndpoint(final UnifiedParticipantIdentity participant, final CompoundServiceDescription serviceDesc,
            final String operationName, LocalEndpointRepository repos) throws JBIException;

    /**
     * returns the participant identity of a participant that has been activating the given endpoint
     * for the indicated operation.
     * 
     * @param se
     *        the service endpoint ServiceEndpoint
     * 
     * @return -- the unified participant identity of the participant that has been activating this
     *         Service endpoint and operation
     */
    UnifiedParticipantIdentity getParticipantIdentityUnifier(ServiceEndpoint se);

    /**
     * returns the service description of the service that is provided through the endpoint.
     * 
     * @param se
     *        the service endpoint that identifies the service description
     * 
     * @return -- the CompoundServiceDescription
     */
    CompoundServiceDescription getServiceDescription(ServiceEndpoint se);

    /**
     * returns the service endpoint activated for the supplied WSDL service name, WSDL port name and
     * operation name.
     * 
     * @param wsdlServiceQName
     *        the WSDL service QName
     * @param wsdlPortName
     *        the WSDL port name
     * 
     * @return -- the AggregateServiceDescription
     */
    ServiceEndpoint getServiceEndpoint(final QName wsdlServiceQName, final String wsdlPortName);
}
