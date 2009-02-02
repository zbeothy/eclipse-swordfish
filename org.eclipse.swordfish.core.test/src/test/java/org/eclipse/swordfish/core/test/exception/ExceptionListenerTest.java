package org.eclipse.swordfish.core.test.exception;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.servicemix.jbi.runtime.impl.EndpointImpl;
import org.apache.servicemix.nmr.api.Endpoint;
import org.apache.servicemix.nmr.api.NMR;
import org.apache.servicemix.nmr.api.Pattern;
import org.apache.servicemix.nmr.core.ChannelImpl;
import org.apache.servicemix.nmr.core.ExchangeImpl;
import org.eclipse.swordfish.api.Interceptor;
import org.eclipse.swordfish.api.InterceptorExceptionListener;
import org.eclipse.swordfish.api.SwordfishException;
import org.eclipse.swordfish.core.test.util.OsgiSupport;
import org.eclipse.swordfish.core.test.util.ServiceMixSupport;
import org.eclipse.swordfish.core.test.util.ServiceMixSupport.ExchangeProcessorImpl;
import org.eclipse.swordfish.core.test.util.base.TargetPlatformOsgiTestCase;
import org.eclipse.swordfish.core.util.xml.StringSource;
import org.osgi.framework.ServiceRegistration;


public class ExceptionListenerTest extends TargetPlatformOsgiTestCase {

    public void test1BlockingInterceptingCall() throws Exception {
        EndpointImpl endpointService1 = null;
        EndpointImpl endpointService2 = null;
        NMR nmr = OsgiSupport.getReference(bundleContext, NMR.class);
        assertNotNull(nmr);
        // prepeare objects for test
        SwordfishException exception = new SwordfishException("This exception for testing Interceptor Exception Listener");
        ExceptionThrowableInterceptor interceptor = new ExceptionThrowableInterceptor(exception);
        addRegistrationToCancel(bundleContext.registerService(Interceptor.class.getCanonicalName(),
                interceptor, null));

        SimpleInterceptorExceptionListener exceptionListener = new SimpleInterceptorExceptionListener();
        
        ServiceRegistration interceptorExceptionListenerSegistration =
        	bundleContext.registerService(InterceptorExceptionListener.class.getCanonicalName(), exceptionListener, null);
        addRegistrationToCancel(interceptorExceptionListenerSegistration);
        

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

            try {
                assertTrue(nmr.createChannel().sendSync(exchange));
                fail();
            } catch(Exception ex){
            }
            Thread.sleep(500);

            assertEquals(exception, exceptionListener.exception);
            assertEquals(exchange.getId(), exceptionListener.exchange.getExchangeId());
            assertEquals(interceptor, exceptionListener.interceptor);
            
            // now try to unregister exception listener and send exchange one more time
            interceptorExceptionListenerSegistration.unregister();
            
            // setting exception listener properties to null
            exceptionListener.exception = null;
            exceptionListener.exchange = null;
            exceptionListener.interceptor = null;
            
            try {
                assertTrue(nmr.createChannel().sendSync(exchange));
                fail();
            } catch(Exception ex){
            }
            Thread.sleep(500);
            assertNull(exceptionListener.exception);
            assertNull(exceptionListener.exchange);
            assertNull(exceptionListener.interceptor);

        } finally {
            nmr.getEndpointRegistry().unregister(endpointService1, null);
            nmr.getEndpointRegistry().unregister(endpointService2, null);
        }
    }
    
    protected String getManifestLocation() {
        return "classpath:org/eclipse/swordfish/core/planner/test/MANIFEST.MF";
    }
}
