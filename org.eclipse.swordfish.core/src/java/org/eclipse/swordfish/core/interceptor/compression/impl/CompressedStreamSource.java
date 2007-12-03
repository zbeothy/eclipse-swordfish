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
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.stream.StreamSource;

/*
 * TODO Implement streaming support
 */
/**
 * The Class CompressedStreamSource.
 */
public class CompressedStreamSource extends StreamSource {

    /** The source. */
    private StreamSource source;

    /**
     * Instantiates a new compressed stream source.
     * 
     * @param source
     *        the source
     */
    public CompressedStreamSource(final StreamSource source) {
        this.source = source;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.transform.stream.StreamSource#getInputStream()
     */
    @Override
    public InputStream getInputStream() {
        InputStream is = this.source.getInputStream();
        try {
            return is != null ? new CompressedXMLInputStream(is) : null;
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.xml.transform.stream.StreamSource#getReader()
     */
    @Override
    public Reader getReader() {
        // TODO Implement
        return null;
    }
}
