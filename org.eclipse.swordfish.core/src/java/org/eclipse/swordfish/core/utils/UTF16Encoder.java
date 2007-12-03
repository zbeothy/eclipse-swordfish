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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * The Class UTF16Encoder.
 */
public class UTF16Encoder implements XMLEncoder {

    /** The Constant AMP. */
    protected static final String AMP = "&amp;";

    /** The Constant QUOTE. */
    protected static final String QUOTE = "&quot;";

    /** The Constant LESS. */
    protected static final String LESS = "&lt;";

    /** The Constant GREATER. */
    protected static final String GREATER = "&gt;";

    /** The Constant LF. */
    protected static final String LF = "\n";

    /** The Constant CR. */
    protected static final String CR = "\r";

    /** The Constant TAB. */
    protected static final String TAB = "\t";

    /**
     * Encode.
     * 
     * @param xmlString
     *        the xml string
     * 
     * @return the string
     */
    public String encode(final String xmlString) {
        StringWriter sw = new StringWriter();
        try {
            this.writeEncoded(sw, xmlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

    /**
     * gets the encoding supported by this encoder.
     * 
     * @return string
     */
    public String getEncoding() {
        return "UTF-16";
    }

    /**
     * write the encoded version of a given string.
     * 
     * @param writer
     *        writer to write this string to
     * @param xmlString
     *        string to be encoded
     * 
     * @throws IOException
     */
    private void writeEncoded(final Writer writer, final String xmlString) throws IOException {
        if (xmlString == null) return;
        int length = xmlString.length();
        char character;
        for (int i = 0; i < length; i++) {
            character = xmlString.charAt(i);
            switch (character) {
                // we don't care about single quotes since axis will
                // use double quotes anyway
                case '&':
                    writer.write(AMP);
                    break;
                case '"':
                    writer.write(QUOTE);
                    break;
                case '<':
                    writer.write(LESS);
                    break;
                case '>':
                    writer.write(GREATER);
                    break;
                case '\n':
                    writer.write(LF);
                    break;
                case '\r':
                    writer.write(CR);
                    break;
                case '\t':
                    writer.write(TAB);
                    break;
                default:
                    if (character < 0x20)
                        throw new IllegalArgumentException("invalidXmlCharacter00 " + Integer.toHexString(character) + " "
                                + xmlString);
                    else if (character > 0xFFFF) {
                        writer.write((0xD7C0 + (character >> 10)));
                        writer.write((0xDC00 | character & 0x3FF));
                    } else {
                        writer.write(character);
                    }
                    break;
            }
        }
    }

}
