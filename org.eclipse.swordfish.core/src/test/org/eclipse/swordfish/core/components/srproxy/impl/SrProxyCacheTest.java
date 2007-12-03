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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.util.HashMap;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.eclipse.swordfish.policytrader.AgreedPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;

/**
 * The Class SrProxyCacheTest.
 */
public class SrProxyCacheTest extends TestCase {

    /** The Constant SERVICE_PORT_TYPE_NAME. */
    public static final QName SERVICE_PORT_TYPE_NAME = new QName("ns", "servicePortTypeName");

    /** The Constant PORT_TYPE_NAME. */
    public static final QName PORT_TYPE_NAME = new QName("ns", "portTypeName");

    /** The Constant CALLBACK_PORT_TYPE_NAME. */
    public static final QName CALLBACK_PORT_TYPE_NAME = new QName("ns", "callbackPortTypeName");

    /** The Constant SERVICE_PORT_TYPE_NAME_1. */
    public static final QName SERVICE_PORT_TYPE_NAME_1 = new QName("ns", "servicePortTypeName1");

    /** The Constant SERVICE_PORT_TYPE_NAME_2. */
    public static final QName SERVICE_PORT_TYPE_NAME_2 = new QName("ns", "servicePortTypeName2");

    /** The Constant SERVICE_NAME_1. */
    public static final String SERVICE_NAME_1 = "{ns}service1";

    /** The Constant CONSUMER_POLICY_ID_11. */
    public static final String CONSUMER_POLICY_ID_11 = "policyId1";

    /** The Constant PROVIDER_NAME_111. */
    public static final String PROVIDER_NAME_111 = "{ns}provider1";

    /** The Constant SERVICE_NAME_2. */
    public static final String SERVICE_NAME_2 = "{ns}service2";

    /** The Constant CONSUMER_POLICY_ID_21. */
    public static final String CONSUMER_POLICY_ID_21 = "policyId21";

    /** The Constant PROVIDER_NAME_211. */
    public static final String PROVIDER_NAME_211 = "{ns}provider211";

    /** The Constant CONSUMER_POLICY_ID_22. */
    public static final String CONSUMER_POLICY_ID_22 = "policyId22";

    /** The Constant PROVIDER_NAME_221. */
    public static final String PROVIDER_NAME_221 = "{ns}provider221";

    /** The Constant PROVIDER_NAME_222. */
    public static final String PROVIDER_NAME_222 = "{ns}provider222";

    /**
     * Test after removing agreed policy getting it from cache fails.
     * 
     * @throws Exception
     */
    public void testAfterRemovingAgreedPolicyGettingItFromCacheFails() throws Exception {
        SrProxyCacheBean underTest = this.newInitializedCache();
        underTest.putAgreedPolicy(this.newAgreedPolicy(SERVICE_NAME_1, CONSUMER_POLICY_ID_11, PROVIDER_NAME_111));
        underTest.putAgreedPolicy(this.newAgreedPolicy(SERVICE_NAME_2, CONSUMER_POLICY_ID_21, PROVIDER_NAME_211));

        AgreedPolicy expectedAgreedPolicy1 = this.newAgreedPolicy(SERVICE_NAME_2, CONSUMER_POLICY_ID_22, PROVIDER_NAME_221);
        AgreedPolicy expectedAgreedPolicy2 = this.newAgreedPolicy(SERVICE_NAME_2, CONSUMER_POLICY_ID_22, PROVIDER_NAME_222);
        underTest.putAgreedPolicy(expectedAgreedPolicy1);
        underTest.putAgreedPolicy(expectedAgreedPolicy2);
        underTest.removeAgreedPolicy(expectedAgreedPolicy1);

        Map expectedAgreedPolicies = new HashMap();
        expectedAgreedPolicies.put(PROVIDER_NAME_222, expectedAgreedPolicy2);

        Map agreedPolicies = underTest.getProviders(SERVICE_NAME_2, CONSUMER_POLICY_ID_22);
        assertEquals(expectedAgreedPolicies, agreedPolicies);
    }

    /**
     * Test get providers where expected policy is in cache.
     * 
     * @throws Exception
     */
    public void testGetProvidersWhereExpectedPolicyIsInCache() throws Exception {
        SrProxyCacheBean underTest = this.newInitializedCache();
        underTest.putAgreedPolicy(this.newAgreedPolicy(SERVICE_NAME_1, CONSUMER_POLICY_ID_11, PROVIDER_NAME_111));
        underTest.putAgreedPolicy(this.newAgreedPolicy(SERVICE_NAME_2, CONSUMER_POLICY_ID_21, PROVIDER_NAME_211));

        AgreedPolicy expectedAgreedPolicy = this.newAgreedPolicy(SERVICE_NAME_2, CONSUMER_POLICY_ID_22, PROVIDER_NAME_221);
        underTest.putAgreedPolicy(expectedAgreedPolicy);

        Map expectedAgreedPolicies = new HashMap();
        expectedAgreedPolicies.put(PROVIDER_NAME_221, expectedAgreedPolicy);

        Map agreedPolicies = underTest.getProviders(SERVICE_NAME_2, CONSUMER_POLICY_ID_22);
        assertEquals(expectedAgreedPolicies, agreedPolicies);
    }

    /**
     * Test get providers where two expected policies are in cache.
     * 
     * @throws Exception
     */
    public void testGetProvidersWhereTwoExpectedPoliciesAreInCache() throws Exception {
        SrProxyCacheBean underTest = this.newInitializedCache();
        underTest.putAgreedPolicy(this.newAgreedPolicy(SERVICE_NAME_1, CONSUMER_POLICY_ID_11, PROVIDER_NAME_111));
        underTest.putAgreedPolicy(this.newAgreedPolicy(SERVICE_NAME_2, CONSUMER_POLICY_ID_21, PROVIDER_NAME_211));

        AgreedPolicy expectedAgreedPolicy1 = this.newAgreedPolicy(SERVICE_NAME_2, CONSUMER_POLICY_ID_22, PROVIDER_NAME_221);
        AgreedPolicy expectedAgreedPolicy2 = this.newAgreedPolicy(SERVICE_NAME_2, CONSUMER_POLICY_ID_22, PROVIDER_NAME_222);
        underTest.putAgreedPolicy(expectedAgreedPolicy1);
        underTest.putAgreedPolicy(expectedAgreedPolicy2);

        Map expectedAgreedPolicies = new HashMap();
        expectedAgreedPolicies.put(PROVIDER_NAME_221, expectedAgreedPolicy1);
        expectedAgreedPolicies.put(PROVIDER_NAME_222, expectedAgreedPolicy2);

        Map agreedPolicies = underTest.getProviders(SERVICE_NAME_2, CONSUMER_POLICY_ID_22);
        assertEquals(expectedAgreedPolicies, agreedPolicies);
    }

    /**
     * Test put valid SDX with callback port type first and service port type.
     * 
     * @throws Exception
     */
    public void testPutValidSDXWithCallbackPortTypeFirstAndServicePortType() throws Exception {
        Definition testSDX =
                new SDXBuilder().callbackPortType(CALLBACK_PORT_TYPE_NAME).forServicePortType(SERVICE_PORT_TYPE_NAME).portType(
                        SERVICE_PORT_TYPE_NAME).sdx();

        SrProxyCacheBean underTest = this.newInitializedCache();
        underTest.putSDX(testSDX);

        assertSame(testSDX, underTest.getSDX(SERVICE_PORT_TYPE_NAME.toString()));
        assertNull(underTest.getSDX(CALLBACK_PORT_TYPE_NAME.toString()));
    }

    /**
     * Test put valid SDX with one port type.
     * 
     * @throws Exception
     */
    public void testPutValidSDXWithOnePortType() throws Exception {

        Definition testSDX = new SDXBuilder().portType(SERVICE_PORT_TYPE_NAME).sdx();

        SrProxyCacheBean underTest = this.newInitializedCache();
        underTest.putSDX(testSDX);
        assertSame(testSDX, underTest.getSDX(SERVICE_PORT_TYPE_NAME.toString()));
    }

    /**
     * Test put valid SDX with two port types.
     * 
     * @throws Exception
     */
    public void testPutValidSDXWithTwoPortTypes() throws Exception {
        Definition testSDX = new SDXBuilder().portType(SERVICE_PORT_TYPE_NAME).portType(PORT_TYPE_NAME).sdx();

        SrProxyCacheBean underTest = this.newInitializedCache();
        underTest.putSDX(testSDX);
        assertSame(testSDX, underTest.getSDX(SERVICE_PORT_TYPE_NAME.toString()));
        assertNull(underTest.getSDX(PORT_TYPE_NAME.toString()));
    }

    /**
     * Test successfull get SDX with two in cache.
     * 
     * @throws Exception
     */
    public void testSuccessfullGetSDXWithTwoInCache() throws Exception {

        Definition testSDX1 = new SDXBuilder().portType(SERVICE_PORT_TYPE_NAME_1).sdx();

        Definition testSDX2 = new SDXBuilder().portType(SERVICE_PORT_TYPE_NAME_2).sdx();

        SrProxyCacheBean underTest = this.newInitializedCache();
        underTest.putSDX(testSDX1);
        underTest.putSDX(testSDX2);

        assertSame(testSDX2, underTest.getSDX(SERVICE_PORT_TYPE_NAME_2.toString()));
    }

    /**
     * New agreed policy.
     * 
     * @param serviceName
     *        the service name
     * @param consumerPolicyId
     *        the consumer policy id
     * @param providerName
     *        the provider name
     * 
     * @return the agreed policy
     */
    AgreedPolicy newAgreedPolicy(final String serviceName, final String consumerPolicyId, final String providerName) {
        return new AgreedPolicyStub() {

            @Override
            public ParticipantPolicyIdentity getConsumerPolicyIdentity() {
                return new ParticipantPolicyIdentityStub() {

                    @Override
                    public String getKeyName() {
                        return consumerPolicyId;
                    }
                };
            }

            @Override
            public String getProvider() {
                return providerName;
            }

            @Override
            public String getService() {
                return serviceName;
            }
        };

    }

    /**
     * New initialized cache.
     * 
     * @return the sr proxy cache bean
     * 
     * @throws Exception
     */
    private SrProxyCacheBean newInitializedCache() throws Exception {
        SrProxyCacheBean cache = new SrProxyCacheBean();
        cache.setComponentContextAccess(new StubComponentContextAccess());
        cache.init();
        return cache;
    }

    /**
     * The Class SDXBuilder.
     */
    public static class SDXBuilder {

        /** The sdx. */
        private Definition sdx;

        /** The callback port type. */
        private PortType callbackPortType;

        /**
         * Instantiates a new SDX builder.
         */
        public SDXBuilder() {
            try {
                WSDLFactory factory = WSDLFactory.newInstance();
                this.sdx = factory.newDefinition();
                ExtensionRegistry reg = factory.newPopulatedExtensionRegistry();
                reg.mapExtensionTypes(Definition.class, new QName("http://schemas.xmlsoap.org/ws/2003/05/partner-link/",
                        "partnerLinkType"), PartnerLinkTypeImpl.class);
            } catch (WSDLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Callback port type.
         * 
         * @param callbackPortTypeName
         *        the callback port type name
         * 
         * @return the SDX builder
         */
        public SDXBuilder callbackPortType(final QName callbackPortTypeName) {
            this.callbackPortType = this.createPortType(callbackPortTypeName);
            return this;
        }

        /**
         * For service port type.
         * 
         * @param servicePortTypeName
         *        the service port type name
         * 
         * @return the SDX builder
         */
        public SDXBuilder forServicePortType(final QName servicePortTypeName) {
            PartnerLinkTypeImpl linkType = new PartnerLinkTypeImpl();
            linkType.setName("CallbackPartnerLink");

            PartnerLinkRoleImpl serviceRole = new PartnerLinkRoleImpl();
            serviceRole.setName("service");
            serviceRole.setPortTypeQName(servicePortTypeName);
            linkType.addPartnerLinkRole(serviceRole);

            PartnerLinkRoleImpl callbackRole = new PartnerLinkRoleImpl();
            callbackRole.setName("callback");
            callbackRole.setPortTypeQName(this.callbackPortType.getQName());
            linkType.addPartnerLinkRole(callbackRole);

            this.sdx.addExtensibilityElement(linkType);
            return this;
        }

        /**
         * Sdx.
         * 
         * @return the definition
         */
        public Definition sdx() {
            return this.sdx;
        }

        /**
         * Port type.
         * 
         * @param portTypeName
         *        the port type name
         * 
         * @return the SDX builder
         */
        SDXBuilder portType(final QName portTypeName) {
            this.createPortType(portTypeName);
            return this;
        }

        /**
         * Creates the port type.
         * 
         * @param portTypeName
         *        the port type name
         * 
         * @return the port type
         */
        private PortType createPortType(final QName portTypeName) {
            PortType portType = this.sdx.createPortType();
            portType.setQName(portTypeName);
            this.sdx.addPortType(portType);
            return portType;
        }
    }

    /**
     * The Class StubComponentContextAccess.
     */
    public static class StubComponentContextAccess extends ComponentContextAccessStub {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swordfish.core.components.srproxy.impl.ComponentContextAccessStub#getInstallRoot()
         */
        @Override
        public String getInstallRoot() {
            return "installRoot";
        }
    }
}
