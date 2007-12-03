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
package org.eclipse.swordfish.core.components.iapi;

import java.net.URI;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.addressing.WSAEndpointReference;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageExchangePattern;

/**
 * The Class OperationDescription.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class OperationDescription {

    /** The Constant PARTNEROPERATION_ATTRIBUTE_QNAME. */
    public static final QName PARTNEROPERATION_ATTRIBUTE_QNAME =
            new QName("http://types.sopware.org/service/ServiceDescription/2.0", "partnerOperation");

    /** The Constant FAULTOPERATION_ATTRIBUTE_QNAME. */
    public static final QName FAULTOPERATION_ATTRIBUTE_QNAME =
            new QName("http://types.sopware.org/service/ServiceDescription/2.0", "faultOperation");

    /** The exchange map. */
    private static HashMap exchangeMap;

    static {
        exchangeMap = new HashMap();
        exchangeMap.put(javax.wsdl.OperationType.NOTIFICATION, MessageExchangePattern.OUT_ONLY_URI);
        exchangeMap.put(javax.wsdl.OperationType.ONE_WAY, MessageExchangePattern.IN_ONLY_URI);
        exchangeMap.put(javax.wsdl.OperationType.REQUEST_RESPONSE, MessageExchangePattern.IN_OUT_URI);
        exchangeMap.put(javax.wsdl.OperationType.SOLICIT_RESPONSE, MessageExchangePattern.OUT_IN_URI);

    }

    /** The internalOperation. */
    private javax.wsdl.Operation internalOperation;

    /** The service desc. */
    private CompoundServiceDescription serviceDesc;

    /** The soap action. */
    private String soapAction;

    /**
     * Instantiates a new internalOperation description.
     * 
     * @param serviceDesc
     *        the service desc
     * @param wsdlOperation
     *        the wsdl internalOperation
     */
    public OperationDescription(final CompoundServiceDescription serviceDesc, final javax.wsdl.Operation wsdlOperation) {
        this.internalOperation = wsdlOperation;
        this.serviceDesc = serviceDesc;
        this.soapAction = serviceDesc.getSoapAction(wsdlOperation.getName());
    }

    /**
     * Gets the exchange pattern.
     * 
     * @return -- the URI describing the exchange pattern of this internalOperation (W3C
     *         interactionstyle) as defined in WSDL
     */
    public URI getExchangePattern() {
        return (URI) exchangeMap.get(this.internalOperation.getStyle());
    }

    /**
     * Gets the name.
     * 
     * @return -- an String containing the WSDL name of this internalOperation
     */
    public String getName() {
        return this.internalOperation.getName();
    }

    /**
     * Gets the partner internalOperation name.
     * 
     * @return -- the string value of the sdx:partner for this internalOperation
     */
    public String getPartnerOperationName() {
        // Map map = internalOperation.getExtensionAttributes();
        QName partnerOperationQName = (QName) this.internalOperation.getExtensionAttribute(PARTNEROPERATION_ATTRIBUTE_QNAME);
        String opName = null;
        if (partnerOperationQName != null) {
            opName = partnerOperationQName.getLocalPart();
        }
        return opName;
    }

    /**
     * Gets the service description.
     * 
     * @return Returns the serviceDescription that declared this internalOperation
     */
    public CompoundServiceDescription getServiceDescription() {
        return this.serviceDesc;
    }

    /**
     * Gets the soap action.
     * 
     * @return the soap action
     */
    public String getSoapAction() {
        return this.soapAction;
    }

    /**
     * Gets the WSA endpoint reference.
     * 
     * @param eprName
     *        the epr name
     * 
     * @return the WSA endpoint reference
     */
    public WSAEndpointReference getWSAEndpointReference(final String eprName) {
        String address = this.getServiceDescription().getReplyEndpointForOperation(this.getName());
        if ((null == address) && javax.wsdl.OperationType.ONE_WAY.equals(this.internalOperation.getStyle()))
            return null;
        else
            return new WSAEndpointReference(eprName, address, null);
    }

    /**
     * Checks for callback internalOperation.
     * 
     * @return true, if successful
     */
    public boolean hasCallbackOperation() {
        return (this.internalOperation.getExtensionAttribute(PARTNEROPERATION_ATTRIBUTE_QNAME) != null);
    }

    /**
     * Checks if is callback internalOperation.
     * 
     * @return true, if is callback internalOperation
     */
    public boolean isCallbackOperation() {
        return (this.serviceDesc.isPartnerOperation(this.getName()));
    }

    /**
     * Checks if is default fault internalOperation.
     * 
     * @return true, if is default fault internalOperation
     */
    public boolean isDefaultFaultOperation() {
        QName result = (QName) this.internalOperation.getExtensionAttribute(FAULTOPERATION_ATTRIBUTE_QNAME);
        boolean res = false;
        if (result != null) {
            res = Boolean.valueOf(result.getLocalPart()).booleanValue();
        }
        return res;
    }
}
