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
package org.eclipse.swordfish.core.components.resolver;

import java.util.List;
import javax.wsdl.Definition;
import org.eclipse.swordfish.policytrader.AgreedPolicy;

/**
 * A factory for creating CompoundServiceDescription objects.
 */
public interface CompoundServiceDescriptionFactory {

    /**
     * Creates a new CompoundServiceDescription object.
     * 
     * @param sdx
     *        the sdx
     * @param spdx
     *        the spdx
     * @param agreedPolicy
     *        the agreed policy
     * 
     * @return the compound service description
     */
    CompoundServiceDescription createCompoundServiceDescription(Definition sdx, Definition spdx, AgreedPolicy agreedPolicy);

    /**
     * Creates a new CompoundServiceDescription object.
     * 
     * @param sdx
     *        the sdx
     * @param spdx
     *        the spdx
     * @param agreedPolicy
     *        the agreed policy
     * @param providerPolicies
     *        the provider policies
     * 
     * @return the compound service description
     */
    CompoundServiceDescription createCompoundServiceDescription(Definition sdx, Definition spdx, AgreedPolicy agreedPolicy,
            List providerPolicies);

}
