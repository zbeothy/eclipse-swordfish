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
package org.eclipse.swordfish.core.components.endpointreferenceresolver;

import javax.jbi.servicedesc.ServiceEndpoint;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.w3c.dom.DocumentFragment;

/**
 * The Interface EndpointReferenceResolver.
 */
public interface EndpointReferenceResolver {

    /**
     * Used to create an Oracle/JBI document fragment if no PAPI is available description.
     * 
     * @param asd
     *        the asd
     * @param operationName
     *        the operation name
     * 
     * @return the document fragment
     */
    DocumentFragment createEndpointReference(CompoundServiceDescription asd, String operationName);

    /**
     * Creates an Oracle/JBI document fragment from an InternalSBB operation description.
     * 
     * @param opdesc
     *        the operation for which to create the endpoint
     * 
     * @return the document fragment
     */
    DocumentFragment createEndpointReference(OperationDescription opdesc);

    /**
     * Turns a WS-Addressing object into an Oracle/JBI document fragment.
     * 
     * @param address
     *        the address
     * 
     * @return the document fragment
     */
    DocumentFragment createEndpointReference(WSAEndpointReference address);

    /**
     * returns the Endpoint name that is going to be used when an EPR is created for this endpoint.
     * 
     * @param opdesc
     *        the opdesc
     * 
     * @return the endpoint name for operation
     */
    String getEndpointNameForOperation(OperationDescription opdesc);

    /**
     * (Indirectly) called by the container to turn a DocumentFragment into a ServiceEndpoint that
     * can be used to create a message exchange factory.
     * 
     * @param epr
     *        the document fragment
     * 
     * @return the service endpoint
     */
    ServiceEndpoint resolveEndpointReference(DocumentFragment epr);
}
