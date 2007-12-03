/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Deutsche Post AG - initial API and implementation
 **************************************************************************************************/
package org.eclipse.swordfish.configrepos.validation;

import java.io.InputStream;
import org.apache.xerces.dom.DOMInputImpl;
import org.eclipse.swordfish.configrepos.shared.validation.ExternalResolver;
import org.eclipse.swordfish.configrepos.shared.validation.ResourceException;
import org.w3c.dom.ls.LSInput;

/**
 * The Class ClasspathResourceResolver.
 */
public class ClasspathResourceResolver implements ExternalResolver {

    /** The use context class loader. */
    private boolean useContextClassLoader = false;

    /**
     * Instantiates a new classpath resource resolver.
     * 
     * @param useContextClassLoader
     *        the use context class loader
     */
    public ClasspathResourceResolver(final boolean useContextClassLoader) {
        this.useContextClassLoader = useContextClassLoader;
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.shared.validation.ExternalResolver#isExternalResource(java.lang.String)
     * @param uri
     * @return boolean
     * @throws ResourceException
     */
    public boolean isExternalResource(final String uri) throws ResourceException {
        return uri.startsWith("classpath");
    }

    /**
     * (non-Javadoc).
     * 
     * @param uri
     * @return boolean
     * @throws ResourceException
     * @see org.eclipse.swordfish.configrepos.shared.validation.ExternalResolver#resolveExternalResource(java.lang.String)
     * 
     */
    public LSInput resolveExternalResource(final String uri) throws ResourceException {
        String[] parts = uri.split(":");
        if (parts.length != 2) throw new ResourceException();
        ClassLoader cl =
                (this.useContextClassLoader ? Thread.currentThread().getContextClassLoader() : this.getClass().getClassLoader());
        InputStream is = cl.getResourceAsStream(parts[1]);
        if (null != is) {
            LSInput di = new DOMInputImpl();
            di.setBaseURI(null);
            di.setByteStream(is);
            di.setEncoding("UTF-8");
            di.setPublicId(null);
            di.setSystemId(uri);
            return di;
        } else
            return null;

    }

    protected boolean isUseContextClassLoader() {
        return this.useContextClassLoader;
    }

    protected void setUseContextClassLoader(final boolean useContextClassLoader) {
        this.useContextClassLoader = useContextClassLoader;
    }
}
