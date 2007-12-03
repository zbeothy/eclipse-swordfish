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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.instancemanager.InstanceManager;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.management.notification.EntityState;
import org.eclipse.swordfish.core.management.notification.ParticipantRole;
import org.eclipse.swordfish.core.management.notification.ParticipantStateNotification;
import org.eclipse.swordfish.core.management.notification.ServiceStateNotification;
import org.eclipse.swordfish.core.papi.impl.untyped.consumer.ServiceProxyImpl;
import org.eclipse.swordfish.core.papi.impl.untyped.provider.ServiceSkeletonImpl;
import org.eclipse.swordfish.papi.internal.InternalEnvironment;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthenticationException;
import org.eclipse.swordfish.papi.internal.exception.InternalAuthorizationException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.eclipse.swordfish.papi.internal.exception.SBBClosedException;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalServiceProxy;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalServiceSkeleton;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * An implementation of InternalSBB interface.
 */
public class SBBImpl extends SOPObjectBase implements SBBExtension {

    /** the <code>kernel</code> this papi instance is working with. */
    private Kernel kernel;

    /** indicates if this instance is active. */
    private boolean isActive;

    /** identity of the creator of this InternalSBB. */
    private UnifiedParticipantIdentity identity;

    /** the instance manager of this InternalSBB .. parent wiring */
    private InstanceManager myInstanceManager;

    /**
     * creates an instance of this InternalSBB implementation.
     * 
     * @param instanceBaseCtx
     *        the application base context this InternalSBB lives in
     * @param instanceCtx
     *        the application context this InternalSBB lives in
     * @param theIdentity
     *        the participant identity constructing this InternalSBB
     * @param instanceManager
     *        the instance manager this InternalSBB is managed through
     */
    public SBBImpl(final AbstractApplicationContext instanceBaseCtx, final AbstractApplicationContext instanceCtx,
            final InternalParticipantIdentity theIdentity, final InstanceManager instanceManager) {
        if (instanceManager == null)
            throw new ComponentRuntimeException("instance manager to create an InternalSBB must not be null");
        if (theIdentity == null) throw new ComponentRuntimeException("participant identity must not be null");
        this.kernel = (Kernel) instanceCtx.getBean(Kernel.ROLE);
        if (this.kernel == null)
            throw new ComponentRuntimeException("received a null kernel object and cannot proceed further."
                    + " This might indicate a defective configuration.");
        this.identity = new UnifiedParticipantIdentity(theIdentity);
        this.kernel.setParticipant(this.identity);
        this.myInstanceManager = instanceManager;
        this.isActive = true;
        this.kernel.getManagementNotificationListener().sendNotification(
                new ParticipantStateNotification(this.identity, EntityState.ADDED));
    }

    /**
     * Equals.
     * 
     * @param o
     *        the o
     * 
     * @return true, if equals
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o != null) {
            String className = o.getClass().getName();
            if (className.equals(this.getClass().getName())) {
                try {
                    Method getIdentityMethod = o.getClass().getMethod("getIdentity", null);
                    Object identityObject = getIdentityMethod.invoke(o, null);
                    return this.identity.equals(identityObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * Gets the environment.
     * 
     * @return the environment
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#getEnvironment()
     */
    public InternalEnvironment getEnvironment() {
        // sanity check first
        if (!this.isActive()) throw new SBBClosedException("Attempt to use an already closed InternalSBB.");

        // buiness next
        return this.getEnvironmentImplementation();
    }

    /**
     * Gets the environment implementation.
     * 
     * @return -- the implementation of the environment used for internal PAPI reasons specially
     *         managing authentication handlers
     */
    public EnvironmentImpl getEnvironmentImplementation() {
        // sanity check first
        if (!this.isActive()) throw new SBBClosedException("Attempt to use an already closed InternalSBB.");

        // buiness next
        MultiKey key = this.buildKey("env", this.identity);
        Object env = this.kernel.getEnvironmentStore().get(key);
        if (env == null) {
            EnvironmentImpl environment = new EnvironmentImpl(this.identity, this);
            this.kernel.getEnvironmentStore().put(key, environment);
            env = environment;
        }
        return (EnvironmentImpl) env;
    }

    /**
     * Gets the identity.
     * 
     * @return the identity
     */
    public UnifiedParticipantIdentity getIdentity() {
        return this.identity;
    }

    /**
     * Gets the kernel.
     * 
     * @return the kernel
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension#getKernel()
     */
    public Kernel getKernel() {
        return this.kernel;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.SOPObjectBase#getParticipantIdentityAsString()
     */
    @Override
    public String getParticipantIdentityAsString() {
        return this.identity.toString();
    }

    /**
     * Checks if is active.
     * 
     * @return true, if is active
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#isActive()
     */
    public boolean isActive() {
        return this.isActive && ((this.kernel != null) && this.kernel.isActive());
    }

    /**
     * List active service proxies.
     * 
     * @return the collection
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#listActiveServiceProxies()
     */
    public Collection listActiveServiceProxies() {
        // sanity check first
        if (!this.isActive()) throw new SBBClosedException("Attempt to use an already closed InternalSBB.");

        // buiness next
        return this.kernel.getProxyStore().values();
    }

    /**
     * List active service skeletons.
     * 
     * @return the collection
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#listActiveServiceSkeletons()
     */
    public Collection listActiveServiceSkeletons() {
        // sanity check first
        if (!this.isActive()) throw new SBBClosedException("Attempt to use an already closed InternalSBB.");

        // buiness next
        return this.kernel.getSkeletonStore().values();
    }

    /**
     * Lookup all service proxies.
     * 
     * @param serviceName
     *        the service name
     * @param policyName
     *        the policy name
     * 
     * @return the collection
     * 
     * @throws ServiceAddressingException
     * @throws InternalInfrastructureException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#lookupAllServiceProxies(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public Collection lookupAllServiceProxies(final QName serviceName, final String policyName)
            throws InternalServiceDiscoveryException, InternalInfrastructureException, InternalAuthenticationException,
            InternalAuthorizationException {
        // sanity check first
        if (!this.isActive()) throw new SBBClosedException("Attempt to use an already closed InternalSBB.");

        // buiness next
        if (serviceName == null) throw new IllegalArgumentException("service name must not be null");

        String actualPolicyName = policyName;
        if (null == actualPolicyName) {
            actualPolicyName = "";
        } else if ("".equals(actualPolicyName.trim())) throw new IllegalArgumentException("policy name must not be empty");

        /*
         * try to get the same Proxy if it has been created before
         */
        MultiKey key = this.buildKey(serviceName, actualPolicyName);
        Object proxy = this.kernel.getProxyStore().get(key);
        /*
         * this is the case if we already had the service proxy build previously
         */
        if (proxy != null) {
            if (proxy instanceof Collection)
                return (Collection) proxy;
            else
                throw new InternalInfrastructureException("fatal lookup of stored state for service proxy");
        } else {
            /*
             * we did not have the proxy so we build it
             */
            Collection descriptionCollection = this.kernel.fetchAllServiceDescription(serviceName, actualPolicyName);
            if (descriptionCollection == null)
                throw new InternalInfrastructureException("retrived service description for " + serviceName.toString() + " is null");
            Iterator iter = descriptionCollection.iterator();
            /*
             * now we create all possible proxies
             */
            while (iter.hasNext()) {
                CompoundServiceDescription description = (CompoundServiceDescription) iter.next();
                InternalServiceProxy theProxy = new ServiceProxyImpl(description, actualPolicyName, this, null);
                this.kernel.getProxyStore().put(key, theProxy);
            }
            // simply use the fact of the proxystore being a multi map
            return (Collection) this.kernel.getProxyStore().get(key);
        }
    }

    /**
     * Lookup service proxy.
     * 
     * @param serviceName
     *        the service name
     * @param policyName
     *        the policy name
     * 
     * @return the service proxy
     * 
     * @throws ServiceAddressingException
     * @throws InternalInfrastructureException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#lookupServiceProxy(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public InternalServiceProxy lookupServiceProxy(final QName serviceName, final String policyName)
            throws InternalServiceDiscoveryException, InternalInfrastructureException, InternalAuthenticationException,
            InternalAuthorizationException {
        // sanity check first
        if (!this.isActive()) throw new SBBClosedException("Attempt to use an already closed InternalSBB.");

        // buiness next
        if (serviceName == null) throw new IllegalArgumentException("service name must not be null");
        String actualPolicyName = policyName;
        if (null == actualPolicyName) {
            actualPolicyName = "";
        } else if ("".equals(actualPolicyName.trim())) throw new IllegalArgumentException("policy name must not be empty");

        /*
         * try to get the same Proxy if it has been created before
         */
        MultiKey key = this.buildKey(serviceName, actualPolicyName);
        Object proxyCol = this.kernel.getProxyStore().get(key);
        /*
         * this is the case if we already had the service proxy build previously
         */
        if (proxyCol != null) {
            Object proxy = ((Collection) proxyCol).toArray()[0];
            if (proxy instanceof InternalServiceProxy)
                return (InternalServiceProxy) proxy;
            else
                throw new InternalInfrastructureException("fatal lookup of stored state for service proxy");
        } else {
            /*
             * we did not have the proxy so we build it
             */
            CompoundServiceDescription description = this.kernel.fetchServiceDescription(serviceName, actualPolicyName);
            if (description == null)
                throw new InternalInfrastructureException("retrived service description for " + serviceName.toString() + " is null");
            InternalServiceProxy theProxy = new ServiceProxyImpl(description, actualPolicyName, this, null);
            this.kernel.getProxyStore().put(key, theProxy);
            this.kernel.getManagementNotificationListener().sendNotification(
                    new ServiceStateNotification(this.kernel.getParticipant(), ParticipantRole.CONSUMER, description
                        .getPortTypeQName(), EntityState.ADDED));
            return theProxy;
        }
    }

    /**
     * Lookup service skeleton.
     * 
     * @param serviceName
     *        the service name
     * @param providerId
     *        the provider id
     * 
     * @return the service skeleton
     * 
     * @throws ServiceAddressingException
     * @throws InternalInfrastructureException
     * @throws InternalAuthenticationException
     * @throws InternalAuthorizationException
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#lookupServiceSkeleton(javax.xml.namespace.QName,
     *      java.lang.String)
     */
    public InternalServiceSkeleton lookupServiceSkeleton(final QName serviceName, final QName providerId)
            throws InternalServiceDiscoveryException, InternalInfrastructureException, InternalAuthenticationException,
            InternalAuthorizationException {
        // sanity check first
        if (!this.isActive()) throw new SBBClosedException("Attempt to use an already closed InternalSBB.");

        // buiness next
        if (serviceName == null) throw new IllegalArgumentException("service name must not be null");

        if (providerId == null) throw new IllegalArgumentException("providerId must not be null");
        /*
         * try to get the same Proxy if it has been created before
         */
        MultiKey key = this.buildKey(serviceName, providerId);
        Object skeleton = this.kernel.getSkeletonStore().get(key);
        /*
         * this is the case if we already had the service proxy build previously
         */
        if (skeleton != null) {
            if (skeleton instanceof InternalServiceSkeleton)
                return (InternalServiceSkeleton) skeleton;
            else
                throw new InternalInfrastructureException("Fatal error while looking up InternalServiceSkeleton for key "
                        + key.toString());
        } else {
            /*
             * we did not had the proxy so we build it
             */
            CompoundServiceDescription description = this.kernel.fetchServiceDescription(serviceName, providerId);
            if (description == null)
                throw new InternalInfrastructureException("Could not retrieve ServiceDescription for " + serviceName.toString()
                        + "/" + providerId.toString());
            InternalServiceSkeleton theSkeleton = null;
            try {
                theSkeleton = new ServiceSkeletonImpl(description, providerId, this, null);
            } catch (RuntimeException e) {
                throw new InternalInfrastructureException("Could not create InternalServiceSkeleton for " + serviceName.toString()
                        + "/" + providerId.toString(), e);
            }
            this.kernel.getSkeletonStore().put(key, theSkeleton);
            this.kernel.getManagementNotificationListener().sendNotification(
                    new ServiceStateNotification(this.kernel.getParticipant(), ParticipantRole.PROVIDER, description
                        .getPortTypeQName(), EntityState.ADDED));
            return theSkeleton;
        }
    }

    /**
     * this implementations release method is a sync implementation, in order to make sure concurent
     * release situations specially closing the JBI Container are handeled correctly.
     * 
     * @see org.eclipse.swordfish.papi.InternalSBB#release()
     */
    // FIX Defect 1377 through sync.
    public synchronized void release() {
        if (this.isActive()) {
            this.isActive = false;
            this.kernel.shutdown();
            this.kernel = null;
            this.myInstanceManager.removeAssociation(this.identity.getParticipantIdentity());
            this.myInstanceManager = null;
        }
    }

    /**
     * Removes the service proxy.
     * 
     * @param serviceProxy
     *        the service proxy
     * @param policyId
     *        the policy id
     * @param sbbInitiated
     *        the sbb initiated
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension#removeServiceProxy(org.eclipse.swordfish.papi.untyped.consumer.InternalServiceProxy,
     *      java.lang.String, boolean)
     */
    public void removeServiceProxy(final InternalServiceProxy serviceProxy, final String policyId, final boolean sbbInitiated) {

        if ((serviceProxy == null) || (policyId == null))
            throw new ComponentRuntimeException("cannot build key with null arguments");
        MultiKey key = this.buildKey(serviceProxy.getName(), policyId);
        this.kernel.getProxyStore().remove(key);
        this.kernel.getManagementNotificationListener().sendNotification(
                new ServiceStateNotification(this.kernel.getParticipant(), ParticipantRole.CONSUMER, serviceProxy.getName(),
                        EntityState.REMOVED));
    }

    /**
     * note that removing the key will remove all associated proxies.
     * 
     * @param serviceSkeleton
     *        the service skeleton
     * @param providerId
     *        the provider id
     * @param sbbInitiated
     *        the sbb initiated
     * 
     * @see org.eclipse.swordfish.core.papi.impl.untyped.SBBExtension#removeServiceSkeleton(org.eclipse.swordfish.papi.untyped.provider.InternalServiceSkeleton,
     *      java.lang.String, boolean)
     */
    public void removeServiceSkeleton(final InternalServiceSkeleton serviceSkeleton, final QName providerId,
            final boolean sbbInitiated) {
        if ((serviceSkeleton == null) || (providerId == null))
            throw new ComponentRuntimeException("cannot build key with null arguments");

        MultiKey key = this.buildKey(serviceSkeleton.getName(), providerId);
        this.kernel.getSkeletonStore().remove(key);
        this.kernel.getManagementNotificationListener().sendNotification(
                new ServiceStateNotification(this.kernel.getParticipant(), ParticipantRole.PROVIDER, serviceSkeleton.getName(),
                        EntityState.REMOVED));
    }

    /**
     * creates a multikey to be used by kernel stores.
     * 
     * @param qname
     *        the first key argument
     * @param providerId
     *        the second key argument
     * 
     * @return -- a multikey of (qname, suffix)
     */
    private MultiKey buildKey(final QName qname, final QName providerId) {
        return new MultiKey(qname, providerId);
    }

    /**
     * creates a multikey to be used by kernel stores.
     * 
     * @param qname
     *        the first key argument
     * @param suffix
     *        the second key argument
     * 
     * @return -- a multikey of (qname, suffix)
     */
    private MultiKey buildKey(final QName qname, final String suffix) {
        return new MultiKey(qname, suffix);
    }

    /**
     * creates a multikey to be used by kernel stores.
     * 
     * @param prefix
     *        first key tupel value
     * @param participant
     *        second key tupel value
     * 
     * @return -- a multikey of (prefix, participant)
     */
    private MultiKey buildKey(final String prefix, final UnifiedParticipantIdentity participant) {
        return new MultiKey(prefix, participant);
    }

}
