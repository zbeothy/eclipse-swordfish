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
