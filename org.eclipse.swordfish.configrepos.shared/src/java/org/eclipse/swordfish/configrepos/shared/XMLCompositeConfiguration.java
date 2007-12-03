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

package org.eclipse.swordfish.configrepos.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.swordfish.configrepos.shared.HierarchicalConfiguration.Node;

/**
 * The Class XMLCompositeConfiguration.
 * 
 */
public class XMLCompositeConfiguration {

    /** The Constant NAME_ATTRIBUTE_NAME. */
    public static final String NAME_ATTRIBUTE_NAME = "sopcs:name";

    /** The Constant VALUE_ATTRIBUTE_NAME. */
    public static final String VALUE_ATTRIBUTE_NAME = "sopcs:value";

    /** The Constant FINAL_ATTRIBUTE_NAME. */
    public static final String FINAL_ATTRIBUTE_NAME = "sopcs:sbb_configuration_attribute_final";

    /** The Constant FIXED_ATTRIBUTE_NAME. */
    public static final String FIXED_ATTRIBUTE_NAME = "sopcs:sbb_configuration_attribute_fixed";

    /** The Constant DELETED_ATTRIBUTE_NAME. */
    public static final String DELETED_ATTRIBUTE_NAME = "sopcs:sbb_configuration_attribute_deleted";

    /** The Constant INDICATOR_VALUE_TRUE. */
    public static final String INDICATOR_VALUE_TRUE = "true";

    /** The Constant CHANGED_ATTRIBUTE_NAME. */
    public static final String CHANGED_ATTRIBUTE_NAME = "sopcs:sbb_configuration_attribute_changed";

    /** The Constant ADDED_ATTRIBUTE_NAME. */
    public static final String ADDED_ATTRIBUTE_NAME = "sopcs:sbb_configuration_attribute_added";

    /** The Constant NS_ATTRIBUTE_START. */
    private static final String NS_ATTRIBUTE_START = "[@xmlns";

    /** The Constant PREFIX_ATTRIBUTE_START. */
    private static final String PREFIX_ATTRIBUTE_START = "[@xmlns:";

    /** The Constant XSI_ATTRIBUTE_START. */
    private static final String XSI_ATTRIBUTE_START = "[@xsi:";

    /**
     * Compute delta.
     * 
     * @param node1
     *        the node1
     * @param node2
     *        the node2
     * @param permitDelete
     *        the permit delete
     * 
     * @return the node
     */
    public static Node computeDelta(final Node node1, final Node node2, final boolean permitDelete) {
        return computeDelta(node1, node2, permitDelete, false, false);
    }

    /**
     * Compute delta.
     * 
     * @param node1
     *        the node1
     * @param node2
     *        the node2
     * @param permitDelete
     *        the permit delete
     * @param node1Final
     *        the node1 final
     * @param node2Fixed
     *        the node2 fixed
     * 
     * @return the node
     */
    public static Node computeDelta(final Node node1, final Node node2, final boolean permitDelete, final boolean node1Final,
            final boolean node2Fixed) {
        Node delta = cloneHead(node2, node1);
        // node1Final |= hasIndicator(node1, FINAL_ATTRIBUTE_NAME);
        // - final handling done by fixedFinal check in ConfigurationStore
        final boolean isNode2Fixed = node2Fixed | hasIndicator(node2, FIXED_ATTRIBUTE_NAME);
        Set nameSet = collectNames(node1.getChildrenMap(), node2.getChildrenMap());
        List deleted = new ArrayList();
        List added = new ArrayList();
        Map matching = new HashMap();
        Iterator names = nameSet.iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            List node1Kids = removeAttributes(node1.getChildren(name));
            List node2Kids = removeAttributes(node2.getChildren(name));
            matchingNodes(node1Kids, node2Kids, deleted, added, matching);
        }
        Iterator entries = matching.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            List node1Kids = (List) entry.getKey();
            List node2Kids = (List) entry.getValue();
            if ((node1Kids.size() == 1) && (node2Kids.size() == 1)) {
                Node kid1 = (Node) node1Kids.get(0);
                Node kid2 = (Node) node2Kids.get(0);
                if (!isAttribute(kid2)
                // && !node1Final
                        // && !hasIndicator(kid1, FINAL_ATTRIBUTE_NAME)
                        // - final handling done by fixedFinal check in
                        // ConfigurationStore
                        && (isNode2Fixed || hasIndicator(kid2, FIXED_ATTRIBUTE_NAME) || !XMLConfiguration.equals(kid1, kid2))) {
                    delta.addChild(computeDelta(kid1, kid2, permitDelete, node1Final, isNode2Fixed));
                }
            } else {
                deleted.addAll(node1Kids);
                added.addAll(node2Kids);
            }
        }
        if (permitDelete) {
            Iterator delKids = filterDeleted(deleted).iterator();
            while (delKids.hasNext()) {
                Node delKid = (Node) delKids.next();
                Node newKid = cloneHead(delKid);
                addIndicator(newKid, DELETED_ATTRIBUTE_NAME);
                delta.addChild(newKid);
            }
        }
        Iterator addKids = added.iterator();
        while (addKids.hasNext()) {
            Node addKid = (Node) addKids.next();
            delta.addChild(XMLConfiguration.cloneNode(addKid));
        }
        return delta;
    }

    /**
     * Compute delta.
     * 
     * @param config1
     *        the config1
     * @param config2
     *        the config2
     * 
     * @return the XML configuration
     */
    public static XMLConfiguration computeDelta(final XMLConfiguration config1, final XMLConfiguration config2) {
        XMLConfiguration delta = new XMLConfiguration();
        Node node1 = XMLConfiguration.cloneNode(config1.getRoot());
        Node node2 = XMLConfiguration.cloneNode(config2.getRoot());
        removeFixedFinal(node1);
        removeFixedFinal(node2);
        delta.setRoot(computeDelta(node1, node2, true));
        return delta;
    }

    /**
     * Checks for indicator.
     * 
     * @param node
     *        the node
     * @param key
     *        the key
     * 
     * @return true, if successful
     */
    public static boolean hasIndicator(final Node node, final String key) {
        List indicators = node.getChildren(ConfigurationKey.constructAttributeKey(key));
        return indicators.size() > 0;
    }

    /**
     * Checks if is matching.
     * 
     * @param name
     *        the name
     * @param node
     *        the node
     * 
     * @return true, if is matching
     */
    public static boolean isMatching(final String name, final Node node) {
        String name2 = nameFromNode(node);
        if (((name == null) || "".equals(name)) && ((name2 == null) || "".equals(name2)))
            return true;
        else {
            if (((name == null) || "".equals(name)) || ((name2 == null) || "".equals(name2)))
                return false;
            else
                return name.equals(name2);
        }
    }

    /**
     * Merge configurations.
     * 
     * @param configs
     *        the configs
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurations(final List configs) throws ConfigurationException {
        return mergeConfigurations(configs, true, true);
    }

    /**
     * Merge configurations.
     * 
     * @param configs
     *        the configs
     * @param strictMerge
     *        the strict merge
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurations(final List configs, final boolean strictMerge) throws ConfigurationException {
        return mergeConfigurations(configs, strictMerge, true);
    }

    /**
     * Merge configurations.
     * 
     * @param configs
     *        the configs
     * @param strictMerge
     *        the strict merge
     * @param isDeep
     *        the is deep
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurations(final List configs, final boolean strictMerge, final boolean isDeep)
            throws ConfigurationException {
        XMLConfiguration result = new XMLConfiguration();
        List nodes = new ArrayList();
        Iterator configIt = configs.iterator();
        while (configIt.hasNext()) {
            Node node = ((XMLConfiguration) configIt.next()).getRoot();
            nodes.add(node);
        }
        result.setRoot(mergeNodes(nodes, strictMerge, isDeep, false));
        result = result.cloneConfiguration();
        result.rebuildDocument();
        return result;
    }

    /**
     * Merge configurations.
     * 
     * @param local
     *        the local
     * @param remote
     *        the remote
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurations(final XMLConfiguration local, final XMLConfiguration remote)
            throws ConfigurationException {
        return mergeConfigurations(local, remote, true, true);
    }

    /**
     * Merge configurations.
     * 
     * @param local
     *        the local
     * @param remote
     *        the remote
     * @param strictMerge
     *        the strict merge
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurations(final XMLConfiguration local, final XMLConfiguration remote,
            final boolean strictMerge) throws ConfigurationException {
        return mergeConfigurations(local, remote, strictMerge, true);
    }

    /**
     * Merge configurations.
     * 
     * @param local
     *        the local
     * @param remote
     *        the remote
     * @param strictMerge
     *        the strict merge
     * @param isDeep
     *        the is deep
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurations(final XMLConfiguration local, final XMLConfiguration remote,
            final boolean strictMerge, final boolean isDeep) throws ConfigurationException {
        XMLConfiguration result = new XMLConfiguration();
        result.setRoot(mergeNodes(local.getRoot(), remote.getRoot(), strictMerge, isDeep, true, false));
        result = result.cloneConfiguration();
        result.rebuildDocument();
        return result;
    }

    /**
     * Merge configurations and namespaces.
     * 
     * @param configs
     *        the configs
     * @param strictMerge
     *        the strict merge
     * @param isDeep
     *        the is deep
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurationsAndNamespaces(final List configs, final boolean strictMerge,
            final boolean isDeep) throws ConfigurationException {
        XMLConfiguration result = new XMLConfiguration();
        List nodes = new ArrayList();
        Iterator configIt = configs.iterator();
        while (configIt.hasNext()) {
            Node node = ((XMLConfiguration) configIt.next()).getRoot();
            nodes.add(node);
        }
        result.setRoot(mergeNodes(nodes, strictMerge, isDeep, true));
        return XMLConfiguration.configFromBytes(result.toBytes());
    }

    /**
     * Merge configurations and namespaces.
     * 
     * @param local
     *        the local
     * @param remote
     *        the remote
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurationsAndNamespaces(final XMLConfiguration local, final XMLConfiguration remote)
            throws ConfigurationException {
        return mergeConfigurationsAndNamespaces(local, remote, true, true);
    }

    /**
     * Merge configurations and namespaces.
     * 
     * @param local
     *        the local
     * @param remote
     *        the remote
     * @param strictMerge
     *        the strict merge
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurationsAndNamespaces(final XMLConfiguration local, final XMLConfiguration remote,
            final boolean strictMerge) throws ConfigurationException {
        return mergeConfigurationsAndNamespaces(local, remote, strictMerge, true);
    }

    /**
     * Merge configurations and namespaces.
     * 
     * @param local
     *        the local
     * @param remote
     *        the remote
     * @param strictMerge
     *        the strict merge
     * @param isDeep
     *        the is deep
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration mergeConfigurationsAndNamespaces(final XMLConfiguration local, final XMLConfiguration remote,
            final boolean strictMerge, final boolean isDeep) throws ConfigurationException {
        XMLConfiguration result = new XMLConfiguration();
        result.setRoot(mergeNodes(local.getRoot(), remote.getRoot(), strictMerge, isDeep, true, true));
        return XMLConfiguration.configFromBytes(result.toBytes());
    }

    /**
     * Name from node.
     * 
     * @param node
     *        the node
     * 
     * @return the string
     */
    public static String nameFromNode(final Node node) {
        String attrKey = ConfigurationKey.constructAttributeKey(NAME_ATTRIBUTE_NAME);
        List values = node.getChildren(attrKey);
        if (values.isEmpty())
            return null;
        else
            return (String) ((Node) values.get(0)).getValue();
    }

    /**
     * Removes the all indicators.
     * 
     * @param node
     *        the node
     */
    public static void removeAllIndicators(final Node node) {
        removeAllIndicatorsAtNode(node);
        Iterator kids = node.getChildren().iterator();
        while (kids.hasNext()) {
            removeAllIndicators((Node) kids.next());
        }
    }

    /**
     * Removes the attribute.
     * 
     * @param node
     *        the node
     * @param name
     *        the name
     */
    public static void removeAttribute(final Node node, final String name) {
        removeAttributeAtNode(node, name);
        Iterator kids = node.getChildren().iterator();
        while (kids.hasNext()) {
            removeAttribute((Node) kids.next(), name);
        }
    }

    /**
     * Removes the fixed final.
     * 
     * @param node
     *        the node
     */
    public static void removeFixedFinal(final Node node) {
        removeFixedFinalAtNode(node);
        Iterator kids = node.getChildren().iterator();
        while (kids.hasNext()) {
            removeFixedFinal((Node) kids.next());
        }
    }

    /**
     * Sets the attribute value.
     * 
     * @param node
     *        the node
     * @param key
     *        the key
     * @param value
     *        the value
     */
    public static void setAttributeValue(final Node node, final String key, final String value) {
        String attrName = ConfigurationKey.constructAttributeKey(key);
        node.remove(attrName);
        Node attr = new Node(attrName);
        attr.setValue(value);
        node.addChild(attr);
    }

    /**
     * Value from attribute.
     * 
     * @param node
     *        the node
     * @param key
     *        the key
     * 
     * @return the string
     */
    public static String valueFromAttribute(final Node node, final String key) {
        String attrKey = ConfigurationKey.constructAttributeKey(key);
        List values = node.getChildren(attrKey);
        if (values.isEmpty())
            return null;
        else
            return (String) ((Node) values.get(0)).getValue();
    }

    /**
     * Adds the indicator.
     * 
     * @param node
     *        the node
     * @param key
     *        the key
     */
    static void addIndicator(final Node node, final String key) {
        if (!isAttribute(node)) {
            removeAllIndicatorsAtNode(node);
            Node indicator = new Node(ConfigurationKey.constructAttributeKey(key));
            indicator.setValue(INDICATOR_VALUE_TRUE);
            node.addChild(indicator);
        }
    }

    /**
     * Filter deleted.
     * 
     * @param nodeList
     *        the node list
     * 
     * @return the list
     */
    static List filterDeleted(final List nodeList) {
        Set nodeSet = new LinkedHashSet();
        List filtered = new ArrayList();
        Iterator nodes = nodeList.iterator();
        while (nodes.hasNext()) {
            Node node = (Node) nodes.next();
            if (!nodeSet.contains(node)) {
                filtered.add(node);
                Iterator matchingNodes = matchingNodesCheckElem(node, nodeList).iterator();
                while (matchingNodes.hasNext()) {
                    nodeSet.add(matchingNodes.next());
                }
            }
        }
        return filtered;
    }

    /**
     * Value from node.
     * 
     * @param node
     *        the node
     * @param key
     *        the key
     * 
     * @return the string
     */
    static String valueFromNode(final Node node, final String key) {
        String value = valueFromAttribute(node, key);
        if (value != null)
            return value;
        else {
            List values = node.getChildren(key);
            if ((values == null) || (values.size() == 0))
                return null;
            else {
                Node kid = (Node) values.get(0);
                value = (String) kid.getValue();
                if (value != null)
                    return value;
                else
                    return valueFromAttribute(kid, VALUE_ATTRIBUTE_NAME);
            }
        }
    }

    /**
     * Apply delta.
     * 
     * @param node
     *        the node
     * @param delta
     *        the delta
     * @param addMeta
     *        the add meta
     * 
     * @return the node
     */
    private static Node applyDelta(final Node node, final Node delta, final boolean addMeta) {
        Node result = cloneHead(delta);
        Set nameSet = collectNames(node.getChildrenMap(), delta.getChildrenMap());
        List unchanged = new ArrayList();
        List added = new ArrayList();
        Map matching = new HashMap();
        Iterator names = nameSet.iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            List nodeKids = removeAttributes(node.getChildren(name));
            List deltaKids = removeAttributes(delta.getChildren(name));
            matchingNodes(nodeKids, deltaKids, unchanged, added, matching);
        }
        Iterator entries = matching.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            List nodeKids = (List) entry.getKey();
            List deltaKids = (List) entry.getValue();
            if ((nodeKids.size() == 1) && (deltaKids.size() == 1)) {
                Node nodeKid = (Node) nodeKids.get(0);
                Node deltaKid = (Node) deltaKids.get(0);
                if (!hasIndicator(deltaKid, DELETED_ATTRIBUTE_NAME)) { // not
                    // deleted
                    Node changed = applyDelta(nodeKid, deltaKid, addMeta); // recursive
                    // call
                    if (addMeta) {
                        addIndicator(changed, CHANGED_ATTRIBUTE_NAME);
                    }
                    result.addChild(changed);
                }
            } else {
                Iterator deltas = deltaKids.iterator();
                while (deltas.hasNext()) {
                    Node deltaKid = (Node) deltas.next();
                    if (!hasIndicator(deltaKid, DELETED_ATTRIBUTE_NAME)) { // not
                        // deleted
                        if (addMeta) {
                            addIndicator(deltaKid, ADDED_ATTRIBUTE_NAME);
                        }
                        added.add(deltaKid); // add delta
                    }
                }
            }
        }
        Iterator unchangedKids = unchanged.iterator();
        while (unchangedKids.hasNext()) {
            Node unchangedKid = (Node) unchangedKids.next();
            if (!hasIndicator(unchangedKid, DELETED_ATTRIBUTE_NAME)) { // not
                // deleted
                result.addChild(unchangedKid); // add unchanged
            }
        }
        Iterator addKids = added.iterator();
        while (addKids.hasNext()) {
            Node addKid = (Node) addKids.next();
            if (!hasIndicator(addKid, DELETED_ATTRIBUTE_NAME)) { // not
                // deleted
                if (addMeta) {
                    addIndicator(addKid, ADDED_ATTRIBUTE_NAME);
                }
                result.addChild(addKid); // add delta
            }
        }
        return result;
    }

    /**
     * Clone head.
     * 
     * @param node
     *        the node
     * 
     * @return the node
     */
    private static Node cloneHead(final Node node) {
        Node result = new Node(node.getName());
        result.setValue(node.getValue());
        result.setParent(node.getParent());
        Iterator kids = node.getChildren().iterator();
        while (kids.hasNext()) {
            Node kid = (Node) kids.next();
            String name = kid.getName();
            if ((name != null) && ConfigurationKey.isAttributeKey(name)) {
                result.addChild(kid);
            }
        }
        return result;
    }

    /**
     * Clone head.
     * 
     * @param node
     *        the node
     * @param node2
     *        the node2
     * 
     * @return the node
     */
    private static Node cloneHead(final Node node, final Node node2) {
        Node result = new Node(node.getName());
        result.setValue(node.getValue());
        result.setParent(node.getParent());
        Iterator kids = node.getChildren().iterator();
        while (kids.hasNext()) {
            Node kid = (Node) kids.next();
            String name = kid.getName();
            if ((name != null) && ConfigurationKey.isAttributeKey(name)) {
                result.addChild(kid);
            }
        }
        kids = node2.getChildren().iterator();
        while (kids.hasNext()) {
            Node kid = (Node) kids.next();
            String name = kid.getName();
            if ((name != null) && isPrefixAttribute(kid) && (result.getChildren(name).size() == 0)) {
                result.addChild(kid);
            }
        }
        return result;
    }

    /**
     * Collect names.
     * 
     * @param kids1
     *        the kids1
     * @param kids2
     *        the kids2
     * 
     * @return the set
     */
    private static Set collectNames(final Map kids1, final Map kids2) {
        Set result;
        if ((kids1 != null) && (kids2 != null)) {
            result = new LinkedHashSet(kids1.keySet());
            result.addAll(kids2.keySet());
        } else if (kids1 != null) {
            result = kids1.keySet();
        } else if (kids2 != null) {
            result = kids2.keySet();
            ;
        } else {
            result = new LinkedHashSet();
        }
        return result;
    }

    /**
     * Contains leaf node.
     * 
     * @param nodes
     *        the nodes
     * 
     * @return true, if successful
     */
    private static boolean containsLeafNode(final List nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            Node node = (Node) nodes.get(i);
            if (!(isAttribute(node) && ConfigurationKey.constructAttributeKey(NAME_ATTRIBUTE_NAME).equals(node.getName()))) {
                List kids = node.getChildren();
                boolean isLeafNode = true;
                for (int j = 0; j < kids.size(); j++) {
                    Node kid = (Node) kids.get(j);
                    String name = kid.getName();
                    if ((name == null) || (!ConfigurationKey.isAttributeKey(name)) || name.startsWith(XSI_ATTRIBUTE_START)
                            || name.startsWith(NS_ATTRIBUTE_START)) {
                        isLeafNode = false;
                        break;
                    }
                }
                if (isLeafNode) return true;
            }
        }
        return false;
    }

    /**
     * Checks if is attribute.
     * 
     * @param node
     *        the node
     * 
     * @return true, if is attribute
     */
    private static boolean isAttribute(final Node node) {
        return ConfigurationKey.isAttributeKey(node.getName());
    }

    /**
     * Checks if is prefix attribute.
     * 
     * @param node
     *        the node
     * 
     * @return true, if is prefix attribute
     */
    private static boolean isPrefixAttribute(final Node node) {
        return ConfigurationKey.isAttributeKey(node.getName()) && node.getName().startsWith(PREFIX_ATTRIBUTE_START);
    }

    /**
     * Matching nodes.
     * 
     * @param nodeList
     *        the node list
     * @param deltaList
     *        the delta list
     * @param deleted
     *        the deleted
     * @param added
     *        the added
     * @param matching
     *        the matching
     */
    private static void matchingNodes(final List nodeList, final List deltaList, final List deleted, final List added,
            final Map matching) {
        if (nodeList.size() == 0) {
            added.addAll(deltaList);
            return;
        }
        if (deltaList.size() == 0) {
            deleted.addAll(nodeList);
            return;
        }
        Map nodeMap = new HashMap();
        Map deltaMap = new HashMap();
        Iterator nodes = nodeList.iterator();
        while (nodes.hasNext()) {
            Node node = (Node) nodes.next();
            if (!nodeMap.containsKey(node)) {
                List matchingNodeList = matchingNodes(node, nodeList);
                List matchingDeltaList = matchingNodes(node, deltaList);
                Iterator matchingNodes = matchingNodeList.iterator();
                while (matchingNodes.hasNext()) {
                    Node match = (Node) matchingNodes.next();
                    nodeMap.put(match, matchingNodeList);
                    if (matchingDeltaList.size() > 0) {
                        deltaMap.put(match, matchingDeltaList);
                    }
                }
                Iterator matchingDeltas = matchingDeltaList.iterator();
                while (matchingDeltas.hasNext()) {
                    Node match = (Node) matchingDeltas.next();
                    nodeMap.put(match, matchingNodeList);
                }
            }
        }
        nodes = nodeList.iterator();
        while (nodes.hasNext()) {
            Node node = (Node) nodes.next();
            if (deltaMap.containsKey(node)) {
                matching.put(nodeMap.get(node), deltaMap.get(node));
            } else {
                deleted.add(node);
            }
        }
        Iterator deltas = deltaList.iterator();
        while (deltas.hasNext()) {
            Node delta = (Node) deltas.next();
            if (!nodeMap.containsKey(delta)) {
                added.add(delta);
            }
        }
    }

    /**
     * Matching nodes.
     * 
     * @param node
     *        the node
     * @param nodeList
     *        the node list
     * 
     * @return the list
     */
    private static List matchingNodes(final Node node, final List nodeList) {
        List result = new ArrayList();
        String name = nameFromNode(node);
        Iterator nodes = nodeList.iterator();
        while (nodes.hasNext()) {
            Node node2 = (Node) nodes.next();
            if ((node == node2) || isMatching(name, node2)) {
                result.add(node2);
            }
        }
        return result;
    }

    /**
     * Matching nodes check elem.
     * 
     * @param node
     *        the node
     * @param nodeList
     *        the node list
     * 
     * @return the list
     */
    private static List matchingNodesCheckElem(final Node node, final List nodeList) {
        List result = new ArrayList();
        String elem = node.getName();
        String name = nameFromNode(node);
        Iterator nodes = nodeList.iterator();
        while (nodes.hasNext()) {
            Node node2 = (Node) nodes.next();
            if (elem.equals(node2.getName()) && isMatching(name, node2)) {
                result.add(node2);
            }
        }
        return result;
    }

    /**
     * Merge nodes.
     * 
     * @param nodes
     *        the nodes
     * @param strictMerge
     *        the strict merge
     * @param isDeep
     *        the is deep
     * @param mergeNamespaces
     *        the merge namespaces
     * 
     * @return the node
     * 
     * @throws ConfigurationException
     */
    private static Node mergeNodes(final List nodes, final boolean strictMerge, final boolean isDeep, final boolean mergeNamespaces)
            throws ConfigurationException {
        if (nodes.size() == 0) throw new ConfigurationException("merge of empty configuration list is not permitted");
        Node result = null;
        Iterator nodeIt = nodes.iterator();
        boolean isFirst = true;
        while (nodeIt.hasNext()) {
            Node node = (Node) nodeIt.next();
            if (isFirst) {
                isFirst = false;
                result = node;
            } else {
                result = mergeNodes(result, node, strictMerge, isDeep, true, mergeNamespaces);
            }
        }
        return result;
    }

    /**
     * Merge nodes.
     * 
     * @param localNode
     *        the local node
     * @param remoteNode
     *        the remote node
     * @param strictMerge
     *        the strict merge
     * @param isDeep
     *        the is deep
     * @param isRoot
     *        the is root
     * @param mergeNamespaces
     *        the merge namespaces
     * 
     * @return the node
     * 
     * @throws ConfigurationException
     */
    private static Node mergeNodes(final Node localNode, final Node remoteNode, final boolean strictMerge, final boolean isDeep,
            final boolean isRoot, final boolean mergeNamespaces) throws ConfigurationException {
        Node result = new Node(remoteNode.getName());
        result.setValue(remoteNode.getValue());
        result.setParent(remoteNode.getParent());
        Set nameSet = collectNames(localNode.getChildrenMap(), remoteNode.getChildrenMap());
        List local = new ArrayList();
        List remote = new ArrayList();
        List nsAttributes = new ArrayList();
        Map matching = new HashMap();
        Iterator names = nameSet.iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            List localKids = localNode.getChildren(name);
            List remoteKids = remoteNode.getChildren(name);
            if (/* name.startsWith(SOPCS_ATTRIBUTE_START) || */
            (!name.startsWith(NS_ATTRIBUTE_START) && !name.startsWith(XSI_ATTRIBUTE_START))) {
                matchingNodes(localKids, remoteKids, local, remote, matching);
            } else {
                if (localKids.size() > 0) {
                    nsAttributes.add(localKids.get(0));
                } else if (remoteKids.size() > 0) {
                    nsAttributes.add(remoteKids.get(0));
                }
            }
        }
        Iterator localKids = local.iterator(); // add local only first
        while (localKids.hasNext()) {
            Node localKid = (Node) localKids.next();
            result.addChild(localKid);
        }
        Iterator entries = matching.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            List localNodeKids = (List) entry.getKey();

            if (!isRoot && strictMerge && containsLeafNode(localNodeKids)) {
                // Throw an exception with all identified clashes.

                StringBuffer clash = new StringBuffer();
                for (int i = 0; i < localNodeKids.size(); i++) {
                    Node localNodeKid = (Node) localNodeKids.get(i);
                    clash.append(localNodeKid.getName());
                    String name = nameFromNode(localNodeKid);
                    if (name != null) {
                        clash.append(" name=\"" + name + "\"");
                    }
                    if (i < localNodeKids.size() - 1) {
                        clash.append(", ");
                    }
                }
                throw new ConfigurationException("Clash between local and remote configuration at node " + localNode.getName()
                        + " properties: " + clash.toString());
            } else {
                // merge nodes
                List remoteNodeKids = (List) entry.getValue();
                if ((localNodeKids.size() == 1) && (remoteNodeKids.size() == 1)) {
                    Node localNodeKid = (Node) localNodeKids.get(0);
                    Node remoteNodeKid = (Node) remoteNodeKids.get(0);
                    Node changed = isDeep ? mergeNodes(localNodeKid, remoteNodeKid, strictMerge, isDeep, false, mergeNamespaces) : // recursive
                            // call
                            remoteNodeKid; // remote overwrites local
                    result.addChild(changed);
                } else {
                    // remote.addAll(localNodeKids);
                    remote.addAll(remoteNodeKids); // remote overwrites local
                }
            }
        }
        Iterator remoteKids = remote.iterator();
        while (remoteKids.hasNext()) {
            Node remoteKid = (Node) remoteKids.next();
            result.addChild(remoteKid);
        }
        if (mergeNamespaces) {
            Iterator nsKids = nsAttributes.iterator();
            while (nsKids.hasNext()) {
                Node nsKid = (Node) nsKids.next();
                result.addChild(nsKid);
            }
        }
        return result;
    }

    /**
     * Removes the all indicators at node.
     * 
     * @param node
     *        the node
     */
    private static void removeAllIndicatorsAtNode(final Node node) {
        removeAttributeAtNode(node, DELETED_ATTRIBUTE_NAME);
        removeAttributeAtNode(node, ADDED_ATTRIBUTE_NAME);
        removeAttributeAtNode(node, CHANGED_ATTRIBUTE_NAME);
    }

    /**
     * Removes the attribute at node.
     * 
     * @param node
     *        the node
     * @param key
     *        the key
     */
    private static void removeAttributeAtNode(final Node node, final String key) {
        node.remove(ConfigurationKey.constructAttributeKey(key));
    }

    /**
     * Removes the attributes.
     * 
     * @param nodeList
     *        the node list
     * 
     * @return the list
     */
    private static List removeAttributes(final List nodeList) {
        List result = new ArrayList();
        Iterator nodes = nodeList.iterator();
        while (nodes.hasNext()) {
            Node node = (Node) nodes.next();
            if (!ConfigurationKey.isAttributeKey(node.getName())) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Removes the fixed final at node.
     * 
     * @param node
     *        the node
     */
    private static void removeFixedFinalAtNode(final Node node) {
        removeAttributeAtNode(node, FINAL_ATTRIBUTE_NAME);
        removeAttributeAtNode(node, FIXED_ATTRIBUTE_NAME);
        removeAllIndicatorsAtNode(node);
    }

    /** The Logger for this class. */
    private Logger log = Logger.getLogger(XMLCompositeConfiguration.class.getName());

    /** Configuration that holds the initial configuration defaults. */
    private XMLConfiguration initialConfiguration;

    /** List holding all the configuration deltas. */
    private List deltaList = new ArrayList();

    /** Configuration that holds the result after applying all deltas. */
    private XMLConfiguration finalConfiguration;

    /**
     * Configuration that holds the result after applying all deltas including meta flags.
     */
    private XMLConfiguration finalConfigurationMeta;

    /**
     * Configuration that holds the result after applying all but the last deltas.
     */
    private XMLConfiguration parentConfiguration;

    /**
     * Configuration that holds last delta - difference between parentConfiguration and
     * finalConfiguration / finalConfigurationMeta.
     */
    private XMLConfiguration lastDelta;

    /**
     * Creates an CompositeConfiguration object by applying a list of delta configurations to an
     * initial configuration. The last delta is applied both with and without generating meta
     * attributes indicating the changes.
     * 
     * @param initConfiguration
     *        the init configuration
     * @param deltas
     *        the deltas
     * @param log
     *        the log
     */
    public XMLCompositeConfiguration(final XMLConfiguration initConfiguration, final List deltas, final Logger log) {
        this.log = log;
        this.initialConfiguration = initConfiguration.cloneConfiguration();
        this.deltaList = deltas;
        this.parentConfiguration = new XMLConfiguration();
        this.finalConfiguration = initConfiguration.cloneConfiguration();
        this.finalConfigurationMeta = initConfiguration.cloneConfiguration();
        Node root = this.initialConfiguration.getRoot();
        Iterator deltaIt = this.deltaList.iterator();
        while (deltaIt.hasNext()) {
            XMLConfiguration delta = (XMLConfiguration) deltaIt.next();
            if (deltaIt.hasNext()) {
                root = applyDelta(root, XMLConfiguration.cloneNode(delta.getRoot()), false);
            } else {
                this.lastDelta = delta;
                this.parentConfiguration.setRoot(XMLConfiguration.cloneNode(root));
                root =
                        applyDelta(XMLConfiguration.cloneNode(this.parentConfiguration.getRoot()), XMLConfiguration.cloneNode(delta
                            .getRoot()), false);
                this.finalConfiguration = new XMLConfiguration();
                this.finalConfiguration.setRoot(root);
                root =
                        applyDelta(XMLConfiguration.cloneNode(this.parentConfiguration.getRoot()), XMLConfiguration.cloneNode(delta
                            .getRoot()), true);
                this.finalConfigurationMeta = new XMLConfiguration();
                this.finalConfigurationMeta.setRoot(root);
            }
        }
    }

    /**
     * Creates an empty CompositeConfiguration object.
     * 
     * @param initialConfiguration
     *        the initial configuration
     * @param log
     *        the log
     */
    public XMLCompositeConfiguration(final XMLConfiguration initialConfiguration, final Logger log) {
        this.log = log;
        this.initialConfiguration = initialConfiguration.cloneConfiguration();
        this.deltaList.clear();
        this.lastDelta = null;
        this.finalConfiguration = this.initialConfiguration;
        this.parentConfiguration = this.initialConfiguration;
        this.finalConfigurationMeta = this.initialConfiguration;
    }

    /**
     * Return the delta configuration at the specified index.
     * 
     * @param index
     *        The index of the delta configuration to retrieve
     * 
     * @return the delta
     */
    public XMLConfiguration getDelta(final int index) {
        return (XMLConfiguration) this.deltaList.get(index);
    }

    /**
     * Retrieves the result configuration by applying all deltas to the initial configuration.
     * 
     * @param addMeta
     *        the add meta
     * 
     * @return the final configuration
     * 
     * @result the computed result configuration
     */
    public XMLConfiguration getFinalConfiguration(final boolean addMeta) {
        if (addMeta)
            return this.finalConfigurationMeta;
        else
            return this.finalConfiguration;
    }

    /**
     * Retrieves the last delta applied.
     * 
     * @return the last delta
     * 
     * @result the last delta applied
     */
    public XMLConfiguration getLastDelta() {
        return this.lastDelta != null ? this.lastDelta : new XMLConfiguration();
    }

    /**
     * Return the number of configurations.
     * 
     * @return the number of configuration
     */
    public int getNumberOfDeltas() {
        return this.deltaList.size();
    }

    /**
     * Retrieves configuration before application of the last delta.
     * 
     * @return the parent configuration
     * 
     * @result configuration before application of the last delta
     */
    public XMLConfiguration getParentConfiguration() {
        return this.parentConfiguration != null ? this.parentConfiguration : new XMLConfiguration();
    }

    /**
     * Set new result configuration. Adds the new computed delta.
     * 
     * @param finalConfig
     *        the final config
     * @param permitDelete
     *        the permit delete
     * 
     * @return the XML configuration
     * 
     * @result the new computed delta
     */
    public XMLConfiguration setFinalConfiguration(final XMLConfiguration finalConfig, final boolean permitDelete) {
        this.parentConfiguration = this.finalConfiguration;
        this.finalConfiguration = finalConfig.cloneConfiguration();
        removeAllIndicators(this.finalConfiguration.getRoot());
        Node deltaRoot = computeDelta(this.parentConfiguration.getRoot(), this.finalConfiguration.getRoot(), permitDelete);
        this.lastDelta = new XMLConfiguration();
        this.lastDelta.setRoot(deltaRoot);
        this.deltaList.add(this.lastDelta);
        Node root = applyDelta(this.parentConfiguration.getRoot(), XMLConfiguration.cloneNode(deltaRoot), false);
        this.finalConfiguration.setRoot(root);
        root = applyDelta(this.parentConfiguration.getRoot(), XMLConfiguration.cloneNode(deltaRoot), true);
        this.finalConfigurationMeta.setRoot(root);
        this.finalConfigurationMeta = new XMLConfiguration();
        this.finalConfigurationMeta.setRoot(root);
        this.log.finest("parent:");
        this.log.finest(this.parentConfiguration.toString());
        this.log.finest("delta:");
        this.log.finest(this.lastDelta.toString());
        this.log.finest("delta appied:");
        this.log.finest(this.finalConfiguration.toString());
        this.log.finest("delta appied meta:");
        this.log.finest(this.finalConfigurationMeta.toString());
        return this.lastDelta;
    }
}
