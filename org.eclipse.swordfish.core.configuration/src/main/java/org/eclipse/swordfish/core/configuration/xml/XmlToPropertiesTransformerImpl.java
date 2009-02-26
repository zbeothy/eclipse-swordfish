/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.core.configuration.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.swordfish.api.SwordfishException;
import org.springframework.util.Assert;

/**
 * Class is not threadsafe
 * @author vzhabiuk
 *
 */
public class XmlToPropertiesTransformerImpl implements XmlToPropertiesTransformer {
    private Map<String, String> properties;
    public Map<String, String> getProperties() {
        Assert.notNull(properties);
        return properties;
    }

    public boolean isConfigurationLoaded() {
        return properties != null;
    }
    public Map<String, Map<String, String>> getPropertiesForPids() {
        Assert.state(properties != null, "Configuration is not loaded");
        Set<String> prefixes = getPidPrefixes(properties.keySet());
        Map<String, Map<String, String>> ret = new HashMap<String, Map<String,String>>();
        for (String prefix : prefixes) {
            Map<String, String> pidProps = new HashMap<String, String>();
            for (String key : getKeysForPid(properties.keySet(), prefix)) {
                pidProps.put(key.substring(prefix.length() + 1), properties.get(key));
            }
            ret.put(prefix, pidProps);
        }
        return ret;
    }

    public void loadConfiguration(String path) {
        Assert.notNull(path);
        try {
            loadConfiguration(new URL(path));
        } catch (MalformedURLException ex) {
           throw new SwordfishException(ex);
        }
    }

    public void loadConfiguration(URL path) {
        Assert.notNull(path);
        InputStream inputStream = null;
        try {
            inputStream = path.openStream();
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            LinkedList<String> currentElements = new LinkedList<String>();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
            Map<String, List<String>> props = new HashMap<String, List<String>>();
            // Read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
                    putElement(props, getQualifiedName(currentElements), event.asCharacters().getData());

                }else if (event.isStartElement()) {
                    currentElements.add(event.asStartElement().getName().getLocalPart());
                    for (Iterator attrIt = event.asStartElement().getAttributes(); attrIt.hasNext();) {
                        Attribute attribute = (Attribute) attrIt.next();
                        putElement(props, getQualifiedName(currentElements) + "[@" + attribute.getName() + "]", attribute.getValue());

                    }
                } else if (event.isAttribute()) {
                     } else if (event.isEndElement()) {
                    String lastElem = event.asEndElement().getName().getLocalPart();
                    if (!currentElements.getLast().equals(lastElem)) {
                        throw new UnsupportedOperationException(lastElem+ "," + currentElements.getLast());
                    }
                    currentElements.removeLast();
                }
            }
            properties = flattenProperties(props);
        } catch (Exception ex) {
            throw new SwordfishException(ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {}
            }
        }
    }

    private String getQualifiedName(List<String> elements) {
        if (elements.size() == 0) {
            return null;
        }
        StringBuilder ret = new StringBuilder(elements.get(0));
        for (int i = 1; i < elements.size(); i++) {
            ret.append(".").append(elements.get(i));
        }
        return ret.toString();
    }

    private Map<String,String> flattenProperties(Map<String, List<String>> props) {
        Map<String,String> ret = new HashMap<String, String>(props.size());
        for (String key : props.keySet()) {
            List<String> values = props.get(key);
            Assert.state(values.size() > 0);
            //remove root element
            if (key.contains(".")) {
                key = key.substring(key.indexOf(".") + 1);
            }
            if (values.size() == 1) {
                ret.put(key, values.get(0));
            } else {
                for (int i = 0; i < values.size(); i++) {
                    ret.put(key + "{" + (i+1) + "}", values.get(i));
                }
            }
        }
        return ret;
    }

    private void  putElement(Map<String, List<String>> props, String key, String value) {
        if (props.containsKey(key)) {
            props.get(key).add(value);
        } else {
            List<String> list = new LinkedList<String>();
            list.add(value);
            props.put(key, list);
        }
    }

    private Set<String> getPidPrefixes(Set<String> keys) {
        Set<String> ret = new HashSet<String>();
        for (String key : keys) {
            String prefix = null;
            if (key.contains(".")) {
                prefix = key.substring(0, key.indexOf("."));
            }
            if (prefix != null) {
                ret.add(prefix);
            }

        }
        return ret;
    }
    private Set<String> getKeysForPid(Set<String> keys, String prefix) {
        Set<String> ret = new HashSet<String>();
        for (String key : keys) {
            if (key.startsWith(prefix)) {
                ret.add(key);
            }
        }
        return ret;
    }
}
