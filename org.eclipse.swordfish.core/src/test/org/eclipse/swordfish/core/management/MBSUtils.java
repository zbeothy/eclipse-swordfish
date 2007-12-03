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
package org.eclipse.swordfish.core.management;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Utility class to access MBeans in a comfortable way.
 * 
 */
public class MBSUtils {

    /** The mbs. */
    private MBeanServer mbs;

    /**
     * Instantiates a new MBS utils.
     * 
     * @param mbs
     *        the mbs
     */
    public MBSUtils(final MBeanServer mbs) {
        this.mbs = mbs;
    }

    /**
     * Assert exists.
     * 
     * @param name
     *        the name
     * 
     * @return true, if successful
     */
    public boolean assertExists(final ObjectName name) {
        return this.mbs.isRegistered(name);
    }

    /**
     * Assert exists.
     * 
     * @param onString
     *        the on string
     * 
     * @return true, if successful
     * 
     * @throws MalformedObjectNameException
     */
    public boolean assertExists(final String onString) throws MalformedObjectNameException {
        boolean ret = false;
        ObjectName name = new ObjectName(onString);
        ret = this.assertExists(name);
        return ret;
    }

}
