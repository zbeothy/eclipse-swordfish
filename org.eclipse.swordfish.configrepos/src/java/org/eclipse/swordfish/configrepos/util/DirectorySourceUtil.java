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
package org.eclipse.swordfish.configrepos.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath;
import org.springframework.core.io.ResourceLoader;

/**
 * The Class DirectorySourceUtil.
 * 
 */
public final class DirectorySourceUtil {

    /** Logger. */
    private static Logger logger = Logger.getLogger(DirectorySourceUtil.class.getName(), "ConfigReposMessageBundle");

    /**
     * Include a file path in the provided list in case the requested file is available.
     * 
     * @param path
     *        points to the base path for the search. Protocols 'file:' and 'classpath:' should be
     *        prepended. 'file:' will be prepended as pre default.
     * @param aFilename
     *        which should be loaded as a configuration file.
     * @param resourceLoader
     *        to use to check if the file exists
     * @param pathList
     *        which should be extended in case the file was found.
     */
    public static void addCanonicalizeFilePath(final String path, final String aFilename, final ResourceLoader resourceLoader,
            final List pathList) {
        String filePath = StringUtils.stripEnd(path, "\\/") + File.separatorChar + StringUtils.stripStart(aFilename, "\\/");
        if (filePath.startsWith("classpath:")) {
            InputStream dummy = null;
            try {
                dummy = resourceLoader.getResource(filePath).getInputStream();
            } catch (IOException e) {
                return;
            } finally {
                try {
                    if (null != dummy) {
                        dummy.close();
                    }
                } catch (IOException e) {
                    return;
                }
                if (null != dummy) {
                    pathList.add(filePath);
                }
            }
        } else {
            if (resourceLoader.getResource(filePath).exists()) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "org.eclipse.swordfish.configrepos.CONFIGRESOURCE_FOUND", new Object[] {filePath});
                }
                pathList.add(filePath);
            }
        }
    }

    /**
     * Append with separator.
     * 
     * @param buffer
     *        to which the string should be appended
     * @param aString
     *        the string
     * 
     * @return new StringBuffer
     */
    public static StringBuffer appendWithSeparator(final StringBuffer buffer, final String aString) {

        if ((buffer.length() > 0) && (buffer.charAt(buffer.length() - 1) != File.separatorChar)) {
            buffer.append(File.separatorChar);
        }
        buffer.append(aString);

        // if (aString.charAt(aString.length() - 1) != File.separatorChar) {
        // buffer.append(File.separatorChar);
        // }
        return buffer;
    }

    /**
     * Canonicalize the file path which will be used by all file based configuration sources.
     * 
     * @param aBasepath
     *        points to the base path for the search. Protocols 'file:' and 'classpath:' should be
     *        prepended. 'file:' will be prepended as pre default.
     * @param aTreeQualifier
     *        which should be browsed. It is the first level of destinction in the directory
     *        structure.
     * @param aPath
     *        to the file directory which should contain the configuration file. Each tuple will be
     *        matched to two directory levels.
     * @param aFilename
     *        which should be loaded as a configuration file.
     * 
     * @return the fully qualified path.
     */
    public static String canonicalizeFilePath(final String aBasepath, final String aTreeQualifier, final ScopePath aPath,
            final String aFilename) {
        StringBuffer buffer = new StringBuffer();

        if (null != aBasepath) {
            appendWithSeparator(buffer, aBasepath);
        }

        if (null != aTreeQualifier) {
            appendWithSeparator(buffer, aTreeQualifier);
        }

        if (null != aPath) {
            Iterator iterator = aPath.getPathPart().iterator();
            while (iterator.hasNext()) {
                PathPart part = (PathPart) iterator.next();
                if ((null != part.getValue()) && (part.getValue().trim().length() != 0)) {
                    // FIXME we need to transform special characters
                    // FIXME we need to take care of hierarchical path parts
                    appendWithSeparator(appendWithSeparator(buffer, part.getType()), part.getValue());
                } else {
                    logger.log(Level.INFO, "scope path component is stripped from the path, since it is malformed");
                    // FIXME We should make a trace note that the scope path
                    // component is stripped from the path, since it is
                    // malformed.
                }
            }
        }

        appendWithSeparator(buffer, aFilename);

        String result = buffer.toString();

        if (result.startsWith("file:")) {
            try {
                return URLDecoder.decode(result, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Unable to decode resource name '" + result + "'");
            }
        } else
            return result;
    }

    /**
     * Compile spring resource path.
     * 
     * @param aPath
     *        the path for the specified protocol
     * 
     * @return the spring compatible resource URL
     */
    public static String compileSpringResourcePath(final String aPath) {
        if ((null == aPath) || (aPath.trim().length() == 0)) throw new IllegalArgumentException("Empty path not allowed.");
        String result = StringUtils.stripEnd(aPath.trim(), "\\/") + File.separator;

        if (result.startsWith("classpath:") || result.startsWith("file:")) return result;

        if (result.startsWith(File.separator) || result.matches("\\p{Upper}:.*")) {
            result = "file:" + File.separator + ("/".equals(File.separator) ? "/" : "") + result;
        }

        return result;
    }

    /**
     * Canonicalize the file path which will be used by all file based configuration sources.
     * 
     * @param aBasepath
     *        points to the base path for the search. Protocols 'file:' and 'classpath:' should be
     *        prepended. 'file:' will be prepended as pre default.
     * @param aTreeQualifier
     *        which should be browsed. It is the first level of destinction in the directory
     *        structure.
     * @param aPath
     *        to the file directory which should contain the configuration file. Each tuple will be
     *        matched to two directory levels.
     * @param aFilename
     *        which should be loaded as a configuration file.
     * @param resourceLoader
     *        which can verify the existance of the file
     * @param pathList
     *        which might be prefilled.
     * 
     * @return the fully qualified path to the file.
     */
    public static String searchCanonicalizeFilePath(final String aBasepath, final String aTreeQualifier, final ScopePath aPath,
            final String aFilename, final ResourceLoader resourceLoader, final List pathList) {
        StringBuffer buffer = new StringBuffer();

        if (null != aBasepath) {
            appendWithSeparator(buffer, aBasepath);
        }

        if (null != aTreeQualifier) {
            appendWithSeparator(buffer, aTreeQualifier);
        }

        addCanonicalizeFilePath(buffer.toString(), aFilename, resourceLoader, pathList);

        if (null != aPath) {
            Iterator iterator = aPath.getPathPart().iterator();
            while (iterator.hasNext()) {
                PathPart part = (PathPart) iterator.next();
                if ((null != part.getValue()) && (part.getValue().trim().length() != 0)) {
                    appendWithSeparator(buffer, part.getType());

                    String[] parts = StringUtils.split(part.getValue(), '.');

                    for (int pos = 0; pos < parts.length; pos++) {
                        if (pos > 0) {
                            buffer.append('.').append(parts[pos]);
                        } else {
                            appendWithSeparator(buffer, parts[pos]);
                        }
                        addCanonicalizeFilePath(buffer.toString(), aFilename, resourceLoader, pathList);
                    }
                } else {
                    logger.log(Level.INFO, "scope path component is stripped from the path, since it is malformed");
                    // FIXME We should make a trace note that the scope path
                    // component is stripped from the path, since it is
                    // malformed.
                }
            }
            if (pathList.size() == 0)
                return canonicalizeFilePath(aBasepath, aTreeQualifier, aPath, aFilename);
            else
                return (String) pathList.get(pathList.size() - 1);
        } else {
            buffer.append(aFilename);
            return buffer.toString();
        }
    }

    /**
     * This is just a utility class.
     */
    private DirectorySourceUtil() {
    }
}
