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
package org.eclipse.swordfish.core.papi.impl.untyped.consumer;

import javax.jbi.JBIException;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Transport;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractOperation;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractService;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.MessageHandlerRegistrationException;
import org.eclipse.swordfish.papi.internal.untyped.InternalIncomingMessageHandler;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalNotificationOperationProxy;

/**
 * Notification proxy implementation.
 */
public class NotificationOperationProxyImpl extends AbstractOperation implements InternalNotificationOperationProxy {

    /** The my handler. */
    private IncomingMessageHandlerProxy myHandler;

    /**
     * Instantiates a new notification operation proxy impl.
     * 
     * @param desc
     *        the desc
     * @param sbb
     *        the sbb
     * @param parent
     *        the parent
     * @param partnersWith
     *        the partners with
     */
    public NotificationOperationProxyImpl(final OperationDescription desc, final SBBExtension sbb, final AbstractService parent,
            final AbstractService partnersWith) {
        // don't resolve the policy for notifications, they will come along with
        // the message
        super(desc, sbb, parent, partnersWith, true);
        this.myHandler = null;
        SPDXPort[] ports = desc.getServiceDescription().getSupportedPorts(this.getName());
        for (int i = 0; i < ports.length; i++) {
            // FIXME: Bad encapsulation:
            // PAPI implementation should not make such assumption
            // about capabilities of underlying layers:
            // Either query or leave it to the object in charge
            final Transport tp = ports[i].getTransport();
            if (!(Transport.JMS.equals(tp)))
                throw new ComponentRuntimeException("Notification operation " + this.getName() + " is only supported on JMS ");
        }
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
            // unregister the endpoint
            try {
                this.getKernel().getEndpointManager().deactivateNotificationEndpoint(this.getKernel().getParticipant(),
                        this.getOperationDescription().getServiceDescription(), this.getName(),
                        this.getKernel().getLocalEndpointRepository());
            } catch (JBIException e) {
                throw new RuntimeException("cannot unregister dynamic endpoint for notification");
            }
            this.getKernel().unregisterMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                    this.getName(), Role.RECEIVER, sbbInitiated);
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
        return InternalCommunicationStyle.NOTIFICATION;
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
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalNotificationOperationProxy#hasMessageHandler()
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
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalNotificationOperationProxy#registerMessageHandler(org.eclipse.swordfish.papi.untyped.InternalIncomingMessageHandler)
     */
    public void registerMessageHandler(final InternalIncomingMessageHandler handler) throws InternalInfrastructureException,
            MessageHandlerRegistrationException {
        // FIXES 2313
        if (!this.isSupportedOperation())
            throw new MessageHandlerRegistrationException("The agreed policy indicates this operation not to be supported");
        if (this.hasMessageHandler()) throw new MessageHandlerRegistrationException("remove existing message handlerfirst");
        if (handler == null) throw new IllegalArgumentException("A handler to be registered must not be null");
        this.myHandler = new IncomingMessageHandlerProxy(this, handler);

        this.getKernel().registerMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                this.getName(), Role.RECEIVER, this.myHandler);
        try {
            this.getKernel().getEndpointManager().activateNotificationEndpoint(this.getKernel().getParticipant(),
                    this.getOperationDescription().getServiceDescription(), this.getName(),
                    this.getKernel().getLocalEndpointRepository());
        } catch (JBIException e) {
            this.getKernel().unregisterMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                    this.getName(), Role.RECEIVER, true);
            this.myHandler = null;
            throw new InternalInfrastructureException("cannot register the message handler ", e);
        }
    }

    /**
     * Release message handler.
     * 
     * @return true, if release message handler
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalNotificationOperationProxy#releaseMessageHandler()
     */
    public boolean releaseMessageHandler() {
        boolean success = false;
        try {
            // unregister the endpoint
            this.getKernel().getEndpointManager().deactivateNotificationEndpoint(this.getKernel().getParticipant(),
                    this.getOperationDescription().getServiceDescription(), this.getName(),
                    this.getKernel().getLocalEndpointRepository());
            // unregister the handler
            this.getKernel().unregisterMessageHandler(this.getOperationDescription().getServiceDescription().getServiceQName(),
                    this.getName(), Role.RECEIVER, false);
            success = true;
            this.myHandler = null;
        } catch (MessageHandlerRegistrationException e) {
            success = true;
        } catch (JBIException e) {
            success = true;
        }
        return success;
    }
}
