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
package org.eclipse.swordfish.configrepos.shared.validation.protocolhandler.classpath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * This class implements the protocol handler for URL string with "classpath:" To register it with
 * the Java URL resolver framework, set the system property
 * java.protocol.handler.pkgs=org.eclipse.swordfish.configrepos.shared.validation.protocolhandler
 * The class must be named "Handler" by convention.
 * 
 */
public class Handler extends URLStreamHandler {

    /**
     * Open connection.
     * 
     * @param url
     *        the url
     * 
     * @return the URL connection
     * 
     * @throws IOException
     */
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        String path = url.getPath();
        if ((null == path) || (path.length() == 0))
            throw new MalformedURLException("URL " + url.toString() + " does not specify a path.");
        // use the context class loader to find the resource and and get the
        // fully qualified URL
        // in most cases, the protocol will be either file: or jar:
        URL realUrl = Thread.currentThread().getContextClassLoader().getResource(path);
        if (null != realUrl) // delegate the openConnection() call to the protocol handler
            // responsible for
            // the fully qualified URL
            return realUrl.openConnection();
        else
            throw new FileNotFoundException("The file " + path + " could not be found on the classpath.");
    }
}
