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
package org.eclipse.swordfish.policytrader.impl;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.policytrader.PolicyFactory;
import org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity;

/**
 * Standard implementation of {@link ServiceDescriptionIdentity}.
 */
public class StandardServiceDescriptionIdentity implements ServiceDescriptionIdentity {

    /** String representation of name attribute. */
    private final String keyName;

    /** Identification of a service. */
    private final QName name;

    /** Optional attribute for use by clients. */
    private String location = null;

    /**
     * Constructor used by {@link PolicyFactory}.
     * 
     * @param name
     *        the name
     */
    public StandardServiceDescriptionIdentity(final QName name) {
        super();
        this.name = name;
        this.keyName = this.getStringFromQName(name);
    }

    /**
     * Constructor used by {@link PolicyFactory}.
     * 
     * @param location
     *        location
     * @param name
     *        the name
     */
    public StandardServiceDescriptionIdentity(final QName name, final String location) {
        super();
        this.name = name;
        this.location = location;
        this.keyName = this.getStringFromQName(name);
    }

    /**
     * Copy constructor.
     * 
     * @param id
     *        source identity
     */
    public StandardServiceDescriptionIdentity(final ServiceDescriptionIdentity id) {
        super();
        this.keyName = id.getKeyName();
        this.location = id.getLocation();
        this.name = this.getQNameFromString(this.keyName);
    }

    /**
     * Constructor used by {@link PolicyFactory}.
     * 
     * @param keyName
     *        key name (usually the policy reference URI)
     */
    public StandardServiceDescriptionIdentity(final String keyName) {
        super();
        this.keyName = keyName;
        this.name = this.getQNameFromString(keyName);
    }

    /**
     * Constructor used by {@link PolicyFactory}.
     * 
     * @param keyName
     *        key name (usually the policy reference URI)
     * @param location
     *        location
     */
    public StandardServiceDescriptionIdentity(final String keyName, final String location) {
        super();
        this.keyName = keyName;
        this.location = location;
        this.name = this.getQNameFromString(keyName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if ((null == other) || !(other instanceof StandardServiceDescriptionIdentity)) return false;
        if (null == this.keyName) return (null == ((StandardServiceDescriptionIdentity) other).getKeyName());
        return this.keyName.equals(((StandardServiceDescriptionIdentity) other).getKeyName());
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.OperationPolicyIdentity#getKeyName()
     */
    public String getKeyName() {
        return this.keyName;
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.OperationPolicyIdentity#getLocation()
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.ServiceDescriptionIdentity#getName()
     */
    public QName getName() {
        return this.name;
    }

    /**
     * Gets the q name from string.
     * 
     * @param key
     *        the key
     * 
     * @return the q name from string
     */
    public QName getQNameFromString(final String key) {
        QName ret = null;
        if (key.startsWith("{")) {
            int endIndex = key.indexOf('}');
            if (-1 != endIndex) {
                String namespaceUri = key.substring(1, endIndex);
                String localPart = key.substring(endIndex + 1);
                ret = new QName(namespaceUri, localPart);
            }
        } else {
            ret = new QName(key);
        }
        return ret;
    }

    /**
     * Gets the string from Q name.
     * 
     * @param qName
     *        the name
     * 
     * @return the string from Q name
     */
    public String getStringFromQName(final QName qName) {
        StringBuffer ret = new StringBuffer();
        if (null != qName.getNamespaceURI()) {
            ret.append('{').append(qName.getNamespaceURI()).append('}');
        }
        ret.append(qName.getLocalPart());
        return new String(ret);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (null == this.keyName) ? 0 : this.keyName.hashCode();
    }

    /*
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.OperationPolicyIdentity#setLocation(java.lang.String)
     */
    public void setLocation(final String location) {
        this.location = location;
    }

}
