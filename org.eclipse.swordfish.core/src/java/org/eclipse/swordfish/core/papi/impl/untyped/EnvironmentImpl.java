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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.components.tracehandler.TraceHandler;
import org.eclipse.swordfish.core.components.tracehandler.TraceHandlerFactory;
import org.eclipse.swordfish.core.exception.ComponentRuntimeException;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.InternalEnvironment;
import org.eclipse.swordfish.papi.internal.InternalParticipantIdentity;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.untyped.InternalCallContext;
import org.eclipse.swordfish.papi.internal.untyped.InternalMessageFactory;

/**
 * An implementation of the InternalSBB environment object.
 */
public class EnvironmentImpl implements InternalEnvironment {

    /**
     * the identity of the particiant which has constructed the InternalSBB hosted in this
     * environment.
     */
    private UnifiedParticipantIdentity ident;

    /** the InternalMessageFactory that is accessible through this environment. */
    private InternalMessageFactory msgFactory;

    /**
     * the InternalSBB extension for this environment. Mainly used to access kernel functionality.
     */
    private SBBExtension sbb;

    /** this is a tracehandler for this InternalSBB instance. */
    private Map traceHandlerMap;

    /**
     * default constructor.
     * 
     * @param identity
     *        the identity of the particioant, which determines the InternalSBB
     * @param theSbb
     *        the sbb instance this environment belongs to. instance uniquely
     */
    public EnvironmentImpl(final UnifiedParticipantIdentity identity, final SBBExtension theSbb) {
        if (identity == null) throw new ComponentRuntimeException("participant identity must not be null");
        this.ident = identity;
        this.msgFactory = new MessageFactoryImpl();
        this.sbb = theSbb;
        this.traceHandlerMap = new HashMap();
    }

    /**
     * Adds the InternalSBB trace destination.
     * 
     * @param aLogger
     *        the a logger
     * 
     * @return true, if add InternalSBB trace destination
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment#addSBBTraceDestination(java.lang.Object)
     *      FIXME implement other logger types
     */
    public boolean addSBBTraceDestination(final Object aLogger) {
        // don't do anything for the loggers we already have
        if (this.traceHandlerMap.containsKey(aLogger)) return false;
        TraceHandler handler = null;
        try {
            // try to retrive a trace handler that is
            handler = TraceHandlerFactory.createTraceHandler(aLogger);
        } catch (InstantiationException e) {
            return false;
            // TODO log this -> e.printStackTrace();
        } catch (IllegalAccessException e) {
            return false;
            // TODO log this -> e.printStackTrace();
        } catch (ClassNotFoundException e) {
            return false;
            // TODO log this -> e.printStackTrace();
        }

        if (handler != null) {
            handler.addToLogger();
            this.traceHandlerMap.put(aLogger, handler);
            return true;
        } else
            return false;
    }

    /**
     * Gets the component.
     * 
     * @param interfaceType
     *        the interface type
     * @param diversifier
     *        the diversifier
     * 
     * @return the component
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment#getComponent(java.lang.Class,
     *      java.lang.String)
     */
    public Object getComponent(final Class interfaceType, final String diversifier) {
        ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
        Object component = null;
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            component = this.sbb.getKernel().getComponent(interfaceType, diversifier);
        } finally {
            Thread.currentThread().setContextClassLoader(currThreadLoader);
        }
        return component;
    }

    /**
     * Gets the location ID.
     * 
     * @return the location ID
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment#getLocation()
     */
    public String getLocationID() {
        return this.sbb.getKernel().getLocationId();
    }

    /**
     * Gets the message factory.
     * 
     * @return the message factory
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment#getMessageFactory()
     */
    public InternalMessageFactory getMessageFactory() {
        return this.msgFactory;
    }

    /**
     * Gets the participant identity.
     * 
     * @return the participant identity
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment#getParticipantIdentity()
     */
    public InternalParticipantIdentity getParticipantIdentity() {
        InternalParticipantIdentity returnInternalParticipantIdentity = this.ident.getParticipantIdentity();
        return returnInternalParticipantIdentity;
    }

    /**
     * Removes the InternalSBB trace destination.
     * 
     * @param aLogger
     *        the a logger
     * 
     * @return true, if remove InternalSBB trace destination
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment#removeSBBTraceDestination(java.lang.Object)
     */
    public boolean removeSBBTraceDestination(final Object aLogger) {
        // FIXME implement this
        if (!this.traceHandlerMap.containsKey(aLogger)) return false;
        TraceHandler handler = (TraceHandler) this.traceHandlerMap.get(aLogger);
        handler.removeFromLogger();
        this.traceHandlerMap.remove(aLogger);
        return true;
    }

    /**
     * Restore call context.
     * 
     * @param key
     *        the key
     * 
     * @return the call context
     * 
     * @throws ContextNotRestoreableException
     * @throws ContextNotFoundException
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment#restoreCallContext(java.lang.String)
     */
    public InternalCallContext restoreCallContext(final String key) throws InternalInfrastructureException,
            InternalIllegalInputException {
        if (key == null) throw new IllegalArgumentException("key to a stored call context to store must not be null");
        return this.sbb.getKernel().restoreCallContext(key);
    }

    /**
     * Store call context.
     * 
     * @param ctx
     *        the ctx
     * 
     * @return the string
     * 
     * @throws ContextNotStoreableException
     * 
     * @see org.eclipse.swordfish.papi.InternalEnvironment#storeCallContext(org.eclipse.swordfish.papi.untyped.InternalCallContext)
     */
    public String storeCallContext(final InternalCallContext ctx) throws InternalInfrastructureException,
            InternalIllegalInputException {
        if (ctx == null) throw new IllegalArgumentException("call context to store must not be null");
        if (!(ctx instanceof CallContextExtension))
            throw new IllegalArgumentException(
                    "this call context is of bad type because it has not been created through InternalSBB");

        return this.sbb.getKernel().storeCallContext((CallContextExtension) ctx);
    }
}
