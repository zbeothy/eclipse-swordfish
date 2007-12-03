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
 * The Interface MessageProperties.
 * 
 * 
 * Constants for keys used to access message properties
 */
public interface MessageProperties {

    /** The MESSAG e_ ID. */
    String MESSAGE_ID = "sbb:message_id";

    /** The CONSUME r_ CAL l_ ID. */
    String CONSUMER_CALL_ID = "sbb:consumer_call_id";

    /** The CREATE d_ TIMESTAMP. */
    String CREATED_TIMESTAMP = "sbb:created_timestamp";

    /** The RELATE d_ TIMESTAMP. */
    String RELATED_TIMESTAMP = "sbb:related_timestamp";

    /** The RELATE s_ TO. */
    String RELATES_TO = "sbb:relates_to";

    /** The SOA p_ FAUL t_ CODE. */
    String SOAP_FAULT_CODE = "javax.xml.soap.fault.code";

    /** The SOA p_ FAUL t_ STRING. */
    String SOAP_FAULT_STRING = "javax.xml.soap.fault.string";
}
