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
package org.eclipse.swordfish.core.interceptor.tracking;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;
import javax.jbi.messaging.NormalizedMessage;
import junit.framework.TestCase;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.DOMPolicyReader;
import org.apache.ws.policy.util.PolicyFactory;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.interceptor.tracking.impl.TrackingProcessorBean;
import org.eclipse.swordfish.core.management.messaging.TestConsumerInOut;
import org.eclipse.swordfish.core.management.messaging.TestNotificationProcessor;
import org.eclipse.swordfish.core.management.messaging.TestProviderInOut;
import org.eclipse.swordfish.core.management.mock.DummyMessageExchange;
import org.eclipse.swordfish.core.management.mock.DummyNormalizedMessage;
import org.eclipse.swordfish.core.management.notification.impl.ManagementNotificationListenerBean;
import org.eclipse.swordfish.core.management.operations.TestLogHandler;
import org.eclipse.swordfish.core.utils.ExchangeProperties;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class TrackingInterceptorTest.
 */
public class TrackingInterceptorTest extends TestCase {

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The interceptor. */
    private TrackingProcessorBean interceptor;

    /** The listener. */
    private ManagementNotificationListenerBean listener;

    /** The processor. */
    private TestNotificationProcessor processor;

    /** The test exchange. */
    private DummyMessageExchange testExchange;

    /** The handler. */
    private TestLogHandler handler;

    /** The reader. */
    private DOMPolicyReader reader;

    /**
     * Instantiates a new tracking interceptor test.
     * 
     * @param name
     *        the name
     */
    public TrackingInterceptorTest(final String name) {
        super(name);
    }

    /**
     * Test level detail.
     * 
     * @throws InternalSBBException
     */
    public void testLevelDetail() throws InternalSBBException {
        TestConsumerInOut consumerDriver = new TestConsumerInOut(this.listener);
        TestProviderInOut providerDriver = new TestProviderInOut(this.listener);
        InputStream is =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/interceptor/tracking/SampleTracklevel1.xml");
        Document doc = TransformerUtil.docFromInputStream(is);
        Element root = doc.getDocumentElement();
        Policy policy = this.reader.readPolicy(root);
        List terms = policy.getTerms();
        // Consumer out
        String correlationId = consumerDriver.executeBlockingOutPart1();
        this.testExchange.setProperty(ExchangeProperties.CORRELATION_ID, correlationId);
        this.interceptor.handleRequest(this.testExchange, Role.SENDER, terms);
        consumerDriver.executeBlockingOutPart2(correlationId);
        // Provider in
        providerDriver.executeInPart1(correlationId);
        this.interceptor.handleRequest(this.testExchange, Role.RECEIVER, terms);
        providerDriver.executeInPart2(correlationId);
        // Provider out
        String responseId = providerDriver.executeOutPart1(correlationId);
        this.interceptor.handleResponse(this.testExchange, Role.SENDER, terms);
        providerDriver.executeOutPart2(correlationId, responseId);
        // Consumer in
        String msgId = consumerDriver.executeBlockingInPart1(correlationId);
        this.interceptor.handleResponse(this.testExchange, Role.RECEIVER, terms);
        consumerDriver.executeBlockingInPart2(correlationId, msgId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.reader = (DOMPolicyReader) PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);
        this.ctx =
                new FileSystemXmlApplicationContext(
                        new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
        this.interceptor = (TrackingProcessorBean) this.ctx.getBean("org.eclipse.swordfish.core.interceptor.Tracking");
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        Logger logger = Logger.getLogger("operations");
        logger.removeHandler(this.handler);
        this.ctx.destroy();
        super.tearDown();
    }

}
