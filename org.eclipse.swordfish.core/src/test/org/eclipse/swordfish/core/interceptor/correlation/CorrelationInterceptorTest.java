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
package org.eclipse.swordfish.core.interceptor.correlation;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.NormalizedMessage;
import junit.framework.TestCase;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.DOMPolicyReader;
import org.apache.ws.policy.util.PolicyFactory;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.interceptor.correlation.impl.CorrelationProcessorBean;
import org.eclipse.swordfish.core.management.mock.DummyMessageExchange;
import org.eclipse.swordfish.core.management.mock.DummyNormalizedMessage;
import org.eclipse.swordfish.core.management.operations.TestLogHandler;
import org.eclipse.swordfish.core.management.operations.impl.OperationalMessageRecord;
import org.eclipse.swordfish.core.utils.TransformerUtil;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class CorrelationInterceptorTest.
 */
public class CorrelationInterceptorTest extends TestCase {

    /** The test handler. */
    private TestLogHandler testHandler;

    /** The test exchange. */
    private MessageExchange testExchange;

    /** The processor. */
    private CorrelationProcessorBean processor;

    /** The ctx. */
    private FileSystemXmlApplicationContext ctx;

    /** The reader. */
    private DOMPolicyReader reader;

    /**
     * Instantiates a new correlation interceptor test.
     * 
     * @param name
     *        the name
     */
    public CorrelationInterceptorTest(final String name) {
        super(name);
    }

    /**
     * Test request.
     * 
     * @throws InternalSBBException
     */
    public void testRequest() throws InternalSBBException {
        InputStream is =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/interceptor/correlation/SampleCorrelations1.xml");
        Document doc = TransformerUtil.docFromInputStream(is);
        Element root = doc.getDocumentElement();
        Policy policy = this.reader.readPolicy(root);
        List terms = policy.getTerms();
        assertEquals(0, this.testHandler.getRecords().size());
        this.processor.handleRequest(this.testExchange, Role.SENDER, terms);
        assertEquals(2, this.testHandler.getRecords().size());
        int orderIdCount = 0;
        int customerCount = 0;
        for (Iterator iter = this.testHandler.getRecords().iterator(); iter.hasNext();) {
            OperationalMessageRecord record = (OperationalMessageRecord) iter.next();
            Object[] params = record.getParameters();
            if ("OrderID".equals(params[0])) {
                assertEquals("id=1234", params[3]);
                orderIdCount++;
            } else if ("CustomerOrder".equals(params[0])) {
                assertEquals("customer=Foo Inc.,order=42", params[3]);
                customerCount++;
            }
        }
        assertEquals(1, orderIdCount);
        assertEquals(1, customerCount);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.ctx =
                new FileSystemXmlApplicationContext(
                        new String[] {"src/test/org/eclipse/swordfish/core/management/ManagementTestBeanConfig.xml"});
        this.testHandler = (TestLogHandler) this.ctx.getBean("test.operations.TestHandler");
        Logger logger = Logger.getLogger("operations");
        logger.addHandler(this.testHandler);
        this.processor = (CorrelationProcessorBean) this.ctx.getBean("org.eclipse.swordfish.core.interceptor.Correlation");
        this.testExchange = new DummyMessageExchange();
        InputStream messageStream =
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/eclipse/swordfish/core/interceptor/correlation/SampleMessage1.xml");
        NormalizedMessage message = new DummyNormalizedMessage(messageStream);
        this.testExchange.setMessage(message, "in");
        this.testExchange.setMessage(message, "out");
        this.reader = (DOMPolicyReader) PolicyFactory.getPolicyReader(PolicyFactory.DOM_POLICY_READER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.ctx.destroy();
        super.tearDown();
    }

}
