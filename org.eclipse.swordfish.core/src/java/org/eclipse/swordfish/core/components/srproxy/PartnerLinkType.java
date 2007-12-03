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

/**
 * The Interface PartnerLinkType.
 */
public interface PartnerLinkType {

    /** The Constant PartnerLinkType_NS. */
    String PATTERN_LINK_TYPE_NS = "http://schemas.xmlsoap.org/ws/2003/05/partner-link/";

    /**
     * Adds the partner link role.
     * 
     * @param role
     *        the role
     */
    void addPartnerLinkRole(PartnerLinkRole role);

    /**
     * Adds the partner link role.
     * 
     * @param role
     *        the role
     */
    void addPartnerLinkRole(PartnerLinkRole[] role);

    /**
     * Creates the partner link role.
     * 
     * @return the partner link role
     */
    PartnerLinkRole createPartnerLinkRole();

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Gets the partner link role.
     * 
     * @param name
     *        the name
     * 
     * @return the partner link role
     */
    PartnerLinkRole getPartnerLinkRole(String name);

    /**
     * Gets the partner link roles.
     * 
     * @return the partner link roles
     */
    PartnerLinkRole[] getPartnerLinkRoles();

    /**
     * Removes the partner link role.
     * 
     * @param role
     *        the role
     */
    void removePartnerLinkRole(PartnerLinkRole role);

    /**
     * Removes the partner link role.
     * 
     * @param name
     *        the name
     */
    void removePartnerLinkRole(String name);

    /**
     * Sets the name.
     * 
     * @param name
     *        the new name
     */
    void setName(String name);
}
