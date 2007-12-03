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
package org.eclipse.swordfish.core.interceptor.compression.impl;

/**
 * XML Constants used in the compression interceptor.
 * 
 */
public interface Constants {

    /** The prefix used for the header and body wrapper. */
    String PREFIX = "sbb";

    /** The elements' namespace. */
    String NS_URI = "http://headers.sopware.org/interceptors/Compression/1.0";

    /** The local name of the wrapper that encloses the compressed body. */
    String WRAPPER_LOCAL_NAME = "GZIPCompressed";

    /** The qualified name of the wrapper. */
    String WRAPPER_QNAME = PREFIX + ":" + WRAPPER_LOCAL_NAME;

    /** The header element local name. */
    String HEADER_LOCAL_NAME = "Compression";

    /** The header element's qualified name. */
    String HEADER_QNAME = PREFIX + ":" + HEADER_LOCAL_NAME;

    /** The header's QName in String from (for insertion into the header map). */
    String QNAME_STRING = "{" + NS_URI + "}" + HEADER_LOCAL_NAME;
}
