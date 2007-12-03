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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.activation.DataHandler;
import javax.jbi.messaging.NormalizedMessage;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.utils.HeaderUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.w3c.dom.DocumentFragment;

/**
 * Abstract base class for incoming messages.
 */
public abstract class IncomingMessageBase extends MessageBase implements InternalIncomingMessage {

    /** TODO AMA revisit after clarifying fault handling in InternalSBB-impl. */
    private boolean isFaultMessage = false;

    /** Map of attachemnts as InputStreams. */
    private Map attachments = null;

    /** PAPI call context. */
    private InternalCallContext internalCallContext = null;

    /**
     * determines if any attachment of this object has been potentially accessed by the PAPI user
     * TODO we may define this value to become true when teh user really access the content of the
     * attachment. Currently this turns true when the user gets the attachment without accessing its
     * content.
     */
    private boolean attachmentAccessed;

    /** index of the next attachment that is valid for access. */
    private Iterator attachmentIterator;

    /**
     * create an incoming message from a NormalizedMessage.
     * 
     * @param aNormalizedMessage
     *        the message from the inner interface
     */
    protected IncomingMessageBase(final NormalizedMessage aNormalizedMessage) {
        super();
        this.attachmentAccessed = false;
        this.extractHeaders(aNormalizedMessage);
        this.extractAttachments(aNormalizedMessage);
        this.attachmentIterator = this.getAttachmentIdentifiers().iterator();
    }

    /**
     * As outgoing message.
     * 
     * @param copyAttachments
     *        declares if attachment are to be copied
     * 
     * @return new InternalOutgoingMessage
     * 
     * @throws InternalMessagingException
     */
    public OutgoingMessageBase asOutgoingMessage(final boolean copyAttachments) throws InternalSBBException {

        if (copyAttachments && this.attachmentAccessed) throw new InternalMessagingException("Attachments already extracted");
        OutgoingMessageBase result = this.newOutgoingMessage();
        if (copyAttachments) {
            result.setAttachments(this.attachments);
        }
        result.setFaultMessage(this.isFaultMessage);
        return result;
    }

    /**
     * Gets the attachment.
     * 
     * @param identifier
     *        the identifier
     * 
     * @return the attachment
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#getAttachment(java.lang.String)
     */
    public DataHandler getAttachment(final String identifier) throws InternalMessagingException {
        if (this.attachments.get(identifier) == null)
            throw new InternalMessagingException("An Attachment with identifier " + identifier
                    + " does not exist or is not accessible.");
        else {
            this.attachmentAccessed = true;
            return (DataHandler) this.attachments.get(identifier);
        }
    }

    /**
     * Gets the attachment count.
     * 
     * @return the attachment count
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#attachmentCount()
     */
    public int getAttachmentCount() throws InternalMessagingException {
        return this.attachments.size();
    }

    /**
     * Gets the attachment identifiers.
     * 
     * @return the attachment identifiers
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#getAttachmentIdentifiers()
     */
    public Set getAttachmentIdentifiers() {
        return this.attachments.keySet();
    }

    /**
     * Gets the attachments.
     * 
     * @return the attachments
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#getAttachments()
     */
    public Iterator getAttachments() throws InternalMessagingException {
        return this.attachments.entrySet().iterator();
    }

    /**
     * (non-Javadoc).
     * 
     * @return the call context
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#getCallContext()
     */
    public InternalCallContext getCallContext() {
        return this.internalCallContext;
    }

    /**
     * reads a header from given message.
     * 
     * @param headerName
     *        the fully namespace-qualified of the header element
     * 
     * @return the header as a W3C DOM Element or null if no such header exists
     */
    public DocumentFragment getHeader(final QName headerName) {
        String namespace = headerName.getNamespaceURI();
        if (this.isInProtectedNamespace(namespace))
            return null;
        else
            return (DocumentFragment) this.getHeaderMap().get(headerName);
    }

    /**
     * Checks for next attachment.
     * 
     * @return true, if has next attachment
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#hasNextAttachment()
     */
    public boolean hasNextAttachment() throws InternalMessagingException {
        return this.attachmentIterator.hasNext();
    }

    /**
     * (non-Javadoc).
     * 
     * @return true, if is fault message
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#isFaultMessage()
     */
    public boolean isFaultMessage() {
        return this.isFaultMessage;
    }

    /**
     * Checks if is stream based.
     * 
     * @return true, if is stream based
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#isStreamBased()
     */
    public boolean isStreamBased() {
        // default - overridden in stream-based messages
        return false;
    }

    /**
     * lists the headers available in the given message.
     * 
     * @return an array of fully qualified headers names
     */
    public QName[] listHeaderNames() {
        Iterator keyIterator = this.getHeaderMap().keySet().iterator();
        Set returnSet = new HashSet();
        while (keyIterator.hasNext()) {
            returnSet.add(keyIterator.next());
        }
        return (QName[]) returnSet.toArray(new QName[0]);
    }

    /**
     * Next attachment.
     * 
     * @return the input stream
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#nextAttachment()
     */
    public InputStream nextAttachment() throws InternalMessagingException {
        try {
            return this.getAttachment((String) this.attachmentIterator.next()).getInputStream();
        } catch (IOException e) {
            throw new InternalMessagingException(e);
        } catch (NoSuchElementException e) {
            throw new InternalMessagingException("There is no more attachment, ", e);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @return the byte[]
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#nextAttachmentsBytes()
     */
    public byte[] nextAttachmentsBytes() throws InternalMessagingException {
        InputStream theInput = this.nextAttachment();
        ByteArrayOutputStream theOutput = new ByteArrayOutputStream();
        byte[] theBuffer = new byte[COPY_BUFFER_SIZE];
        try {
            for (int theReadCount = theInput.read(theBuffer); theReadCount > 0; theReadCount = theInput.read(theBuffer)) {
                theOutput.write(theBuffer, 0, theReadCount);
            }
        } catch (IOException e) {
            // TODO fix error handling, localize error message
            throw new InternalMessagingException("error while converting attachement from stream to byte array", e);
        }
        return theOutput.toByteArray();
    }

    /**
     * Sets the call context.
     * 
     * @param aCallContext
     *        call context to be set
     */
    public void setCallContext(final InternalCallContext aCallContext) {
        this.internalCallContext = aCallContext;
    }

    /*
     * private void fillMessageHeaders(NormalizedMessage message) { if (!getHeaderMap().isEmpty()) {
     * message.setProperty(HeaderUtil.HEADER_PROPERTY, getHeaderMap()); }
     */

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
     * New outgoing message.
     * 
     * @return new outgoing message
     * 
     * @throws InternalMessagingException
     */
    protected abstract OutgoingMessageBase newOutgoingMessage() throws InternalMessagingException;

    /**
     * Extract attachments.
     * 
     * @param normalizedMessage
     *        the normalized message
     */
    private void extractAttachments(final NormalizedMessage normalizedMessage) {
        this.attachments = new HashMap();
        Iterator iter = normalizedMessage.getAttachmentNames().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            this.attachments.put(name, normalizedMessage.getAttachment(name));
        }
    }

    /**
     * extract the headers from given message.
     * 
     * @param aMessage
     *        amessage
     */
    private void extractHeaders(final NormalizedMessage aMessage) {
        Map jbiHeaders = (Map) aMessage.getProperty(HeaderUtil.HEADER_PROPERTY);
        if (jbiHeaders != null) {
            Iterator keyIterator = jbiHeaders.keySet().iterator();
            while (keyIterator.hasNext()) {
                String name = (String) keyIterator.next();
                if (!this.isInProtectedNamespace(name)) {
                    this.getHeaderMap().put(QName.valueOf(name), jbiHeaders.get(name));
                }
            }
        }
    }

}
