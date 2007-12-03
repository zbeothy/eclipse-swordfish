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
package org.eclipse.swordfish.core.interceptor.compression;

import org.eclipse.swordfish.core.components.processing.ProcessingComponent;

/**
 * The compression processor is an interceptor that compresses/decompresses and
 * Base64-encodes/decodes the SOAP message body. The fact that the body has been compressed is
 * expressed by means of a SOAP header
 * {http://headers.sopware.org/interceptors/Compression/1.0}Compression. The body is wrapped in a
 * {http://headers.sopware.org/interceptors/Compression/1.0}GZipCompressed element.
 * 
 */
public interface CompressionProcessor extends ProcessingComponent {

    /**
     * This attribute describes the role name for Authentication component. TODO Do we still need
     * this?
     */
    String ROLE = CompressionProcessor.class.getName();
}
