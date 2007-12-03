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
package org.eclipse.swordfish.core.components.tracehandler;

/**
 * general behaviour of the tracehandler.
 */
public interface TraceHandler {

    /**
     * Adds the to logger.
     */
    void addToLogger();

    /**
     * Removes the from logger.
     */
    void removeFromLogger();

    /**
     * Sets the logger.
     * 
     * @param logger
     *        the new logger
     */
    void setLogger(Object logger);

}
