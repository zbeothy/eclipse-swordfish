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
 * The naming strategy for JMX.
 * 
 */
public interface NamingStrategy {

    /**
     * Creates ObjectName for specified string. Extends the object name properties if needed. This
     * method performs similar to {@link #getObjectName(ObjectName)}.
     * 
     * @param objectName
     *        the string representation of expected ObjectName
     * @return the ObjectName appropriate for current container
     * @see #getObjectName(ObjectName)
     * @throws MalformedObjectNameException
     *         if cannot construct object name
     */
    ObjectName createObjectName(String objectName) throws MalformedObjectNameException;

    /**
     * Extends the object name properties if needed for current naming strategy. This method
     * performs similar to {@link #createObjectName(String)}.
     * 
     * @param objectName
     *        object name to extend
     * @return the object name appropriate for current naming strategy.
     * @see #createObjectName(String)
     */
    ObjectName getObjectName(ObjectName objectName);

    /**
     * Simplifies the complex object name by removing this naming strategy specific properties if
     * needed.
     * 
     * @param objectName
     *        the object name to simplify
     * @return the simplified object name
     * @see #createObjectName(String)
     * @see #getObjectName(ObjectName)
     */
    ObjectName getSimpleObjectName(ObjectName objectName);

    /**
     * Ensures that object with given name template is registered. Completes the object name if
     * necessary.
     * 
     * @param objectName
     *        object name to extend
     * @return the object name of an existing object
     * @see #createObjectName(String)
     */
    ObjectName lookupObjectName(ObjectName objectName);

}
