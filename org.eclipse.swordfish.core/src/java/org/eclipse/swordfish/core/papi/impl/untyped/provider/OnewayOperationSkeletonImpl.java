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
package org.eclipse.swordfish.core.papi.impl.untyped.provider;

import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractService;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalOnewayOperationSkeleton;

/**
 * The Class OnewayOperationSkeletonImpl.
 * 
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class OnewayOperationSkeletonImpl extends AbstractOperation implements InternalOnewayOperationSkeleton {

    /** The my handler. */
    private IncomingMessageHandlerProxy myHandler;

    /**
     * The Constructor.
     * 
     * @param desc
     *        this operations description
     * @param sbb
     *        the managing InternalSBB instance
     * @param parent
     *        the InternalService skeleton to which this operation belongs
     * @param partnersWith
     *        the partners with
     */
    public OnewayOperationSkeletonImpl(final OperationDescription desc, final SBBExtension sbb, final AbstractService parent,
            final AbstractService partnersWith) {
        super(desc, sbb, parent, partnersWith, false);
        this.myHandler = null;
    }

    /**
     * Cleanup.
     * 
     * @param sbbInitiated
     *        the sbb initiated
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation#cleanup(boolean)
     */
    @Override
    public void cleanup(final boolean sbbInitiated) {
        if (this.hasMessageHandler()) {
            try {
                if (this.isCallbackOperation()) {
                    this.getKernel().unregisterMessageHandler(
                            this.getOperationDescription().getServiceDescription().getServiceQName(), this.getName(), Role.SENDER,
                            sbbInitiated);
                } else {
                    this.getKernel().unregisterMessageHandler(
                            this.getOperationDescription().getServiceDescription().getServiceQName(), this.getName(),
                            Role.RECEIVER, sbbInitiated);
                }
            } catch (MessageHandlerRegistrationException e) {
                e.printStackTrace();
                // TODO log the fact of what happened right now
            }
        }
        this.myHandler = null;
        super.cleanup(sbbInitiated);
    }

    /**
     * Gets the communication style.
     * 
     * @return the communication style
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalOperation#getCommunicationStyle()
     */
    @Override
    public InternalCommunicationStyle getCommunicationStyle() {
        return InternalCommunicationStyle.ONEWAY;
    }

    /**
     * Gets the message handler.
     * 
     * @return -- the previously registered message handler or null if no message handler was
     *         registered
     */
    public InternalIncomingMessageHandler getMessageHandler() {
        return this.myHandler.getHandler();
    }

    /**
     * Checks for message handler.
     * 
     * @return true, if has message handler
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalOnewayOperationSkeleton#hasMessageHandler()
     */
    public boolean hasMessageHandler() {
        return this.myHandler != null;
    }

    /**
     * Register message handler.
     * 
     * @param handler
     *        the handler
     * 
     * @throws InternalInfrastructureException
     * @throws MessageHandlerRegistrationException
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalOnewayOperationSkeleton#registerMessageHandler(org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler)
     */
    public void registerMessageHandler(final InternalIncomingMessageHandler handler) throws InternalInfrastructureException,
            MessageHandlerRegistrationException {

        if (this.hasMessageHandler()) throw new MessageHandlerRegistrationException("remove existing message handlerfirst");
        if (handler == null) throw new IllegalArgumentException("A handler to be registered mustnot be null");

        this.myHandler = new IncomingMessageHandlerProxy(this, handler);
        if (this.isCallbackOperation()) {
            this.getKernel().registerMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                    this.getName(), Role.SENDER, this.myHandler);
        } else {
            this.getKernel().registerMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                    this.getName(), Role.RECEIVER, this.myHandler);
        }

    }

    /**
     * Release message handler.
     * 
     * @return true, if release message handler
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalOnewayOperationSkeleton#releaseMessageHandler()
     */
    public boolean releaseMessageHandler() {

        boolean success = false;
        try {
            if (this.isCallbackOperation()) {
                this.getKernel().unregisterMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                        this.getName(), Role.SENDER, false);
            } else {
                this.getKernel().unregisterMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                        this.getName(), Role.RECEIVER, false);
            }
            success = true;
            this.myHandler = null;
        } catch (MessageHandlerRegistrationException e) {
            e.printStackTrace();
            // FIXME do some logging at least
        }
        return success;
    }
}
