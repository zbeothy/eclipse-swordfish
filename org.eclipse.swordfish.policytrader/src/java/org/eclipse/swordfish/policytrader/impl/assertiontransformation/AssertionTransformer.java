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
package org.eclipse.swordfish.policytrader.impl.assertiontransformation;

import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * The Interface AssertionTransformer.
 */
public interface AssertionTransformer {

    /** The CLASSI c_ AGREE d_ POLIC y_ NAMESPACE. */
    String CLASSIC_AGREED_POLICY_NAMESPACE = AgreedPolicy.AGREED_POLICY_CLASSIC_NAMESPACE;

    /** The CLASSI c_ ASSERTIO n_ NAMESPACE. */
    String CLASSIC_ASSERTION_NAMESPACE = AgreedPolicy.CLASSIC_ASSERTION_NAMESPACE;

    /** The CLASSI c_ ASSERTIO n_ TYP e_ ATTRIB. */
    String CLASSIC_ASSERTION_TYPE_ATTRIB = "type";

    /** The WS p_ AGREE d_ POLIC y_ NAMESPACE. */
    String WSP_AGREED_POLICY_NAMESPACE = AgreedPolicy.AGREED_POLICY_NAMESPACE;

    /** The WS p_ ASSERTIO n_ NAMESPACE. */
    String WSP_ASSERTION_NAMESPACE = AgreedPolicy.ASSERTION_NAMESPACE;

    /** The WS p_ AUTHENTICATION. */
    QKey WSP_AUTHENTICATION = new QKey(WSP_ASSERTION_NAMESPACE, "Authentication");

    /** The CLASSI c_ AUTHENTICATIO n_ BAG. */
    QKey CLASSIC_AUTHENTICATION_BAG = new QKey(CLASSIC_ASSERTION_NAMESPACE, "Authentications");

    /** The CLASSI c_ AUTHENTICATION. */
    QKey CLASSIC_AUTHENTICATION = new QKey(CLASSIC_ASSERTION_NAMESPACE, "Authentication");

    /** The WS p_ AUTHORIZATION. */
    QKey WSP_AUTHORIZATION = new QKey(WSP_ASSERTION_NAMESPACE, "Authorization");

    /** The CLASSI c_ AUTHORIZATION. */
    QKey CLASSIC_AUTHORIZATION = new QKey(CLASSIC_ASSERTION_NAMESPACE, "Authorization");

    /** The CLASSI c_ TRANSPOR t_ BAG. */
    QKey CLASSIC_TRANSPORT_BAG = new QKey(CLASSIC_ASSERTION_NAMESPACE, "Transports");

    /** The CLASSI c_ TRANSPORT. */
    QKey CLASSIC_TRANSPORT = new QKey(CLASSIC_ASSERTION_NAMESPACE, "Transport");

    /**
     * Classic transport assertion types. Order needs to correspond to
     * {@link WSP_TRANSPORT_ASSERTIONS}.
     */
    String[] CLASSIC_TRANSPORT_TYPES = {"HTTP", "HTTPS", "JMS"};

    /**
     * WS-Policy based transport assertions. Order needs to correspond to
     * {@link CLASSIC_TRANSPORT_TYPES}.
     */
    QKey[] WSP_TRANSPORT_ASSERTIONS =
            {new QKey(WSP_ASSERTION_NAMESPACE, "HttpTransport"), new QKey(WSP_ASSERTION_NAMESPACE, "HttpsTransport"),
                    new QKey(WSP_ASSERTION_NAMESPACE, "JmsTransport")};

    /** The CLASSI c_ TRANSFORMATION. */
    QKey CLASSIC_TRANSFORMATION = new QKey(CLASSIC_ASSERTION_NAMESPACE, "Transformation");

    /** The CLASSI c_ TRANSFORMATIO n_ BAG. */
    QKey CLASSIC_TRANSFORMATION_BAG = new QKey(CLASSIC_ASSERTION_NAMESPACE, "Transformations");

    /** The WS p_ TRANSFORMATION. */
    QKey WSP_TRANSFORMATION = new QKey(WSP_ASSERTION_NAMESPACE, "Transformation");

    /** The CLASSI c_ CUSTO m_ VALIDATION. */
    QKey CLASSIC_CUSTOM_VALIDATION = new QKey(CLASSIC_ASSERTION_NAMESPACE, "CustomValidation");

    /** The CLASSI c_ CUSTO m_ VALIDATIO n_ BAG. */
    QKey CLASSIC_CUSTOM_VALIDATION_BAG = new QKey(CLASSIC_ASSERTION_NAMESPACE, "CustomValidations");

    /** The WS p_ CUSTO m_ VALIDATION. */
    QKey WSP_CUSTOM_VALIDATION = new QKey(WSP_ASSERTION_NAMESPACE, "CustomValidation");

    /** The CLASSI c_ TRACKIN g_ LEVEL. */
    QKey CLASSIC_TRACKING_LEVEL = new QKey(CLASSIC_ASSERTION_NAMESPACE, "TrackingLevel");

    /** The WS p_ TRACKIN g_ LEVEL. */
    QKey WSP_TRACKING_LEVEL = new QKey(WSP_ASSERTION_NAMESPACE, "TrackingLevel");

    /** The CLASSI c_ EXTENSION. */
    QKey CLASSIC_EXTENSION = new QKey(CLASSIC_ASSERTION_NAMESPACE, "Extension");

    /** The WS p_ EXTENSION. */
    QKey WSP_EXTENSION = new QKey(WSP_ASSERTION_NAMESPACE, "Extension");

    /** The CLASSI c_ MA x_ RESPONS e_ TIME. */
    QKey CLASSIC_MAX_RESPONSE_TIME = new QKey(CLASSIC_ASSERTION_NAMESPACE, "MaxResponseTime");

    /** The WS p_ MA x_ RESPONS e_ TIME. */
    QKey WSP_MAX_RESPONSE_TIME = new QKey(WSP_ASSERTION_NAMESPACE, "MaxResponseTime");

    /**
     * Assertion.
     * 
     * @param wspAssertionName
     *        the wsp assertion name
     * @param wsAssertion
     *        the ws assertion
     * @param classicPolicy
     *        the classic policy
     */
    void assertion(final QKey wspAssertionName, final PrimitiveAssertion wsAssertion, final ClassicOperationPolicy classicPolicy);
}
