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
package org.eclipse.swordfish.core.papi.impl.untyped;

import java.io.InputStream;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingDOMMessageImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingStreamMessageImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingStringMessageImpl;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalMessageFactory;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.w3c.dom.Document;

/**
 * Message factory.
 */
public class MessageFactoryImpl implements InternalMessageFactory {

    /**
     * Standard constructor.
     */
    public MessageFactoryImpl() {
        super();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aDocument
     *        the a document
     * 
     * @return the outgoing message
     * 
     * @throws PayloadOversizedException
     * @throws PayloadConstructionException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createFault(org.w3c.dom.Document)
     */
    public InternalOutgoingMessage createFault(final Document aDocument) throws InternalInfrastructureException,
            InternalIllegalInputException {
        OutgoingMessageBase result = (OutgoingMessageBase) this.createMessage(aDocument);
        result.setFaultMessage(true);
        return result;
    }

    /**
     * (non-Javadoc).
     * 
     * @param anXmlStream
     *        the an xml stream
     * 
     * @return the outgoing message
     * 
     * @throws PayloadConstructionException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createFault(java.io.InputStream)
     */
    public InternalOutgoingMessage createFault(final InputStream anXmlStream) throws InternalInfrastructureException,
            InternalIllegalInputException {
        OutgoingMessageBase result = (OutgoingMessageBase) this.createMessage(anXmlStream);
        result.setFaultMessage(true);
        return result;
    }

    /**
     * (non-Javadoc).
     * 
     * @param anXml
     *        the an xml
     * 
     * @return the outgoing message
     * 
     * @throws PayloadOversizedException
     * @throws PayloadConstructionException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createFault(java.lang.String)
     */
    public InternalOutgoingMessage createFault(final String anXml) throws InternalInfrastructureException,
            InternalIllegalInputException {
        OutgoingMessageBase result = (OutgoingMessageBase) this.createMessage(anXml);
        result.setFaultMessage(true);
        return result;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aMessage
     *        the a message
     * 
     * @return the outgoing message
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createFaultWithAttachments(org.eclipse.swordfish.papi.untyped.InternalIncomingMessage)
     */
    public InternalOutgoingMessage createFaultWithAttachments(final InternalIncomingMessage aMessage) throws InternalSBBException {
        OutgoingMessageBase result = (OutgoingMessageBase) this.createMessageWithAttachments(aMessage);
        result.setFaultMessage(true);
        return result;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aMessage
     *        the a message
     * 
     * @return the outgoing message
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createFaultWithoutAttachments(org.eclipse.swordfish.papi.untyped.InternalIncomingMessage)
     */
    public InternalOutgoingMessage createFaultWithoutAttachments(final InternalIncomingMessage aMessage)
            throws InternalSBBException {
        OutgoingMessageBase result = (OutgoingMessageBase) this.createMessageWithoutAttachments(aMessage);
        result.setFaultMessage(true);
        return result;
    }

    /**
     * (non-Javadoc).
     * 
     * @param aDocument
     *        the a document
     * 
     * @return the outgoing message
     * 
     * @throws PayloadOversizedException
     * @throws PayloadConstructionException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createMessage(org.w3c.dom.Document)
     */
    public InternalOutgoingMessage createMessage(final Document aDocument) throws InternalInfrastructureException,
            InternalIllegalInputException {
        return new OutgoingDOMMessageImpl(aDocument);
    }

    /**
     * (non-Javadoc).
     * 
     * @param anXmlStream
     *        the an xml stream
     * 
     * @return the outgoing message
     * 
     * @throws PayloadConstructionException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createMessage(java.io.InputStream)
     */
    public InternalOutgoingMessage createMessage(final InputStream anXmlStream) throws InternalInfrastructureException,
            InternalIllegalInputException {
        return new OutgoingStreamMessageImpl(anXmlStream);
    }

    /**
     * (non-Javadoc).
     * 
     * @param anXml
     *        the an xml
     * 
     * @return the outgoing message
     * 
     * @throws PayloadOversizedException
     * @throws PayloadConstructionException
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createMessage(java.lang.String)
     */
    public InternalOutgoingMessage createMessage(final String anXml) throws InternalIllegalInputException,
            InternalInfrastructureException {
        return new OutgoingStringMessageImpl(anXml);
    }

    /**
     * (non-Javadoc).
     * 
     * @param aMessage
     *        the a message
     * 
     * @return the outgoing message
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createMessageWithAttachments(org.eclipse.swordfish.papi.untyped.InternalIncomingMessage)
     */
    // FIXME don't translate the payloadException.
    // FIXMEthrow IllegalArgument insteaof UnsupportedOperationException
    public InternalOutgoingMessage createMessageWithAttachments(final InternalIncomingMessage aMessage) throws InternalSBBException {
        if (aMessage instanceof IncomingMessageBase) {
            try {
                return ((IncomingMessageBase) aMessage).asOutgoingMessage(true);
            } catch (InternalMessagingException e) {
                throw new InternalIllegalInputException(e);
            }
        }
        throw new UnsupportedOperationException();
    }

    /**
     * (non-Javadoc).
     * 
     * @param aMessage
     *        the a message
     * 
     * @return the outgoing message
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalMessageFactory#createMessageWithoutAttachments(org.eclipse.swordfish.papi.untyped.InternalIncomingMessage)
     */
    public InternalOutgoingMessage createMessageWithoutAttachments(final InternalIncomingMessage aMessage)
            throws InternalSBBException {
        if (aMessage instanceof IncomingMessageBase) {
            try {
                return ((IncomingMessageBase) aMessage).asOutgoingMessage(false);
            } catch (InternalMessagingException e) {
                throw new InternalIllegalInputException(e);
            }
        }
        throw new UnsupportedOperationException();
    }

}
