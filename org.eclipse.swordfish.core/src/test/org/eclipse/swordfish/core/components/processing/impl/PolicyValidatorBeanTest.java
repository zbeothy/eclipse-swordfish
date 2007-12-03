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
package org.eclipse.swordfish.core.components.processing.impl;

import java.security.NoSuchAlgorithmException;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.core.components.processing.PolicyViolatedException;
import org.eclipse.swordfish.core.components.resolver.mock.MockPolicyResolver;
import org.eclipse.swordfish.policytrader.OperationPolicy;
import org.eclipse.swordfish.policytrader.OperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.ParticipantPolicy;
import org.eclipse.swordfish.policytrader.ParticipantPolicyIdentity;
import org.eclipse.swordfish.policytrader.exceptions.BackendException;
import org.eclipse.swordfish.policytrader.exceptions.CorruptedSourceException;
import org.eclipse.swordfish.policytrader.exceptions.UnreadableSourceException;
import org.eclipse.swordfish.policytrader.impl.StandardOperationPolicyIdentity;
import org.eclipse.swordfish.policytrader.impl.StandardParticipantPolicyIdentity;

/**
 * The Class PolicyValidatorBeanTest.
 */
public class PolicyValidatorBeanTest extends TestCase {

    /** The Constant DEFAULT_PROVIDER_PPDX. */
    private static final String DEFAULT_PROVIDER_PPDX = "http://types.sopware.org/qos/ParticipantPolicy/1.1/DefaultProviderPolicy";

    /** The Constant DEFAULT_PROVIDER_OPDX. */
    private static final String DEFAULT_PROVIDER_OPDX =
            "http://types.sopware.org/qos/ParticipantPolicy/1.1/DefaultProviderPolicy_defaultOperation";

    /** The Constant AUTHENTICATED_PROVIDER_PPDX. */
    private static final String AUTHENTICATED_PROVIDER_PPDX =
            "http://policies.sopware.org/security/AuthorizationProvider/1.0/AuthorizationProvider/secureAccess";

    /** The Constant AUTHENTICATED_PROVIDER_OPDX. */
    private static final String AUTHENTICATED_PROVIDER_OPDX =
            "http://policies.sopware.org/TechnicalServiceProvider/1.0/AuthenticatedSyncOperation";

    /** The Constant VALIDATED_PROVIDER_OPDX. */
    private static final String VALIDATED_PROVIDER_OPDX =
            "http://policies.sopware.org/TechnicalServiceProvider/1.0/ValidatedSyncOperation";

    /** The Constant SECURED_PROVIDER_PPDX. */
    private static final String SECURED_PROVIDER_PPDX =
            "http://policies.sopware.org/security/AuthorizationProvider/1.0/SecureProvider";

    /** The Constant AUTHORIZED_PROVIDER_OPDX. */
    private static final String AUTHORIZED_PROVIDER_OPDX =
            "http://policies.sopware.org/TechnicalServiceProvider/1.0/AuthorizedSyncOperation";

    /** The Constant SIGNED_PROVIDER_OPDX. */
    private static final String SIGNED_PROVIDER_OPDX =
            "http://policies.sopware.org/TechnicalServiceProvider/1.0/SignedSyncOperation";

    /** The Constant ENCRYPTED_PROVIDER_OPDX. */
    private static final String ENCRYPTED_PROVIDER_OPDX =
            "http://policies.sopware.org/TechnicalServiceProvider/1.0/EncryptedSyncOperation";

    /** The Constant PROVIDER_1. */
    private static final QName PROVIDER_1 = new QName("SampleProvider");

    /** The Constant SERVICE. */
    private static final QName SERVICE = new QName("SampleService");

    /** The Constant AGREED_HTTP_ONLY. */
    private static final String AGREED_HTTP_ONLY = "http_only";

    /** The Constant AGREED_FORGED_AUTHENTICATION. */
    private static final String AGREED_FORGED_AUTHENTICATION = "forged_authentication";

    /** The Constant AGREED_AUTHENTICATION. */
    private static final String AGREED_AUTHENTICATION = "authentication";

    /** The Constant AGREED_AUTHORIZATION. */
    private static final String AGREED_AUTHORIZATION = "authorization";

    /** The Constant AGREED_SIGNED. */
    private static final String AGREED_SIGNED = "signed";

    /** The Constant AGREED_SIGNED_FORGED. */
    private static final String AGREED_SIGNED_FORGED = "signed_forged";

    /** The Constant AGREED_ENCRYPTION. */
    private static final String AGREED_ENCRYPTION = "encrypted";

    /** The Constant AGREED_SDXVALIDATION. */
    private static final String AGREED_SDXVALIDATION = "sdxvalidation";

    /** The Constant AGREED_SDXVALIDATION_BOTH. */
    private static final String AGREED_SDXVALIDATION_BOTH = "sdxvalidation_both";

    /** The Constant AGREED_SDXVALIDATION_CONSUMER. */
    private static final String AGREED_SDXVALIDATION_CONSUMER = "sdxvalidation_forged";

    /** The resolver. */
    private MockPolicyResolver resolver;

    /** The testee. */
    private PolicyValidatorBean testee;

    /**
     * Instantiates a new policy validator bean test.
     * 
     * @param name
     *        the name
     */
    public PolicyValidatorBeanTest(final String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws UnreadableSourceException, CorruptedSourceException {
        this.resolver = new MockPolicyResolver();
        this.resolver.addOperationPolicy(ENCRYPTED_PROVIDER_OPDX,
                "org/eclipse/swordfish/core/components/processing/impl/EncryptedSyncOperation.opdx");
        this.resolver.addOperationPolicy(VALIDATED_PROVIDER_OPDX,
                "org/eclipse/swordfish/core/components/processing/impl/ValidatedSyncOperation.opdx");
        this.resolver.addOperationPolicy(DEFAULT_PROVIDER_OPDX,
                "org/eclipse/swordfish/core/components/processing/impl/defaultprovider_default.opdx");
        this.resolver.addOperationPolicy(AUTHENTICATED_PROVIDER_OPDX,
                "org/eclipse/swordfish/core/components/processing/impl/AuthenticatedSyncOperation.opdx");
        this.resolver.addOperationPolicy(AUTHORIZED_PROVIDER_OPDX,
                "org/eclipse/swordfish/core/components/processing/impl/AuthorizedSyncOperation.opdx");
        this.resolver.addOperationPolicy(SIGNED_PROVIDER_OPDX,
                "org/eclipse/swordfish/core/components/processing/impl/SignedSyncOperation.opdx");
        this.resolver.addParticipantPolicy(DEFAULT_PROVIDER_PPDX,
                "org/eclipse/swordfish/core/components/processing/impl/defaultprovider.ppdx");
        this.resolver.addParticipantPolicy(AUTHENTICATED_PROVIDER_PPDX,
                "org/eclipse/swordfish/core/components/processing/impl/AuthorizationProvider.ppdx");
        this.resolver.addParticipantPolicy(SECURED_PROVIDER_PPDX,
                "org/eclipse/swordfish/core/components/processing/impl/SecureProvider.ppdx");
        this.resolver.addAgreedPolicy(AGREED_HTTP_ONLY,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_noauthentication.xml");
        this.resolver.addAgreedPolicy(AGREED_FORGED_AUTHENTICATION,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_forgedAuthentication.xml");
        this.resolver.addAgreedPolicy(AGREED_AUTHENTICATION,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_authentication.xml");
        this.resolver.addAgreedPolicy(AGREED_AUTHORIZATION,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_authorization.xml");
        this.resolver.addAgreedPolicy(AGREED_SDXVALIDATION,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_sdxvalidation.xml");
        this.resolver.addAgreedPolicy(AGREED_SDXVALIDATION_BOTH,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_sdxvalidation_both.xml");
        this.resolver.addAgreedPolicy(AGREED_SDXVALIDATION_CONSUMER,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_sdxvalidation_forged.xml");
        this.resolver.addAgreedPolicy(AGREED_SIGNED, "org/eclipse/swordfish/core/components/processing/impl/Agreed_signature.xml");
        this.resolver.addAgreedPolicy(AGREED_SIGNED_FORGED,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_signature_forged.xml");
        this.resolver.addAgreedPolicy(AGREED_ENCRYPTION,
                "org/eclipse/swordfish/core/components/processing/impl/Agreed_encryption.xml");
        this.testee = new PolicyValidatorBean();
        this.testee.setPolicyResolver(this.resolver);
        this.testee.init();
        this.testee.setValidityDuration(1);
    }

    /**
     * checks that an agreed policy with sdxvalidation of request on consumer side is accepted by a
     * ProviderPolicy without sdx validation.
     * 
     * @throws PolicyViolatedException
     */
    public void testConsumerSdxValidation() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SDXVALIDATION_CONSUMER); // ok,
        // so
        // it's
        // not
        // forged
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(DEFAULT_PROVIDER_PPDX);
        this.testee.validate(agreed, "bar", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that the validation rejects an agreed policy that contains a wrong authentication
     * assertion.
     * 
     * @throws PolicyViolatedException
     */
    public void testForgedAuthentication() {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_FORGED_AUTHENTICATION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(AUTHENTICATED_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "authorize", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to wrong authentication assertion in agreed policy");
        } catch (PolicyViolatedException e) {
            // expected
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Agreed Policy for operation authorize does not match behavior specified in provider policy"));
        }
    }

    /**
     * agreed policy specifies encryption for response, but provider can't handle it -> possible DoS
     * for receiver.
     * 
     * @throws PolicyViolatedException
     */
    public void testForgedEncryption() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_ENCRYPTION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(DEFAULT_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "bar", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to missing assertion in agreed policy");
        } catch (PolicyViolatedException e) {
            // expected
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Agreed Policy for operation bar does not match behavior specified in provider policy"));
        }
    }

    /**
     * checks that an agreed policy with sdxvalidation of request on consumer side is rejected if
     * ProviderPolicy requires validation on provider side.
     * 
     * @throws PolicyViolatedException
     */
    public void testForgedSdxValidation() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SDXVALIDATION_CONSUMER);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "boogle", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to missing assertion in agreed policy");
        } catch (PolicyViolatedException e) {
            // expected
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Agreed Policy for operation boogle does not match behavior specified in provider policy"));
        }
    }

    /**
     * agreed policy specifies verification for signature, but receiver can't handle it -> possible
     * DoS for receiver.
     * 
     * @throws PolicyViolatedException
     */
    public void testForgedSignature1() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SIGNED);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(AUTHENTICATED_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "authorize", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to missing assertion in agreed policy");
        } catch (PolicyViolatedException e) {
            // expected
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Agreed Policy for operation authorize does not match behavior specified in provider policy"));
        }
    }

    /**
     * checks that an agreed policy with faulty "verify" attribute is rejected by a matching
     * ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testForgedSignature2() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SIGNED_FORGED);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "bar", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to missing assertion in agreed policy");
        } catch (PolicyViolatedException e) {
            // expected
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Agreed Policy for operation bar does not match behavior specified in provider policy"));
        }
    }

    /**
     * Test hash.
     * 
     * @throws PolicyViolatedException
     * @throws NoSuchAlgorithmException
     */
    public void testHash() throws PolicyViolatedException, NoSuchAlgorithmException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SDXVALIDATION_CONSUMER);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(DEFAULT_PROVIDER_PPDX);
        String hash1 = this.testee.getHash(pp_id, "foo", agreed);
        String hash2 = this.testee.getHash(pp_id, "bar", agreed);
        assertFalse(hash1.equals(hash2));
        Policy agreed2 = this.resolver.resolveAgreedOperationPolicy(AGREED_AUTHENTICATION);
        hash2 = this.testee.getHash(pp_id, "foo", agreed2);
        assertFalse(hash1.equals(hash2));
        hash2 = this.testee.getHash(pp_id, "foo", agreed);
        assertEquals(hash1, hash2);
    }

    /**
     * checks that an agreed policy without authentication is accepted by a matching ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMatchingAuthentication() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_AUTHENTICATION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(AUTHENTICATED_PROVIDER_PPDX);
        this.testee.validate(agreed, "authorize", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that an agreed policy without authentication is accepted by a matching ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMatchingAuthorization() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_AUTHORIZATION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        this.testee.validate(agreed, "foo", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that an agreed policy with sdxvalidation of request on both sides is accepted by a
     * matching ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMatchingDoubleSdxValidation() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SDXVALIDATION_BOTH);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        this.testee.validate(agreed, "boogle", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that an agreed policy with encryption of response is accepted by a matching
     * ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMatchingEncryption() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_ENCRYPTION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        this.testee.validate(agreed, "baz", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that an agreed policy without authentication is accepted by a matching ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMatchingNoAuthentication() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_HTTP_ONLY);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(DEFAULT_PROVIDER_PPDX);
        this.testee.validate(agreed, "foo", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that an agreed policy with sdxvalidation of request on provider side is accepted by a
     * matching ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMatchingSdxValidation() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SDXVALIDATION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        this.testee.validate(agreed, "boogle", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that an agreed policy with signature is accepted by a matching ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMatchingSignature() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SIGNED);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        this.testee.validate(agreed, "bar", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that the validation rejects an agreed policy that misses an assertion that is required
     * by the ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMissingAuthentication() {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_HTTP_ONLY);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(AUTHENTICATED_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "authorize", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to missing assertion in agreed policy");
        } catch (PolicyViolatedException e) {
            // expected
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Agreed Policy for operation authorize does not match behavior specified in provider policy"));
        }
    }

    /**
     * checks that the validation rejects an agreed policy that misses an assertion that is required
     * by the ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMissingAuthorization() {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_AUTHENTICATION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "foo", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to missing assertion in agreed policy");
        } catch (PolicyViolatedException e) {
            // expected
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Agreed Policy for operation foo does not match behavior specified in provider policy"));
        }
    }

    /**
     * checks that an agreed policy without signature is rejected by a matching ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testMissingSignature() throws PolicyViolatedException {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_AUTHENTICATION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "bar", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to missing assertion in agreed policy");
        } catch (PolicyViolatedException e) {
            // expected
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Agreed Policy for operation bar does not match behavior specified in provider policy"));
        }
    }

    /**
     * Test resolver.
     * 
     * @throws BackendException
     */
    public void testResolver() throws BackendException {
        OperationPolicyIdentity op_id = new StandardOperationPolicyIdentity(DEFAULT_PROVIDER_OPDX);
        OperationPolicy opPolicy = this.resolver.resolveOperationPolicy(op_id);
        assertNotNull(opPolicy);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(DEFAULT_PROVIDER_PPDX);
        ParticipantPolicy ppPolicy = this.resolver.resolveParticipantPolicy(pp_id);
        assertNotNull(ppPolicy);
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_HTTP_ONLY);
        assertNotNull(agreed);
    }

    /**
     * checks that an agreed policy with sdxvalidation of request on provider side is accepted by a
     * matching ProviderPolicy.
     * 
     * @throws PolicyViolatedException
     */
    public void testValidationCache() throws PolicyViolatedException {
        this.testee.setValidityDuration(100000);
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_SDXVALIDATION);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(SECURED_PROVIDER_PPDX);
        this.testee.validate(agreed, "boogle", pp_id, PROVIDER_1, SERVICE);
        this.testee.validate(agreed, "boogle", pp_id, PROVIDER_1, SERVICE);
        // no assertions - if validation fails, an exception is thrown
    }

    /**
     * checks that the validation rejects an operation that is not defined in the policy.
     * 
     * @throws PolicyViolatedException
     */
    public void testWrongOperationName() {
        Policy agreed = this.resolver.resolveAgreedOperationPolicy(AGREED_HTTP_ONLY);
        ParticipantPolicyIdentity pp_id = new StandardParticipantPolicyIdentity(AUTHENTICATED_PROVIDER_PPDX);
        try {
            this.testee.validate(agreed, "foo", pp_id, PROVIDER_1, SERVICE);
            fail("PolicyViolatedException expected due to missing operation in ProviderPolicy");
        } catch (PolicyViolatedException e) {
            String msg = e.getResourceKey();
            assertTrue(msg.startsWith("Provider policy "
                    + "http://policies.sopware.org/security/AuthorizationProvider/1.0/AuthorizationProvider/secureAccess"
                    + " does not specify a policy for requested operation foo"));
        }
    }

}
