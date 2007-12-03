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
package org.eclipse.swordfish.configrepos.scopepath.query.dom;

/**
 * Java content class for ResourceQueryType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this java content
 * object. (defined at
 * file:/D:/eclipse31/servicemix/sbb-configrepos-scopepath-dom/src/xsd/ConfigurationQuery.xsd line
 * 34)
 * <p>
 * 
 * <pre>
 * &lt;complexType name=&quot;ResourceQueryType&quot;&gt;
 * &lt;complexContent&gt;
 * &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 * &lt;sequence&gt;
 * &lt;element ref=&quot;{http://types.sopware.org/configuration/BasicScopePath/1.0}ScopePath&quot;/&gt;
 * &lt;element name=&quot;tree&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 * &lt;element name=&quot;componentId&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 * &lt;element name=&quot;resourceId&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;/&gt;
 * &lt;/sequence&gt;
 * &lt;/restriction&gt;
 * &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
public interface ResourceQueryType {

    /**
     * Gets the value of the componentId property.
     * 
     * @return possible object is {@link java.lang.String}
     */
    java.lang.String getComponentId();

    /**
     * Gets the value of the resourceId property.
     * 
     * @return possible object is {@link java.lang.String}
     */
    java.lang.String getResourceId();

    /**
     * Gets the value of the scopePath property.
     * 
     * @return possible object is
     *         {@link org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathType}
     *         {@link org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath}
     */
    org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath getScopePath();

    /**
     * Gets the value of the tree property.
     * 
     * @return possible object is {@link java.lang.String}
     */
    java.lang.String getTree();

    /**
     * Sets the value of the componentId property.
     * 
     * @param value
     *        allowed object is {@link java.lang.String}
     */
    void setComponentId(java.lang.String value);

    /**
     * Sets the value of the resourceId property.
     * 
     * @param value
     *        allowed object is {@link java.lang.String}
     */
    void setResourceId(java.lang.String value);

    /**
     * Sets the value of the scopePath property.
     * 
     * @param value
     *        allowed object is
     *        {@link org.eclipse.swordfish.configrepos.scopepath.basic.dom.PathType}
     *        {@link org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath}
     */
    void setScopePath(org.eclipse.swordfish.configrepos.scopepath.basic.dom.ScopePath value);

    /**
     * Sets the value of the tree property.
     * 
     * @param value
     *        allowed object is {@link java.lang.String}
     */
    void setTree(java.lang.String value);

}
