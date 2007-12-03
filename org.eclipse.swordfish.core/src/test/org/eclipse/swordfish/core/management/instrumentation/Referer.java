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
package org.eclipse.swordfish.core.management.instrumentation;

import java.util.ArrayList;
import java.util.HashMap;
import javax.management.ObjectName;

/**
 * Managed resource that refers to another managed resource.
 * 
 */
public class Referer {

    /** The attribute. */
    private String attribute = "jatmey";

    /** The collection. */
    private ArrayList collection = new ArrayList();

    /** The children. */
    private ObjectName[] children;

    /** The single child. */
    private ObjectName singleChild;

    /** The map. */
    private HashMap map = new HashMap();

    /**
     * Instantiates a new referer.
     */
    public Referer() {
        this.map.put("foo", "Qap'la");
        this.map.put(new Integer(14), new Float(3.14156));
        this.map.put("NullValue", null);
        this.map.put(null, "NullKey");
        this.map.put(new Integer(15), new Float(3.14156));
        this.map.put(new Integer(16), new Float(3.14156));
        this.map.put(new Integer(17), new Float(3.14156));
        this.map.put(new Integer(18), new Float(3.14156));
        this.map.put(new Integer(19), new Float(3.14156));
        this.map.put(new Integer(150), new Float(3.14156));
        this.map.put(new Integer(151), new Float(3.14156));
        this.map.put(new Integer(152), new Float(3.14156));
        this.map.put(new Integer(153), new Float(3.14156));
        this.map.put(new Integer(154), new Float(3.14156));
        this.map.put(new Integer(155), new Float(3.14156));
        this.map.put(new Integer(156), new Float(3.14156));
    }

    /**
     * Gets the attribute.
     * 
     * @return the attribute
     */
    public String getAttribute() {
        return this.attribute;
    }

    /**
     * Gets the children.
     * 
     * @return the children
     */
    public ObjectName[] getChildren() {
        return this.children;
    }

    /**
     * Gets the collection.
     * 
     * @return the collection
     */
    public ArrayList getCollection() {
        return this.collection;
    }

    /**
     * Gets the map.
     * 
     * @return the map
     */
    public HashMap getMap() {
        return this.map;
    }

    /**
     * Gets the single child.
     * 
     * @return the single child
     */
    public ObjectName getSingleChild() {
        return this.singleChild;
    }

    /**
     * Sets the attribute.
     * 
     * @param attribute
     *        the new attribute
     */
    public void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    /**
     * Sets the child.
     * 
     * @param child
     *        the new child
     */
    public void setChild(final ObjectName child) {
        this.children =
                new ObjectName[] {child, child, child, child, child, child, child, child, child, child, child, child, child, child};
    }

    /**
     * Sets the single child.
     * 
     * @param singleChild
     *        the new single child
     */
    public void setSingleChild(final ObjectName singleChild) {
        this.singleChild = singleChild;
        this.collection.add(singleChild);
        this.collection.add("QiB");
        this.collection.add("choq");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.collection.add("Dol");
        this.map.put("reference", singleChild);
    }

}
