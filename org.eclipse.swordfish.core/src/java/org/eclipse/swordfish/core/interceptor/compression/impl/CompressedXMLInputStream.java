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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO Think about how to implement this.
 */
public class CompressedXMLInputStream extends FilterInputStream {

    /**
     * Instantiates a new compressed XML input stream.
     * 
     * @param inputStream
     *        the input stream
     * 
     * @throws IOException
     */
    public CompressedXMLInputStream(final InputStream inputStream) throws IOException {
        super(inputStream);
    }

}
