package org.eclipse.swordfish.core.configuration.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.springframework.util.Assert;

public class SaxParsingPrototype {
    private static String getQualifiedName(List<String> elements) {
        if (elements.size() == 0) {
            return null;
        }
        StringBuilder ret = new StringBuilder(elements.get(0));
        for (int i = 1; i < elements.size(); i++) {
            ret.append(".").append(elements.get(i));
        }
        return ret.toString();
    }

    private static Map<String,String> flatten(Map<String, List<String>> props) {
        Map<String,String> ret = new HashMap<String, String>(props.size());
        for (String key : props.keySet()) {
            List<String> values = props.get(key);
            Assert.state(values.size() > 0);
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

    private static void  putElement(Map<String, List<String>> props, String key, String value) {
        if (props.containsKey(key)) {
            props.get(key).add(value);
        } else {
            List<String> list = new LinkedList<String>();
            list.add(value);
            props.put(key, list);
        }
    }
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        LinkedList<String> currentElements = new LinkedList<String>();
        InputStream in = SaxParsingPrototype.class.getResource(
                "ComplexPidXmlProperties.xml").openStream();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
        Map<String, List<String>> props = new HashMap<String, List<String>>();
        // Read the XML document
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
                putElement(props, getQualifiedName(currentElements), event.asCharacters().getData());

            }else if (event.isStartElement()) {
                System.out.println(event.asStartElement().getName());
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

        eventReader.close();
    }

}
