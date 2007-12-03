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

/**
 * basically provides a callable version of instanceof.
 * 
 */
public class ClassSelector extends AbstractSelector {

    /** The selector. */
    private Class selector;

    /**
     * Instantiates a new class selector.
     * 
     * @param selector
     *        the selector
     */
    public ClassSelector(final Class selector) {
        super(selector);
        this.selector = selector;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.swordfish.policy.selector.Selector#isValid(java.lang.Object)
     */
    public boolean isValid(final Object obj) {
        return this.selector.isInstance(obj);
    }

}
