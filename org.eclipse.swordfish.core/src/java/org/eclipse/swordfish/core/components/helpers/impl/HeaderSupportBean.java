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
package org.eclipse.swordfish.core.components.helpers.impl;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageBase;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.OutgoingMessageBase;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalMessagingException;
import org.eclipse.swordfish.papi.internal.extension.advanced.InternalHeaderSupport;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.w3c.dom.DocumentFragment;

/**
 * The Class HeaderSupportBean.
 */
public class HeaderSupportBean implements InternalHeaderSupport {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: HeaderSupportBean.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    // ------------------------------------------------------------- Properties

    /**
     * Gets the header.
     * 
     * @param message
     *        the message
     * @param headerName
     *        the header name
     * 
     * @return the headerr
     */
    public DocumentFragment getHeader(final InternalIncomingMessage message, final QName headerName) {
        if (message instanceof IncomingMessageBase)
            return ((IncomingMessageBase) message).getHeader(headerName);
        else
            return null;
    }

    /**
     * List header names.
     * 
     * @param message
     *        the message
     * 
     * @return the q name[]
     */
    public QName[] listHeaderNames(final InternalIncomingMessage message) {
        if (message instanceof IncomingMessageBase)
            return ((IncomingMessageBase) message).listHeaderNames();
        else
            return null;
    }

    /**
     * Sets the header.
     * 
     * @param message
     *        the message
     * @param headerName
     *        the header name
     * @param headerContent
     *        the header content
     * 
     * @throws ParticipantHandlingException
     */
    public void setHeader(final InternalOutgoingMessage message, final QName headerName, final DocumentFragment headerContent)
            throws InternalInfrastructureException, InternalIllegalInputException {
        if (message instanceof OutgoingMessageBase) {
            try {
                ((OutgoingMessageBase) message).setHeader(headerName, headerContent);
            } catch (InternalMessagingException e) {
                throw new InternalIllegalInputException(e);
            }
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Sets the supported must understand headers.
     * 
     * @param operation
     *        the operation
     * @param headers
     *        the headers
     */
    public void setSupportedMustUnderstandHeaders(final InternalOperation operation, final QName[] headers) {
        if (operation instanceof AbstractOperation) {
            AbstractOperation op = (AbstractOperation) operation;
            op.setSupportedMustUnderstandHeaders(headers);
        }
    }
}
