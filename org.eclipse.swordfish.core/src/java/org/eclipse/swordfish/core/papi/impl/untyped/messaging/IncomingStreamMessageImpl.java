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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.w3c.dom.Document;

/**
 * The Class IncomingStreamMessageImpl.
 * 
 */
public class IncomingStreamMessageImpl extends IncomingStreamMessageBase {

    /** the stream. */
    private InputStream content;

    /** an encoding. */
    private String encoding = null;

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
    IncomingStreamMessageImpl(final NormalizedMessage aMessage, final InputStream content) {
        super(aMessage);
        this.content = content;
    }

    /**
     * The Constructor.
     * 
     * @param aMessage
     *        aMessage
     * @param content
     *        content
     * @param anEncoding
     *        encoding
     */
    IncomingStreamMessageImpl(final NormalizedMessage aMessage, final InputStream content, final String anEncoding) {
        super(aMessage);
        this.content = content;
        if ((anEncoding != null) && Charset.isSupported(anEncoding)) {
            this.encoding = anEncoding;
        }
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
        if (this.encoding != null)
            return toDOM(this.getReader());
        else
            return toDOM(this.getContent());
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
        return this.getContent();
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
        if (this.encoding != null)
            return toString(this.getReader());
        else
            return toString(this.getContent());
    }

    /**
     * Checks if is stream based.
     * 
     * @return true, if is stream based
     * 
     * @see org.eclipse.swordfish.papi.untyped.IncomingPayload#isStreamBased()
     */
    @Override
    public boolean isStreamBased() {
        return true;
    }

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
        } catch (InternalMessagingException e) {
            throw new InternalMessagingException(
                    "InternalIncomingMessage cannot be read again for filling derived InternalOutgoingMessage", e);
        }
        if (null == this.encoding)
            return new OutgoingStreamMessageImpl(this.content);
        else {
            try {
                return new OutgoingReaderMessageImpl(new InputStreamReader(this.content, this.encoding));
            } catch (UnsupportedEncodingException e) {
                // this case should never happen, as the encoding as been
                // previously tested
                throw new RuntimeException("Irregularly unsupported encoding");
            }
        }
    }

    // /**
    // * @see org.eclipse.swordfish.papi.untyped.IncomingPayload#getXMLReader()
    // */
    // public XMLEventReader getXMLReader() throws PayloadAccessException {
    // setAccessed();
    // try {
    // if (encoding != null) {
    // return XMLInputFactory.newInstance().createXMLEventReader(getReader());
    // } else {
    // return XMLInputFactory.newInstance().createXMLEventReader(content);
    // }
    // } catch (XMLStreamException e) {
    // throw new PayloadAccessException(e);
    // } catch (FactoryConfigurationError e) {
    // throw new PayloadAccessException(e);
    // }
    // }

    /**
     * mark as accessed and check for access only once.
     * 
     * @throws InternalMessagingException
     */
    protected void setAccessed() throws InternalMessagingException {
        if (this.accessed && !(this.content instanceof ByteArrayInputStream))
            throw new InternalMessagingException("Content must not be accessed more than once.");
        this.accessed = true;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     * 
     * @throws PayloadAccessException
     */
    private InputStream getContent() throws InternalMessagingException {
        if (this.content instanceof ByteArrayInputStream) {
            try {
                this.content.reset();
            } catch (IOException e) {
                throw new InternalMessagingException("Could not reset ByteArrayInputStream ", e);
            }
        }
        return this.content;
    }

    /**
     * create a reader based on content stream and encoding. may only be called if encoding is set
     * and supported.
     * 
     * @return a reader
     * 
     * @throws PayloadAccessException
     *         on problems with the content
     */
    private Reader getReader() throws InternalMessagingException {
        try {
            return new InputStreamReader(this.getContent(), this.encoding);
        } catch (UnsupportedEncodingException e) {
            throw new InternalMessagingException("Unsupported encoding", e);
        }
    }

}
