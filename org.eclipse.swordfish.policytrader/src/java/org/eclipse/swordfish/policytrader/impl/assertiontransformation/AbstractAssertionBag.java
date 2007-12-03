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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * The Class AbstractAssertionBag.
 */
public abstract class AbstractAssertionBag extends Writeable {

    /** The assertions. */
    private Map assertions = new HashMap();

    /**
     * Instantiates a new abstract assertion bag.
     */
    protected AbstractAssertionBag() {
        super();
    }

    /**
     * Adds the assertion.
     * 
     * @param assertion
     *        the assertion
     */
    public void addAssertion(final ClassicAssertion assertion) {
        final QKey key = assertion.getAssertionName();
        List al = (List) this.assertions.get(key);
        if (null == al) {
            al = new LinkedList();
            this.assertions.put(key, al);
        }
        al.add(assertion);
    }

    /**
     * Gets the assertions.
     * 
     * @param assertionName
     *        the assertion name
     * 
     * @return the assertions
     */
    public List getAssertions(final QKey assertionName) {
        final List result = (List) this.assertions.get(assertionName);
        return null == result ? Collections.EMPTY_LIST : result;
    }

    /**
     * Gets the first assertion.
     * 
     * @param assertionName
     *        the assertion name
     * 
     * @return the first assertion
     */
    public ClassicAssertion getFirstAssertion(final QKey assertionName) {
        final List al = (List) this.assertions.get(assertionName);
        if (null == al) return null;
        return (ClassicAssertion) al.get(0);
    }

    /**
     * Checks for assertions.
     * 
     * @param name
     *        the name
     * 
     * @return true, if successful
     */
    public boolean hasAssertions(final QKey name) {
        return (this.assertions.get(name) != null);
    }

    /**
     * Removes the assertion.
     * 
     * @param assertion
     *        the assertion
     * 
     * @return true, if successful
     */
    public boolean removeAssertion(final ClassicAssertion assertion) {
        final QKey assertionName = assertion.getAssertionName();
        final List al = (List) this.assertions.get(assertionName);
        if (null == al) return false;
        if (al.remove(assertion)) {
            if (al.size() == 0) {
                this.assertions.remove(assertionName);
            }
            return true;
        }
        return false;
    }

    /**
     * Removes the assertions.
     * 
     * @param assertionName
     *        the assertion name
     * 
     * @return the list
     */
    public List removeAssertions(final QKey assertionName) {
        final List result = (List) this.assertions.remove(assertionName);
        return null == result ? Collections.EMPTY_LIST : result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policytrader.impl.assertiontransformation.Writeable#writeTo(javax.xml.stream.XMLStreamWriter)
     */
    @Override
    public abstract void writeTo(XMLStreamWriter writer) throws XMLStreamException;

    /**
     * Write content.
     * 
     * @param writer
     *        the writer
     * 
     * @throws XMLStreamException
     */
    protected void writeContent(final XMLStreamWriter writer) throws XMLStreamException {
        for (Iterator i = this.assertions.values().iterator(); i.hasNext();) {
            final List l = (List) i.next();
            for (Iterator ii = l.iterator(); ii.hasNext();) {
                final ClassicAssertion a = (ClassicAssertion) ii.next();
                a.writeTo(writer);
            }
        }
    }
}
