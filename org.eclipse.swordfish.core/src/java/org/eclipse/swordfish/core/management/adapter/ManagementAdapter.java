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
package org.eclipse.swordfish.core.management.adapter;

/**
 * Common interface for all components that provide external connections.
 * 
 */
public interface ManagementAdapter {

    /**
     * Gets the host.
     * 
     * @return the hostname or IP address that the adapter listens on - may be <code>null</code>
     */
    String getHost();

    /**
     * Gets the object name.
     * 
     * @return String representation of the adapter's MBean - may be <code>null</code>
     */
    String getObjectName();

    /**
     * Gets the port.
     * 
     * @return the IP port number the adapter listens on - may be <code>null</code>
     */
    Integer getPort();

}
