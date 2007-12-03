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

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.resolver.CompoundServiceDescription;

/**
 * The Interface InternalProxy.
 * 
 */
public interface InternalProxy {

    /** the role of this interface to look it up. */
    String ROLE = InternalProxy.class.getName();

    /**
     * Invoke service.
     * 
     * @param aggServiceDesc
     *        aggregate service desc
     * @param operationName
     *        operation name
     * @param inMessage
     *        in message
     * 
     * @return String message
     * 
     * @throws Exception
     *         exception
     */
    String invokeService(CompoundServiceDescription aggServiceDesc, String operationName, String inMessage) throws Exception;

    /**
     * Invoke service.
     * 
     * @param aggServiceDesc
     *        the agg service desc
     * @param operationName
     *        the operation name
     * @param inMessage
     *        the in message
     * @param controller
     *        the controller
     * 
     * @return the object
     * 
     * @throws Exception
     */
    Object invokeService(CompoundServiceDescription aggServiceDesc, String operationName, String inMessage,
            ResilienceController controller) throws Exception;

    /**
     * Invoke service.
     * 
     * @param serviceName
     *        service name
     * @param operationName
     *        operation name
     * @param inMessage
     *        in message
     * 
     * @return String message
     * 
     * @throws Exception
     *         exception
     */
    String invokeService(QName serviceName, String operationName, String inMessage) throws Exception;
}
