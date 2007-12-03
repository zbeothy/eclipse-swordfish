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
package org.eclipse.swordfish.core.management.notification;

/**
 * Groups notifications that apply to MessageExchanges.
 * 
 */
public interface ExchangeNotification extends OperationNotification {

    /**
     * Gets the correlation ID.
     * 
     * @return ID of message exchange
     */
    String getCorrelationID();

    /**
     * Gets the participant role.
     * 
     * @return the role this participant has in the message exchange
     */
    ParticipantRole getParticipantRole();

}
