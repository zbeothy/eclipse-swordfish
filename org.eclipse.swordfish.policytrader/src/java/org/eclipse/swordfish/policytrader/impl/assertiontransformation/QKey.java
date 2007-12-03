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
package org.eclipse.swordfish.policytrader.impl.assertiontransformation;

import javax.xml.namespace.QName;

/**
 * The Class QKey.
 */
public class QKey {

    /**
     * Eq.
     * 
     * @param a
     *        the a
     * @param b
     *        the b
     * 
     * @return true, if successful
     */
    private static boolean eq(final Object a, final Object b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    /**
     * Hc.
     * 
     * @param a
     *        the a
     * 
     * @return the int
     */
    private static int hc(final Object a) {
        return a == null ? 0 : a.hashCode();
    }

    /**
     * Zero.
     * 
     * @param s
     *        the s
     * 
     * @return true, if successful
     */
    private static boolean zero(final String s) {
        return (s == null) || (s.length() == 0);
    }

    /** The namespace URI. */
    private final String namespaceURI;

    /** The local name. */
    private final String localName;

    /**
     * Instantiates a new q key.
     * 
     * @param qname
     *        the qname
     */
    public QKey(final QName qname) {
        super();
        this.namespaceURI = qname.getNamespaceURI();
        this.localName = qname.getLocalPart();
    }

    /**
     * Instantiates a new q key.
     * 
     * @param namespaceURI
     *        the namespace URI
     * @param localName
     *        the local name
     */
    public QKey(final String namespaceURI, final String localName) {
        super();
        this.namespaceURI = namespaceURI;
        this.localName = localName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        if ((other == null) || !(other instanceof QKey)) return false;
        QKey o = (QKey) other;
        return eq(this.namespaceURI, o.namespaceURI) && eq(this.localName, o.localName);
    }

    /**
     * Gets the local name.
     * 
     * @return the local name
     */
    public String getLocalName() {
        return this.localName;
    }

    /**
     * Gets the namespace URI.
     * 
     * @return the namespace URI
     */
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return hc(this.namespaceURI) + hc(this.localName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QKey(" + (zero(this.namespaceURI) ? this.localName : "{" + this.namespaceURI + "}" + this.localName) + ")";
    }
}
