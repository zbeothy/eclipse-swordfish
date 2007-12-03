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
 * The Class ResourceQueryTypeImpl.
 */
public class ResourceQueryTypeImpl implements org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType {

    /** The _ scope path. */
    private org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath scopePath;

    /** The _ tree. */
    private java.lang.String tree;

    /** The _ resource id. */
    private java.lang.String resourceId;

    /** The _ component id. */
    private java.lang.String componentId;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType#getComponentId()
     */
    public java.lang.String getComponentId() {
        return this.componentId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType#getResourceId()
     */
    public java.lang.String getResourceId() {
        return this.resourceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType#getScopePath()
     */
    public org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath getScopePath() {
        return this.scopePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType#getTree()
     */
    public java.lang.String getTree() {
        return this.tree;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType#setComponentId(java.lang.String)
     */
    public void setComponentId(final java.lang.String value) {
        this.componentId = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType#setResourceId(java.lang.String)
     */
    public void setResourceId(final java.lang.String value) {
        this.resourceId = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType#setScopePath(org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath)
     */
    public void setScopePath(final org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath value) {
        this.scopePath = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.query.dom.ResourceQueryType#setTree(java.lang.String)
     */
    public void setTree(final java.lang.String value) {
        this.tree = value;
    }

}
