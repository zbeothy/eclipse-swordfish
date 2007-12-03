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
package org.eclipse.swordfish.core.components.dynamicendpointhandler.impl;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.srproxy.SPDXPort;

/**
 * The Class AbstractJMSDynamicEndpoint.
 */
public abstract class AbstractJMSDynamicEndpoint extends AbstractDynamicEndpoint {

    /**
     * Instantiates a new abstract JMS dynamic endpoint.
     * 
     * @param serviceName
     *        the service name
     * @param port
     *        the port
     */
    protected AbstractJMSDynamicEndpoint(final QName serviceName, final SPDXPort port) {

        super(serviceName, port);
    }

}
