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
package org.eclipse.swordfish.core.management.mock;

import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.InternalEnvironment;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalOperation;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;

/**
 * Minimal implementation of InternalOperation to pass tests in
 * org.eclipse.swordfish.core.management
 * 
 */
public class DummyOperation implements InternalOperation {

    /** The internalService. */
    private InternalService internalService;

    /** The name. */
    private String name;

    /**
     * Instantiates a new dummy operation.
     * 
     * @param arg0
     *        the arg0
     * @param arg1
     *        the arg1
     */
    public DummyOperation(final InternalService arg0, final String arg1) {
        this.internalService = arg0;
        this.name = arg1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Operation#addAuthenticationHandler(org.eclipse.swordfish.papi.authentication.AuthenticationHandler)
     */
    public void addAuthenticationHandler(final InternalAuthenticationHandler anAuthenticationHandler) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Operation#getAuthenticationHandlers()
     */
    public InternalAuthenticationHandler[] getAuthenticationHandlers() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Operation#getCommunicationStyle()
     */
    public InternalCommunicationStyle getCommunicationStyle() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Operation#getEnvironment()
     */
    public InternalEnvironment getEnvironment() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Operation#getName()
     */
    public String getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Operation#getService()
     */
    public InternalService getService() {
        return this.internalService;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Operation#removeAuthenticationHandler(org.eclipse.swordfish.papi.authentication.AuthenticationHandler)
     */
    public void removeAuthenticationHandler(final InternalAuthenticationHandler anAuthenticationHandler) {

    }

}
