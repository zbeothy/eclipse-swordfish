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

import org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation;
import org.eclipse.swordfish.papi.internal.exception.InternalParticipantException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessage;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;

/**
 * usefull to add some meta data information to the incoming message handler.
 */
public class IncomingMessageHandlerProxy implements InternalIncomingMessageHandler {

    /** The operation. */
    private AbstractOperation operation;

    /** The handler. */
    private InternalIncomingMessageHandler handler;

    /**
     * Instantiates a new incoming message handler proxy.
     * 
     * @param operation
     *        the operation
     * @param handler
     *        the handler
     */
    public IncomingMessageHandlerProxy(final AbstractOperation operation, final InternalIncomingMessageHandler handler) {
        this.operation = operation;
        this.handler = handler;
    }

    /**
     * Gets the handler.
     * 
     * @return the handler
     */
    public InternalIncomingMessageHandler getHandler() {
        return this.handler;
    }

    /**
     * Gets the operation.
     * 
     * @return the operation
     */
    public AbstractOperation getOperation() {
        return this.operation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler#handleError(org.eclipse.swordfish.core.papi.impl.exception.InternalSBBException,
     *      org.eclipse.swordfish.papi.untyped.InternalCallContext)
     */
    public void handleError(final InternalSBBException e, final InternalCallContext ctx) throws InternalParticipantException {
        // ClassLoader currClassLoader = null;
        // try {
        // currClassLoader = Thread.currentThread().getContextClassLoader();
        // Thread.currentThread().setContextClassLoader(
        // handler.getClass().getClassLoader());
        // handler.handleError(mapException(e), ctx);
        this.handler.handleError(e, ctx);
        // } finally {
        // Thread.currentThread().setContextClassLoader(currClassLoader);
        // }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler#handleMessage(org.eclipse.swordfish.papi.untyped.InternalIncomingMessage)
     */
    public void handleMessage(final InternalIncomingMessage arg0) throws InternalParticipantException {
        // ClassLoader currClassLoader = null;
        // try {
        // currClassLoader = Thread.currentThread().getContextClassLoader();
        // Thread.currentThread().setContextClassLoader(
        // handler.getClass().getClassLoader());
        this.handler.handleMessage(arg0);
        // } finally {
        // Thread.currentThread().setContextClassLoader(currClassLoader);
        // }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler#onRelease(boolean)
     */
    public void onRelease(final boolean arg0) {
        // ClassLoader currClassLoader = null;
        // try {
        // currClassLoader = Thread.currentThread().getContextClassLoader();
        // Thread.currentThread().setContextClassLoader(
        // handler.getClass().getClassLoader());
        this.handler.onRelease(arg0);
        // } finally {
        // Thread.currentThread().setContextClassLoader(currClassLoader);
        // }
    }

    // /**
    // * Map exception.
    // *
    // * @param e the e
    // *
    // * @return the InternalSBB exception
    // */
    // private InternalSBBException mapException(InternalSBBException e) {
    // try {
    // throw e;
    // } catch (InternalSBBException ee) {
    // return ee;
    // }
    // }

}
