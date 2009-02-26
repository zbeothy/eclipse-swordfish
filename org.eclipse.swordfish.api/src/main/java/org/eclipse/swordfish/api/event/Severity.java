/*******************************************************************************
 * Copyright (c) 2008, 2009 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swordfish.api.event;

/**
 * specified event severity level, used in org.eclipse.swordfish.api.event.Event.getSeverity() and 
 * org.eclipse.swordfish.api.event.EventHandler.getSeverity();
 * 
 * NONE - returns from org.eclipse.swordfish.api.Event.getSeverity() implementation by default, if no
 *        severity specified
 * ALL  - returns from org.eclipse.swordfish.api.event.EventHandler.getSeverity() implementation by default
 *        to specify that events with all severity level acceptable
 * @author akopachevsky
 */

public interface Severity {
    /**
     * returns from org.eclipse.swordfish.api.SFEvent.getSeverity() implementation by default, if 
     * no severity specified
     */
    int NONE = 0;
    
    /**
     * DEBUG severity bit.
     */
    int DEBUG = 1;

    /**
     * INFO severity bit.
     */
    int INFO = 2;

    /**
     * ERROR severity bit.
     */
    int ERROR = 4;

    /**
     * returns from org.eclipse.swordfish.api.event.SFEventHandler.getSeverity()
     * implementation by default to specify that events with all severity level acceptable
     * (the event severities acceptable are described as OR'ed event bits).
     */
    int ALL = DEBUG|INFO|ERROR;
}