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
package org.eclipse.swordfish.core.components.srproxy;

import java.util.List;
import javax.wsdl.Port;
import org.eclipse.swordfish.core.components.iapi.Transport;

/**
 * This is an interface for a port in a <code>ServiceProviderDescription</code> (SPDX).
 */
public interface SPDXPort extends Port {

    /**
     * Gets the locator cluster locations.
     * 
     * @return the loaction attributes in the order they are defined in the spdx file if any and an
     *         empty list if there is no locator defined.
     */
    List getLocatorClusterLocations();

    /**
     * Returns the type of transport bound to this port.
     * 
     * @return the Transport
     */
    Transport getTransport();

    /**
     * Checks if is shared.
     * 
     * @return true if this spdx port is shared among participants
     */
    boolean isShared();

    /**
     * Checks if is using locator.
     * 
     * @return true if this spdx port is defined to use locators
     */
    boolean isUsingLocator();
}
