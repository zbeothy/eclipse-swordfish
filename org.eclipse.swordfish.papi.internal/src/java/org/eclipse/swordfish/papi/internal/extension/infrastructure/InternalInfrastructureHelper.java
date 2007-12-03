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
package org.eclipse.swordfish.papi.internal.extension.infrastructure;

/**
 * Interface to perform Infrastructure related tasks. Instances of this interface enables to get
 * information of the current infrastructure instance being used. <p/> An instance implementing this
 * interface can be obtained via the environment of an existing SBB by
 * 
 * <pre>
 *    InternalOperations myOperations =  mySbb.getEnvironment().getComponent(
 *        org.eclipse.swordfish.papi.extension.infrastructure.InfrastructureHelper.class, null)
 * </pre>
 * 
 * This instance remains valid until the SBB is released. As best practice it is recommended to
 * obtain such an instance only once and store it in some variable.
 * 
 */
public interface InternalInfrastructureHelper {

    /**
     * Get an InternalInfrastructureInstance object providing the information of the infrastructure
     * used by the current SBB instance.
     * 
     * @return InternalInfrastructureInstance an object with information of the infrastrucuture used
     *         by the current SBB instance.
     * 
     */
    InternalInfrastructureInstance getCurrentInfrastructureInstance();
}
