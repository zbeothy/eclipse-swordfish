/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.service.importer.ImportedOsgiServiceProxy;
import org.springframework.osgi.service.importer.ServiceReferenceProxy;

public class AopProxyUtil {
    
    public static <T> T getTargetService(T proxy, BundleContext bundleContext){
        if (proxy instanceof ImportedOsgiServiceProxy) {
            ServiceReference serviceReference = ((ImportedOsgiServiceProxy) proxy).getServiceReference();
            if (serviceReference instanceof ServiceReferenceProxy) {
                serviceReference = ((ServiceReferenceProxy) serviceReference).getTargetServiceReference();
            }
            return (T)bundleContext.getService(serviceReference);
        }
        return proxy;
    }
}
