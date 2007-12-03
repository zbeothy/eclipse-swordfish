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
package org.eclipse.swordfish.core.components.contextstore;

import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * The Interface ContextStore.
 */
public interface ContextStore {

    /** role of this class. */
    String ROLE = ContextStore.class.getName();

    /**
     * Destroy.
     */
    void destroy();

    /**
     * Init.
     * 
     * @throws ContextException
     */
    void init() throws InternalInfrastructureException;

    /**
     * Removes the call context.
     * 
     * @param key
     *        the key
     */
    void removeCallContext(String key);

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
     */
    CallContextExtension restoreCallContext(String key) throws InternalIllegalInputException, InternalInfrastructureException;

    /**
     * Store call context.
     * 
     * @param callContext
     *        the call context
     * 
     * @return the string
     * 
     * @throws ContextNotStoreableException
     */
    String storeCallContext(CallContextExtension callContext) throws InternalIllegalInputException, InternalInfrastructureException;
}
