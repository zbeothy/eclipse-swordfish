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
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyFactory;
import org.eclipse.swordfish.policytrader.PolicyTrader;
import org.eclipse.swordfish.policytrader.callback.PolicyResolver;
import org.eclipse.swordfish.policytrader.callback.PolicyTradingListener;
import org.eclipse.swordfish.policytrader.impl.PolicyTraderImpl;
import org.eclipse.swordfish.policytrader.impl.XMLStreamToDOMWriter;
import org.w3c.dom.Document;

/**
 */
public class PolicyTraderDemo {

    /**
     * Main.
     * 
     * @param args
     *        command line arguments
     */
    public static void main(final String[] args) {
        PolicyTraderDemo demo = new PolicyTraderDemo(args);
        try {
            demo.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private final Map argMap = new HashMap();

    private final DocumentBuilderFactory builderFactory;

    /**
     * 
     */
    public PolicyTraderDemo(final String[] args) {
        super();
        this.builderFactory = this.initDocumentBuilderFactory();
        for (int i = 0; i < args.length; i++) {
            final String s = args[i];
            final int ndx = s.indexOf('=');
            if (ndx > 0) {
                final String key = s.substring(0, ndx);
                final String value = s.substring(ndx + 1);
                if (value.length() > 0) {
                    this.argMap.put(key, value);
                }
            }
        }
    }

    public void run() throws Exception {
        final PolicyTrader policyTrader = new PolicyTraderImpl();
        final PolicyFactory factory = policyTrader.getPolicyFactory();
        final PolicyResolver policyResolver = new PolicyResolverImpl(factory);
        policyTrader.setPolicyResolver(policyResolver);
        policyTrader.addListener(new PolicyTradingListener() {

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
            }

        });
        final ParticipantPolicyIdentity providerPolicyID =
                factory.createParticipantPolicyIdentity(this.getArg("ppURI"), this.getArg("ppLocation"));
        final ParticipantPolicyIdentity consumerPolicyID =
                factory.createParticipantPolicyIdentity(this.getArg("cpURI"), this.getArg("cpLocation"));
        final AgreedPolicy result = policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        System.out.println("Agreed Policy");
        if (result == ParticipantPolicy.FAILED_AGREEMENT_POLICY) {
            System.out.println("*** Failed Agreement ***");
        } else {
            final Date d1 = new Date();
            Thread.sleep(1000L);
            final Date d2 = new Date();
            result.setValid(d1, d2);
            final Document doc = this.builderFactory.newDocumentBuilder().newDocument();
            final XMLStreamToDOMWriter writer = new XMLStreamToDOMWriter(doc);
            result.writeTo(writer);
            System.out.println(XPrinter.toString(doc));
            this.builderFactory.newDocumentBuilder().newDocument();
            /*
             * final XMLStreamToDOMWriter cwriter = new XMLStreamToDOMWriter(cdoc);
             * result.writeClassicTo(cwriter); System.out.println(); System.out.println("*****
             * Classic Agreed Policy *****"); System.out.println();
             * System.out.println(XPrinter.toString(cdoc));
             */
        }
    }

    private String getArg(final String key) {
        return (String) this.argMap.get(key);
    }

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
}
