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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.apache.ws.policy.Assertion;
import org.eclipse.swordfish.policy.selector.Selector;

/**
 * Iterator implementation that iterates over an assertion's terms, returning only those entries
 * that match the specified criteria (type or qname).
 * 
 */
public class TermIterator implements Iterator {

    /** The selector. */
    private Selector selector;

    /** The it. */
    private Iterator it;

    /** The next element. */
    private Object theNextElement;

    /** The recurse. */
    private boolean recurse;

    /** The iterators. */
    private Stack iterators = new Stack();

    /**
     * Instantiates a new term iterator.
     * 
     * @param model
     *        the model
     * @param selector
     *        the selector
     */
    public TermIterator(final Assertion model, final Selector selector) {
        this.selector = selector;
        this.recurse = false;
        this.init(model);
    }

    /**
     * Instantiates a new term iterator.
     * 
     * @param model
     *        the model
     * @param selector
     *        the selector
     * @param recurse
     *        the recurse
     */
    public TermIterator(final Assertion model, final Selector selector, final boolean recurse) {
        this.selector = selector;
        this.recurse = recurse;
        this.init(model);
    }

    public Iterator getIt() {
        return this.it;
    }

    public Stack getIterators() {
        return this.iterators;
    }

    public Selector getSelector() {
        return this.selector;
    }

    public Object getTheNextElement() {
        return this.theNextElement;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return (null != this.theNextElement);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if (null == this.theNextElement) throw new NoSuchElementException();
        Object temp = this.theNextElement;
        this.theNextElement = this.findNextValidElement();
        return temp;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        throw new UnsupportedOperationException("Method remove not supported");
    }

    /**
     * Find next valid element.
     * 
     * @return the next valid element in the underlying model's terms
     */
    protected Object findNextValidElement() {
        if (this.recurse)
            return this.findRecursive();
        else
            return this.findShallow();
    }

    /**
     * Init.
     * 
     * @param model
     *        the model
     */
    protected void init(final Assertion model) {
        this.it = model.getTerms().iterator();
        this.theNextElement = this.findNextValidElement();
    }

    /**
     * Find next element.
     * 
     * @return the object
     */
    private Object findNextElement() {
        Assertion ret = null;
        while ((!this.it.hasNext()) && (!this.iterators.empty())) {
            this.it = (Iterator) this.iterators.pop();
        }
        if (this.it.hasNext()) {
            ret = (Assertion) this.it.next();
            List terms = ret.getTerms();
            if (null != terms) {
                Iterator newIt = terms.iterator();
                if (newIt.hasNext()) {
                    this.iterators.push(this.it);
                    this.it = newIt;
                }
            }
        }
        return ret;
    }

    /**
     * Find recursive.
     * 
     * @return the object
     */
    private Object findRecursive() {
        Object ret = null;
        Object val = this.findNextElement();
        while ((null != val)) {
            if (this.selector.isValid(val)) {
                ret = val;
                break;
            }
            val = this.findNextElement();
        }
        return ret;
    }

    /**
     * Find shallow.
     * 
     * @return the object
     */
    private Object findShallow() {
        Object ret = null;
        while (this.it.hasNext()) {
            Object test = this.it.next();
            if (this.selector.isValid(test)) {
                ret = test;
                break;
            }
        }
        return ret;
    }

}
