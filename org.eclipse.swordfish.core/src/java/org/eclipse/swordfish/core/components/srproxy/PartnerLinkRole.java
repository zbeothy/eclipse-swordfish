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
package org.eclipse.swordfish.core.components.srproxy;

import javax.xml.namespace.QName;

/**
 * The Interface PartnerLinkRole.
 */
public interface PartnerLinkRole {

    /** The Constant PartnerLinkType_NS. */
    String PATTERN_LINK_TYPE_NS = "http://schemas.xmlsoap.org/ws/2003/05/partner-link/";

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Gets the port type Q name.
     * 
     * @return the port type Q name
     */
    QName getPortTypeQName();

    /**
     * Sets the name.
     * 
     * @param name
     *        the new name
     */
    void setName(String name);

    /**
     * Sets the port type Q name.
     * 
     * @param qname
     *        the new port type Q name
     */
    void setPortTypeQName(QName qname);
}
