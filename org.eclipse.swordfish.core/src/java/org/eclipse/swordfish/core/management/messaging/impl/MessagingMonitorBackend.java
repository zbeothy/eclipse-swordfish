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
package org.eclipse.swordfish.core.management.messaging.impl;

import org.eclipse.swordfish.core.management.operations.Operations;

/**
 * Interface for reporting backends that process messaging events to generate various kinds of
 * output.
 * 
 */
public interface MessagingMonitorBackend {

    /**
     * Message exchange was aborted due to application problems.
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleAbortedApplication(ExchangeJournal journal);

    /**
     * Message exchange was aborted due to network problems.
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleAbortedNet(ExchangeJournal journal);

    /**
     * Message exchange was completed successfully.
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleCompleted(ExchangeJournal journal);

    /**
     * Message was delivered to driving entity (application or net).
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleDelivered(ExchangeJournal journal);

    /**
     * Message exchange was completed successfully for this participant, but will continue
     * elsewhere.
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleDoneLocal(ExchangeJournal journal);

    /**
     * Message was received by this InternalSBB instance from a processing instance (application or
     * net).
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleHandback(ExchangeJournal journal);

    /**
     * Message was handed off from this InternalSBB instance to a processing instance (application
     * or net).
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleHandoff(ExchangeJournal journal);

    /**
     * Message exchange was received from driving entity (application or net).
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleReceived(ExchangeJournal journal);

    /**
     * Message exchange was started at this participant.
     * 
     * @param journal
     *        the journal
     */
    void handleStarted(ExchangeJournal journal);

    /**
     * An event for the message exchange occured that is not specified above.
     * 
     * @param journal
     *        for message exchange that generated the event
     */
    void handleUnspecifiedEvent(ExchangeJournal journal);

    /**
     * Sets the operations.
     * 
     * @param ops
     *        the new operations
     */
    void setOperations(Operations ops);

}
