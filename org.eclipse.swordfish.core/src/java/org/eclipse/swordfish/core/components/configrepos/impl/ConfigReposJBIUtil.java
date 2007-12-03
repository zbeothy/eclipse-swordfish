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
package org.eclipse.swordfish.core.components.configrepos.impl;

import org.eclipse.swordfish.configrepos.util.DirectorySourceUtil;
import org.eclipse.swordfish.core.logging.Log;
import org.eclipse.swordfish.core.logging.SBBLogFactory;

/**
 * The Class ConfigReposJBIUtil.
 * 
 */
public final class ConfigReposJBIUtil {

    /** The Constant log. */
    private final static Log LOG = SBBLogFactory.getLog(ConfigReposJBIUtil.class);

    /**
     * Compose JBI workspace directory.
     * 
     * @param aProtocolPrefix
     *        prefix
     * @param aPath
     *        a path
     * @param aSuffix
     *        a suffix
     * 
     * @return result string
     */
    public static String composeJBIWorkspaceDirectory(final String aProtocolPrefix, final String aPath, final String aSuffix) {
        StringBuffer result = new StringBuffer();
        result.append(aProtocolPrefix);
        DirectorySourceUtil.appendWithSeparator(result, aPath);
        DirectorySourceUtil.appendWithSeparator(result, aSuffix);

        if (LOG.isTraceEnabled()) {
            LOG.trace("JBI workdirectory for configrepos is '" + result.toString() + "'");
        }
        return result.toString();
    }

    /**
     * Instantiates a new config repos JBI util.
     */
    private ConfigReposJBIUtil() {
        super();
    }
}
