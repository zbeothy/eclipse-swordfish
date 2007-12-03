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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.endpointmanager.LocalEndpointRepository;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescriptionFactory;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * The Class CompoundServiceDescriptionFactoryBean.
 */
public class CompoundServiceDescriptionFactoryBean implements CompoundServiceDescriptionFactory {

    /** The Constant log. */
    private static final Log LOG = SBBLogFactory.getLog(CompoundServiceDescriptionFactoryBean.class);

    /** The kernel. */
    private Kernel kernel;

    /** The local endpoint repository. */
    private LocalEndpointRepository localEndpointRepository;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescriptionFactory#createCompoundServiceDescription(javax.wsdl.Definition,
     *      javax.wsdl.Definition, org.eclipse.swordfish.policytrader.AgreedPolicy)
     */
    public CompoundServiceDescription createCompoundServiceDescription(final Definition sdx, final Definition spdx,
            final AgreedPolicy agreedPolicy) {
        return this.createCompoundServiceDescription(sdx, spdx, agreedPolicy, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.resolver.CompoundServiceDescriptionFactory#createCompoundServiceDescription(javax.wsdl.Definition,
     *      javax.wsdl.Definition, org.eclipse.swordfish.policytrader.AgreedPolicy, java.util.List)
     */
    public CompoundServiceDescription createCompoundServiceDescription(final Definition sdx, final Definition spdx,
            final AgreedPolicy agreedPolicy, final List/* <ParticipantPolicy> */providerPolicies) {
        CompoundServiceDescriptionImpl csd = null;
        try {
            // now we can create the compound description
            csd = new CompoundServiceDescriptionImpl(sdx, spdx, agreedPolicy, providerPolicies, this.getKernel().getParticipant());

            // and now we figure out where the service definition of the
            // callback
            // endpoint is defined.
            QName portType = csd.getPartnerPortTypeQName();
            if (csd.hasPartnerDescription()) {
                Service service = this.getLocalEndpointRepository().getLocalCallbackDefinition(portType);
                if (null != service) {
                    // BUGFIX preparation: 2096
                    // clone the services before setting them into csd. do
                    // not share any possible mutable state.
                    csd.setCallbackService((Service) this.cloneDescriptionElement(service));
                } else {
                    LOG.info("Service has one or more request/callback operation(s), "
                            + "but no callback service is defined in configuration for " + portType.toString()
                            + ". If this occurs on the consumer side, the Callback " + "Service will not be available.");
                }

            }
        } catch (Exception e) {
            LOG.error("cannot create a compound ServiceDescription: " + e.getClass().getName() + ": " + e.getMessage(), e);
        }
        return csd;
    }

    /**
     * destroy method.
     */
    public void destroy() {
        this.kernel = null;
        this.localEndpointRepository = null;
    }

    /**
     * Gets the kernel.
     * 
     * @return the kernel
     */
    public Kernel getKernel() {
        return this.kernel;
    }

    /**
     * Gets the local endpoint repository.
     * 
     * @return the local endpoint repository
     */
    public LocalEndpointRepository getLocalEndpointRepository() {
        return this.localEndpointRepository;
    }

    /**
     * Sets the kernel.
     * 
     * @param kernel
     *        the new kernel
     */
    public void setKernel(final Kernel kernel) {
        this.kernel = kernel;
    }

    /**
     * Sets the local endpoint repository.
     * 
     * @param localEndpointRepository
     *        the new local endpoint repository
     */
    public void setLocalEndpointRepository(final LocalEndpointRepository localEndpointRepository) {
        this.localEndpointRepository = localEndpointRepository;
    }

    /**
     * Clone description element.
     * 
     * @param service
     *        the service
     * 
     * @return the object
     */
    private Object cloneDescriptionElement(final Object service) {
        Object cloned;
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            ObjectOutputStream oas = new ObjectOutputStream(bas);
            oas.writeObject(service);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bas.toByteArray()));
            cloned = ois.readObject();
        } catch (IOException e) {
            throw new ComponentRuntimeException("cannot clone servive description to build a compound servicedescription");
        } catch (ClassNotFoundException e) {
            throw new ComponentRuntimeException("cannot find WSDLService class clone servive description.This should never happen");
        }
        return cloned;
    }

}
