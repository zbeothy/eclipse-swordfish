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
package org.eclipse.swordfish.policytrader.testing.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * Helper methods for XML debug output
 */
public class XPrinterBase {

    /**
     * new line charachter
     */
    private static final String LS = "\n";

    /**
     * the namespace URI for XML namespaces
     */
    private static final String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";

    /**
     * the namespace URI for XML namespaces
     */
    private static final String NS_URI_XML = "http://www.w3.org/XML/1998/namespace";

    /**
     * Non-recursively extract namespace attributes from a Document, DocumentFragment or Element
     * node into a given map. In case of a Document or DocumentFragment the root element node is
     * taken.
     * 
     * @param node
     *        Node of which attributes are scanned
     * @param map
     *        Map where to put the namespace attributes
     */
    protected static void extractNamespacePrefixAttributesIntoMap(final Node node, final Map map) {

        Node base = node;
        if (base instanceof Document) {
            base = ((Document) base).getDocumentElement();
        } else if (base instanceof DocumentFragment) {
            base = base.getFirstChild();
            while ((base != null) && (base.getNodeType() != Node.ELEMENT_NODE)) {
                base = base.getNextSibling();
            }
        }
        final NamedNodeMap m = base.getAttributes();
        if (null == m) return;
        final int l = m.getLength();
        Node n;
        for (int i = 0; i < l; i++) {
            n = m.item(i);
            if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
                final String name = n.getNodeName();
                if (name.startsWith("xmlns:")) {
                    final String key = name.substring("xmlns:".length());
                    final String value = n.getNodeValue();
                    map.put(key, value);
                }
            }
        }
    }

    /**
     * This method tries to figure out the encoding used to build this node.
     * 
     * @param node
     *        the node to examine
     * @return the encoding used in the XML declaration to build this document.
     */
    protected static String getEncoding(final Node node) {
        String encoding = null;
        recurseForEncoding(node.getOwnerDocument(), 0, encoding);
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

    /**
     * Return a string containing this node serialized as XML.
     */
    protected static String nodeToString(final Node node, final boolean omitXMLDecl, final boolean pretty) {

        StringWriter sw = new StringWriter();
        serializeAsXML(node, sw, omitXMLDecl, pretty);
        return sw.toString();
    }

    /**
     * this method traverses the DOM tree to print out each node charachteristic into a writer.
     * 
     * @param node
     *        the current node to be printed. This nodes childs are traversed.
     * @param startnode
     *        the root node.
     * @param out
     *        the printwriter to print the serialized information to.
     * @param pretty
     *        indicate if we do intending for the print out or not.
     * @param indent
     *        the number of intends while pretty printing.
     * @param encoder
     *        the char encoder to be used for the correct charachter encoding.
     */
    protected static void print(final Node node, final Node startnode, final PrintWriter out, final boolean pretty,
            final int indent, final XMLEncoder encoder) {

        NSStack namespaceStack = new NSStack();
        print(node, namespaceStack, node, out, pretty, 0, encoder);
    }

    /**
     * Serialize this node into the writer as XML.
     */
    protected static void serializeAsXML(final Node node, final Writer writer, final boolean omitXMLDecl, final boolean pretty) {

        PrintWriter out = new PrintWriter(writer);
        String encoding = getEncoding(node);
        XMLEncoder encoder = XMLEncoderFactory.createInstance(encoding);
        if (!omitXMLDecl) {
            out.print("<?xml version=\"1.0\" encoding=\"");
            out.print(encoder.getEncoding());
            out.println("\"?>");
        }
        print(node, node, out, pretty, 0, encoder);
        out.flush();
    }

    /**
     * Searches for the namespace URI of the given prefix in the given DOM range.
     * 
     * The namespace is not searched in parent of the "stopNode". This is usefull to get all the
     * needed namespaces when you need to ouput only a subtree of a DOM document.
     * 
     * @param prefix
     *        the prefix to find
     * @param e
     *        the starting node
     * @param stopNode
     *        null to search in all the document or a parent node where the search must stop.
     * @return null if no namespace is found, or the namespace URI.
     */
    private static String getNamespace(final String prefix, Node e, final Node stopNode) {

        while ((e != null) && (e.getNodeType() == Node.ELEMENT_NODE)) {
            Attr attr = null;
            if (prefix == null) {
                attr = ((Element) e).getAttributeNode("xmlns");
            } else {
                attr = ((Element) e).getAttributeNodeNS(NS_URI_XMLNS, prefix);
            }
            if (attr != null) return attr.getValue();
            if (e == stopNode) return null;
            e = e.getParentNode();
        }
        return null;
    }

    /**
     * normalizes a String using the given encoder.
     * 
     * @param s
     *        the String to be normalized.
     * @param encoder
     *        the encoder to use.
     * @return the normalized representation of the String.
     */
    private static String normalize(final String s, final XMLEncoder encoder) {

        return encoder.encode(s);
    }

    /**
     * this method traverses the DOM tree to print out each node charachteristic into a writer.
     * 
     * @param node
     *        the current node to be printed. This nodes childs are traversed.
     * @param namespaceStack
     *        namespace stack to be used in order to have correct namespace handling.
     * @param startnode
     *        the root node.
     * @param out
     *        the printwriter to print the serialized information to.
     * @param pretty
     *        indicate if we do intending for the print out or not.
     * @param indent
     *        the number of intends while pretty printing.
     * @param encoder
     *        the char encoder to be used for the correct charachter encoding.
     */
    private static void print(final Node node, final NSStack namespaceStack, final Node startnode, final PrintWriter out,
            final boolean pretty, final int indent, final XMLEncoder encoder) {

        if (node == null) return;

        boolean hasChildren = false;
        int type = node.getNodeType();

        switch (type) {
            case Node.DOCUMENT_NODE: {
                NodeList children = node.getChildNodes();

                if (children != null) {
                    int numChildren = children.getLength();

                    for (int i = 0; i < numChildren; i++) {
                        print(children.item(i), namespaceStack, startnode, out, pretty, indent, encoder);
                    }
                }
                break;
            }

            case Node.DOCUMENT_FRAGMENT_NODE: {
                NodeList children = node.getChildNodes();

                if (children != null) {
                    int numChildren = children.getLength();

                    for (int i = 0; i < numChildren; i++) {
                        print(children.item(i), namespaceStack, startnode, out, pretty, indent, encoder);
                    }
                }
                break;
            }

            case Node.ELEMENT_NODE: {
                namespaceStack.push();

                out.print('<' + node.getNodeName());

                String elPrefix = node.getPrefix();
                String elNamespaceURI = node.getNamespaceURI();

                if ((elPrefix != null) && (elNamespaceURI != null) && (elPrefix.length() > 0)) {
                    boolean prefixIsDeclared = false;

                    try {
                        String namespaceURI = namespaceStack.getNamespaceURI(elPrefix);

                        if (elNamespaceURI.equals(namespaceURI)) {
                            prefixIsDeclared = true;
                        }
                    } catch (IllegalArgumentException e) {
                    }

                    if (!prefixIsDeclared) {
                        printNamespaceDecl(node, elPrefix, namespaceStack, startnode, out);
                    }
                }

                NamedNodeMap attrs = node.getAttributes();
                int len = (attrs != null) ? attrs.getLength() : 0;

                for (int i = 0; i < len; i++) {
                    Attr attr = (Attr) attrs.item(i);

                    out.print(' ' + attr.getNodeName() + "=\"" + normalize(attr.getValue(), encoder) + '\"');

                    String attrPrefix = attr.getPrefix();
                    String attrNamespaceURI = attr.getNamespaceURI();

                    if ((attrPrefix != null) && (attrNamespaceURI != null) && (attrPrefix.length() > 0)) {
                        boolean prefixIsDeclared = false;

                        try {
                            String namespaceURI = namespaceStack.getNamespaceURI(attrPrefix);

                            if (attrNamespaceURI.equals(namespaceURI)) {
                                prefixIsDeclared = true;
                            }
                        } catch (IllegalArgumentException e) {
                        }

                        if (!prefixIsDeclared) {
                            printNamespaceDecl(attr, attrPrefix, namespaceStack, startnode, out);
                        }
                    }

                    // SOPSolutions: we need to have the attribute value namespaces also
                    String valuePrefix =
                            attr.getValue().substring(0, attr.getValue().indexOf(":") < 0 ? 0 : attr.getValue().indexOf(":"));
                    valuePrefix = valuePrefix.trim();
                    valuePrefix = valuePrefix.length() == 0 ? null : valuePrefix;

                    if (valuePrefix != null) {
                        boolean prefixIsDeclared = false;
                        String probableNS =
                                getNamespace(valuePrefix, attr.getOwnerElement(), attr.getOwnerElement().getOwnerDocument());
                        if (probableNS != null) {
                            // we assume that if this has been a name space inside
                            // the value than it has been declared
                            // previously. But if the NS is not defined we have no
                            // idea weather this prefix was intended
                            // to be a QName or if it is just simply a regular text
                            // charachter.
                            try {
                                String namespaceURI = namespaceStack.getNamespaceURI(valuePrefix);

                                if (probableNS.equals(namespaceURI)) {
                                    prefixIsDeclared = true;
                                }

                            } catch (IllegalArgumentException e) {
                            }

                            if (!prefixIsDeclared) {
                                printNamespaceDecl(attr, valuePrefix, namespaceStack, startnode, out);
                            }
                        }
                    }
                    // END of try
                }

                NodeList children = node.getChildNodes();

                if (children != null) {
                    int numChildren = children.getLength();

                    hasChildren = (numChildren > 0);

                    if (hasChildren) {
                        out.print('>');
                        if (pretty) {
                            out.print(LS);
                            for (int i = 0; i <= indent; i++)
                                out.print(' ');
                        }
                    }

                    for (int i = 0; i < numChildren; i++) {
                        print(children.item(i), namespaceStack, startnode, out, pretty, indent + 1, encoder);
                    }
                } else {
                    hasChildren = false;
                }

                if (!hasChildren) {
                    out.print("/>");
                    if (pretty) {
                        out.print(LS);
                        if (node.getNextSibling() != null) {
                            for (int i = 0; i < indent; i++)
                                out.print(' ');
                        }
                    }
                }

                namespaceStack.pop();
                break;
            }

            case Node.ENTITY_REFERENCE_NODE: {
                out.print('&');
                out.print(node.getNodeName());
                out.print(';');
                break;
            }

            case Node.CDATA_SECTION_NODE: {
                out.print("<![CDATA[");
                out.print(node.getNodeValue());
                out.print("]]>");
                break;
            }

            case Node.TEXT_NODE: {
                out.print(normalize(node.getNodeValue(), encoder));
                break;
            }

            case Node.COMMENT_NODE: {
                out.print("<!--");
                out.print(node.getNodeValue());
                out.print("-->");
                if (pretty) out.print(LS);
                break;
            }

            case Node.PROCESSING_INSTRUCTION_NODE: {
                out.print("<?");
                out.print(node.getNodeName());

                String data = node.getNodeValue();

                if ((data != null) && (data.length() > 0)) {
                    out.print(' ');
                    out.print(data);
                }

                out.println("?>");
                if (pretty) out.print(LS);
                break;
            }
        }

        if ((type == Node.ELEMENT_NODE) && (hasChildren == true)) {
            if (pretty) {
                if (node.getLastChild().getNodeType() != Node.ELEMENT_NODE) {
                    out.print(LS);
                }
                for (int i = 0; i < indent; i++)
                    out.print(' ');
            }
            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
            if (pretty) {
                out.print(LS);
                if (node.getNextSibling() != null) {
                    for (int i = 0; i < indent; i++)
                        out.print(' ');
                }
            }
            hasChildren = false;
        }
    }

    /**
     * print the namespace declaration of an element node
     */
    private static void printNamespaceDecl(final Element owner, final Node node, final String thePrefix,
            final NSStack namespaceStack, final Node startnode, final PrintWriter out) {

        String namespaceURI = node.getNamespaceURI();
        String prefix = thePrefix;

        if (namespaceURI == null) {
            namespaceURI = getNamespace(prefix, owner, owner.getOwnerDocument());
        }

        if (!(namespaceURI.equals(NS_URI_XMLNS) && prefix.equals("xmlns"))
                && !(namespaceURI.equals(NS_URI_XML) && prefix.equals("xml"))) {

            if (getNamespace(prefix, owner, startnode) == null) {
                out.print(" xmlns:" + prefix + "=\"" + namespaceURI + '\"');
            }
        } else {
            prefix = node.getLocalName();
            namespaceURI = node.getNodeValue();
        }

        namespaceStack.add(namespaceURI, prefix);
    }

    /**
     * pushes the namespace declaration of the given node into the given print writer.
     * 
     * @param node
     *        the node from which to find the namespace.
     * @param namespaceStack
     *        the NS stack to search through.
     * @param startnode
     *        the root node from where we started.
     * @param out
     *        the printwriter to push the value into.
     */
    private static void printNamespaceDecl(final Node node, final String thePrefix, final NSStack namespaceStack,
            final Node startnode, final PrintWriter out) {

        switch (node.getNodeType()) {
            case Node.ATTRIBUTE_NODE:
                printNamespaceDecl(((Attr) node).getOwnerElement(), node, thePrefix, namespaceStack, startnode, out);
                break;

            case Node.ELEMENT_NODE:
                printNamespaceDecl((Element) node, node, thePrefix, namespaceStack, startnode, out);
                break;
        }
    }

    // TODO why are the processing instructions gone?
    /**
     * visits all values in the node to find out the encoding.
     * 
     * @param node
     * @param level
     * @param encoding
     */
    private static void recurseForEncoding(final Node node, final int level, String encoding) {

        if (encoding != null) return;
        if (node == null) return;
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            // Get child node
            Node childNode = list.item(i);
            if (childNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                ProcessingInstruction pi = (ProcessingInstruction) childNode;
                if ("xml".equals(pi.getTarget()) && pi.getData().startsWith("encoding")) {
                    encoding = pi.getData().substring(pi.getData().indexOf("\"") + 1, pi.getData().lastIndexOf("\""));
                    return;
                }
            }
            recurseForEncoding(childNode, level + 1, encoding);
        }
    }

    /**
     * Unused constructor, protected for inheritance
     */
    protected XPrinterBase() {
        super();
    }
}
