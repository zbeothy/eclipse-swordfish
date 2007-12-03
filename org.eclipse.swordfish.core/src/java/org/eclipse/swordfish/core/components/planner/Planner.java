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
package org.eclipse.swordfish.core.components.planner;

import java.util.List;
import org.apache.ws.policy.Policy;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.components.iapi.Scope;

/**
 * Creates a processing plan with from a given configuration in a given AgreementScope and
 * AgreementRole. TODO implement me
 */
public interface Planner {

    /** role of this interface. */
    String ROLE = Planner.class.getName();

    /**
     * the implementation of the planner takes a list of assertions that has to be fulfilled.
     * 
     * @param operationPolicy
     *        the operation policy
     * @param role
     *        the role
     * @param scope
     *        the scope
     * 
     * @return a list of components in the order to be invoked
     */
    List plan(Policy operationPolicy, Role role, Scope scope);
}
