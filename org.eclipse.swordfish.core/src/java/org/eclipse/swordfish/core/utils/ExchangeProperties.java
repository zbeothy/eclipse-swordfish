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
package org.eclipse.swordfish.core.utils;

/**
 * The Interface ExchangeProperties.
 * 
 * 
 * Constants for keys used to access exchange properties
 */
public interface ExchangeProperties {

    /** The SB b_ SERVIC e_ INTERFAC e_ NAME. */
    String SBB_SERVICE_INTERFACE_NAME = "sbb:portType";

    /** The PARTICIPAN t_ IDENTITY. */
    String PARTICIPANT_IDENTITY = "sbb:participant_identity";

    /** The CORRELATIO n_ ID. */
    String CORRELATION_ID = "sbb:correlation_id";

    /** The SB b_ AUT h_ CALLBACKS. */
    String SBB_AUTH_CALLBACKS = "sbb:auth_callbacks";

    /** The SERVIC e_ AUT h_ CALLBACKS. */
    String SERVICE_AUTH_CALLBACKS = "sbb:service_auth_callbacks";

    /** The OPERATIO n_ AUT h_ CALLBACKS. */
    String OPERATION_AUTH_CALLBACKS = "sbb:operation_auth_callbacks";

    /** The MESSAG e_ AUT h_ CALLBACKS. */
    String MESSAGE_AUTH_CALLBACKS = "sbb:message_auth_callbacks";

    /** The AGREE d_ POLICY. */
    String AGREED_POLICY = "sbb:agreed_policy";

    /** The REPL y_ T o_ ENDPOINT. */
    String REPLY_TO_ENDPOINT = "sbb:reply_to";

    /** The FAUL t_ T o_ ENDPOINT. */
    String FAULT_TO_ENDPOINT = "sbb:fault_to";

    /** The RE f_ PARAMS. */
    String REF_PARAMS = "sbb:ref_params";

    /** The PARTNE r_ OPERATION. */
    String PARTNER_OPERATION = "sbb:partner_op";

    /** The CAL l_ RELATION. */
    String CALL_RELATION = "sbb:callRelation";

    /** The CAL l_ CONTEXT. */
    String CALL_CONTEXT = "sbb:callContext";

    // Oracle BC specific properties
    /** The SOA p_ ACTION. */
    String SOAP_ACTION = "soap_action";

    /** The PRIORITY. */
    String PRIORITY = "priority";

    // IONA Locator proxy configs
    /** The CONVERSATIO n_ INDTIFIER. */
    String CONVERSATION_INDTIFIER = "com.iona.locatorproxy.stickIdentity";

    /** The CONVERSATIO n_ REFRESHABLE. */
    String CONVERSATION_REFRESHABLE = "com.iona.locatorproxy.stickRefreshable";
}
