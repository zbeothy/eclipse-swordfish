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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.component.ComponentLifeCycle;
import javax.management.ObjectName;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentRepository;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnit;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitState;
import org.eclipse.swordfish.core.components.deploymentmanagement.DeploymentUnitType;
import org.eclipse.swordfish.core.components.deploymentmanagement.impl.ConfigurationDeploymentUnitHandler;
import org.eclipse.swordfish.core.components.deploymentmanagement.impl.DeploymentRepositoryImpl;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.components.jbi.impl.ComponentContextAccessBean;
import org.eclipse.swordfish.core.components.messaging.DeliveryChannelListener;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.utils.DumpeableParticipantIdentity;
import org.eclipse.swordfish.core.utils.Dumper;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.InternalSBB;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class defined the lifeCycle of the InternalSBB engine, which is controlled through the JBI
 * container.
 */
public class LifeCycleImpl implements ComponentLifeCycle {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: LifeCycleImpl.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    /** logger to be used. */
    private static final Log LOG = SBBLogFactory.getLog(LifeCycleImpl.class);

    /** InternalState if not configured yet. */
    private static final int NOT_CONFIGURED = 0;

    /** InternalState for minimal configuration. */
    private static final int MINIMAL_CONFIGURED = 2;

    /** state for complete configuration. */
    private static final int CONFIGURED = 4;

    /** the name of the very parent Spring context Object wiring file. */
    private static final String ENGINE_RESOURCE_FILE_NAME = "EngineConfig.xml";

    /** the name of InternalSBB core engines Object wiring file. */
    private static final String SBB_RESOURCE_FILE_NAME = "SBBConfiguration.xml";

    // ----------------------------------------------------- Instance Variables

    /** Attribute with current state. */
    private int currentState;

    /** the component this context belongs to. */
    private ComponentImpl parent;

    /** this objects private place holder for the component context. */
    private ComponentContext componentContext;

    /**
     * indicates whether the engine has been started once in this VM. this is false until we really
     * start the core at least once. before this variable becomes true the engine will be able to
     * handle deployments but not to do anything else
     */
    private boolean onceStarted;

    /** Engine Context Bean Factory. */
    private ClassPathXmlApplicationContext engineCtx;

    /** internal participant. */
    private InternalSBB internalParticipantSBB = null;

    // ----------------------------------------------------------- Constructors

    /**
     * public constructor.
     * 
     * @param creatingComponent
     *        the parent component
     */
    public LifeCycleImpl(final ComponentImpl creatingComponent) {
        this.parent = creatingComponent;
        this.currentState = NOT_CONFIGURED;
        this.onceStarted = false;
    }

    // ------------------------------------------------------------- Properties

    /**
     * Gets the extension M bean name.
     * 
     * @return the extension M bean name
     * 
     * @see javax.jbi.component.ComponentLifeCycle#getExtensionMBeanName()
     */
    public ObjectName getExtensionMBeanName() {
        return null;
    }

    // ------------------------------------------------------ Lifecycle Methods

    /**
     * the engine life cycle initialization is independent from spring to be able to handle initial
     * deployments.
     * 
     * @param context
     *        the context
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentLifeCycle#init(javax.jbi.component.ComponentContext)
     */
    public void init(final ComponentContext context) throws JBIException {
        try {
            this.componentContext = context;
            String baseWorkDir =
                    System.getProperty("SOPware.ServiceBackbone.WorkspaceRoot", System.getProperty(
                            "org.eclipse.swordfish.sbb.workspaceroot", context.getWorkspaceRoot()));
            String baseInstallDir =
                    System.getProperty("SOPware.ServiceBackbone.InstallRoot", System.getProperty(
                            "org.eclipse.swordfish.sbb.installroot", context.getInstallRoot()));
            DeploymentRepository repository = new DeploymentRepositoryImpl(baseWorkDir);
            Collection col = repository.getOutOfSyncDeplyoments();
            if (col.size() > 0) {
                LOG.warn("InternalSBB deployment respository out of sync, trying to sync.");
                Iterator iter = col.iterator();
                while (iter.hasNext()) {
                    DeploymentUnit unit = (DeploymentUnit) iter.next();
                    repository.removeDeploymentUnit(unit.getName());
                    LOG.info("Removed for sync reasons: " + unit.getName());
                }
            }
            ConfigurationDeploymentUnitHandler bootstrapHandler =
                    new ConfigurationDeploymentUnitHandler(baseInstallDir + File.separator + "conf" + File.separator + "SBB",
                            baseWorkDir + File.separator + "conf" + File.separator + "SBB", null);
            try {
                bootstrapHandler.deploy();
            } catch (InternalInfrastructureException e1) {
                LOG.error("Issues dealing with the initial installation.", e1);
            }
            col = repository.getDeploymentUnitNames();
            if (!col.isEmpty()) {
                LOG.info("deploying existing configurations.");
            }
            Iterator iter = col.iterator();
            while (iter.hasNext()) {
                DeploymentUnit unit = repository.getDeploymentUnit((String) iter.next());
                if (unit.getType().equals(DeploymentUnitType.CONFIGURATION)) {
                    try {
                        unit.getHandler().deploy();
                        unit.setState(DeploymentUnitState.STARTED);
                    } catch (InternalInfrastructureException e) {
                        LOG.error("failed to restart the deployment " + unit.getName(), e);
                    }
                }
            }
            this.parent.setDeploymentRepository(repository);
            LOG.info("InternalSBB Engine initialized for startup.");
        } catch (Exception e) {
            LOG.error("unexpected exception occured of type '" + e.getClass().getName() + "'.", e);
            new RuntimeException("unexpected exception.", e);
        }
    }

    /**
     * Shut down.
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentLifeCycle#shutDown()
     */
    public void shutDown() throws JBIException {
        LOG.debug("stopping internal processors.");
        if (this.internalParticipantSBB != null) {
            try {
                this.internalParticipantSBB.release();
            } catch (Exception e) {
                final DumpeableParticipantIdentity pid =
                        DumpeableParticipantIdentity.decorate(
                                this.internalParticipantSBB.getEnvironment().getParticipantIdentity(), new Dumper() {

                                    public String dump(InternalParticipantIdentity ip) {
                                        return ip.getApplicationID() + '@' + ip.getInstanceID();
                                    }
                                });
                LOG.warn("Error releasing internal InternalSBB[[" + pid.dump() + "] instance");
            }
            this.internalParticipantSBB = null;
        }
        if (this.parent.getApplicationContext() != null) {
            DeliveryChannelListener listener =
                    ((DeliveryChannelListener) this.parent.getApplicationContext().getBean(DeliveryChannelListener.ROLE));
            listener.shutdown();
        } else {
            LOG.error("Engine context is null, it has not been loaded during startup");
        }
        this.parent.release();
        this.engineCtx.destroy();
        this.engineCtx.close();
        LogFactory.releaseAll();
        SBBLogFactory.releaseAll();
        this.engineCtx = null;
        this.componentContext = null;
        LOG.debug("shutdown completed");
    }

    /**
     * Start.
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentLifeCycle#start()
     */
    public void start() throws JBIException {
        ClassPathXmlApplicationContext sbbCtx = null;
        try {
            if (this.onceStarted) {
                this.doStartComponents();
                return;
            }
            this.currentState = this.examineInstallation();
            if ((this.engineCtx == null) && ((this.currentState & MINIMAL_CONFIGURED) == MINIMAL_CONFIGURED)) {
                LOG.debug("loading engines minimal context");
                ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                try {
                    this.engineCtx = new ClassPathXmlApplicationContext(ENGINE_RESOURCE_FILE_NAME);
                    ((ComponentContextAccessBean) this.engineCtx.getBean(ComponentContextAccess.ROLE))
                        .setComponentContext(this.componentContext);
                } catch (Throwable e) {
                    e.printStackTrace();
                    LOG.error("cannot load engine context", e);
                } finally {
                    Thread.currentThread().setContextClassLoader(currThreadLoader);
                }
            }
            if ((this.parent.getApplicationContext() == null) && ((this.currentState & CONFIGURED) == CONFIGURED)) {
                LOG.debug("loading engine's private framework");
                ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                try {
                    sbbCtx = new ClassPathXmlApplicationContext(new String[] {SBB_RESOURCE_FILE_NAME}, this.engineCtx);
                } catch (Throwable t) {
                    t.printStackTrace();
                    LOG.error("cannot load sbb context", t);
                } finally {
                    Thread.currentThread().setContextClassLoader(currThreadLoader);
                }
                DeliveryChannelListener listener = ((DeliveryChannelListener) sbbCtx.getBean(DeliveryChannelListener.ROLE));
                listener.init();
                this.parent.setApplicationContext(sbbCtx);
                this.doStartComponents();
                this.onceStarted = true;
            }
            if (sbbCtx == null) {
                LOG
                    .info("Engine started in deployment mode as the configuration for a full start is not found.\n"
                            + "You will not be able to retrieve InternalSBB instances but to deploy configurations to complete the installation.");
            }
        } catch (Exception e) {
            LOG.error("unexpected exception occured of type '" + e.getClass().getName() + "'.", e);
            new RuntimeException("unexpected exception.", e);
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Stop.
     * 
     * @throws JBIException
     * 
     * @see javax.jbi.component.ComponentLifeCycle#stop()
     */
    public void stop() throws JBIException {
        if (this.onceStarted) {
            LOG.debug("stopping");
            DeliveryChannelListener listener =
                    ((DeliveryChannelListener) this.parent.getApplicationContext().getBean(DeliveryChannelListener.ROLE));
            listener.stop();
            LOG.debug("stopped");
        }
    }

    // -------------------------------------------------------- Private Methods

    /**
     * checks the existance and readability of a file in the workspace root checks the existance and
     * readability of a given file.
     * 
     * @param fileName
     *        is the name of the file in the workspace
     * 
     * @return whether the file is readable
     */
    private boolean checkFile(final String fileName) {
        File f = new File(fileName);
        boolean res = f.exists() & f.canRead();
        return res;
    }

    /**
     * checks the existance and readability of a file in the install root.
     * 
     * @param fileName
     *        the file name
     * 
     * @return whether the file exists
     */
    private boolean checkInstallRootFile(final String fileName) {
        String baseWorkDir = System.getProperty("SOPware.ServiceBackbone.WorkspaceRoot", this.componentContext.getWorkspaceRoot());
        return this.checkFile(baseWorkDir + File.separator + fileName);
    }

    /**
     * checks the existance and readability of a file in the install root.
     * 
     * @param resourceName
     *        the resource name
     * 
     * @return whether the file exists
     */
    private boolean checkInstallRootResource(final String resourceName) {
        return this.checkResource(resourceName);
    }

    /**
     * checks the existance and readability of a resource in the classpath of the engine.
     * 
     * @param resourceName
     *        is the name of the file in the workspace
     * 
     * @return whether the file is readable
     */
    private boolean checkResource(final String resourceName) {
        boolean res = false;
        if (null != this.getClass().getClassLoader().getResource(resourceName)) {
            res = true;
        }
        return res;
    }

    /**
     * Do start components.
     */
    private void doStartComponents() {
        LOG.debug("starting");
        DeliveryChannelListener listener =
                ((DeliveryChannelListener) this.parent.getApplicationContext().getBean(DeliveryChannelListener.ROLE));
        listener.start();
        LOG.debug("started");
    }

    /**
     * Examine installation.
     * 
     * @return the state of the current configuration based on the existance of some files we assume
     *         to exist
     */
    private int examineInstallation() {
        int state = NOT_CONFIGURED;
        if (this.checkInstallRootResource(ENGINE_RESOURCE_FILE_NAME) & this.checkInstallRootResource(SBB_RESOURCE_FILE_NAME)) {
            state = state | MINIMAL_CONFIGURED;
            if (this.checkInstallRootFile("conf" + File.separator + "SBB" + File.separator + "bootstrap_cfg.xml")) {
                state = state | CONFIGURED;
            }
        }
        return state;
    }

    // private void deleteRecurse(final File aFile) {
    // if (aFile.isDirectory()) {
    // File[] fc = aFile.listFiles();
    // for (int i = 0; i < fc.length; i++) {
    // deleteRecurse(fc[i]);
    // }
    // }
    // aFile.delete();
    // }
}
