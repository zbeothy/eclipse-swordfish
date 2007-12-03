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

import javax.xml.namespace.QName;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.policy.selector.AbstractSelector;

/**
 * Allows the selection of a PrimitiveAssertion according to its QName.
 * 
 */
public class PrimitiveAssertionSelector extends AbstractSelector {

    /** The qname. */
    private QName qname;

    /**
     * Instantiates a new primitive assertion selector.
     * 
     * @param name
     *        the name
     */
    public PrimitiveAssertionSelector(final QName name) {
        super(name);
        this.qname = name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.selector.Selector#isValid(java.lang.Object)
     */
    public boolean isValid(final Object obj) {
        boolean ret = false;
        if (obj instanceof PrimitiveAssertion) {
            QName test = ((PrimitiveAssertion) obj).getName();
            ret =
                    ((this.qname.getNamespaceURI().equals(test.getNamespaceURI())) && (this.qname.getLocalPart().equals(test
                        .getLocalPart())));
        }
        return ret;
    }

}
