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
package org.eclipse.swordfish.core.management.instrumentation;

/**
 * Managed Resource with attribute and operation.
 * 
 */
public class Resource implements ResourceInterface {

    /** The foo. */
    private String foo = "foo";

    /** The bar. */
    private String bar = "bar";

    /** The count. */
    private int count = 0;

    /**
     * Gets the bar.
     * 
     * @return the bar
     */
    public String getBar() {
        return this.bar;
    }

    /**
     * Gets the count.
     * 
     * @return the count
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Gets the foo.
     * 
     * @return the foo
     */
    public String getFoo() {
        return this.foo;
    }

    /**
     * Sets the count.
     * 
     * @param val
     *        the new count
     */
    public void setCount(final int val) {
        this.count = val;
    }

    /**
     * Sets the foo.
     * 
     * @param attribute
     *        the new foo
     */
    public void setFoo(final String attribute) {
        this.foo = attribute;
    }

    /**
     * Trigger.
     */
    public void trigger() {
        this.count++;
    }

    /**
     * This method should not be available via JMX.
     */
    public void unexposed() {

    }

}
