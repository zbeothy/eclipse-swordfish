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
 * The Class IncomingStreamMessageBase.
 */
public abstract class IncomingStreamMessageBase extends IncomingMessageBase {

    /**
     * Instantiates a new incoming stream message base.
     * 
     * @param aNormalizedMessage
     *        the a normalized message
     */
    protected IncomingStreamMessageBase(final NormalizedMessage aNormalizedMessage) {
        super(aNormalizedMessage);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#getXMLDocument()
     */
    public Document getXMLDocument() throws InternalMessagingException {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#getXMLStream()
     */
    public InputStream getXMLStream() throws InternalMessagingException {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessage#getXMLString()
     */
    public String getXMLString() throws InternalMessagingException {
        // Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageBase#newOutgoingMessage()
     */
    @Override
    protected OutgoingMessageBase newOutgoingMessage() throws InternalMessagingException {
        // Auto-generated method stub
        return null;
    }

}
