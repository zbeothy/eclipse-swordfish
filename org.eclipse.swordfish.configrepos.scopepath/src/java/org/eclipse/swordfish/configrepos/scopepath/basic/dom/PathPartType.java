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
 * Java content class for PathPartType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this java content
 * object. (defined at
 * file:/D:/eclipse31/servicemix/sbb-configrepos-scopepath-dom/src/xsd/BasicScopePath.xsd line 40)
 * <p>
 * 
 * <pre>
 * &lt;complexType name=&quot;PathPartType&quot;&gt;
 * &lt;complexContent&gt;
 * &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 * &lt;attribute name=&quot;type&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 * &lt;attribute name=&quot;value&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 * &lt;/restriction&gt;
 * &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public interface PathPartType {

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link java.lang.String}
     */
    java.lang.String getType();

    /**
     * Gets the value of the value property.
     * 
     * @return possible object is {@link java.lang.String}
     */
    java.lang.String getValue();

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *        allowed object is {@link java.lang.String}
     */
    void setType(java.lang.String value);

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *        allowed object is {@link java.lang.String}
     */
    void setValue(java.lang.String value);

}
