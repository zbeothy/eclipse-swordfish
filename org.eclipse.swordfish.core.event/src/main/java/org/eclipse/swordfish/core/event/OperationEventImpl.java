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
package org.eclipse.swordfish.core.event;


import org.eclipse.swordfish.api.event.EventConstants;
import org.eclipse.swordfish.api.event.OperationEvent;


public class OperationEventImpl extends EventImpl implements OperationEvent {

    public String getTopic() {
        return EventConstants.TOPIC_OPERATION_EVENT;
    }

}
