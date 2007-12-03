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

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.OperationDescription;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.papi.impl.untyped.AbstractService;
import org.eclipse.swordfish.core.papi.impl.untyped.MessageExchangePattern;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension;
import org.eclipse.swordfish.core.papi.impl.untyped.consumer.ServiceProxyImpl;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.exception.SBBRuntimeException;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalServiceProxy;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalNotificationOperationSkeleton;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalOnewayOperationSkeleton;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalRequestResponseOperationSkeleton;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalServiceSkeleton;

/**
 * Implementation of a service skeleton.
 */
public class ServiceSkeletonImpl extends AbstractService implements InternalServiceSkeleton {

    /** the provider Id that this service represnets. */
    private QName providerId;

    /**
     * The Constructor.
     * 
     * @param description
     *        the description of this skeleton
     * @param providerId
     *        the provider id this service is bound to
     * @param sbb
     *        the managing InternalSBB instance
     * @param partnersWith
     *        the partners with
     * 
     * @throws InternalInfrastructureException
     *         if the construction of the service goes wrong
     */
    public ServiceSkeletonImpl(final CompoundServiceDescription description, final QName providerId, final SBBExtension sbb,
            final AbstractService partnersWith) throws InternalInfrastructureException {
        super(description, sbb, partnersWith);
        this.providerId = providerId;
        // now for service skeletons we are going to register the endpoints to
        // the DC
        // this automatically applies for the partner service skeletons
        this.activateEndpoints();
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
        try {
            this.deactivateEndpoints();
        } catch (InternalInfrastructureException e) {
            e.printStackTrace();
            // TODO we could not deregister the endpoint from DC ... log this
            // fact!!
        }

        super.cleanup(sbbInitiated);
        // this is why I did not removed SBBExtension from the super class
        if (/* !sbbInitiated && */!this.isPartnerService()) {
            this.getSBBExtension().removeServiceSkeleton(this, this.providerId, sbbInitiated);
        }
        this.providerId = null;
    }

    /**
     * Gets the callback service proxy.
     * 
     * @return the callback service proxy
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalServiceSkeleton#getCallbackServiceProxy()
     */
    public InternalServiceProxy getCallbackServiceProxy() {
        return (InternalServiceProxy) super.getPartnerService();
    }

    /**
     * Gets the notification operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the notification operation
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalServiceSkeleton#getNotificationOperation(java.lang.String)
     */
    public InternalNotificationOperationSkeleton getNotificationOperation(final String operationName) throws InternalSBBException {
        InternalOperation op = super.getOperation(operationName);
        if (!(op instanceof InternalNotificationOperationSkeleton))
            throw new InternalIllegalInputException("a notification operation " + operationName
                    + " is not a member of this service skeleton");
        else
            return (InternalNotificationOperationSkeleton) op;
    }

    /**
     * Gets the oneway operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the oneway operation
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalServiceSkeleton#getOnewayOperation(java.lang.String)
     */
    public InternalOnewayOperationSkeleton getOnewayOperation(final String operationName) throws InternalSBBException {
        InternalOperation op = super.getOperation(operationName);
        if (!(op instanceof InternalOnewayOperationSkeleton))
            throw new InternalIllegalInputException("a one way operation " + operationName
                    + " is not a member of this service skeleton");
        else
            return (InternalOnewayOperationSkeleton) op;
    }

    /**
     * Gets the request response operation.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the request response operation
     * 
     * @see org.eclipse.swordfish.papi.untyped.provider.InternalServiceSkeleton#getRequestResponseOperation(java.lang.String)
     */
    public InternalRequestResponseOperationSkeleton getRequestResponseOperation(final String operationName)
            throws InternalSBBException {
        InternalOperation op = super.getOperation(operationName);
        if (!(op instanceof InternalRequestResponseOperationSkeleton))
            throw new InternalIllegalInputException("a request response operation " + operationName
                    + " is not a member of this service skeleton");
        else
            return (InternalRequestResponseOperationSkeleton) op;
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
        return this.isPartnerService() ? ParticipantRole.CONSUMER : ParticipantRole.PROVIDER;
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
     * @see org.eclipse.swordfish.core.papi.impl.untyped.AbstractService#setup(org.eclipse.swordfish.core.components.iapi.ServiceDescription,
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
             * build the operation according to its communication style TODO pass the partners with
             * to enable the operations to wire to their counter parts
             */
            if (MessageExchangePattern.OUT_ONLY_URI.equals(desc.getExchangePattern())) {
                op = new NotificationOperationSkeletonImpl(desc, parent, this, partnersWith);
            } else if (MessageExchangePattern.IN_ONLY_URI.equals(desc.getExchangePattern())) {
                op = new OnewayOperationSkeletonImpl(desc, parent, this, partnersWith);
            } else if (MessageExchangePattern.IN_OUT_URI.equals(desc.getExchangePattern())) {
                op = new RequestResponseOperationSkeletonImpl(desc, parent, this, partnersWith);
            } else
                throw new InternalInfrastructureException("unknown message exchange pattern " + desc.getExchangePattern() + " for "
                        + desc.getName());

            super.addOperation(op);
        }
        if (description.getPartnerDescription() != null) {
            InternalService partner = new ServiceProxyImpl(description.getPartnerDescription(), null, parent, this);
            this.setPartnerService(partner);
        }
    }
}
