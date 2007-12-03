/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Apache Software Foundation, Deutsche Post AG
 **************************************************************************************************/
/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.eclipse.swordfish.configrepos.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertyConverter;
import org.apache.commons.lang.StringUtils;

/**
 * The Class HierarchicalConfiguration.
 * 
 * 
 * adaption of commons-configuration HierarchicalConfiguration
 * 
 * <p>
 * A specialized configuration class that extends its base class by the ability of keeping more
 * structure in the stored properties.
 * </p>
 * <p>
 * There are some sources of configuration data that cannot be stored very well in a
 * <code>BaseConfiguration</code> object because then their structure is lost. This is especially
 * true for XML documents. This class can deal with such structured configuration sources by storing
 * the properties in a tree-like organization.
 * </p>
 * <p>
 * The internal used storage form allows for a more sophisticated access to single properties. As an
 * example consider the following XML document:
 * </p>
 * <p>
 * 
 * <pre>
 * &lt;database&gt;
 * &lt;tables&gt;
 * &lt;table&gt;
 * &lt;name&gt;users&lt;/name&gt;
 * &lt;fields&gt;
 * &lt;field&gt;
 * &lt;name&gt;lid&lt;/name&gt;
 * &lt;type&gt;long&lt;/name&gt;
 * &lt;/field&gt;
 * &lt;field&gt;
 * &lt;name&gt;usrName&lt;/name&gt;
 * &lt;type&gt;java.lang.String&lt;/type&gt;
 * &lt;/field&gt;
 * ...
 * &lt;/fields&gt;
 * &lt;/table&gt;
 * &lt;table&gt;
 * &lt;name&gt;documents&lt;/name&gt;
 * &lt;fields&gt;
 * &lt;field&gt;
 * &lt;name&gt;docid&lt;/name&gt;
 * &lt;type&gt;long&lt;/type&gt;
 * &lt;/field&gt;
 * ...
 * &lt;/fields&gt;
 * &lt;/table&gt;
 * ...
 * &lt;/tables&gt;
 * &lt;/database&gt;
 * </pre>
 * 
 * </p>
 * <p>
 * If this document is parsed and stored in a <code>HierarchicalConfiguration</code> object (which
 * can be done by one of the sub classes), there are enhanced possibilities of accessing properties.
 * The keys for querying information can contain indices that select a certain element if there are
 * multiple hits.
 * </p>
 * <p>
 * For instance the key <code>tables.table(0).name</code> can be used to find out the name of the
 * first table. In opposite <code>tables.table.name</code> would return a collection with the
 * names of all available tables. Similarily the key <code>tables.table(1).fields.field.name</code>
 * returns a collection with the names of all fields of the second table. If another index is added
 * after the <code>field</code> element, a single field can be accessed:
 * <code>tables.table(1).fields.field(0).name</code>.
 * </p>
 * <p>
 * There is a <code>getMaxIndex()</code> method that returns the maximum allowed index that can be
 * added to a given property key. This method can be used to iterate over all values defined for a
 * certain property.
 * </p>
 * ebourg Exp $
 */
public class HierarchicalConfiguration extends AbstractConfiguration implements Serializable, Cloneable {

    /**
     * 
     */
    private static final long serialVersionUID = -3614083416082670955L;

    /** Constant for a new dummy key. */
    private static final String NEW_KEY = "newKey";

    /** Stores the root node of this configuration. */
    private Node root = new Node();

    /**
     * Adds a collection of nodes at the specified position of the configuration tree. This method
     * works similar to <code>addProperty()</code>, but instead of a single property a whole
     * collection of nodes can be added - and thus complete configuration sub trees. E.g. with this
     * method it is possible to add parts of another <code>HierarchicalConfiguration</code> object
     * to this object.
     * 
     * @param key
     *        the key where the nodes are to be added; can be <b>null </b>, then they are added to
     *        the root node
     * @param nodes
     *        a collection with the <code>Node</code> objects to be added
     */
    public void addNodes(final String key, final Collection nodes) {
        if ((nodes == null) || nodes.isEmpty()) return;

        Node parent;
        if (StringUtils.isEmpty(key)) {
            parent = this.getRoot();
        } else {
            ConfigurationKey.KeyIterator kit = new ConfigurationKey(key).iterator();
            parent = this.fetchAddNode(kit, this.getRoot());

            // fetchAddNode() does not really fetch the last component,
            // but one before. So we must perform an additional step.
            ConfigurationKey keyNew = new ConfigurationKey(kit.currentKey(true));
            keyNew.append(NEW_KEY);
            parent = this.fetchAddNode(keyNew.iterator(), parent);
        }

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            parent.addChild((Node) it.next());
        }
    }

    /**
     * Removes the property with the given key. Properties with names that start with the given key
     * (i.e. properties below the specified key in the hierarchy) won't be affected.
     * 
     * @param key
     *        the key of the property to be removed
     */
    @Override
    public void clearProperty(final String key) {
        List nodes = this.fetchNodeList(key);

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            this.clearNode((Node) it.next());
        }
    }

    /**
     * Removes all values of the property with the given name and of keys that start with this name.
     * So if there is a property with the key &quot;foo&quot; and a property with the key
     * &quot;foo.bar&quot;, a call of <code>clearTree("foo")</code> would remove both properties.
     * 
     * @param key
     *        the key of the property to be removed
     */
    public void clearTree(final String key) {
        List nodes = this.fetchNodeList(key);

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            this.removeNode((Node) it.next());
        }
    }

    /**
     * Creates a copy of this object. This new configuration object will contain copies of all nodes
     * in the same structure.
     * 
     * @return the copy
     */
    @Override
    public Object clone() {
        try {
            HierarchicalConfiguration copy = (HierarchicalConfiguration) super.clone();

            // clone the nodes, too
            CloneVisitor v = new CloneVisitor();
            this.getRoot().visit(v, null);
            copy.setRoot(v.getClone());

            return copy;
        } catch (CloneNotSupportedException cex) {
            // should not happen
            throw new ConfigurationRuntimeException(cex);
        }
    }

    /**
     * Checks if the specified key is contained in this configuration. Note that for this
     * configuration the term &quot;contained&quot; means that the key has an associated value. If
     * there is a node for this key that has no value but children (either defined or undefined),
     * this method will still return <b>false </b>.
     * 
     * @param key
     *        the key to be chekced
     * 
     * @return a flag if this key is contained in this configuration
     */
    @Override
    public boolean containsKey(final String key) {
        return this.getProperty(key) != null;
    }

    /**
     * Returns an iterator with all keys defined in this configuration. Note that the keys returned
     * by this method will not contain any indices. This means that some structure will be lost.
     * <p/>
     * 
     * @return an iterator with the defined keys in this configuration
     */
    @Override
    public Iterator getKeys() {
        DefinedKeysVisitor visitor = new DefinedKeysVisitor();
        this.getRoot().visit(visitor, new ConfigurationKey());

        return visitor.getKeyList().iterator();
    }

    /**
     * Returns an iterator with all keys defined in this configuration that start with the given
     * prefix. The returned keys will not contain any indices.
     * 
     * @param prefix
     *        the prefix of the keys to start with
     * 
     * @return an iterator with the found keys
     */
    @Override
    public Iterator getKeys(final String prefix) {
        DefinedKeysVisitor visitor = new DefinedKeysVisitor(prefix);
        List nodes = this.fetchNodeList(prefix);
        ConfigurationKey key = new ConfigurationKey();

        for (Iterator itNodes = nodes.iterator(); itNodes.hasNext();) {
            Node node = (Node) itNodes.next();
            for (Iterator it = node.getChildren().iterator(); it.hasNext();) {
                ((Node) it.next()).visit(visitor, key);
            }
        }

        return visitor.getKeyList().iterator();
    }

    /**
     * Returns the maximum defined index for the given key. This is useful if there are multiple
     * values for this key. They can then be addressed separately by specifying indices from 0 to
     * the return value of this method.
     * 
     * @param key
     *        the key to be checked
     * 
     * @return the maximum defined index for this key
     */
    public int getMaxIndex(final String key) {
        return this.fetchNodeList(key).size() - 1;
    }

    /**
     * Fetches the specified property. Performs a recursive lookup in the tree with the
     * configuration properties.
     * 
     * @param key
     *        the key to be looked up
     * 
     * @return the found value
     */
    public Object getProperty(final String key) {
        List nodes = this.fetchNodeList(key);

        if (nodes.size() == 0)
            return null;
        else {
            List list = new ArrayList();
            for (Iterator it = nodes.iterator(); it.hasNext();) {
                Node node = (Node) it.next();
                if (node.getValue() != null) {
                    list.add(node.getValue());
                }
            }

            if (list.size() < 1)
                return null;
            else
                return (list.size() == 1) ? list.get(0) : list;
        }
    }

    /**
     * Returns the root node of this hierarchical configuration.
     * 
     * @return the root node
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * Checks if this configuration is empty. Empty means that there are no keys with any values,
     * though there can be some (empty) nodes.
     * 
     * @return a flag if this configuration is empty
     */
    @Override
    public boolean isEmpty() {
        return !this.nodeDefined(this.getRoot());
    }

    /**
     * Sets the value of the specified property.
     * 
     * @param key
     *        the key of the property to set
     * @param value
     *        the new value of this property
     */
    @Override
    public void setProperty(final String key, final Object value) {
        Iterator itNodes = this.fetchNodeList(key).iterator();
        Iterator itValues = PropertyConverter.toIterator(value, getDelimiter());
        while (itNodes.hasNext() && itValues.hasNext()) {
            ((Node) itNodes.next()).setValue(itValues.next());
        }

        // Add additional nodes if necessary
        while (itValues.hasNext()) {
            this.addPropertyDirect(key, itValues.next());
        }

        // Remove remaining nodes
        while (itNodes.hasNext()) {
            this.clearNode((Node) itNodes.next());
        }
    }

    /**
     * Sets the root node of this hierarchical configuration.
     * 
     * @param node
     *        the root node
     */
    public void setRoot(final Node node) {
        if (node == null) throw new IllegalArgumentException("Root node must not be null!");
        this.root = node;
    }

    /**
     * Creates a new <code>Configuration</code> object containing all keys that start with the
     * specified prefix. This implementation will return a <code>HierarchicalConfiguration</code>
     * object so that the structure of the keys will be saved.
     * 
     * @param prefix
     *        the prefix of the keys for the subset
     * 
     * @return a new configuration object representing the selected subset
     */
    @Override
    public Configuration subset(final String prefix) {
        Collection nodes = this.fetchNodeList(prefix);
        if (nodes.isEmpty()) return new HierarchicalConfiguration();

        HierarchicalConfiguration result = new HierarchicalConfiguration();
        CloneVisitor visitor = new CloneVisitor();

        for (Iterator it = nodes.iterator(); it.hasNext();) {
            Node nd = (Node) it.next();
            nd.visit(visitor, null);

            List children = visitor.getClone().getChildren();
            if (children.size() > 0) {
                for (int i = 0; i < children.size(); i++) {
                    result.getRoot().addChild((Node) children.get(i));
                }
            }
        }

        return (result.isEmpty()) ? new HierarchicalConfiguration() : result;
    }

    /**
     * <p>
     * Adds the property with the specified key.
     * </p>
     * <p>
     * To be able to deal with the structure supported by this configuration implementation the
     * passed in key is of importance, especially the indices it might contain. The following
     * example should clearify this: Suppose the actual configuration contains the following
     * elements:
     * </p>
     * <p>
     * 
     * <pre>
     * tables
     * +-- table
     * +-- name = user
     * +-- fields
     * +-- field
     * +-- name = uid
     * +-- field
     * +-- name = firstName
     * ...
     * +-- table
     * +-- name = documents
     * +-- fields
     * ...
     * </pre>
     * 
     * </p>
     * <p>
     * In this example a database structure is defined, e.g. all fields of the first table could be
     * accessed using the key <code>tables.table(0).fields.field.name</code>. If now properties
     * are to be added, it must be exactly specified at which position in the hierarchy the new
     * property is to be inserted. So to add a new field name to a table it is not enough to say
     * just
     * </p>
     * <p>
     * 
     * <pre>
     * config.addProperty(&quot;tables.table.fields.field.name&quot;, &quot;newField&quot;);
     * </pre>
     * 
     * </p>
     * <p>
     * The statement given above contains some ambiguity. For instance it is not clear, to which
     * table the new field should be added. If this method finds such an ambiguity, it is resolved
     * by following the last valid path. Here this would be the last table. The same is true for the
     * <code>field</code>; because there are multiple fields and no explicit index is provided, a
     * new <code>name</code> property would be added to the last field - which is propably not
     * what was desired.
     * </p>
     * <p>
     * To make things clear explicit indices should be provided whenever possible. In the example
     * above the exact table could be specified by providing an index for the <code>table</code>
     * element as in <code>tables.table(1).fields</code>. By specifying an index it can also be
     * expressed that at a given position in the configuration tree a new branch should be added. In
     * the example above we did not want to add an additional <code>name</code> element to the
     * last field of the table, but we want a complete new <code>field</code> element. This can be
     * achieved by specifying an invalid index (like -1) after the element where a new branch should
     * be created. Given this our example would run:
     * </p>
     * <p>
     * 
     * <pre>
     * config.addProperty(&quot;tables.table(1).fields.field(-1).name&quot;, &quot;newField&quot;);
     * </pre>
     * 
     * </p>
     * <p>
     * With this notation it is possible to add new branches everywhere. We could for instance
     * create a new <code>table</code> element by specifying
     * </p>
     * <p>
     * 
     * <pre>
     * config.addProperty(&quot;tables.table(-1).fields.field.name&quot;, &quot;newField2&quot;);
     * </pre>
     * 
     * </p>
     * <p>
     * (Note that because after the <code>table</code> element a new branch is created indices in
     * following elements are not relevant; the branch is new so there cannot be any ambiguities.)
     * </p>
     * 
     * @param key
     *        the key of the new property
     * @param obj
     *        the value of the new property
     */
    @Override
    protected void addPropertyDirect(final String key, final Object obj) {
        ConfigurationKey.KeyIterator it = new ConfigurationKey(key).iterator();
        Node parent = this.fetchAddNode(it, this.getRoot());

        Node child = this.createNode(it.currentKey(true));
        child.setValue(obj);
        parent.addChild(child);
    }

    /**
     * Clears the value of the specified node. If the node becomes undefined by this operation, it
     * is removed from the hierarchy.
     * 
     * @param node
     *        the node to be cleard
     */
    protected void clearNode(final Node node) {
        node.setValue(null);
        if (!this.nodeDefined(node)) {
            this.removeNode(node);
        }
    }

    /**
     * Creates the missing nodes for adding a new property. This method ensures that there are
     * corresponding nodes for all components of the specified configuration key.
     * 
     * @param keyIt
     *        the key iterator
     * @param nRoot
     *        the base node of the path to be created
     * 
     * @return the last node of the path
     */
    protected Node createAddPath(final ConfigurationKey.KeyIterator keyIt, final Node nRoot) {
        if (keyIt.hasNext()) {
            Node child = this.createNode(keyIt.currentKey(true));
            nRoot.addChild(child);
            keyIt.next();
            return this.createAddPath(keyIt, child);
        } else
            return nRoot;
    }

    /**
     * Creates a new <code>Node</code> object with the specified name. This method can be
     * overloaded in derived classes if a specific node type is needed. This base implementation
     * always returns a new object of the <code>Node</code> class.
     * 
     * @param name
     *        the name of the new node
     * 
     * @return the new node
     */
    protected Node createNode(final String name) {
        return new Node(name);
    }

    /**
     * Returns a reference to the parent node of an add operation. Nodes for new properties can be
     * added as children of this node. If the path for the specified key does not exist so far, it
     * is created now.
     * 
     * @param keyIt
     *        the iterator for the key of the new property
     * @param startNode
     *        the node to start the search with
     * 
     * @return the parent node for the add operation
     */
    protected Node fetchAddNode(final ConfigurationKey.KeyIterator keyIt, final Node startNode) {
        if (!keyIt.hasNext()) throw new IllegalArgumentException("Key must be defined!");

        return this.createAddPath(keyIt, this.findLastPathNode(keyIt, startNode));
    }

    /**
     * Helper method for fetching a list of all nodes that are addressed by the specified key.
     * 
     * @param key
     *        the key
     * 
     * @return a list with all affected nodes (never <b>null </b>)
     */
    protected List fetchNodeList(final String key) {
        List nodes = new LinkedList();
        this.findPropertyNodes(new ConfigurationKey(key).iterator(), this.getRoot(), nodes);
        return nodes;
    }

    /**
     * Finds the last existing node for an add operation. This method traverses the configuration
     * tree along the specified key. The last existing node on this path is returned.
     * 
     * @param keyIt
     *        the key iterator
     * @param node
     *        the actual node
     * 
     * @return the last existing node on the given path
     */
    protected Node findLastPathNode(final ConfigurationKey.KeyIterator keyIt, final Node node) {
        String keyPart = keyIt.nextKey(true);

        if (keyIt.hasNext()) {
            List list = node.getChildren(keyPart);
            int idx = (keyIt.hasIndex()) ? keyIt.getIndex() : list.size() - 1;
            if ((idx < 0) || (idx >= list.size()))
                return node;
            else
                return this.findLastPathNode(keyIt, (Node) list.get(idx));
        } else
            return node;
    }

    /**
     * Recursive helper method for fetching a property. This method processes all facets of a
     * configuration key, traverses the tree of properties and fetches the the nodes of all matching
     * properties.
     * 
     * @param keyPart
     *        the configuration key iterator
     * @param node
     *        the actual node
     * @param nodes
     *        here the found nodes are stored
     */
    protected void findPropertyNodes(final ConfigurationKey.KeyIterator keyPart, final Node node, final Collection nodes) {
        if (!keyPart.hasNext()) {
            nodes.add(node);
        } else {
            String key = keyPart.nextKey(true);
            List children = node.getChildren(key);
            if (keyPart.hasIndex()) {
                if ((keyPart.getIndex() < children.size()) && (keyPart.getIndex() >= 0)) {
                    this.findPropertyNodes((ConfigurationKey.KeyIterator) keyPart.clone(), (Node) children.get(keyPart.getIndex()),
                            nodes);
                }
            } else {
                for (Iterator it = children.iterator(); it.hasNext();) {
                    this.findPropertyNodes((ConfigurationKey.KeyIterator) keyPart.clone(), (Node) it.next(), nodes);
                }
            }
        }
    }

    /**
     * Checks if the specified node is defined.
     * 
     * @param node
     *        the node to be checked
     * 
     * @return a flag if this node is defined
     */
    protected boolean nodeDefined(final Node node) {
        DefinedVisitor visitor = new DefinedVisitor();
        node.visit(visitor, null);
        return visitor.isDefined();
    }

    /**
     * Removes the specified node from this configuration. This method ensures that parent nodes
     * that become undefined by this operation are also removed.
     * 
     * @param node
     *        the node to be removed
     */
    protected void removeNode(final Node node) {
        Node parent = node.getParent();
        if (parent != null) {
            parent.remove(node);
            if (!this.nodeDefined(parent)) {
                this.removeNode(parent);
            }
        }
    }

    /**
     * A data class for storing (hierarchical) property information. A property can have a value and
     * an arbitrary number of child properties.
     */
    public static class Node implements Serializable, Cloneable {

        /**
         * 
         */
        private static final long serialVersionUID = 2109560516800382530L;

        /** Stores a reference to this node's parent. */
        private Node parent;

        /** Stores the name of this node. */
        private String name;

        /** Stores the value of this node. */
        private Object value;

        /** Stores a reference to an object this node is associated with. */
        private Object reference;

        /** Stores the children of this node. */
        private LinkedHashMap children; // Explict type here or we

        // will get a findbugs error
        // because Map doesn't imply
        // Serializable

        /**
         * Creates a new instance of <code>Node</code>.
         */
        public Node() {
            this(null);
        }

        /**
         * Creates a new instance of <code>Node</code> and sets the name.
         * 
         * @param name
         *        the node's name
         */
        public Node(final String name) {
            this.setName(name);
        }

        /**
         * Creates a new instance of <code>Node</code> and sets the name and the value.
         * 
         * @param name
         *        the node's name
         * @param value
         *        the value
         */
        public Node(final String name, final Object value) {
            this.setName(name);
            this.setValue(value);
        }

        /**
         * Adds the specified child object to this node. Note that there can be multiple children
         * with the same name.
         * 
         * @param child
         *        the child to be added
         */
        public void addChild(final Node child) {
            if (this.children == null) {
                this.children = new LinkedHashMap();
            }

            List c = (List) this.children.get(child.getName());
            if (c == null) {
                c = new ArrayList();
                this.children.put(child.getName(), c);
            }

            c.add(child);
            child.setParent(this);
        }

        /**
         * Creates a copy of this object. This is not a deep copy, the children are not cloned.
         * 
         * @return a copy of this object
         */
        @Override
        public Object clone() {
            try {
                Node copy = (Node) super.clone();
                copy.children = null;
                return copy;
            } catch (CloneNotSupportedException cex) {
                return null; // should not happen
            }
        }

        /**
         * Returns a list with the child nodes of this node.
         * 
         * @return a list with the children (can be empty, but never <b>null </b>)
         */
        public List getChildren() {
            List result = new ArrayList();

            if (this.children != null) {
                for (Iterator it = this.children.values().iterator(); it.hasNext();) {
                    result.addAll((Collection) it.next());
                }
            }

            return result;
        }

        /**
         * Returns a list with this node's children with the given name.
         * 
         * @param sName
         *        the name of the children
         * 
         * @return a list with all chidren with this name; may be empty, but never <b>null </b>
         */
        public List getChildren(final String sName) {
            if ((sName == null) || (this.children == null)) return this.getChildren();

            List list = new ArrayList();
            List c = (List) this.children.get(sName);
            if (c != null) {
                list.addAll(c);
            }

            return list;
        }

        /**
         * Returns the child nodes map.
         * 
         * @return the child nodes map <b/>)
         */
        public Map getChildrenMap() {
            return this.children;
        }

        /**
         * Returns the name of this node.
         * 
         * @return the node name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Returns the parent of this node.
         * 
         * @return this node's parent (can be <b>null </b>)
         */
        public Node getParent() {
            return this.parent;
        }

        /**
         * Returns the reference object for this node.
         * 
         * @return the reference object
         */
        public Object getReference() {
            return this.reference;
        }

        /**
         * Returns the value of this node.
         * 
         * @return the node value (may be <b>null </b>)
         */
        public Object getValue() {
            return this.value;
        }

        /**
         * Returns a flag whether this node has child elements.
         * 
         * @return <b>true</b> if there a child node, <b>false</b> otherwise
         */
        public boolean hasChildren() {
            if (this.children != null) {
                for (Iterator it = this.children.values().iterator(); it.hasNext();) {
                    Collection nodes = (Collection) it.next();
                    if (!nodes.isEmpty()) return true;
                }
            }

            return false;
        }

        /**
         * Removes the specified child from this node.
         * 
         * @param child
         *        the child node to be removed
         * 
         * @return a flag if the child could be found
         */
        public boolean remove(final Node child) {
            if (this.children == null) return false;

            List c = (List) this.children.get(child.getName());
            if (c == null)
                return false;
            else {
                if (c.remove(child)) {
                    child.removeReference();
                    if (c.isEmpty()) {
                        this.children.remove(child.getName());
                    }
                    return true;
                } else
                    return false;
            }
        }

        /**
         * Removes all children with the given name.
         * 
         * @param sName
         *        the name of the children to be removed
         * 
         * @return a flag if children with this name existed
         */
        public boolean remove(final String sName) {
            if (this.children == null) return false;

            List nodes = (List) this.children.remove(sName);
            if (nodes != null) {
                this.nodesRemoved(nodes);
                return true;
            } else
                return false;
        }

        /**
         * Removes all children of this node.
         */
        public void removeChildren() {
            if (this.children != null) {
                Iterator it = this.children.values().iterator();
                this.children = null;
                while (it.hasNext()) {
                    this.nodesRemoved((Collection) it.next());
                }
            }
        }

        /**
         * Sets the name of this node.
         * 
         * @param string
         *        the node name
         */
        public void setName(final String string) {
            this.name = string;
        }

        /**
         * Sets the parent of this node.
         * 
         * @param node
         *        the parent node
         */
        public void setParent(final Node node) {
            this.parent = node;
        }

        /**
         * Sets the reference object for this node. A node can be associated with a reference object
         * whose concrete meaning is determined by a sub class of
         * <code>HierarchicalConfiguration</code>. In an XML configuration e.g. this reference
         * could be an element in a corresponding XML document. The reference is used by the
         * <code>BuilderVisitor</code> class when the configuration is stored.
         * 
         * @param ref
         *        the reference object
         */
        public void setReference(final Object ref) {
            this.reference = ref;
        }

        /**
         * Sets the value of this node.
         * 
         * @param object
         *        the node value
         */
        public void setValue(final Object object) {
            this.value = object;
        }

        /**
         * A generic method for traversing this node and all of its children. This method sends the
         * passed in visitor to this node and all of its children.
         * 
         * @param visitor
         *        the visitor
         * @param key
         *        here a configuration key with the name of the root node of the iteration can be
         *        passed; if this key is not <b>null </b>, the full pathes to the visited nodes are
         *        builded and passed to the visitor's <code>visit()</code> methods
         */
        public void visit(final NodeVisitor visitor, final ConfigurationKey key) {
            int length = 0;
            if (key != null) {
                length = key.length();
                if (this.getName() != null) {
                    key.append(StringUtils.replace(this.getName(), String.valueOf(ConfigurationKey.PROPERTY_DELIMITER),
                            ConfigurationKey.ESCAPED_DELIMITER));
                }
            }

            visitor.visitBeforeChildren(this, key);

            if (this.children != null) {
                for (Iterator it = this.children.values().iterator(); it.hasNext() && !visitor.terminate();) {
                    Collection col = (Collection) it.next();
                    for (Iterator it2 = col.iterator(); it2.hasNext() && !visitor.terminate();) {
                        ((Node) it2.next()).visit(visitor, key);
                    }
                }
            }

            if (key != null) {
                key.setLength(length);
            }
            visitor.visitAfterChildren(this, key);
        }

        /**
         * Deals with the reference when a node is removed. This method is called for each removed
         * child node. It can be overloaded in sub classes, for which the reference has a concrete
         * meaning and remove operations need some update actions. This default implementation is
         * empty.
         */
        protected void removeReference() {
        }

        /**
         * Helper method for calling <code>removeReference()</code> on a list of removed nodes.
         * Used by methods that can remove multiple child nodes in one step.
         * 
         * @param nodes
         *        collection with the nodes to be removed
         */
        private void nodesRemoved(final Collection nodes) {
            for (Iterator it = nodes.iterator(); it.hasNext();) {
                ((Node) it.next()).removeReference();
            }
        }
    }

    /**
     * <p>
     * Definition of a visitor class for traversing a node and all of its children.
     * </p>
     * <p>
     * This class defines the interface of a visitor for <code>Node</code> objects and provides a
     * default implementation. The method <code>visit()</code> of <code>Node</code> implements a
     * generic iteration algorithm based on the <em>Visitor</em> pattern. By providing different
     * implementations of visitors it is possible to collect different data during the iteration
     * process.
     * </p>
     */
    public static class NodeVisitor {

        /**
         * Returns a flag that indicates if iteration should be stopped. This method is called after
         * each visited node. It can be useful for visitors that search a specific node. If this
         * node is found, the whole process can be stopped. This base implementation always returns
         * <b>false </b>.
         * 
         * @return a flag if iteration should be stopped
         */
        public boolean terminate() {
            return false;
        }

        /**
         * Visits the specified node after its children have been processed. This gives a visitor
         * the opportunity of collecting additional data after the child nodes have been visited.
         * 
         * @param node
         *        the node to be visited
         * @param key
         *        the key of this node (may be <b>null </b>)
         */
        public void visitAfterChildren(final Node node, final ConfigurationKey key) {
        }

        /**
         * Visits the specified node. This method is called during iteration for each node before
         * its children have been visited.
         * 
         * @param node
         *        the actual node
         * @param key
         *        the key of this node (may be <b>null </b>)
         */
        public void visitBeforeChildren(final Node node, final ConfigurationKey key) {
        }
    }

    /**
     * A specialized visitor base class that can be used for storing the tree of configuration
     * nodes. The basic idea is that each node can be associated with a reference object. This
     * reference object has a concrete meaning in a derived class, e.g. an entry in a JNDI context
     * or an XML element. When the configuration tree is set up, the <code>load()</code> method is
     * responsible for setting the reference objects. When the configuration tree is later modified,
     * new nodes do not have a defined reference object. This visitor class processes all nodes and
     * finds the ones without a defined reference object. For those nodes the <code>insert()</code>
     * method is called, which must be defined in concrete sub classes. This method can perform all
     * steps to integrate the new node into the original structure.
     */
    protected abstract static class BuilderVisitor extends NodeVisitor {

        /**
         * Visits the specified node before its children have been traversed.
         * 
         * @param node
         *        the node to visit
         * @param key
         *        the current key
         */
        @Override
        public void visitBeforeChildren(final Node node, final ConfigurationKey key) {
            Iterator children = node.getChildren().iterator();
            Node sibling1 = null;
            Node nd = null;

            while (children.hasNext()) {
                // find the next new node
                do {
                    sibling1 = nd;
                    nd = (Node) children.next();
                } while ((nd.getReference() != null) && children.hasNext());

                if (nd.getReference() == null) {
                    // find all following new nodes
                    List newNodes = new ArrayList();
                    newNodes.add(nd);
                    while (children.hasNext()) {
                        nd = (Node) children.next();
                        if (nd.getReference() == null) {
                            newNodes.add(nd);
                        } else {
                            break;
                        }
                    }

                    // Insert all new nodes
                    Node sibling2 = (nd.getReference() == null) ? null : nd;
                    for (Iterator it = newNodes.iterator(); it.hasNext();) {
                        Node insertNode = (Node) it.next();
                        if (insertNode.getReference() == null) {
                            Object ref = this.insert(insertNode, node, sibling1, sibling2);
                            if (ref != null) {
                                insertNode.setReference(ref);
                            }
                            sibling1 = insertNode;
                        }
                    }
                }
            }
        }

        /**
         * Inserts a new node into the structure constructed by this builder. This method is called
         * for each node that has been added to the configuration tree after the configuration has
         * been loaded from its source. These new nodes have to be inserted into the original
         * structure. The passed in nodes define the position of the node to be inserted: its parent
         * and the siblings between to insert. The return value is interpreted as the new reference
         * of the affected <code>Node</code> object; if it is not <b>null </b>, it is passed to
         * the node's <code>setReference()</code> method.
         * 
         * @param newNode
         *        the node to be inserted
         * @param parent
         *        the parent node
         * @param sibling1
         *        the sibling after which the node is to be inserted; can be <b>null </b> if the new
         *        node is going to be the first child node
         * @param sibling2
         *        the sibling before which the node is to be inserted; can be <b>null </b> if the
         *        new node is going to be the last child node
         * 
         * @return the reference object for the node to be inserted
         */
        protected abstract Object insert(Node newNode, Node parent, Node sibling1, Node sibling2);
    }

    /**
     * A specialized visitor that is able to create a deep copy of a node hierarchy.
     */
    static class CloneVisitor extends NodeVisitor {

        /** A stack with the actual object to be copied. */
        private Stack copyStack;

        /** Stores the result of the clone process. */
        private Node result;

        /**
         * Creates a new instance of <code>CloneVisitor</code>.
         */
        public CloneVisitor() {
            this.copyStack = new Stack();
        }

        /**
         * Returns the result of the clone process. This is the root node of the cloned node
         * hierarchy.
         * 
         * @return the cloned root node
         */
        public Node getClone() {
            return this.result;
        }

        /**
         * Visits the specified node after its children have been processed.
         * 
         * @param node
         *        the node
         * @param key
         *        the key of this node
         */
        @Override
        public void visitAfterChildren(final Node node, final ConfigurationKey key) {
            Node copy = (Node) this.copyStack.pop();
            if (this.copyStack.isEmpty()) {
                this.result = copy;
            }
            // old fixed version dwz, there was a bug in 1.1. node should be on stack.
            // code from 1.2 seems ok.
            // if (copyStack.isEmpty()) {
            // result = node;
            // } else {
            // result = (Node) copyStack.pop();
            // }

        }

        /**
         * Visits and copies the specified node.
         * 
         * @param node
         *        the node
         * @param key
         *        the key of this node
         */
        @Override
        public void visitBeforeChildren(final Node node, final ConfigurationKey key) {
            Node copy = (Node) node.clone();

            if (!this.copyStack.isEmpty()) {
                ((Node) this.copyStack.peek()).addChild(copy);
            }

            this.copyStack.push(copy);
        }
    }

    /**
     * A specialized visitor that fills a list with keys that are defined in a node hierarchy.
     */
    static class DefinedKeysVisitor extends NodeVisitor {

        /** Stores the list to be filled. */
        private Set keyList;

        /** Stores a prefix for the keys. */
        private String prefix;

        /**
         * Default constructor.
         */
        public DefinedKeysVisitor() {
            this.keyList = new LinkedHashSet();
        }

        /**
         * Creates a new <code>DefinedKeysVisitor</code> instance and sets the prefix for the keys
         * to fetch.
         * 
         * @param prefix
         *        the prefix
         */
        public DefinedKeysVisitor(final String prefix) {
            this();
            this.prefix = prefix;
        }

        /**
         * Returns the list with all defined keys.
         * 
         * @return the list with the defined keys
         */
        public Set getKeyList() {
            return this.keyList;
        }

        /**
         * Visits the specified node. If this node has a value, its key is added to the internal
         * list.
         * 
         * @param node
         *        the node to be visited
         * @param key
         *        the key of this node
         */
        @Override
        public void visitBeforeChildren(final Node node, final ConfigurationKey key) {
            if ((node.getValue() != null) && (key != null)) {
                this.addKey(key);
            }
        }

        /**
         * Adds the specified key to the internal list.
         * 
         * @param key
         *        the key to add
         */
        protected void addKey(final ConfigurationKey key) {
            if (this.prefix == null) {
                this.keyList.add(key.toString());
            } else {
                StringBuffer buf = new StringBuffer(this.prefix);
                if (!key.isAttributeKey()) {
                    buf.append(ConfigurationKey.PROPERTY_DELIMITER);
                }
                buf.append(key);
                this.keyList.add(buf.toString());
            }
        }
    }

    /**
     * A specialized visitor that checks if a node is defined. &quot;Defined&quot; in this terms
     * means that the node or at least one of its sub nodes is associated with a value.
     */
    static class DefinedVisitor extends NodeVisitor {

        /** Stores the defined flag. */
        private boolean defined;

        /**
         * Returns the defined flag.
         * 
         * @return the defined flag
         */
        public boolean isDefined() {
            return this.defined;
        }

        /**
         * Checks if iteration should be stopped. This can be done if the first defined node is
         * found.
         * 
         * @return a flag if iteration should be stopped
         */
        @Override
        public boolean terminate() {
            return this.isDefined();
        }

        /**
         * Visits the node. Checks if a value is defined.
         * 
         * @param node
         *        the actual node
         * @param key
         *        the key of this node
         */
        @Override
        public void visitBeforeChildren(final Node node, final ConfigurationKey key) {
            this.defined = node.getValue() != null;
        }
    }
}
