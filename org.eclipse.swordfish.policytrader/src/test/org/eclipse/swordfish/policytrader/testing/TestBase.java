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
package org.eclipse.swordfish.policytrader.testing;

import java.util.Date;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;
import org.eclipse.swordfish.policytrader.PolicyTrader;
import org.eclipse.swordfish.policytrader.callback.PolicyTradingListener;
import org.eclipse.swordfish.policytrader.impl.PolicyTraderImpl;
import org.eclipse.swordfish.policytrader.impl.XMLStreamToDOMWriter;
import org.w3c.dom.Document;

/**
 * Base class for policy trader unit tests.
 */
public abstract class TestBase extends TestCase {

    /**
     * Consumer-side test participant policy id.
     */
    protected static final String CONSUMER_PPURI = "http://policies.test.org/participant/TestConsumerPolicy";

    /**
     * Provider-side test participant policy id.
     */
    protected static final String PROVIDER_PPURI = "http://policies.test.org/participant/TestProviderPolicy";

    protected static final String SUCCESS = "success";

    protected static final String FAILURE = "failure";

    protected static final String UNUSED = "unused";

    protected static final String DEFAULT_OP = "@AnyOperation@";

    protected static final CountDown cnt(final int start) {
        return new CountDown(start);
    }

    /**
     * Policy trader to be tested.
     */
    protected final PolicyTrader policyTrader = new PolicyTraderImpl();

    /**
     * Policy factory of policy trader.
     */
    protected final PolicyFactory policyFactory = this.policyTrader.getPolicyFactory();

    /**
     * Policy resolver initialized and set into policy trader. (happens in TestBase constructor)
     */
    protected final PolicyResolverImpl policyResolver = new PolicyResolverImpl(this.policyFactory);

    protected final PRListener standardPRListener = new PRListener() {

        public void operationPolicyResolved(final OperationPolicyIdentity id, final OperationPolicy result) {
            System.out.println("Operation policy resolved: " + id.getLocation());
            System.out.println();
            if (TestBase.this.expectedPR != null) {
                final CountDown c = (CountDown) TestBase.this.expectedPR.get(id);
                if ((null == c) || !c.dec()) throw new RuntimeException("Excess call to policy resolution: " + id.getLocation());
            }
        }

        public void participantPolicyResolved(final ParticipantPolicyIdentity id, final ParticipantPolicy result) {
            System.out.println("Participant policy resolved: " + id.getLocation());
            System.out.println();
            if (TestBase.this.expectedPR != null) {
                final CountDown c = (CountDown) TestBase.this.expectedPR.get(id);
                if ((null == c) || !c.dec()) throw new RuntimeException("Excess call to policy resolution: " + id.getLocation());
            }
        }
    };

    protected final PTListener standardPTListener = new PTListener() {

        public void agreementFailedAtOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
                final OperationPolicyIdentity providerOpPolicyID) {
            System.err.println("Operation policy trading failed");
            System.err.println("Operation name: " + operationName);
            System.err.println("Consumer-side operation policy");
            System.err.println("    URI: " + consumerOpPolicyID.getKeyName());
            System.err.println("    Location: " + consumerOpPolicyID.getLocation());
            System.err.println("Provider-side operation policy");
            System.err.println("    URI: " + providerOpPolicyID.getKeyName());
            System.err.println("    Location: " + providerOpPolicyID.getLocation());
            System.err.println();
            if (TestBase.this.expectedPT != null) {
                final String agreed = (String) TestBase.this.expectedPT.get(operationName);
                if (!FAILURE.equals(agreed)) throw new RuntimeException("Unexpected failure at operation " + operationName);
            }
            if (TestBase.this.expectedPTCLocs != null) {
                final String location = (String) TestBase.this.expectedPTCLocs.get(operationName);
                if ((location == null) || !location.equals(consumerOpPolicyID.getLocation()))
                    throw new RuntimeException("Unexpected consumer policy for operation " + operationName);
            }
            if (TestBase.this.expectedPTPLocs != null) {
                final String location = (String) TestBase.this.expectedPTPLocs.get(operationName);
                if ((location == null) || !location.equals(providerOpPolicyID.getLocation()))
                    throw new RuntimeException("Unexpected provider policy for operation " + operationName);
            }
        }

        public void agreementSucceededAtOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
                final OperationPolicyIdentity providerOpPolicyID) {
            System.out.println("Operation policy trading succeeded");
            System.out.println("Operation name: " + operationName);
            System.out.println("Consumer-side operation policy");
            System.out.println("    URI: " + consumerOpPolicyID.getKeyName());
            System.out.println("    Location: " + consumerOpPolicyID.getLocation());
            System.out.println("Provider-side operation policy");
            System.out.println("    URI: " + providerOpPolicyID.getKeyName());
            System.out.println("    Location: " + providerOpPolicyID.getLocation());
            System.out.println();
            if (TestBase.this.expectedPT != null) {
                final String agreed = (String) TestBase.this.expectedPT.get(operationName);
                if (!SUCCESS.equals(agreed)) throw new RuntimeException("Unexpected success at operation " + operationName);
            }
            if (TestBase.this.expectedPTCLocs != null) {
                final String location = (String) TestBase.this.expectedPTCLocs.get(operationName);
                if ((location == null) || !location.equals(consumerOpPolicyID.getLocation()))
                    throw new RuntimeException("Unexpected consumer policy for operation " + operationName);
            }
            if (TestBase.this.expectedPTPLocs != null) {
                final String location = (String) TestBase.this.expectedPTPLocs.get(operationName);
                if ((location == null) || !location.equals(providerOpPolicyID.getLocation()))
                    throw new RuntimeException("Unexpected provider policy for operation " + operationName);
            }
        }

        public void agreementUnusedOperation(final String operationName, final OperationPolicyIdentity consumerOpPolicyID,
                final OperationPolicyIdentity providerOpPolicyID) {
            System.out.println("Operation flagged \"unused\" in policy");
            System.out.println("Operation name: " + operationName);
            System.out.println("Consumer-side operation policy");
            System.out.println("    URI: " + consumerOpPolicyID.getKeyName());
            System.out.println("    Location: " + consumerOpPolicyID.getLocation());
            System.out.println("Provider-side operation policy");
            System.out.println("    URI: " + providerOpPolicyID.getKeyName());
            System.out.println("    Location: " + providerOpPolicyID.getLocation());
            System.out.println();
            if (TestBase.this.expectedPT != null) {
                final String agreed = (String) TestBase.this.expectedPT.get(operationName);
                if (!UNUSED.equals(agreed)) throw new RuntimeException("Unexpected unused operation " + operationName);
            }
            if (TestBase.this.expectedPTCLocs != null) {
                final String location = (String) TestBase.this.expectedPTCLocs.get(operationName);
                if ((location == null) || !location.equals(consumerOpPolicyID.getLocation()))
                    throw new RuntimeException("Unexpected consumer policy for operation " + operationName);
            }
            if (TestBase.this.expectedPTPLocs != null) {
                final String location = (String) TestBase.this.expectedPTPLocs.get(operationName);
                if ((location == null) || !location.equals(providerOpPolicyID.getLocation()))
                    throw new RuntimeException("Unexpected provider policy for operation " + operationName);
            }
        }
    };

    /**
     * Number of calls for each operation. Key=operationName, Value=CountDown
     */
    protected Map expectedPR = null;

    /**
     * Expected results of trading for each operation. Key=operationName,
     * Value=StringConstant(SUCCESS, FAILURE, UNUSED)
     */
    protected Map expectedPT = null;

    /**
     * Expected consumer policy id location for trading of each operation. Key=operationName,
     * Value=String(consumerPolicyId.location)
     */
    protected Map expectedPTCLocs = null;

    /**
     * Expected provider policy id location for trading of each operation. Key=operationName,
     * Value=String(providerPolicyId.location)
     */
    protected Map expectedPTPLocs = null;

    /**
     * Internal factory for DOM documents.
     */
    private final DocumentBuilderFactory builderFactory = this.initDocumentBuilderFactory();

    /**
     * Constructor.
     * 
     * @param name
     *        test name
     */
    protected TestBase(final String name) {
        super(name);
        this.policyTrader.setPolicyResolver(this.policyResolver);
        this.policyResolver.setListener(this.standardPRListener);
        this.policyTrader.addListener(this.standardPTListener);
    }

    /**
     * Utility method for creation of new empty DOM documents.
     * 
     * @return new Document
     */
    protected final Document createDocument() {
        try {
            return this.builderFactory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Utility method for creation of new StAX to DOM writer initialized with an empty Document.
     * 
     * @return new StAX to DOM writer
     */
    protected final XMLStreamToDOMWriter createXMLStreamToDOMWriter() {
        return new XMLStreamToDOMWriter(this.createDocument());
    }

    protected final void printOut(final AgreedPolicy ap) throws Exception {
        final Date d1 = new Date();
        final Date d2 = new Date(d1.getTime() + 100000L);
        ap.setValid(d1, d2);
        final XMLStreamToDOMWriter writer = this.createXMLStreamToDOMWriter();
        ap.writeTo(writer);
        System.out.println(XPrinter.toString(writer.getDocument()));
        /*
         * final XMLStreamToDOMWriter cwriter = createXMLStreamToDOMWriter();
         * ap.writeClassicTo(cwriter); System.out.println(); System.out.println("***** Classic
         * Agreed Policy *****"); System.out.println();
         * System.out.println(XPrinter.toString(cwriter.getDocument()));
         */
    }

    /**
     * Internal DOM factory initialization.
     * 
     * @return DOM factory
     */
    private DocumentBuilderFactory initDocumentBuilderFactory() {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
            bf.setValidating(false);
            bf.setNamespaceAware(true);
            bf.setIgnoringElementContentWhitespace(true);
            return bf;
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Test helper.
     */
    protected static class CountDown {

        private int cnt;

        public CountDown(final int start) {
            super();
            this.cnt = start;
        }

        public boolean dec() {
            return (--this.cnt) >= 0;
        }
    }

    /**
     * Convenience interface.
     */
    protected static interface PRListener extends PolicyResolverImpl.Listener {
    }

    /**
     * Convenience interface.
     */
    protected static interface PTListener extends PolicyTradingListener {
    }
}
