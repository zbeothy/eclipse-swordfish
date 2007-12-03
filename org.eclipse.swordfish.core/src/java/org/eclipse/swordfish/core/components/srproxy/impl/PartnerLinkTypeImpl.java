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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole;
import org.eclipse.swordfish.core.components.srproxy.PartnerLinkType;
import org.eclipse.swordfish.core.utils.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class PartnerLinkTypeImpl.
 */
public class PartnerLinkTypeImpl implements ExtensibilityElement, PartnerLinkType {

    /** The name. */
    private String name;

    /** The roles. */
    private List roles;

    /** The elem type. */
    private QName elemType;

    /** The required. */
    private Boolean required;

    /**
     * Instantiates a new partner link type impl.
     */
    public PartnerLinkTypeImpl() {
        this.roles = new ArrayList();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#addPartnerLinkRole(org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole)
     */
    public void addPartnerLinkRole(final PartnerLinkRole role) {
        if (this.getPartnerLinkRole(role.getName()) != null) {
            // replace in the case of this role already existing
            this.removePartnerLinkRole(this.getPartnerLinkRole(role.getName()));
            this.roles.add(role);
        } else {
            if (this.roles.size() == 2)
                throw new IllegalArgumentException("cardinality of PartnerLinkType must not excced 2");
            else {
                this.roles.add(role);
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#addPartnerLinkRole(org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole[])
     */
    public void addPartnerLinkRole(final PartnerLinkRole[] role) {
        for (int i = 0; i < role.length; i++) {
            this.addPartnerLinkRole(role[i]);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#createPartnerLinkRole()
     */
    public PartnerLinkRole createPartnerLinkRole() {
        return new PartnerLinkRoleImpl();
    }

    /**
     * Gets the element.
     * 
     * @return the element
     */
    public Element getElement() {
        try {
            return XMLUtil.docFromString(this.toString()).getDocumentElement();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("cannot serialize partnerlink type ", e);
        } catch (SAXException e) {
            throw new RuntimeException("cannot serialize partnerlink type ", e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensibilityElement#getElementType()
     */
    public QName getElementType() {
        return this.elemType;
    }

    // Biz. Methods
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#getPartnerLinkRole(java.lang.String)
     */
    public PartnerLinkRole getPartnerLinkRole(final String sName) {
        for (int i = 0; i < this.roles.size(); i++) {
            if (((PartnerLinkRole) this.roles.get(i)).getName().equals(sName)) return ((PartnerLinkRole) this.roles.get(i));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#getPartnerLinkRoles()
     */
    public PartnerLinkRole[] getPartnerLinkRoles() {
        PartnerLinkRole[] roleArray = new PartnerLinkRole[this.roles.size()];
        return (PartnerLinkRole[]) this.roles.toArray(roleArray);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensibilityElement#getRequired()
     */
    public Boolean getRequired() {
        return this.required;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#removePartnerLinkRole(org.eclipse.swordfish.core.components.srproxy.PartnerLinkRole)
     */
    public void removePartnerLinkRole(final PartnerLinkRole role) {
        this.roles.remove(role);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#removePartnerLinkRole(java.lang.String)
     */
    public void removePartnerLinkRole(final String sName) {
        PartnerLinkRole role = this.getPartnerLinkRole(sName);
        this.roles.remove(role);
    }

    /**
     * Sets the element.
     * 
     * @param elem
     *        the new element
     */
    public void setElement(final Element elem) {
        String sName = elem.getAttribute("name");
        this.setName(sName);
        NodeList nl = elem.getElementsByTagNameNS(PATTERN_LINK_TYPE_NS, "role");
        for (int i = 0; i < nl.getLength(); i++) {
            PartnerLinkRole role = this.createPartnerLinkRole();
            Element el = (Element) nl.item(i);
            role.setName(el.getAttribute("name"));
            // be able to handle both, if the portType is an attribute
            // but also if it is a nested element
            String ptName = el.getAttribute("portType");
            if (!"".equals(ptName)) {
                ptName = ptName.trim();
            } else {
                NodeList nl2 = el.getElementsByTagNameNS(PATTERN_LINK_TYPE_NS, "portType");
                if (nl2.getLength() == 0) {
                    continue;
                }
                el = (Element) nl2.item(0);
                ptName = el.getAttribute("name");
                ptName = ptName.trim();
            }
            if (ptName.indexOf(":") > 0) {
                String prefix = ptName.substring(0, ptName.indexOf(":")).trim();
                String suffix = ptName.substring(ptName.indexOf(":") + 1, ptName.length()).trim();
                String ns = XMLUtil.getNameSpaceForPrefix(el, prefix);
                role.setPortTypeQName(new QName(ns, suffix));
            } else {
                role.setPortTypeQName(new QName("", ptName));
            }
            this.addPartnerLinkRole(role);
        }
    }

    // extensibility element methods
    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensibilityElement#setElementType(javax.xml.namespace.QName)
     */
    public void setElementType(final QName type) {
        this.elemType = type;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.core.components.srproxy.PartnerLinkType#setName(java.lang.String)
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.wsdl.extensions.ExtensibilityElement#setRequired(java.lang.Boolean)
     */
    public void setRequired(final Boolean req) {
        this.required = req;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String str = "<plnk:partnerLinkType xmlns:plnk=\"" + PATTERN_LINK_TYPE_NS + "\" name=\"" + this.getName() + "\">\n";
        PartnerLinkRole[] array = this.getPartnerLinkRoles();
        for (int i = 0; i < array.length; i++) {
            str = str + array[i].toString() + "\n";
        }
        str = str + "</plnk:partnerLinkType>";
        return str;
    }
}
