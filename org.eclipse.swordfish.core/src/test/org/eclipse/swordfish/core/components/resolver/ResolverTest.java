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

import java.util.Collection;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.eclipse.swordfish.papi.internal.exception.InternalServiceDiscoveryException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The Class ResolverTest.
 */
public class ResolverTest extends TestCase {

    /** The ctx. */
    private ClassPathXmlApplicationContext ctx;;

    /** The policy resolver. */
    private PolicyResolver policyResolver;

    /** The sd resolver. */
    private ServiceDescriptionResolver sdResolver;

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.ServiceDescriptionResolverBean.fetchAllServiceDescription(QName,
     * ParticipantPolicy)'
     */
    /**
     * Test fetch all service description.
     */
    public void testFetchAllServiceDescription() {
        String policy = this.policyResolver.resolvePolicyID("foo");
        QName service = new QName("www-deutschepost-de/TestDomain/Library/1.0", "Library");
        try {
            Collection sds = this.sdResolver.fetchAllServiceDescription(service, policy);
            assertNotNull(sds);
        } catch (InternalServiceDiscoveryException e) {
            fail("Unexpected exception.");
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.ServiceDescriptionResolverBean.fetchServiceDescription(QName,
     * ParticipantPolicy)'
     */
    /**
     * Test fetch service description Q name participant policy.
     */
    public void testFetchServiceDescriptionQNameParticipantPolicy() {
        String policy = this.policyResolver.resolvePolicyID("foo");
        QName service = new QName("www-deutschepost-de/TestDomain/Library/1.0", "Library");
        CompoundServiceDescription csd = null;
        ;
        try {
            csd = this.sdResolver.fetchServiceDescription(service, policy);
        } catch (InternalServiceDiscoveryException e) {
            fail("Unexpected exception.");
        }
        assertEquals(csd.getPortTypeQName().getLocalPart(), "Library");
        assertEquals(csd.getPortTypeQName().getNamespaceURI(), "www-deutschepost-de/TestDomain/Library/1.0");
        QName wrong_service = new QName("www-deutschepost-de/TestDomain/Library/1.0", "I_do_not_exist");
        csd = null;
        try {
            csd = this.sdResolver.fetchServiceDescription(wrong_service, policy);
        } catch (InternalServiceDiscoveryException e) {
            assertEquals(InternalServiceDiscoveryException.class, e.getClass());
        }
    }

    /*
     * Test method for
     * 'org.eclipse.swordfish.core.components.resolver.impl.ServiceDescriptionResolverBean.fetchServiceDescription(QName,
     * ParticipantPolicy, String)'
     */
    /**
     * Test fetch service description Q name participant policy string.
     */
    public void testFetchServiceDescriptionQNameParticipantPolicyString() {
        String policy = this.policyResolver.resolvePolicyID("foo");
        QName service = new QName("www-deutschepost-de/TestDomain/Library/1.0", "Library");
        QName providerID = new QName("www-deutschepost-de/TestDomain/Library/1.0_binding", "LibraryService");
        CompoundServiceDescription csd = null;
        ;
        try {
            csd = this.sdResolver.fetchServiceDescription(service, providerID, policy);
        } catch (InternalServiceDiscoveryException e) {
            fail("Unexpected Exception.");
        }
        assertEquals(csd.getPortTypeQName().getLocalPart(), "Library");
        assertEquals(csd.getPortTypeQName().getNamespaceURI(), "www-deutschepost-de/TestDomain/Library/1.0");
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
                        new String[] {"/org/eclipse/swordfish/core/components/resolver/ResolverTestBeanConfig.xml"});
        this.policyResolver = (PolicyResolver) this.ctx.getBean("org.eclipse.swordfish.core.components.resolver.PolicyResolver");
        this.sdResolver =
                (ServiceDescriptionResolver) this.ctx
                    .getBean("org.eclipse.swordfish.core.components.resolver.ServiceDescriptionResolver");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.policyResolver = null;
        this.sdResolver = null;
        this.ctx.destroy();
        super.tearDown();
    }

}
