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
package org.eclipse.swordfish.core.papi.impl.untyped;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.Kernel;
import org.eclipse.swordfish.papi.internal.InternalSBB;
import org.eclipse.swordfish.papi.internal.untyped.consumer.InternalServiceProxy;
import org.eclipse.swordfish.papi.internal.untyped.provider.InternalServiceSkeleton;

/**
 * This interface extends the functionality exposed through InternalSBB with some more signatures
 * that can be used by InternalSBB components.
 */
public interface SBBExtension extends InternalSBB {

    /**
     * Gets the environment implementation.
     * 
     * @return the environment implementation
     */
    EnvironmentImpl getEnvironmentImplementation();

    /**
     * Gets the kernel.
     * 
     * @return -- the kernel this InternalSBB is running on
     */
    Kernel getKernel();

    /**
     * Gets the participant identity as string.
     * 
     * @return the participant identity as string
     */
    String getParticipantIdentityAsString();

    /**
     * this method removes a service proxy from the kernel. It is located here as the key for
     * remembering the generated proxy is also created in this class.
     * 
     * @param policyId
     *        the policy id for which this proxy was looked up
     * @param serviceProxy
     *        the proxy to remove
     * @param sbbInitiated
     *        indicates if this removal is SBBinitiated
     */
    void removeServiceProxy(InternalServiceProxy serviceProxy, String policyId, boolean sbbInitiated);

    /**
     * this method removes a service skeleton from the kernel. It is located here as the key for
     * remembering the generated skeleton is also created in this class.
     * 
     * @param providerId
     *        the providerId for which this skeleton was looked up
     * @param serviceSkeleton
     *        the proxy to remove
     * @param sbbInitiated
     *        indicates if this removal is SBBinitiated
     */
    void removeServiceSkeleton(InternalServiceSkeleton serviceSkeleton, QName providerId, boolean sbbInitiated);

}
