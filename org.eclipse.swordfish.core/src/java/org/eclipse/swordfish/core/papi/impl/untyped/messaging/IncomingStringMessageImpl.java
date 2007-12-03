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

import java.io.InputStream;
import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.w3c.dom.Document;

/**
 * The Class IncomingStringMessageImpl.
 * 
 */
public class IncomingStringMessageImpl extends IncomingMessageBase {

    /** the XML content as String. */
    private String content;

    /**
     * The Constructor.
     * 
     * @param aMessage
     *        aMessage
     * @param content
     *        content
     */
    IncomingStringMessageImpl(final NormalizedMessage aMessage, final String content) {
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
    public Document getXMLDocument() throws InternalMessagingException {
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
    public InputStream getXMLStream() throws InternalMessagingException {
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
    public String getXMLString() throws InternalMessagingException {
        return this.content;
    }

    // /**
    // * @see org.eclipse.swordfish.papi.untyped.IncomingPayload#getXMLReader()
    // */
    // public XMLEventReader getXMLReader() throws PayloadAccessException {
    // try {
    // StringReader reader = new StringReader(content);
    // return XMLInputFactory.newInstance().createXMLEventReader(reader);
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
        return new OutgoingStringMessageImpl(this.content);
    }
}
