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
package org.eclipse.swordfish.core.components.resolver;

import java.util.Collection;
import java.util.List;
import javax.wsdl.Part;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Scope;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.w3c.dom.Document;

/**
 * The Interface CompoundServiceDescription.
 */
public interface CompoundServiceDescription {

    /**
     * Choose port.
     * 
     * @param operationName
     *        the operation name
     * @param defaultTransport
     *        the default transport
     * 
     * @return the SPDX port
     */
    SPDXPort choosePort(String operationName, String defaultTransport);

    /**
     * Creates the WSDL.
     * 
     * @return the document
     */
    Document createWSDL();

    /**
     * Gets the agreed policy.
     * 
     * @return the agreed policy
     */
    AgreedPolicy getAgreedPolicy();

    /**
     * Gets the agreed transports.
     * 
     * @param operationName
     *        the operation name
     * @param scope
     *        the scope
     * 
     * @return the agreed transports
     */
    List /* <String> */getAgreedTransports(String operationName, Scope scope);

    /**
     * Gets the default fault operation.
     * 
     * @return the default fault operation
     */
    OperationDescription getDefaultFaultOperation();

    /**
     * Gets the operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the operation
     */
    OperationDescription getOperation(String operationName);

    /**
     * Gets the operation input message part.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the operation input message part
     */
    Part getOperationInputMessagePart(String operationName);

    /**
     * Gets the operation output message part.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the operation output message part
     */
    Part getOperationOutputMessagePart(String operationName);

    /**
     * Gets the operations.
     * 
     * @return the operations
     */
    Collection /* <OperationDescription> */getOperations();

    /**
     * Gets the partner description.
     * 
     * @return the partner description
     */
    CompoundServiceDescription getPartnerDescription();

    /**
     * Gets the port.
     * 
     * @param wsdlPortName
     *        the wsdl port name
     * 
     * @return the port
     */
    SPDXPort getPort(String wsdlPortName);

    /**
     * Gets the ports.
     * 
     * @return the ports
     */
    SPDXPort[] getPorts();

    /**
     * Gets the port type Q name.
     * 
     * @return the port type Q name
     */
    QName getPortTypeQName();

    /**
     * Gets the provider policy.
     * 
     * @param providerPolicyId
     *        the provider policy id
     * 
     * @return the provider policy
     */
    ParticipantPolicy getProviderPolicy(String providerPolicyId);

    /**
     * Gets the reply endpoint for operation.
     * 
     * @param name
     *        the name
     * 
     * @return the reply endpoint for operation
     */
    String getReplyEndpointForOperation(String name);

    /**
     * Gets the service Q name.
     * 
     * @return the service Q name
     */
    QName getServiceQName();

    /**
     * Gets the soap action.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the soap action
     */
    String getSoapAction(String operationName);

    /**
     * Gets the supported port local names.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the supported port local names
     */
    String[] getSupportedPortLocalNames(String operationName);

    /**
     * Gets the supported ports.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the supported ports
     */
    SPDXPort[] getSupportedPorts(String operationName);

    /**
     * Gets the WSD ldefined schemas.
     * 
     * @return the WSD ldefined schemas
     */
    List getWSDLdefinedSchemas();

    /**
     * Checks for partner description.
     * 
     * @return true, if successful
     */
    boolean hasPartnerDescription();

    /**
     * Checks for provider policies.
     * 
     * @return true, if successful
     */
    boolean hasProviderPolicies();

    /**
     * Checks if is notification only port.
     * 
     * @param portName
     *        the port name
     * 
     * @return true, if is notification only port
     */
    boolean isNotificationOnlyPort(String portName);

    /**
     * Checks if is notification port.
     * 
     * @param portName
     *        the port name
     * 
     * @return true, if is notification port
     */
    boolean isNotificationPort(String portName);

    /**
     * Checks if is partner description.
     * 
     * @return true, if is partner description
     */
    boolean isPartnerDescription();

    /**
     * Checks if is partner operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return true, if is partner operation
     */
    boolean isPartnerOperation(String operationName);
}
