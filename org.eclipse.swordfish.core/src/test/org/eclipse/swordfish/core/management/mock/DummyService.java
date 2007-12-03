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

import java.util.Collection;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.papi.internal.InternalCommunicationStyle;
import org.eclipse.swordfish.papi.internal.InternalEnvironment;
import org.eclipse.swordfish.papi.internal.InternalSBB;
import org.eclipse.swordfish.papi.internal.authentication.InternalAuthenticationHandler;
import org.eclipse.swordfish.papi.internal.untyped.InternalService;

/**
 * Minimal implementation of InternalService to pass tests in org.eclipse.swordfish.core.management
 * 
 */
public class DummyService implements InternalService {

    /** The name. */
    private QName name;

    /**
     * Instantiates a new dummy service.
     * 
     * @param localName
     *        the local name
     */
    public DummyService(final String localName) {
        this.name = new QName("http://sopgroup.org", localName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#addAuthenticationHandler(org.eclipse.swordfish.papi.authentication.AuthenticationHandler)
     */
    public void addAuthenticationHandler(final InternalAuthenticationHandler anAuthenticationHandler) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#getAuthenticationHandlers()
     */
    public InternalAuthenticationHandler[] getAuthenticationHandlers() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#getEnvironment()
     */
    public InternalEnvironment getEnvironment() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#getName()
     */
    public QName getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#getOperationCommunicationStyle(java.lang.String)
     */
    public InternalCommunicationStyle getOperationCommunicationStyle(final String anOperationName) {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#getOperationNames()
     */
    public Collection getOperationNames() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#getProviderID()
     */
    public QName getProviderID() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#getSBB()
     */
    public InternalSBB getSBB() {

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#hasCallbackService()
     */
    public boolean hasCallbackService() {

        return false;
    }

    /**
     * Checks for partner service.
     * 
     * @return true, if successful
     */
    public boolean hasPartnerService() {

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#isActive()
     */
    public boolean isActive() {

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#release()
     */
    public void release() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.papi.untyped.Service#removeAuthenticationHandler(org.eclipse.swordfish.papi.authentication.AuthenticationHandler)
     */
    public void removeAuthenticationHandler(final InternalAuthenticationHandler anAuthenticationHandler) {

    }

}
