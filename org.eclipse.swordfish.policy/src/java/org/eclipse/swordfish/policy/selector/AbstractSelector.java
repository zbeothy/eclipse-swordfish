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
 * Allows the definition of arbitrary comparisions as function.
 * 
 */
public abstract class AbstractSelector implements Selector {

    /**
     * Instantiates a new abstract selector.
     * 
     * @param selector
     *        the selector
     */
    protected AbstractSelector(final Object selector) {
        if (null == selector) throw new NullPointerException("Null argument not allowed");
    }

}
