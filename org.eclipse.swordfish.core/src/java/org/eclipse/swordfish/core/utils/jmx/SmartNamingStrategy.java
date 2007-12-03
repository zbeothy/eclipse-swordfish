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
 * The naming strategy for Spring framework. This class shall be extended by another class in
 * sbb-se-core (which will implement spring ObjectNamingstrategy) to be specified as naming strategy
 * in configuration XML. This is needed due to ClassLoading specifics.
 * 
 */
public class SmartNamingStrategy {

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.jmx.export.naming.ObjectNamingStrategy#getObjectName(java.lang.Object,
     *      java.lang.String)
     */
    public ObjectName getObjectName(final Object managedBean, final String beanKey) throws MalformedObjectNameException {
        return NamingStrategyFactory.getNamingStrategy().createObjectName(beanKey);
    }

}
