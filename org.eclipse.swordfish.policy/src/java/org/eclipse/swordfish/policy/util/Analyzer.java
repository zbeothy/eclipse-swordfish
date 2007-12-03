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
package org.eclipse.swordfish.policy.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.ws.policy.All;
import org.apache.ws.policy.Assertion;
import org.apache.ws.policy.ExactlyOne;
import org.apache.ws.policy.Policy;
import org.apache.ws.policy.PolicyReference;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * The Class Analyzer.
 */
public class Analyzer {

    /** The Constant DEFAULT_INDENT. */
    private static final String DEFAULT_INDENT = "  ";

    /**
     * Dump.
     * 
     * @param assertion
     *        the assertion
     * 
     * @return the string
     */
    public static String dump(final Object assertion) {
        StringBuffer ret = new StringBuffer();
        ret = dump(assertion, false, new StringBuffer(), new StringBuffer());
        return ret.toString();
    }

    /**
     * Dump.
     * 
     * @param assertion
     *        the assertion
     * @param verbose
     *        the verbose
     * 
     * @return the string
     */
    public static String dump(final Object assertion, final boolean verbose) {
        StringBuffer ret = new StringBuffer();
        ret = dump(assertion, verbose, new StringBuffer(), new StringBuffer());
        return ret.toString();
    }

    /**
     * Dump.
     * 
     * @param object
     *        the object
     * @param verbose
     *        the verbose
     * @param buffer
     *        the buffer
     * @param indent
     *        the indent
     * 
     * @return the string buffer
     */
    private static StringBuffer dump(final Object object, final boolean verbose, final StringBuffer buffer,
            final StringBuffer indent) {
        if (!(object instanceof Assertion)) {
            buffer.append("Unexpected object " + object.toString() + " (" + object.getClass().getName() + ")\n");
            return buffer;
        }
        Assertion assertion = (Assertion) object;
        buffer.append("\n").append(indent);
        // TODO: needs refactoring
        if (assertion instanceof Policy) {
            buffer.append("Policy");
            dumpAttributes(((Policy) assertion).getAttributes(), verbose, buffer, indent);
        } else if (assertion instanceof All) {
            buffer.append("All");
        } else if (assertion instanceof ExactlyOne) {
            buffer.append("ExactlyOne");
            /*
             * } else if (assertion instanceof ExtendedPrimitiveAssertion) {
             * buffer.append(((ExtendedPrimitiveAssertion) assertion).getPrefix())
             * .append(":").append( ((ExtendedPrimitiveAssertion) assertion).getName()
             * .getLocalPart());
             */
        } else if (assertion instanceof PrimitiveAssertion) {
            buffer.append(((PrimitiveAssertion) assertion).getName().getPrefix()).append(":");
            buffer.append(((PrimitiveAssertion) assertion).getName().getLocalPart());
            if (null != ((PrimitiveAssertion) assertion).getValue()) {
                buffer.append(" -> ").append(((PrimitiveAssertion) assertion).getValue().toString());
            }
            buffer.append("  ").append(((PrimitiveAssertion) assertion).getName().getNamespaceURI());
            dumpAttributes(((PrimitiveAssertion) assertion).getAttributes(), verbose, buffer, indent);
        } else if (assertion instanceof PolicyReference) {
            buffer.append("Ref -> ").append(((PolicyReference) assertion).getPolicyURIString());
        }
        dumpAssertionContent(assertion, verbose, buffer, indent);
        return buffer;
    }

    /**
     * Dump assertion content.
     * 
     * @param assertion
     *        the assertion
     * @param verbose
     *        the verbose
     * @param buffer
     *        the buffer
     * @param indent
     *        the indent
     */
    private static void dumpAssertionContent(final Assertion assertion, final boolean verbose, final StringBuffer buffer,
            final StringBuffer indent) {
        StringBuffer myIndent = new StringBuffer(indent.toString()).append(DEFAULT_INDENT);
        List terms = assertion.getTerms();
        for (Iterator iter = terms.iterator(); iter.hasNext();) {
            Assertion child = (Assertion) iter.next();
            dump(child, verbose, buffer, myIndent);
        }
    }

    /**
     * Dump attributes.
     * 
     * @param attributes
     *        the attributes
     * @param verbose
     *        the verbose
     * @param buffer
     *        the buffer
     * @param indent
     *        the indent
     */
    private static void dumpAttributes(final Hashtable attributes, final boolean verbose, final StringBuffer buffer,
            final StringBuffer indent) {
        for (Iterator iter = attributes.keySet().iterator(); iter.hasNext();) {
            QName key = (QName) iter.next();
            Object value = attributes.get(key);
            buffer.append("\n").append(indent).append(" @");
            if ((null != key.getPrefix()) && !("".equals(key.getPrefix()))) {
                buffer.append(key.getPrefix()).append(":");
            }
            buffer.append(key.getLocalPart()).append("->").append(value);
            buffer.append("  ").append(key.getNamespaceURI());
        }
    }

}
