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
package org.eclipse.swordfish.policy;

import javax.xml.namespace.QName;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;

/**
 * The Class PolicyConstants.
 */
public class PolicyConstants {

    // QNames for supported primitive assertions

    // definitions from neethi

    /** The WS p_ NAMESPAC e_ URI. */
    public static final String WSP_NAMESPACE_URI = org.apache.ws.policy.PolicyConstants.POLICY_NAMESPACE_URI;

    // well-known policies

    /** The EMPT y_ POLICY. */
    public static final Policy EMPTY_POLICY = new Policy();

    /** The Constant SOP_ASSERTION_URI. */
    public static final String SOP_ASSERTION_URI = "http://types.sopware.org/qos/SOPAssertions/1.1";

    // internally used QNames

    /** The Constant SOP_ASSERTION_TEMPORARY_URI. */
    public static final String SOP_ASSERTION_TEMPORARY_URI = "http://types.sopware.org/qos/SOPAssertions/1.1/temp";

    /** Role attribute added to all primitive assertions before matching. */
    public final static QName SOP_ROLE_ATTRIBUTE = new QName(SOP_ASSERTION_TEMPORARY_URI, "role", "soptemp");

    /** Role attribute added to all primitive assertions before matching. */
    public final static QName SOP_OPTIONAL_ATTRIBUTE = new QName(SOP_ASSERTION_TEMPORARY_URI, "optional", "soptemp");

    /**
     * Used to indicate that the processor is called during runtime matching of agreed policy with
     * provider policy (mode="runtime").
     */
    public final static QName SOP_MODE_ATTRIBUTE = new QName(SOP_ASSERTION_TEMPORARY_URI, "mode", "soptemp");

    /** ID assertion added to all alternatives before matching. */
    public final static QName SOP_ID_ASSERTION = new QName(SOP_ASSERTION_TEMPORARY_URI, "Id", "soptemp");

    /** The Constant WSP_OPTIONAL_ATTRIBUTE. */
    public final static QName WSP_OPTIONAL_ATTRIBUTE = new QName(WSP_NAMESPACE_URI, "Optional");

    /** The Constant SOP_WSP_EXTENSIONS_URI. */
    public final static String SOP_WSP_EXTENSIONS_URI = "http://types.sopware.org/qos/WS-PolicyExtensions/1.1";

    /** The Constant SOP_WSP_EXTENSIONS_PREFIX. */
    public final static String SOP_WSP_EXTENSIONS_PREFIX = "sopwsp";

    /** The Constant SOP_WSP_NAME_ATTRIBUTE. */
    public final static QName SOP_WSP_NAME_ATTRIBUTE = new QName(SOP_WSP_EXTENSIONS_URI, "shortname", SOP_WSP_EXTENSIONS_PREFIX);

    static {
        EMPTY_POLICY.addTerm(new ExactlyOne());
    }

}
