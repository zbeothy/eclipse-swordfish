/*******************************************************************************
 * Copyright (c) 2008 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Oliver Wolf - initial API and implementation
 *******************************************************************************/

package org.eclipse.swordfish.api;

import java.util.Map;

import javax.jbi.messaging.MessageExchange;

/**
 *  The base interceptor interface that provides the processing logic. Can be plugged into the nmr
 *
 */
public interface Interceptor {
    public static final String TYPE_PROPERTY = "type";

    /**
     * @param exchange the messageExchange to be processed
     * @throws RuntimeException if the processing error occured and some
     *  specific error handling activities should take place in the InterceptorExceptionListener
     */
    public void process(MessageExchange exchange) throws SwordfishException;


    /**
     * By using this method the implementation class can supply properties associated with the current interceptor
     *  e.g priority
     */
    public Map<String,?> getProperties();

}
