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
package org.eclipse.swordfish.policytrader.testing.helpers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class UTF16Encoder implements XMLEncoder {

    protected static final String AMP = "&amp;";

    protected static final String QUOTE = "&quot;";

    protected static final String LESS = "&lt;";

    protected static final String GREATER = "&gt;";

    protected static final String LF = "\n";

    protected static final String CR = "\r";

    protected static final String TAB = "\t";

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
     * gets the encoding supported by this encoder
     * 
     * @return string
     */
    public String getEncoding() {
        return "UTF-16";
    }

    /**
     * write the encoded version of a given string
     * 
     * @param writer
     *        writer to write this string to
     * @param xmlString
     *        string to be encoded
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
