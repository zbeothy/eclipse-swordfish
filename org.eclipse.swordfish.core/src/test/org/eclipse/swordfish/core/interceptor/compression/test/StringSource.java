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
package org.eclipse.swordfish.core.interceptor.compression.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * A helper class which provides a JAXP {@link Source} from a String which can be read as many times
 * as required.
 * 
 */
public class StringSource extends StreamSource {

    /** The text. */
    private String text;

    /**
     * Instantiates a new string source.
     * 
     * @param text
     *        the text
     */
    public StringSource(final String text) {
        this.text = text;
    }

    /**
     * Instantiates a new string source.
     * 
     * @param text
     *        the text
     * @param systemId
     *        the system id
     */
    public StringSource(final String text, final String systemId) {
        this.text = text;
        this.setSystemId(systemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.transform.stream.StreamSource#getInputStream()
     */
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.text.getBytes());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.xml.transform.stream.StreamSource#getReader()
     */
    @Override
    public Reader getReader() {
        return new StringReader(this.text);
    }

}
