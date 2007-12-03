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
package org.eclipse.swordfish.core.management.instrumentation.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.management.ObjectName;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;
import org.eclipse.swordfish.core.management.objectname.ObjectNameFactory;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalMonitorable;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalParticipantMonitor;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalUnknownComponentException;

/**
 * The Class ParticipantMonitorBean.
 */
public class ParticipantMonitorBean implements InternalParticipantMonitor {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: ParticipantMonitorBean.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ParticipantMonitorBean.class);

    // ----------------------------------------------------- Instance Variables

    /** currently registered monitorables. */
    private HashMap monitorables;

    /** The instrumentation manager. */
    private InstrumentationManagerBean instrumentationManager;

    /** InputStream containing the MBean description. */
    private InputStream desc;

    /** The object name factory. */
    private ObjectNameFactory objectNameFactory;

    /** The default name properties. */
    private Properties defaultNameProperties;

    /** The domain parts. */
    private List domainParts;

    // ----------------------------------------------------------- Constructors

    /**
     * Instantiates a new participant monitor bean.
     */
    public ParticipantMonitorBean() {
        this.monitorables = new HashMap();
        this.domainParts = new Vector(1);
        this.domainParts.add("participant");
        this.defaultNameProperties = new Properties();
        this.defaultNameProperties.put("type", "ParticipantMonitor");
        this.initDesc();
    }

    // ------------------------------------------------------------- Properties

    /**
     * Destroy.
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy");
        }
        if (null != this.monitorables) {
            LOG.info("Still monitorables for participant " + this.instrumentationManager.getParticipant()
                    + " registered on shutdown - deregistering");
            for (Iterator iter = this.monitorables.values().iterator(); iter.hasNext();) {
                MonitorableWrapper wrapper = (MonitorableWrapper) iter.next();
                try {
                    this.instrumentationManager.unregisterInstrumentation(wrapper);
                } catch (InternalInfrastructureException e) {
                    LOG.info("Unexpected exception when trying to unregister monitor for participant.", e);
                }
            }
            this.monitorables.clear();
            this.monitorables = null;
        }
        this.instrumentationManager = null;
        if (LOG.isTraceEnabled()) {
            LOG.trace("destroyed");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalParticipantMonitor#getComponents()
     */
    public List getComponents() {
        List ret = new Vector(this.monitorables.keySet());
        return ret;
    }

    /**
     * (non-Javadoc).
     * 
     * @param monitorable
     *        the monitorable
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalParticipantMonitor#register(org.eclipse.swordfish.papi.extension.instrumentation.InternalMonitorable)
     */
    public void register(final InternalMonitorable monitorable) throws InternalInfrastructureException,
            InternalAlreadyRegisteredException {
        this.register(monitorable, this.defaultNameProperties);
    }

    // ------------------------------------------------------ Lifecycle Methods

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalParticipantMonitor#register(org.eclipse.swordfish.papi.extension.instrumentation.InternalMonitorable,java.util.Properties)
     */
    public void register(final InternalMonitorable monitorable, final Properties nameProperties)
            throws InternalInfrastructureException, InternalAlreadyRegisteredException {
        if (!nameProperties.contains("type")) {
            nameProperties.put("type", "ParticipantMonitor");
        }
        ObjectName on = this.objectNameFactory.getObjectName(monitorable, this.domainParts, nameProperties);
        this.registerInternal(monitorable, on);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Sets the instrumentation manager.
     * 
     * @param manager
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManagerBean manager) {
        this.instrumentationManager = manager;
        try {
            manager.registerInstrumentationDescription(this.desc);
        } catch (Exception e) {
            LOG.error("Unexpected exception trying to register MBean description - ParticipantMonitor will be inoperational.\n"
                    + e.toString());
        }
    }

    /**
     * Sets the object name factory.
     * 
     * @param factory
     *        the new object name factory
     */
    public void setObjectNameFactory(final ObjectNameFactory factory) {
        this.objectNameFactory = factory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalParticipantMonitor#unregister(org.eclipse.swordfish.papi.extension.instrumentation.InternalMonitorable)
     */
    public void unregister(final InternalMonitorable monitorable) throws InternalInfrastructureException,
            InternalUnknownComponentException {
        if (!(this.monitorables.keySet().contains(monitorable)))
            throw new InternalUnknownComponentException("Component is not registered as monitorable", monitorable);
        MonitorableWrapper wrapper = (MonitorableWrapper) this.monitorables.remove(monitorable);
        this.instrumentationManager.unregisterInstrumentation(wrapper);
    }

    // -------------------------------------------------------- Private Methods

    /**
     * initializes the <code>InputStream</code> for the MBean description.
     */
    private void initDesc() {
        this.desc =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/management/instrumentation/impl/MonitorableDesc.xml");
        this.desc.mark(Integer.MAX_VALUE);
    }

    /**
     * Register internal.
     * 
     * @param monitorable
     *        the monitorable
     * @param on
     *        the on
     * 
     * @throws InternalAlreadyRegisteredException
     * @throws ParticipantHandlingException
     */
    private void registerInternal(final InternalMonitorable monitorable, final ObjectName on)
            throws InternalAlreadyRegisteredException, InternalInfrastructureException {
        if (this.monitorables.containsKey(monitorable))
            throw new InternalAlreadyRegisteredException("Component already registered.", monitorable);
        MonitorableWrapper wrapper = new MonitorableWrapper(monitorable);
        try {
            this.instrumentationManager.registerInstrumentation(wrapper, on);
            this.monitorables.put(monitorable, wrapper);
        } catch (Exception e) {
            LOG.error("Unexpected exception trying to register " + monitorable
                    + "\n The component will not be monitored. Reason: \n" + e.toString());
            throw new InternalInfrastructureException("While trying to register " + monitorable, e);
        }
    }

}
