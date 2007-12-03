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
package org.eclipse.swordfish.configrepos.scopepath.basic.dom;

/**
 * Java content class for PathType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this java content
 * object. (defined at
 * file:/D:/eclipse31/servicemix/sbb-configrepos-scopepath-dom/src/xsd/BasicScopePath.xsd line 30)
 * <p>
 * 
 * <pre>
 * &lt;complexType name=&quot;PathType&quot;&gt;
 * &lt;complexContent&gt;
 * &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 * &lt;sequence&gt;
 * &lt;element ref=&quot;{http://types.sopware.org/configuration/BasicScopePath/1.0}PathPart&quot; maxOccurs=&quot;unbounded&quot;/&gt;
 * &lt;/sequence&gt;
 * &lt;attribute name=&quot;separator&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; default=&quot;/&quot; /&gt;
 * &lt;/restriction&gt;
 * &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public interface PathType {

    /**
     * Gets the value of the PathPart property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any
     * modification you make to the returned list will be present inside the JAXB object. This is
     * why there is not a <CODE>set</CODE> method for the PathPart property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getPathPart().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPartType}
     * {@link org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathPart}
     * 
     * @return the path part
     */
    java.util.List getPathPart();

    /**
     * Gets the value of the separator property.
     * 
     * @return possible object is {@link java.lang.String}
     */
    java.lang.String getSeparator();

    /**
     * Sets the value of the separator property.
     * 
     * @param value
     *        allowed object is {@link java.lang.String}
     */
    void setSeparator(java.lang.String value);

}
