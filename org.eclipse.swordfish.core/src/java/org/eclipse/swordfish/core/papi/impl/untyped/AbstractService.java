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
package org.eclipse.swordfish.core.papi.impl.untyped;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.management.notification.EntityState;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.papi.impl.untyped.consumer.ServiceProxyImpl;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.InternalEnvironment;
import org.eclipse.swordfish.papi.internal.InternalSBB;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;

/**
 * InternalOperation Proxy Implementation.
 */
/**
 * AbstractService.
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public abstract class AbstractService extends SOPObjectBase implements InternalService {

    /** the InternalSBB instance hosting this service object. */
    private SBBExtension parent;

    /** this is the most important information containing what this service look like. */
    private CompoundServiceDescription myDescription;

    /** a Map containing all operation objects exposed through this service. */
    private Map operationMap;

    /** The released. */
    private boolean released;

    /** The partner service. */
    private InternalService partnerService;

    /** The is root. */
    private boolean isRoot;

    /**
     * The Constructor.
     * 
     * @param description
     *        descriptor for this service
     * @param sbb
     *        the managing InternalSBB instance
     * @param partnersWith
     *        the partners with
     * 
     * @throws InternalInfrastructureException
     *         if one of the constructing parameters is null
     */
    public AbstractService(final CompoundServiceDescription description, final SBBExtension sbb, final AbstractService partnersWith)
            throws InternalInfrastructureException {
        if (description == null) throw new InternalInfrastructureException("cannot construct a service with a null descriptor");
        if (sbb == null) throw new InternalInfrastructureException("creating InternalSBB instance must not be null");
        this.isRoot = (partnersWith == null);
        this.operationMap = new HashMap();
        this.myDescription = description;
        this.parent = sbb;
        this.partnerService = null;
        this.setup(description, sbb, partnersWith);
        this.released = false;
    }

    /**
     * will cleanup all resources associated with this abstract service specially it whipes out all
     * operations. <br>
     * Subclasses might overwrite this method to handle their own additional work but they must call
     * their super class at the end of their work. <br>
     * Note that we need to have the method here public to make it invokeable from SBBImpl
     * 
     * @param sbbInitiated
     *        if this clan up is initiated through InternalSBB or not
     */
    public void cleanup(final boolean sbbInitiated) throws InternalSBBException {
        if (this.hasCallbackService()) {
            ((AbstractService) this.getPartnerService()).cleanup(sbbInitiated);
        }
        Iterator iter = this.getOperationNames().iterator();
        while (iter.hasNext()) {
            AbstractOperation op = (AbstractOperation) this.getOperation((String) iter.next());
            if (!this.isPartnerService()) {
                this.parent.getKernel().sendOperationStateNotification(EntityState.REMOVED, op, this.getRole(op));
            }
            op.cleanup(sbbInitiated);
        }
        this.operationMap.clear();
        this.released = true;
        // REMARK I did not reset the parent and description on purpose!
    }

    /**
     * Gets the environment.
     * 
     * @return the environment
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#getEnvironment()
     */
    public InternalEnvironment getEnvironment() {
        return this.parent.getEnvironment();
    }

    /**
     * Gets the environment implementation.
     * 
     * @return -- the implementation of the environment
     */
    public EnvironmentImpl getEnvironmentImplementation() {
        return this.parent.getEnvironmentImplementation();
    }

    /**
     * Gets the name.
     * 
     * @return the name
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#getName()
     */
    public QName getName() {
        return this.myDescription.getPortTypeQName();
    }

    /**
     * Gets the operation communication style.
     * 
     * @param operationName
     *        the operation name
     * 
     * @return the operation communication style
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#getOperationCommunicationStyle(java.lang.String)
     */
    public InternalCommunicationStyle getOperationCommunicationStyle(final String operationName) throws InternalSBBException {
        if (operationName == null) throw new IllegalArgumentException("operation name must not be null");
        InternalOperation op = (InternalOperation) this.operationMap.get(operationName);
        if (op == null)
            throw new InternalIllegalInputException("operation " + operationName + " is not a member of this service proxy");
        return op.getCommunicationStyle();
    }

    /**
     * Gets the operation names.
     * 
     * @return the operation names
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#getOperationNames()
     */
    public Collection getOperationNames() {
        return this.operationMap.keySet();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.SOPObjectBase#getParticipantIdentityAsString()
     */
    @Override
    public String getParticipantIdentityAsString() {
        return this.getSBBExtension().getParticipantIdentityAsString();
    }

    /**
     * Gets the provider ID.
     * 
     * @return the provider ID
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#getProviderID()
     */
    public QName getProviderID() {
        return this.myDescription.getServiceQName();
    }

    /**
     * Gets the InternalSBB.
     * 
     * @return the InternalSBB
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#getSBB()
     */
    public InternalSBB getSBB() {
        return this.parent;
    }

    /**
     * Checks for callback service.
     * 
     * @return true, if has callback service
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#hasCallbackService()
     */
    public boolean hasCallbackService() {
        return this.partnerService != null;
    }

    /**
     * Checks if is active.
     * 
     * @return -- true if release has been called on this service. TODO make this to be public
     */
    public boolean isActive() {
        return !this.released;
    }

    /**
     * Checks if is root.
     * 
     * @return Returns the isRoot.
     */
    public boolean isRoot() {
        return this.isRoot;
    }

    /**
     * Release.
     * 
     * @see org.eclipse.swordfish.papi.untyped.InternalService#release()
     */
    public void release() throws InternalSBBException {
        this.released = true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String out = null;
        if (this instanceof ServiceProxyImpl) {
            out = "Proxy for " + this.getName() + "\n";
        } else {
            out = "Skeleton for " + this.getName() + "\n";
            out += "with ProviderId " + this.getProviderID() + "\n";
        }
        out += "InternalOperations: " + this.getOperationNames() + "\n";
        if (this.hasCallbackService()) {
            out += "has Partner " + this.getPartnerService().getName() + "\n";
        } else if (this.isPartnerService()) {
            out += "is Partner " + "\n";
        }
        out += "Participant ID: " + this.getKernel().getParticipant();
        return out;
    }

    /**
     * registers all endpoints defined in this service description.
     * 
     * @throws ServiceException
     */
    protected void activateEndpoints() throws InternalInfrastructureException {
        this.getKernel().activateAllEndpoints(this.myDescription);
    }

    /**
     * for subclass usage. Adds an operation to the map of operations this object hostsF
     * 
     * @param op
     *        the InternalOperation Object to be added
     */
    protected void addOperation(final InternalOperation op) {
        this.operationMap.put(op.getName(), op);
        // notify the management subsystem that a new operation proxy/skeleton
        // was created
        // this is not done for partner proxies/skeletons since these are hidden
        // for the purposes
        // of mesage tracking, response time monitoring etc.
        if (!this.isPartnerService()) {
            this.parent.getKernel().sendOperationStateNotification(EntityState.ADDED, op, this.getRole(op));
        }
    }

    /**
     * unregisters all endpoints defined in this service description.
     * 
     * @throws ServiceException
     */
    protected void deactivateEndpoints() throws InternalInfrastructureException {
        this.getKernel().deactivateAllEndpoints(this.myDescription);
    }

    /**
     * Gets the kernel.
     * 
     * @return the Kernel this Abstract InternalService is attached to
     */
    protected Kernel getKernel() {
        return this.getSBBExtension().getKernel();
    }

    /**
     * returns an operation by its name.
     * 
     * @param operationName
     *        the name of the operation
     * 
     * @return -- the operation object
     */
    protected InternalOperation getOperation(final String operationName) throws InternalSBBException {
        if (operationName == null) throw new IllegalArgumentException("operation name must not be null");

        InternalOperation op = (InternalOperation) this.operationMap.get(operationName);

        if (op == null) throw new InternalIllegalInputException("operation " + operationName + " is not a member of this service");
        return op;
    }

    /**
     * Gets the partner service.
     * 
     * @return Returns the partnerService.
     */
    protected InternalService getPartnerService() {
        return this.partnerService;
    }

    /**
     * returns the role this participant has with respect to a given operation.
     * 
     * @param op
     *        the op
     * 
     * @return the role of this participant
     */
    protected abstract ParticipantRole getRole(InternalOperation op);

    /**
     * Gets the InternalSBB extension.
     * 
     * @return -- the InternalSBB extension inerface that enables subclasses to do cleanup work
     */
    protected SBBExtension getSBBExtension() {
        return this.parent;
    }

    /**
     * Checks if is partner service.
     * 
     * @return -- true if this service is a partner service of another service
     */
    protected boolean isPartnerService() {
        return !this.isRoot;
    }

    /**
     * Sets the partner service.
     * 
     * @param partnerService
     *        The partnerService to set.
     */
    protected void setPartnerService(final InternalService partnerService) {
        this.partnerService = partnerService;
    }

    /**
     * sets up the current structure regarding the type of this service. This is up to the real
     * implementation
     * 
     * @param description
     *        the aggregate service description which describes this service
     * @param theParent
     *        the InternalSBB object hosting this service
     * @param partnersWith
     *        the partners with
     * 
     * @throws InternalInfrastructureException
     *         if the construction cannot happen in the subclass
     */
    protected abstract void setup(final CompoundServiceDescription description, final SBBExtension theParent,
            final AbstractService partnersWith) throws InternalInfrastructureException;
}
