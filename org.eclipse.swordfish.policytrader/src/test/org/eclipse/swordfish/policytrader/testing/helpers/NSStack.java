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
package org.eclipse.swordfish.policytrader.testing.helpers;

import java.util.ArrayList;

class NSStack {

    private Mapping[] stack;

    private int top = 0;

    private int iterator = 0;

    private int currentDefaultNS = -1;

    private boolean optimizePrefixes = true;

    public NSStack() {
        this.stack = new Mapping[32];
        this.stack[0] = null;
    }

    public NSStack(final boolean optimizePrefixes) {
        this.optimizePrefixes = optimizePrefixes;
        this.stack = new Mapping[32];
        this.stack[0] = null;
    }

    /**
     * Add a mapping for a namespaceURI to the specified prefix to the top frame in the stack. If
     * the prefix is already mapped in that frame, remap it to the (possibly different)
     * namespaceURI.
     */
    public void add(final String namespaceURI, String prefix) {
        int idx = this.top;
        prefix = prefix.intern();
        try {
            // Replace duplicate prefixes (last wins - this could also
            // fault)
            for (int cursor = this.top; this.stack[cursor] != null; cursor--) {
                if (this.stack[cursor].getPrefix() == prefix) {
                    this.stack[cursor].setNamespaceURI(namespaceURI);
                    idx = cursor;
                    return;
                }
            }

            this.push();
            this.stack[this.top] = new Mapping(namespaceURI, prefix);
            idx = this.top;
        } finally {
            // If this is the default namespace, note the new in-scope
            // default is here.
            if (prefix.length() == 0) {
                this.currentDefaultNS = idx;
            }
        }
    }

    /**
     * Return a copy of the current frame. Returns null if none are present.
     */
    public ArrayList cloneFrame() {
        if (this.stack[this.top] == null) return null;

        ArrayList clone = new ArrayList();

        for (Mapping map = this.topOfFrame(); map != null; map = this.next()) {
            clone.add(map);
        }

        return clone;
    }

    /**
     * Given a prefix, return the associated namespace (if any).
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null) prefix = "";

        prefix = prefix.intern();

        for (int cursor = this.top; cursor > 0; cursor--) {
            Mapping map = this.stack[cursor];
            if (map == null) continue;

            if (map.getPrefix() == prefix) return map.getNamespaceURI();
        }

        return null;
    }

    /**
     * Return an active prefix for the given namespaceURI, including the default prefix ("").
     */
    public String getPrefix(final String namespaceURI) {
        return this.getPrefix(namespaceURI, false);
    }

    /**
     * Return an active prefix for the given namespaceURI. NOTE : This may return null even if the
     * namespaceURI was actually mapped further up the stack IF the prefix which was used has been
     * repeated further down the stack. I.e.:
     * 
     * <pre:outer xmlns:pre="namespace"> <pre:inner xmlns:pre="otherNamespace"> *here's where we're
     * looking* </pre:inner> </pre:outer>
     * 
     * If we look for a prefix for "namespace" at the indicated spot, we won't find one because
     * "pre" is actually mapped to "otherNamespace"
     */
    public String getPrefix(String namespaceURI, boolean noDefault) {
        if ((namespaceURI == null) || (namespaceURI.length() == 0)) return null;

        if (this.optimizePrefixes) {
            // If defaults are OK, and the given NS is the current default,
            // return "" as the prefix to favor defaults where possible.
            if (!noDefault && (this.currentDefaultNS > 0) && (this.stack[this.currentDefaultNS] != null)
                    && (namespaceURI == this.stack[this.currentDefaultNS].getNamespaceURI())) return "";
        }
        namespaceURI = namespaceURI.intern();

        for (int cursor = this.top; cursor > 0; cursor--) {
            Mapping map = this.stack[cursor];
            if (map == null) continue;

            if (map.getNamespaceURI() == namespaceURI) {
                String possiblePrefix = map.getPrefix();
                if (noDefault && (possiblePrefix.length() == 0)) continue;

                // now make sure that this is the first occurance of this
                // particular prefix
                for (int cursor2 = this.top; true; cursor2--) {
                    if (cursor2 == cursor) return possiblePrefix;
                    map = this.stack[cursor2];
                    if (map == null) continue;
                    if (possiblePrefix == map.getPrefix()) break;
                }
            }
        }

        return null;
    }

    /**
     * Return the next namespace mapping in the top frame.
     */
    public Mapping next() {
        if (this.iterator > this.top)
            return null;
        else
            return this.stack[this.iterator++];
    }

    /**
     * Remove the top frame from the stack.
     */
    public void pop() {
        this.clearFrame();

        this.top--;

        // If we've moved below the current default NS, figure out the new
        // default (if any)
        if (this.top < this.currentDefaultNS) {
            // Reset the currentDefaultNS to ignore the frame just removed.
            this.currentDefaultNS = this.top;
            while (this.currentDefaultNS > 0) {
                if ((this.stack[this.currentDefaultNS] != null) && (this.stack[this.currentDefaultNS].getPrefix().length() == 0))
                    break;
                this.currentDefaultNS--;
            }
        }

        if (this.top == 0) return;
    }

    /**
     * Create a new frame at the top of the stack.
     */
    public void push() {
        this.top++;

        if (this.top >= this.stack.length) {
            Mapping newstack[] = new Mapping[this.stack.length * 2];
            System.arraycopy(this.stack, 0, newstack, 0, this.stack.length);
            this.stack = newstack;
        }

        this.stack[this.top] = null;
    }

    /**
     * Reset the embedded iterator in this class to the top of the current (i.e., last) frame. Note
     * that this is not threadsafe, nor does it provide multiple iterators, so don't use this
     * recursively. Nor should you modify the stack while iterating over it.
     */
    public Mapping topOfFrame() {
        this.iterator = this.top;
        while (this.stack[this.iterator] != null)
            this.iterator--;
        this.iterator++;
        return this.next();
    }

    /**
     * Remove all mappings from the current frame.
     */
    private void clearFrame() {
        while (this.stack[this.top] != null)
            this.top--;
    }

    // --- inner class
    class Mapping {

        private String namespaceURI;

        private String prefix;

        public Mapping(final String namespaceURI, final String prefix) {
            this.setPrefix(prefix);
            this.setNamespaceURI(namespaceURI);
        }

        public String getNamespaceURI() {
            return this.namespaceURI;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public void setNamespaceURI(final String namespaceURI) {
            this.namespaceURI = namespaceURI.intern();
        }

        public void setPrefix(final String prefix) {
            this.prefix = prefix.intern();
        }
    }
}
