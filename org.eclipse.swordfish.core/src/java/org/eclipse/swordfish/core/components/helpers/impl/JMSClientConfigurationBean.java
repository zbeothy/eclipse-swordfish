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
package org.eclipse.swordfish.core.components.helpers.impl;

/**
 * The Class JMSClientConfigurationBean.
 */
public class JMSClientConfigurationBean {

    /**
     * Sets the connection timeout.
     * 
     * @param connectionTimeout
     *        the new connection timeout
     */
    public void setConnectionTimeout(final int connectionTimeout) {
        System.setProperty("jms.connection.timeout", "" + (connectionTimeout * 1000));
    }

}
