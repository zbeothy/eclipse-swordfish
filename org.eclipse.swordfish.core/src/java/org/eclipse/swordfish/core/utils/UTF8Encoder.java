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
 * The Class UTF8Encoder.
 */
public class UTF8Encoder implements XMLEncoder {

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

    /** The Constant ENCODING_UTF_8. */
    private static final String ENCODING_UTF_8 = "UTF-8";

    /**
     * Encode a string.
     * 
     * @param xmlString
     *        string to be encoded
     * 
     * @return encoded string
     */
    public String encode(final String xmlString) {
        if (xmlString == null) return "";
        char[] characters = xmlString.toCharArray();
        StringBuffer out = null;
        char character;

        for (int i = 0; i < characters.length; i++) {
            character = characters[i];
            switch (character) {
                // we don't care about single quotes since axis will
                // use double quotes anyway
                case '&':
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(AMP);
                    break;
                case '"':
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(QUOTE);
                    break;
                case '<':
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(LESS);
                    break;
                case '>':
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(GREATER);
                    break;
                case '\n':
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(LF);
                    break;
                case '\r':
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(CR);
                    break;
                case '\t':
                    if (out == null) {
                        out = this.getInitialByteArray(xmlString, i);
                    }
                    out.append(TAB);
                    break;
                default:
                    if (character < 0x20)
                        throw new IllegalArgumentException("invalid XML character " + xmlString.substring(0, i));
                    else {
                        if (out != null) {
                            out.append(character);
                        }
                    }
                    break;
            }
        }
        if (out == null) return xmlString;
        return out.toString();
    }

    /**
     * gets the encoding supported by this encoder.
     * 
     * @return string
     */
    public String getEncoding() {
        return ENCODING_UTF_8;
    }

    /**
     * Gets the initial byte array.
     * 
     * @param aXmlString
     *        the a xml string
     * @param pos
     *        the pos
     * 
     * @return the initial byte array
     */
    protected StringBuffer getInitialByteArray(final String aXmlString, final int pos) {
        return new StringBuffer(aXmlString.substring(0, pos));
    }
}
