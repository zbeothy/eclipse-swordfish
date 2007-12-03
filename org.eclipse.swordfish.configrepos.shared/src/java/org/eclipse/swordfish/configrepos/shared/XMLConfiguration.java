/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Apache Software Foundation, Deutsche Post AG
 **************************************************************************************************/
/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertyConverter;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A specialized hierarchical configuration class that is able to parse XML documents.
 * 
 * <p>
 * The parsed document will be stored keeping its structure. The class also tries to preserve as
 * much information from the loaded XML document as possible, including comments and processing
 * instructions. These will be contained in documents created by the <code>save()</code> methods,
 * too.
 * </p>
 * 
 * <p>
 * Like other file based configuration classes this class maintains the name and path to the loaded
 * configuration file. These properties can be altered using several setter methods, but they are
 * not modified by <code>save()</code> and <code>load()</code> methods. If XML documents contain
 * relative paths to other documents (e.g. to a DTD), these references are resolved based on the
 * path set for this configuration.
 * </p>
 * 
 * <p>
 * By inheriting from <code>{@link AbstractConfiguration}</code> this class provides some extended
 * functionaly, e.g. interpolation of property values. Like in
 * <code>{@link PropertiesConfiguration}</code> property values can contain delimiter characters
 * (the comma ',' per default) and are then splitted into multiple values. This works for XML
 * attributes and text content of elements as well. The delimiter can be escaped by a backslash. As
 * an example consider the following XML fragment:
 * </p>
 * 
 * <p>
 * 
 * <pre>
 * &lt;config&gt;
 * &lt;array&gt;10,20,30,40&lt;/array&gt;
 * &lt;scalar&gt;3\,1415&lt;/scalar&gt;
 * &lt;cite text=&quot;To be or not to be\, this is the question!&quot;/&gt;
 * &lt;/config&gt;
 * </pre>
 * 
 * </p>
 * <p>
 * Here the content of the <code>array</code> element will be splitted at the commas, so the
 * <code>array</code> key will be assigned 4 values. In the <code>scalar</code> property and the
 * <code>text</code> attribute of the <code>cite</code> element the comma is escaped, so that no
 * splitting is performed.
 * </p>
 * 
 * <p>
 * <code>XMLConfiguration</code> implements the <code>{@link FileConfiguration}</code> interface
 * and thus provides full support for loading XML documents from different sources like files, URLs,
 * or streams. A full description of these features can be found in the documentation of
 * <code>{@link AbstractFileConfiguration}</code>.
 * </p> *
 * 
 * 
 * adaption of commons-configuration XMLConfiguration
 */

public class XMLConfiguration extends AbstractHierarchicalFileConfiguration {

    /** The Constant JNDI_ATTR_CN_VALUE. */
    public static final String JNDI_ATTR_CN_VALUE = "configuration";

    /** The Constant JNDI_ATTR_CN_NAME. */
    public static final String JNDI_ATTR_CN_NAME = "cn";

    /** The Constant JNDI_ATTR_OBJECT_CLASS_NAME. */
    public static final String JNDI_ATTR_OBJECT_CLASS_NAME = "objectClass";

    /** The Constant JNDI_CLASS_TOP. */
    public static final String JNDI_CLASS_TOP = "top";

    /** The Constant JNDI_CLASS_SOP_CFG. */
    public static final String JNDI_CLASS_SOP_CFG = "sopCScfgobject";

    /** The Constant JNDI_ATTR_XML_NAME. */
    public static final String JNDI_ATTR_XML_NAME = "sopCScfgxmldata";

    private static final long serialVersionUID = 1353677853586227352L;

    /** Delimiter character for attributes. Set to 0x0 to avoid problems with JNDI URLS - dwz */
    private static final char ATTR_DELIMITER = (char) 0x0;

    /** Constant for the default root element name. */
    private static final String DEFAULT_ROOT_NAME = "configuration";

    /**
     * Clone node.
     * 
     * @param node
     *        the node
     * 
     * @return the node
     */
    public static Node cloneNode(final Node node) {
        Node result = new Node(node.getName());
        result.setValue(node.getValue());
        result.setParent(node.getParent());
        Iterator kids = node.getChildren().iterator();
        while (kids.hasNext()) {
            Node kid = (Node) kids.next();
            result.addChild(cloneNode(kid));
        }
        return result;
    }

    /**
     * Config from bytes.
     * 
     * @param configBytes
     *        the config bytes
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration configFromBytes(final byte[] configBytes) throws ConfigurationException {
        ByteArrayInputStream configIs = new ByteArrayInputStream(configBytes);
        XMLConfiguration xmlConfig = new XMLConfiguration();
        xmlConfig.load(configIs);
        return xmlConfig;
    }

    /**
     * Creates the from jndi attributes.
     * 
     * @param attribs
     *        the attribs
     * 
     * @return the XML configuration
     * 
     * @throws ConfigurationException
     */
    public static XMLConfiguration createFromJndiAttributes(final Attributes attribs) throws ConfigurationException {
        String xml;
        String cn;
        try {
            xml = (String) attribs.get(JNDI_ATTR_XML_NAME).get();
            cn = (String) attribs.get(JNDI_ATTR_CN_NAME).get();
        } catch (NamingException e) {
            throw new ConfigurationException("Error accessing expected attributes for configuartion", e);
        }
        if ((xml == null) || (xml.length() == 0) || (!JNDI_ATTR_CN_VALUE.equals(cn)))
            throw new ConfigurationException("Attributes don't hold expected structure for configuration");
        XMLConfiguration result = new XMLConfiguration();
        result.load(new StringReader(xml));
        return result;
    }

    /**
     * Equals.
     * 
     * @param node1
     *        the node1
     * @param node2
     *        the node2
     * 
     * @return true, if successful
     */
    public static boolean equals(final Node node1, final Node node2) {
        if (!equalObjects(node1.getName(), node2.getName())) return false;
        if (!equalObjects(node1.getValue(), node2.getValue())) return false;
        Map kidsMap1 = node1.getChildrenMap();
        Map kidsMap2 = node2.getChildrenMap();
        if ((kidsMap1 == null) && (kidsMap2 == null)) return true;
        if ((kidsMap1 == null) || (kidsMap2 == null)) return false;
        Set entrySet1 = kidsMap1.entrySet();
        Set entrySet2 = kidsMap2.entrySet();
        if (entrySet1.size() != entrySet2.size()) return false;
        Iterator entries1 = kidsMap1.entrySet().iterator();
        while (entries1.hasNext()) {
            Map.Entry entry1 = (Map.Entry) entries1.next();
            String key1 = (String) entry1.getKey();
            List kids1 = (List) entry1.getValue();
            List kids2 = (List) kidsMap2.get(key1);
            if (kids2 == null) return false;
            if (kids1.size() == kids2.size()) {
                for (int i = 0; i < kids1.size(); i++) {
                    if (!equals((Node) kids1.get(i), (Node) kids2.get(i))) return false;
                }
            } else
                return false;
        }
        return true;
    }

    /**
     * Find invalid parent.
     * 
     * @param node
     *        the node
     * 
     * @return the node
     */
    public static Node findInvalidParent(final Node node) {
        Iterator kids = node.getChildren().iterator();
        while (kids.hasNext()) {
            Node kid = (Node) kids.next();
            if (node != kid.getParent()) return kid;
            Node invalid = findInvalidParent(kid);
            if (invalid != null) return invalid;
        }
        return null;
    }

    /**
     * Equal objects.
     * 
     * @param o1
     *        the o1
     * @param o2
     *        the o2
     * 
     * @return true, if successful
     */
    private static boolean equalObjects(final Object o1, final Object o2) {
        if ((o1 == null) && (o2 == null))
            return true;
        else
            return (o1 != null) && o1.equals(o2);
    }

    /** The document from this configuration's data source. */
    private Document document;

    /** Stores the name of the root element. */
    private String rootElementName;

    /** Stores the document builder that should be used for loading. */
    private DocumentBuilder documentBuilder;

    /** Stores a flag whether DTD validation should be performed. */
    private boolean validating;

    /**
     * Creates a new instance of <code>XMLConfiguration</code>.
     */
    public XMLConfiguration() {
        super();
        // disable delimiter handling
        // otherwise we run into problems with
        // schema validation, because escaped
        // delimiters in LDAP URLs may cause
        // trouble - dwz
        AbstractConfiguration.setDelimiter(ATTR_DELIMITER);
    }

    /**
     * Creates a new instance of <code>XMLConfiguration</code>. The configuration is loaded from
     * the specified file.
     * 
     * @param file
     *        the file
     * 
     * @throws ConfigurationException
     *         if an error occurs while loading the file
     */
    public XMLConfiguration(final File file) throws ConfigurationException {
        this();
        this.setFile(file);
        if (file.exists()) {
            this.load();
        }
    }

    /**
     * Creates a new instance of <code>XMLConfiguration</code>. The configuration is loaded from
     * the specified file
     * 
     * @param fileName
     *        the name of the file to load
     * 
     * @throws ConfigurationException
     *         if the file cannot be loaded
     */
    public XMLConfiguration(final String fileName) throws ConfigurationException {
        this();
        this.setFileName(fileName);
        // setFile(new File(fileName));
        this.load();
    }

    /**
     * Creates a new instance of <code>XMLConfiguration</code>. The configuration is loaded from
     * the specified URL.
     * 
     * @param url
     *        the URL
     * 
     * @throws ConfigurationException
     *         if loading causes an error
     */
    public XMLConfiguration(final URL url) throws ConfigurationException {
        this();
        this.setURL(url);
        this.load();
    }

    /**
     * Removes all properties from this configuration. If this configuration was loaded from a file,
     * the associated DOM document is also cleared.
     */
    @Override
    public void clear() {
        super.clear();
        this.document = null;
    }

    /**
     * Creates a copy of this object. The new configuration object will contain the same properties
     * as the original, but it will lose any connection to a source document (if one exists). This
     * is to avoid race conditions if both the original and the copy are modified and then saved.
     * 
     * @return the copy
     */
    @Override
    public Object clone() {
        XMLConfiguration copy = (XMLConfiguration) super.clone();

        // clear document related properties
        copy.document = null;
        copy.setDelegate(this.createDelegate());
        // clear all references in the nodes, too
        copy.getRoot().visit(new NodeVisitor() {

            @Override
            public void visitBeforeChildren(final Node node, final ConfigurationKey key) {
                node.setReference(null);
            }
        }, null);

        return copy;
    }

    /**
     * Clone configuration.
     * 
     * @return the XML configuration
     */
    public XMLConfiguration cloneConfiguration() {
        XMLConfiguration clone = new XMLConfiguration();
        clone.setRoot(cloneNode(this.getRoot()));
        return clone;
    }

    /**
     * Creates the nodes at path.
     * 
     * @param key
     *        the key
     * 
     * @return the list
     */
    public List createNodesAtPath(final String key) {
        List nodes = new ArrayList();
        this.createNodesAtPath(new ConfigurationKey(key).iterator(), this.getRoot(), nodes);
        if (nodes.size() > 0) {
            nodes.remove(0);
        }
        return nodes;
    }

    /**
     * Fetch nodes at path.
     * 
     * @param key
     *        the key
     * 
     * @return the list
     */
    public List fetchNodesAtPath(final String key) {
        List nodes = new ArrayList();
        this.findNodesAtPath(new ConfigurationKey(key).iterator(), this.getRoot(), nodes);
        if (nodes.size() > 0) {
            nodes.remove(0);
        }
        return nodes;
    }

    /**
     * Returns the XML document this configuration was loaded from. The return value is <b>null</b>
     * if this configuration was not loaded from a XML document.
     * 
     * @return the XML document this configuration was loaded from
     */
    public Document getDocument() {
        return this.document;
    }

    /**
     * Returns the <code>DocumentBuilder</code> object that is used for loading documents. If no
     * specific builder has been set, this method returns <b>null</b>.
     * 
     * @return the <code>DocumentBuilder</code> for loading new documents
     */
    public DocumentBuilder getDocumentBuilder() {
        return this.documentBuilder;
    }

    /**
     * Gets the node.
     * 
     * @param prefix
     *        the prefix
     * 
     * @return the node
     */
    public Node getNode(final String prefix) {
        List nodes = this.fetchNodeList(prefix);
        if (nodes.size() > 0)
            return (Node) nodes.get(0);
        else
            return null;
    }

    /**
     * Gets the nodes.
     * 
     * @param prefix
     *        the prefix
     * 
     * @return the nodes
     */
    public List getNodes(final String prefix) {
        return this.fetchNodeList(prefix);
    }

    /**
     * Returns the name of the root element. If this configuration was loaded from a XML document,
     * the name of this document's root element is returned. Otherwise it is possible to set a name
     * for the root element that will be used when this configuration is stored.
     * 
     * @return the name of the root element
     */
    public String getRootElementName() {
        if (this.getDocument() == null)
            return (this.rootElementName == null) ? DEFAULT_ROOT_NAME : this.rootElementName;
        else
            return this.getDocument().getDocumentElement().getNodeName();
    }

    /**
     * Initializes this configuration from an XML document.
     * 
     * @param pDocument
     *        the document to be parsed
     * @param elemRefs
     *        a flag whether references to the XML elements should be set
     */
    public void initProperties(final Document pDocument, final boolean elemRefs) {
        this.constructHierarchy(this.getRoot(), pDocument.getDocumentElement(), elemRefs);
    }

    /**
     * Returns the value of the validating flag.
     * 
     * @return the validating flag
     */
    public boolean isValidating() {
        return this.validating;
    }

    /**
     * Loads the configuration from the given input stream.
     * 
     * @param in
     *        the input stream
     * 
     * @throws ConfigurationException
     *         if an error occurs
     */
    @Override
    public void load(final InputStream in) throws ConfigurationException {
        this.load(new InputSource(in));
    }

    /**
     * Load the configuration from the given reader. Note that the <code>clear()</code> method is
     * not called, so the properties contained in the loaded file will be added to the actual set of
     * properties.
     * 
     * @param in
     *        An InputStream.
     * 
     * @throws ConfigurationException
     *         if an error occurs
     */
    public void load(final Reader in) throws ConfigurationException {
        this.load(new InputSource(in));
    }

    /**
     * Rebuild document.
     * 
     * @throws ConfigurationException
     */
    public void rebuildDocument() throws ConfigurationException {
        this.document = this.createDocument();
    }

    /**
     * Saves the configuration to the specified writer.
     * 
     * @param writer
     *        the writer used to save the configuration
     * 
     * @throws ConfigurationException
     *         if an error occurs
     */
    public void save(final Writer writer) throws ConfigurationException {
        ClassLoader currThreadLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            Source source = new DOMSource(this.createDocument());
            Result result = new StreamResult(writer);

            Properties props = new Properties();
            props.setProperty(OutputKeys.INDENT, "yes");
            props.setProperty(OutputKeys.ENCODING, "UTF-8");
            props.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
            props.setProperty(OutputKeys.METHOD, "xml");
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperties(props);

            transformer.transform(source, result);

            // old code from the 1.1 version as adapted for SOPWare - dwz

            // Transformer transformer =
            // TransformerFactory.newInstance().newTransformer();

            // Workaround code replacing code commented out below
            // Document doc = createDocument();
            // OutputFormat format = new OutputFormat(doc);
            // XMLSerializer output = new XMLSerializer(writer, format);
            // output.serialize(doc);

            // Code commented out, since Tomcat and J2EE runtime environments
            // mix and match
            // different vendor brands for XML parsers and XSLT processors,
            // which obviously
            // do not "like eachother" and produce errors or no output.
            // Source source = new DOMSource(createDocument());
            // Result result = new StreamResult(writer);

            // Properties props = new Properties();
            // props.setProperty(OutputKeys.INDENT, "yes");
            // props.setProperty(OutputKeys.ENCODING, "UTF-8");
            // props.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
            // props.setProperty(OutputKeys.METHOD, "xml");
            // props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            // transformer.setOutputProperties(props);

            // transformer.transform(source, result);

            // } catch (IOException e) {
            // throw new ConfigurationException(e.getMessage(), e);
            // }
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(currThreadLoader);
        }

    }

    /**
     * Sets the <code>DocumentBuilder</code> object to be used for loading documents. This method
     * makes it possible to specify the exact document builder. So an application can create a
     * builder, configure it for its special needs, and then pass it to this method.
     * 
     * @param documentBuilder
     *        the document builder to be used; if undefined, a default builder will be used
     * 
     */
    public void setDocumentBuilder(final DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    /**
     * Sets the name of the root element. This name is used when this configuration object is stored
     * in an XML file. Note that setting the name of the root element works only if this
     * configuration has been newly created. If the configuration was loaded from an XML file, the
     * name cannot be changed and an <code>UnsupportedOperationException</code> exception is
     * thrown. Whether this configuration has been loaded from an XML document or not can be found
     * out using the <code>getDocument()</code> method.
     * 
     * @param name
     *        the name of the root element
     */
    public void setRootElementName(final String name) {
        if (this.getDocument() != null)
            throw new UnsupportedOperationException("The name of the root element "
                    + "cannot be changed when loaded from an XML document!");
        this.rootElementName = name;
    }

    /**
     * Sets the value of the validating flag. This flag determines whether DTD validation should be
     * performed when loading XML documents. This flag is evaluated only if no custom
     * <code>DocumentBuilder</code> was set.
     * 
     * @param validating
     *        the validating flag
     */
    public void setValidating(final boolean validating) {
        this.validating = validating;
    }

    /**
     * To bytes.
     * 
     * @return the byte[]
     * 
     * @throws ConfigurationException
     */
    public byte[] toBytes() throws ConfigurationException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(bos, "UTF-8");
            this.cloneConfiguration().save(writer);
            writer.flush();
            return bos.toByteArray();
        } catch (UnsupportedEncodingException uee) {
            throw new ConfigurationException("error while cloning configuration.", uee);
        } catch (IOException ioe) {
            throw new ConfigurationException("error while cloning configuration.", ioe);
        }
    }

    /**
     * To jndi attributes.
     * 
     * @return the attributes
     * 
     * @throws ConfigurationException
     */
    public Attributes toJndiAttributes() throws ConfigurationException {
        Attributes attribs = new BasicAttributes();
        attribs.put(JNDI_ATTR_CN_NAME, JNDI_ATTR_CN_VALUE);
        attribs.put(JNDI_ATTR_OBJECT_CLASS_NAME, JNDI_CLASS_TOP);
        attribs.get(JNDI_ATTR_OBJECT_CLASS_NAME).add(JNDI_CLASS_SOP_CFG);
        StringWriter writer = new StringWriter();
        this.cloneConfiguration().save(writer);
        attribs.put(JNDI_ATTR_XML_NAME, writer.toString());
        return attribs;
    }

    /**
     * (non-Javadoc).
     * 
     * @see java.lang.Object#toString()
     * @return String
     */
    @Override
    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            this.cloneConfiguration().save(writer);
            return writer.toString();
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return "Error converting configuration to string, " + e.getMessage();
        }
    }

    /**
     * Creates the file configuration delegate for this object. This implementation will return an
     * instance of a class derived from <code>FileConfigurationDelegate</code> that deals with
     * some specialities of <code>XMLConfiguration</code>.
     * 
     * @return the delegate for this object
     */
    @Override
    protected FileConfigurationDelegate createDelegate() {
        return new XMLFileConfigurationDelegate();
    }

    /**
     * Creates a DOM document from the internal tree of configuration nodes.
     * 
     * @return the new document
     * 
     * @throws ConfigurationException
     *         if an error occurs
     */
    protected Document createDocument() throws ConfigurationException {
        try {
            if (this.document == null) {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document newDocument = builder.newDocument();
                Element rootElem = newDocument.createElement(this.getRootElementName());
                newDocument.appendChild(rootElem);
                this.document = newDocument;
            }

            XMLBuilderVisitor builder = new XMLBuilderVisitor(this.document);
            builder.processDocument(this.getRoot());
            return this.document;
        } catch (DOMException domEx) {
            throw new ConfigurationException(domEx);
        } catch (ParserConfigurationException pex) {
            throw new ConfigurationException(pex);
        }
    }

    /**
     * Creates the <code>DocumentBuilder</code> to be used for loading files. This implementation
     * checks whether a specific <code>DocumentBuilder</code> has been set. If this is the case,
     * this one is used. Otherwise a default builder is created. Depending on the value of the
     * validating flag this builder will be a validating or a non validating
     * <code>DocumentBuilder</code>.
     * 
     * @return the <code>DocumentBuilder</code> for loading configuration files
     * 
     * @throws ParserConfigurationException
     *         if an error occurs
     */
    protected DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        if (this.getDocumentBuilder() != null)
            return this.getDocumentBuilder();
        else {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(this.isValidating());
            factory.setNamespaceAware(true);
            DocumentBuilder result = factory.newDocumentBuilder();

            if (this.isValidating()) {
                // register an error handler which detects validation errors
                result.setErrorHandler(new DefaultHandler() {

                    @Override
                    public void error(final SAXParseException ex) throws SAXException {
                        throw ex;
                    }
                });
            }
            return result;
        }
    }

    /**
     * Creates a new node object. This implementation returns an instance of the
     * <code>XMLNode</code> class.
     * 
     * @param name
     *        the node's name
     * 
     * @return the new node
     */
    @Override
    protected Node createNode(final String name) {
        return new XMLNode(name, null);
    }

    /**
     * Helper method for building the internal storage hierarchy. The XML elements are transformed
     * into node objects.
     * 
     * @param node
     *        the actual node
     * @param element
     *        the actual XML element
     * @param elemRefs
     *        a flag whether references to the XML elements should be set
     */
    private void constructHierarchy(final Node node, final Element element, final boolean elemRefs) {
        this.processAttributes(node, element, elemRefs);
        StringBuffer buffer = new StringBuffer();
        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            org.w3c.dom.Node w3cNode = list.item(i);
            if (w3cNode instanceof Element) {
                Element child = (Element) w3cNode;
                Node childNode = new XMLNode(child.getTagName(), elemRefs ? child : null);
                this.constructHierarchy(childNode, child, elemRefs);
                node.addChild(childNode);
                this.handleDelimiters(node, childNode);
            } else if (w3cNode instanceof Text) {
                Text data = (Text) w3cNode;
                buffer.append(data.getData());
            }
        }
        String text = buffer.toString().trim();
        if ((text.length() > 0) || !node.hasChildren()) {
            node.setValue(text);
        }
    }

    /**
     * Creates the nodes at path.
     * 
     * @param keyPart
     *        the key part
     * @param node
     *        the node
     * @param data
     *        the data
     */
    private void createNodesAtPath(final ConfigurationKey.KeyIterator keyPart, final Node node, final Collection data) {
        data.add(node);
        if (keyPart.hasNext()) {
            String key = keyPart.nextKey(true);
            List children = node.getChildren(key);
            if (keyPart.hasIndex()) {
                if ((keyPart.getIndex() < children.size()) && (keyPart.getIndex() >= 0)) {
                    this.createNodesAtPath((ConfigurationKey.KeyIterator) keyPart.clone(), (Node) children.get(keyPart.getIndex()),
                            data);
                } else
                    throw new RuntimeException("error creating nodes at path");
            } else {
                if (children.size() == 0) {
                    node.addChild(new Node(key));
                    children = node.getChildren(key);
                }
                this.createNodesAtPath((ConfigurationKey.KeyIterator) keyPart.clone(), (Node) children.get(0), data);
            }
        }
    }

    /**
     * Recursive helper method for fetching configurations along a scope path. This method processes
     * all facets of a configuration key, traverses the tree of configurations and fetches the the
     * configurations along the scope path.
     * 
     * @param keyPart
     *        the configuration key iterator
     * @param node
     *        the actual node
     * @param data
     *        here the found nodes are stored
     */
    private void findNodesAtPath(final ConfigurationKey.KeyIterator keyPart, final Node node, final Collection data) {
        data.add(node);
        if (keyPart.hasNext()) {
            String key = keyPart.nextKey(true);
            List children = node.getChildren(key);
            if (keyPart.hasIndex()) {
                if ((keyPart.getIndex() < children.size()) && (keyPart.getIndex() >= 0)) {
                    this.findNodesAtPath((ConfigurationKey.KeyIterator) keyPart.clone(), (Node) children.get(keyPart.getIndex()),
                            data);
                }
            } else {
                if (children.size() > 0) {
                    this.findNodesAtPath((ConfigurationKey.KeyIterator) keyPart.clone(), (Node) children.get(0), data);
                }
            }
        }
    }

    /*
     * original version: public void save(Writer writer) throws ConfigurationException { try {
     * Transformer transformer = TransformerFactory.newInstance().newTransformer(); Source source =
     * new DOMSource(createDocument()); Result result = new StreamResult(writer);
     * 
     * transformer.setOutputProperty(OutputKeys.INDENT, "yes"); if (getEncoding() != null) {
     * transformer.setOutputProperty(OutputKeys.ENCODING, getEncoding()); }
     * transformer.transform(source, result); } catch (TransformerException e) { throw new
     * ConfigurationException(e.getMessage(), e); } }
     */

    /**
     * Deals with elements whose value is a list. In this case multiple child elements must be
     * added.
     * 
     * @param parent
     *        the parent element
     * @param child
     *        the child element
     */
    private void handleDelimiters(final Node parent, final Node child) {
        if (child.getValue() != null) {
            List values = PropertyConverter.split(child.getValue().toString(), getDelimiter());
            if (values.size() > 1) {
                // remove the original child
                parent.remove(child);
                // add multiple new children
                for (Iterator it = values.iterator(); it.hasNext();) {
                    Node c = new XMLNode(child.getName(), null);
                    c.setValue(it.next());
                    parent.addChild(c);
                }
            } else if (values.size() == 1) {
                // we will have to replace the value because it might
                // contain escaped delimiters
                child.setValue(values.get(0));
            }
        }
    }

    /**
     * Loads a configuration file from the specified input source.
     * 
     * @param source
     *        the input source
     * 
     * @throws ConfigurationException
     *         if an error occurs
     */
    private void load(final InputSource source) throws ConfigurationException {
        try {
            URL sourceURL = this.getDelegate().getURL();
            if (sourceURL != null) {
                source.setSystemId(sourceURL.toString());
            }

            DocumentBuilder builder = this.createDocumentBuilder();
            Document newDocument = builder.parse(source);
            Document oldDocument = this.document;
            this.document = null;
            this.initProperties(newDocument, oldDocument == null);
            this.document = (oldDocument == null) ? newDocument : oldDocument;
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

    /**
     * Helper method for constructing node objects for the attributes of the given XML element.
     * 
     * @param node
     *        the actual node
     * @param element
     *        the actual XML element
     * @param elemRefs
     *        a flag whether references to the XML elements should be set
     */
    private void processAttributes(final Node node, final Element element, final boolean elemRefs) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            org.w3c.dom.Node w3cNode = attributes.item(i);
            if (w3cNode instanceof Attr) {
                Attr attr = (Attr) w3cNode;
                for (Iterator it = PropertyConverter.split(attr.getValue(), getDelimiter()).iterator(); it.hasNext();) {
                    Node child = new XMLNode(ConfigurationKey.constructAttributeKey(attr.getName()), elemRefs ? element : null);
                    child.setValue(it.next());
                    node.addChild(child);
                }
            }
        }
    }

    /**
     * A concrete <code>BuilderVisitor</code> that can construct XML documents.
     */
    static class XMLBuilderVisitor extends BuilderVisitor {

        /**
         * Updates the value of the specified attribute of the given node. Because there can be
         * multiple child nodes representing this attribute the new value is determined by iterating
         * over all those child nodes.
         * 
         * @param node
         *        the affected node
         * @param name
         *        the name of the attribute
         */
        static void updateAttribute(final Node node, final String name) {
            if (node != null) {
                updateAttribute(node, (Element) node.getReference(), name);
            }
        }

        /**
         * Helper method for updating the value of the specified node's attribute with the given
         * name.
         * 
         * @param node
         *        the affected node
         * @param elem
         *        the element that is associated with this node
         * @param name
         *        the name of the affected attribute
         */
        private static void updateAttribute(final Node node, final Element elem, final String name) {
            if ((node != null) && (elem != null)) {
                List attrs = node.getChildren(name);
                StringBuffer buf = new StringBuffer();
                for (Iterator it = attrs.iterator(); it.hasNext();) {
                    Node attr = (Node) it.next();
                    if (attr.getValue() != null) {
                        if (buf.length() > 0) {
                            buf.append(getDelimiter());
                        }
                        buf.append(PropertyConverter.escapeDelimiters(attr.getValue().toString(), getDelimiter()));
                    }
                    attr.setReference(elem);
                }

                if (buf.length() < 1) {
                    elem.removeAttribute(ConfigurationKey.removeAttributeMarkers(name));
                } else {
                    elem.setAttribute(ConfigurationKey.removeAttributeMarkers(name), buf.toString());
                }
            }
        }

        /** Stores the document to be constructed. */
        private Document document;

        /**
         * Creates a new instance of <code>XMLBuilderVisitor</code>.
         * 
         * @param doc
         *        the document to be created
         */
        public XMLBuilderVisitor(final Document doc) {
            this.document = doc;
        }

        /**
         * Processes the node hierarchy and adds new nodes to the document.
         * 
         * @param rootNode
         *        the root node
         */
        public void processDocument(final Node rootNode) {
            rootNode.visit(this, null);
        }

        /**
         * Inserts a new node. This implementation ensures that the correct XML element is created
         * and inserted between the given siblings.
         * 
         * @param newNode
         *        the node to insert
         * @param parent
         *        the parent node
         * @param sibling1
         *        the first sibling
         * @param sibling2
         *        the second sibling
         * 
         * @return the new node
         */
        @Override
        protected Object insert(final Node newNode, final Node parent, final Node sibling1, final Node sibling2) {
            if (ConfigurationKey.isAttributeKey(newNode.getName())) {
                updateAttribute(parent, this.getElement(parent), newNode.getName());
                return null;
            } else {
                Element elem = this.document.createElement(newNode.getName());
                if (newNode.getValue() != null) {
                    elem.appendChild(this.document.createTextNode(PropertyConverter.escapeDelimiters(newNode.getValue().toString(),
                            getDelimiter())));
                }
                if (sibling2 == null) {
                    this.getElement(parent).appendChild(elem);
                } else if (sibling1 != null) {
                    this.getElement(parent).insertBefore(elem, this.getElement(sibling1).getNextSibling());
                } else {
                    this.getElement(parent).insertBefore(elem, this.getElement(parent).getFirstChild());
                }
                return elem;
            }
        }

        /**
         * Helper method for accessing the element of the specified node.
         * 
         * @param node
         *        the node
         * 
         * @return the element of this node
         */
        private Element getElement(final Node node) {
            // special treatement for root node of the hierarchy
            return (node.getName() != null) ? (Element) node.getReference() : this.document.getDocumentElement();
        }
    }

    /**
     * A specialized <code>Node</code> class that is connected with an XML element. Changes on a
     * node are also performed on the associated element.
     */
    class XMLNode extends Node {

        /**
         * 
         */
        private static final long serialVersionUID = 5844542338325090032L;

        /**
         * Creates a new instance of <code>XMLNode</code> and initializes it with a name and the
         * corresponding XML element.
         * 
         * @param name
         *        the node's name
         * @param elem
         *        the XML element
         */
        public XMLNode(final String name, final Element elem) {
            super(name);
            this.setReference(elem);
        }

        /**
         * Sets the value of this node. If this node is associated with an XML element, this element
         * will be updated, too.
         * 
         * @param value
         *        the node's new value
         */
        @Override
        public void setValue(final Object value) {
            super.setValue(value);

            if ((this.getReference() != null) && (XMLConfiguration.this.document != null)) {
                if (ConfigurationKey.isAttributeKey(this.getName())) {
                    this.updateAttribute();
                } else {
                    this.updateElement(value);
                }
            }
        }

        /**
         * Updates the associated XML elements when a node is removed.
         */
        @Override
        protected void removeReference() {
            if (this.getReference() != null) {
                Element element = (Element) this.getReference();
                if (ConfigurationKey.isAttributeKey(this.getName())) {
                    this.updateAttribute();
                } else {
                    org.w3c.dom.Node parentElem = element.getParentNode();
                    if (parentElem != null) {
                        parentElem.removeChild(element);
                    }
                }
            }
        }

        /**
         * Returns the only text node of this element for update. This method is called when the
         * element's text changes. Then all text nodes except for the first are removed. A reference
         * to the first is returned or <b>null </b> if there is no text node at all.
         * 
         * @return the first and only text node
         */
        private Text findTextNodeForUpdate() {
            Text result = null;
            Element elem = (Element) this.getReference();
            // Find all Text nodes
            NodeList children = elem.getChildNodes();
            Collection textNodes = new ArrayList();
            for (int i = 0; i < children.getLength(); i++) {
                org.w3c.dom.Node nd = children.item(i);
                if (nd instanceof Text) {
                    if (result == null) {
                        result = (Text) nd;
                    } else {
                        textNodes.add(nd);
                    }
                }
            }

            // We don't want CDATAs
            if (result instanceof CDATASection) {
                textNodes.add(result);
                result = null;
            }

            // Remove all but the first Text node
            for (Iterator it = textNodes.iterator(); it.hasNext();) {
                elem.removeChild((org.w3c.dom.Node) it.next());
            }
            return result;
        }

        /**
         * Updates the node's value if it represents an attribute.
         */
        private void updateAttribute() {
            XMLBuilderVisitor.updateAttribute(this.getParent(), this.getName());
        }

        /**
         * Updates the node's value if it represents an element node.
         * 
         * @param value
         *        the new value
         */
        private void updateElement(final Object value) {
            Text txtNode = this.findTextNodeForUpdate();
            if (value == null) {
                // remove text
                if (txtNode != null) {
                    ((Element) this.getReference()).removeChild(txtNode);
                }
            } else {
                if (txtNode == null) {
                    txtNode =
                            XMLConfiguration.this.document.createTextNode(PropertyConverter.escapeDelimiters(value.toString(),
                                    getDelimiter()));
                    if (((Element) this.getReference()).getFirstChild() != null) {
                        ((Element) this.getReference()).insertBefore(txtNode, ((Element) this.getReference()).getFirstChild());
                    } else {
                        ((Element) this.getReference()).appendChild(txtNode);
                    }
                } else {
                    txtNode.setNodeValue(PropertyConverter.escapeDelimiters(value.toString(), getDelimiter()));
                }
            }
        }
    }

    /**
     * A special implementation of the <code>FileConfiguration</code> interface that is used
     * internally to implement the <code>FileConfiguration</code> methods for
     * <code>XMLConfiguration</code>, too.
     */
    private class XMLFileConfigurationDelegate extends FileConfigurationDelegate {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swordfish.configrepos.shared.AbstractFileConfiguration#load(java.io.InputStream)
         */
        @Override
        public void load(final InputStream in) throws ConfigurationException {
            XMLConfiguration.this.load(in);
        }
    }
}
