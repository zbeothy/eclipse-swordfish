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
package org.eclipse.swordfish.core.components.helpers;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.w3c.dom.DocumentFragment;

/**
 * The Interface InternalHeaderSupport.
 */
public interface HeaderSupport {

    /** this interfaces role. */
    String ROLE = HeaderSupport.class.getName();

    /**
     * Gets the header.
     * 
     * @param message
     *        the message
     * @param headerName
     *        the header name
     * 
     * @return the header
     */
    DocumentFragment getHeader(InternalIncomingMessage message, QName headerName);

    /**
     * List header names.
     * 
     * @param message
     *        the message
     * 
     * @return the q name[]
     */
    QName[] listHeaderNames(InternalIncomingMessage message);

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
    void setHeader(InternalOutgoingMessage message, QName headerName, DocumentFragment headerContent)
            throws InternalInfrastructureException, InternalIllegalInputException;
}
