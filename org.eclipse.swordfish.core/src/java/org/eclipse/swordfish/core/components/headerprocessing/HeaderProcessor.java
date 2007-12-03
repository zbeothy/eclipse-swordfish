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
package org.eclipse.swordfish.core.components.headerprocessing;

import javax.jbi.messaging.NormalizedMessage;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * The Interface HeaderProcessor.
 */
public interface HeaderProcessor {

    /** role name for adressing processor. */
    String ROLE = HeaderProcessor.class.getName();

    /**
     * Init.
     */
    void init();

    /**
     * Map incoming request.
     * 
     * @param msg
     *        the msg
     * @param ctx
     *        the ctx
     * 
     * @throws InternalSBBException
     */
    void mapIncomingRequest(final NormalizedMessage msg, CallContextExtension ctx) throws InternalSBBException;

    /**
     * Map incoming response.
     * 
     * @param msg
     *        the msg
     * @param ctx
     *        the ctx
     * 
     * @throws InternalSBBException
     */
    void mapIncomingResponse(final NormalizedMessage msg, CallContextExtension ctx) throws InternalSBBException;

    /**
     * Map outgoing request.
     * 
     * @param ctx
     *        the ctx
     * @param msg
     *        the msg
     * 
     * @throws InternalSBBException
     */
    void mapOutgoingRequest(final CallContextExtension ctx, NormalizedMessage msg) throws InternalSBBException;

    /**
     * Map outgoing response.
     * 
     * @param ctx
     *        the ctx
     * @param msg
     *        the msg
     * 
     * @throws InternalSBBException
     */
    void mapOutgoingResponse(final CallContextExtension ctx, NormalizedMessage msg) throws InternalSBBException;
}
