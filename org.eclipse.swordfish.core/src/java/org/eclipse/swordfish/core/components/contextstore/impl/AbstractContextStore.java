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
package org.eclipse.swordfish.core.components.contextstore.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.eclipse.swordfish.core.components.contextstore.ContextStore;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtensionFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * The Class AbstractContextStore.
 */
public abstract class AbstractContextStore implements ContextStore {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(AbstractContextStore.class);

    /**
     * Removes the call context.
     * 
     * @param key
     *        the key
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.ContextStore#removeCallContext(java.lang.String)
     */
    public abstract void removeCallContext(final String key);

    /**
     * Restore call context.
     * 
     * @param key
     *        the key
     * 
     * @return the call context extension
     * 
     * @throws ContextNotRestoreableException
     * @throws ContextNotFoundException
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.ContextStore#restoreCallContext(java.lang.String)
     */
    public abstract CallContextExtension restoreCallContext(final String key) throws InternalIllegalInputException,
            InternalInfrastructureException;

    /**
     * Store call context.
     * 
     * @param callContext
     *        the call context
     * 
     * @return the string
     * 
     * @throws ContextNotStoreableException
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.ContextStore#storeCallContext(org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension)
     */
    public abstract String storeCallContext(final CallContextExtension callContext) throws InternalIllegalInputException,
            InternalInfrastructureException;

    /**
     * builds a key that is used to access the context to be stored some times later.
     * 
     * @param ctx
     *        the context to be stored
     * 
     * @return -- a string that acts as a key to this context
     */
    protected String buildKey(final CallContextExtension ctx) {
        // TODO is this a good key?
        String key = ctx.getMessageID() + ctx.getCorrelationID();
        key = key.replace(':', '-');
        return key;
    }

    /**
     * unmarshals a previously marshalled call context from a byteArray.
     * 
     * @param ba
     *        the byte array containing the context
     * 
     * @return -- the restored call context
     * 
     * @throws IOException
     *         if an error occurs during deserialization
     */
    protected CallContextExtension deserializeContext(final byte[] ba) throws IOException {
        ByteArrayInputStream bai = new ByteArrayInputStream(ba);
        CallContextExtension ctx = CallContextExtensionFactory.createCallContextExtension();
        if (LOG.isDebugEnabled()) {
            LOG.debug("about to deserialize context: " + new String(ba));
        }
        ctx.unmarshall(bai);
        return ctx;
    }

    /**
     * marshalls a call context into a byte array to be saved in the DB.
     * 
     * @param ctx
     *        the context to be stored
     * 
     * @return -- a byte array representation of the object
     * 
     * @throws IOException
     *         if an error occurs during serialization
     */
    protected byte[] serializeContext(final CallContextExtension ctx) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ctx.marshall(bao);
        return bao.toByteArray();
    }

}
