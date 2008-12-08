package org.eclipse.swordfish.core.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.apache.servicemix.jbi.runtime.impl.EndpointImpl;
import org.apache.servicemix.jbi.runtime.impl.MessageExchangeImpl;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Pattern;
import org.apache.servicemix.nmr.core.ChannelImpl;
import org.apache.servicemix.nmr.core.ExchangeImpl;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.configuration.ConfigurationConsumer;
import org.eclipse.swordfish.api.configuration.PollableConfigurationSource;
import org.eclipse.swordfish.api.context.SwordfishContext;
import org.eclipse.swordfish.api.context.SwordfishContextAware;
import org.eclipse.swordfish.api.event.EventService;
import org.eclipse.swordfish.core.event.ConfigurationEventImpl;
import org.eclipse.swordfish.core.event.EventServiceImpl;
import org.eclipse.swordfish.core.interceptor.CxfDecoratingInterceptor;
import org.eclipse.swordfish.core.interceptor.EndpointResolverInterceptor;
import org.eclipse.swordfish.core.interceptor.LoggingInterceptor;
import org.eclipse.swordfish.core.planner.api.Planner;
import org.eclipse.swordfish.core.test.mock.MockConfigurationConsumer;
import org.eclipse.swordfish.core.test.mock.MockInterceptor;
import org.eclipse.swordfish.core.test.mock.PollableConfigurationSourceMock;
import org.eclipse.swordfish.core.test.util.OsgiSupport;
import org.eclipse.swordfish.core.test.util.ServiceMixSupport;
import org.eclipse.swordfish.core.test.util.ServiceMixSupport.ExchangeProcessorImpl;
import org.eclipse.swordfish.core.util.xml.StringSource;
import org.osgi.framework.ServiceReference;

public class SwordfishCoreIntegrationTest extends TargetPlatformOsgiTestCase {
    public void test1SimpleConfiguration() throws Exception {
        final CountDownLatch contextInjectedLatch = new CountDownLatch(1);
        final CountDownLatch configurationUpdated = new CountDownLatch(1);
        final Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("TestTime", String.valueOf(System.currentTimeMillis()));
        MockConfigurationConsumer configurationConsumer = new MockConfigurationConsumer() {
            @Override
            public void onReceiveConfiguration(Map configuration) {
               if (configuration != null && configuration.containsKey("TestTime")) {
                   configurationUpdated.countDown();
               }
               super.onReceiveConfiguration(configuration);
            }
        };
        configurationConsumer.setId("MockConfigurationConsumerIdTest1");
        addRegistrationToCancel(bundleContext.registerService(ConfigurationConsumer.class.getCanonicalName(), configurationConsumer, null));
        addRegistrationToCancel(bundleContext.registerService(SwordfishContextAware.class.getCanonicalName(), new SwordfishContextAware() {
            public void setContext(SwordfishContext swordfishContext) {
                swordfishContext.getConfigurationService().updateConfiguration("MockConfigurationConsumerIdTest1", configuration);
                contextInjectedLatch.countDown();
            }
        }, null));
        assertTrue(contextInjectedLatch.await(4, TimeUnit.SECONDS));
        assertTrue(configurationUpdated.await(9999, TimeUnit.SECONDS));
        assertEquals(configurationConsumer.getConfiguration().get("TestTime"), configuration.get("TestTime"));
    }
    public void test2ConfigurationPollableSourceTest() throws Exception {
        final CountDownLatch configurationUpdated = new CountDownLatch(1);
        final Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("TestTime", String.valueOf(System.currentTimeMillis()));
        MockConfigurationConsumer configurationConsumer = new MockConfigurationConsumer() {
            @Override
            public void onReceiveConfiguration(Map configuration) {
               if (configuration != null && configuration.containsKey("TestTime")) {
                   configurationUpdated.countDown();
               }
               super.onReceiveConfiguration(configuration);
            }
        };
        configurationConsumer.setId("MockConfigurationConsumerIdTest2");
        addRegistrationToCancel(bundleContext.registerService(ConfigurationConsumer.class.getCanonicalName(), configurationConsumer, null));
        addRegistrationToCancel(bundleContext.registerService(PollableConfigurationSource.class.getCanonicalName(),
                new PollableConfigurationSourceMock().addConfiguration("MockConfigurationConsumerIdTest2", configuration), null));
        assertTrue(configurationUpdated.await(4, TimeUnit.SECONDS));
        assertEquals(configurationConsumer.getConfiguration().get("TestTime"), configuration.get("TestTime"));
    }
    public void test3AsynchronousConfigurationUpdateTest() throws Exception {
        final CountDownLatch configurationUpdated = new CountDownLatch(1);
        final Map<String, String> configuration = new HashMap<String, String>();
        configuration.put("TestTime", String.valueOf(System.currentTimeMillis()));
        EventServiceImpl eventSender = (EventServiceImpl)OsgiSupport.getReference(bundleContext, EventService.class);
        assertNotNull(eventSender);
        assertNotNull(eventSender.getEventAdmin());
        MockConfigurationConsumer configurationConsumer = new MockConfigurationConsumer() {
            @Override
            public void onReceiveConfiguration(Map configuration) {
               if (configuration != null && configuration.containsKey("TestTime")) {
                   configurationUpdated.countDown();
               }
               super.onReceiveConfiguration(configuration);
            }
        };
        configurationConsumer.setId("MockConfigurationConsumerIdTest3");
        addRegistrationToCancel(bundleContext.registerService(ConfigurationConsumer.class.getCanonicalName(), configurationConsumer, null));
        ConfigurationEventImpl configurationEvent = new ConfigurationEventImpl();
        Map<String, Object> configurations = new HashMap<String, Object>();
        configurations.put("MockConfigurationConsumerIdTest3", configuration);
        configurationEvent.setConfiguration(configurations);
        eventSender.postEvent(configurationEvent);
        assertTrue(configurationUpdated.await(4, TimeUnit.SECONDS));
        assertEquals(configurationConsumer.getConfiguration().get("TestTime"), configuration.get("TestTime"));
    }

    public void test4SimplePlannerInterceptorChain() throws Exception {
        ServiceReference[] serviceReferences = bundleContext.getAllServiceReferences(Planner.class.getName(), null);
        assertEquals(serviceReferences.length, 1);
        Planner planner = (Planner) bundleContext.getService(serviceReferences[0]);
        List<Interceptor> interceptors = planner.getInterceptorChain(planner.getRegisteredInterceptors(), new MessageExchangeImpl(null));
        assertEquals(interceptors.size(), 3);
        assertEquals(interceptors.get(0).getProperties().get(Interceptor.TYPE_PROPERTY), EndpointResolverInterceptor.class);
        assertEquals(interceptors.get(1).getProperties().get(Interceptor.TYPE_PROPERTY), CxfDecoratingInterceptor.class);
        assertEquals(interceptors.get(2).getProperties().get(Interceptor.TYPE_PROPERTY), LoggingInterceptor.class);
    }

    public void test5NonBlockingInterceptingCall() throws Exception {
        EndpointImpl endpointService1 = null;
        EndpointImpl endpointService2 = null;
        NMR nmr = OsgiSupport.getReference(bundleContext, NMR.class);
        assertNotNull(nmr);
        MockInterceptor mockInterceptor = new MockInterceptor();
        addRegistrationToCancel(bundleContext.registerService(Interceptor.class.getCanonicalName(),
                mockInterceptor, null));
        Thread.sleep(500);
        try {
            endpointService1 = ServiceMixSupport.createAndRegisterEndpoint(nmr, new QName("namespace", "Service1"), null);
            endpointService2 = ServiceMixSupport.createAndRegisterEndpoint(nmr, new QName("namespace", "Service2"),
                    new ExchangeProcessorImpl(new QName("namespace", "Service2").toString()));
            ExchangeImpl exchange = new ExchangeImpl(Pattern.InOut);
            exchange.setSource(((ChannelImpl) endpointService1.getChannel()).getEndpoint());
            exchange.getIn(true).setBody(new org.eclipse.swordfish.core.util.xml.StringSource("<Hello/>"));
            Map<String, String> props = new HashMap<String, String>();
            props.put(Endpoint.SERVICE_NAME, endpointService2.getServiceName().toString());
            exchange.setTarget(ServiceMixSupport.lookup(nmr, props));
            assertEquals(endpointService2.getQueue().size(), 0);
            assertEquals(endpointService1.getQueue().size(), 0);
            nmr.createChannel().send(exchange);
            Thread.sleep(500);
            assertEquals(endpointService2.getQueue().size(), 1);
            assertEquals(endpointService1.getQueue().size(), 1);
            assertEquals(mockInterceptor.getExchanges().size(), 2);
            MessageExchangeImpl messageExchangeImpl = (MessageExchangeImpl) mockInterceptor.getExchanges().get(0);
            assertEquals(exchange, messageExchangeImpl.getInternalExchange());
        } finally {
            nmr.getEndpointRegistry().unregister(endpointService1, null);
            nmr.getEndpointRegistry().unregister(endpointService2, null);
        }
    }

    public void test6BlockingInterceptingCall() throws Exception {
        EndpointImpl endpointService1 = null;
        EndpointImpl endpointService2 = null;
        NMR nmr = OsgiSupport.getReference(bundleContext, NMR.class);
        assertNotNull(nmr);
        MockInterceptor mockInterceptor = new MockInterceptor();
        addRegistrationToCancel(bundleContext.registerService(Interceptor.class.getCanonicalName(),
                mockInterceptor, null));
        Thread.sleep(500);
        try {
            endpointService1 = ServiceMixSupport.createAndRegisterEndpoint(nmr, new QName("namespace", "Service1"), null);
            endpointService2 = ServiceMixSupport.createAndRegisterEndpoint(nmr, new QName("namespace", "Service2"),
                    new ExchangeProcessorImpl(new QName("namespace", "Service2").toString()));
            ExchangeImpl exchange = new ExchangeImpl(Pattern.InOut);
            exchange.setSource(((ChannelImpl) endpointService1.getChannel()).getEndpoint());
            exchange.getIn(true).setBody(new StringSource("<Hello/>"));
            Map<String, String> props = new HashMap<String, String>();
            props.put(Endpoint.SERVICE_NAME, endpointService2.getServiceName().toString());
            exchange.setTarget(ServiceMixSupport.lookup(nmr, props));
            assertEquals(endpointService2.getQueue().size(), 0);
            assertEquals(endpointService1.getQueue().size(), 0);
            assertTrue(nmr.createChannel().sendSync(exchange));
            Thread.sleep(500);
            assertEquals(endpointService2.getQueue().size(), 1);
            assertEquals(endpointService1.getQueue().size(), 0);
            assertEquals(mockInterceptor.getExchanges().size(), 2);
            MessageExchangeImpl messageExchangeImpl = (MessageExchangeImpl) mockInterceptor.getExchanges().get(0);
            assertEquals(exchange, messageExchangeImpl.getInternalExchange());
        } finally {
            nmr.getEndpointRegistry().unregister(endpointService1, null);
            nmr.getEndpointRegistry().unregister(endpointService2, null);
        }
    }
}
