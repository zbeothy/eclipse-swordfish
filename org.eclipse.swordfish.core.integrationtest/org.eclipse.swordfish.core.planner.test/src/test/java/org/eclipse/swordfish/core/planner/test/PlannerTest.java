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
package org.eclipse.swordfish.core.planner.test;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.swordfish.api.FilterStrategy;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SortingStrategy;
import org.eclipse.swordfish.core.planner.api.Planner;
import org.eclipse.swordfish.core.test.util.base.TargetPlatformOsgiTestCase;
import org.eclipse.swordfish.core.test.util.mock.DefaultHintFilterStrategy;
import org.eclipse.swordfish.core.test.util.mock.MockHintExtractor;
import org.eclipse.swordfish.core.test.util.mock.MockInterceptor;
import org.eclipse.swordfish.core.test.util.mock.MockSortingStrategy;
import org.osgi.framework.ServiceReference;

public class PlannerTest extends TargetPlatformOsgiTestCase {

    public void test4PlannerCreation() throws Exception {
        ServiceReference ref = null;

        ref = bundleContext.getServiceReference(Planner.class.getName());

        assertNotNull("Service Reference is null", ref);
        Planner planner = (Planner) bundleContext.getService(ref);
        assertNotNull("Cannot find the service", planner);
        assertTrue(planner.toString().startsWith("org.eclipse.swordfish.core.planner.PlannerImpl"));

    }

    public void test5InterceptorRegistration() throws Exception {
        ServiceReference plannerRef = null;
        plannerRef = bundleContext.getAllServiceReferences(Planner.class
                .getName(), null)[0];
        Planner planner = (Planner) bundleContext.getService(plannerRef);
        Set<Interceptor> interceptors = null;
        interceptors = planner.getRegisteredInterceptors();
        LOG.info("Interceptors: " + interceptors);
        Interceptor interceptor1 = new MockInterceptor();
        Dictionary properties = new Hashtable();
        properties.put("key1", "value1");
        addRegistrationToCancel(bundleContext.registerService(
                "org.eclipse.swordfish.api.Interceptor", interceptor1,
                properties));
        Thread.sleep(100);
        interceptors = planner.getRegisteredInterceptors();
        int interceptorNummber = interceptors.size();

        assertTrue(interceptorNummber > 1);
        properties.put("key1", "value2");
        addRegistrationToCancel(bundleContext.registerService(
                "org.eclipse.swordfish.api.Interceptor", new MockInterceptor(),
                properties));

        interceptors = planner.getRegisteredInterceptors();
        assertEquals(interceptorNummber + 1, interceptors.size());
    }

    public void test6Strategies() throws Exception {
        ServiceReference plannerRef = null;
        plannerRef = bundleContext.getAllServiceReferences(Planner.class
                .getName(), null)[0];
        Planner planner = (Planner) bundleContext.getService(plannerRef);
        Set<Interceptor> interceptors = null;
        Interceptor interceptor1 = new MockInterceptor();
        Dictionary properties = new Hashtable();
        addRegistrationToCancel(bundleContext.registerService(
                "org.eclipse.swordfish.api.Interceptor", interceptor1,
                properties));
        Thread.sleep(100);
        SortingStrategy sortingStrategy = new MockSortingStrategy();
        addRegistrationToCancel(bundleContext.registerService(
                "org.eclipse.swordfish.api.SortingStrategy", sortingStrategy,
                properties));
        FilterStrategy filterStrategy = new DefaultHintFilterStrategy();
        addRegistrationToCancel(bundleContext.registerService(
                "org.eclipse.swordfish.api.FilterStrategy", filterStrategy,
                properties));
        planner.setHintExtractor(new MockHintExtractor());
        interceptors = planner.getRegisteredInterceptors();
        LOG.info("Interceptors: " + interceptors);
        int interceptorNummber = interceptors.size();
        assertTrue(interceptorNummber > 1);
        List<Interceptor> chain = planner.getInterceptorChain(interceptors,
                null);
        LOG.info("Interceptor chain: " + chain);
    }

    @Override
    protected String getManifestLocation() {
        return "classpath:org/eclipse/swordfish/core/planner/test/MANIFEST.MF";
    }
}
