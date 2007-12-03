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
package org.eclipse.swordfish.core.engine;

import javax.jbi.component.Component;
import javax.jbi.component.ComponentLifeCycle;
import javax.jbi.component.ServiceUnitManager;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.servicedesc.ServiceEndpoint;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentRepository;
import org.eclipse.swordfish.core.components.endpointmanager.EndpointManager;
import org.eclipse.swordfish.core.components.endpointreferenceresolver.EndpointReferenceResolver;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;
import org.springframework.context.support.AbstractApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

/**
 * InternalSBB Engine main implementation. In JBI terminology it simply represents a component.
 */
public class ComponentImpl implements Component {

    /** the Spring application context associated with the component. */
    private AbstractApplicationContext applicationContext;

    /** the JBI lifecycle that this component uses (main InternalSBB object). */
    private LifeCycleImpl lifeCycle;

    /**
     * the deployment repository for this component, it is created by the lifecycle at its
     * initialization time.
     */
    private DeploymentRepository deploymentRepositoty;

    /** this objects service Unit manager. */
    private ServiceUnitManager suManager;

    /**
     * used by JBI container to instantiate the component.
     */
    public ComponentImpl() {
        this.suManager = null;
        this.lifeCycle = new LifeCycleImpl(this);
    }

    /**
     * Gets the application context.
     * 
     * @return -- the Spring application Context
     */
    public AbstractApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    /**
     * Gets the life cycle.
     * 
     * @return the life cycle
     * 
     * @see javax.jbi.component.Component#getLifeCycle()
     */
    public ComponentLifeCycle getLifeCycle() {
        return this.lifeCycle;
    }

    /**
     * Gets the service description.
     * 
     * @param endpoint
     *        the endpoint
     * 
     * @return the service description
     * 
     * @see javax.jbi.component.Component#getServiceDescription(javax.jbi.servicedesc.ServiceEndpoint)
     */
    public Document getServiceDescription(final ServiceEndpoint endpoint) {
        EndpointManager epm =
                (EndpointManager) this.applicationContext
                    .getBean("org.eclipse.swordfish.core.components.endpointmanager.EndpointManager");
        CompoundServiceDescription csd = epm.getServiceDescription(endpoint);
        return csd.createWSDL();
    }

    /**
     * Gets the service unit manager.
     * 
     * @return the service unit manager
     * 
     * @see javax.jbi.component.Component#getServiceUnitManager()
     */
    public ServiceUnitManager getServiceUnitManager() {
        if (this.suManager == null) {
            this.suManager = new ServiceUnitManagerImpl(this.deploymentRepositoty);
        }
        return this.suManager;
    }

    /**
     * Checks if is exchange with consumer okay.
     * 
     * @param endpoint
     *        the endpoint
     * @param mep
     *        the mep
     * 
     * @return true, if is exchange with consumer okay
     * 
     * @see javax.jbi.component.Component#isExchangeWithConsumerOkay(javax.jbi.servicedesc.ServiceEndpoint,
     *      javax.jbi.messaging.MessageExchange)
     */
    public boolean isExchangeWithConsumerOkay(final ServiceEndpoint endpoint, final MessageExchange mep) {
        // TODO This is postponed for JSR 265 intergration into JSR 208
        return false;
    }

    /**
     * Checks if is exchange with provider okay.
     * 
     * @param endpoint
     *        the endpoint
     * @param mep
     *        the mep
     * 
     * @return true, if is exchange with provider okay
     * 
     * @see javax.jbi.component.Component#isExchangeWithProviderOkay(javax.jbi.servicedesc.ServiceEndpoint,
     *      javax.jbi.messaging.MessageExchange)
     */
    public boolean isExchangeWithProviderOkay(final ServiceEndpoint endpoint, final MessageExchange mep) {
        // TODO This is postponed for JSR 265 intergration into JSR 208
        return false;
    }

    /**
     * kills the Spring application context, so this component becomes fully invalid.
     */
    public void release() {
        this.applicationContext.destroy();
        this.applicationContext.close();
        this.applicationContext = null;

        this.deploymentRepositoty = null;
        this.lifeCycle = null;
        this.suManager = null;
    }

    /**
     * Resolve endpoint reference.
     * 
     * @param dynamicEndpointDescription
     *        the dynamic endpoint description
     * 
     * @return the service endpoint
     * 
     * @see javax.jbi.component.Component#resolveEndpointReference(org.w3c.dom.DocumentFragment)
     */
    public ServiceEndpoint resolveEndpointReference(final DocumentFragment dynamicEndpointDescription) {
        EndpointReferenceResolver eprr =
                (EndpointReferenceResolver) this.applicationContext
                    .getBean("org.eclipse.swordfish.core.components.endpointreferenceresolver.EndpointReferenceResolver");
        return eprr.resolveEndpointReference(dynamicEndpointDescription);
    }

    /**
     * sets the overall Spring application context for the component.
     * 
     * @param appCtx
     *        the spring application context for the InternalSBB core
     */
    public void setApplicationContext(final AbstractApplicationContext appCtx) {
        this.applicationContext = appCtx;
    }

    /**
     * sets the deployment repository which is used by the InternalSBB engine. The valus is injected
     * through the lifecycle instance.
     * 
     * @param repos
     *        the DeploymentRepository to set
     */
    public void setDeploymentRepository(final DeploymentRepository repos) {
        this.deploymentRepositoty = repos;
    }
}
