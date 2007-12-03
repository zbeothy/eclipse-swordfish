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
package org.eclipse.swordfish.core.management.adapter;

import java.util.logging.LogRecord;

/**
 * The Interface SnmpAdapter.
 */
public interface SnmpAdapter {

    /**
     * Close.
     */
    void close();

    /**
     * Flush.
     */
    void flush();

    /**
     * Publish.
     * 
     * @param record
     *        the record
     */
    void publish(LogRecord record);

    /**
     * Sets the em host.
     * 
     * @param value
     *        the new em host
     */
    void setEmHost(String value);

    /**
     * Sets the em port.
     * 
     * @param value
     *        the new em port
     */
    void setEmPort(String value);

    /**
     * Sets the mip location.
     * 
     * @param value
     *        the new mip location
     */
    void setMipLocation(String value);

    /**
     * Sets the oid root.
     * 
     * @param value
     *        the new oid root
     */
    void setOidRoot(String value);

    /**
     * Sets the sbb instance id.
     * 
     * @param value
     *        the new sbb instance id
     */
    void setSbbInstanceId(String value);

    /**
     * Sets the target host.
     * 
     * @param value
     *        the new target host
     */
    void setTargetHost(String value);

    /**
     * Sets the target port.
     * 
     * @param value
     *        the new target port
     */
    void setTargetPort(int value);

}
