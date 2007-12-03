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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.ws.policy.Assertion;
import org.eclipse.swordfish.policy.selector.Selector;

/**
 * Iterates over an assertion's terms, returning a Collection that contains only those entries that
 * match the specified criteria (type or qname).
 * 
 */

public class TermCollector {

    /** The selector. */
    private Selector selector;

    /** The model. */
    private Assertion model;

    /**
     * Instantiates a new term collector.
     * 
     * @param model
     *        the model
     * @param selector
     *        the selector
     */
    public TermCollector(final Assertion model, final Selector selector) {
        this.model = model;
        this.selector = selector;
    }

    /**
     * Collect.
     * 
     * @return the collection
     */
    public Collection collect() {
        List result = new ArrayList();
        Iterator it = new TermIterator(this.model, this.selector);
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    public Assertion getModel() {
        return this.model;
    }

    public Selector getSelector() {
        return this.selector;
    }

}
