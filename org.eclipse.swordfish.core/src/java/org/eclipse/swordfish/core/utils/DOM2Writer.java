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
package org.eclipse.swordfish.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * This utility class is used to serialize a DOM object into a writer, specially into a string.
 * 
 */
public class DOM2Writer {

    /** new line charachter. */
    static private final String LS = "\n";

    /** the namespace URI for XML namespaces. */
    static private final String NS_URI_XMLNS = "http://www.w3.org/2000/xmlns/";

    /** the namespace URI for XML namespaces. */
    static private final String NS_URI_XML = "http://www.w3.org/XML/1998/namespace";

    /**
     * Return a string containing this node serialized as XML.
     * 
     * @param node
     *        the node
     * 
     * @return the string
     */
    public static String nodeToPrettyString(final Node node) {
        StringWriter sw = new StringWriter();

        serializeAsXML(node, sw, true, true);

        return sw.toString();
    }

    /**
     * Return a string containing this node serialized as XML.
     * 
     * @param node
     *        the node
     * @param omitXMLDecl
     *        the omit XML decl
     * 
     * @return the string
     */
    public static String nodeToString(final Node node, final boolean omitXMLDecl) {
        StringWriter sw = new StringWriter();

        serializeAsXML(node, sw, omitXMLDecl);

        return sw.toString();
    }

    /**
     * Serialize as XML.
     * 
     * @param node
     *        the node to serialize.
     * @param writer
     *        the writer to serialize the DOM content to.
     * @param omitXMLDecl
     *        control if XML declarations are going to be ommitted or not.
     */
    public static void serializeAsXML(final Node node, final Writer writer, final boolean omitXMLDecl) {
        serializeAsXML(node, writer, omitXMLDecl, false);
    }

    /**
     * Serialize this node into the writer as XML.
     * 
     * @param node
     *        the node
     * @param writer
     *        the writer
     * @param omitXMLDecl
     *        the omit XML decl
     * @param pretty
     *        the pretty
     */
    public static void serializeAsXML(final Node node, final Writer writer, final boolean omitXMLDecl, final boolean pretty) {
        PrintWriter out = new PrintWriter(writer);
        String encoding = getEncoding(node);
        XMLEncoder encoder = XMLEncoderFactory.createInstance(encoding);
        if (!omitXMLDecl) {
            out.print("<?xml version=\"1.0\" encoding=\"");
            out.print(encoder.getEncoding());
            out.println("\"?>");
        }
        NSStack namespaceStack = new NSStack();
        print(node, namespaceStack, node, out, pretty, 0, encoder);
        out.flush();
    }

    /**
     * This method tries to figure out the encoding used to build this node.
     * 
     * @param node
     *        the node to examine
     * 
     * @return the encoding used in the XML declaration to build this document.
     */
    private static String getEncoding(final Node node) {
        String encoding = null;
        visit(node.getOwnerDocument(), 0, encoding);
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
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
     * 
     * @return null if no namespace is found, or the namespace URI.
     */
    private static String getNamespace(final String prefix, final Node e, final Node stopNode) {
        Node node = e;
        while ((node != null) && (node.getNodeType() == Node.ELEMENT_NODE)) {
            Attr attr = null;
            if (prefix == null) {
                attr = ((Element) node).getAttributeNode("xmlns");
            } else {
                attr = ((Element) node).getAttributeNodeNS(NS_URI_XMLNS, prefix);
            }
            if (attr != null) return attr.getValue();
            if (node == stopNode) return null;
            node = node.getParentNode();
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
     * 
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
            case Node.DOCUMENT_NODE:
                NodeList children1 = node.getChildNodes();

                if (children1 != null) {
                    int numChildren = children1.getLength();

                    for (int i = 0; i < numChildren; i++) {
                        print(children1.item(i), namespaceStack, startnode, out, pretty, indent, encoder);
                    }
                }
                break;

            case Node.DOCUMENT_FRAGMENT_NODE:
                NodeList children2 = node.getChildNodes();

                if (children2 != null) {
                    int numChildren = children2.getLength();

                    for (int i = 0; i < numChildren; i++) {
                        print(children2.item(i), namespaceStack, startnode, out, pretty, indent, encoder);
                    }
                }
                break;

            case Node.ELEMENT_NODE:
                namespaceStack.push();

                if (pretty) {
                    for (int i = 0; i < indent; i++) {
                        out.print(' ');
                    }
                }

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
                        e.printStackTrace();
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
                            e.printStackTrace();
                        }

                        if (!prefixIsDeclared) {
                            printNamespaceDecl(attr, attrPrefix, namespaceStack, startnode, out);
                        }
                    }

                    // SOPSolutions: we need to have the attribute value namespaces
                    // also
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
                                e.printStackTrace();
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
                    }
                }

                namespaceStack.pop();
                break;

            case Node.ENTITY_REFERENCE_NODE:
                out.print('&');
                out.print(node.getNodeName());
                out.print(';');
                break;

            case Node.CDATA_SECTION_NODE:
                out.print("<![CDATA[");
                out.print(node.getNodeValue());
                out.print("]]>");
                break;

            case Node.TEXT_NODE:
                out.print(normalize(node.getNodeValue(), encoder));
                break;

            case Node.COMMENT_NODE:
                out.print("<!--");
                out.print(node.getNodeValue());
                out.print("-->");
                if (pretty) {
                    out.print(LS);
                }
                break;

            case Node.PROCESSING_INSTRUCTION_NODE:
                out.print("<?");
                out.print(node.getNodeName());

                String data = node.getNodeValue();

                if ((data != null) && (data.length() > 0)) {
                    out.print(' ');
                    out.print(data);
                }

                out.println("?>");
                if (pretty) {
                    out.print(LS);
                }
                break;
            default:
                System.out.print("");
        }

        if ((type == Node.ELEMENT_NODE) && (hasChildren == true)) {
            if (pretty) {
                for (int i = 0; i < indent; i++) {
                    out.print(' ');
                }
            }
            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
            if (pretty) {
                out.print(LS);
            }
            hasChildren = false;
        }
    }

    /**
     * print the namespace declaration of an element node.
     * 
     * @param owner
     *        the owner
     * @param node
     *        the node
     * @param thePrefix
     *        the the prefix
     * @param namespaceStack
     *        the namespace stack
     * @param startnode
     *        the startnode
     * @param out
     *        the out
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
     * @param thePrefix
     *        the the prefix
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
            default:
                System.out.print("");
        }
    }

    // TODO why are the processing instructions gone?
    /**
     * visits all values in the node to find out the encoding.
     * 
     * @param node
     *        the node
     * @param level
     *        the level
     * @param encoding
     *        the encoding
     */
    private static void visit(final Node node, final int level, final String encoding) {
        if (encoding != null) return;
        if (node == null) return;
        NodeList list = node.getChildNodes();
        String enc = encoding;
        for (int i = 0; i < list.getLength(); i++) {
            // Get child node
            Node childNode = list.item(i);
            if (childNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                ProcessingInstruction pi = (ProcessingInstruction) childNode;
                if ("xml".equals(pi.getTarget()) && pi.getData().startsWith("encoding")) {
                    enc = pi.getData().substring(pi.getData().indexOf("\"") + 1, pi.getData().lastIndexOf("\""));
                    return;
                }
            }
            visit(childNode, level + 1, enc);
        }
    }
}
