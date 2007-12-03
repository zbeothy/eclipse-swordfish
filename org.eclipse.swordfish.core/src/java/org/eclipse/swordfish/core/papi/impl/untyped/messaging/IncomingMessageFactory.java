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

import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * internal factory for incoming messages.
 */
public final class IncomingMessageFactory extends MessageBase {

    /**
     * Factory method for creation of an IncomingPayload based on a received normalized message.
     * 
     * @param aMessage
     *        the recieved normalized message
     * 
     * @return an Incoming Payload representing the content of the normalized Message
     */
    public static IncomingMessageBase createIncomingMessage(final NormalizedMessage aMessage) {
        if (aMessage == null) throw new IllegalArgumentException("Can't handle null normalized messages.");
        Source theSource = aMessage.getContent();
        if (theSource == null) throw new IllegalArgumentException("Can't handle incoming messages without content.");
        if (theSource instanceof DOMSource) {
            IncomingMessageBase dom = createDOMBased(aMessage, (DOMSource) theSource);
            return dom;
        }
        if (theSource instanceof SAXSource) {
            IncomingStreamMessageBase sax = createSAXBased(aMessage, (SAXSource) theSource);
            return sax;
        }
        if (theSource instanceof StreamSource) {
            IncomingStreamMessageBase stream = createStreamBased(aMessage, (StreamSource) theSource);
            return stream;
        }
        throw new IllegalArgumentException("Can't handle incoming messages with content of type " + theSource.getClass().getName());
    }

    /**
     * Creates the DOM based.
     * 
     * @param aMessage
     *        the message
     * @param aSource
     *        the source object
     * 
     * @return an IncomingPayload
     */
    private static IncomingMessageBase createDOMBased(final NormalizedMessage aMessage, final DOMSource aSource) {
        Node theNode = aSource.getNode();
        if (theNode == null) throw new NullPointerException("Node of the incoming DOM may not be null");
        if (theNode instanceof Document)
            return new IncomingDOMMessageImpl(aMessage, (Document) theNode);
        /*
         * }
         * 
         * if (theNode instanceof Element) { try { String theXML = toString(theNode); return new
         * IncomingStringMessageImpl(aMessage, theXML); } catch (PayloadAccessException e) {
         * IllegalArgumentException iaex = new IllegalArgumentException( "content conversion
         * failed"); iaex.initCause(e); throw iaex; }
         */
        else
            throw new IllegalArgumentException("Can't handle incoming messages with content  nodeof type "
                    + theNode.getClass().getName());

    }

    /**
     * Creates the SAX based.
     * 
     * @param aMessage
     *        the message
     * @param aSource
     *        the source object
     * 
     * @return an IncomingPayload
     */
    private static IncomingStreamMessageBase createSAXBased(final NormalizedMessage aMessage, final SAXSource aSource) {
        InputSource theSource = aSource.getInputSource();
        if (theSource == null) throw new NullPointerException("Can't handle incoming SAX messages without content.");
        if (theSource.getCharacterStream() != null) return new IncomingReaderMessageImpl(aMessage, theSource.getCharacterStream());
        if (theSource.getByteStream() != null)
            return new IncomingStreamMessageImpl(aMessage, theSource.getByteStream(), theSource.getEncoding());
        throw new IllegalArgumentException("Can't handle incoming SAX messages without stream content.");
    }

    /**
     * Creates the stream based.
     * 
     * @param aMessage
     *        the message
     * @param aSource
     *        the source object
     * 
     * @return an IncomingPayload
     */
    private static IncomingStreamMessageBase createStreamBased(final NormalizedMessage aMessage, final StreamSource aSource) {
        if (aSource.getReader() != null) return new IncomingReaderMessageImpl(aMessage, aSource.getReader());
        if (aSource.getInputStream() != null) return new IncomingStreamMessageImpl(aMessage, aSource.getInputStream());
        throw new IllegalArgumentException("Can't handle incoming stream messages without stream content.");
    }

    /**
     * hidden constructor.
     */
    private IncomingMessageFactory() {
        super();
    }
}
