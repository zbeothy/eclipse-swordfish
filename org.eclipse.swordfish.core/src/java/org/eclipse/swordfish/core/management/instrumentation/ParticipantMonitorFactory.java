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
package org.eclipse.swordfish.core.management.instrumentation;

import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.core.management.instrumentation.impl.ParticipantMonitorBean;
import org.eclipse.swordfish.core.management.objectname.ObjectNameFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A factory for creating InternalParticipantMonitor objects.
 */
public class ParticipantMonitorFactory implements ExtensionFactory, ApplicationContextAware {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: ParticipantMonitorFactory.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    // ----------------------------------------------------- Instance Variables

    /** The instance. */
    private Object instance = null;

    /** The manager. */
    private InstrumentationManagerBean manager;

    /** The factory. */
    private ObjectNameFactory factory;

    // ------------------------------------------------------------- Properties

    /**
     * Destroy.
     */
    public void destroy() {
        ((ParticipantMonitorBean) this.instance).destroy();
        this.instance = null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getInstance(final String diversifier) {
        if (null == this.instance) {
            this.instance = this.createInstance();
        }
        return this.instance;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext arg0) throws BeansException {
    }

    /**
     * Sets the instrumentation manager.
     * 
     * @param mbManager
     *        the new instrumentation manager
     */
    public void setInstrumentationManager(final InstrumentationManagerBean mbManager) {
        this.manager = mbManager;
    }

    // ------------------------------------------------------ Lifecycle Methods

    /**
     * Sets the object name factory.
     * 
     * @param objFactory
     *        the new object name factory
     */
    public void setObjectNameFactory(final ObjectNameFactory objFactory) {
        this.factory = objFactory;
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Creates a new InternalParticipantMonitor object.
     * 
     * @return the object
     */
    private Object createInstance() {
        ParticipantMonitorBean ret = new ParticipantMonitorBean();
        ret.setInstrumentationManager(this.manager);
        ret.setObjectNameFactory(this.factory);
        return ret;
    }

}
