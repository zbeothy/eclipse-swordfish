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
package org.eclipse.swordfish.configrepos.scopepath.basic.dom;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.PathPartImpl;
import org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl.ScopePathImpl;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;

/**
 * The Class ScopePathUtil.
 * 
 */
public class ScopePathUtil {

    /**
     * Search for a specific path part and fetch its value.
     * 
     * @param scopePath
     *        to search in
     * @param aKey
     *        to find
     * 
     * @return the value, or null if it was not found
     */
    public static String findValueInScopePath(final ScopePath scopePath, final String aKey) {
        Iterator iter = scopePath.getPathPart().iterator();
        while (iter.hasNext()) {
            PathPart part = (PathPart) iter.next();
            if (part.getType().equalsIgnoreCase(aKey.toUpperCase())) return part.getValue();
        }
        return null;
    }

    /** Path part separator, if this was set by the related constructor. */
    private String pathSeparator = ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR;

    /**
     * Initialize a ScopePathUtil object, with the default '/' path part separator.
     */
    public ScopePathUtil() {
        super();
    }

    /**
     * Initialize a ScopePathUtil object, with the specified path part separator.
     * 
     * @param aPathSeparator
     *        which should be set when creating ScopePath objects
     */
    public ScopePathUtil(final String aPathSeparator) {
        this();
        this.pathSeparator = aPathSeparator;
    }

    /**
     * Compose a ScopePath object from its string representation.
     * 
     * @param aScopePathString
     *        which contains the string representation of the scope path
     * 
     * @return a scope path object
     */
    public ScopePath composeScopePath(final String aScopePathString) {

        if ((null == aScopePathString) || (aScopePathString.trim().length() == 0)) return null;

        ScopePath result = null;
        try {
            result = new ScopePathImpl();

            // add a location element
            if (null != this.pathSeparator) {
                result.setSeparator(this.pathSeparator);
            }
            String[] splitPath = StringUtils.split(aScopePathString, "/");
            for (int i = 0; i < splitPath.length; i++) {
                String[] subPath = StringUtils.split(splitPath[i], "=");
                if (subPath.length != 2)
                    throw new IllegalArgumentException("malformed type '" + splitPath + "' in ScopePath definition");
                PathPart part = new PathPartImpl();
                part.setType(subPath[0].trim());
                part.setValue(subPath[1].trim());
                result.getPathPart().add(part);
            }
        } catch (NoSuchElementException nsee) {
            throw new IllegalArgumentException("wrong scope path definition '" + aScopePathString + "'");
        }

        return result;
    }

    /**
     * Create a ScopePath object, based on a set of parameters common to SBB PAPI programming.
     * 
     * @param aLocation
     *        which should be used. This can be null or an empty String, which will omit this part
     *        of the path
     * @param appID
     *        which should be used subsequent to the location. This can be null, which will omit
     *        this part of the path
     * @param instID
     *        which should be used subsequent to the location. This can be null, which will omit
     *        this part of the path
     * 
     * @return a ScopePath object compiled of the provided parameters
     * 
     * @throws ScopePathException
     *         in case the scope path could not be created, e.g. due to an wrong parameter.
     */
    public ScopePath composeScopePath(final String aLocation, final String appID, final String instID) throws ScopePathException {

        ScopePath path = null;
        path = new ScopePathImpl();
        if (null != this.pathSeparator) {
            path.setSeparator(this.pathSeparator);
        }

        // add the location as the first part of the path
        if (null != aLocation) {
            PathPart part = new PathPartImpl();
            part.setType(ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_LOCATION);
            part.setValue(aLocation);
            path.getPathPart().add(part);
        }

        // add the participant identity to the path part
        if (null != appID) {
            PathPart part = new PathPartImpl();
            part.setType(ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_APPLICATION);
            part.setValue(appID);
            path.getPathPart().add(part);

            if (null != instID) {
                part = new PathPartImpl();
                part.setType(ConfigurationConstants.CONFIGREPOS_SCOPEPATHKEY_INSTANCE);
                part.setValue(instID);
                path.getPathPart().add(part);
            }
        }

        return path;
    }
}
