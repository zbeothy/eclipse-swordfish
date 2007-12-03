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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.internalproxy.InternalProxy;
import org.eclipse.swordfish.core.components.internalproxy.ResilienceController;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;

/**
 * Simple mock class for the internal proxy.
 * 
 */
public class MockInternalProxy implements InternalProxy {

    /** The echo. */
    List echo = new ArrayList();

    /**
     * Creates a new mock.
     * 
     * @param echoList
     *        Behaviour of the mock is defined by a List that contains XML data (String) as values.
     *        Subsequent calls to invokeService will return subsequent values from the List.
     */
    public MockInternalProxy(final List echoList) {
        this.echo = echoList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.InternalProxy#invokeService(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription,
     *      java.lang.String, java.lang.String)
     */
    public String invokeService(final CompoundServiceDescription aggServiceDesc, final String operationName, final String inMessage)
            throws Exception {
        return (String) this.echo.remove(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.InternalProxy#invokeService(org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription,
     *      java.lang.String, java.lang.String,
     *      org.eclipse.swordfish.core.components.internalproxy.ResilienceController)
     */
    public Object invokeService(final CompoundServiceDescription aggServiceDesc, final String operationName,
            final String inMessage, final ResilienceController controller) throws Exception {
        return this.echo.remove(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.core.components.internalproxy.InternalProxy#invokeService(javax.xml.namespace.QName,
     *      java.lang.String, java.lang.String)
     */
    public String invokeService(final QName serviceName, final String operationName, final String inMessage) throws Exception {
        return null;
    }

    /**
     * Invoke service.
     * 
     * @param serviceName
     *        the service name
     * @param operationName
     *        the operation name
     * @param policyid
     *        the policyid
     * @param inMessage
     *        the in message
     * 
     * @return the string
     * 
     * @throws Exception
     */
    public String invokeService(final QName serviceName, final String operationName, final String policyid, final String inMessage)
            throws Exception {

        return (String) this.echo.remove(0);
    }
}
