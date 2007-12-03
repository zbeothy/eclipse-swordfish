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
package org.eclipse.swordfish.configrepos.scopepath.basic.dom.impl;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The Class PathPartTypeImpl.
 * 
 */
public class PathPartTypeImpl implements org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPartType {

    /** The _ type. */
    private java.lang.String type;

    /** The _ value. */
    private java.lang.String value;

    /**
     * (non-Javadoc).
     * 
     * @param obj
     *        the obj
     * 
     * @return true, if equals
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!this.getClass().isInstance(obj) || (null == obj)) return false;
        PathPartTypeImpl ppti = (PathPartTypeImpl) obj;

        if (((null == this.type) && (null != ppti.type)) || ((null != this.type) && (null == ppti.type))
                || ((null == this.value) && (null != ppti.value)) || ((null != this.value) && (null == ppti.value))) return false;

        if (this.type.equalsIgnoreCase(ppti.type) && this.value.equalsIgnoreCase(ppti.value)) return true;

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPartType#getType()
     */
    public java.lang.String getType() {
        return this.type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPartType#getValue()
     */
    public java.lang.String getValue() {
        return this.value;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the int
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(1033, 1483).append(this.type).append(this.value).toHashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPartType#setType(java.lang.String)
     */
    public void setType(final java.lang.String type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPartType#setValue(java.lang.String)
     */
    public void setValue(final java.lang.String value) {
        this.value = value;
    }

    /**
     * (non-Javadoc).
     * 
     * @see java.lang.Object#toString()
     * @return String
     */
    @Override
    public String toString() {
        return this.type + "=" + this.value;
    }

}
