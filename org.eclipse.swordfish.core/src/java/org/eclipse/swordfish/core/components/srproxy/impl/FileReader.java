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
package org.eclipse.swordfish.core.components.srproxy.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * Class for reading in files from the local file system, such as configuration files and XML
 * templates.
 * 
 */
public class FileReader {

    /** Logger for this class. */
    private static final Log LOG = SBBLogFactory.getLog(FileReader.class);

    /** Buffer size for file reading. */
    private static final int BUFFER_SIZE = 1024;

    /** Base location to read files from. */
    private String baseDir;

    /**
     * Creates a new FileReader object. All files will be read from the base location
     * nonDefaultBaseDir. Examples for nonDefaultBaseDir parameter: myConfDir/, my/other/confDir/
     * 
     * @param nonDefaultBaseDir
     *        base location (path prefix, must end with path separator)
     */
    public FileReader(final String nonDefaultBaseDir) {
        this.baseDir = nonDefaultBaseDir;
    }

    /**
     * Reads the contents a file and writes it to a String.
     * 
     * @param xmlFilename
     *        name of the file whose content should be read into a String
     * 
     * @return A String which contains the contents of the file, or null if the file could not be
     *         read for any reason.
     * 
     * @throws IOException
     *         if an error occurs while reading from the file
     */
    public String readFileContent(final String xmlFilename) throws IOException {

        // Get File as InputStream
        InputStream inStream = this.filenameToInputStream(xmlFilename);

        return this.readStreamContent(inStream);
    }

    /**
     * Reads the contents of an InputStream to a String.
     * 
     * @param inStream
     *        the in stream
     * 
     * @return its contents as String
     * 
     * @throws IOException
     *         if an error occurs while reading from the stream
     */
    public String readStreamContent(final InputStream inStream) throws IOException {

        // Prepare OutputStream
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        // Read file
        byte[] buffer = new byte[BUFFER_SIZE];
        int bufferLength = buffer.length;
        int availableBytes = 0;

        try {
            while ((availableBytes = inStream.read(buffer, 0, bufferLength)) >= 0) {

                outStream.write(buffer, 0, availableBytes);
            }
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    // Cannot close stream
                    LOG.debug("Cannot close stream");
                }
            }
        }

        return outStream.toString();
    }

    /**
     * Factory method for creating a buffered FileInputStream.
     * 
     * @param file
     *        the file to read from
     * 
     * @return a new FileInputStream wrapped in a BufferedInputStream
     * 
     * @throws FileNotFoundException
     *         if the file cannot be found or opened
     */
    protected BufferedInputStream createBufferedFileInputStream(final File file) throws FileNotFoundException {

        return new BufferedInputStream(new FileInputStream(file));
    }

    /**
     * Returns a buffered FileInputStream to read from a specified file.
     * 
     * @param xmlFilename
     *        The name of tha file to read from.
     * 
     * @return a buffered FileInputStream to read from a specified file
     * 
     * @throws FileNotFoundException
     *         if the file is not found
     */
    protected InputStream filenameToInputStream(final String xmlFilename) throws FileNotFoundException {
        if (null == xmlFilename) {
            if (LOG.isErrorEnabled()) {
                // Cannot read file
                LOG.error("Error initializing component Service Registry Proxy. Internal error: null filename."); //$NON-NLS-1$
            }
            throw new FileNotFoundException("null"); //$NON-NLS-1$
        }
        File file = new File(this.baseDir + xmlFilename);

        BufferedInputStream inStream = null;
        inStream = this.createBufferedFileInputStream(file);

        return inStream;
    }
}
