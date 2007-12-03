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

import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides templates for XML data.
 * 
 */
public class TemplateHandler {

    /** File Reader to read template files. */
    private FileReader fileReader;

    /**
     * Creates a new instance of TemplateHandler. It reads templates from the specified directory.
     */
    public TemplateHandler() {
        this.fileReader = new FileReader("");
    }

    /**
     * Provides a document as String in which placeholders are filled in with values. The template
     * for the document is read from the local file system.
     * 
     * @param templateFilename
     *        the name of the template file
     * @param placeholders
     *        a set of placeholders
     * @param values
     *        a set of values for the placeholders
     * 
     * @return the template with values filled in, or null if the number of placeholders is not
     *         equal to the number of values
     * 
     * @throws Exception
     *         if the template file cannot be read
     */
    public String fillInTemplate(final String templateFilename, final String[] placeholders, final String[] values)
            throws Exception {

        String result = null;
        InputStream inStream = null;
        try {
            // try and get the file from system classloader
            inStream = this.getClass().getResourceAsStream("templates/" + templateFilename);
            if (null == inStream)
                throw new IOException("Warn: Could not load as resource: '" + "templates/" + templateFilename + "'.");
            result = this.fileReader.readStreamContent(inStream);
        } catch (IOException e1) {
            result = this.fileReader.readFileContent(templateFilename);
        } finally {
            inStream.close();
        }

        if ((null == placeholders) && (null == values)) // No placeholders to replace, so return
                                                        // original template
            // document
            return result;

        if ((null == placeholders) || (null == values) || (placeholders.length != values.length))
            throw new Exception("Error while filling template '" + templateFilename + "'. Placeholders='" + placeholders
                    + "'. Values='" + values + "'.");

        // Replace placeholders
        for (int i = 0; i < placeholders.length; i++) {
            if (-1 == result.indexOf(placeholders[i]))
                throw new Exception("Error while filling template '" + templateFilename + "'. Placeholder '" + placeholders[i]
                        + "' not found in template.");
            result = result.replaceFirst(placeholders[i], values[i]);
        }

        return result;
    }
}
