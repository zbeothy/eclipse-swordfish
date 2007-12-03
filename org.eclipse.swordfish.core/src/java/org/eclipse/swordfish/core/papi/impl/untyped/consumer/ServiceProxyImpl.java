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

import java.util.Iterator;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractService;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageExchangePattern;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.provider.ServiceSkeletonImpl;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.SBBRuntimeException;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalNotificationOperationProxy;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalOnewayOperationProxy;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalRequestResponseOperationProxy;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalServiceProxy;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalServiceSkeleton;

/**
 * The implementation of the service proxy.
 */
public class ServiceProxyImpl extends AbstractService implements InternalServiceProxy {

    /** the policyIs under which this proxy was created and negotiated. */
    private String policyId;

    /**
     * The Constructor.
     * 
     * @param description
     *        the description of this service
     * @param policyId
     *        the policy id under which this service got constructed
     * @param sbb
     *        the hosting InternalSBB instance
     * @param partnersWith
     *        the partners with
     * 
     * @throws InternalInfrastructureException
     *         if the service cannot be constructed due to some reasons
     */
    public ServiceProxyImpl(final CompoundServiceDescription description, final String policyId, final SBBExtension sbb,
            final AbstractService partnersWith) throws InternalInfrastructureException {
        super(description, sbb, partnersWith);
        this.policyId = policyId;
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
    public void cleanup(final boolean sbbInitiated) throws InternalSBBException {
        super.cleanup(sbbInitiated);
        // this is why I did not removed SBBExtension from the super class
        // TODO do not remove me if I am a partner service, it will be removed
        // with the real service
        if (/* !sbbInitiated && */!this.isPartnerService()) {
            this.getSBBExtension().removeServiceProxy(this, this.policyId, sbbInitiated);
        }
    }

    /**
     * Gets the callback service skeleton.
     * 
     * @return the callback service skeleton
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalServiceProxy#getCallbackServiceSkeleton()
     */
    public InternalServiceSkeleton getCallbackServiceSkeleton() {
        return (InternalServiceSkeleton) super.getPartnerService();
    }

    /**
     * Gets the notification operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the notification operation
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalServiceProxy#getNotificationOperation(java.lang.String)
     */
    public InternalNotificationOperationProxy getNotificationOperation(final String operationName) throws InternalSBBException {
        InternalOperation op = super.getOperation(operationName);
        if (!(op instanceof InternalNotificationOperationProxy))
            throw new InternalIllegalInputException("a notification operation " + operationName
                    + " is not a member of this service proxy");
        else
            return (InternalNotificationOperationProxy) op;
    }

    /**
     * Gets the oneway operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the oneway operation
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalServiceProxy#getOnewayOperation(java.lang.String)
     */
    public InternalOnewayOperationProxy getOnewayOperation(final String operationName) throws InternalSBBException {
        InternalOperation op = super.getOperation(operationName);
        if (!(op instanceof InternalOnewayOperationProxy))
            throw new InternalIllegalInputException("a oneway operation " + operationName
                    + " is not a member of this service proxy");
        else
            return (InternalOnewayOperationProxy) op;
    }

    /**
     * Gets the request response operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the request response operation
     * 
     * @see org.eclipse.swordfish.papi.untyped.consumer.InternalServiceProxy#getRequestResponseOperation(java.lang.String)
     */
    public InternalRequestResponseOperationProxy getRequestResponseOperation(final String operationName)
            throws InternalSBBException {
        InternalOperation op = super.getOperation(operationName);
        if (!(op instanceof InternalRequestResponseOperationProxy))
            throw new InternalIllegalInputException("a request-response operation " + operationName
                    + " is not a member of this service proxy");
        else
            return (InternalRequestResponseOperationProxy) op;
    }

    /**
     * Release.
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#release()
     */
    @Override
    public void release() throws InternalSBBException {
        if (!this.isPartnerService()) {
            if (this.getSBBExtension().isActive()) {
                this.cleanup(false);
            }
            super.release();
        } else
            throw new SBBRuntimeException(
                    "you cannot release partner services, they are released when the owning service is released.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.AbstractService#getRole(org.eclipse.swordfish.papi.untyped.InternalOperation)
     */
    @Override
    protected ParticipantRole getRole(final InternalOperation op) {
        return this.isPartnerService() ? ParticipantRole.PROVIDER : ParticipantRole.CONSUMER;
    }

    /**
     * Setup.
     * 
     * @param description
     *        the description
     * @param parent
     *        the parent
     * @param partnersWith
     *        the partners with
     * 
     * @throws InternalInfrastructureException
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.AbstractService#setup(org.eclipse.swordfish.core.components.iapi.AggregateServiceDescription,
     *      org.eclipse.swordfish.core.papi.impl.untyped.SBBImpl) TODO implement the incarnation of
     *      the partner service
     */
    @Override
    protected void setup(final CompoundServiceDescription description, final SBBExtension parent, final AbstractService partnersWith)
            throws InternalInfrastructureException {
        Iterator iter = description.getOperations().iterator();
        while (iter.hasNext()) {
            OperationDescription desc = (OperationDescription) iter.next();
            InternalOperation op = null;
            /*
             * build the operation according to its communication style
             */
            if (MessageExchangePattern.OUT_ONLY_URI.equals(desc.getExchangePattern())) {
                op = new NotificationOperationProxyImpl(desc, parent, this, partnersWith);
            } else if (MessageExchangePattern.IN_ONLY_URI.equals(desc.getExchangePattern())) {
                op = new OnewayOperationProxyImpl(desc, parent, this, partnersWith);
            } else if (MessageExchangePattern.IN_OUT_URI.equals(desc.getExchangePattern())) {
                op = new RequestResponseOperationProxyImpl(desc, parent, this, partnersWith);
            } else
                throw new InternalInfrastructureException("unknown message exchange pattern " + desc.getExchangePattern() + " for "
                        + desc.getName());

            super.addOperation(op);

        }
        if (description.getPartnerDescription() != null) {
            InternalService partner = new ServiceSkeletonImpl(description.getPartnerDescription(), null, parent, this);
            this.setPartnerService(partner);
        }
    }
}
