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
package org.eclipse.swordfish.policytrader;

/**
 * Identifier object for policies, used as policy keys in Policy resolving.
 */
public interface PolicyIdentity {

    /**
     * Key name.
     * 
     * @return a non-empty String
     */
    String getKeyName();

    /**
     * Get the optional location (e.g. URL or path name in file system). The location is just a hint
     * for the client and has no influence on the identity, i.e. two PolicyIdentity instances with
     * different locations and the same name are equal.
     * 
     * @return location String or <code>null</code> if none has been defined.
     */
    String getLocation();

    /**
     * Set the optional location (e.g. URL or path name in file system). The location is just a hint
     * for the client and has no influence on the identity, i.e. two PolicyIdentity instances with
     * different locations and the same name are equal.
     * 
     * @param location
     *        any String or <code>null</code>
     */
    void setLocation(String location);

}
