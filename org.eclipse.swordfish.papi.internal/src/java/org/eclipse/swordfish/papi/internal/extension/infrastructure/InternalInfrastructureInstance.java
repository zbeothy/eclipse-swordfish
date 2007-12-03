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
package org.eclipse.swordfish.papi.internal.extension.infrastructure;

import java.net.URL;

/**
 * Interface to get Infrastructure information. Instances of this interface enables to get URL's of
 * the Configuration and ServiceRegistry service.
 * 
 */
public interface InternalInfrastructureInstance {

    /**
     * Get the Configuration Service URL.
     * 
     * @return String Configuration service URL
     */
    URL getConfigurationServiceURL();

    /**
     * Get the Service Registry URL.
     * 
     * @return String Service Registry URL
     */
    URL getServiceRegistryURL();

}
