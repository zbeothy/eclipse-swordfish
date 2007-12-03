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
package org.eclipse.swordfish.core.utils.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Standard naming strategy. Appropriate for most container.
 * 
 */
public class StandardNamingStrategy implements NamingStrategy {

    /** Singleton instance. */
    private static final StandardNamingStrategy SINGLETON = new StandardNamingStrategy();

    /**
     * Returns the instance of this class.
     * 
     * @return singleton instance
     */
    public static StandardNamingStrategy getInstance() {
        return SINGLETON;
    }

    /**
     * Hidden constructor.
     * 
     */
    private StandardNamingStrategy() {
    }

    /**
     * {@inheritDoc} Returns the ObjectName created with specified <code>objectName</code>
     * parameter.
     * 
     * @see org.eclipse.swordfish.core.utils.jmx.NamingStrategy#createObjectName(java.lang.String)
     */
    public ObjectName createObjectName(final String objectName) throws MalformedObjectNameException {
        return new ObjectName(objectName);
    }

    /**
     * {@inheritDoc}
     * 
     * @return unmodified <code>objectName</code>
     * @see org.eclipse.swordfish.core.utils.jmx.NamingStrategy#getObjectName(javax.management.ObjectName)
     */
    public ObjectName getObjectName(final ObjectName objectName) {
        return objectName;
    }

    /**
     * {@inheritDoc}
     * 
     * @return unmodified <code>objectName</code>
     * @see org.eclipse.swordfish.core.utils.jmx.NamingStrategy#getSimpleObjectName(javax.management.ObjectName)
     */
    public ObjectName getSimpleObjectName(final ObjectName objectName) {
        return objectName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.utils.jmx.NamingStrategy#lookupObjectName(javax.management.ObjectName)
     */
    public ObjectName lookupObjectName(final ObjectName objectName) {
        return objectName;
    }

}
