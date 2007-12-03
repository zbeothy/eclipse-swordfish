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
package org.eclipse.swordfish.policytrader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.xml.sax.SAXException;

public class AgreedPolicyFactoryTest extends TestCase {

    private AgreedPolicyFactory factory;

    public AgreedPolicyFactoryTest(final String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.factory = AgreedPolicyFactory.getInstance();
    }

    public void testGetReducedPolicy() throws SAXException, IOException {
        InputStream is = this.getClass().getResourceAsStream("/org/eclipse/swordfish/policytrader/testing/TestAgreed1.xml");
        AgreedPolicy agreed = this.factory.createFrom(is);
        AgreedPolicy test = agreed.getReducedAgreedPolicy("seekBook");
        Policy operationPolicy = test.getOperationPolicy("seekBook");
        assertNotNull(operationPolicy);
        Map map = ((AbstractAgreedPolicy) test).getOperationPolicies();
        assertEquals(1, map.size());
        String providerId = test.getProviderPolicyIdentity().getKeyName();
        assertEquals("http://policies.test.org/participant/TestProviderPolicy", providerId);
    }

    public void testInstantiation() throws SAXException, IOException {
        InputStream is = this.getClass().getResourceAsStream("/org/eclipse/swordfish/policytrader/testing/TestAgreed1.xml");
        AgreedPolicy agreed = this.factory.createFrom(is);
        Policy operationPolicy = agreed.getOperationPolicy("seekBook");
        assertNotNull(operationPolicy);
        String providerId = agreed.getProviderPolicyIdentity().getKeyName();
        assertEquals("http://policies.test.org/participant/TestProviderPolicy", providerId);
    }

}
