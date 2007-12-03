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
/*
 * (C) DPWN
 */
package org.eclipse.swordfish.core.processor;

import java.util.Collection;
import javax.jbi.messaging.MessageExchange;
import org.apache.ws.policy.Assertion;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.processing.impl.AbstractProcessingComponent;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * The Class DummyComponentBean.
 */
public class DummyComponentBean extends AbstractProcessingComponent implements DummyComponent {

    /**
     * (non-Javadoc).
     * 
     * @param assertion
     *        the assertion
     * 
     * @return true, if can handle
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#canHandle(org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public boolean canHandle(final Assertion assertion) throws InternalSBBException {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#canHandle(java.util.Collection)
     */
    public boolean canHandle(final Collection assertions) throws InternalSBBException {
        return false;
    }

    /**
     * (non-Javadoc).
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertion
     *        the assertion
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleFault(final MessageExchange context, final Role role, final Assertion assertion) throws InternalSBBException {
        System.out.println("DummyComponentBean handleFault called.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleFault(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleFault(final MessageExchange context, final Role role, final Collection assertions)
            throws InternalSBBException, PolicyViolatedException {

    }

    /**
     * (non-Javadoc).
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertion
     *        the assertion
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Assertion assertion)
            throws InternalSBBException {
        System.out.println("DummyComponentBean handleRequest called.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleRequest(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleRequest(final MessageExchange context, final Role role, final Collection assertions)
            throws InternalSBBException, PolicyViolatedException {

    }

    /**
     * (non-Javadoc).
     * 
     * @param context
     *        the context
     * @param role
     *        the role
     * @param assertion
     *        the assertion
     * 
     * @throws InternalSBBException
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role,
     *      org.eclipse.swordfish.core.components.policy.Assertion)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Assertion assertion)
            throws InternalSBBException {
        System.out.println("DummyComponentBean handleResponse called.");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.processing.ProcessingComponent#handleResponse(javax.jbi.messaging.MessageExchange,
     *      org.eclipse.swordfish.core.components.iapi.Role, java.util.Collection)
     */
    public void handleResponse(final MessageExchange context, final Role role, final Collection assertions)
            throws InternalSBBException, PolicyViolatedException {

    }

}
