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
 * Interface of an object that is used by a
 * <code>org.eclipse.swordfish.policy.util.TermIterator</code> to determine whether an object
 * should be included in the result set.
 * 
 */
public interface Selector {

    /**
     * Checks if is valid.
     * 
     * @param obj
     *        the obj
     * 
     * @return <code>true</code> if <code>obj</code> should be included in the iterators return
     *         values <code>false</code> otherwise
     */
    boolean isValid(Object obj);

}
