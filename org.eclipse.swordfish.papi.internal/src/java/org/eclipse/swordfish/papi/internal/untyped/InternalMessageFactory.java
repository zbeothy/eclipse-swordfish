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
package org.eclipse.swordfish.papi.internal.untyped;

import java.io.InputStream;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;

/**
 * This is the factory used to generate outgoing messages and faults.
 * <p>
 * Messages can be created from XML sources in various formats (String, DOM tree ({@link org.w3c.dom.Document}),
 * or InputStream). Messages can also be created based on an incoming message, and can optionally
 * include attachments.
 * </p>
 * <p>
 * Note: Incoming messages cannot be created via this factory; they are always created by the
 * InternalSBB internally.
 * </p>
 * 
 * @see org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage
 * 
 */
public interface InternalMessageFactory {

    /**
     * This method creates the outgoing <code>fault</code> message from an XML document, which was
     * passed in as a DOM document.
     * <p>
     * The XML input is not checked or validated against the service description. Validation takes
     * place while the message is sent.
     * </p>
     * 
     * @param aDocument
     *        The XML document from which the message is created.
     * 
     * @return The outgoing fault message. This consists of the XML document.
     * 
     * @throws PayloadConstructionException
     *         the payload construction exception
     * @throws PayloadOversizedException
     *         the payload oversized exception
     * @throws InternalSBBException
     */
    InternalOutgoingMessage createFault(Document aDocument) throws InternalSBBException;

    /**
     * This method creates the outgoing <code>fault</code> message from an XML document passed in
     * as an InputStream.
     * <p>
     * The XML input is not checked or validated against the service description. Validation takes
     * place while the message is sent. The stream passed in is not accessed within this method.
     * Problems related to the stream are signaled only while this message is sent.
     * </p>
     * 
     * @param anXmlStream
     *        The XML stream from which the message is created.
     * @return The outgoing <code>fault</code> message. It consists of the XML document, which was
     *         passed in as a stream. This stream must be open while the message is sent.
     * @throws PayloadConstructionException
     * 
     * @throws InternalSBBException
     */
    InternalOutgoingMessage createFault(InputStream anXmlStream) throws InternalSBBException;

    /**
     * This method creates the outgoing <code>fault</code> message from an XML document that is
     * passed in as a String.
     * <p>
     * The XML input is not checked or validated against the service description. Validation takes
     * place while the message is sent.
     * </p>
     * 
     * @param anXml
     *        The XML document from which the message is created.
     * 
     * @return The outgoing <code>fault</code> message. This consists of the XML document.
     * 
     * @throws PayloadConstructionException
     *         the payload construction exception
     * @throws PayloadOversizedException
     *         the payload oversized exception
     * @throws InternalSBBException
     */
    InternalOutgoingMessage createFault(String anXml) throws InternalSBBException;

    /**
     * This method creates an outgoing <code>fault</code> message based upon an incoming message.
     * The attachments of the incoming message are included in the outgoing message.
     * <p>
     * This method is used, for example, if a service provider internally calls another service and
     * wants to pass the response of this other service back to the original caller.
     * </p>
     * 
     * @param aMessage
     *        The incoming message, which is the basis for the creation of the outgoing message.
     * @return The outgoing <code>fault message</code>, which is an exact copy of the message
     *         that was passed in.
     * 
     * @throws InternalSBBException
     * @see #createFaultWithoutAttachments(InternalIncomingMessage)
     */
    InternalOutgoingMessage createFaultWithAttachments(InternalIncomingMessage aMessage) throws InternalSBBException;

    /**
     * This method creates the outgoing <code>fault</code> message based upon the incoming
     * message, but the attachments of the incoming message are not included.
     * <p>
     * The XML input is not checked or validated against the service description. Validation takes
     * place while this message is sent.
     * </p>
     * 
     * @param aMessage
     *        The incoming message, which is the basis for the creation of the outgoing message.
     * @return The outgoing <code>fault</code> message. This is an exact copy of the XML part of
     *         the message passed in, except that the incoming attachments are not copied.
     * 
     * @throws InternalSBBException
     * @see #createFaultWithAttachments(InternalIncomingMessage)
     */
    InternalOutgoingMessage createFaultWithoutAttachments(InternalIncomingMessage aMessage) throws InternalSBBException;

    /**
     * This method creates the outgoing message from an XML document passed in as a DOM document.
     * <p>
     * The XML input is not checked or validated against the service description. Validation takes
     * place instead, while the message is actually sent.
     * </p>
     * 
     * @param aDocument
     *        The XML document from which the message is created, or <code>null</code> to create
     *        an empty message.
     * 
     * @return The outgoing message. This consists of the XML document.
     * 
     * @throws PayloadConstructionException
     *         the payload construction exception
     * @throws PayloadOversizedException
     *         the payload oversized exception
     * @throws InternalSBBException
     */
    InternalOutgoingMessage createMessage(Document aDocument) throws InternalSBBException;

    /**
     * This method creates the outgoing message from an XML document passed in as an InputStream.
     * <p>
     * The XML input is not checked or validated against the service description. Validation takes
     * place instead, while the message is sent. The stream, which was passed in, is not accessed
     * within this method. Problems related to the stream are signaled only when this message is
     * actually sent.
     * </p>
     * 
     * @param anXmlStream
     *        The XML stream from which the message is created, or <code>null</code> to create an
     *        empty message.
     * 
     * @return The outgoing message. It consists of the XML document passed in as a stream. This
     *         stream must be open when the message is sent.
     * 
     * @throws PayloadConstructionException
     *         the payload construction exception
     * @throws InternalSBBException
     */
    InternalOutgoingMessage createMessage(InputStream anXmlStream) throws InternalSBBException;

    /**
     * This method creates the outgoing message from an XML document that is passed in as a String.
     * <p>
     * The XML input is not checked or validated against the service description. Validation, if
     * required, takes place when the message is sent.
     * </p>
     * 
     * @param anXml
     *        The XML document, which is the creation base for the new message, or <code>null</code>
     *        to create an empty message
     * 
     * @return The outgoing message (the XML document).
     * 
     * @throws PayloadConstructionException
     *         the payload construction exception
     * @throws PayloadOversizedException
     *         the payload oversized exception
     * @throws InternalSBBException
     */
    InternalOutgoingMessage createMessage(String anXml) throws InternalSBBException;

    /**
     * 
     * This method creates an outgoing message based on an incoming message. The attachments of the
     * incoming message are included in the outgoing message.
     * <p>
     * This method is used, for example, if a service provider internally calls another service and
     * wants to pass the response of this other service back to the original caller.
     * </p>
     * 
     * @param aMessage
     *        The incoming message, which is the basis for the creation of the outgoing message.
     * @return The outgoing message, which is an exact copy of the message that was passed in.
     * 
     * @throws InternalSBBException
     * @see #createMessageWithoutAttachments(InternalIncomingMessage)
     */
    InternalOutgoingMessage createMessageWithAttachments(InternalIncomingMessage aMessage) throws InternalSBBException;

    /**
     * This method creates the outgoing message based upon the incoming message, but the attachments
     * of the incoming message are not included.
     * <p>
     * The XML input is not checked or validated against the service description. Validation takes
     * place when this message is sent.
     * </p>
     * 
     * @param aMessage
     *        The incoming message, which is the basis for the creation of the outgoing message.
     * @return The outgoing message. This is an exact copy of the XML part of the message passed in,
     *         except that the incoming attachments are not copied.
     * 
     * @throws InternalSBBException
     * @see #createMessageWithAttachments(InternalIncomingMessage)
     */
    InternalOutgoingMessage createMessageWithoutAttachments(InternalIncomingMessage aMessage) throws InternalSBBException;

}
