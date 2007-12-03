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
import org.eclipse.swordfish.core.components.helpers.impl.HeaderSupportBean;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;
import org.w3c.dom.DocumentFragment;

/**
 * This proxy class is necessary so that the internal users of InternalHeaderSupport do not depend
 * on the presence of the papi extension jars at runtime.
 * 
 */
public class HeaderSupportProxy implements HeaderSupport {

    /** The wrapped. */
    private HeaderSupportBean wrapped;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.advanced.InternalHeaderSupport#getHeader(org.eclipse.swordfish.papi.untyped.IncomingMessage,
     *      javax.xml.namespace.QName)
     */
    public DocumentFragment getHeader(final InternalIncomingMessage message, final QName headerName) {
        return this.wrapped.getHeader(message, headerName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.advanced.InternalHeaderSupport#listHeaderNames(org.eclipse.swordfish.papi.untyped.IncomingMessage)
     */
    public QName[] listHeaderNames(final InternalIncomingMessage message) {
        return this.wrapped.listHeaderNames(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.advanced.InternalHeaderSupport#setHeader(org.eclipse.swordfish.papi.untyped.OutgoingMessage,
     *      javax.xml.namespace.QName, org.w3c.dom.DocumentFragment)
     */
    public void setHeader(final InternalOutgoingMessage message, final QName headerName, final DocumentFragment headerContent)
            throws InternalInfrastructureException, InternalIllegalInputException {
        this.wrapped.setHeader(message, headerName, headerContent);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.advanced.InternalHeaderSupport#setSupportedMustUnderstandHeaders(org.eclipse.swordfish.papi.untyped.Operation,
     *      javax.xml.namespace.QName[])
     */
    public void setSupportedMustUnderstandHeaders(final InternalOperation operation, final QName[] headers) {
        this.wrapped.setSupportedMustUnderstandHeaders(operation, headers);
    }

    /**
     * Sets the wrapped.
     * 
     * @param wrapped
     *        the new wrapped
     */
    public void setWrapped(final HeaderSupportBean wrapped) {
        this.wrapped = wrapped;
    }

}
