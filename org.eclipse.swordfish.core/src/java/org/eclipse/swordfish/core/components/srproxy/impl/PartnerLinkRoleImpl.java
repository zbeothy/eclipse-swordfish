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
package org.eclipse.swordfish.core.components.srproxy.impl;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole;

/**
 * The Class PartnerLinkRoleImpl.
 */
public class PartnerLinkRoleImpl implements PartnerLinkRole {

    /** The name. */
    private String name;

    /** The port type name. */
    private QName portTypeName;

    /**
     * Instantiates a new partner link role impl.
     */
    public PartnerLinkRoleImpl() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole#getPortTypeQName()
     */
    public QName getPortTypeQName() {
        return this.portTypeName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole#setName(java.lang.String)
     */
    public void setName(final String name) {
        if (name == null) throw new IllegalArgumentException("PartnerLinkRole names must not be null");
        this.name = name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole#setPortTypeQName(javax.xml.namespace.QName)
     */
    public void setPortTypeQName(final QName qname) {
        if (qname == null) throw new IllegalArgumentException("PartnerLinkRole must be a non-null portType name");
        this.portTypeName = qname;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String ns = "";
        String localPart = "";
        if (this.getPortTypeQName() != null) {
            ns = this.getPortTypeQName().getNamespaceURI().trim();
            localPart = this.getPortTypeQName().getLocalPart();
        }
        String str = null;
        if (ns.length() > 0) {
            str =
                    "<plnk:role name=\"" + this.getName() + "\" >" + "\n<plnk:portType xmlns:ns0=\"" + ns + "\" name=\"ns0:"
                            + localPart + "\" />" + "\n</plnk:role>";

        } else {
            str =
                    "<plnk:role name=\"" + this.getName() + "\" >" + "\n<plnk:portType name=\"" + localPart + "\" />"
                            + "\n</plnk:role>";
        }
        return str;
    }

}
