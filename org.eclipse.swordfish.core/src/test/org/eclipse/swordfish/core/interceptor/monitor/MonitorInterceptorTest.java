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
package org.eclipse.swordfish.core.interceptor.monitor;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;
import javax.jbi.messaging.NormalizedMessage;
import junit.framework.TestCase;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.DOMPolicyReader;
import org.apache.ws.policy.util.PolicyFactory;
import org.eclipse.swordfish.core.components.extension.ExtensionFactory;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.interceptor.monitor.impl.MonitoringProcessorBean;
import org.eclipse.swordfish.core.management.instrumentation.Resource;
import org.eclipse.swordfish.core.management.messages.TrackingMessage;
import org.eclipse.swordfish.core.management.messaging.TestConsumerInOut;
import org.eclipse.swordfish.core.management.messaging.TestNotificationProcessor;
import org.eclipse.swordfish.core.management.messaging.TestProviderInOut;
import org.eclipse.swordfish.core.management.mock.DummyMessageExchange;
import org.eclipse.swordfish.core.management.mock.DummyNormalizedMessage;
import org.eclipse.swordfish.core.management.notification.impl.ManagementNotificationListenerBean;
import org.eclipse.swordfish.core.management.operations.OperationalMessage;
import org.eclipse.swordfish.core.management.operations.TestLogHandler;
import org.eclipse.swordfish.core.management.operations.impl.OperationalMessageRecord;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.core.utils.ExchangeProperties;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalInstrumentationManager;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class MonitorInterceptorTest.
 */
public class MonitorInterceptorTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The interceptor. */
    private MonitoringProcessorBean interceptor;

    /** The test exchange. */
    private DummyMessageExchange testExchange;

    /** The processor. */
    private TestNotificationProcessor processor;

    /** The listener. */
    private ManagementNotificationListenerBean listener;

    /** The handler. */
    private TestLogHandler handler;

    /** The reader. */
    private DOMPolicyReader reader;

    /**
     * Instantiates a new monitor interceptor test.
     * 
     * @param name
     *        the name
     */
    public MonitorInterceptorTest(final String name) {
        super(name);
    }

    /**
     * Test consumer late.
     * 
     * @throws Exception
     */
    public void testConsumerLate() throws Exception {
        try {
            TestConsumerInOut exchangeDriver = new TestConsumerInOut(this.listener);
            InputStream is =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "org/eclipse/swordfish/core/interceptor/monitor/SampleConsumerResponseTime.xml");
            Document doc = TransformerUtil.docFromInputStream(is);
            Element root = doc.getDocumentElement();
            Policy policy = this.reader.readPolicy(root);
            List terms = policy.getTerms();
            String correlationId = exchangeDriver.executeBlockingOutPart1();
            try {
                Thread.sleep(250); // exceed 200 ms response time
            } catch (InterruptedException e) {
                // noop
            }
            exchangeDriver.executeBlockingOutPart2(correlationId);
            CallContextExtension cc = (CallContextExtension) this.testExchange.getProperty(ExchangeProperties.CALL_CONTEXT);
            cc.setCorrelationID(correlationId);
            String msgId = exchangeDriver.executeBlockingInPart1(correlationId);
            int count = this.processor.getCount();
            List messages = this.handler.getRecords();
            int startMessages = messages.size();
            this.interceptor.handleResponse(this.testExchange, Role.SENDER, terms);
            assertEquals(count + 1, this.processor.getCount());
            exchangeDriver.executeBlockingInPart2(correlationId, msgId);
            assertEquals(startMessages + 1, messages.size());
            OperationalMessageRecord record = (OperationalMessageRecord) messages.get(0);
            OperationalMessage msg = record.getOperationalMessage();
            assertEquals(TrackingMessage.RESPONSETIME_EXCEEDED, msg);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Test consumer ok.
     * 
     * @throws Exception
     */
    public void testConsumerOk() throws Exception {
        try {
            System.out.println("---------------------");
            System.out.println(System.currentTimeMillis());
            TestConsumerInOut exchangeDriver = new TestConsumerInOut(this.listener);
            InputStream is =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "org/eclipse/swordfish/core/interceptor/monitor/SampleConsumerResponseTime.xml");
            Document doc = TransformerUtil.docFromInputStream(is);
            Element root = doc.getDocumentElement();
            Policy policy = this.reader.readPolicy(root);
            List terms = policy.getTerms();
            System.out.println(System.currentTimeMillis());
            this.listener.processNotifications();
            System.out.println(System.currentTimeMillis());
            String correlationId = exchangeDriver.executeBlockingOutPart1();
            System.out.println(System.currentTimeMillis());
            exchangeDriver.executeBlockingOutPart2(correlationId);
            CallContextExtension cc = (CallContextExtension) this.testExchange.getProperty(ExchangeProperties.CALL_CONTEXT);
            cc.setCorrelationID(correlationId);
            String msgId = exchangeDriver.executeBlockingInPart1(correlationId);
            int count = this.processor.getCount();
            List messages = this.handler.getRecords();
            int startMessages = messages.size();
            this.interceptor.handleResponse(this.testExchange, Role.SENDER, terms);
            assertEquals(count + 1, this.processor.getCount());
            exchangeDriver.executeBlockingInPart2(correlationId, msgId);
            System.out.println(System.currentTimeMillis());
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            assertEquals(startMessages, messages.size());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Test provider late.
     * 
     * @throws Exception
     */
    public void testProviderLate() throws Exception {
        try {
            TestProviderInOut exchangeDriver = new TestProviderInOut(this.listener);
            InputStream is =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "org/eclipse/swordfish/core/interceptor/monitor/SampleProviderResponseTime.xml");
            Document doc = TransformerUtil.docFromInputStream(is);
            Element root = doc.getDocumentElement();
            Policy policy = this.reader.readPolicy(root);
            List terms = policy.getTerms();
            String correlationId = exchangeDriver.executeIn();
            try {
                Thread.sleep(250); // exceed 200 ms response time
            } catch (InterruptedException e) {
                // noop
            }
            this.testExchange.setProperty(ExchangeProperties.CORRELATION_ID, correlationId);
            String msgId = exchangeDriver.executeOutPart1(correlationId);
            int count = this.processor.getCount();
            this.interceptor.handleResponse(this.testExchange, Role.RECEIVER, terms);
            assertEquals(count + 1, this.processor.getCount());
            exchangeDriver.executeOutPart2(correlationId, msgId);
            List messages = this.handler.getRecords();
            assertEquals(0, messages.size());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Test provider ok.
     * 
     * @throws Exception
     */
    public void testProviderOk() throws Exception {
        try {
            TestProviderInOut exchangeDriver = new TestProviderInOut(this.listener);
            InputStream is =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "org/eclipse/swordfish/core/interceptor/monitor/SampleProviderResponseTime.xml");
            Document doc = TransformerUtil.docFromInputStream(is);
            Element root = doc.getDocumentElement();
            Policy policy = this.reader.readPolicy(root);
            List terms = policy.getTerms();
            String correlationId = exchangeDriver.executeIn();
            this.testExchange.setProperty(ExchangeProperties.CORRELATION_ID, correlationId);
            String msgId = exchangeDriver.executeOutPart1(correlationId);
            int count = this.processor.getCount();
            this.interceptor.handleResponse(this.testExchange, Role.RECEIVER, terms);
            assertEquals(count + 1, this.processor.getCount());
            exchangeDriver.executeOutPart2(correlationId, msgId);
            List messages = this.handler.getRecords();
            assertEquals(0, messages.size());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        try {
            super.setUp();
            this.reader = (DOMPolicyReader) PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);
            this.ctx =
                    new FileSystemXmlApplicationContext(
                            new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
            this.interceptor = (MonitoringProcessorBean) this.ctx.getBean("org.eclipse.swordfish.core.interceptor.Monitor");
            this.ctx.getBean("mbeanServer");
            this.listener =
                    (ManagementNotificationListenerBean) this.ctx
                        .getBean("org.eclipse.swordfish.core.management.notification.ManagementNotificationListener");
            this.listener.setDirectProcessing(true);
            this.processor = new TestNotificationProcessor();
            List processors = this.listener.getNotificationProcessors();
            processors.add(this.processor);
            this.listener.setNotificationProcessors(processors);
            this.testExchange = new DummyMessageExchange();
            InputStream messageStream =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "org/eclipse/swordfish/core/interceptor/monitor/SampleMessage1.xml");
            NormalizedMessage message = new DummyNormalizedMessage(messageStream);
            this.testExchange.setMessage(message, "in");
            this.testExchange.setMessage(message, "out");
            this.handler = null;
            Logger logger = Logger.getLogger("operations");
            Handler[] handlers = logger.getHandlers();
            boolean found = false;
            for (int i = 0; i < handlers.length; i++) {
                if (handlers[i] instanceof TestLogHandler) {
                    this.handler = (TestLogHandler) handlers[i];
                }
            }
            if (!found) {
                this.handler = (TestLogHandler) this.ctx.getBean("test.operations.TestHandler");
                logger.addHandler(this.handler);
            }
            // Thread.sleep(5000);
            // run one test exchange to prevent timing issues due to lazy
            // initialization
            InputStream desc =
                    this.getClass().getClassLoader().getResourceAsStream(
                            "org/eclipse/swordfish/core/management/instrumentation/ResourceDesc.xml");
            Object obj = this.ctx.getBean(InternalInstrumentationManager.class.getName());
            if (obj instanceof ExtensionFactory) {
                ExtensionFactory factory = (ExtensionFactory) obj;
                obj = factory.getInstance(null);
            }
            InternalInstrumentationManager manager = (InternalInstrumentationManager) obj;
            Resource resource = new Resource();
            manager.registerInstrumentation(resource, desc);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        try {
            Logger logger = Logger.getLogger("operations");
            logger.removeHandler(this.handler);
            this.ctx.destroy();
            super.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
