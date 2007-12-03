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
package org.eclipse.swordfish.core.interceptor.monitor.impl;

/**
 * XML Constants used in the correlation interceptor.
 * 
 */
public interface Constants {

    /** The prefix used for the header and body wrapper. */
    String PREFIX = "log";

    /** The elements' namespace. */
    String NS_URI = "http://headers.sopware.org/interceptors/MessageLog/1.0";

    /** The header element local name. */
    String HEADER_LOCAL_NAME = "Log";

    /** The header element's qualified name. */
    String HEADER_QNAME = PREFIX + ":" + HEADER_LOCAL_NAME;

    /** The header's QName in String from (for insertion into the header map). */
    String QNAME_STRING = "{" + NS_URI + "}" + HEADER_LOCAL_NAME;
}
