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
import java.util.Iterator;
import java.util.Set;
import javax.activation.DataHandler;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.w3c.dom.Document;

/**
 * This interface contains the business-related contents (payload) of an incoming message. An
 * incoming message can be a request (for a service provider) or a response or notification (for a
 * service consumer).
 * <p>
 * An incoming message encapsulates the message payload and provides access to a call context (which
 * enables access to message metadata).
 * <p>
 * The payload is XML and can be accessed in several ways. In addition, the payload can hold
 * multiple binary attachments such as images.
 * </p>
 * <p>
 * The XML payload can be accessed in different representations, which are
 * <ul>
 * <li>String
 * <li>DOM</li>
 * <li>Stream (a Java InputStream)</li>
 * </ul>
 * </p>
 * <p>
 * The best practices to process an InternalIncomingMessage are:
 * <ol>
 * <li>check whether the message is passed as a stream using {@link #isStreamBased()}</li>
 * <li>process the message accordingly using the suitable getXML... method</li>
 * </ol>
 * Potential message attachments should be processed analogously based on the suitable attachment
 * processing methods from this package.
 * </p>
 * <p>
 * Note: The message access methods are provided independent of the internal representation of the
 * payload.
 * </p>
 * <p>
 * IMPORTANT: Whenever a message payload is based on a streamed representation of the included
 * content, this content may be only be accessed at most once.<br>
 * Attachments can only ever be accessed at most once, regardless of whether the payload is stream
 * based or not.
 * </p>
 * <p>
 * CAUTION: Payload content in streamed representation may become very large when extracted as DOM
 * or String. If it becomes too large, processing the message may result in problems.
 * </p>
 * 
 */
public interface InternalIncomingMessage {

    /**
     * This method returns the attachment that was attached to this message using the identifier.
     * The attachment is returned in form of a activation DataHandler so the data can be extracted
     * using the well known Java methods.
     * 
     * This method may not be used, if this message is stream based.
     * 
     * @param identifier
     *        the identifier that was used to attach the data
     * @return <code>DataHandler</code> containing the attachment
     * @throws InternalMessagingException
     * 
     * @throws InternalSBBException
     */
    DataHandler getAttachment(String identifier) throws InternalSBBException;

    /**
     * This method returns the number of binary attachments contained in the payload. This number is
     * always the same regardless of the number of the attachments that have already been accessed
     * by calling {@link #nextAttachment()}.
     * 
     * @return The number of binary attachments contained in the payload.
     * @throws InternalMessagingException
     * 
     * @throws InternalSBBException
     */
    int getAttachmentCount() throws InternalSBBException;

    /**
     * This method returns a Set of Strings that contain the identifiers of the attachments
     * accessible on this message. If this set is empty then this message has no attachments.
     * 
     * This method may not be used, if this message is stream based.
     * 
     * @return <code>Set</code> of Strings which are the identifier of accessible attachments.
     * 
     * @throws InternalSBBException
     * 
     */
    Set getAttachmentIdentifiers();

    /**
     * Returns an iterator on all attchements of this message. The Iterator will hold instances of
     * <code>Map.Entry</code>. Each entry will hold a <code>String</code> representing the
     * identifier of the attachment in field key and a <code>DataHandler</code> representing the
     * attachement in the field value.
     * 
     * For stream based messages only this method enables access to attachments. It may be possible,
     * that reading data from an included <code>DataHandler</code> is only allowed, when the next
     * item of the iterator has not been requested.
     * 
     * For stream based messages this method may only be called once, because using two different
     * iterators on the same stream will produce unpredictable results.
     * 
     * @return an Iterator(Map.Entry(String, DataHandler))
     * @throws InternalMessagingException
     * 
     * @throws InternalSBBException
     * 
     */
    Iterator getAttachments() throws InternalSBBException;

    /**
     * This method returns the context of the call this message is associated with.
     * 
     * @return The <code>InternalCallContext</code> this message is associated with.
     */
    InternalCallContext getCallContext();

    /**
     * This method gets the payload content as a DOM tree {@link org.w3c.dom.Document}.
     * <p>
     * This method must not be called more than once in a streamed payload and it must not be called
     * once another payload extraction method has been called already.
     * </p>
     * <p>
     * 
     * @return The payload DOM.
     * @throws InternalSBBException
     */
    Document getXMLDocument() throws InternalSBBException;

    /**
     * This method gets the message payload as a {@link java.io.InputStream} of the bytes in the XML
     * String.
     * <p>
     * The encoding is UTF-8 by default. Other encodings are permitted, but in these cases the XML
     * must be a complete XML document and must declare its encoding according to XML standards.
     * </p>
     * <p>
     * If the payload is stream-based (as indicated by {@link #isStreamBased()} returning
     * <code>true</code>), the method must not be called if another payload extraction method has
     * already been called.
     * </p>
     * <p>
     * This method may be called repeatedly. For a streamed payload, ({@link #isStreamBased()}
     * returns <code>true</code>), each call returns the same stream object. Otherwise this
     * method can return either the same object or different stream objects.
     * </p>
     * 
     * @return A stream that can be used to retrieve the XML.
     * 
     * @throws InternalSBBException
     */
    InputStream getXMLStream() throws InternalSBBException;

    /**
     * This method gets the payload content as an XML {@link java.lang.String}.
     * <p>
     * In a streamed payload this method must not be called more than once, and it must not be
     * called once another payload extraction method has been called already.
     * </p>
     * 
     * @return The extracted String.
     * 
     * @throws InternalSBBException
     * 
     * @see #isStreamBased()
     */
    String getXMLString() throws InternalSBBException;

    /**
     * This method returns an iterator-style check for attachments which have yet to be retrieved.
     * 
     * @return <code>true</code> if there are still attachments which have not yet been retrieved.
     * @throws InternalMessagingException
     * @throws InternalSBBException
     * @deprecated This is the old style API which is only avaiable not to break compatibility. This
     *             part of the API cannot be mixed with usage of the new API based on DataHandlers
     *             describing Attachements.
     * 
     * The implementation is based on
     * @link{#getAttachements()} so the same restrictions apply as if using this method.
     * 
     * @see #getAttachments()
     */
    @Deprecated
    boolean hasNextAttachment() throws InternalSBBException;

    /**
     * This method checks if the present message holds a <code>fault</code>, for example, as a
     * result of a business <code>fault</code> sent by the service provider.
     * 
     * @return <code>True</code> if the present message is product of a <code>fault</code>. and
     *         <code>false</code> if the message is a "good" response.
     */
    boolean isFaultMessage();

    /**
     * This method checks for streamed payload content. Whenever streamed payload is detected for a
     * certain message, the method returns <code>true</code>. In such cases one should access the
     * payload as a stream only to avoid a potential {@link PayloadOversizedException}.
     * <p>
     * Note: Content can be extracted at most once and can become excessively large especially if it
     * is streamed.
     * </p>
     * 
     * @return <code>True</code> if the content is streamed.
     * 
     * @see InternalIncomingMessage
     */
    boolean isStreamBased();

    /**
     * This method returns a stream to extract the next attachment.
     * 
     * Each attachment can be retrieved only once.
     * 
     * @return The input stream to the attachment
     * @throws InternalMessagingException
     * @throws InternalSBBException
     * @deprecated This is the old style API which is only avaiable not to break compatibility. This
     *             part of the API cannot be mixed with usage of the new API based on DataHandlers
     *             describing Attachements.
     * 
     * The implementation is based on
     * @link{#getAttachements()} so the same restrictions apply as if using this method.
     * 
     * @see #getAttachments()
     */
    @Deprecated
    InputStream nextAttachment() throws InternalSBBException;

    /**
     * This method extracts and returns all the bytes of the next attachment as a
     * <code>byte[]</code>.
     * 
     * Each attachment can be retrieved only once.
     * 
     * @return The attachment as a byte array.
     * @throws InternalMessagingException
     * 
     * @throws InternalSBBException
     * 
     * @deprecated This is the old style API which is only avaiable not to break compatibility. This
     *             part of the API cannot be mixed with usage of the new API based on DataHandlers
     *             describing Attachements.
     * 
     * The implementation is based on
     * @link{#getAttachements()} so the same restrictions apply as if using this method.
     * 
     * @see #getAttachments()
     */
    @Deprecated
    byte[] nextAttachmentsBytes() throws InternalSBBException;

}
