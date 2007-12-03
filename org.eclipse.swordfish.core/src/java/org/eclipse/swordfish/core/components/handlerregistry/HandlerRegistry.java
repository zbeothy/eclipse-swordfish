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
package org.eclipse.swordfish.core.components.handlerregistry;

import javax.xml.namespace.QName;
import org.eclipse.swordfish.core.components.iapi.Role;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.IncomingMessageHandlerProxy;

/**
 * The Interface HandlerRegistry.
 * 
 */
public interface HandlerRegistry {

    /** This classes role. */
    String ROLE = HandlerRegistry.class.getName();

    /**
     * Associate.
     * 
     * @param role
     *        the role
     * @param service
     *        the service
     * @param operationName
     *        the operation name
     * @param handler
     *        the handler
     */
    void associate(Role role, QName service, String operationName, IncomingMessageHandlerProxy handler);

    /**
     * Gets the handler.
     * 
     * @param role
     *        the role
     * @param service
     *        the service
     * @param operationName
     *        the operation name
     * 
     * @return the handler
     */
    IncomingMessageHandlerProxy getHandler(Role role, QName service, String operationName);

    /**
     * Has.
     * 
     * @param role
     *        the role
     * @param service
     *        the service
     * @param operationName
     *        the operation name
     * 
     * @return true, if successful
     */
    boolean has(Role role, QName service, String operationName);

    /**
     * Remove.
     * 
     * @param role
     *        the role
     * @param service
     *        the service
     * @param operationName
     *        the operation name
     * @param sbbInitiated
     *        the sbb initiated
     */
    void remove(Role role, QName service, String operationName, boolean sbbInitiated);

}
