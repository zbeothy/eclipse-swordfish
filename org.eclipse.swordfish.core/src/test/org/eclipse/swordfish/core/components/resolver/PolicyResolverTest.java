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
package org.eclipse.swordfish.core.components.resolver;

import junit.framework.TestCase;
import org.eclipse.swordfish.papi.internal.exception.ConfigurationException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The Class PolicyResolverTest.
 */
public class PolicyResolverTest extends TestCase {

    /** The ctx. */
    private ClassPathXmlApplicationContext ctx;;

    /** The policy resolver. */
    private PolicyResolver policyResolver;

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.PolicyResolverBean.resolvePolicyId(String)'
     */
    /**
     * Test resolve policy name.
     */
    public void testResolvePolicyName() {
        String policy = this.policyResolver.resolvePolicyID("FastAndFurious");
        assertNotNull(policy);
        policy = null;
        policy = this.policyResolver.resolvePolicyID("SlowAsASnake");
        assertNotNull(policy);
        policy = null;
        try {
            policy = this.policyResolver.resolvePolicyID("IDoNotExist");
        } catch (Exception e) {
            assertEquals(ConfigurationException.class, e.getClass());
        }
        assertNull(policy);
        try {
            policy = this.policyResolver.resolvePolicyID("MyFileDoesNotExist");
        } catch (Exception e) {
            assertEquals(ConfigurationException.class, e.getClass());
        }
        assertNull(policy);
        policy = this.policyResolver.resolvePolicyID(null);
        assertNotNull(policy);
        assertEquals("urn://ConsumerPolicyId#version", policy);
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
                new ClassPathXmlApplicationContext(
                        new String[] {"/org/eclipse/swordfish/core/components/resolver/PolicyResolverTestBeanConfig.xml"});
        this.policyResolver = (PolicyResolver) this.ctx.getBean("org.eclipse.swordfish.core.components.resolver.PolicyResolver");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.policyResolver = null;
        this.ctx.destroy();
        super.tearDown();
    }

}
