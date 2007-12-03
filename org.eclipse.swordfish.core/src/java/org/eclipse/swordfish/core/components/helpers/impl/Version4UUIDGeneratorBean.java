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
package org.eclipse.swordfish.core.components.helpers.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.eclipse.swordfish.core.components.helpers.UUIDGenerator;

// RFC 4122

/**
 * The Class Version4UUIDGeneratorBean.
 */
public class Version4UUIDGeneratorBean implements UUIDGenerator {

    /**
     * Hex encode and format.
     * 
     * @param aInput
     *        the a input
     * @param prefix
     *        the prefix
     * 
     * @return the string
     */
    static private String hexEncodeAndFormat(final byte[] aInput, final String prefix) {
        StringBuffer result = prefix != null ? new StringBuffer(prefix) : new StringBuffer();
        char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int idx = 0; idx < aInput.length; ++idx) {
            byte b = aInput[idx];
            if (idx == 6) { // version identifier
                b = (byte) ((b & 0x0f) | 0x40);
            }
            if (idx == 8) { // variant identifier
                b = (byte) ((b & 0x3f) | 0x80);
            }
            result.append(digits[(b & 0xf0) >> 4]);
            result.append(digits[b & 0x0f]);
            if ((idx == 3) || (idx == 5) || (idx == 7) || (idx == 9)) {
                result.append("-");
            }
        }
        return result.toString();
    }

    /** The prng. */
    private SecureRandom prng;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.helpers.UUIDGenerator#getUUID(java.lang.String)
     */
    public synchronized String getUUID(final String prefix) {
        byte bytes[] = new byte[16];
        this.prng.nextBytes(bytes);
        return hexEncodeAndFormat(bytes, prefix);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.helpers.UUIDGenerator#init()
     */
    public void init() {
        try {
            this.prng = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex);
        }

    }
}

/*
 * This is the easiest UUID to generate, (at least when you have a good random generator). No
 * guarantees can be made towards uniqueness though. Here is a simple recipy: Take 16 random bytes
 * (octets), put them all behind each other, for the description the numbering starts with byte 1
 * (most significant, first) to byte 16 (least significant, last). Then put in the version and
 * variant. To put in the version, take the 7th byte and perform an and operation using 0x0f,
 * followed by an or operation with 0x40. To put in the variant, take the 9th byte and perform an
 * and operation using 0x3f, followed by an or operation with 0x80. To make the string
 * representation, take the hexadecimal presentation of bytes 1-4 (without 0x in front of it) let
 * them follow by a -, then take bytes 5 and 6, - bytes 7 and 8, - bytes 9 and 10, - then followed
 * by bytes 11-16. The result is something like: 00e8da9b-9ae8-4bdd-af76-af89bed2262f
 */
