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
package org.eclipse.swordfish.core.components.resolver.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.core.components.resolver.PolicyResolver;
import org.eclipse.swordfish.core.components.srproxy.ServiceInfo;
import org.eclipse.swordfish.core.components.srproxy.SrProxy;
import org.eclipse.swordfish.core.components.srproxy.SrProxyException;
import org.eclipse.swordfish.papi.internal.exception.ConfigurationException;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity;
import org.eclipse.swordfish.policytrader.ServiceDescriptor;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;

/**
 * The Class PolicyResolverBean.
 * 
 */
public class PolicyResolverBean implements PolicyResolver {

    /**
     * Reference to the configuration repository manager component, by which resources holding the
     * policy definitions are being loaded.
     */
    private ConfigurationRepositoryManagerInternal manager = null;

    /** The policy map which assigns policy names to their definitions. */
    private Map policyMap;

    /**
     * Flag which will activate the usage of the default consumer policy on a service provider,
     * which will allow the latter to process service calls from non-SOP participants.
     */
    private boolean useDefaultConsumerPolicy;

    /** The default consumer policy for non-SOP consumers. */
    private String defaultConsumerPolicyName;

    /** Default policy id. */
    private String defaultPolicyID;

    /** The sr proxy. */
    private SrProxy srProxy = null;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getAssignedPolicyIds(javax.xml.namespace.QName)
     */
    public List/* <String> */getAssignedPolicyIds(final QName providerId) throws BackendException {
        try {
            return this.srProxy.getParticipantPolicyIds(providerId);
        } catch (SrProxyException e) {
            throw new BackendException(e);
        }
    }

    /**
     * (non-Javadoc).
     * 
     * @return the default consumer policy ID
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getDefaultConsumerPolicy()
     */
    public String getDefaultConsumerPolicyID() {
        if (this.isUseDefaultConsumerPolicy())
            return this.resolvePolicyID(this.defaultConsumerPolicyName);
        else
            return null;
    }

    /**
     * Return the default consumer policy name used by this resolver.
     * 
     * @return the default consumer policy which should be used. Should be defined in the policyMap
     */
    public String getDefaultConsumerPolicyName() {
        return this.defaultConsumerPolicyName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#getDefaultPolicyID()
     */
    public String getDefaultPolicyID() {
        return this.defaultPolicyID;
    }

    /**
     * Gets the manager.
     * 
     * @return Returns the configuration repository manager used for resolving policy description
     *         resources.
     */
    public ConfigurationRepositoryManagerInternal getManager() {
        return this.manager;
    }

    /**
     * Gets the sr proxy.
     * 
     * @return the sr proxy
     */
    public SrProxy getSrProxy() {
        return this.srProxy;
    }

    /**
     * Check whether default consumer policies will be applied.
     * 
     * @return flag result, whether the default consumer policy feature should be used by this
     *         resolver
     */
    public boolean isUseDefaultConsumerPolicy() {
        return this.useDefaultConsumerPolicy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveOperationPolicy(org.eclipse.swordfish.policytrader.OperationPolicyIdentity)
     */
    public OperationPolicy resolveOperationPolicy(final OperationPolicyIdentity opi) throws BackendException {
        try {
            return this.srProxy.getOperationPolicy(opi.getKeyName());
        } catch (Exception e) {
            throw new BackendException(e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveParticipantPolicy(org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity)
     */
    public ParticipantPolicy resolveParticipantPolicy(final ParticipantPolicyIdentity ppi) throws BackendException {
        try {
            return this.srProxy.getParticipantPolicy(ppi.getKeyName());
        } catch (Exception e) {
            throw new BackendException(e);
        }
    }

    /**
     * Will try to resolve the provided policy name onto a resource name from its internal policy
     * map. In case the name is being found, the related source will be loaded and return in a
     * ParticipantPolicy object. Throws InternalConfigurationException in case the policy can not be
     * resolved.
     * 
     * @param aPolicyName
     *        the a policy name
     * 
     * @return the string
     * 
     * @see org.eclipse.swordfish.core.components.resolver.PolicyResolver#resolvePolicyName(java.lang.String)
     */
    public String resolvePolicyID(final String aPolicyName) {

        if (aPolicyName == null) throw new ConfigurationException("The Policy name to be resolved is null.");

        if (aPolicyName.trim().equals("")) throw new ConfigurationException("The Policy name to be resolved is empty.");

        if (!this.policyMap.containsKey(aPolicyName))
            throw new ConfigurationException("A policy with the policyName \"" + aPolicyName + "\" could not be found.");

        String policyID = (String) this.policyMap.get(aPolicyName);
        return policyID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.callback.PolicyResolver#resolveServiceDescriptor(org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity)
     */
    public ServiceDescriptor resolveServiceDescriptor(final ServiceDescriptionIdentity sdi) throws BackendException {
        QName serviceName = sdi.getName();
        ServiceInfo si;
        try {
            si = this.srProxy.getServiceDescription(serviceName);
        } catch (SrProxyException e) {
            throw new BackendException(e);
        }
        PortType pt = si.getServiceDescription().getPortType(serviceName);
        List operations = pt.getOperations();
        final List operationNames = new ArrayList(operations.size());
        for (int i = 0; i < operations.size(); i++) {
            Operation operation = (Operation) operations.get(i);
            operationNames.set(i, operation.getName());
        }
        return new ServiceDescriptorImpl(operationNames);
    }

    /**
     * Set the default consumer policy name used by this resolver.
     * 
     * @param defaultConsumerPolicyName
     *        which should be used. Should be defined in the policyMap
     */
    public void setDefaultConsumerPolicyName(final String defaultConsumerPolicyName) {
        this.defaultConsumerPolicyName = defaultConsumerPolicyName;
    }

    /**
     * Sets the default policy ID.
     * 
     * @param defaultPolicyID
     *        the new default policy ID
     */
    public void setDefaultPolicyID(final String defaultPolicyID) {
        this.defaultPolicyID = defaultPolicyID;
    }

    /**
     * Sets the manager.
     * 
     * @param manager
     *        The configuration repository manager used for resolving policy description resources.
     */
    public void setManager(final ConfigurationRepositoryManagerInternal manager) {
        this.manager = manager;
    }

    /**
     * Assign the policy to definition map.
     * 
     * @param policyMap
     *        which should be used by this resolver
     */
    public void setPolicyMap(final Map policyMap) {
        this.policyMap = policyMap;
    }

    /**
     * Sets the sr proxy.
     * 
     * @param srProxy
     *        the new sr proxy
     */
    public void setSrProxy(final SrProxy srProxy) {
        this.srProxy = srProxy;
    }

    /**
     * Set whether default consumer policies will be applied.
     * 
     * @param useDefaultConsumerPolicy
     *        flag
     */
    public void setUseDefaultConsumerPolicy(final boolean useDefaultConsumerPolicy) {
        this.useDefaultConsumerPolicy = useDefaultConsumerPolicy;
    }

    /**
     * The Class ServiceDescriptorImpl.
     */
    private class ServiceDescriptorImpl implements ServiceDescriptor {

        /** The operation names. */
        private List operationNames;

        /**
         * Instantiates a new service descriptor impl.
         * 
         * @param operationNames
         *        the operation names
         */
        private ServiceDescriptorImpl(final List operationNames) {
            this.operationNames = operationNames;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.swordfish.policytrader.ServiceDescriptor#getOperationNames()
         */
        public List getOperationNames() {
            return this.operationNames;
        }
    }

}
