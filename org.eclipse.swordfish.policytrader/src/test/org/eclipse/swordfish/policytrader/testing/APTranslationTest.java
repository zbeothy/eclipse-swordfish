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

import java.io.InputStream;
import java.util.List;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.util.PolicyReader;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.impl.XMLStreamToDOMWriter;
import org.eclipse.swordfish.policytrader.impl.assertiontransformation.AbstractAssertionBag;
import org.eclipse.swordfish.policytrader.impl.assertiontransformation.AssertionTransformer;
import org.eclipse.swordfish.policytrader.impl.assertiontransformation.ClassicOperationPolicy;
import org.eclipse.swordfish.policytrader.impl.assertiontransformation.PolicyAssertionBag;
import org.eclipse.swordfish.policytrader.impl.assertiontransformation.QKey;
import org.w3c.dom.Document;

/**
 * !TODO: provide test for top-level primitive assertions analogous to testBags to improve
 * readability
 * 
 * 
 */
public class APTranslationTest extends TestBase {

    public APTranslationTest(final String name) {
        super(name);
    }

    public void testAP1() throws Exception {
        final ClassicOperationPolicy cp = this.doTest("APTest_1.wsp");
        this.testBags(cp, AssertionTransformer.CLASSIC_TRANSPORT_BAG, AssertionTransformer.CLASSIC_TRANSPORT, 4, 0, 4, 0);
        this.testBags(cp, AssertionTransformer.CLASSIC_TRANSFORMATION_BAG, AssertionTransformer.CLASSIC_TRANSFORMATION, 1, 0, 0, 1);
        this.testBags(cp, AssertionTransformer.CLASSIC_CUSTOM_VALIDATION_BAG, AssertionTransformer.CLASSIC_CUSTOM_VALIDATION, 0, 1,
                0, 0);
        this.testBags(cp, AssertionTransformer.CLASSIC_AUTHENTICATION_BAG, AssertionTransformer.CLASSIC_AUTHENTICATION, 1, 1, 0, 0);
        this.testPAB(cp.getRequestSender(), 1, 2, 1, 0);
        this.testPAB(cp.getRequestReceiver(), 1, 2, 0, 1);
        this.testPAB(cp.getResponseSender(), 1, 2, 0, 0);
        this.testPAB(cp.getResponseReceiver(), 1, 2, 1, 0);
    }

    public void testAP2() throws Exception {
        final ClassicOperationPolicy cp = this.doTest("APTest_2.wsp");
        this.testBags(cp, AssertionTransformer.CLASSIC_TRANSPORT_BAG, AssertionTransformer.CLASSIC_TRANSPORT, 4, 0, 4, 0);
        this.testBags(cp, AssertionTransformer.CLASSIC_TRANSFORMATION_BAG, AssertionTransformer.CLASSIC_TRANSFORMATION, 0, 1, 1, 0);
        this.testBags(cp, AssertionTransformer.CLASSIC_CUSTOM_VALIDATION_BAG, AssertionTransformer.CLASSIC_CUSTOM_VALIDATION, 1, 1,
                1, 1);
        this.testBags(cp, AssertionTransformer.CLASSIC_AUTHENTICATION_BAG, AssertionTransformer.CLASSIC_AUTHENTICATION, 1, 1, 1, 1);
        this.testPAB(cp.getRequestSender(), 1, 2, 0, 1);
        this.testPAB(cp.getRequestReceiver(), 1, 2, 1, 1);
        this.testPAB(cp.getResponseSender(), 1, 2, 1, 0);
        this.testPAB(cp.getResponseReceiver(), 1, 2, 0, 0);
    }

    private ClassicOperationPolicy doTest(final String policyXml) throws Exception {
        final Policy p = this.getPolicy(policyXml);
        final ClassicOperationPolicy cp = new ClassicOperationPolicy();
        cp.apply(p);
        final Document doc = this.createDocument();
        final XMLStreamToDOMWriter w = new XMLStreamToDOMWriter(doc);
        w.writeStartElement("", AgreedPolicy.OPERATION_POLICY_TAG, AgreedPolicy.AGREED_POLICY_CLASSIC_NAMESPACE);
        w.writeDefaultNamespace(AgreedPolicy.AGREED_POLICY_CLASSIC_NAMESPACE);
        w.setDefaultNamespace(AgreedPolicy.AGREED_POLICY_CLASSIC_NAMESPACE);
        w.writeNamespace("sopa", AgreedPolicy.CLASSIC_ASSERTION_NAMESPACE);
        w.setPrefix("sopa", AgreedPolicy.CLASSIC_ASSERTION_NAMESPACE);
        cp.writeTo(w);
        w.writeEndElement();
        System.out.println(XPrinter.toString(doc));
        return cp;
    }

    private Policy getPolicy(final String resourceName) throws Exception {
        final PolicyReader pr =
                org.apache.ws.policy.util.PolicyFactory.getPolicyReader(org.apache.ws.policy.util.PolicyFactory.DOM_POLICY_READER);
        final InputStream is = this.getClass().getResourceAsStream(resourceName);
        return pr.readPolicy(is);
    }

    private void testBag(final PolicyAssertionBag policyPart, final QKey bagName, final QKey assertionName, final int expectedSize) {
        final List bags = policyPart.getAssertions(bagName);
        final List bagContent;
        if (expectedSize > 0) {
            assertEquals(1, bags.size());
            bagContent = ((AbstractAssertionBag) bags.get(0)).getAssertions(assertionName);
            assertEquals(expectedSize, bagContent.size());
        } else {
            assertEquals(0, bags.size());
            bagContent = null;
        }

    }

    private void testBags(final ClassicOperationPolicy cp, final QKey bagName, final QKey assertionName, final int expectReqSnd,
            final int expectReqRcv, final int expectResSnd, final int expectResRcv) {
        this.testBag(cp.getRequestSender(), bagName, assertionName, expectReqSnd);
        this.testBag(cp.getRequestReceiver(), bagName, assertionName, expectReqRcv);
        this.testBag(cp.getResponseSender(), bagName, assertionName, expectResSnd);
        this.testBag(cp.getResponseReceiver(), bagName, assertionName, expectResRcv);
    }

    private void testPAB(final PolicyAssertionBag bag, final int tlsize, final int exsize, final int mrsize,
            final int authorizationSize) throws Exception {
        final List trackingLevels = bag.getAssertions(AssertionTransformer.CLASSIC_TRACKING_LEVEL);
        final List extensions = bag.getAssertions(AssertionTransformer.CLASSIC_EXTENSION);
        final List maxResponseTimes = bag.getAssertions(AssertionTransformer.CLASSIC_MAX_RESPONSE_TIME);
        final List authorizations = bag.getAssertions(AssertionTransformer.CLASSIC_AUTHORIZATION);
        assertEquals(tlsize, trackingLevels.size());
        assertEquals(exsize, extensions.size());
        assertEquals(mrsize, maxResponseTimes.size());
        assertEquals(authorizationSize, authorizations.size());
    }
}
