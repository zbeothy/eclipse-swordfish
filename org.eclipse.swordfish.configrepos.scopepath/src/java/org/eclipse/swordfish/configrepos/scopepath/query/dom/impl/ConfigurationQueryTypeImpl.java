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
package org.eclipse.swordfish.configrepos.scopepath.query.dom.impl;

/**
 * The Class ConfigurationQueryTypeImpl.
 */
public class ConfigurationQueryTypeImpl implements org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQueryType {

    /** The _ scope path. */
    private org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath scopePath;

    /** The _ tree. */
    private java.lang.String tree;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQueryType#getScopePath()
     */
    public org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath getScopePath() {
        return this.scopePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQueryType#getTree()
     */
    public java.lang.String getTree() {
        return this.tree;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQueryType#setScopePath(org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath)
     */
    public void setScopePath(final org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath value) {
        this.scopePath = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ConfigurationQueryType#setTree(java.lang.String)
     */
    public void setTree(final java.lang.String value) {
        this.tree = value;
    }

}
