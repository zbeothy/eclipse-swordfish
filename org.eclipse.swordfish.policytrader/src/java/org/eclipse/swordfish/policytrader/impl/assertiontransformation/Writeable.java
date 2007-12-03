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
package org.eclipse.swordfish.policytrader.impl.assertiontransformation;

import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Base class for writing to a XMLStreamWriter.
 */
public abstract class Writeable {

    /** Standard prefix mappings. */
    private static final Map STANDARD_PREFIXES = createStandardPrefixes();

    /** Counter for dynamic prefixes. */
    private static int nscount = 0;

    /**
     * Check for String value of <code>null</code> or empty String.
     * 
     * @param s
     *        String to test
     * 
     * @return <code>true</code> if String is neither <code>null</code> nor empty
     */
    protected static boolean nonzero(final String s) {
        return (s != null) && (s.length() > 0);
    }

    /**
     * Write an element tag dealing with namespaces.
     * 
     * @param name
     *        qualified name of element
     * @param writer
     *        StAX writer
     * 
     * @throws XMLStreamException
     *         as thrown by StAX writer
     */
    protected static void writeStartElement(final QKey name, final XMLStreamWriter writer) throws XMLStreamException {
        final String nsURI = name.getNamespaceURI();
        if (zero(nsURI)) {
            writer.writeStartElement(name.getLocalName());
            return;
        }
        final String prefix = writer.getPrefix(nsURI);
        if (prefix == null) {
            final String pfx = getPrefix(nsURI);
            writer.writeStartElement(pfx, name.getLocalName(), nsURI);
            writer.writeNamespace(pfx, nsURI);
            writer.setPrefix(pfx, nsURI);
        } else {
            writer.writeStartElement(nsURI, name.getLocalName());
        }
    }

    /**
     * Check for String value of <code>null</code> or empty String.
     * 
     * @param s
     *        String to test
     * 
     * @return <code>true</code> if String is <code>null</code> or empty
     */
    protected static boolean zero(final String s) {
        return (s == null) || (s.length() == 0);
    }

    /**
     * Initialize standard prefixes.
     * 
     * @return standard prefix map
     */
    private static Map createStandardPrefixes() {
        final Map result = new HashMap();
        result.put(AssertionTransformer.CLASSIC_ASSERTION_NAMESPACE, "sopa");
        result.put(AssertionTransformer.CLASSIC_AGREED_POLICY_NAMESPACE, "sopap");
        return result;
    }

    /**
     * Return a suitable standard or dynamic prefix for a namespace URI.
     * 
     * @param namespaceURI
     *        namespace URI
     * 
     * @return prefix String
     */
    private static String getPrefix(final String namespaceURI) {
        String result = (String) STANDARD_PREFIXES.get(namespaceURI);
        if (null == result) {
            if (nscount < 0) {
                nscount = 0;
            }
            result = "pns" + (nscount++);
        }
        return result;
    }

    /**
     * Standard constructor.
     */
    protected Writeable() {
        super();
    }

    /**
     * Write receiver into a StAX writer.
     * 
     * @param writer
     *        StAX writer
     * 
     * @throws XMLStreamException
     *         from StAX writer
     */
    public abstract void writeTo(final XMLStreamWriter writer) throws XMLStreamException;
}
