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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.PolicyTrader;
import org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedPolicyException;
import org.eclipse.swordfish.policytrader.impl.AgreedPolicyFactory;
import org.eclipse.swordfish.policytrader.impl.StandardServiceDescriptionIdentity;
import org.xml.sax.SAXException;

public class PolicyTraderTest extends TestBase {

    private static final String EXAMPLE_SERVICE = "{http://services.sopware.org/ExampleURI}ExampleService";

    private static final String LIBRARY_SERVICE = "{http://services.sopware.org/demos/Library/1.0}Library";

    public PolicyTraderTest(final String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testCustomExpiryDates() throws SAXException, IOException, ParserConfigurationException, BackendException,
            CorruptedPolicyException {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "unmentionedOp"});
        Properties props = new Properties();
        long validityDuration = 7200000;
        validityDuration *= 1000;
        props.put(PolicyTrader.VALIDITY_DURATION, String.valueOf(validityDuration));
        this.policyTrader.setProperties(props);
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        Date validSince = result.validSince();
        Date validThrough = result.validThrough();
        assertTrue(validSince.before(validThrough));
        long since = validSince.getTime();
        long through = validThrough.getTime();
        assertTrue(since + validityDuration == through);
        result.writeTo(System.out);
    }

    public void testExpiryDateSerialization() throws BackendException, CorruptedPolicyException, IOException, SAXException,
            ParserConfigurationException {
        final ParticipantPolicyIdentity consumerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_1.ppdx");
        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "unmentionedOp"});
        final AgreedPolicy result = this.policyTrader.tradePolicies(consumerPolicyID, providerPolicyID);
        Date dateBefore = result.validSince();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.writeTo(bos);
        String resultString = bos.toString();
        InputStream is = new ByteArrayInputStream(resultString.getBytes());
        AgreedPolicy newResult = AgreedPolicyFactory.getInstance().createFrom(is);
        Date dateAfter = newResult.validSince();
        // milliseconds don't make it through the serialization
        long secondsBefore = dateBefore.getTime() / 1000;
        long secondsAfter = dateAfter.getTime() / 1000;
        assertEquals(secondsBefore, secondsAfter);
    }

    public void testFailCachedTradingResult() throws BackendException, CorruptedPolicyException {

        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "unmentionedOp"});

        final ServiceDescriptionIdentity sid1 = new StandardServiceDescriptionIdentity(LIBRARY_SERVICE);
        final ParticipantPolicyIdentity consumerPolicyID1 =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID1 =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_1.ppdx");
        final AgreedPolicy result1 = this.policyTrader.tradePolicies(consumerPolicyID1, providerPolicyID1, sid1);

        final ServiceDescriptionIdentity sid2 = new StandardServiceDescriptionIdentity(EXAMPLE_SERVICE);
        final ParticipantPolicyIdentity consumerPolicyID2 =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID2 =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_1.ppdx");
        final AgreedPolicy result2 = this.policyTrader.tradePolicies(consumerPolicyID2, providerPolicyID2, sid2);

        assertFalse(result1.equals(result2));
    }

    public void testGetCachedTradingResult() throws BackendException, CorruptedPolicyException {

        this.policyResolver.setOperationNames(new String[] {"secureInsertOp", "unmentionedOp"});
        final ServiceDescriptionIdentity sid = new StandardServiceDescriptionIdentity(LIBRARY_SERVICE);

        final ParticipantPolicyIdentity consumerPolicyID1 =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID1 =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_1.ppdx");
        final AgreedPolicy result1 = this.policyTrader.tradePolicies(consumerPolicyID1, providerPolicyID1, sid);

        final ParticipantPolicyIdentity consumerPolicyID2 =
                this.policyFactory.createParticipantPolicyIdentity(CONSUMER_PPURI, "TestDefaultOpCP_1.ppdx");
        final ParticipantPolicyIdentity providerPolicyID2 =
                this.policyFactory.createParticipantPolicyIdentity(PROVIDER_PPURI, "TestDefaultOpPP_1.ppdx");
        final AgreedPolicy result2 = this.policyTrader.tradePolicies(consumerPolicyID2, providerPolicyID2, sid);

        assertEquals(result1, result2);
    }

}
