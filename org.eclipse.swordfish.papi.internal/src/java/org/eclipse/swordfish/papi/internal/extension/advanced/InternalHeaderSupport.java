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
package org.eclipse.swordfish.papi.internal.extension.advanced;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.w3c.dom.DocumentFragment;

/**
 * Interface to set and access application-defined message headers .<br>
 * 
 * An instance implementing this interface can be obtained via the environment of an existing SBB by
 * 
 * <pre>
 *     InternalHeaderSupport myHeaderSupport =  mySbb.getEnvironment().getComponent(
 *         org.eclipse.swordfish.papi.extension.advanced.HeaderSupport.class, null)
 * </pre>
 * 
 */
public interface InternalHeaderSupport {

    /**
     * Retrieves an application-defined message header from an incoming message.
     * 
     * @param message -
     *        the incoming message from which the header shall be retrieved
     * @param headerName -
     *        a <code>QName</code> specifiying the fully qualified name (XML namespace and local
     *        name) of the header element.
     * @return a <code>DocumentFragment</code> containing the header content including the
     *         enclosing element specified by headerName or <code>null</code> if the message does
     *         not contain the header
     */
    DocumentFragment getHeader(InternalIncomingMessage message, QName headerName);

    /**
     * Lists all application-defined message headers available in the incoming message.
     * 
     * @param message -
     *        the incoming message for which the headers shall be listed
     * @return an array of <code>QName</code>s containing the fully qualified names (XML
     *         namespace and local name) of the header elements
     */
    QName[] listHeaderNames(InternalIncomingMessage message);

    /**
     * Adds an application-defined message header to an outgoing message.
     * <p>
     * Note: make sure to use a name space aware document builder to create the header's document
     * fragment representation.
     * 
     * @param message -
     *        the outgoing message to which the header shall be added.
     * @param headerName -
     *        a <code>QName</code> specifiying the fully qualified name (XML namespace and local
     *        name) of the header element.
     * @param headerContent -
     *        a <code>DocumentFragment</code> containing the header content including the
     *        enclosing element specified by headerName
     * @throws InternalSBBException
     *         if adding the header had to be aborted due to an internal error
     */
    void setHeader(InternalOutgoingMessage message, QName headerName, DocumentFragment headerContent) throws InternalSBBException;

    /**
     * This extension operation provides a handle to participant's application to deal with SOAP
     * mustunderstand headers.
     * <p>
     * A particioant can use this method to associate an array of tag names along with an operation.
     * When using this operation the particiopant code is not able to add any header with
     * mustUnderstand set to true, if the headers is not included in the QName[]. In this case a
     * ServiceInvocationException is thrown to the participant application, when trying to send the
     * message.
     * <p>
     * if an inbound request includes a header with must understand set to true and this headers is
     * not included in the QName[], than SBB throws a SOAPMustunderstandFault to the caller.
     * <p>
     * Both, operation and header QName[] must not be null, otherwise an IllegalArgumentException is
     * thrown.
     * <p>
     * Initially no MustUnderstand headers are supported. That means that any message that comes
     * with such an indication will cause a SOAPFault. This is the same behaviour that one can
     * achieve by passing an empty array to this method.
     * 
     * @param operation -
     *        The operation that is supporting the headers
     * @param headers -
     *        Array of QNames defining the namespace of the supported must understang header. The
     *        local part indicates the tag name of the must understand header.
     */
    void setSupportedMustUnderstandHeaders(InternalOperation operation, QName[] headers);
}
