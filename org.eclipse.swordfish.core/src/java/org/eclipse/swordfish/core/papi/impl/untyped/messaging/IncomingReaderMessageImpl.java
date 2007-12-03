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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.w3c.dom.Document;

/**
 * The Class IncomingReaderMessageImpl.
 * 
 */
public class IncomingReaderMessageImpl extends IncomingStreamMessageBase {

    /** the reader containing the payload. */
    private Reader content;

    /** Comment for <code>isAccessed</code>. */
    private boolean accessed = false;

    /**
     * The Constructor.
     * 
     * @param aMessage
     *        the message
     * @param content
     *        the content
     */
    IncomingReaderMessageImpl(final NormalizedMessage aMessage, final Reader content) {
        super(aMessage);
        this.content = content;
    }

    /**
     * Gets the XML document.
     * 
     * @return the XML document
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.IncomingPayload#getXMLDocument()
     */
    @Override
    public Document getXMLDocument() throws InternalMessagingException {
        this.setAccessed();
        return toDOM(this.content);
    }

    /**
     * Gets the XML stream.
     * 
     * @return the XML stream
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.IncomingPayload#getXMLStream()
     */
    @Override
    public InputStream getXMLStream() throws InternalMessagingException {
        this.setAccessed();
        return toInputStream(this.content);
    }

    /**
     * Gets the XML string.
     * 
     * @return the XML string
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.papi.untyped.IncomingPayload#getXMLString()
     */
    @Override
    public String getXMLString() throws InternalMessagingException {
        this.setAccessed();
        return toString(this.content);
    }

    // /**
    // * @see org.eclipse.swordfish.papi.untyped.IncomingPayload#getXMLReader()
    // */
    // public XMLEventReader getXMLReader() throws PayloadAccessException {
    // setAccessed();
    // try {
    // return XMLInputFactory.newInstance().createXMLEventReader(content);
    // } catch (XMLStreamException e) {
    // throw new PayloadAccessException(e);
    // } catch (FactoryConfigurationError e) {
    // throw new PayloadAccessException(e);
    // }
    // }

    /**
     * (non-Javadoc).
     * 
     * @return the outgoing message base
     * 
     * @throws InternalMessagingException
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageBase#newOutgoingMessage()
     */
    @Override
    protected OutgoingMessageBase newOutgoingMessage() throws InternalMessagingException {
        try {
            this.setAccessed();
            return new OutgoingReaderMessageImpl(this.getContent());
        } catch (InternalMessagingException e) {
            throw new InternalMessagingException(
                    "InternalIncomingMessage cannot be read again for filling derived InternalOutgoingMessage", e);
        }
    }

    /**
     * Sets the accessed.
     * 
     * @throws InternalMessagingException
     */
    protected void setAccessed() throws InternalMessagingException {
        if (this.accessed && !(this.content instanceof StringReader))
            throw new InternalMessagingException("Content must not be accessed more than once.");
        this.accessed = true;
    }

    private Reader getContent() throws InternalMessagingException {
        if (this.content instanceof StringReader) {
            try {
                this.content.reset();
            } catch (IOException e) {
                throw new InternalMessagingException("Could not reset StringReader ", e);
            }
        }
        return this.content;
    }
}
