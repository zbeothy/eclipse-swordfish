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

import javax.xml.namespace.QName;

/**
 * The Interface ServiceDescriptionIdentity.
 */
public interface ServiceDescriptionIdentity extends PolicyIdentity {

    /**
     * Gets the name.
     * 
     * @return the <code>QName</code> identifying the service, complements
     * @see PolicyIdentity#getKeyName()
     */
    QName getName();

}
