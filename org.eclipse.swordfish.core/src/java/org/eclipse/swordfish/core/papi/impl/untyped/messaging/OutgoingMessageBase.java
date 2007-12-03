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
package org.eclipse.swordfish.core.papi.impl.untyped.messaging;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.activation.DataHandler;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import org.eclipse.swordfish.core.utils.MessageProperties;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.w3c.dom.DocumentFragment;

/**
 * Abstract base class for outgoing messages.
 */
public abstract class OutgoingMessageBase extends MessageBase implements InternalOutgoingMessage {

    /** value of the SOAP fault code if there is a fault. */
    private String faultCode = "server";

    /** value of the SOAP fault String if there is a fault. */
    private String faultString = "server";

    /** indicates if a message is a fault message. */
    private boolean isFaultMessage = false;

    /** List of attachemnts as InputStreams. */
    private Map attachments = null;

    /** a numbering schema for the attachments when using the deprecated methods. */
    private int attachmentCount = 0;

    /**
     * standard constructor.
     */
    protected OutgoingMessageBase() {
        super();
    }

    /**
     * Adds the attachment.
     * 
     * @param handler
     *        the handler
     * 
     * @return the string
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage#addAttachment(javax.activation.DataHandler)
     */
    public String addAttachment(final DataHandler handler) {
        this.attachmentCount++;
        String name = handler.getName() != null ? handler.getName() : "attachment_" + this.attachmentCount;
        this.addAttachment(name, handler);
        return name;
    }

    /**
     * Adds the attachment.
     * 
     * @param identifier
     *        the identifier
     * @param handler
     *        the handler
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage#addAttachment(java.lang.String,
     *      javax.activation.DataHandler)
     */
    public void addAttachment(final String identifier, final DataHandler handler) {
        if (this.attachments == null) {
            this.attachments = new HashMap();
        }
        // Wozu wird der Identifier spaeter denn benutzt?
        this.attachments.put(identifier, handler);
    }

    /**
     * Attach.
     * 
     * @param inByte
     *        the in byte
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage#attach(byte[])
     */
    public void attach(final byte[] inByte) throws InternalMessagingException {
        this.attach(new ByteArrayInputStream(inByte));
    }

    /**
     * Attach.
     * 
     * @param inStream
     *        the in stream
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOutgoingMessage#attach(java.io.InputStream)
     */
    public void attach(final InputStream inStream) throws InternalMessagingException {
        AttachmentDataSource attachmentDataSource = new AttachmentDataSource(inStream, -1);
        // force this name to be null so it is reassigned
        attachmentDataSource.setName(null);
        this.addAttachment(new DataHandler(attachmentDataSource));
    }

    /**
     * fill a normalized message with the content of this payload.
     * 
     * @param aMessage
     *        the normalized message to be fiffed
     * @param ctx
     *        the ctx
     * 
     * @throws InvalidPayloadException
     */
    public void fillMessage(final NormalizedMessage aMessage, final CallContextExtension ctx) throws InternalIllegalInputException {
        try {
            this.fillMessageContent(aMessage);
            this.fillMessageAttachments(aMessage);
            this.fillMessageHeaders(ctx);
            // lucky who could set the fault code and string previously.
            // Otherwise we will have a generic one
            if (this.isFaultMessage()) {
                if (aMessage instanceof Fault) {
                    aMessage.setProperty(MessageProperties.SOAP_FAULT_CODE, this.getFaultCode());
                    aMessage.setProperty(MessageProperties.SOAP_FAULT_STRING, this.getFaultDescription());
                } else
                    throw new InternalMessagingException("the outgoing message used to indicate a fault message"
                            + ",but the normalized message is not an instance of Fault!");
            }

        } catch (Exception e) {
            throw new InternalIllegalInputException("cannot create the desired payload ", e);
        }
    }

    /**
     * Gets the fault code.
     * 
     * @return Returns the faultCode.
     */
    public String getFaultCode() {
        return this.faultCode;
    }

    /**
     * Gets the fault description.
     * 
     * @return Returns the faultString.
     */
    public String getFaultDescription() {
        return this.faultString;
    }

    /**
     * Checks if is fault message.
     * 
     * @return <code>true</code> if the current message is a fault
     */
    public boolean isFaultMessage() {
        return this.isFaultMessage;
    }

    /**
     * Internal method for attachment transfer.
     * 
     * @param attachments
     *        list of attachments
     * 
     * @throws InternalMessagingException
     */
    public void setAttachments(final Map attachments) throws InternalMessagingException {
        if (this.attachments != null) throw new InternalMessagingException("Attachments cannot be set twice");
        this.attachments = attachments;
    }

    /**
     * Sets the fault code.
     * 
     * @param faultCode
     *        The faultCode to set.
     */
    public void setFaultCode(final String faultCode) {
        if (!this.isFaultMessage()) throw new IllegalStateException("outgoing message is not a fault");
        if (faultCode != null) {
            this.faultCode = faultCode;
        }
    }

    /**
     * Sets the fault description.
     * 
     * @param description
     *        the description
     */
    public void setFaultDescription(final String description) {
        if (!this.isFaultMessage()) throw new IllegalStateException("outgoing message is not a fault");
        if (description != null) {
            this.faultString = description;
        }
    }

    /**
     * Sets the fault message.
     * 
     * @param isFault
     *        indicates whether the present message is to be treated as fault or not
     */
    public void setFaultMessage(final boolean isFault) {
        this.isFaultMessage = isFault;
    }

    /**
     * Adds a header to a message.
     * 
     * @param headerName
     *        fully qualified name of the header
     * @param headerContent
     *        the header element to add
     * 
     * @throws InternalMessagingException
     */
    public void setHeader(final QName headerName, final DocumentFragment headerContent) throws InternalMessagingException {
        String namespace = headerName.getNamespaceURI();
        if (this.isInProtectedNamespace(namespace))
            throw new InternalMessagingException("not allowed to set reseved namespace" + namespace);

        if (this.getHeaderMap().containsKey(headerName)) {
            DocumentFragment theFragment = (DocumentFragment) this.getHeaderMap().get(headerName);
            theFragment.appendChild(headerContent);
        } else {
            this.getHeaderMap().put(headerName, headerContent);
        }
    }

    /**
     * Create a Source representing the content. Since each implemenetation has a different
     * representation of the XML content this must be implemented by subclasses. We will keep the
     * messages in the native format they have initially so we prevent parsing and transformations
     * as long as possible. The only exception is the null message. For this type of message I
     * assume the processing times to be rather little and the memory impact will also be rather
     * small.
     * 
     * @return a Source representing the content
     * 
     * @throws InternalMessagingException
     */
    protected abstract Source createContentSource() throws InternalMessagingException;

    /**
     * Fill the attachements into the given message.
     * 
     * @param aMessage
     *        the message
     * 
     * @throws InternalMessagingException
     *         InternalMessagingException
     */
    private void fillMessageAttachments(final NormalizedMessage aMessage) throws javax.jbi.messaging.MessagingException {
        if (this.attachments == null) return;
        Iterator allAttachments = this.attachments.keySet().iterator();
        int theCounter = 0;
        while (allAttachments.hasNext()) {
            String theAttachmentId = (String) allAttachments.next();
            theCounter++;
            aMessage.addAttachment(theAttachmentId, (DataHandler) this.attachments.get(theAttachmentId));
        }
    }

    /**
     * Fills the message payloads XML into the given message as Source.
     * 
     * @param aMessage
     *        to be filled with content
     * 
     * @throws InternalMessagingException
     *         InternalMessagingException
     * @throws PayloadConstructionException
     */
    private void fillMessageContent(final NormalizedMessage aMessage) throws InternalIllegalInputException {
        try {
            aMessage.setContent(this.createContentSource());
        } catch (javax.jbi.messaging.MessagingException e) {
            throw new InternalIllegalInputException(e);
        } catch (InternalMessagingException e) {
            throw new InternalIllegalInputException(e);
        }
    }

    /**
     * puts the headers into the normalized message headers properties.
     * 
     * @param ctx
     *        the ctx
     */
    private void fillMessageHeaders(final CallContextExtension ctx) {
        Iterator iter = this.getHeaderMap().keySet().iterator();
        while (iter.hasNext()) {
            QName name = (QName) iter.next();
            DocumentFragment value = (DocumentFragment) this.getHeaderMap().get(name);
            ctx.addUserMessagingHeader(name, value);
        }
    }

}
