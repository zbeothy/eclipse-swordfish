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
package org.eclipse.swordfish.core.components.internalproxy;

import java.util.Collection;
import javax.xml.namespace.QName;
import org.springframework.beans.factory.DisposableBean;
import org.w3c.dom.Document;

/**
 * The Interface ServiceOperationProxy.
 * 
 */
public interface ServiceOperationProxy extends DisposableBean {

    /**
     * Destroy this service operation proxy instance.
     * 
     * @throws Exception
     *         while cleaning up
     */
    void destroy() throws Exception;

    /**
     * Invoke service operation.
     * 
     * @param aServiceDescriptionCollection
     *        is a collection of service endpoints the controller will be provided with to selected
     *        the next invocation target
     * @param aServiceOperation
     *        is the service operation which shall be invoked next
     * @param aController
     *        is the controller which should tweak the operation of the proxy
     * @param aPayload
     *        is the payload which should be transferred
     * 
     * @return the result of the operation. The controller decides what type the result will be of
     * 
     * @throws Exception
     *         of any type which can be thrown during operation of the proxy
     */
    Object invokeServiceOperation(final Collection aServiceDescriptionCollection, final QName aServiceOperation,
            final ResilienceController aController, final Document aPayload) throws Exception;
}
