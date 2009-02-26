/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.wsdl;

import java.util.Collection;
import java.util.Map;

import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;


public interface ServiceDescription {

	public QName getPortTypeQName();

	/**
	 * Gets the partner port type Q name.
	 *
	 * @return the partner port type Q name
	 */
	public QName getPartnerPortTypeQName();

	/** {@inheritDoc}
	 * @see org.sbb.core.components.resolver.CompoundServiceDescription#getServiceQName()
	 */
	public QName getServiceQName();

	/** {@inheritDoc}
	 * @see org.sbb.core.components.resolver.CompoundServiceDescription#getOperations()
	 */
	public Collection<Operation> getOperations();

	public Operation getOperation(String operationName);

	public SwordfishPort getPort(String wsdlPortName);

	public SwordfishPort[] getSupportedPorts(String operationName);

	public SwordfishPort[] getPorts();

	/**
	 * Chooses the WSDL port to use as the endpoint for the operation referred
	 * to by the supplied operation name The choice depends on 1. which ports
	 * are available in the service description 2. what types of transport they
	 * are bound to 3. what types of transport are defined by the agreed policy
	 * for the operation
	 *
	 * @param operationName the operation name (local part of WSDL operation name)
	 * @param defaultTransport the default transport
	 *
	 * @return the selected SwordfishPort
	 */

	public SwordfishPort choosePort(String operationName,
			String defaultTransport);

	/**
	 * returns the (we support only one)"part" of the input message of the
	 * indicated operation.
	 *
	 * @param operationName the operation name
	 *
	 * @return the operation input message part
	 */
	public Part getOperationInputMessagePart(String operationName);

	/**
	 * returns the (we support only one)"part" of the out message of the
	 * indicated operation.
	 *
	 * @param operationName the operation name
	 *
	 * @return the operation output message part
	 */
	public Part getOperationOutputMessagePart(String operationName);

	/** Returns tuples containing containing WS location uri and the SoapBinding object
	 * The SoapBindng object exposes the transport uri if available
	 * @return
	 */
	public  Map<SOAPAddress, SOAPBinding> getAvailableLocations();

}