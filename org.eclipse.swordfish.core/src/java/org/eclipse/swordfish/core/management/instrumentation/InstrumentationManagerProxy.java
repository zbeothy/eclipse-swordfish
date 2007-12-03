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

import java.io.InputStream;
import java.util.Properties;
import javax.management.ObjectName;
import org.eclipse.swordfish.core.management.instrumentation.impl.InstrumentationManagerBean;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalInstrumentationManager;

/**
 * This proxy class is necessary so that the internal users of InsrumentationManager do not depend
 * on the presence of the papi extension jars at runtime.
 * 
 */
public class InstrumentationManagerProxy implements InternalInstrumentationManager {

    // -------------------------------------------------------------- Constants

    /** (R)evision (C)ontrol (S)ystem (Id)entifier. */
    public static final String RCS_ID = "@(#) $Id: InstrumentationManagerProxy.java,v 1.1.2.3 2007/11/09 17:47:05 kkiehne Exp $";

    // ----------------------------------------------------- Instance Variables

    /** The wrapped. */
    private InstrumentationManagerBean wrapped;

    // ------------------------------------------------------------- Properties

    /**
     * Destroy.
     */
    public void destroy() {
        this.wrapped = null;
    }

    /**
     * {@inheritDoc}
     */
    public ObjectName getObjectName(final Object instrumentation) {
        if (null == instrumentation) throw new IllegalArgumentException("Agument 'instrumentation' must not be null!");
        return (null == this.wrapped) ? null : this.wrapped.getObjectName(instrumentation);
    }

    // ------------------------------------------------------ Lifecycle Methods

    /**
     * {@inheritDoc}
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#registerInstrumentation(java.lang.Object,java.io.InputStream)
     */
    public void registerInstrumentation(final Object arg0, final InputStream arg1) throws InternalInfrastructureException,
            InternalAlreadyRegisteredException {
        try {
            this.wrapped.registerInstrumentation(arg0, arg1);
        } catch (org.eclipse.swordfish.core.management.instrumentation.AlreadyRegisteredException e) {
            throw new InternalAlreadyRegisteredException(e.getMessage(), e.getComponent());
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    public void registerInstrumentation(final Object arg0, final InputStream arg1, final Properties arg2)
            throws InternalAlreadyRegisteredException, InternalInfrastructureException {
        try {
            this.wrapped.registerInstrumentation(arg0, arg1, arg2);
        } catch (AlreadyRegisteredException e) {
            throw new InternalAlreadyRegisteredException(e.getMessage(), e.getComponent());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws ParticipantHandlingException
     * @throws InternalAlreadyRegisteredException
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#registerInstrumentation(java.lang.Object,java.io.InputStream,java.util.Properties,java.lang.String)
     */
    public void registerInstrumentation(final Object arg0, final InputStream arg1, final Properties arg2, final String arg3)
            throws InternalInfrastructureException, InternalAlreadyRegisteredException {
        try {
            this.wrapped.registerInstrumentation(arg0, arg1, arg2, arg3);
        } catch (AlreadyRegisteredException e) {
            throw new InternalAlreadyRegisteredException(e.getMessage(), e.getComponent());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws InternalAlreadyRegisteredException
     * @throws ParticipantHandlingException
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#registerInstrumentation(java.lang.Object,java.io.InputStream,java.lang.String)
     */
    public void registerInstrumentation(final Object arg0, final InputStream arg1, final String arg2)
            throws InternalAlreadyRegisteredException, InternalInfrastructureException {
        try {
            this.wrapped.registerInstrumentation(arg0, arg1, arg2);
        } catch (AlreadyRegisteredException e) {
            throw new InternalAlreadyRegisteredException(e.getMessage(), e.getComponent());
        }
    }

    /**
     * Sets the wrapped.
     * 
     * @param wrapped
     *        the new wrapped
     */
    public void setWrapped(final InstrumentationManagerBean wrapped) {
        this.wrapped = wrapped;
        this.wrapped.setSpecifier("participant");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.papi.extension.instrumentation.InternalInstrumentationManager#unregisterInstrumentation(java.lang.Object)
     */
    public boolean unregisterInstrumentation(final Object arg0) throws InternalInfrastructureException {
        return this.wrapped.unregisterInstrumentation(arg0);
    }

}
