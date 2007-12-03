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
package org.eclipse.swordfish.core.management.operations.impl;

/**
 * MBean interface definition for OperationsBean used to publish notifications through MBean Server,
 * bypassing PAPI.
 * 
 */
public interface MBeanOperationsBeanMBean {

    /**
     * Publish.
     * 
     * @param msgType
     *        the msg type
     * @param params
     *        the params
     */
    void publish(String msgType, String[] params);

    /**
     * Register message type.
     * 
     * @param msgType
     *        the msg type
     * @param attributes
     *        the attributes
     */
    void registerMessageType(String msgType, String[] attributes);

}
