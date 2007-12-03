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
package org.eclipse.swordfish.policy.selector;

import javax.xml.namespace.QName;
import org.apache.ws.policy.PrimitiveAssertion;

/**
 * The Class NameSelector.
 */
public class NameSelector extends AbstractSelector {

    /** The local name. */
    private String localName = null;

    /** The q name. */
    private QName qName = null;

    /**
     * Instantiates a new name selector.
     * 
     * @param qName
     *        the q name
     */
    public NameSelector(final QName qName) {
        super(qName);
        this.qName = qName;
    }

    /**
     * Instantiates a new name selector.
     * 
     * @param localName
     *        the local name
     */
    public NameSelector(final String localName) {
        super(localName);
        this.localName = localName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.selector.Selector#isValid(java.lang.Object)
     */
    public boolean isValid(final Object obj) {
        if (!(obj instanceof PrimitiveAssertion)) return false;
        if (null != this.localName)
            return ((PrimitiveAssertion) obj).getName().getLocalPart().equalsIgnoreCase(this.localName);
        else
            return ((PrimitiveAssertion) obj).getName().equals(this.qName);
    }

}
