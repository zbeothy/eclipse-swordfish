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
 * DOM-based incoming message.
 */
public class IncomingDOMMessageImpl extends IncomingMessageBase {

    /** the content of this payload. */
    private Document content;

    /**
     * The Constructor.
     * 
     * @param aMessage
     *        aMessage
     * @param aDocument
     *        content
     */
    IncomingDOMMessageImpl(final NormalizedMessage aMessage, final Document aDocument) {
        super(aMessage);
        this.content = aDocument;
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
        return this.content;
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
        return toString(this.content);
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
        return new OutgoingDOMMessageImpl(this.content);
    }
}
