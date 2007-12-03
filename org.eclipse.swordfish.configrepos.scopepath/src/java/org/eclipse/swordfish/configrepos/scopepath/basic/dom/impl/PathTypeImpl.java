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

import java.util.Iterator;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.swordfish.configrepos.shared.ConfigurationConstants;

/**
 * The Class PathTypeImpl.
 */
public class PathTypeImpl implements org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathType {

    /** The _ separator. */
    private java.lang.String separator;

    /** The _ path part. */
    private java.util.List pathPart;

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
        PathTypeImpl target = (PathTypeImpl) obj;
        Iterator thisIter = this.pathPart.iterator();
        Iterator thatIter = target.pathPart.iterator();
        while (thisIter.hasNext()) {
            if (!thisIter.next().equals(thatIter.next())) return false;
        }

        if (!thisIter.hasNext() && !thatIter.hasNext()) return true;

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathType#getPathPart()
     */
    public java.util.List getPathPart() {
        return this.getSafePathPart();
    }

    /**
     * (non-Javadoc).
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathType#getSeparator()
     * @return java.lang.String
     */
    public java.lang.String getSeparator() {
        if (this.separator == null)
            return ConfigurationConstants.CONFIGREPOS_SCOPEPATH_SUBSCOPESEPARATOR;
        else
            return this.separator;
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
        HashCodeBuilder builder = new HashCodeBuilder(1481, 1777);
        Iterator iter = this.pathPart.iterator();
        while (iter.hasNext()) {
            builder.appendSuper(iter.next().hashCode());
        }
        return builder.toHashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathType#setSeparator(java.lang.String)
     */
    public void setSeparator(final java.lang.String value) {
        this.separator = value;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the string
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        Iterator iter = this.pathPart.iterator();
        while (iter.hasNext()) {
            result.append(((PathPartTypeImpl) iter.next()).toString());
            if (iter.hasNext()) {
                result.append("/");
            }
        }
        return new String(result);
    }

    /**
     * _get path part.
     * 
     * @return the java.util. list
     */
    protected java.util.List getSafePathPart() {
        if (this.pathPart == null) {
            this.pathPart = new java.util.ArrayList();
        }
        return this.pathPart;
    }
}
