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
package org.eclipse.swordfish.papi.internal.untyped.provider;

import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalOutgoingMessage;

/**
 * This interface provides the concrete <code>InternalOperation</code> skeleton to be used by a
 * provider for publishing messages using the Notification communication style.
 * 
 */
public interface InternalNotificationOperationSkeleton extends InternalOperation {

    /**
     * This methods <b>sends (publishes) a notification</b>.
     * 
     * @param aMessage
     *        the "notification" message
     * @return A <code>InternalCallContext</code> holding the meta information about the issued
     *         call
     * 
     * @throws InternalSBBException
     */
    InternalCallContext sendNotification(InternalOutgoingMessage aMessage) throws InternalSBBException;

    /**
     * This methods <b>sends (publishes) a notification</b> in the context of some other call.
     * 
     * @param aMessage
     *        the "notification" message
     * @param aContextToRelateTo
     *        passed InternalCallContext to relate to. The resulting context will contain a relation
     *        of type "TriggeringCall" to this context's messageID.
     * 
     * @return A <code>InternalCallContext</code> holding the meta information about the issued
     *         call
     * 
     * @throws InternalSBBException
     */
    InternalCallContext sendNotification(InternalOutgoingMessage aMessage, InternalCallContext aContextToRelateTo)
            throws InternalSBBException;
}
