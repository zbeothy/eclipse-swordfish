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
package org.eclipse.swordfish.core.components.iapi.impl;

import java.util.Observer;
import org.eclipse.swordfish.configrepos.ConfigurationRepositoryManagerInternal;
import org.eclipse.swordfish.core.components.iapi.InternalSBBFactory;
import org.eclipse.swordfish.core.components.instancemanager.AssociationException;
import org.eclipse.swordfish.core.components.instancemanager.InstanceManager;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.papi.impl.untyped.SBBImpl;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.InternalSBB;
import org.eclipse.swordfish.papi.internal.exception.SBBRuntimeException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The Class InternalSBBFactoryBean.
 * 
 */
public class InternalSBBFactoryBean implements InternalSBBFactory, ApplicationContextAware {

    /** Spring configuration file for instance. */
    private static final String INSTANCE_RESOURCE_FILENAME = "InstanceConfiguration.xml";

    /** Spring configuration file for instance. */
    private static final String INSTANCE_BASE_RESOURCE_FILENAME = "InstanceBaseConfiguration.xml";

    /** Private log of the factory. */
    private static final Log LOG = SBBLogFactory.getLog(InternalSBBFactoryBean.class);

    /**
     * Spring ApplicationContext this bean lives in. This value is injected by the framework after
     * creation.
     */
    private AbstractApplicationContext sbbCtx = null;

    /** the manager for the InternalSBB instances. */
    private InstanceManager instanceManager;

    /**
     * Adds an observer to the internal instance manager.
     * 
     * @param observer
     *        the observer
     * 
     * @see org.eclipse.swordfish.core.components.iapi.InternalSBBFactory#addObserver(java.util.Observer)
     */
    public void addObserver(final Observer observer) {
        this.instanceManager.addObserver(observer);
    }

    /**
     * Create an InternalSBB.
     * 
     * @param identity
     *        the identity
     * 
     * @return the InternalSBB
     * 
     * @see org.eclipse.swordfish.core.components.iapi.InternalSBBFactory#createSBB(org.eclipse.swordfish.papi.InternalParticipantIdentity)
     */
    public synchronized InternalSBB createSBB(final InternalParticipantIdentity identity) {

        if (identity == null) throw new SBBRuntimeException("participant identity must not be null");
        if ((identity.getApplicationID() == null) && !(identity instanceof InternalParticipantIdentity))
            throw new SBBRuntimeException("participant identity's ApplicationId must not be null");

        InternalSBB theSBB = null;
        theSBB = this.instanceManager.query(identity);
        if (theSBB != null)
            return theSBB;
        else {
            LOG.info("Creating InternalSBB instance for participant " + "[" + identity.getApplicationID() + ","
                    + identity.getInstanceID() + "]");

            AbstractApplicationContext instanceCtx = null;
            AbstractApplicationContext instanceBaseCtx = null;
            // changing the context classloader of the current thread to class
            // classloader
            // reason is in Web container enviornment, the context class loader
            // we get
            // here is application class loader and spring uses context class
            // loader to load
            // beans, after loading the bean we change it back to old context
            // class loader.

            ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                LOG.debug("Loading InstanceBase beans config for participant " + "[" + identity.getApplicationID() + ","
                        + identity.getInstanceID() + "]");
                instanceBaseCtx =
                        new ClassPathXmlApplicationContext(new String[] {"classpath:" + INSTANCE_BASE_RESOURCE_FILENAME},
                                this.sbbCtx);

                ConfigurationRepositoryManagerInternal configmngr =
                        (ConfigurationRepositoryManagerInternal) instanceBaseCtx.getBean("sbb_instance_configmngr");
                if (configmngr != null) {
                    configmngr.setParticipantId(identity.getApplicationID(), identity.getInstanceID());
                }

                LOG.debug("Loading Instance beans config for participant " + "[" + identity.getApplicationID() + ","
                        + identity.getInstanceID() + "]");
                instanceCtx =
                        new ClassPathXmlApplicationContext(new String[] {"classpath:" + INSTANCE_RESOURCE_FILENAME},
                                instanceBaseCtx);

            } catch (Exception e) {
                throw new SBBRuntimeException(e);
            } finally {
                Thread.currentThread().setContextClassLoader(currThreadLoader);
            }
            theSBB = new SBBImpl(instanceBaseCtx, instanceCtx, identity, this.instanceManager);

            try {
                this.instanceManager.associate(theSBB, identity);
                LOG.info("InternalSBB instance created for participant " + "[" + identity.getApplicationID() + ","
                        + identity.getInstanceID() + "]");

                return theSBB;
            } catch (AssociationException e) {
                throw new RuntimeException("association of the participant identity went wrong", e);
            }
        }
    }

    /**
     * Set the spring application context.
     * 
     * @param anApplicationContext
     *        the an application context
     * 
     * @throws BeansException
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(final ApplicationContext anApplicationContext) throws BeansException {
        this.sbbCtx = (AbstractApplicationContext) anApplicationContext;

    }

    /**
     * Set the intance manager for this InternalSBB facade.
     * 
     * @param instanceManager
     *        which should be referenced by this entity
     */
    public void setInstanceManager(final InstanceManager instanceManager) {
        this.instanceManager = instanceManager;
    }

}
