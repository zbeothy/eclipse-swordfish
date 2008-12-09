package org.eclipse.swordfish.core.planner.test;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.swordfish.api.FilterStrategy;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.SortingStrategy;
import org.eclipse.swordfish.core.planner.api.Planner;
import org.eclipse.swordfish.core.test.util.base.BaseMavenOsgiTestCase;
import org.eclipse.swordfish.core.test.util.mock.DefaultHintFilterStrategy;
import org.eclipse.swordfish.core.test.util.mock.MockHintExtractor;
import org.eclipse.swordfish.core.test.util.mock.MockInterceptor;
import org.eclipse.swordfish.core.test.util.mock.MockSortingStrategy;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.util.OsgiStringUtils;

/**
 * @author dwolz
 */
public class PlannerTest extends BaseMavenOsgiTestCase {


    /**
     * The location of the packaged OSGi bundles to be installed for this test.
     * Values are Spring resource paths. The bundles we want to use are part of
     * the same multi-project maven build as this project is. Hence we use the
     * localMavenArtifact helper method to find the bundles produced by the
     * package phase of the maven build (these tests will run after the
     * packaging phase, in the integration-test phase).
     *
     * JUnit, commons-logging, spring-core and the spring OSGi test bundle are
     * automatically included so do not need to be specified here.
     */
    @Override
    protected String[] getTestBundlesNames() {
        return new String[] {
                getBundle("org.eclipse", "org.eclipse.osgi.services"),
//                getBundle("org.eclipse.equinox", "org.eclipse.equinox.cm"),
                getBundle("org.apache.geronimo.specs",
                        "geronimo-activation_1.1_spec"),
                getBundle("javax.wsdl",
                        "com.springsource.javax.wsdl"),
                getBundle("org.apache.servicemix.jbi",
                        "org.apache.servicemix.jbi.api"),
                getBundle("org.apache.servicemix.jbi",
                        "org.apache.servicemix.jbi.runtime"),
                getBundle("org.apache.servicemix.nmr",
                        "org.apache.servicemix.nmr.api"),
                getBundle("org.apache.servicemix.nmr",
                        "org.apache.servicemix.nmr.core"),
                getBundle("org.eclipse.swordfish", "org.eclipse.swordfish.api"),
                getBundle("org.eclipse.swordfish",
            			"org.eclipse.swordfish.core.test.util"),
                getBundle("org.eclipse.swordfish",
                	"org.eclipse.swordfish.core.util"),
                getBundle("org.eclipse.swordfish",
                	"org.eclipse.swordfish.core"),
                getBundle("org.eclipse.swordfish",
                	"org.eclipse.swordfish.core.event"),
                getBundle("org.eclipse.swordfish",
                        "org.eclipse.swordfish.core.planner"),
                getBundle("org.springframework.osgi",
                        "spring-osgi-core")
        };
    }
    
    public void test1OsgiPlatformStarts() throws Exception {
        LOG.info(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        LOG.info(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        LOG.info(bundleContext
                .getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
    }

    public void test2OsgiEnvironment() throws Exception {
        Bundle[] bundles = bundleContext.getBundles();
        LOG.info("Installed bundles:");
        for (int i = 0; i < bundles.length; i++) {
            LOG.info(OsgiStringUtils.nullSafeName(bundles[i]));
        }
    }

    public void test3OSGiStartedOk() {
        assertNotNull(bundleContext);
    }

    /**
     * The planner service should have been exported as an OSGi service, which
     * we can verify using the OSGi service APIs.
     *
     * In a Spring bundle, using osgi:reference is a much easier way to get a
     * reference to a published service.
     *
     */
    public void test4PlannerCreation() throws Exception {
        waitOnContextCreation("org.eclipse.swordfish.core.planner");
        ServiceReference[] refs;
        refs = bundleContext
                .getServiceReferences(Planner.class.getName(), null);
        refs = bundleContext.getAllServiceReferences(Planner.class.getName(),
                null);
        LOG.info(bundleContext.getService(refs[0]).getClass().getClassLoader()
                .toString());
        LOG.info(Planner.class.getClassLoader().toString());
        refs = null;
        ServiceReference ref = null;

        ref = bundleContext.getAllServiceReferences(Planner.class.getName(),
                null)[0];

        assertNotNull("Service Reference is null", ref);
        try {
            Planner planner = (Planner) bundleContext.getService(ref);
            assertNotNull("Cannot find the service", planner);
            assertTrue(planner.toString().startsWith(
                    "org.eclipse.swordfish.core.planner.PlannerImpl"));
        } finally {
            bundleContext.ungetService(ref);
        }
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
        LOG.info("Interceptors: " + interceptors);
        assertEquals(1, interceptors.size());
        properties.put("key1", "value2");
        addRegistrationToCancel(bundleContext.registerService(
                "org.eclipse.swordfish.api.Interceptor", new MockInterceptor(),
                properties));
        Thread.sleep(100);
        interceptors = planner.getRegisteredInterceptors();
        LOG.info("Interceptors: " + interceptors);
        assertEquals(2, interceptors.size());
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
        assertEquals(3, interceptors.size());
        List<Interceptor> chain = planner.getInterceptorChain(interceptors,
                null);
        LOG.info("Interceptor chain: " + chain);
    }
}
