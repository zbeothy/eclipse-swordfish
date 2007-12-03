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
package org.eclipse.swordfish.core.interceptor.transformation.impl;

/**
 * The Class StyleSheetID.
 * 
 */
public class StyleSheetID {

    /** the sourcepath of the stylesheet. */
    private String sourcePath;

    /** the id of the stylesheet. */
    private String id;

    /**
     * The Constructor.
     * 
     * @param pSourcePath
     *        the SourcePath
     * @param pID
     *        the ID
     */
    public StyleSheetID(final String pSourcePath, final String pID) {
        this.sourcePath = pSourcePath;
        this.id = pID;
    }

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
        if (this == obj) return true;

        if (!(obj instanceof StyleSheetID)) return false;
        final StyleSheetID ssObj = (StyleSheetID) obj;
        if (this.sourcePath != null ? !this.sourcePath.equals(ssObj.sourcePath) : ssObj.sourcePath != null) return false;
        if (this.id != null ? !this.id.equals(ssObj.id) : ssObj.id != null) return false;

        return true;
    }

    /**
     * Gets the as transformation ID.
     * 
     * @return sourcePath in a format that could be processed by the tranformation service
     */
    public String getAsTransformationID() {
        return this.sourcePath + this.id;
    }

    /**
     * Gets the ID.
     * 
     * @return id the Id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Gets the source path.
     * 
     * @return sourcePath the sourcePath
     */
    public String getSourcePath() {
        return this.sourcePath;
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
        int result = 0;
        result = (this.sourcePath != null ? this.sourcePath.hashCode() : 0);
        result = 29 * (this.id != null ? this.id.hashCode() : 0);
        return result;
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
        StringBuffer sb = new StringBuffer();
        sb.append("sourcePath : ");
        sb.append(this.sourcePath);
        sb.append(" id : ");
        sb.append(this.id);
        return sb.toString();
    }

}
