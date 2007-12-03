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

import java.io.IOException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * A small wrapper around Sun's private Base64-encoding utilities. Might need to be changed if
 * running on another vendor's VM
 * 
 */
public class Base64Support {

    /** the encoder. */
    private BASE64Encoder encoder = new BASE64Encoder();

    /** the decoder. */
    private BASE64Decoder decoder = new BASE64Decoder();

    /**
     * BASE64-decodes a String.
     * 
     * @param s
     *        string to decode
     * 
     * @return the BASE64 decoded bytes
     * 
     * @throws IOException
     *         when a problem with the encoded String occurs
     */
    public byte[] decode(final String s) throws IOException {
        return this.decoder.decodeBuffer(s.trim());
    }

    /**
     * BASE64-encodes a byte array.
     * 
     * @param bytes
     *        bytes to encode
     * 
     * @return the BASE64 encoding of the bytes
     */
    public String encode(final byte[] bytes) {
        return this.encoder.encode(bytes);
    }
}
